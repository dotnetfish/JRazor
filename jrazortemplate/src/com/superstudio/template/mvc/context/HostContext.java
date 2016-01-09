package com.superstudio.template.mvc.context;

import com.superstudio.template.mvc.TemplateEngineCollection;
import com.superstudio.template.mvc.templateengine.TemplateEngines;
import com.superstudio.web.HttpApplicationStateBase;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Chaoqun on 2015/11/11.
 */
public  class HostContext {
    private static ThreadLocal<HostContext> threadLocalRequestContext;

    public HostContext() {
        this.setItems(new HashMap<>());
        threadLocalRequestContext.set(this);
    }

    public static HostContext getCurrent() throws Exception {

        if (threadLocalRequestContext == null) {
            throw new Exception("HttpContext尚未初始化。");
        }
        return threadLocalRequestContext.get();
    }

    public HostContext(HostContext context) {
        this.setItems(context.getItems());
    }

    public static HostContext initRequestContext() {

        if (threadLocalRequestContext == null) {
            threadLocalRequestContext = new ThreadLocal<HostContext>();
        }
        HostContext context = new HostContext();
        threadLocalRequestContext.set(context);
        return context;
    }


    public Map<Object, Object> getItems() {
        return items;
    }

    public void setItems(Map<Object, Object> items) {
        this.items = items;
    }

    private Map<Object, Object> items;


    public HttpApplicationStateBase getApplication() {

        return null;
    }

    public boolean isDebuggingEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    public TemplateEngineCollection getTemplateEngineCollection() {

        return TemplateEngines.Engines;
    }

    public    String mapPath(String vp){return  vp;}
}
