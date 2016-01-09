package com.superstudio.template.mvc;

import com.superstudio.template.templatepages.UrlHelper;
import com.superstudio.template.templatepages.HtmlHelper;


public interface ITemplateStartPageChild
{
	HtmlHelper<Object> getHtml();
	UrlHelper getUrl();
	TemplateContext getTemplateContext();
}