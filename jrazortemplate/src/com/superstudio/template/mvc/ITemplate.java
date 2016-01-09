package com.superstudio.template.mvc;

import java.io.Writer;

public interface ITemplate {
	void render(TemplateContext templateContext, Writer writer) throws InstantiationException, IllegalAccessException;
}
