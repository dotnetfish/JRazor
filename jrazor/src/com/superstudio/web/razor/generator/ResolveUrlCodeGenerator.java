package com.superstudio.web.razor.generator;

import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.web.razor.parser.syntaxTree.*;

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
			// add a non-literal context call (non-literal because the expanded URL will not match the source character-by-character)
			context.AddContextCall(target, context.getHost().getGeneratedClassContext().getBeginContextMethodName(), false);
		}

		if (!StringHelper.isNullOrEmpty(target.getContent()) && !context.getHost().getDesignTimeMode())
		{
			String code = context.BuildCodeString(cw ->
			{
				if (context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput)
				{
					if (!StringHelper.isNullOrEmpty(context.getTargetWriterName()))
					{
						cw.writeStartMethodInvoke(context.getHost().getGeneratedClassContext().getWriteLiteralToMethodName());
						cw.writeSnippet(context.getTargetWriterName());
						cw.writeParameterSeparator();
					}
					else
					{
						cw.writeStartMethodInvoke(context.getHost().getGeneratedClassContext().getWriteLiteralMethodName());
					}
				}
				cw.writeStartMethodInvoke(context.getHost().getGeneratedClassContext().getResolveUrlMethodName());
				try {
					cw.writeStringLiteral(target.getContent());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cw.writeEndMethodInvoke();
				if (context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput)
				{
					cw.writeEndMethodInvoke();
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
				context.AddStatement(code);
			}
			else
			{
				context.BufferStatementFragment(code);
			}
		}

		if (context.getHost().getEnableInstrumentation() && context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput)
		{
			context.AddContextCall(target, context.getHost().getGeneratedClassContext().getendContextMethodName(), false);
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