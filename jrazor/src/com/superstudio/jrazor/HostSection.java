package com.superstudio.jrazor;

public class HostSection //extends ConfigurationSection
{
	public static final String SectionName = RazorWebSectionGroup.GroupName + "/host";
	//private static final ConfigurationProperty _typeProperty = new ConfigurationProperty("factoryType", String.class, null, ConfigurationPropertyOptions.IsRequired);
	private boolean _factoryTypeSet;
	private String _factoryType;
 
	public final String getFactoryType()
	{
		if (!this._factoryTypeSet)
		{
			//return (String)super[HostSection._typeProperty];
		}
		return this._factoryType;
	}
	public final void setFactoryType(String value)
	{
		this._factoryType = value;
		this._factoryTypeSet = true;
	}
}