package com.superstudio.jrazor.template;

public interface ITemplatePageActivator {
		<T> T Create(TemplateHostContext controllerContext, java.lang.Class<T> type) throws InstantiationException, IllegalAccessException;
	
}
