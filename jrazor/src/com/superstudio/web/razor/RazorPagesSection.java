package com.superstudio.web.razor;

import com.superstudio.commons.ConfigurationSection;

import java.util.List;

public class RazorPagesSection extends ConfigurationSection
{
	public static final String SectionName = RazorWebSectionGroup.GROUP_NAME + "/pages";
	//private static final ConfigurationProperty _pageBaseTypeProperty = new ConfigurationProperty("pageBaseType", String.class, null, ConfigurationPropertyOptions.IsRequired);
	//private static final ConfigurationProperty _namespacesProperty = new ConfigurationProperty("namespaces", NamespaceCollection.class, null, ConfigurationPropertyOptions.IsRequired);
	private boolean _pageBaseTypeSet;
	private boolean _namespacesSet;
	private String _pageBaseType;
	private List<String> _namespaces;

	public final String getPageBaseType()
	{
		if (!this._pageBaseTypeSet)
		{
			//return (String)super[RazorPagesSection._pageBaseTypeProperty];
		}
		return this._pageBaseType;
	}
	public final void setPageBaseType(String value)
	{
		this._pageBaseType = value;
		this._pageBaseTypeSet = true;
	}

	public final List<String> getNamespaces()
	{
		if (!this._namespacesSet)
		{
			//return (List<String>)super.get(RazorPagesSection._namespacesProperty);
		}
		return this._namespaces;
	}
	public final void setNamespaces(List<String> value)
	{
		this._namespaces = value;
		this._namespacesSet = true;
	}
}