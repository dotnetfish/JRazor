package com.superstudio.codedom.compiler;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import com.superstudio.codedom.*;
import com.superstudio.commons.Assembly;
import com.superstudio.commons.Encoding;
import com.superstudio.commons.Environment;
import com.superstudio.commons.SR;
import com.superstudio.commons.StreamReader;
import com.superstudio.commons.StreamWriter;
import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.commons.io.*;
import com.superstudio.commons.io.FileMode;

public abstract class CodeCompiler extends CodeGenerator implements ICodeCompiler {
	protected abstract String getFileExtension();

	protected abstract String getCompilerName();

	public final CompilerResults compileAssemblyFromDom(CompilerParameters options, CodeCompileUnit e) {
		if (options == null) {
			throw new IllegalArgumentException("options");
		}
		CompilerResults result;
		try {
			result = this.FromDom(options, e);
		} finally {
			options.getTempFiles().SafeDelete();
		}
		return result;
	}

	public final CompilerResults compileAssemblyFromFile(CompilerParameters options, String fileName) throws FileNotFoundException, Exception {
		if (options == null) {
			throw new IllegalArgumentException("options");
		}
		CompilerResults result;
		try {
			result = this.FromFile(options, fileName);
		} finally {
			options.getTempFiles().SafeDelete();
		}
		return result;
	}

	public final CompilerResults compileAssemblyFromSource(CompilerParameters options, String source) throws Exception {
		if (options == null) {
			throw new IllegalArgumentException("options");
		}
		CompilerResults result;
		try {
			result = this.FromSource(options, source);
		} finally {
			options.getTempFiles().SafeDelete();
		}
		return result;
	}

	public final CompilerResults compileAssemblyFromSourceBatch(CompilerParameters options, String[] sources) throws Exception {
		if (options == null) {
			throw new IllegalArgumentException("options");
		}
		CompilerResults result;
		try {
			result = this.fromSourceBatch(options, sources);
		} finally {
			options.getTempFiles().SafeDelete();
		}
		return result;
	}

	public final CompilerResults compileAssemblyFromFileBatch(CompilerParameters options, String[] fileNames)
			throws FileNotFoundException, Exception {
		if (options == null) {
			throw new IllegalArgumentException("options");
		}
		if (fileNames == null) {
			throw new IllegalArgumentException("fileNames");
		}
		CompilerResults result;
		try {
			/*
			 * for (int i = 0; i < fileNames.length; i++) { try (
			 * FileInputStream stream=File.OpenRead(fileNames[i])); { } }
			 */
			try (AutoCloseable closeable = com.superstudio.commons.io.File.OpenRead(fileNames)) {

			}
			result = this.FromFileBatch(options, fileNames);
		} finally {
			options.getTempFiles().SafeDelete();
		}
		return result;
	}

	public final CompilerResults compileAssemblyFromDomBatch(CompilerParameters options, CodeCompileUnit[] ea) {
		if (options == null) {
			throw new IllegalArgumentException("options");
		}
		CompilerResults result;
		try {
			result = this.FromDomBatch(options, ea);
		} finally {
			options.getTempFiles().SafeDelete();
		}
		return result;
	}

	public final void Compile(CompilerParameters options, String compilerDirectory, String compilerExe,
			String arguments, RefObject<String> outputFile, RefObject<Integer> nativeReturnValue,
			String trueArgs) throws Exception {
		String text = null;
		outputFile.setRefObj (options.getTempFiles().AddExtension("out"));
		String text2 = Path.Combine(compilerDirectory, compilerExe);
		if ((new java.io.File(text2)).isFile()) {
			String trueCmdLine = null;
			if (trueArgs != null) {
				trueCmdLine = "\"" + text2 + "\" " + trueArgs;
			}
			RefObject<String> tempRef_text = new RefObject<String>(text);
			nativeReturnValue.setRefObj( Executor.ExecWaitWithCapture(options.getSafeUserToken(),
					"\"" + text2 + "\" " + arguments, Environment.CurrentDirectory, options.getTempFiles(), outputFile,
					tempRef_text, trueCmdLine));
			text = tempRef_text.getRefObj();
			return;
		}
		throw new IllegalStateException(SR.GetString("CompilerNotFound", new Object[] { text2 }));
	}

	protected CompilerResults FromDom(CompilerParameters options, CodeCompileUnit e) {
		if (options == null) {
			throw new IllegalArgumentException("options");
		}
		// (new
		// SecurityPermission(SecurityPermissionFlag.UnmanagedCode)).Demand();
		return this.FromDomBatch(options, new CodeCompileUnit[] { e });
	}

