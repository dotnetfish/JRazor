package com.superstudio.template.templatepages;

import java.util.*;

import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.exception.HttpException;
import com.superstudio.web.HttpRequestBase;
import com.superstudio.web.HttpResponseBase;
import com.superstudio.web.HttpServerUtilityBase;
import com.superstudio.web.HttpSessionStateBase;



public abstract class WebPageRenderingBase extends WebPageExecutingBase implements ITemplateFile
{
	//private IPrincipal _user;
	//private UrlDataList _urlData;
	private TemplateFileInfo _templateFileInfo;
	private DisplayModeProvider _displayModeProvider;

	/*public Cache getCache()
	{
		if (getContext() != null)
		{
			return getContext().getCache();
		}
		return null;
	}*/

	public final DisplayModeProvider getDisplayModeProvider()
	{
		return (_displayModeProvider != null) ? _displayModeProvider : DisplayModeProvider.getInstance();
	}

	public final void setDisplayModeProvider(DisplayModeProvider value)
	{
		_displayModeProvider = value;
	}

	protected final IDisplayMode getDisplayMode()
	{
		return getDisplayModeProvider().getDisplayMode(getContext());
	}

	public abstract String getLayout();
	public abstract void setLayout(String value) throws HttpException;

	public abstract Map<Object, Object> getPageData();

	public abstract Object getPage();

	private WebPageContext PageContext;
	public final WebPageContext getPageContext()
	{
		return PageContext;
	}
	public final void setPageContext(WebPageContext value)
	{
		PageContext = value;
	}

	/*public final ProfileBase getProfile()
	{
		if (getContext() != null)
		{
			return getContext().Profile;
		}
		return null;
	}*/

/*
	public HttpRequestBase getRequest()
	{
		if (getContext() != null)
		{
			return getContext().getRequest();
		}
		return null;
	}

	public HttpResponseBase getResponse()
	{
		if (getContext() != null)
		{
			return getContext().getResponse();
		}
		return null;
	}

	public HttpServerUtilityBase getServer()
	{
		if (getContext() != null)
		{
			return getContext().getServer();
		}
		return null;
	}

	public HttpSessionStateBase getSession()
	{
		if (getContext() != null)
		{
			return getContext().getSession();
		}
		return null;
	}
*/

	/*public List<String> getUrlData()
	{
		if (_urlData == null)
		{
			WebPageMatch match = WebPageRoute.GetWebPageMatch(getContext());
			if (match != null)
			{
				_urlData = new UrlDataList(match.PathInfo);
			}
			else
			{
					// REVIEW: Can there ever be no route match?
				_urlData = new UrlDataList(null);
			}
		}
		return _urlData;
	}*/

	/*public IPrincipal getUser()
	{
		if (_user == null)
		{
			return getContext().User;
		}
		return _user;
	}
	public void setUser(IPrincipal value)
	{
		_user = value;
	}
*/
	public TemplateFileInfo getTemplateInfo()
	{
		if (_templateFileInfo == null)
		{
			_templateFileInfo = new TemplateFileInfo(getVirtualPath());
		}
		return _templateFileInfo;
	}

	public boolean getIsPost()
	{
		return false;
		//return getRequest().getHttpMethod().equals("POST");
	}

	public boolean getIsAjax()
	{
		return false;
		/*HttpRequestBase request = getRequest();
		if (request == null)
		{
			return false;
		}
		return (request["X-Requested-With"].equals("XMLHttpRequest")) || ((request.getHeaders() != null) && (request.Headers["X-Requested-With"].equals("XMLHttpRequest")));
*/	}

	public final String getCulture()
	{
		return null;
		//return Thread.currentThread().CurrentCulture.Name;
	}
	public final void setCulture(String value)
	{
		if (StringHelper.isNullOrEmpty(value))
		{
				// GetCultureInfo accepts empty strings but throws for null strings. To maintain consistency in our string handling behavior, throw
			throw new IllegalArgumentException(CommonResources.Argument_Cannot_Be_Null_Or_Empty+"value");
		}
		//CultureUtil.SetCulture(Thread.currentThread(), getContext(), value);
	}

	public final String getUICulture()
	{
		return null;
		//return Thread.currentThread().CurrentUICulture.Name;
	}
	public final void setUICulture(String value)
	{
		if (StringHelper.isNullOrEmpty(value))
		{
				// GetCultureInfo accepts empty strings but throws for null strings. To maintain consistency in our string handling behavior, throw
			throw new IllegalArgumentException(CommonResources.Argument_Cannot_Be_Null_Or_Empty+"value");
		}
		//CultureUtil.SetUICulture(Thread.currentThread(), getContext(), value);
	}

	// Calls the execute() method, and calls runPage() if the page is an InitPage but
	// did not call runPage().
	public abstract void executePageHierarchy() throws Exception;

	public abstract HelperResult renderPage(String path, Object... data) throws Exception;
}