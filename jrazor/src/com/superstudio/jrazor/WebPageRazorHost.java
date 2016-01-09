package com.superstudio.jrazor;

import java.util.*;

import com.superstudio.codedom.CodeNamespaceImport;
import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.HostingEnvironment;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.commons.io.Path;
import com.superstudio.jrazor.generator.CodeGeneratorContext;
import com.superstudio.jrazor.generator.GeneratedClassContext;
import com.superstudio.jrazor.parser.HtmlMarkupParser;
import com.superstudio.jrazor.parser.ParserBase;
import com.superstudio.jrazor.parser.ParserHelpers;

public class WebPageRazorHost extends RazorEngineHost {
	public static final String PageClassNamePrefix = "_Page_";
	public static final String ApplicationInstancePropertyName = "ApplicationInstance";
	public static final String ContextPropertyName = "Context";
	public static final String DefineSectionMethodName = "DefineSection";
	public static final String WebDefaultNamespace = "ASP";
	public static final String WriteToMethodName = "WriteTo";
	public static final String WriteLiteralToMethodName = "WriteLiteralTo";
	public static final String BeginContextMethodName = "BeginContext";
	public static final String EndContextMethodName = "EndContext";
	public static final String ResolveUrlMethodName = "Href";
	private static final String ApplicationStartFileName = "_AppStart";
	private static final String PageStartFileName = "_PageStart";
	public static final String FallbackApplicationTypeName = "HttpApplication"; // HttpApplication.class.FullName;
	public static final String PageBaseClass = "WebPage";// WebPage.class.getName();
	public static final String TemplateTypeName = "HelperResult";// HelperResult.class.FullName;
	private static java.util.concurrent.ConcurrentHashMap<String, Object> _importedNamespaces = new java.util.concurrent.ConcurrentHashMap<String, Object>();
	private final HashMap<String, String> _specialFileBaseTypes = new HashMap<String, String>();
	private String _className;
	private RazorCodeLanguage _codeLanguage;
	private String _globalAsaxTypeName;
	private Boolean _isSpecialPage = null;
	private String _physicalPath;
	private String _specialFileBaseClass;

	public RazorCodeLanguage getCodeLanguage() {
		if (this._codeLanguage == null) {
			this._codeLanguage = this.getNewCodeLanguage();
		}
		return this._codeLanguage;
	}

	protected void setCodeLanguage(RazorCodeLanguage value) {
		this._codeLanguage = value;
	}

	public String getDefaultBaseClass() {
		if (super.getDefaultBaseClass() != null) {
			return super.getDefaultBaseClass();
		}
		if (this.getIsSpecialPage()) {
			return this.getSpecialPageBaseClass();
		}
		return this.getDefaultPageBaseClass();
	}

	public void setDefaultBaseClass(String value) {
		super.setDefaultBaseClass(value);
	}

	public String getDefaultClassName() {
		if (this._className == null) {
			this._className = this.getClassName(this.getVirtualPath());
		}
		return this._className;
	}

	public void setDefaultClassName(String value) {
		this._className = value;
	}

	private boolean defaultDebugCompilation;

	public final boolean getDefaultDebugCompilation() {
		return defaultDebugCompilation;
	}

	public final void setDefaultDebugCompilation(boolean value) {
		defaultDebugCompilation = value;
	}

	private String defaultPageBaseClass;

	public final String getDefaultPageBaseClass() {
		return defaultPageBaseClass;
	}

	public final void setDefaultPageBaseClass(String value) {
		defaultPageBaseClass = value;
	}

	public final String getGlobalAsaxTypeName() {
		String arg_21_0;
		if ((arg_21_0 = this._globalAsaxTypeName) == null) {
			if (!HostingEnvironment.getIsHosted()) {
				return WebPageRazorHost.FallbackApplicationTypeName;
			}
			// arg_21_0 = BuildManager.GetGlobalAsaxType().FullName;
			arg_21_0 = "BuildManagerGlobalAsaxType";
		}
		return arg_21_0;
	}

