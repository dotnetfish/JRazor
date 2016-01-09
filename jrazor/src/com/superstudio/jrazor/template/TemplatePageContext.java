package com.superstudio.jrazor.template;

import java.io.Writer;
import java.util.*;

import com.superstudio.commons.csharpbridge.action.ActionOne;
import com.superstudio.jrazor.SectionWriter;
import com.superstudio.jrazor.TemplatePageRenderingBase;
import com.superstudio.jrazor.TemplateStack;


// Class for containing various pieces of data required by a WebPage
public class TemplatePageContext
{
	private static final Object _sourceFileKey = new Object();
	private Stack<Writer> _outputStack;
	private Stack<HashMap<String, SectionWriter>> _sectionWritersStack;
	private Map<Object, Object> _pageData;
	//private ValidationHelper _validation;
	//private ModelStateDictionary _modelStateDictionary;

	public TemplatePageContext()
	{
		this(null, null, null);
	}

	public TemplatePageContext(TemplateHostContext context, TemplatePageRenderingBase page, Object model)
	{
		setContext(context);
		setPage(page);
		setModel(model);
	}

	public  TemplatePageContext getCurrent() throws Exception
	{
			// The TemplateStack stores instances of WebPageRenderingBase. 
			// Retrieve the top-most item from the stack and cast it to WebPageBase. 

		
		if (getContext() != null)
		{
			//HttpContextWrapper contextWrapper = new HttpContextWrapper(httpContext);

			ITemplateFile currentTemplate = TemplateStack.GetCurrentTemplate(getContext());
			TemplatePageRenderingBase currentPage = ((TemplatePageRenderingBase)((currentTemplate instanceof TemplatePageRenderingBase) ? currentTemplate : null));

			return (currentPage == null) ? null : currentPage.getPageContext();
		}
		return null;
	}

	private TemplateHostContext context;
	public final TemplateHostContext getContext()
	{
		return context;
	}
	public final void setContext(TemplateHostContext value)
	{
		context = value;
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
			Debug.Assert(getHttpContext() != null, "HttpContext must be initalized for Validation to work.");
			_validation = new ValidationHelper(getHttpContext(), getModelState());
		}
		return _validation;
	}*/
	/*private void setValidation(ValidationHelper value)
	{
		_validation = value;
	}
*/
	private ActionOne<Writer> bodyAction;
	public final ActionOne<Writer> getBodyAction()
	{
		return bodyAction;
	}
	public final void setBodyAction(ActionOne<Writer> value)
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

	private TemplatePageRenderingBase Page;
	public final TemplatePageRenderingBase getPage()
	{
		return Page;
	}
	public final void setPage(TemplatePageRenderingBase value)
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
		HashSet<String> sourceFiles = (HashSet<String>)(getContext().getItems().get(_sourceFileKey));
		if (sourceFiles == null)
		{
			sourceFiles = new HashSet<String>();
			getContext().getItems().put(_sourceFileKey,sourceFiles);
		}
		return sourceFiles;
	}

	public static <TModel> TemplatePageContext CreateNestedPageContext(TemplatePageContext parentContext, Map<Object, Object> pageData, TModel model, boolean isLayoutPage)
	{
		TemplatePageContext nestedContext = new TemplatePageContext();
		nestedContext.setContext(parentContext.getContext());
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