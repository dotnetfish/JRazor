package com.superstudio.template.mvc;

import com.superstudio.template.mvc.context.RenderContext;


public interface ITemplatePageActivator
{
	Object create(RenderContext renderContext, java.lang.Class type) throws InstantiationException, IllegalAccessException;
}