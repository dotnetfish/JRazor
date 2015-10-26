package com.superstudio.jrazor.generator;


import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.jrazor.parser.syntaxTree.Span;

public class MarkupCodeGenerator extends SpanCodeGenerator
{
	@Override
	public void generateCode(Span target, CodeGeneratorContext context)
	{
		if (!context.getHost().getDesignTimeMode() && StringHelper.isNullOrEmpty(target.getContent()))
		{
			return;
		}

		if (context.getHost().getEnableInstrumentation())
		{
			context.addContextCall(target, context.getHost().getGeneratedClassContext().getBeginContextMethodName(), true);
		}

		if (!StringHelper.isNullOrEmpty(target.getContent()) && !context.getHost().getDesignTimeMode())
		{
 
			String code = context.buildCodeString(cw ->
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
				try {
					cw.writeStringLiteral(target.getContent());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cw.WriteEndMethodInvoke();
				cw.writeEndStatement();
			}
		   );
			context.addStatement(code);
		}

		if (context.getHost().getEnableInstrumentation())
		{
			context.addContextCall(target, context.getHost().getGeneratedClassContext().getEndContextMethodName(), true);
		}
	}

	@Override
	public String toString()
	{
		return "Markup";
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof MarkupCodeGenerator;
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
}