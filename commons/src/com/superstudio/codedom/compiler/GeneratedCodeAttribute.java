package com.superstudio.codedom.compiler;

import com.superstudio.codedom.*;


//ORIGINAL LINE: [__DynamicallyInvokable, AttributeUsage(AttributeTargets.All, Inherited = false, AllowMultiple = false)] public sealed class GeneratedCodeAttribute : Attribute
public final class GeneratedCodeAttribute //extends Attribute
{
	private String tool;

	private String version;


//ORIGINAL LINE: [__DynamicallyInvokable] public string Tool
	 private String getTool()
	 {
		return this.tool;
	 }


//ORIGINAL LINE: [__DynamicallyInvokable] public string Version
	 private String getVersion()
	 {
		return this.version;
	 }


//ORIGINAL LINE: [__DynamicallyInvokable] public GeneratedCodeAttribute(string tool, string version)
	public GeneratedCodeAttribute(String tool, String version)
	{
		this.tool = tool;
		this.version = version;
	}
}