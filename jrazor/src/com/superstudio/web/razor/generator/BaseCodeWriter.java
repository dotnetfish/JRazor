package com.superstudio.web.razor.generator;

import com.sun.deploy.util.StringUtils;
import com.superstudio.commons.csharpbridge.action.ActionOne;

public abstract class BaseCodeWriter extends CodeWriter
{
	@Override
	public void writeSnippet(String snippet)
	{
		getInnerWriter().write(snippet);
	}

	@Override
	protected void emitStartMethodInvoke(String methodName)
	{
		emitStartMethodInvoke(methodName, new String[0]);
	}

	@Override
	protected void emitStartMethodInvoke(String methodName, String... genericArguments)
	{


		if (genericArguments != null && genericArguments.length > 0)
		{

			int lastIndex=methodName.lastIndexOf('.');
			if(lastIndex>0){
				String genericMethodName=methodName.substring(lastIndex+1);
				getInnerWriter().write(methodName.substring(0,lastIndex+1));
				writeStartGenerics();
				for (int i = 0; i < genericArguments.length; i++)
				{
					if (i > 0)
					{
						writeParameterSeparator();
					}
					writeSnippet(genericArguments[i]);
				}
				writeEndGenerics();
				getInnerWriter().write(genericMethodName);
			}

		}else{
			getInnerWriter().write(methodName);
		}

		getInnerWriter().write("(");
	}

	@Override
	protected void emitEndMethodInvoke()
	{
		getInnerWriter().write(")");
	}

	@Override
	protected void emitEndConstructor()
	{
		getInnerWriter().write(")");
	}

	@Override
	protected void emitEndLambdaExpression()
	{
	}

	@Override
	public void writeParameterSeparator()
	{
		getInnerWriter().write(", ");
	}

	protected final <T> void writeCommaSeparatedList(T[] items, ActionOne<T> writeItemAction)
	{
		for (int i = 0; i < items.length; i++)
		{
			if (i > 0)
			{
				getInnerWriter().write(", ");
			}
			writeItemAction.execute(items[i]);
		}
	}

	protected void writeStartGenerics()
	{
	}

	protected void writeEndGenerics()
	{
	}
}