package com.superstudio.jrazor.template;

import java.util.Stack;

import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.web.HttpContextBase;

public class TemplateStack
{
    private static final Object _contextKey = new Object();

    public static ITemplateFile GetCurrentTemplate(HttpContextBase httpContext) throws ArgumentNullException
    {
        if (httpContext == null)
        {
            throw new ArgumentNullException("httpContext");
        }
       return  GetStack(httpContext).firstElement();
        //return CollectionHelper.firstOrDefault(GetStack(httpContext));
    }

    public static ITemplateFile Pop(HttpContextBase httpContext) throws ArgumentNullException
    {
        if (httpContext == null)
        {
            throw new ArgumentNullException("httpContext");
        }
        return GetStack(httpContext).pop();
    }

    public static void Push(HttpContextBase httpContext, ITemplateFile templateFile) throws ArgumentNullException
    {
        if (templateFile == null)
        {
            throw new ArgumentNullException("templateFile");
        }
        if (httpContext == null)
        {
            throw new ArgumentNullException("httpContext");
        }
        GetStack(httpContext).push(templateFile);
    }

    private static Stack<ITemplateFile> GetStack(HttpContextBase httpContext)
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