package com.superstudio.template.mvc;

import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.template.mvc.actionresult.TemplateDataDictionary;
import com.superstudio.template.mvc.context.HostContext;
import com.superstudio.template.mvc.context.HttpContextWrapper;
import com.superstudio.template.mvc.context.RenderContext;
import com.superstudio.template.templatepages.FormContext;
import com.superstudio.template.templatepages.ScopeStorage;

import java.io.Writer;
import java.util.Map;
import java.util.function.Supplier;

public class TemplateContext extends RenderContext {

	/*
	 * private final String ClientValidationScript = "<script type="
	 * "text/javascript""> //<![CDATA[ if (!window.mvcClientValidationMetadata)
	 * {{ window.mvcClientValidationMetadata = []; }}
	 * window.mvcClientValidationMetadata.push({0}); //]]> </script>";
	 */
	static final String ClientValidationKeyName = "ClientValidationEnabled";
	static final String UnobtrusiveJavaScriptKeyName = "UnobtrusiveJavaScriptEnabled";
	static final String ValidationSummaryMessageElementKeyName = "ValidationSummaryMessageElement";
	static final String ValidationMessageElementKeyName = "ValidationMessageElement";

	// Some values have to be stored in HttpContext.Items in order to be
	// propagated between calls
	// to RenderPartial(), RenderAction(), etc.
	private static final Object _formContextKey = new Object();
	private static final Object _lastFormNumKey = new Object();

	private Supplier<Map<Object, Object>> _scopeThunk;
	private Map<Object, Object> _transientScope;

	private TemplateDataDictionary _dynamicTemplateDataDictionary;
	private Supplier<String> _formIdGenerator;

	// We need a default FormContext if the user uses html <form> instead of an
	// MvcForm
	private FormContext _defaultFormContext = new FormContext();

	// parameterless constructor used for mocking
	public TemplateContext() {
	}

	public TemplateContext(RenderContext renderContext, ITemplate template, TemplateDataDictionary templateData,
						   Map<String, Object> tempData, Writer writer) throws ArgumentNullException

	{

		super(renderContext);
		if (renderContext == null) {
			throw new ArgumentNullException("renderContext");
		}
		if (template == null) {
			throw new ArgumentNullException("template");
		}
		if (templateData == null) {
			throw new ArgumentNullException("templateData");
		}
		if (tempData == null) {
			throw new ArgumentNullException("tempData");
		}
		if (writer == null) {
			throw new ArgumentNullException("writer");
		}

		this.setTemplate(template);
		this.setTemplateData(templateData);
		this.setWriter(writer);
		this.setTempData(tempData);
	}

	private boolean clientValidationEnabled;

	/*
	 * { get { return GetClientValidationEnabled(Scope, HttpContext); } set {
	 * SetClientValidationEnabled(value, Scope, HttpContext); } }
	 */
	private FormContext formContext;

	/*
	 * { get { // Never return a null form context, this is important for
	 * validation purposes return HttpContext.Items[_formContextKey] as
	 * FormContext ?? _defaultFormContext; } set {
	 * HttpContext.Items[_formContextKey] = value; } }
	 */
	private Supplier<String> formIdGenerator;

	/*
	 * { get { if (_formIdGenerator == null) { _formIdGenerator =
	 * DefaultFormIdGenerator; } return _formIdGenerator; } set {
	 * _formIdGenerator = value; } }
	 */

	static Supplier<Map<Object, Object>> GlobalScopeThunk;

	private Map<Object, Object> scope;

	/*
	 * { get { if (ScopeThunk != null) { return ScopeThunk(); } if
	 * (_transientScope == null) { _transientScope = new Dictionary<object,
	 * object>(); } return _transientScope; } }
	 */

	Supplier<Map<Object, Object>> scopeThunk;

	/*
	 * { get { return _scopeThunk ?? GlobalScopeThunk; } set { _scopeThunk =
	 * value; } }
	 */
	private Map<String, Object> tempData;

	public boolean unobtrusiveJavaScriptEnabled;

	/*
	 * { get { return GetUnobtrusiveJavaScriptEnabled(Scope, HttpContext); } set
	 * { SetUnobtrusiveJavaScriptEnabled(value, Scope, HttpContext); } }
	 */

