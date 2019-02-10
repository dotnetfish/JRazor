package com.superstudio.template.mvc.context;

import com.superstudio.template.mvc.TemplateContext;
import com.superstudio.template.mvc.actionresult.TemplateDataDictionary;
import com.superstudio.template.templatepages.DisplayModeProvider;
import com.superstudio.template.templatepages.IDisplayMode;

import java.io.Writer;
import java.util.HashMap;

public class RenderContext {

	//private ControllerBase controller;
	private HostContext httpContext;
	private Boolean isChildAction;
	private TemplateContext parentActionTemplateContext;
	private RequestContext requestContext;
	private TemplateInfo templateInfo;

	private Writer writer;
	public RenderContext() {
	}

	protected RenderContext(RenderContext renderContext) {
		this.requestContext=renderContext.getRequestContext();
		this.httpContext=new HostContext(this.requestContext.getContext());
		//this.controller=renderContext.getController();
		this.setTemplateInfo(renderContext.getTemplateInfo());
	}

	public RenderContext(RequestContext requestContext) {
		
		this.requestContext=requestContext;
		this.httpContext=new HostContext(requestContext.getContext());
		//this.controller=controller;
		this.setTemplateInfo(requestContext.getTemplateInfo());
	}

	public RenderContext(HostContext httpContext, TemplateInfo templateInfo) {
		this.httpContext=httpContext;
		this.requestContext=new RequestContext(templateInfo,httpContext);
		this.setTemplateInfo(templateInfo);
		//this.controller=controller;
	}


	public Boolean isChildAction() {
		return isChildAction;
	}

	public void setChildAction(Boolean isChildAction) {
		this.isChildAction = isChildAction;
	}

	public TemplateContext getParentActionTemplateContext() {
		return parentActionTemplateContext;
	}

	public void setParentActionTemplateContext(TemplateContext parentActionTemplateContext) {
		this.parentActionTemplateContext = parentActionTemplateContext;
	}

	public RequestContext getRequestContext() {
		return requestContext;
	}

	public void setRequestContext(RequestContext requestContext) {
		this.requestContext = requestContext;
	}

	/*public RouteData getRouteData() {
		return routeData;
	}

	public void setRouteData(RouteData routeData) {
		this.routeData = routeData;
	}*/

	public HostContext getHttpContext() {
		return httpContext;
	}

	public void setHttpContext(HostContext httpContext) {
		this.httpContext = httpContext;
	}
	
	 public IDisplayMode getDisplayMode() {
		return DisplayModeProvider.getDisplayMode(getHttpContext());
	}

	public void setDisplayMode(IDisplayMode displayMode) {
		DisplayModeProvider.SetDisplayMode(getHttpContext(), displayMode);
	}



	
    /* {
         get { return DisplayModeProvider.getDisplayMode(HttpContext); }
         set { DisplayModeProvider.SetDisplayMode(HttpContext, value); }
     }*/
	private TemplateDataDictionary templateData=new TemplateDataDictionary(new HashMap<>());
	public TemplateDataDictionary getTemplateData() {

		return templateData;
	}
	public void setTemplateData(TemplateDataDictionary templateData) {

		this.templateData=templateData;
	}

	public Writer getWriter() {
		return writer;
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}

	public TemplateInfo getTemplateInfo() {
		return templateInfo;
	}

	public void setTemplateInfo(TemplateInfo templateInfo) {
		this.templateInfo = templateInfo;
	}
}
