package com.superstudio.template.mvc.actionresult;

import java.io.IOException;

import com.superstudio.template.mvc.ITemplate;
import com.superstudio.template.mvc.context.RenderContext;

public interface ITemplateEngine {
	 TemplateEngineResult findPartialTemplate(RenderContext renderContext, String partialTemplateName, boolean useCache) throws Exception;
     TemplateEngineResult findTemplate(RenderContext renderContext, String templateName, String masterName, boolean useCache) throws Exception;
     void releaseTemplate(RenderContext renderContext, ITemplate template) throws IOException;
}
