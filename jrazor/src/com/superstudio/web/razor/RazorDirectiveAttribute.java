package com.superstudio.web.razor;

import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.web.CommonResources;



/** 
 Specifies a Razor directive that is rendered as an attribute on the generated class. 
 
*/

//[AttributeUsage(AttributeTargets.Class, AllowMultiple = true, Inherited = true)]
public final class RazorDirectiveAttribute //extends Attribute
{
	private final Object _typeId = new Object();

	public RazorDirectiveAttribute(String name, String value)
	{
		if (StringHelper.isNullOrEmpty(name))
		{
			throw new IllegalArgumentException(CommonResources.getArgument_Cannot_Be_Null_Or_Empty()+ "name");
		}

		setName(name);
		setValue(value);
	}

	/*@Override
	public Object getTypeId()
	{
		return _typeId;
	}*/

	private String privateName;
	public String getName()
	{
		return privateName;
	}
	private void setName(String value)
	{
		privateName = value;
	}

	private String privateValue;
	public String getValue()
	{
		return privateValue;
	}
	private void setValue(String value)
	{
		privateValue = value;
	}

	/*@Override
	public boolean equals(Object obj)
	{
		RazorDirectiveAttribute attribute = (RazorDirectiveAttribute)((obj instanceof RazorDirectiveAttribute) ? obj : null);
		return attribute != null && getName().equals(attribute.getName(), StringComparison.OrdinalIgnoreCase) && StringComparer.OrdinalIgnoreCase.equals(getValue(), attribute.getValue());
	}

	@Override
	public int hashCode()
	{
		return (StringComparer.OrdinalIgnoreCase.hashCode(getName()) * 31) + (getValue() == null ? 0 : StringComparer.OrdinalIgnoreCase.hashCode(getValue()));
	}*/
}