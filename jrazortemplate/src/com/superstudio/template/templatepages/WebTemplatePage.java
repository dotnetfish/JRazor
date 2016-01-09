package com.superstudio.template.templatepages;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.MvcResources;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.template.mvc.TemplateContext;
import com.superstudio.template.mvc.actionresult.TemplateDataDictionary;
import com.superstudio.template.mvc.context.HostContext;

import java.io.Writer;



public abstract class WebTemplatePage extends WebPageBase
{
	private TemplateDataDictionary _templateData;
	
	private HostContext _context;

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
	
	public HostContext getContext()
	{
		return (_context != null) ? _context : getTemplateContext().getHttpContext();
	}
	//@Override
	public void setContext(HostContext value)
	{
		_context = value;
	}
	
	private TemplateContext templateContext;

	private HtmlHelper<Object> html;
	public final HtmlHelper<Object> getHtml()
	{
		return html;
	}
	public final void setHtml(HtmlHelper<Object> value)
	{
		html = value;
	}

	public final Object getModel()
	{
		return getTemplateData().getModel();
	}

	private String overridenLayoutPath;
	public final String getOverridenLayoutPath()
	{
		return overridenLayoutPath;
	}
	public final void setOverridenLayoutPath(String value)
	{
		overridenLayoutPath = value;
	}

	

	private UrlHelper Url;

	private String layout;
	public final UrlHelper getUrl()
	{
		return Url;
	}
	public final void setUrl(UrlHelper value)
	{
		Url = value;
	}



	public final TemplateDataDictionary getTemplateData()
	{
		if (_templateData == null)
		{
			setTemplateData(new TemplateDataDictionary(null));
		}
		return _templateData;
	}
	public final void setTemplateData(TemplateDataDictionary value)
	{
		this._templateData=value;
	}

	
protected void configurePage(WebPageBase parentPage)
	{
		WebTemplatePage baseTemplatePage = (WebTemplatePage)((parentPage instanceof WebTemplatePage) ? parentPage : null);
		if (baseTemplatePage == null)
		{
				throw new IllegalStateException(
						String.format(CultureInfo.CurrentCulture,
								MvcResources.CshtmlTemplate_WrongTemplateBase, parentPage.getVirtualPath()));
		}

		// Set TemplateContext and TemplateData here so that the layout page inherits TemplateData from the main page
		setTemplateContext(baseTemplatePage.getTemplateContext());
		setTemplateData(baseTemplatePage.getTemplateData());
		initHelpers();
	}

	@Override
	public void executePageHierarchy()
	{
		// change the Writer so that things like html.BeginForm work correctly
		Writer oldWriter = getTemplateContext().getWriter();
		getTemplateContext().setWriter(getOutput());

		try {
			super.executePageHierarchy();
		} catch (ArgumentNullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Overwrite LayoutPage so that returning a template with a custom master page works.
		if (!StringHelper.isNullOrEmpty(getOverridenLayoutPath()))
		{
			setLayout(getOverridenLayoutPath());
		}

		// Restore the old template Context Writer
		getTemplateContext().setWriter (oldWriter);
	}

	public void initHelpers()
	{
		//setAjax(new AjaxHelper<Object>(getTemplateContext(), this));
		setHtml(new HtmlHelper<Object>(getTemplateContext(), this));
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