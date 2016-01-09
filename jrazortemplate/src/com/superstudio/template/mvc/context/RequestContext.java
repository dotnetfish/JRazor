package com.superstudio.template.mvc.context;

public class RequestContext {

	private TemplateInfo templateInfo;
	

	
	private HostContext context;
	public RequestContext(TemplateInfo templateInfo,HostContext context){
		this.context=context;
		this.templateInfo=templateInfo;

	}
	





	



	



	


	public HostContext getContext() {
		return context;
	}

	public void setContext(HostContext context) {
		this.context = context;
	}

	public TemplateInfo getTemplateInfo() {
		return templateInfo;
	}

	public void setTemplateInfo(TemplateInfo templateInfo) {
		this.templateInfo = templateInfo;
	}
}
