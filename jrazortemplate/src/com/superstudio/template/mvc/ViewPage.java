/*package com.superstudio.web.mvc;

import java.io.Writer;
import java.util.Map;

import com.superstudio.commons.EventArgs;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.web.mvc.actionResult.TemplateDataDictionary;
import HtmlHelper;
import UrlHelper;

public class TemplatePage extends Page implements ITemplateDataContainer
{

//ORIGINAL LINE: [ThreadStatic] private static int _nextId;
	private static ThreadLocal<Integer> _nextId;

	private TemplateDataDictionary _dynamicTemplateData;
	private String _masterLocation;

	private TemplateDataDictionary _templateData;

	private AjaxHelper<Object> Ajax;
	public final AjaxHelper<Object> getAjax()
	{
		return Ajax;
	}
	public final void setAjax(AjaxHelper<Object> value)
	{
		Ajax = value;
	}

	private HtmlHelper<Object> Html;
	public final HtmlHelper<Object> getHtml()
	{
		return Html;
	}
	public final void setHtml(HtmlHelper<Object> value)
	{
		Html = value;
	}

	public final String getMasterLocation()
	{
		return (_masterLocation != null) ? _masterLocation : "";
	}
	public final void setMasterLocation(String value)
	{
		_masterLocation = value;
	}

	public final Object getModel()
	{
		return getTemplateData().getModel();
	}

	public final Map<String,Object> getTempData()
	{
		return getTemplateContext().getTempData();
	}

	private UrlHelper Url;
	public final UrlHelper getUrl()
	{
		return Url;
	}
	public final void setUrl(UrlHelper value)
	{
		Url = value;
	}

	public final Object getTemplateBag()
	{
		if (_dynamicTemplateData == null)
		{
			_dynamicTemplateData = new TemplateDataDictionary(() -> getTemplateData());
		}
		return _dynamicTemplateData;
	}

	private TemplateContext templateContext;
	public final TemplateContext getTemplateContext()
	{
		return templateContext;
	}
	public final void setTemplateContext(TemplateContext value)
	{
		templateContext = value;
	}


//ORIGINAL LINE: [SuppressMessage("Microsoft.Usage", "CA2227:CollectionPropertiesShouldBeReadOnly", Justification = "This is the mechanism by which the TemplatePage gets its TemplateDataDictionary object.")] public TemplateDataDictionary TemplateData
	public final TemplateDataDictionary getTemplateData()
	{
		if (_templateData == null)
		{
			SetTemplateData(new TemplateDataDictionary());
		}
		return _templateData;
	}
	public final void setTemplateData(TemplateDataDictionary value)
	{
		SetTemplateData(value);
	}

	private Writer writer;//HtmlTextWriter
	public final Writer getWriter()
	{
		return writer;
	}
	private void setWriter(Writer value)
	{
		writer = value;
	}

	public void InitHelpers()
	{
		//setAjax(new AjaxHelper<Object>(getTemplateContext(), this));
		setHtml(new HtmlHelper<Object>(getTemplateContext(), this));
		setUrl(new UrlHelper(getTemplateContext().getRequestContext()));

	public static String NextId()
	{
		int i=_nextId.get()+1;
	
		_nextId.set(i);
		return String.valueOf(i);
	}

	@Override
	protected void OnPreInit(EventArgs e)
	{
		super.OnPreInit(e);

		if (!StringUtils.isBlank(getMasterLocation()))
		{
			this.masterPageFile = getMasterLocation();
		}
	}

	@Override
	public void ProcessRequest(HttpContext context)
	{
		// Tracing requires IDs to be unique.
		ID = NextId();

		super.ProcessRequest(context);
	}

	@Override
	protected void Render(HtmlTextWriter writer)
	{
		setWriter(writer);
		try
		{
			super.Render(writer);
		}
		finally
		{
			setWriter(null);
		}
	}


//ORIGINAL LINE: [SuppressMessage("Microsoft.Reliability", "CA2000:Dispose objects before losing scope", Justification = "The object is disposed in the finally block of the method")] public virtual void RenderTemplate(TemplateContext templateContext)
	public void RenderTemplate(TemplateContext templateContext)
	{
		setTemplateContext(templateContext);
		InitHelpers();

		boolean createdSwitchWriter = false;
		SwitchWriter switchWriter = (SwitchWriter)((templateContext.HttpContext.Response.Output instanceof SwitchWriter) ? templateContext.HttpContext.Response.Output : null);

		try
		{
			if (switchWriter == null)
			{
				switchWriter = new SwitchWriter();
				createdSwitchWriter = true;
			}

			try (switchWriter.Scope(templateContext.Writer))
			{
				if (createdSwitchWriter)
				{
					// It's safe to reset the _nextId within a Server.Execute() since it pushes a new TraceContext onto
					// the stack, so there won't be an ID conflict.
					int originalNextId = _nextId;
					try
					{
						_nextId = 0;
						templateContext.HttpContext.Server.Execute(HttpHandlerUtil.WrapForServerExecute(this), switchWriter, true); // preserveForm
					}
					finally
					{
						// Restore the original _nextId in case this isn't actually the outermost template, since resetting
						// the _nextId may now cause trace ID conflicts in the outer template.
						_nextId = originalNextId;
					}
				}
				else
				{
					ProcessRequest(HttpContext.Current);
				}
			}
		}
		finally
		{
			if (createdSwitchWriter)
			{
				switchWriter.Dispose();
			}
		}
	}


//ORIGINAL LINE: [SuppressMessage("Microsoft.Usage", "CA1801:RetemplateUnusedParameters", MessageId = "textWriter", Justification = "This method existed in MVC 1.0 and has been deprecated.")][SuppressMessage("Microsoft.Performance", "CA1822:MarkMembersAsStatic", Justification = "This method existed in MVC 1.0 and has been deprecated.")][Obsolete("The TextWriter is now provided by the TemplateContext object passed to the RenderTemplate method.", true)] public void SetTextWriter(TextWriter textWriter)
	@Deprecated
	public final void SetTextWriter(TextWriter textWriter) // error
	{
		// this is now a no-op
	}

	protected void SetTemplateData(TemplateDataDictionary templateData)
	{
		_templateData = templateData;
	}

	public static class SwitchWriter extends TextWriter
	{
		public SwitchWriter()
		{
			super(Locale.CurrentCulture);
		}

		@Override
		public Encoding getEncoding()
		{
			return getInnerWriter().Encoding;
		}

		@Override
		public IFormatProvider getFormatProvider()
		{
			return getInnerWriter().FormatProvider;
		}

		private TextWriter InnerWriter;
		public final TextWriter getInnerWriter()
		{
			return InnerWriter;
		}
		public final void setInnerWriter(TextWriter value)
		{
			InnerWriter = value;
		}

		@Override
		public String getNewLine()
		{
			return getInnerWriter().NewLine;
		}
		@Override
		public void setNewLine(String value)
		{
			getInnerWriter().NewLine = value;
		}

		@Override
		public void Close()
		{
			getInnerWriter().Close();
		}

		@Override
		public void Flush()
		{
			getInnerWriter().Flush();
		}

		public final java.io.Closeable Scope(TextWriter writer)
		{
			WriterScope scope = new WriterScope(this, getInnerWriter());

			try
			{
				if (writer != this)
				{
					setInnerWriter(writer);
				}

				return scope;
			}
			catch (java.lang.Exception e)
			{
				scope.Dispose();
				throw e;
			}
		}

		@Override
		public void Write(boolean value)
		{
			getInnerWriter().Write(value);
		}

		@Override
		public void Write(char value)
		{
			getInnerWriter().Write(value);
		}

		@Override
		public void Write(char[] buffer)
		{
			getInnerWriter().Write(buffer);
		}

		@Override
		public void Write(char[] buffer, int index, int count)
		{
			getInnerWriter().Write(buffer, index, count);
		}

		@Override
		public void Write(java.math.BigDecimal value)
		{
			getInnerWriter().Write(value);
		}

		@Override
		public void Write(double value)
		{
			getInnerWriter().Write(value);
		}

		@Override
		public void Write(float value)
		{
			getInnerWriter().Write(value);
		}

		@Override
		public void Write(int value)
		{
			getInnerWriter().Write(value);
		}

		@Override
		public void Write(long value)
		{
			getInnerWriter().Write(value);
		}

		@Override
		public void Write(Object value)
		{
			getInnerWriter().Write(value);
		}

		@Override
		public void Write(String format, Object arg0)
		{
			getInnerWriter().Write(format, arg0);
		}

		@Override
		public void Write(String format, Object arg0, Object arg1)
		{
			getInnerWriter().Write(format, arg0, arg1);
		}

		@Override
		public void Write(String format, Object arg0, Object arg1, Object arg2)
		{
			getInnerWriter().Write(format, arg0, arg1, arg2);
		}

		@Override
		public void Write(String format, Object... arg)
		{
			getInnerWriter().Write(format, arg);
		}

		@Override
		public void Write(String value)
		{
			getInnerWriter().Write(value);
		}


//ORIGINAL LINE: public override void Write(uint value)
		@Override
		public void Write(int value)
		{
			getInnerWriter().Write(value);
		}


//ORIGINAL LINE: public override void Write(ulong value)
		@Override
		public void Write(long value)
		{
			getInnerWriter().Write(value);
		}

		@Override
		public void WriteLine()
		{
			getInnerWriter().WriteLine();
		}

		@Override
		public void WriteLine(boolean value)
		{
			getInnerWriter().WriteLine(value);
		}

		@Override
		public void WriteLine(char value)
		{
			getInnerWriter().WriteLine(value);
		}

		@Override
		public void WriteLine(char[] buffer)
		{
			getInnerWriter().WriteLine(buffer);
		}

		@Override
		public void WriteLine(char[] buffer, int index, int count)
		{
			getInnerWriter().WriteLine(buffer, index, count);
		}

		@Override
		public void WriteLine(java.math.BigDecimal value)
		{
			getInnerWriter().WriteLine(value);
		}

		@Override
		public void WriteLine(double value)
		{
			getInnerWriter().WriteLine(value);
		}

		@Override
		public void WriteLine(float value)
		{
			getInnerWriter().WriteLine(value);
		}

		@Override
		public void WriteLine(int value)
		{
			getInnerWriter().WriteLine(value);
		}

		@Override
		public void WriteLine(long value)
		{
			getInnerWriter().WriteLine(value);
		}

		@Override
		public void WriteLine(Object value)
		{
			getInnerWriter().WriteLine(value);
		}

		@Override
		public void WriteLine(String format, Object arg0)
		{
			getInnerWriter().WriteLine(format, arg0);
		}

		@Override
		public void WriteLine(String format, Object arg0, Object arg1)
		{
			getInnerWriter().WriteLine(format, arg0, arg1);
		}

		@Override
		public void WriteLine(String format, Object arg0, Object arg1, Object arg2)
		{
			getInnerWriter().WriteLine(format, arg0, arg1, arg2);
		}

		@Override
		public void WriteLine(String format, Object... arg)
		{
			getInnerWriter().WriteLine(format, arg);
		}

		@Override
		public void WriteLine(String value)
		{
			getInnerWriter().WriteLine(value);
		}


//ORIGINAL LINE: public override void WriteLine(uint value)
		@Override
		public void WriteLine(int value)
		{
			getInnerWriter().WriteLine(value);
		}


//ORIGINAL LINE: public override void WriteLine(ulong value)
		@Override
		public void WriteLine(long value)
		{
			getInnerWriter().WriteLine(value);
		}

		private final static class WriterScope implements java.io.Closeable
		{
			private SwitchWriter _switchWriter;
			private TextWriter _writerToRestore;

			public WriterScope(SwitchWriter switchWriter, TextWriter writerToRestore)
			{
				_switchWriter = switchWriter;
				_writerToRestore = writerToRestore;
			}

			public void close() throws java.io.IOException
			{
				_switchWriter.setInnerWriter(_writerToRestore);
			}
		}
	}
}*/