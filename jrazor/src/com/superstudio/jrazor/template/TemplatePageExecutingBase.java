package com.superstudio.jrazor.template;

import java.io.IOException;
import java.io.Writer;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.exception.HttpException;
import com.superstudio.jrazor.*;




/*
WebPage class hierarchy

WebPageExecutingBase                        The base class for all Plan9 files (_pagestart, _appstart, and regular pages)
    ApplicationStartPage                    Used for _appstart.cshtml
    WebPageRenderingBase
        StartPage                           Used for _pagestart.cshtml
        WebPageBase
            WebPage                         Plan9Pages
            ViewWebPage?                    MVC Views
HelperPage                                  Base class for Web Pages in App_Code.
*/

// The base class for all CSHTML files (_pagestart, _appstart, and regular pages)
public abstract class TemplatePageExecutingBase
{
	private IVirtualPathFactory _virtualPathFactory;
	//private DynamicHttpApplicationState _dynamicAppState;
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

	/*public HttpApplicationStateBase getAppState()
	{
		if (getContext() != null)
		{
			return getContext().getApplication();
		}
		return null;
	}*/

/*	public Object getApp()
	{
		if (_dynamicAppState == null && getAppState() != null)
		{
			_dynamicAppState = new DynamicHttpApplicationState();
		}
		return _dynamicAppState;
	}*/

	private TemplateHostContext context;
	public TemplateHostContext getContext()
	{
		return context;
	}
	public void setContext(TemplateHostContext value)
	{
		context = value;
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


	public abstract void Execute() throws Exception;

	/*public String Href(String path, Object... pathParts)
	{
		return UrlUtil.GenerateClientUrl(getContext(), getVirtualPath(), path, pathParts);
	}*/

	protected final void BeginContext(int startPosition, int length, boolean isLiteral)
	{
		BeginContext(GetOutputWriter(), getVirtualPath(), startPosition, length, isLiteral);
	}

	protected final void BeginContext(String virtualPath, int startPosition, int length, boolean isLiteral)
	{
		BeginContext(GetOutputWriter(), virtualPath, startPosition, length, isLiteral);
	}

	protected final void BeginContext(Writer writer, int startPosition, int length, boolean isLiteral)
	{
		BeginContext(writer, getVirtualPath(), startPosition, length, isLiteral);
	}

	protected final void beginContext(Writer writer, String virtualPath, int startPosition, int length, boolean isLiteral)
	{
		// Double check that the instrumentation service is active because WriteAttribute always calls this
		if (getInstrumentationService().getIsAvailable())
		{
			getInstrumentationService().beginContext(getContext(), virtualPath, writer, startPosition, length, isLiteral);
		}
	}

	protected final void endContext(int startPosition, int length, boolean isLiteral)
	{
		endContext(GetOutputWriter(), getVirtualPath(), startPosition, length, isLiteral);
	}

	protected final void endContext(String virtualPath, int startPosition, int length, boolean isLiteral)
	{
		endContext(GetOutputWriter(), virtualPath, startPosition, length, isLiteral);
	}

	protected final void endContext(Writer writer, int startPosition, int length, boolean isLiteral)
	{
		EndContext(writer, getVirtualPath(), startPosition, length, isLiteral);
	}

	protected final void endContext(Writer writer, String virtualPath, int startPosition, int length, boolean isLiteral)
	{
		// Double check that the instrumentation service is active because WriteAttribute always calls this
		if (getInstrumentationService().getIsAvailable())
		{
			getInstrumentationService().endContext(getContext(), virtualPath, writer, startPosition, length, isLiteral);
		}
	}

	public String getDirectory(String virtualPath)
	{
		return VirtualPathUtility.GetDirectory(virtualPath);
	}

	/** 
	 Normalizes path relative to the current virtual path and throws if a file does not exist at the location.
	 * @throws HttpException 
	*/
	protected String normalizeLayoutPagePath(String layoutPagePath) throws HttpException
	{
		String virtualPath = normalizePath(layoutPagePath);
		// Look for it as specified, either absolute, relative or same folder
		if (getVirtualPathFactory().exists(virtualPath))
		{
			return virtualPath;
		}
		throw new HttpException(StringHelper.format(CultureInfo.CurrentCulture,
				WebPageResources.WebPage_LayoutPageNotFound, new Object[]{layoutPagePath,virtualPath}
				
				));
	}

	public String normalizePath(String path)
	{
		// If it's relative, resolve it
		return VirtualPathUtility.combine(getVirtualPath(), path);
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
		WriteAttributeTo(getVirtualPath(), writer, name, prefix, suffix, values);
	}

	protected void writeAttributeTo(String pageVirtualPath, Writer writer, String name, PositionTagged<String> prefix, PositionTagged<String> suffix, AttributeValue... values) throws IOException
	{
		boolean first = true;
		boolean wroteSomething = false;
		if (values.length == 0)
		{
			// Explicitly empty attribute, so write the prefix and suffix
			WritePositionTaggedLiteral(writer, pageVirtualPath, prefix);
			WritePositionTaggedLiteral(writer, pageVirtualPath, suffix);
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
					stringValue = (val.getValue() instanceof String) ? val.getValue().toString() : null;
				}

				if (first)
				{
					try {
						WritePositionTaggedLiteral(writer, pageVirtualPath, prefix);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					first = false;
				}
				else
				{
					WritePositionTaggedLiteral(writer, pageVirtualPath, attrVal.getPrefix());
				}

				// Calculate length of the source span by the position of the next value (or suffix)
				int sourceLength = next.getPosition() - attrVal.getValue().getPosition();

				BeginContext(writer, pageVirtualPath, attrVal.getValue().getPosition(), sourceLength, attrVal.getLiteral());
				if (attrVal.getLiteral())
				{
					WriteLiteralTo(writer, (stringValue != null) ? stringValue : val.getValue());
				}
				else
				{
					WriteTo(writer, (stringValue != null) ? stringValue : val.getValue()); // Write value
				}
				endContext(writer, pageVirtualPath, attrVal.getValue().getPosition(), sourceLength, attrVal.getLiteral());
				wroteSomething = true;
			}
			if (wroteSomething)
			{
				WritePositionTaggedLiteral(writer, pageVirtualPath, suffix);
			}
		}
	}

	private void WritePositionTaggedLiteral(Writer writer, String pageVirtualPath, String value, int position) throws IOException
	{
		BeginContext(writer, pageVirtualPath, position, value.length(), true);
		WriteLiteralTo(writer, value);
		EndContext(writer, pageVirtualPath, position, value.length(), true);
	}

	private void WritePositionTaggedLiteral(Writer writer, String pageVirtualPath, PositionTagged<String> value) throws IOException
	{
		WritePositionTaggedLiteral(writer, pageVirtualPath, value.getValue(), value.getPosition());
	}

	// This method is called by generated code and needs to stay in sync with the parser
	public static void WriteTo(Writer writer, HelperResult content)
	{
		if (content != null)
		{
			content.WriteTo(writer);
		}
	}

	// This method is called by generated code and needs to stay in sync with the parser
	public static void WriteTo(Writer writer, Object content) throws IOException
	{
		if(content==null)return;
		writer.write(EncodeUtility.HtmlEncode(content.toString()));
	}

	// This method is called by generated code and needs to stay in sync with the parser
	public static void WriteLiteralTo(Writer writer, Object content) throws IOException
	{
		if(content==null)return;
		writer.write(content.toString());
	}

	protected Writer GetOutputWriter()
	{
		return null;
	}
}