package com.superstudio.jrazor.template;

import com.superstudio.commons.MvcResources;
import com.superstudio.jrazor.HtmlHelper;
import com.superstudio.jrazor.IViewStartPageChild;
import com.superstudio.jrazor.StartPage;


public abstract class TemplateStartPage extends StartPage implements IViewStartPageChild
{
	private IViewStartPageChild _viewStartPageChild;

	public final HtmlHelper<Object> getHtml()
	{
		return getViewStartPageChild().getHtml();
	}

	

	public final TemplateContext getTemplateContext()
	{
		return getViewStartPageChild().getTemplateContext();
	}

	public final IViewStartPageChild getViewStartPageChild()
	{
		if (_viewStartPageChild == null)
		{
			IViewStartPageChild child = (getChildPage() instanceof IViewStartPageChild) ? getChildPage() : null;
			if (child == null)
			{
				throw new IllegalStateException(MvcResources.ViewStartPage_RequiresMvcRazorView);
			}
			_viewStartPageChild = child;
		}

		return _viewStartPageChild;
	}
}