	public final void setGlobalAsaxTypeName(String value) {
		this._globalAsaxTypeName = value;
	}

	public final boolean getIsSpecialPage() {
		this.checkForSpecialPage();
		return this._isSpecialPage.booleanValue();
	}

	public final String getPhysicalPath() {
		this.mapPhysicalPath();
		return this._physicalPath;
	}

	public final void setPhysicalPath(String value) {
		this._physicalPath = value;
	}

	// @Override
	public String getInstrumentedSourceFilePath() {
		return this.getVirtualPath();
	}

	// @Override
	public void setInstrumentedSourceFilePath(String value) {
		this.setVirtualPath(value);
	}

	private String getSpecialPageBaseClass() {
		this.checkForSpecialPage();
		return this._specialFileBaseClass;
	}

	private String virtualPath;

	public final String getVirtualPath() {
		return virtualPath;
	}

	private void setVirtualPath(String value) {
		virtualPath = value;
	}

	private WebPageRazorHost() {
		//TODo change to what it really need
		
		this.setDefaultNamespace("JRazor");
		this.setGeneratedClassContext(
				new GeneratedClassContext(
						GeneratedClassContext.DefaultExecuteMethodName,
				GeneratedClassContext.DefaultWriteMethodName, GeneratedClassContext.DefaultWriteLiteralMethodName,
				"WriteTo", "WriteLiteralTo", WebPageRazorHost.TemplateTypeName, "DefineSection", "BeginContext",
				"EndContext"));
		this.getGeneratedClassContext().setResolveUrlMethodName("Href");
		this.setDefaultPageBaseClass(WebPageRazorHost.PageBaseClass);
		this.setDefaultDebugCompilation(true);
		this.setEnableInstrumentation(false);
	}

	public WebPageRazorHost(String virtualPath) {
		this(virtualPath, null);
	}

	public WebPageRazorHost(String virtualPath, String physicalPath) {
		this();
		if (StringHelper.isNullOrEmpty(virtualPath)) {
			 throw new IllegalArgumentException(StringHelper.format(CultureInfo.CurrentCulture,
			 CommonResources.Argument_Cannot_Be_Null_Or_Empty, new Object[]
			 {"virtualPath"}));
		}
		this.setVirtualPath(virtualPath);
		this.setPhysicalPath(physicalPath);
		this.setDefaultClassName(this.getClassName(this.getVirtualPath()));
		this.setCodeLanguage(this.getCodeLanguage());
		//TODO check value;
		this.setEnableInstrumentation(true);
		//this.setEnableInstrumentation((new InstrumentationService()).getIsAvailable());
	}

	public static void addGlobalImport(String ns) {
		if (StringHelper.isNullOrEmpty(ns)) {
			 //throw new IllegalArgumentException(String.format(CultureInfo.CurrentCulture,
			// CommonResources.Argument_Cannot_Be_Null_Or_Empty, new Object[]
			// {"ns"}), "ns");
		}
			WebPageRazorHost._importedNamespaces.put(ns, null);// (ns, null);
	}

	private void checkForSpecialPage() {
		if (this._isSpecialPage == null) {
			String fileNameWithoutExtension = Path.GetFileNameWithoutExtension(this.getVirtualPath());
			String specialFileBaseClass = null;
			if (this._specialFileBaseTypes.containsKey(fileNameWithoutExtension)
					? (specialFileBaseClass = this._specialFileBaseTypes.get(fileNameWithoutExtension))
							.equals(specialFileBaseClass)
					: false) {
				this._isSpecialPage = new Boolean(true);
				this._specialFileBaseClass = specialFileBaseClass;
				return;
			}
			this._isSpecialPage = new Boolean(false);
		}
	}

	// @Override
	public ParserBase createMarkupParser() {
		return new HtmlMarkupParser();
	}

	private static RazorCodeLanguage determineCodeLanguage(String fileName) {
		String text = Path.GetExtension(fileName);
		if (StringHelper.isNullOrEmpty(text)) {
			return null;
		}
		if (text.charAt(0) == '.') {
			text = text.substring(1);
		}
		return WebPageRazorHost.getLanguageByExtension(text);
	}

