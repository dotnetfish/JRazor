package com.superstudio.template.mvc.context;

/**
 * Created by kenqu on 2015/11/19.
 */
public class TemplateInfo {
    public TemplateInfo(){

    }
    public TemplateInfo(String virtualPath){

    }
    private String templateName;

    private String templateCategory;

    private String area="";

    public String getTemplateCategory() {
        return templateCategory;
    }

    public void setTemplateCategory(String templateCategory) {
        this.templateCategory = templateCategory;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