	/// <summary>
	/// Element name used to wrap a top-level message generated by
	/// <see cref="ValidationExtensions.ValidationSummary(HtmlHelper)"/> and
	/// other overloads.
	/// </summary>
	/*
	 * public virtual String ValidationSummaryMessageElement
	 * 
	 * { get { return GetValidationSummaryMessageElement(Scope, HttpContext); }
	 * set { if (String.IsNullOrEmpty(value)) { throw
	 * Error.ParameterCannotBeNullOrEmpty("value"); }
	 * 
	 * SetValidationSummaryMessageElement(value, Scope, HttpContext); } }
	 */

	/// <summary>
	/// Element name used to wrap a top-level message generated by
	/// <see cref="ValidationExtensions.ValidationMessage(HtmlHelper, String)"/>
	/// and other overloads.
	/// </summary>
	/*
	 * public virtual String ValidationMessageElement
	 * 
	 * { get { return GetValidationMessageElement(Scope, HttpContext); } set {
	 * if (String.IsNullOrEmpty(value)) { throw
	 * Error.ParameterCannotBeNullOrEmpty("value"); }
	 * 
	 * SetValidationMessageElement(value, Scope, HttpContext); } }
	 */
	private ITemplate template;

	public Map<String, Object> TemplateBag;

	/*
	 * { get { if (_dynamicTemplateDataDictionary == null) {
	 * _dynamicTemplateDataDictionary = new DynamicTemplateDataDictionary(() =>
	 * TemplateData); } return _dynamicTemplateDataDictionary; } }
	 */

	private TemplateDataDictionary templateData;

	private Writer writer;

	/*private String DefaultFormIdGenerator() {
		int formNum = IncrementFormCount(httpContext.getItems());
		return StringHelper.format(CultureInfo.InvariantCulture, "form{0}", formNum);
	}

	static boolean GetClientValidationEnabled(Map<Object, Object> scope, HttpContextBase httpContext) {
		return ScopeCache.Get(scope, httpContext).ClientValidationEnabled;
	}

	FormContext GetFormContextForClientValidation() {
		return (ClientValidationEnabled) ? FormContext : null;
	}

	static boolean GetUnobtrusiveJavaScriptEnabled(Map<Object,Object> scope = null, HttpContextBase httpContext = null)
			        {
			            return ScopeCache.Get(scope, httpContext).UnobtrusiveJavaScriptEnabled;
			        }

	static String GetValidationSummaryMessageElement(Map<Object, Object> scope, HttpContextBase httpContext) {
		return ScopeCache.Get(scope, httpContext).ValidationSummaryMessageElement;
	}

	static String GetValidationMessageElement(Map<Object, Object> scope, HttpContextBase httpContext) {
		return ScopeCache.Get(scope, httpContext).ValidationMessageElement;
	}

	private static int IncrementFormCount(Map items) {
		Object lastFormNum = items.get(_lastFormNumKey);
		int newFormNum = (lastFormNum != null) ? ((int) lastFormNum) + 1 : 0;
		items.put(_lastFormNumKey, newFormNum);
		return newFormNum;
	}*/

	/*public void OutputClientValidation() {
		FormContext formContext = GetFormContextForClientValidation();
		
		 * if (formContext == null || UnobtrusiveJavaScriptEnabled) { return; //
		 * do nothing }
		 

		// String scriptWithCorrectNewLines =
		// ClientValidationScript.Replace("\r\n", Environment.NewLine);
		// String validationJson = formContext.getJsonValidationMetadata();
		// String formatted = StringHelper.format(CultureInfo.InvariantCulture,
		// scriptWithCorrectNewLines, validationJson);

		// getWriter().write(formatted);
	}*/

	/*
	 * static void SetClientValidationEnabled(boolean enabled, Map<Object,
	 * Object> scope, HttpContextBase httpContext) { ScopeCache.Get(scope,
	 * httpContext).setClientValidationEnabled (enabled); }
	 * 
	 * static void SetUnobtrusiveJavaScriptEnabled(boolean enabled, Map<Object,
	 * Object> scope, HttpContextBase httpContext) { ScopeCache.Get(scope,
	 * httpContext).setUnobtrusiveJavaScriptEnabled (enabled); }
	 * 
	 * static void SetValidationSummaryMessageElement(String elementName,
	 * Map<Object, Object> scope, HttpContextBase httpContext) {
	 * ScopeCache.Get(scope,
	 * httpContext).setValidationSummaryMessageElement(elementName); }
	 * 
	 * static void SetValidationMessageElement(String elementName, Map<Object,
	 * Object> scope, HttpContextBase httpContext) { ScopeCache.Get(scope,
	 * httpContext).setValidationMessageElement( elementName); }
	 */