	protected CompilerResults FromFile(CompilerParameters options, String fileName)
			throws FileNotFoundException, Exception {
		if (options == null) {
			throw new IllegalArgumentException("options");
		}
		if (fileName == null) {
			throw new IllegalArgumentException("fileName");
		}
		// (new
		// SecurityPermission(SecurityPermissionFlag.UnmanagedCode)).Demand();
		/*
		 * try (File.OpenRead(fileName)) { }
		 */
		try (AutoCloseable closeable = File.OpenRead(fileName)) {

		}
		return this.FromFileBatch(options, new String[] { fileName });
	}

	protected CompilerResults FromSource(CompilerParameters options, String source) throws Exception {
		if (options == null) {
			throw new IllegalArgumentException("options");
		}
		// (new
		// SecurityPermission(SecurityPermissionFlag.UnmanagedCode)).Demand();
		return this.fromSourceBatch(options, new String[] { source });
	}

	protected CompilerResults FromDomBatch(CompilerParameters options, CodeCompileUnit[] ea) {
		if (options == null) {
			throw new IllegalArgumentException("options");
		}
		if (ea == null) {
			throw new IllegalArgumentException("ea");
		}
		// (new
		// SecurityPermission(SecurityPermissionFlag.UnmanagedCode)).Demand();
		String[] array = new String[ea.length];
		CompilerResults result = null;
		try {
			// WindowsImpersonationContext impersonation =
			// Executor.RevertImpersonation();
			try {
				for (int i = 0; i < ea.length; i++) {
					if (ea[i] != null) {
						this.ResolveReferencedAssemblies(options, ea[i]);
						array[i] = options.getTempFiles().AddExtension(i + this.getFileExtension());
						FileStream stream = new FileStream(array[i], FileMode.Create, FileAccess.Write, FileShare.Read);
						try {
							try (StreamWriter streamWriter = new StreamWriter(stream, Encoding.UTF8)) {
								((ICodeGenerator) this).generateCodeFromCompileUnit(ea[i], streamWriter,
										super.getOptions());
								streamWriter.Flush();
							}catch(Exception ex){
								ex.printStackTrace();
							}
						} finally {
							stream.close();
						}
					}
				}
				result = this.FromFileBatch(options, array);
			} catch (Exception ex) {

			} finally {
				// Executor.ReImpersonate(impersonation);
			}
		} catch (java.lang.Exception e) {
			throw e;
		}
		return result;
	}

	private void ResolveReferencedAssemblies(CompilerParameters options, CodeCompileUnit e) {
		if (e.getReferencedAssemblies().size() > 0) {
			for (String current : e.getReferencedAssemblies()) {
				if (!options.getReferencedAssemblies().contains(current)) {
					options.getReferencedAssemblies().add(current);
				}
			}
		}
	}

