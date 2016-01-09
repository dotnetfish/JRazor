package com.superstudio.jrazor.template;

import java.util.Map;

public class TemplateDataDictionary {

	private Object model;
	private Map<String, Object> templateData;

	public TemplateDataDictionary(Map<String, Object> object) {
		// TODO Auto-generated constructor stub
		templateData=object;
	}

	public Map<String, Object> getViewData() {
		return templateData;
	}

	public void setViewData(Map<String, Object> viewData) {
		this.templateData = viewData;
	}

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}

}
