package com.superstudio.template.templatepages;

import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.template.mvc.context.HostContext;

import java.util.Stack;


public class TemplateStack
{
    private static final Object _contextKey = new Object();

    public static ITemplateFile getCurrentTemplate(HostContext httpContext) throws ArgumentNullException
    {
        if (httpContext == null)
        {
            throw new ArgumentNullException("httpContext");
        }
       return  getStack(httpContext).firstElement();
        //return CollectionHelper.firstOrDefault(getStack(httpContext));
    }

    public static ITemplateFile pop(HostContext httpContext) throws ArgumentNullException
    {
        if (httpContext == null)
        {
            throw new ArgumentNullException("httpContext");
        }
        return getStack(httpContext).pop();
    }


    public static void push(HostContext httpContext, ITemplateFile templateFile) throws ArgumentNullException
    {
        if (templateFile == null)
        {
            throw new ArgumentNullException("templateFile");
        }
        if (httpContext == null)
        {
            throw new ArgumentNullException("httpContext");
        }
        getStack(httpContext).push(templateFile);
    }

    private static Stack<ITemplateFile> getStack(HostContext httpContext)
    {
    	Stack<ITemplateFile> stack = (Stack<ITemplateFile>)httpContext.getItems().get(_contextKey);
        if (stack == null)
        {
            stack = new Stack<ITemplateFile>();
            httpContext.getItems().put(_contextKey,stack);//= stack;
        }
        return stack;
    }
}