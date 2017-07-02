package com.superstudio.codedom.compiler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.superstudio.commons.*;


public class CompilerParameters implements Serializable
{

	//private String coreAssemblyFileName = "";

	private List<String> assemblyNames = new ArrayList<String>();


	//private List<String> embeddedResources = new ArrayList<String>();

	//private List<String> linkedResources = new ArrayList<String>();

	//private String outputName;

	//private String mainClass;

	//private boolean generateInMemory;

	private boolean includeDebugInformation;

	private int warningLevel = -1;

	//private String compilerOptions;

	//private String win32Resource;

	private boolean treatWarningsAsErrors;

	//private boolean generateExecutable;

	//private TempFileCollection tempFiles;


	//private Evidence evidence;

	/*public final String getCoreAssemblyFileName()
	{
		return this.coreAssemblyFileName;
	}
	public final void setCoreAssemblyFileName(String value)
	{
		this.coreAssemblyFileName = value;
	}

	public final boolean getGenerateExecutable()
	{
		return this.generateExecutable;
	}
	public final void setGenerateExecutable(boolean value)
	{
		this.generateExecutable = value;
	}

	public final boolean getGenerateInMemory()
	{
		return this.generateInMemory;
	}
	public final void setGenerateInMemory(boolean value)
	{
		this.generateInMemory = value;
	}
*/
	public final List<String> getReferencedAssemblies()
	{
		return this.assemblyNames;
	}

	/*public final String getMainClass()
	{
		return this.mainClass;
	}
	public final void setMainClass(String value)
	{
		this.mainClass = value;
	}

	public final String getOutputAssembly()
	{
		return this.outputName;
	}
	public final void setOutputAssembly(String value)
	{
		this.outputName = value;
	}

	public final TempFileCollection getTempFiles()
	{
		if (this.tempFiles == null)
		{
			this.tempFiles = new TempFileCollection();
		}
		return this.tempFiles;
	}
	public final void setTempFiles(TempFileCollection value)
	{
		this.tempFiles = value;
	}
*/
	public final boolean getIncludeDebugInformation()
	{
		return this.includeDebugInformation;
	}
	public final void setIncludeDebugInformation(boolean value)
	{
		this.includeDebugInformation = value;
	}

	public final boolean getTreatWarningsAsErrors()
	{
		return this.treatWarningsAsErrors;
	}
	public final void setTreatWarningsAsErrors(boolean value)
	{
		this.treatWarningsAsErrors = value;
	}

	public final int getWarningLevel()
	{
		return this.warningLevel;
	}
	public final void setWarningLevel(int value)
	{
		this.warningLevel = value;
	}

	public final String getCompilerOptions()
	{
		return this.getCompilerOptions();
	}
	public final void setCompilerOptions(String value)
	{
		this.setCompilerOptions(value);// = value;
	}

	/*public final String getWin32Resource()
	{
		return this.win32Resource;
	}
	public final void setWin32Resource(String value)
	{
		this.win32Resource = value;
	}


	public final List<String> getEmbeddedResources()
	{
		return this.embeddedResources;
	}


	public final List<String> getLinkedResources()
	{
		return this.linkedResources;
	}
*/

	public CompilerParameters()
	{
		this(null);
	}


	public CompilerParameters(String[] assemblyNames)
	{
		this(assemblyNames,  false);
	}

	public CompilerParameters(String[] assemblyNames,  boolean includeDebugInformation)
	{
		if (assemblyNames != null)
		{
			for(String str :assemblyNames){
				this.getReferencedAssemblies().add(str);
			}
			
		}
		//this.outputName = outputName;
		this.includeDebugInformation = includeDebugInformation;
	}
}