package com.superstudio.jrazor.template;

import java.io.IOException;

import com.superstudio.commons.exception.ArgumentNullException;


public interface ITemplateEngine {
	 TemplateEngineResult findPartialTemplate(TemplateContext controllerContext, String partialViewName, boolean useCache) throws Exception;
	 TemplateEngineResult findTemplate(TemplateContext controllerContext, String viewName, String masterName, boolean useCache) throws Exception;
     void releaseTemplate(TemplateContext controllerContext, ITemplate view) throws IOException;
}
