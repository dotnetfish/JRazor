package com.superstudio.jrazor.generator;


import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.jrazor.parser.syntaxTree.Span;

public class ResolveUrlCodeGenerator extends SpanCodeGenerator
{
	@Override
	public void generateCode(Span target, CodeGeneratorContext context)
	{
		// Check if the host supports it
		if (StringHelper.isNullOrEmpty(context.getHost().getGeneratedClassContext().getResolveUrlMethodName()))
		{
			// Nope, just use the default MarkupCodeGenerator behavior
			new MarkupCodeGenerator().generateCode(target, context);
			return;
		}

		if (!context.getHost().getDesignTimeMode() && StringHelper.isNullOrEmpty(target.getContent()))
		{
			return;
		}

		if (context.getHost().getEnableInstrumentation() && context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput)
		{
			// Add a non-literal context call (non-literal because the expanded URL will not match the source character-by-character)
			context.addContextCall(target, context.getHost().getGeneratedClassContext().getBeginContextMethodName(), false);
		}

		if (!StringHelper.isNullOrEmpty(target.getContent()) && !context.getHost().getDesignTimeMode())
		{
			String code = context.buildCodeString(cw ->
			{
				if (context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput)
				{
					if (!StringHelper.isNullOrEmpty(context.getTargetWriterName()))
					{
						cw.WriteStartMethodInvoke(context.getHost().getGeneratedClassContext().getWriteLiteralToMethodName());
						cw.writeSnippet(context.getTargetWriterName());
						cw.writeParameterSeparator();
					}
					else
					{
						cw.WriteStartMethodInvoke(context.getHost().getGeneratedClassContext().getWriteLiteralMethodName());
					}
				}
				cw.WriteStartMethodInvoke(context.getHost().getGeneratedClassContext().getResolveUrlMethodName());
				try {
					cw.writeStringLiteral(target.getContent());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cw.WriteEndMethodInvoke();
				if (context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput)
				{
					cw.WriteEndMethodInvoke();
					cw.writeEndStatement();
				}
				else
				{
					cw.writeLineContinuation();
				}
			}
		   );
			if (context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput)
			{
				context.addStatement(code);
			}
			else
			{
				context.bufferStatementFragment(code);
			}
		}

		if (context.getHost().getEnableInstrumentation() && context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput)
		{
			context.addContextCall(target, context.getHost().getGeneratedClassContext().getEndContextMethodName(), false);
		}
	}

	@Override
	public String toString()
	{
		return "VirtualPath";
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof ResolveUrlCodeGenerator;
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
}