package com.superstudio.web.razor;

import com.superstudio.codedom.CodeCompileUnit;
import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.CompilerType;
import com.superstudio.commons.EventListener;
import com.superstudio.commons.TextReader;
import com.superstudio.commons.compilation.BuildProvider;
import com.superstudio.commons.exception.HttpException;
import com.superstudio.commons.exception.HttpParseException;
import com.superstudio.web.razor.generator.CodeGenerationCompleteEventArgs;
import com.superstudio.web.razor.parser.syntaxTree.RazorError;

import java.util.ArrayList;
import java.util.List;

public class RazorBuildProvider extends BuildProvider
{
	private static Boolean _isFullTrust = null;
	private CodeCompileUnit _generatedCode;
	private WebPageRazorHost _host;
	private List<String> _virtualPathDependencies;
//	private IAssemblyBuilder _assemblyBuilder;
	public static EventListener<CodeGenerationCompleteEventArgs> CodeGenerationCompleted;


	public static EventListener<?> CodeGenerationStarted;


	public static EventListener<CompilingPathEventArgs> CompilingPath;

	private EventListener<CodeGenerationCompleteEventArgs> codeGenerationCompletedInternal;
	private EventListener<CodeGenerationCompleteEventArgs> codeGenerationStartedInternal;
	
	
	public final WebPageRazorHost getHost() throws HttpException
	{
		if (this._host == null)
		{
			this._host = this.CreateHost();
		}
		return this._host;
	}
	public final void setHost(WebPageRazorHost value)
	{
		this._host = value;
	}
	@Override
	public List<String> getVirtualPathDependencies() throws HttpException
	{
		if (this._virtualPathDependencies != null)
		{
			//return ArrayList.ReadOnly(this._virtualPathDependencies);
			return this._virtualPathDependencies;
		}
		return super.getVirtualPathDependencies();
	}
	public final String getVirtualPath() throws HttpException
	{
		return super.getVirtualPath();
	}
	/*public final AssemblyBuilder getAssemblyBuilder()
	{
		AssemblyBuilderWrapper assemblyBuilderWrapper = (AssemblyBuilderWrapper)((this._assemblyBuilder instanceof AssemblyBuilderWrapper) ? this._assemblyBuilder : null);
		if (assemblyBuilderWrapper != null)
		{
			return assemblyBuilderWrapper.getInnerBuilder();
		}
		return null;
	}
	public final IAssemblyBuilder getAssemblyBuilderInternal()
	{
		return this._assemblyBuilder;
	}
*/	public final CodeCompileUnit getGeneratedCode() throws Exception
	{
		this.EnsureGeneratedCode();
		return this._generatedCode;
	}
	public final void setGeneratedCode(CodeCompileUnit value)
	{
		this._generatedCode = value;
	}
	@Override
	public CompilerType getCodeCompilerType() throws Exception
	{
		this.EnsureGeneratedCode();
		CompilerType defaultCompilerTypeForLanguage = super.getDefaultCompilerTypeForLanguage(this.getHost().getCodeLanguage().getLanguageName());
		if (!RazorBuildProvider._isFullTrust.equals(false) && this.getHost().getDefaultDebugCompilation())
		{
			try
			{
				RazorBuildProvider.SetIncludeDebugInfoFlag(defaultCompilerTypeForLanguage);
				RazorBuildProvider._isFullTrust = new Boolean(true);
			}
			catch (SecurityException e)
			{
				RazorBuildProvider._isFullTrust = new Boolean(false);
			}
		}
		return defaultCompilerTypeForLanguage;
	}
	public final void addVirtualPathDependency(String dependency)
	{
		if (this._virtualPathDependencies == null)
		{
			this._virtualPathDependencies = new ArrayList<String>();
		}
		this._virtualPathDependencies.add(dependency);
	}
	/*@Override
	public java.lang.Class getGeneratedType(CompilerResults results) throws HttpException
	{
		return results.getCompiledAssembly().GetType((StringHelper.format(Locale.CurrentCulture, "%1$s.%2$s", new Object[] {this.getHost().getDefaultNamespace(), this.getHost().getDefaultClassName()})));
	}*/
	/*@Override
	public void generateCode(AssemblyBuilder assemblyBuilder)
	{
		try {
			this.GenerateCodeCore(new AssemblyBuilderWrapper(assemblyBuilder));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
/*	public void GenerateCodeCore(IAssemblyBuilder assemblyBuilder) throws Exception
	{
		this.OnCodeGenerationStarted(assemblyBuilder);
		assemblyBuilder.addCodeCompileUnit(this, this.getGeneratedCode());
		assemblyBuilder.generateTypeFactory(String.format(Locale.InvariantCulture, "%1$s.%2$s", new Object[] {this.getHost().getDefaultNamespace(), this.getHost().getDefaultClassName()}));
	}*/
	protected TextReader InternalOpenReader()
	{
		return super.openReader();
	}
	protected WebPageRazorHost CreateHost() throws HttpException
	{
		WebPageRazorHost hostFromConfig = this.GetHostFromConfig();
		CompilingPathEventArgs compilingPathEventArgs = new CompilingPathEventArgs(this.getVirtualPath(), hostFromConfig);
		this.OnBeforeCompilePath(compilingPathEventArgs);
		return compilingPathEventArgs.getHost();
	}
	protected WebPageRazorHost GetHostFromConfig() throws HttpException
	{
		return WebRazorHostFactory.createHostFromConfig(this.getVirtualPath());
	}
	protected void OnBeforeCompilePath(CompilingPathEventArgs args)
	{
		EventListener<CompilingPathEventArgs> compilingPath = RazorBuildProvider.CompilingPath;
		if (compilingPath != null)
		{
			compilingPath.execute(this, args);
		}
	}
	/*private void OnCodeGenerationStarted(IAssemblyBuilder assemblyBuilder)
	{
		//this._assemblyBuilder = assemblyBuilder;
		EventListener<?> eventHandler = (this.codeGenerationStartedInternal != null) ?
				this.codeGenerationStartedInternal : RazorBuildProvider.CodeGenerationStarted;
		if (eventHandler != null)
		{
			eventHandler.get(this, null);
		}
	}*/
	private void OnCodeGenerationCompleted(CodeCompileUnit generatedCode) throws HttpException
	{
		EventListener<CodeGenerationCompleteEventArgs> eventHandler =
				(this.codeGenerationCompletedInternal != null) ? 
						this.codeGenerationCompletedInternal 
						: RazorBuildProvider.CodeGenerationCompleted;
		if (eventHandler != null)
		{
			eventHandler.execute(this, new CodeGenerationCompleteEventArgs(this.getHost().getVirtualPath(), this.getHost().getPhysicalPath(), generatedCode));
		}
	}
	private void EnsureGeneratedCode() throws Exception
	{
		if (this._generatedCode == null)
		{
			RazorTemplateEngine razorTemplateEngine = new RazorTemplateEngine(this.getHost());
			GeneratorResults generatorResults = null;
			try (TextReader textReader = this.InternalOpenReader())
			{
				RazorTemplateEngine arg_36_0 = razorTemplateEngine;
				String className = null;
				String rootNamespace = null;
				String physicalPath = this.getHost().getPhysicalPath();
				generatorResults = arg_36_0.generateCode(textReader, className, rootNamespace, physicalPath);
			}
			if (!generatorResults.getSuccess())
			{
				throw RazorBuildProvider.CreateExceptionFromParserError(CollectionHelper.lastOrDefault(generatorResults.getParserErrors()), this.getVirtualPath());
			}
			this._generatedCode = generatorResults.getGeneratedCode();
			this.OnCodeGenerationCompleted(this._generatedCode);
		}
	}
	private static HttpParseException CreateExceptionFromParserError(RazorError error, String virtualPath)
	{
		return new HttpParseException(error.getMessage() + System.lineSeparator(), null, virtualPath, null, error.getLocation().getLineIndex() + 1);
	}
	private static void SetIncludeDebugInfoFlag(CompilerType compilerType)
	{
		//TODO compiler later
		//compilerType.getCompilerParameters().setIncludeDebugInformation(true);
	}
}