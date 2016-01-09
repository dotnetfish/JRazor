package com.superstudio.jrazor.template;

import java.io.Writer;

import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.commons.csharpbridge.StringHelper;


public abstract class TemplatePage extends TemplatePageBase
{
	private TemplateDataDictionary templateData;
	
	private TemplateHostContext _context;

	/*private AjaxHelper<Object> Ajax;
	public final AjaxHelper<Object> getAjax()
	{
		return Ajax;
	}
	public final void setAjax(AjaxHelper<Object> value)
	{
		Ajax = value;
	}
*/
		// REVIEW why are we forced to override this?
	
	
	
	
	private TemplateContext templateContext;

	/*private HtmlHelper<Object> Html;
	public final HtmlHelper<Object> getHtml()
	{
		return Html;
	}
	public final void setHtml(HtmlHelper<Object> value)
	{
		Html = value;
	}*/

	public final Object getModel()
	{
		return getTemplateData().getModel();
	}

	private String OverridenLayoutPath;
	public final String getOverridenLayoutPath()
	{
		return OverridenLayoutPath;
	}
	public final void setOverridenLayoutPath(String value)
	{
		OverridenLayoutPath = value;
	}

	private String layout;

	/*private UrlHelper Url;

	
	public final UrlHelper getUrl()
	{
		return Url;
	}
	public final void setUrl(UrlHelper value)
	{
		Url = value;
	}*/

	

	//private TemplateContext TemplateContext;
	/*public final TemplateContext getTemplateContext()
	{
		return TemplateContext;
	}
	public final void setTemplateContext(TemplateContext value)
	{
		TemplateContext = value;
	}*/

	public final TemplateDataDictionary getTemplateData()
	{
		if (templateData == null)
		{
			setTemplateData(new TemplateDataDictionary(null));
		}
		return templateData;
	}
	public final void setTemplateData(TemplateDataDictionary value)
	{
		this.templateData=value;
	}

	
	protected void configurePage(TemplatePageBase parentPage)
	{
		/*TemplatePageBase baseViewPage = (TemplatePageBase)((parentPage instanceof TemplatePageBase) ? parentPage : null);
		if (baseViewPage == null)
		{
				throw new IllegalStateException(StringHelper.format(CultureInfo.CurrentCulture, MvcResources.CshtmlView_WrongViewBase, parentPage.VirtualPath));
		}

		// Set TemplateContext and ViewData here so that the layout page inherits ViewData from the main page
		setTemplateContext(baseViewPage.getTemplateContext());
		setViewData(baseViewPage.getViewData());
		InitHelpers();*/
	}

	@Override
	public void executePageHierarchy()
	{
		// Change the Writer so that things like Html.BeginForm work correctly
		Writer oldWriter = getTemplateContext().getWriter();
		getTemplateContext().setWriter(getOutput());

		try {
			super.executePageHierarchy();
		} catch (ArgumentNullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Overwrite LayoutPage so that returning a view with a custom master page works.
		if (!StringHelper.isNullOrEmpty(getOverridenLayoutPath()))
		{
			setLayout(getOverridenLayoutPath());
		}

		// Restore the old View Context Writer
		getTemplateContext().setWriter (oldWriter);
	}

	public void InitHelpers()
	{
		//setAjax(new AjaxHelper<Object>(getTemplateContext(), this));
		//setHtml(new HtmlHelper<Object>(getTemplateContext(), this));
		//setUrl(new UrlHelper(getTemplateContext().getRequestContext()));
	}

	
	public TemplateContext getTemplateContext() {
		return templateContext;
	}
	public void setTemplateContext(TemplateContext templateContext) {
		this.templateContext = templateContext;
	}
	public String getLayout() {
		return layout;
	}
	public void setLayout(String layout) {
		this.layout = layout;
	}
}