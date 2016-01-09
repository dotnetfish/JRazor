package com.superstudio.template.templatepages;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.exception.HttpException;
import com.superstudio.template.mvc.context.HostContext;
import com.superstudio.web.HttpApplicationStateBase;
import com.superstudio.web.HttpUtility;

import java.io.IOException;
import java.io.Writer;



/*
WebPage class hierarchy

WebPageExecutingBase                        The base class for all Plan9 files (_pagestart, _appstart, and regular pages)
    ApplicationStartPage                    Used for _appstart.cshtml
    WebPageRenderingBase
        StartPage                           Used for _pagestart.cshtml
        WebPageBase
            WebPage                         Plan9Pages
            TemplateWebPage?                    MVC Templates
HelperPage                                  Base class for Web Pages in App_Code.
*/

// The base class for all CSHTML files (_pagestart, _appstart, and regular pages)
public abstract class WebPageExecutingBase
{
	private IVirtualPathFactory _virtualPathFactory;
	private DynamicHttpApplicationState _dynamicAppState;
	private InstrumentationService _instrumentationService = null;

	public final InstrumentationService getInstrumentationService()
	{
		if (_instrumentationService == null)
		{
			_instrumentationService = new InstrumentationService();
		}
		return _instrumentationService;
	}
	public final void setInstrumentationService(InstrumentationService value)
	{
		_instrumentationService = value;
	}

	public HttpApplicationStateBase getAppState()
	{
		if (getContext() != null)
		{
			return getContext().getApplication();
		}
		return null;
	}

	public Object getApp()
	{
		if (_dynamicAppState == null && getAppState() != null)
		{
			_dynamicAppState = new DynamicHttpApplicationState();
		}
		return _dynamicAppState;
	}

	private HostContext Context;
	public HostContext getContext()
	{
		return Context;
	}
	public void setContext(HostContext value)
	{
		Context = value;
	}

	private String VirtualPath;
	public String getVirtualPath()
	{
		return VirtualPath;
	}
	public void setVirtualPath(String value)
	{
		VirtualPath = value;
	}

	public IVirtualPathFactory getVirtualPathFactory()
	{
		return (_virtualPathFactory != null) ? _virtualPathFactory : VirtualPathFactoryManager.getInstance();
	}
	public void setVirtualPathFactory(IVirtualPathFactory value)
	{
		_virtualPathFactory = value;
	}


	public abstract void execute() throws Exception;

	public String href(String path, Object... pathParts)
	{
		return UrlUtil.generateClientUrl(getContext(), getVirtualPath(), path, pathParts);
	}

	protected final void beginContext(int startPosition, int length, boolean isLiteral)
	{
		beginContext(getOutputWriter(), getVirtualPath(), startPosition, length, isLiteral);
	}

	protected final void beginContext(String virtualPath, int startPosition, int length, boolean isLiteral)
	{
		beginContext(getOutputWriter(), virtualPath, startPosition, length, isLiteral);
	}

	protected final void beginContext(Writer writer, int startPosition, int length, boolean isLiteral)
	{
		beginContext(writer, getVirtualPath(), startPosition, length, isLiteral);
	}

	protected final void beginContext(Writer writer, String virtualPath, int startPosition, int length, boolean isLiteral)
	{
		// Double check that the instrumentation service is active because writeAttribute always calls this
		if (getInstrumentationService().getIsAvailable())
		{
			getInstrumentationService().beginContext(getContext(), virtualPath, writer, startPosition, length, isLiteral);
		}
	}

	protected final void endContext(int startPosition, int length, boolean isLiteral)
	{
		endContext(getOutputWriter(), getVirtualPath(), startPosition, length, isLiteral);
	}

	protected final void endContext(String virtualPath, int startPosition, int length, boolean isLiteral)
	{
		endContext(getOutputWriter(), virtualPath, startPosition, length, isLiteral);
	}

	protected final void endContext(Writer writer, int startPosition, int length, boolean isLiteral)
	{
		endContext(writer, getVirtualPath(), startPosition, length, isLiteral);
	}

	protected final void endContext(Writer writer, String virtualPath, int startPosition, int length, boolean isLiteral)
	{
		// Double check that the instrumentation service is active because writeAttribute always calls this
		if (getInstrumentationService().getIsAvailable())
		{
			getInstrumentationService().endContext(getContext(), virtualPath, writer, startPosition, length, isLiteral);
		}
	}

	public String getDirectory(String virtualPath)
	{
		return VirtualPathUtility.getDirectory(virtualPath);
	}

	/** 
	 Normalizes path relative to the current virtual path and throws if a file does not exist at the location.
	 * @throws HttpException 
	*/
	protected String normalizeLayoutPagePath(String layoutPagePath) throws HttpException
	{
		//String virtualPath = normalizePath(layoutPagePath);
		//layoutPagePath=layoutPagePath.replace("~","");
		//String virtualPath= Paths.get(layoutPagePath).normalize().toString();
		// Look for it as specified, either absolute, relative or same folder
		if (getVirtualPathFactory().exists(layoutPagePath))
		{
			return layoutPagePath;
		}
		throw new HttpException(StringHelper.format(CultureInfo.CurrentCulture, 
				WebPageResources.WebPage_LayoutPageNotFound, new Object[]{layoutPagePath,layoutPagePath}
				
				));
	}

