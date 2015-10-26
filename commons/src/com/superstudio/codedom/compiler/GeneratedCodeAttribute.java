package com.superstudio.codedom.compiler;

 
public final class GeneratedCodeAttribute //extends Attribute
{
	private String tool;

	private String version;

 

	 private String getTool()
	 {
		return this.tool;
	 }

 

	 private String getVersion()
	 {
		return this.version;
	 }

 
	public GeneratedCodeAttribute(String tool, String version)
	{
		this.tool = tool;
		this.version = version;
	}
}