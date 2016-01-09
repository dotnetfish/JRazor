package com.superstudio.jrazor.template;

import java.io.Writer;
public interface ITemplate {
	void render(TemplateContext viewContext, Writer writer) throws InstantiationException, IllegalAccessException;
}
