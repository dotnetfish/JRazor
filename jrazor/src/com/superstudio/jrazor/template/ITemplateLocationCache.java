package com.superstudio.jrazor.template;



public interface ITemplateLocationCache
{
	String getViewLocation(TemplateHostContext context, String key);
	void insertViewLocation(TemplateHostContext context, String key, String virtualPath);
}