	public String normalizePath(String path)
	{
		// If it's relative, resolve it
		return  path;
	//	return VirtualPathUtility.combine(getVirtualPath(), path);
	}

	public abstract void write(HelperResult result);

	public abstract void write(Object value) throws IOException;

	public abstract void writeLiteral(Object value) throws IOException;

	public void writeAttribute(String name, PositionTagged<String> prefix, PositionTagged<String> suffix, AttributeValue... values) throws IOException
	{
		writeAttributeTo(getOutputWriter(), name, prefix, suffix, values);
	}

	public void writeAttributeTo(Writer writer, String name, PositionTagged<String> prefix, PositionTagged<String> suffix, AttributeValue... values) throws IOException
	{
		writeAttributeTo(getVirtualPath(), writer, name, prefix, suffix, values);
	}

	protected void writeAttributeTo(String pageVirtualPath, Writer writer, String name, PositionTagged<String> prefix, PositionTagged<String> suffix, AttributeValue... values) throws IOException
	{
		boolean first = true;
		boolean wroteSomething = false;
		if (values.length == 0)
		{
			// Explicitly empty attribute, so write the prefix and suffix
			writePositionTaggedLiteral(writer, pageVirtualPath, prefix);
			writePositionTaggedLiteral(writer, pageVirtualPath, suffix);
		}
		else
		{
			for (int i = 0; i < values.length; i++)
			{
				AttributeValue attrVal = values[i];
				PositionTagged<Object> val = attrVal.getValue();
				PositionTagged<String> next = i == values.length - 1 ? suffix : values[i + 1].getPrefix(); // Still in the list, grab the next prefix -  End of the list, grab the suffix

				if (val.getValue() == null)
				{
					// Nothing to write
					continue;
				}

				// The special cases here are that the value we're writing might already be a string, or that the 
				// value might be a bool. If the value is the bool 'true' we want to write the attribute name instead
				// of the string 'true'. If the value is the bool 'false' we don't want to write anything.
				//
				// Otherwise the value is another object (perhaps an IHtmlString), and we'll ask it to format itself.
				String stringValue;
				Boolean boolValue = (Boolean)(val.getValue());
				if (boolValue.equals(true))
				{
					stringValue = name;
				}
				else if (boolValue.equals(false))
				{
					continue;
				}
				else
				{
					stringValue = (String)((val.getValue() instanceof String) ? val.getValue() : null);
				}

				if (first)
				{
					try {
						writePositionTaggedLiteral(writer, pageVirtualPath, prefix);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					first = false;
				}
				else
				{
					writePositionTaggedLiteral(writer, pageVirtualPath, attrVal.getPrefix());
				}

				// Calculate length of the source span by the position of the next value (or suffix)
				int sourceLength = next.getPosition() - attrVal.getValue().getPosition();

				beginContext(writer, pageVirtualPath, attrVal.getValue().getPosition(), sourceLength, attrVal.getLiteral());
				if (attrVal.getLiteral())
				{
					writeLiteralTo(writer, (stringValue != null) ? stringValue : val.getValue());
				}
				else
				{
					writeTo(writer, (stringValue != null) ? stringValue : val.getValue()); // write value
				}
				endContext(writer, pageVirtualPath, attrVal.getValue().getPosition(), sourceLength, attrVal.getLiteral());
				wroteSomething = true;
			}
			if (wroteSomething)
			{
				writePositionTaggedLiteral(writer, pageVirtualPath, suffix);
			}
		}
	}

	private void writePositionTaggedLiteral(Writer writer, String pageVirtualPath, String value, int position) throws IOException
	{
		beginContext(writer, pageVirtualPath, position, value.length(), true);
		writeLiteralTo(writer, value);
		endContext(writer, pageVirtualPath, position, value.length(), true);
	}

	private void writePositionTaggedLiteral(Writer writer, String pageVirtualPath, PositionTagged<String> value) throws IOException
	{
		writePositionTaggedLiteral(writer, pageVirtualPath, value.getValue(), value.getPosition());
	}

	// This method is called by generated code and needs to stay in sync with the parser
	public static void writeTo(Writer writer, HelperResult content)
	{
		if (content != null)
		{
			content.writeTo(writer);
		}
	}

	// This method is called by generated code and needs to stay in sync with the parser
	public static void writeTo(Writer writer, Object content) throws IOException
	{
		if(content==null)return;
		writer.write(HttpUtility.HtmlEncode(content.toString()));
	}

	// This method is called by generated code and needs to stay in sync with the parser
	public static void writeLiteralTo(Writer writer, Object content) throws IOException
	{
		if(content==null)return;
		writer.write(content.toString());
	}

	protected Writer getOutputWriter()
	{
		return null;
	}
}