package com.superstudio.web.razor;

import com.superstudio.codedom.CodeCompileUnit;
import com.superstudio.codedom.CodeMemberMethod;
import com.superstudio.codedom.CodeNamespace;
import com.superstudio.codedom.CodeTypeDeclaration;
import com.superstudio.commons.csharpbridge.action.Func;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.web.razor.generator.CodeGeneratorContext;
import com.superstudio.web.razor.generator.GeneratedClassContext;
import com.superstudio.web.razor.generator.RazorCodeGenerator;
import com.superstudio.web.razor.parser.HtmlMarkupParser;
import com.superstudio.web.razor.parser.ParserBase;

import java.util.ArrayList;
import java.util.List;



/** 
 Defines the environment in which a Razor template will live
 
 
 The host defines the following things:
 * What method names will be used for rendering markup, expressions etc.  For example "write", "WriteLiteral"
 * The namespace imports to be added to every page generated via this host
 * The default Base Class to inherit the generated class from
 * The default Class Name and Namespace for the generated class (can be overridden by parameters in RazorTemplateEngine.GeneratedCode)
 * The language of the code in a Razor page
 * The markup, code parsers and code generators to use (the system will select defaults, but a Host gets a change to augment them)
	 ** See DecorateNNN methods
 * Additional code to add to the generated code (see postProcessGeneratedCode)
 
*/
public class RazorEngineHost
{
	public static final String InternalDefaultClassName = "__CompiledTemplate";
	public static final String InternalDefaultNamespace = "Razor";

	private boolean _instrumentationActive = false;
	private Func<ParserBase> _markupParserFactory;

	private int _tabSize = 4;

	public RazorEngineHost()
	{
		setGeneratedClassContext(GeneratedClassContext.Default);
		setNamespaceImports(new ArrayList<String>());
		setDesignTimeMode(false);
		setDefaultNamespace(InternalDefaultNamespace);
		setDefaultClassName(InternalDefaultClassName);
		setEnableInstrumentation(false);
	}

	/** 
	 Creates a host which uses the specified code language and the HTML markup language
	 
	 @param codeLanguage The code language to use
	 * @throws Exception 
	*/
	public RazorEngineHost(RazorCodeLanguage codeLanguage) throws Exception
	{

		this(codeLanguage, new Func<ParserBase>(){
			@Override
			public ParserBase execute(){
			return new HtmlMarkupParser();
			}
			});
	}

	public RazorEngineHost(RazorCodeLanguage codeLanguage, Func<ParserBase> markupParserFactory) throws Exception
	{
		this();
		if (codeLanguage == null)
		{
			//throw new ArgumentNullException("codeLanguage");
		}
		if (markupParserFactory == null)
		{
			//throw new ArgumentNullException("markupParserFactory");
		}
	//item=	markupParserFactory.execute();
		setCodeLanguage(codeLanguage);
		_markupParserFactory = markupParserFactory;
	}

	/** 
	 Details about the methods and types that should be used to generate code for Razor constructs
	 
	*/
	private GeneratedClassContext privateGeneratedClassContext;
	public GeneratedClassContext getGeneratedClassContext()
	{
		return privateGeneratedClassContext;
	}
	public void setGeneratedClassContext(GeneratedClassContext value)
	{
		privateGeneratedClassContext = value;
	}

	/** 
	 A list of namespaces to import in the generated file
	 
	*/
	private List<String> privateNamespaceImports;
	public List<String> getNamespaceImports()
	{
		return privateNamespaceImports;
	}
	private void setNamespaceImports(List<String> value)
	{
		privateNamespaceImports = value;
	}

	/** 
	 The base-class of the generated class
	 
	*/
	private String privateDefaultBaseClass;
	public String getDefaultBaseClass()
	{
		return privateDefaultBaseClass;
	}
	public void setDefaultBaseClass(String value)
	{
		privateDefaultBaseClass = value;
	}

	/** 
	 Indiciates if the parser and code generator should run in design-time mode
	 
	*/
	private boolean privateDesignTimeMode;
	public boolean getDesignTimeMode()
	{
		return privateDesignTimeMode;
	}
	public void setDesignTimeMode(boolean value)
	{
		privateDesignTimeMode = value;
	}

	/** 
	 The name of the generated class
	 
	*/
	private String privateDefaultClassName;
	public String getDefaultClassName()
	{
		return privateDefaultClassName;
	}
	public void setDefaultClassName(String value)
	{
		privateDefaultClassName = value;
	}

	/** 
	 The namespace which will contain the generated class
	 
	*/
	private String privateDefaultNamespace;
	public String getDefaultNamespace()
	{
		return privateDefaultNamespace;
	}
	public void setDefaultNamespace(String value)
	{
		privateDefaultNamespace = value;
	}

