package com.superstudio.template.templatepages;

import com.superstudio.template.mvc.context.HostContext;
import com.superstudio.template.mvc.context.HttpContextWrapper;

import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;


// Class for containing various pieces of data required by a WebPage
public class WebPageContext
{
	private static final Object _sourceFileKey = new Object();
	private Stack<Writer> _outputStack;
	private Stack<HashMap<String, SectionWriter>> _sectionWritersStack;
	private Map<Object, Object> _pageData;
	//private ValidationHelper _validation;
	//private ModelStateDictionary _modelStateDictionary;

	public WebPageContext()
	{
		this(null, null, null);
	}

	public WebPageContext(HostContext context, WebPageRenderingBase page, Object model)
	{
		setHttpContext(context);
		setPage(page);
		setModel(model);
	}

	public static WebPageContext getCurrent() throws Exception
	{
			// The TemplateStack stores instances of WebPageRenderingBase. 
			// Retrieve the top-most item from the stack and cast it to WebPageBase. 

		HostContext httpContext = HostContext.getCurrent();
		if (httpContext != null)
		{
			HttpContextWrapper contextWrapper = new HttpContextWrapper(httpContext);

			ITemplateFile currentTemplate = TemplateStack.getCurrentTemplate(contextWrapper);
			WebPageRenderingBase currentPage = ((WebPageRenderingBase)((currentTemplate instanceof WebPageRenderingBase) ? currentTemplate : null));

			return (currentPage == null) ? null : currentPage.getPageContext();
		}
		return null;
	}

	private HostContext httpContext;
	public final HostContext getHttpContext()
	{
		return httpContext;
	}
	public final void setHttpContext(HostContext value)
	{
		httpContext = value;
	}

	private Object Model;
	public final Object getModel()
	{
		return Model;
	}
	public final void setModel(Object value)
	{
		Model = value;
	}

	/*public final ModelStateDictionary getModelState()
	{
		if (_modelStateDictionary == null)
		{
			_modelStateDictionary = new ModelStateDictionary();
		}
		return _modelStateDictionary;
	}
	private void setModelState(ModelStateDictionary value)
	{
		_modelStateDictionary = value;
	}

	public final ValidationHelper getValidation()
	{
		if (_validation == null)
		{
			Debug.assertSymbol(getHttpContext() != null, "HttpContext must be initalized for Validation to work.");
			_validation = new ValidationHelper(getHttpContext(), getModelState());
		}
		return _validation;
	}*/
	/*private void setValidation(ValidationHelper value)
	{
		_validation = value;
	}
*/
	private Consumer<Writer> bodyAction;
	public final Consumer<Writer> getBodyAction()
	{
		return bodyAction;
	}
	public final void setBodyAction(Consumer<Writer> value)
	{
		bodyAction = value;
	}

	public final Stack<Writer> getOutputStack()
	{
		if (_outputStack == null)
		{
			_outputStack = new Stack<Writer>();
		}
		return _outputStack;
	}
	public final void setOutputStack(Stack<Writer> value)
	{
		_outputStack = value;
	}

	private WebPageRenderingBase Page;
	public final WebPageRenderingBase getPage()
	{
		return Page;
	}
	public final void setPage(WebPageRenderingBase value)
	{
		Page = value;
	}

	public final Map<Object, Object> getPageData()
	{
		if (_pageData == null)
		{
			_pageData = new HashMap<Object,Object>();
		}
		return _pageData;
	}
	public final void setPageData(Map<Object, Object> value)
	{
		_pageData = value;
	}

	public final Stack<HashMap<String, SectionWriter>> getSectionWritersStack()
	{
		if (_sectionWritersStack == null)
		{
			_sectionWritersStack = new Stack<HashMap<String, SectionWriter>>();
		}
		return _sectionWritersStack;
	}
	public final void setSectionWritersStack(Stack<HashMap<String, SectionWriter>> value)
	{
		_sectionWritersStack = value;
	}

	// NOTE: We use a hashset because order doesn't matter and we want to eliminate duplicates
	public final HashSet<String> getSourceFiles()
	{
		HashSet<String> sourceFiles = (HashSet<String>)(getHttpContext().getItems().get(_sourceFileKey));
		if (sourceFiles == null)
		{
			sourceFiles = new HashSet<String>();
			getHttpContext().getItems().put(_sourceFileKey,sourceFiles);
		}
		return sourceFiles;
	}

	public static <TModel> WebPageContext createNestedPageContext(
			WebPageContext parentContext,
			Map<Object, Object> pageData,
			TModel model,
			boolean isLayoutPage)
	{
		WebPageContext nestedContext = new WebPageContext();
		nestedContext.setHttpContext(parentContext.getHttpContext());
		nestedContext.setOutputStack(parentContext.getOutputStack());
		//nestedContext.setValidation(parentContext.getValidation());
		nestedContext.setPageData(pageData);
		nestedContext.setModel(model);
		//nestedContext.setModelState(parentContext.getModelState());

		if (isLayoutPage)
		{
			nestedContext.setBodyAction(parentContext.getBodyAction());
			nestedContext.setSectionWritersStack(parentContext.getSectionWritersStack());
		}
		return nestedContext;
	}
}