	protected String getClassName(String virtualPath) {
		return ParserHelpers
				.sanitizeClassName("_Page_" + StringHelper.trimStart(virtualPath, '~', '/'));
	}

	protected RazorCodeLanguage getNewCodeLanguage() {
		RazorCodeLanguage razorCodeLanguage = WebPageRazorHost.determineCodeLanguage(this.getVirtualPath());
		if (razorCodeLanguage == null && !StringHelper.isNullOrEmpty(this.getPhysicalPath())) {
			razorCodeLanguage = WebPageRazorHost.determineCodeLanguage(this.getPhysicalPath());
		}
		if (razorCodeLanguage == null) {
			throw new IllegalStateException(StringHelper.format(CultureInfo.CurrentCulture,
					RazorWebResources.BuildProvider_No_CodeLanguageService_For_Path,
					new Object[] { this.getVirtualPath() }));
		}
		return razorCodeLanguage;
	}

	public static Iterable<String> getGlobalImports() {
		 
		// queries:
		return _importedNamespaces.keySet();
	}

	private static RazorCodeLanguage getLanguageByExtension(String extension) {
		return RazorCodeLanguage.getLanguageByExtension(extension);
	}

	private void mapPhysicalPath() {
		if (this._physicalPath == null && HostingEnvironment.getIsHosted()) {
			String text = HostingEnvironment.MapPath(this.getVirtualPath());
			if (!StringHelper.isNullOrEmpty(text) && (new java.io.File(text)).isFile()) {
				this._physicalPath = text;
			}
		}
	}

	@Override
	public void postProcessGeneratedCode(CodeGeneratorContext context) throws ArgumentNullException {
		super.postProcessGeneratedCode(context);
		
		List<CodeNamespaceImport> imports = CollectionHelper.select(WebPageRazorHost.getGlobalImports(),
				(s) -> new CodeNamespaceImport(s));
		CodeNamespaceImport[] list=new CodeNamespaceImport[imports.size()];
		context.getNamespace().getImports().AddRange(imports.toArray(list));
		/*CodeMemberProperty codeMemberProperty = new CodeMemberProperty();
		codeMemberProperty.setName("ApplicationInstance");
		codeMemberProperty.setType(new CodeTypeReference(this.getGlobalAsaxTypeName()));
		codeMemberProperty.setHasGet(true);
		codeMemberProperty.setHasSet(false);
		codeMemberProperty.setAttributes(MemberAttributes.forValue(12290));
		codeMemberProperty.getGetStatements()
				.Add(new CodeMethodReturnStatement(new CodeCastExpression(
						new CodeTypeReference(this.getGlobalAsaxTypeName()), new CodePropertyReferenceExpression(
								new CodePropertyReferenceExpression(null, "Context"), "ApplicationInstance"))));
		context.getGeneratedClass().getMembers().Insert(0, codeMemberProperty);*/
	}

	protected final void registerSpecialFile(String fileName, java.lang.Class baseType) {
		if (baseType == null) {
			throw new IllegalArgumentException("baseType");
		}
		this.registerSpecialFile(fileName, baseType.getName());
	}

	protected final void registerSpecialFile(String fileName, String baseTypeName) {
		if (StringHelper.isNullOrEmpty(fileName)) {
			// throw new
			// IllegalArgumentException(StringHelper.format(CultureInfo.CurrentCulture,
			// CommonResources.Argument_Cannot_Be_Null_Or_Empty, new Object[]
			// {"fileName"}), "fileName");
		}
		if (StringHelper.isNullOrEmpty(baseTypeName)) {
			// throw new
			// IllegalArgumentException(StringHelper.format(CultureInfo.CurrentCulture,
			// CommonResources.Argument_Cannot_Be_Null_Or_Empty, new Object[]
			// {"baseTypeName"}), "baseTypeName");
		}
		this._specialFileBaseTypes.put(fileName, baseTypeName);
	}
}