	/** 
	 Boolean indicating if helper methods should be instance methods or static methods
	 
	*/
	private boolean privateStaticHelpers;
	public boolean getStaticHelpers()
	{
		return privateStaticHelpers;
	}
	public void setStaticHelpers(boolean value)
	{
		privateStaticHelpers = value;
	}

	/** 
	 The language of the code within the Razor template.
	 
	*/
	private RazorCodeLanguage privateCodeLanguage;
	public RazorCodeLanguage getCodeLanguage()
	{
		return privateCodeLanguage;
	}
	protected void setCodeLanguage(RazorCodeLanguage value)
	{
		privateCodeLanguage = value;
	}

	/** 
	 Boolean indicating if instrumentation code should be injected into the output page
	 
	*/
		// Always disable instrumentation in DesignTimeMode.
	public boolean getEnableInstrumentation()
	{
		return !getDesignTimeMode() && _instrumentationActive;
	}
	public void setEnableInstrumentation(boolean value)
	{
		_instrumentationActive = value;
	}

	/** 
	 Gets or sets whether the design time editor is using tabs or spaces for indentation.
	 
	*/
	private boolean privateIsIndentingWithTabs;
	public boolean getIsIndentingWithTabs()
	{
		return privateIsIndentingWithTabs;
	}
	public void setIsIndentingWithTabs(boolean value)
	{
		privateIsIndentingWithTabs = value;
	}

	/** 
	 Tab size used by the hosting editor, when indenting with tabs.
	 
	*/
	public int getTabSize()
	{
		return _tabSize;
	}

	public void setTabSize(int value)
	{
		_tabSize = Math.max(value, 1);
	}

	/** 
	 Gets or sets the path to use for this document when generating Instrumentation calls
	 
	*/
	private String privateInstrumentedSourceFilePath;
	public String getInstrumentedSourceFilePath()
	{
		return privateInstrumentedSourceFilePath;
	}
	public void setInstrumentedSourceFilePath(String value)
	{
		privateInstrumentedSourceFilePath = value;
	}

	/** 
	 Constructs the markup parser.  Must return a new instance on EVERY call to ensure thread-safety
	 
	*/
	public ParserBase createMarkupParser()
	{
		if (_markupParserFactory != null)
		{
			return _markupParserFactory.execute();
		}
		return null;
	}

	/** 
	 Gets an instance of the code parser and is provided an opportunity to decorate or replace it
	 
	 @param incomingCodeParser The code parser
	 @return Either the same code parser, after modifications, or a different code parser
	 * @throws Exception 
	*/
	public ParserBase decorateCodeParser(ParserBase incomingCodeParser) throws Exception
	{
		if (incomingCodeParser == null)
		{
			throw new ArgumentNullException("incomingCodeParser");
		}
		return incomingCodeParser;
	}

	/** 
	 Gets an instance of the markup parser and is provided an opportunity to decorate or replace it
	 
	 @param incomingMarkupParser The markup parser
	 @return Either the same markup parser, after modifications, or a different markup parser
	 * @throws Exception 
	*/
	public ParserBase decorateMarkupParser(ParserBase incomingMarkupParser) throws Exception
	{
		if (incomingMarkupParser == null)
		{
			throw new ArgumentNullException("incomingMarkupParser");
		}
		return incomingMarkupParser;
	}

	/** 
	 Gets an instance of the code generator and is provided an opportunity to decorate or replace it
	 
	 @param incomingCodeGenerator The code generator
	 @return Either the same code generator, after modifications, or a different code generator
	 * @throws Exception 
	*/
	public RazorCodeGenerator decorateCodeGenerator(RazorCodeGenerator incomingCodeGenerator) throws Exception
	{
		if (incomingCodeGenerator == null)
		{
			throw new ArgumentNullException("incomingCodeGenerator");
		}
		return incomingCodeGenerator;
	}

	/** 
	 Gets the important CodeDOM nodes generated by the code generator and has a chance to add to them.
	 
	 
	 all the other parameter values can be located by traversing tree in the codeCompileUnit node, they
	 are simply provided for convenience
	 
	 @param context The current <see cref="CodeGeneratorContext"/>.
	*/
	public void postProcessGeneratedCode(CodeGeneratorContext context)
	{

		postProcessGeneratedCode(context.getCompileUnit(), context.getNamespace(), context.getGeneratedClass(), context.getTargetMethod());

	}

	
	private void postProcessGeneratedCode(CodeCompileUnit codeCompileUnit, CodeNamespace generatedNamespace, CodeTypeDeclaration generatedClass, CodeMemberMethod executeMethod)
	{
		if (codeCompileUnit == null)
		{
			//throw new ArgumentNullException("codeCompileUnit");
		}
		if (generatedNamespace == null)
		{
			//throw new ArgumentNullException("generatedNamespace");
		}
		if (generatedClass == null)
		{
			//throw new ArgumentNullException("generatedClass");
		}
		if (executeMethod == null)
		{
			//throw new ArgumentNullException("executeMethod");
		}
	}
}