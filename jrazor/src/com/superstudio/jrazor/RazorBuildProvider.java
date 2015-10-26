package com.superstudio.jrazor;

import java.util.*;

import com.superstudio.codedom.CodeCompileUnit;
import com.superstudio.codedom.compiler.CompilerResults;
import com.superstudio.commons.AssemblyBuilder;
import com.superstudio.commons.AssemblyBuilderWrapper;
import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.CompilerType;
import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.EventHandler;
import com.superstudio.commons.IAssemblyBuilder;
import com.superstudio.commons.TextReader;
import com.superstudio.commons.compilation.BuildProvider;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.exception.HttpException;
import com.superstudio.commons.exception.HttpParseException;
import com.superstudio.jrazor.generator.CodeGenerationCompleteEventArgs;
import com.superstudio.jrazor.parser.syntaxTree.RazorError;

public class RazorBuildProvider extends BuildProvider
{
	private static Boolean isFullTrust = null;
	private CodeCompileUnit generatedCode;
	private WebPageRazorHost host;
	private List<String> virtualPathDependencies;
	private IAssemblyBuilder assemblyBuilder;
	public static  EventHandler<CodeGenerationCompleteEventArgs> CodeGenerationCompleted;


	public static  EventHandler<?> CodeGenerationStarted;


