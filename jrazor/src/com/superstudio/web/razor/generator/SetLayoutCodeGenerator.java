package com.superstudio.web.razor.generator;

import com.superstudio.codedom.*;


import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.web.razor.parser.syntaxTree.*;
import org.apache.commons.lang3.StringUtils;

public class SetLayoutCodeGenerator extends SpanCodeGenerator
{
	public SetLayoutCodeGenerator(String layoutPath)
	{
		setLayoutPath(layoutPath);
	}

	private String privateLayoutPath;
	public final String getLayoutPath()
	{
		return privateLayoutPath;
	}
	public final void setLayoutPath(String value)
	{
		privateLayoutPath = value;
	}

	@Override
	public void generateCode(Span target, CodeGeneratorContext context)
	{
		if (!context.getHost().getDesignTimeMode() && !StringUtils.isBlank(context.getHost().getGeneratedClassContext().getLayoutPropertyName()))
		{
			context.getTargetMethod().getStatements().add(
					new CodeAssignStatement(new CodePropertyReferenceExpression(null, context.getHost().getGeneratedClassContext().getLayoutPropertyName()), new CodePrimitiveExpression(getLayoutPath())));
		}
	}

	@Override
	public String toString()
	{
		return "Layout: " + getLayoutPath();
	}

	@Override
	public boolean equals(Object obj)
	{
		SetLayoutCodeGenerator other = (SetLayoutCodeGenerator)((obj instanceof SetLayoutCodeGenerator) ? obj : null);
		return other != null && StringUtils.equals(other.getLayoutPath(), getLayoutPath());
	}

	@Override
	public int hashCode()
	{
		return getLayoutPath().hashCode();
	}
}