package com.superstudio.template.mvc.actionresult;

import java.util.Map;

public class TemplateDataDictionary {

	private Object model;
	private Map<String, Object> templateData;

	public TemplateDataDictionary(Map<String, Object> object) {
		// TODO Auto-generated constructor stub
		templateData=object;
	}

	public Map<String, Object> getTemplateData() {
		return templateData;
	}

	public void setTemplateData(Map<String, Object> templateData) {
		this.templateData = templateData;
	}

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}

	public  Object get(String key){
		return  this.templateData.get(key);
	}

}
