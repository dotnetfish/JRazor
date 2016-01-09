package com.superstudio.jrazor.template;

import com.superstudio.jrazor.IDisplayMode;

import java.util.Map;

public class TemplateHostContext {

	
	public TemplateHostContext(TemplateHostContext httpContext) {
		// TODO Auto-generated constructor stub
	}

	public Map<Object,Object> getItems() {
		// TODO Auto-generated method stub
		return null;
	}

	public IDisplayMode getDisplayMode() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public TemplateContext getContext() {
		return context;
	}

	public void setContext(TemplateContext context) {
		this.context = context;
	}

	private TemplateContext context;

	public String mapPath(String vp) {
		// TODO Auto-generated method stub
		return null;
	}

}
