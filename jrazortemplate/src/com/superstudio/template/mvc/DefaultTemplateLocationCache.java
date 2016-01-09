package com.superstudio.template.mvc;

import com.superstudio.template.mvc.context.HostContext;

public class DefaultTemplateLocationCache implements ITemplateLocationCache {

	//private static ConcurrentHashMap<Context,> templateLocationCache
	private static final String  prefix="__templatelocationCachePrefix__";
	public static final ITemplateLocationCache Null = null;

	@Override
	public String getTemplateLocation(HostContext httpContext, String key) {

		Object object= httpContext.getItems().get(prefix+key);
		if(object==null)return null;
		return  object.toString();
	}

	@Override
	public void insertTemplateLocation(HostContext httpContext, String key, String virtualPath) {
		httpContext.getItems().put(prefix+key,virtualPath);
		
	}

}
