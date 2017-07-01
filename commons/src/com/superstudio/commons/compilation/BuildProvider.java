package com.superstudio.commons.compilation;

import com.superstudio.codedom.compiler.CompilerResults;
import com.superstudio.commons.*;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.exception.HttpException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public abstract class BuildProvider
{
	private static class CompilationBuildProviderInfo extends BuildProviderInfo
	{
		private java.lang.Class _type;
		@Override
		public java.lang.Class getType()
		{
			return this._type;
		}
		public CompilationBuildProviderInfo(java.lang.Class type)
		{
			this._type = type;
		}
	}
	private static HashMap<String, BuildProviderInfo> s_dynamicallyRegisteredProviders = new HashMap<String, BuildProviderInfo>();
	public SimpleBitVector32 flags;

	public static final int isDependedOn = 1;
	public static final int noBuildResult = 2;
	public static final int ignoreParseErrors = 4;
	public static final int ignoreControlProperties = 8;
	public static final int dontThrowOnFirstParseError = 16;
	public static final int contributedCode = 32;
	private VirtualPath _virtualPath;
	private List _referencedAssemblies;
	private List<BuildProvider> _buildProviderDependencies;
	public CompilerType getCodeCompilerType() throws  Exception
	{
		return null;
	}
	public List<String> getVirtualPathDependencies() throws HttpException
	{
		//return new SingleObjectCollection(this.getVirtualPath());
		 List<String> list=new ArrayList<String>();
		list.add(this.getVirtualPath());
		 return list;
	}
	protected String getVirtualPath() throws HttpException
	{
		return VirtualPath.GetVirtualPathString(this._virtualPath);
	}
	public final VirtualPath getVirtualPathObject()
	{
		return this._virtualPath;
	}
	protected final List getReferencedAssemblies()
	{
		return this._referencedAssemblies;
	}
	public final List<BuildProvider> getBuildProviderDependencies()
	{
		return this._buildProviderDependencies;
	}
	public final boolean getIsDependedOn()
	{
		return this.flags.get(1);
	}
	public boolean getIgnoreParseErrors()
	{
		return this.flags.get(4);
	}
	public void setIgnoreParseErrors(boolean value)
	{
		this.flags.set(4, value); 
	}
	public final boolean getIgnoreControlProperties()
	{
		return this.flags.get(8);
	}
	public final void setIgnoreControlProperties(boolean value)
	{
		this.flags.set(8, value);
	}
	public final boolean getThrowOnFirstParseError()
	{
		return !this.flags.get(16);
	}
	public final void setThrowOnFirstParseError(boolean value)
	{
		this.flags.set(16, !value);//16] = !value;
	}
	/*public IAssemblyDependencyParser getAssemblyDependencyParser()
	{
		return null;
	}*/
	/*public void generateCode(AssemblyBuilder assemblyBuilder) throws  Exception
	{
	}*/
	public java.lang.Class getGeneratedType(CompilerResults results) throws HttpException
	{
		return null;
	}
	public String getCustomString(CompilerResults results)
	{
		return null;
	}
	/*public BuildProviderResultFlags GetResultFlags(CompilerResults results)
	{
		return BuildProviderResultFlags.Default;
	}*/
	public void processCompileErrors(CompilerResults results)
	{
	}
	public Collection getBuildResultVirtualPathDependencies()
	{
		return null;
	}
	/*protected final Stream OpenStream() throws HttpException
	{
		return this.OpenStream(this.getVirtualPath());
	}
	protected final Stream OpenStream(String virtualPath)
	{
		//return File.Open(virtualPath, open, read, share)
		return VirtualPathProvider.OpenFile(virtualPath);
	}
	public final Stream OpenStream(VirtualPath virtualPath)
	{
		return virtualPath.OpenFile();
	}
	
	*/
	/*public final TextReader openReader(VirtualPath virtualPath)
	{
		return Util.ReaderFromStream(this.OpenStream(virtualPath), virtualPath);
	}*/
	protected final TextReader openReader(String virtualPath)
	{
		return new TextReader(virtualPath);
		//return this.openReader(VirtualPath.create(virtualPath));
	}
	protected final TextReader openReader()
	{
		return this.openReader(this.getVirtualPathObject().getVirtualPathString());
	}
	public static void registerBuildProvider(String extension, java.lang.Class providerType) throws Exception
	{
		if (StringHelper.isNullOrEmpty(extension))
		{
			throw ExceptionUtil.ParameterNullOrEmpty("extension");
		}
		if (providerType == null)
		{
			throw new IllegalArgumentException("providerType");
		}
		if (!BuildProvider.class.isAssignableFrom(providerType))
		{
			throw ExceptionUtil.ParameterInvalid("providerType");
		}
		//BuildManager.ThrowIfPreAppStartNotRunning();
		BuildProvider.s_dynamicallyRegisteredProviders.put(extension, new BuildProvider.CompilationBuildProviderInfo(providerType));
	}
	/*public static BuildProviderInfo GetBuildProviderInfo(CompilationSection config, String extension)
	{
		BuildProvider buildProvider = config.BuildProviders[extension];
		if (buildProvider != null)
		{
			return buildProvider.BuildProviderInfo;
		}
		BuildProviderInfo result = null;
		result = BuildProvider.s_dynamicallyRegisteredProviders.get(extension);
		return result;
	}*/
	protected final CompilerType getDefaultCompilerTypeForLanguage(String language)
	{
		return null;
		//return CompilationUtil.GetCompilerInfoFromLanguage(this.getVirtualPathObject(), language);
	}
	/*
	protected final CompilerType GetDefaultCompilerType()
	{
		return CompilationUtil.GetDefaultLanguageCompilerInfo(null, this.getVirtualPathObject());
	}*/
	public final void setNoBuildResult()
	{
		this.flags.set(2,true);//[2] = true;
	}
	public final void setContributedCode()
	{
		this.flags.set(32,true);// = true;
	}
	public final void setVirtualPath(VirtualPath virtualPath)
	{
		this._virtualPath = virtualPath;
	}
	public final void setReferencedAssemblies(List referencedAssemblies)
	{
		this._referencedAssemblies = referencedAssemblies;
	}
	public final void addBuildProviderDependency(BuildProvider dependentBuildProvider)
	{
		if (this._buildProviderDependencies == null)
		{
			//this._buildProviderDependencies = new BuildProviderSet();
			this._buildProviderDependencies=new ArrayList<BuildProvider>();
		}
		this._buildProviderDependencies.add(dependentBuildProvider);
		dependentBuildProvider.flags.set(1, true);
	}
	/*public final String GetCultureName()
	{
		return Util.GetCultureName(this.getVirtualPath());
	}*/
	/*public final BuildResult GetBuildResult(CompilerResults results)
	{
		BuildResult buildResult = this.CreateBuildResult(results);
		if (buildResult == null)
		{
			return null;
		}
		buildResult.VirtualPath = this.getVirtualPathObject();
		this.SetBuildResultDependencies(buildResult);
		return buildResult;
	}
	public BuildResult CreateBuildResult(CompilerResults results)
	{
		if (this.flags[2])
		{
			return null;
		}
		if (!BuildManagerHost.InClientBuildManager && results != null)
		{
			Assembly arg_20_0 = results.CompiledAssembly;
		}
		java.lang.Class generatedType = this.getGeneratedType(results);
		BuildResult buildResult;
		if (generatedType != null)
		{
			BuildResultCompiledType buildResultCompiledType = this.CreateBuildResult(generatedType);
			if (!buildResultCompiledType.IsDelayLoadType && (results == null || generatedType.Assembly != results.CompiledAssembly))
			{
				buildResultCompiledType.UsesExistingAssembly = true;
			}
			buildResult = buildResultCompiledType;
		}
		else
		{
			String customString = this.getCustomString(results);
			if (customString != null)
			{
				buildResult = new BuildResultCustomString(this.flags[32] ? results.CompiledAssembly : null, customString);
			}
			else
			{
				if (results == null)
				{
					return null;
				}
				buildResult = new BuildResultCompiledAssembly(results.CompiledAssembly);
			}
		}
		int num = (int)this.GetResultFlags(results);
		if (num != 0)
		{
			num &= 65535;
			buildResult.Flags |= num;
		}
		return buildResult;
	}
	public BuildResultCompiledType CreateBuildResult(java.lang.Class t)
	{
		return new BuildResultCompiledType(t);
	}
	public final void SetBuildResultDependencies(BuildResult result)
	{
		result.AddVirtualPathDependencies(this.getVirtualPathDependencies());
	}
	public static CompilerType GetCompilerTypeFromBuildProvider(BuildProvider buildProvider)
	{
		HttpContext httpContext = null;
		if (EtwTrace.IsTraceEnabled(5, 1) && (httpContext = HttpContext.Current) != null)
		{
			EtwTrace.Trace(EtwTraceType.ETW_TYPE_PARSE_ENTER, httpContext.WorkerRequest);
		}
		CompilerType result;
		try
		{
			CompilerType codeCompilerType = buildProvider.getCodeCompilerType();
			if (codeCompilerType != null)
			{
				CompilationUtil.CheckCompilerOptionsAllowed(codeCompilerType.getCompilerParameters().getCompilerOptions(), false, null, 0);
			}
			result = codeCompilerType;
		}
		finally
		{
			if (EtwTrace.IsTraceEnabled(5, 1) && httpContext != null)
			{
				EtwTrace.Trace(EtwTraceType.ETW_TYPE_PARSE_LEAVE, httpContext.WorkerRequest);
			}
		}
		return result;
	}
	public static String GetDisplayName(BuildProvider buildProvider) throws HttpException
	{
		if (buildProvider.getVirtualPath() != null)
		{
			return buildProvider.getVirtualPath();
		}
		return buildProvider.getClass().getSimpleName();
	}
	public Collection GetGeneratedTypeNames()
	{
		return null;
	}
	protected CodeCompileUnit GetCodeCompileUnit(RefObject<Map> linePragmasTable)
	{
		CodeCompileUnit arg_30_0 = new CodeSnippetCompileUnit(Util.StringFromVirtualPath(this.getVirtualPathObject()));
		LinePragmaCodeInfo value = new LinePragmaCodeInfo(1, 1, 1, -1, false);
		linePragmasTable.argValue = new Hashtable();
		linePragmasTable.argValue.put(1, value);
		return arg_30_0;
	}
	public Collection GetCompileWithDependencies()
	{
		return null;
	}*/
}