package com.superstudio.template.mvc;

import com.superstudio.template.mvc.context.HostContext;



public interface ITemplateLocationCache
{
	String getTemplateLocation(HostContext httpContext, String key);
	void insertTemplateLocation(HostContext httpContext, String key, String virtualPath);
}