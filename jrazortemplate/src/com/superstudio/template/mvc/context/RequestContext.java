package com.superstudio.template.mvc.context;

import com.superstudio.template.mvc.RouteData;
import com.superstudio.web.HttpContext;
import com.superstudio.web.HttpRequestBase;
import com.superstudio.web.HttpResponseBase;
import com.superstudio.template.mvc.Route;

public class RequestContext {
	
	/*private static String requestContextKey="_RequestContextKey_";
	private static ThreadLocal<RequestContext> threadLocalRequestContext;
	*/
	/*public static RequestContext getCurrent() throws Exception{
		
		if(threadLocalRequestContext==null){
			throw new Exception("RequestContext尚未初始化。");
		}
		return (RequestContext)threadLocalRequestContext.get();
	}
	*/
	/*public static  RequestContext initRequestContext(Route route,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		if(threadLocalRequestContext==null){
			threadLocalRequestContext=new ThreadLocal<RequestContext>();
		}
		RequestContext context=new RequestContext(route,request,response);
		threadLocalRequestContext.set(context);
		return context;
	}
	*/
	private HttpRequestBase request;
	private HttpResponseBase response;
	private TemplateInfo templateInfo;
	

	
	private HostContext context;
	public RequestContext(TemplateInfo templateInfo,HostContext context){
		this.context=context;
		this.templateInfo=templateInfo;
		//this.request=context.getRequest();
	//	this.response=context.getResponse();
		//this.routeData=new RouteData(route,context.getRequest());
	}
	
	public RequestContext(TemplateInfo templateInfo,HttpRequestBase request,HttpResponseBase response) throws Exception{
		this.request=request;
		this.response=response;
		this.templateInfo=templateInfo;
	//	this.routeData=new RouteData(templateInfo,request);
	}

	public HttpRequestBase getRequest() {
		return request;
	}

	

	public HttpResponseBase getResponse() {
		return response;
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
