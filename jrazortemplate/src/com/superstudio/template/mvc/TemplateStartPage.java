package com.superstudio.template.mvc;

import com.superstudio.commons.MvcResources;
import com.superstudio.template.templatepages.HtmlHelper;
import com.superstudio.template.templatepages.StartPage;
import com.superstudio.template.templatepages.UrlHelper;



public abstract class TemplateStartPage extends StartPage implements ITemplateStartPageChild
{
	private ITemplateStartPageChild _templateStartPageChild;

	public final HtmlHelper<Object> getHtml()
	{
		return getTemplateStartPageChild().getHtml();
	}

	public final UrlHelper getUrl()
	{
		return getTemplateStartPageChild().getUrl();
	}

	public final TemplateContext getTemplateContext()
	{
		return getTemplateStartPageChild().getTemplateContext();
	}

	public final ITemplateStartPageChild getTemplateStartPageChild()
	{
		if (_templateStartPageChild == null)
		{
			ITemplateStartPageChild child = (ITemplateStartPageChild)((getChildPage() instanceof ITemplateStartPageChild) ? getChildPage() : null);
			if (child == null)
			{
				throw new IllegalStateException(MvcResources.TemplateStartPage_RequiresMvcRazorTemplate);
			}
			_templateStartPageChild = child;
		}

		return _templateStartPageChild;
	}
}