	protected CompilerResults FromFileBatch(CompilerParameters options, String[] fileNames) throws Exception {
		if (options == null) {
			throw new IllegalArgumentException("options");
		}
		if (fileNames == null) {
			throw new IllegalArgumentException("fileNames");
		}
		// (new
		// SecurityPermission(SecurityPermissionFlag.UnmanagedCode)).Demand();
		String path = null;
		int num = 0;
		CompilerResults compilerResults = new CompilerResults(options.getTempFiles());
		// (new
		// SecurityPermission(SecurityPermissionFlag.ControlEvidence)).Assert();
		try {
			compilerResults.setEvidence(options.getEvidence());
		} finally {
			// TODO modify this code
			// CodeAccessPermission.RevertAssert();
		}
		boolean flag = false;
		if (options.getOutputAssembly() == null || options.getOutputAssembly().length() == 0) {
			String fileExtension = options.getGenerateExecutable() ? "exe" : "dll";
			options.setOutputAssembly(
					compilerResults.getTempFiles().AddExtension(fileExtension, !options.getGenerateInMemory()));
			(new FileStream(options.getOutputAssembly(), FileMode.Create, FileAccess.ReadAndWrite)).close();
			flag = true;
		}
		compilerResults.getTempFiles().AddExtension("pdb");
		String text = this.CmdArgsFromParameters(options) + " " + CodeCompiler.joinStringArray(fileNames, " ");
		String responseFileCmdArgs = this.GetResponseFileCmdArgs(options, text);
		String trueArgs = null;
		if (responseFileCmdArgs != null) {
			trueArgs = text;
			text = responseFileCmdArgs;
		}
		RefObject<String> tempRef_path = new RefObject<String>(path);
		RefObject<Integer> tempRef_num = new RefObject<Integer>(num);
		this.Compile(options, Executor.GetRuntimeInstallDirectory(), this.getCompilerName(), text, tempRef_path,
				tempRef_num, trueArgs);
		path = tempRef_path.getRefObj();
		num = tempRef_num.getRefObj();
		compilerResults.setNativeCompilerReturnValue(num);// = num;
		if (num != 0 || options.getWarningLevel() > 0) {
			FileStream fileStream = new FileStream(path, FileMode.Open, FileAccess.Read, FileShare.ReadAndWrite);
			try {
				if (fileStream.getLength() > 0L) {
					StreamReader streamReader = new StreamReader(fileStream, Encoding.UTF8);
					String text2;
					do {
						text2 = streamReader.ReadLine();
						if (text2 != null) {
							compilerResults.getOutput().add(text2);
							this.ProcessCompilerOutputLine(compilerResults, text2);
						}
					} while (text2 != null);
					streamReader.close();
				}
			} finally {
				fileStream.Close();
			}
			if (num != 0 & flag) {
				(new java.io.File(options.getOutputAssembly())).delete();
			}
		}
		if (!compilerResults.getErrors().getHasErrors() && options.getGenerateInMemory()) {
			// FileStream fileStream2 = new
			// FileStream(options.getOutputAssembly(), FileMode.Open,
			// FileAccess.Read, FileShare.Read);

			FileInputStream fileStream2 = new FileInputStream(FileDescriptor.in);
			try {
				int num2 = (int) fileStream2.available();
				 
				
				
				byte[] array = new byte[num2];
				fileStream2.read(array, 0, num2);
				// (new
				// SecurityPermission(SecurityPermissionFlag.ControlEvidence)).Assert();
				try {
					compilerResults.setCompiledAssembly(Assembly.Load(array, null, options.getEvidence()));
					//compilerResults.setCompiledAssembly(Package.getPackages("")));
					return compilerResults;
				} finally {
					// TODO modify this code
					// CodeAccessPermission.RevertAssert();
				}
			} finally {
				fileStream2.close();
			}
		}
		compilerResults.setPathToAssembly(options.getOutputAssembly());
		return compilerResults;
	}

	protected abstract void ProcessCompilerOutputLine(CompilerResults results, String line);

	protected abstract String CmdArgsFromParameters(CompilerParameters options);

	protected String GetResponseFileCmdArgs(CompilerParameters options, String cmdArgs) throws Exception {
		String text = options.getTempFiles().AddExtension("cmdline");
		FileStream stream = new FileStream(text, FileMode.Create, FileAccess.Write, FileShare.Read);
		try {
			try (StreamWriter streamWriter = new StreamWriter(stream, Encoding.UTF8)) {
				streamWriter.Write(cmdArgs);
				streamWriter.Flush();
			}
		} finally {
			stream.close();
		}
		return "@\"" + text + "\"";
	}

	protected CompilerResults fromSourceBatch(CompilerParameters options, String[] sources) throws Exception {
		if (options == null) {
			throw new IllegalArgumentException("options");
		}
		if (sources == null) {
			throw new IllegalArgumentException("sources");
		}
		// (new
		// SecurityPermission(SecurityPermissionFlag.UnmanagedCode)).Demand();
		String[] array = new String[sources.length];
		CompilerResults result = null;
		try {
			// WindowsImpersonationContext impersonation =
			// Executor.RevertImpersonation();
			try {
				for (int i = 0; i < sources.length; i++) {
					String text = options.getTempFiles().AddExtension(i + this.getFileExtension());
					FileStream stream = new FileStream(text, FileMode.Create, FileAccess.Write, FileShare.Read);
					try {
						try (StreamWriter streamWriter = new StreamWriter(stream, "")) {
							streamWriter.Write(sources[i]);
							streamWriter.Flush();
						}
					} finally {
						stream.close();
					}
					array[i] = text;
				}
				result = this.FromFileBatch(options, array);
			} finally {
				//TODO
				//Executor.ReImpersonate(impersonation);
			}
		} catch (java.lang.Exception e) {
			throw e;
		}
		return result;
	}

	protected static String joinStringArray(String[] sa, String separator) {
		if (sa == null || sa.length == 0) {
			return "";
		}
		if (sa.length == 1) {
			return "\"" + sa[0] + "\"";
		}
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < sa.length - 1; i++) {
			stringBuilder.append("\"");
			stringBuilder.append(sa[i]);
			stringBuilder.append("\"");
			stringBuilder.append(separator);
		}
		stringBuilder.append("\"");
		stringBuilder.append(sa[sa.length - 1]);
		stringBuilder.append("\"");
		return stringBuilder.toString();
	}
}