	public static  EventHandler<CompilingPathEventArgs> CompilingPath;
	private  EventHandler<CodeGenerationCompleteEventArgs> codeGenerationCompletedInternal;
	private  EventHandler<CodeGenerationCompleteEventArgs> codeGenerationStartedInternal;
	
	
	public final WebPageRazorHost getHost() throws HttpException
	{
		if (this.host == null)
		{
			this.host = this.createHost();
		}
		return this.host;
	}
	public final void setHost(WebPageRazorHost value)
	{
		this.host = value;
	}
	@Override
	public List<String> getVirtualPathDependencies() throws HttpException
	{
		if (this.virtualPathDependencies != null)
		{
			//return ArrayList.ReadOnly(this._virtualPathDependencies);
			return this.virtualPathDependencies;
		}
		return super.getVirtualPathDependencies();
	}
	public final String getVirtualPath() throws HttpException
	{
		return super.getVirtualPath();
	}
	public final AssemblyBuilder getAssemblyBuilder()
	{
		AssemblyBuilderWrapper assemblyBuilderWrapper = (AssemblyBuilderWrapper)((this.assemblyBuilder instanceof AssemblyBuilderWrapper) ? this.assemblyBuilder : null);
		if (assemblyBuilderWrapper != null)
		{
			return assemblyBuilderWrapper.getInnerBuilder();
		}
		return null;
	}
	public final IAssemblyBuilder getAssemblyBuilderInternal()
	{
		return this.assemblyBuilder;
	}
	public final CodeCompileUnit getGeneratedCode() throws HttpException, Exception
	{
		this.ensureGeneratedCode();
		return this.generatedCode;
	}
	public final void setGeneratedCode(CodeCompileUnit value)
	{
		this.generatedCode = value;
	}
	@Override
	public CompilerType getCodeCompilerType() throws Exception
	{
		this.ensureGeneratedCode();
		CompilerType defaultCompilerTypeForLanguage = super.GetDefaultCompilerTypeForLanguage(this.getHost().getCodeLanguage().getLanguageName());
		if (!RazorBuildProvider.isFullTrust.equals(false) && this.getHost().getDefaultDebugCompilation())
		{
			try
			{
				RazorBuildProvider.setIncludeDebugInfoFlag(defaultCompilerTypeForLanguage);
				RazorBuildProvider.isFullTrust = new Boolean(true);
			}
			catch (SecurityException e)
			{
				RazorBuildProvider.isFullTrust = new Boolean(false);
			}
		}
		return defaultCompilerTypeForLanguage;
	}
	public final void addVirtualPathDependency(String dependency)
	{
		if (this.virtualPathDependencies == null)
		{
			this.virtualPathDependencies = new ArrayList<String>();
		}
		this.virtualPathDependencies.add(dependency);
	}
	@Override
	public java.lang.Class getGeneratedType(CompilerResults results) throws HttpException
	{
		return results.getCompiledAssembly().GetType((StringHelper.format(CultureInfo.CurrentCulture, "%1$s.%2$s", new Object[] {this.getHost().getDefaultNamespace(), this.getHost().getDefaultClassName()})));
	}
	@Override
	public void generateCode(AssemblyBuilder assemblyBuilder)
	{
		try {
			this.generateCodeCore(new AssemblyBuilderWrapper(assemblyBuilder));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void generateCodeCore(IAssemblyBuilder assemblyBuilder) throws Exception
	{
		this.onCodeGenerationStarted(assemblyBuilder);
		assemblyBuilder.AddCodeCompileUnit(this, this.getGeneratedCode());
		assemblyBuilder.GenerateTypeFactory(String.format(CultureInfo.InvariantCulture, "%1$s.%2$s", new Object[] {this.getHost().getDefaultNamespace(), this.getHost().getDefaultClassName()}));
	}
	protected TextReader internalOpenReader() throws HttpException
	{
		return super.OpenReader();
	}
	protected WebPageRazorHost createHost() throws HttpException
	{
		WebPageRazorHost hostFromConfig = this.getHostFromConfig();
		CompilingPathEventArgs compilingPathEventArgs = new CompilingPathEventArgs(this.getVirtualPath(), hostFromConfig);
		this.onBeforeCompilePath(compilingPathEventArgs);
		return compilingPathEventArgs.getHost();
	}
	protected WebPageRazorHost getHostFromConfig() throws HttpException
	{
		return WebRazorHostFactory.createHostFromConfig(this.getVirtualPath());
	}
	protected void onBeforeCompilePath(CompilingPathEventArgs args)
	{
		EventHandler<CompilingPathEventArgs> compilingPath = RazorBuildProvider.CompilingPath;
		if (compilingPath != null)
		{
			compilingPath.execute(this, args);
		}
	}
	private void onCodeGenerationStarted(IAssemblyBuilder assemblyBuilder)
	{
		this.assemblyBuilder = assemblyBuilder;
		EventHandler<?> eventHandler = (this.codeGenerationStartedInternal != null) ?
				this.codeGenerationStartedInternal : RazorBuildProvider.CodeGenerationStarted;
		if (eventHandler != null)
		{
			eventHandler.execute(this, null);
		}
	}
	private void onCodeGenerationCompleted(CodeCompileUnit generatedCode) throws HttpException
	{
		EventHandler<CodeGenerationCompleteEventArgs> eventHandler = 
				(this.codeGenerationCompletedInternal != null) ? 
						this.codeGenerationCompletedInternal 
						: RazorBuildProvider.CodeGenerationCompleted;
		if (eventHandler != null)
		{
			eventHandler.execute(this, new CodeGenerationCompleteEventArgs(this.getHost().getVirtualPath(), this.getHost().getPhysicalPath(), generatedCode));
		}
	}
	private void ensureGeneratedCode() throws HttpException, Exception 
	{
		if (this.generatedCode == null)
		{
			RazorTemplateEngine razorTemplateEngine = new RazorTemplateEngine(this.getHost());
			GeneratorResults generatorResults = null;
			try (TextReader textReader = this.internalOpenReader())
			{
				RazorTemplateEngine arg_36_0 = razorTemplateEngine;
				String className = null;
				String rootNamespace = null;
				String physicalPath = this.getHost().getPhysicalPath();
				generatorResults = arg_36_0.generateCode(textReader, className, rootNamespace, physicalPath);
			}
			if (!generatorResults.getSuccess())
			{
				throw RazorBuildProvider.createExceptionFromParserError(CollectionHelper.lastOrDefault(generatorResults.getParserErrors()), this.getVirtualPath());
			}
			this.generatedCode = generatorResults.getGeneratedCode();
			this.onCodeGenerationCompleted(this.generatedCode);
		}
	}
	private static HttpParseException createExceptionFromParserError(RazorError error, String virtualPath)
	{
		return new HttpParseException(error.getMessage() + System.lineSeparator(), null, virtualPath, null, error.getLocation().getLineIndex() + 1);
	}
	private static void setIncludeDebugInfoFlag(CompilerType compilerType)
	{
		//TODO compiler later
		//compilerType.getCompilerParameters().setIncludeDebugInformation(true);
	}
}