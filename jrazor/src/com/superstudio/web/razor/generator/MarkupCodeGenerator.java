package com.superstudio.web.razor.generator;

import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.web.razor.parser.syntaxTree.*;

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
			context.AddContextCall(target, context.getHost().getGeneratedClassContext().getBeginContextMethodName(), true);
		}

		if (!StringHelper.isNullOrEmpty(target.getContent()) && !context.getHost().getDesignTimeMode())
		{

			String code = context.BuildCodeString(cw ->
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
				try {
					cw.writeStringLiteral(target.getContent());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				cw.writeEndMethodInvoke();
				cw.writeEndStatement();
			}
		   );
			context.AddStatement(code);
		}

		if (context.getHost().getEnableInstrumentation())
		{
			context.AddContextCall(target, context.getHost().getGeneratedClassContext().getendContextMethodName(), true);
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