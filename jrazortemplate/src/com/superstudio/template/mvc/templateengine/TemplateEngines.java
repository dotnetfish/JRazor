package com.superstudio.template.mvc.templateengine;

import com.superstudio.template.mvc.TemplateEngineCollection;
import com.superstudio.template.mvc.actionresult.ITemplateEngine;

public  class TemplateEngines {

	public static final TemplateEngineCollection Engines = new TemplateEngineCollection();

	public static void registe(ITemplateEngine engine){
		Engines.add(engine);
	}
}
