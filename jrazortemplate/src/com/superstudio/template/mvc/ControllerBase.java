package com.superstudio.template.mvc;

import java.util.HashMap;
import java.util.Map;

import com.superstudio.template.mvc.actionresult.TemplateDataDictionary;
import com.superstudio.template.mvc.context.RenderContext;
import com.superstudio.template.mvc.context.RequestContext;

public abstract class ControllerBase {

	public void initialize(RequestContext requestContext) {
		tempData=new HashMap<String,Object>();
		templateData=new TemplateDataDictionary(tempData);
		
		
		//setTemplateData(new )
	}
	
	protected ControllerBase(){
		
	}

	private RenderContext renderContext;
    public RenderContext getRenderContext(){
    	return this.renderContext;
    }
    private Map<String,Object> tempData;
   // public Boolean ValidateRequest { get; set; }
    public IValueProvider valueProvider;
   
   // public dynamic TemplateBag { get; }
    private TemplateDataDictionary templateData;

    protected  void execute(RequestContext requestContext){
    	
    }

	protected abstract void executeCore();

	public Map<String,Object> getTempData() {
		return tempData;
	}

	public void setTempData(Map<String,Object> tempData) {
		this.tempData = tempData;
	}

	public TemplateDataDictionary getTemplateData() {
		return templateData;
	}

	public void setTemplateData(TemplateDataDictionary templateData) {
		this.templateData = templateData;
	}
  

}