	private static <TValue> TValue scopeGet(Map<Object, Object> scope, String name, TValue defaultValue) {
		Object result;
		if (scope.get(name) != null)

		{
			return (TValue) scope.get(name);
			// return (TValue) Convert.ChangeType(result, typeof(TValue),
			// CultureInfo.InvariantCulture);
		}
		return defaultValue;
	}

	//@Override
	public Writer getWriter() {
		return writer;
	}

	public void setWriter(Writer writer) {
		this.writer = writer;
	}

	public TemplateDataDictionary getTemplateData() {
		return templateData;
	}

	public void setTemplateData(TemplateDataDictionary templateData) {
		this.templateData = templateData;
	}

	public ITemplate getTemplate() {
		return template;
	}

	public void setTemplate(ITemplate template) {
		this.template = template;
	}

	public Map<String, Object> getTempData() {
		return tempData;
	}

	public void setTempData(Map<String, Object> tempData) {
		this.tempData = tempData;
	}

	public Map<Object, Object> getScope() {
		return scope;
	}

	public void setScope(Map<Object, Object> scope) {
		this.scope = scope;
	}

	public FormContext getFormContext() {
		return formContext;
	}

	public void setFormContext(FormContext formContext) {
		this.formContext = formContext;
	}

	private static final class ScopeCache {
		private static final Object _cacheKey = new Object();
		private boolean _clientValidationEnabled;
		private Map<Object, Object> _scope;
		private boolean _unobtrusiveJavaScriptEnabled;
		private String _validationSummaryMessageElement;
		private String _validationMessageElement;

		private ScopeCache(Map<Object, Object> scope) {
			_scope = scope;

			_clientValidationEnabled = scopeGet(scope, ClientValidationKeyName, false);
			_unobtrusiveJavaScriptEnabled = scopeGet(scope, UnobtrusiveJavaScriptKeyName, false);
			_validationSummaryMessageElement = scopeGet(scope, ValidationSummaryMessageElementKeyName, "span");
			_validationMessageElement = scopeGet(scope, ValidationMessageElementKeyName, "span");
		}

		/*
		 * public boolean ClientValidationEnabled
		 * 
		 * { get { return _clientValidationEnabled; } set {
		 * _clientValidationEnabled = value; _scope[ClientValidationKeyName] =
		 * value; } }
		 */

		/*
		 * public boolean UnobtrusiveJavaScriptEnabled
		 * 
		 * { get{return
		 * _unobtrusiveJavaScriptEnabled;}set{_unobtrusiveJavaScriptEnabled=
		 * value; _scope[UnobtrusiveJavaScriptKeyName]=value;} }
		 * 
		 * public String ValidationSummaryMessageElement
		 * 
		 * { get{return _validationSummaryMessageElement;}set{
		 * _validationSummaryMessageElement=
		 * value;_scope[ValidationSummaryMessageElementKeyName]=value;} }
		 * 
		 * public String ValidationMessageElement
		 * 
		 * { get{return
		 * _validationMessageElement;}set{_validationMessageElement=value;
		 * _scope[ ValidationMessageElementKeyName]=value;} }
		 */

		public static ScopeCache Get(Map<Object, Object> scope, HostContext httpContext) throws Exception {
			if (httpContext == null && HostContext.getCurrent() != null) {
				httpContext = new HttpContextWrapper(HostContext.getCurrent());
			}

			ScopeCache result = null;
			scope = scope == null ? ScopeStorage.getCurrentScope() : scope;

			if (httpContext != null) {
				result = (ScopeCache) httpContext.getItems().get(_cacheKey);
			}

			if (result == null || result._scope != scope) {
				result = new ScopeCache(scope);

				if (httpContext != null) {
					httpContext.getItems().put(_cacheKey, result);
				}
			}

			return result;
		}
	}
}
