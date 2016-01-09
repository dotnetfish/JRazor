package com.superstudio.template.mvc;

import com.superstudio.template.mvc.context.HostContext;
import com.superstudio.web.HttpContextBase;


public interface ITemplateLocationCache
{
	String getTemplateLocation(HostContext httpContext, String key);
	void insertTemplateLocation(HostContext httpContext, String key, String virtualPath);
}