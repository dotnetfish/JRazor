package com.superstudio.web.razor.generator;

import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.web.razor.parser.syntaxTree.*;
import org.apache.commons.lang3.StringUtils;

public class MarkupCodeGenerator extends SpanCodeGenerator
{
	@Override
	public void generateCode(Span target, CodeGeneratorContext context)
	{
		if (!context.getHost().getDesignTimeMode() && StringUtils.isBlank(target.getContent()))
		{
			return;
		}

		if (context.getHost().getEnableInstrumentation())
		{
			context.addContextCall(target, context.getHost().getGeneratedClassContext().getBeginContextMethodName(), true);
		}

		if (!StringUtils.isBlank(target.getContent()) && !context.getHost().getDesignTimeMode())
		{

			String code = context.buildCodeString(cw ->
			{
				if (!StringUtils.isBlank(context.getTargetWriterName()))
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
			context.addStatement(code);
		}

		if (context.getHost().getEnableInstrumentation())
		{
			context.addContextCall(target, context.getHost().getGeneratedClassContext().getendContextMethodName(), true);
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