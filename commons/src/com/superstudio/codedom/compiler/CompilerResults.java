package com.superstudio.codedom.compiler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.superstudio.commons.Assembly;
import com.superstudio.commons.AssemblyName;
import com.superstudio.commons.Evidence;

public class CompilerResults implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7789972860752337040L;

	private CompilerErrorCollection errors = new CompilerErrorCollection();

	// private List<String> output = new List<String>();
	private List<String> output = new ArrayList<String>();

	private Assembly compiledAssembly;

	private String pathToAssembly;

	private int nativeCompilerReturnValue;

	private TempFileCollection tempFiles;

	private Evidence evidence;

	public TempFileCollection getTempFiles() {
		return this.tempFiles;
	}

	private void setTempFiles(TempFileCollection value) {
		this.tempFiles = value;
	}

	@Deprecated
	private Evidence getEvidence() {
		Evidence result = null;
		if (this.evidence != null) {
			result = (Evidence) this.evidence.clone();
		}
		return result;
	}

	@Deprecated
	public void setEvidence(Evidence value) {
		if (value != null) {
			this.evidence = value.clone();
			return;
		}
		this.evidence = null;
	}

	 public Assembly getCompiledAssembly() {
		if (this.compiledAssembly == null && this.pathToAssembly != null) {
			AssemblyName tempVar = new AssemblyName();
			tempVar.setCodeBase(this.pathToAssembly);
			this.compiledAssembly = Assembly.Load(tempVar, this.evidence);
		}
		return this.compiledAssembly;
	}

	public void setCompiledAssembly(Assembly value) {
		this.compiledAssembly = value;
	}

	public final CompilerErrorCollection getErrors() {
		return this.errors;
	}

	public List<String> getOutput() {
		return this.output;
	}

	private String getPathToAssembly() {
		return this.pathToAssembly;
	}

	public void setPathToAssembly(String value) {
		this.pathToAssembly = value;
	}

	public final int getNativeCompilerReturnValue() {
		return this.nativeCompilerReturnValue;
	}

	public void setNativeCompilerReturnValue(int value) {
		this.nativeCompilerReturnValue = value;
	}

	 
	 
	// ORIGINAL LINE: [PermissionSet(SecurityAction.LinkDemand, Name =
	// "FullTrust")] public CompilerResults(TempFileCollection tempFiles)
	public CompilerResults(TempFileCollection tempFiles) {
		this.tempFiles = tempFiles;
	}
}