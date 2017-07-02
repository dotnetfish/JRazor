package com.superstudio.codedom.compiler;

import com.superstudio.codedom.*;
import com.superstudio.commons.Component;
import com.superstudio.commons.SR;
import com.superstudio.commons.TypeConverter;
import com.superstudio.commons.TypeDescriptor;
import com.superstudio.commons.exception.ConfigurationErrorsException;
import com.superstudio.commons.io.TextWriter;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class CodeDomProvider extends Component {

	public String getFileExtension() {
		return "";
	}

	public LanguageOptions getLanguageOptions() {
		return LanguageOptions.None;
	}

	public static CodeDomProvider createProvider(String language, Map<String, String> providerOptions)
			throws ClassNotFoundException, ConfigurationErrorsException, NoSuchMethodException, SecurityException,
			InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

		return CodeDomProvider.getCompilerInfo(language).CreateProvider(providerOptions);

	}

	public static CodeDomProvider createProvider(String language) throws ClassNotFoundException,
			ConfigurationErrorsException, InstantiationException, IllegalAccessException {
		return CodeDomProvider.getCompilerInfo(language).CreateProvider();
	}


	public static CompilerInfo getCompilerInfo(String language) throws ConfigurationErrorsException {
		CompilerParameters compilerParameters = new CompilerParameters();
		String	 codeDomProviderTypeName="com.superstudio.language.java.JavaCodeProvider";
		CompilerInfo javaCompilerInfo = new CompilerInfo(compilerParameters, codeDomProviderTypeName);
		javaCompilerInfo._compilerLanguages = new String[] {"java"};
		javaCompilerInfo._compilerExtensions = new String[] {".java", "java"};
		javaCompilerInfo._providerOptions = new HashMap<String, String>();
		return javaCompilerInfo;
	}




	public abstract ICodeGenerator createGenerator();

	public ICodeGenerator createGenerator(TextWriter output) {
		return this.createGenerator();
	}

	public ICodeGenerator createGenerator(String fileName) {
		return this.createGenerator();
	}

	
	public abstract ICodeCompiler createCompiler();

	@Deprecated
	public ICodeParser CreateParser() {
		return null;
	}

	public TypeConverter getConverter(java.lang.Class type) {
		return TypeDescriptor.GetConverter(type);
	}

	/*public CompilerResults compileAssemblyFromDom(CompilerParameters options, CodeCompileUnit... compilationUnits)
			throws Exception {
		return this.createCompilerHelper().compileAssemblyFromDomBatch(options, compilationUnits);
	}

	public CompilerResults compileAssemblyFromFile(CompilerParameters options, String... fileNames)
			throws Exception {
		return this.createCompilerHelper().compileAssemblyFromFileBatch(options, fileNames);
	}

	public CompilerResults compileAssemblyFromSource(CompilerParameters options, String... sources) throws Exception {
		return this.createCompilerHelper().compileAssemblyFromSourceBatch(options, sources);
	}
*/
	public boolean isValidIdentifier(String value) {
		return this.createGeneratorHelper().isValidIdentifier(value);
	}

	public String createEscapedIdentifier(String value) {
		return this.createGeneratorHelper().createEscapedIdentifier(value);
	}

	public String createValidIdentifier(String value) {
		return this.createGeneratorHelper().createValidIdentifier(value);
	}

	public String getTypeOutput(CodeTypeReference type) {
		return this.createGeneratorHelper().getTypeOutput(type);
	}

	public boolean supports(GeneratorSupport generatorSupport) {
		return this.createGeneratorHelper().supports(generatorSupport);
	}

	public void generateCodeFromExpression(CodeExpression expression, TextWriter writer, CodeGeneratorOptions options)
			throws Exception {
		this.createGeneratorHelper().generateCodeFromExpression(expression, writer, options);
	}

	public void generateCodeFromStatement(CodeStatement statement, TextWriter writer, CodeGeneratorOptions options)
			throws Exception {
		this.createGeneratorHelper().generateCodeFromStatement(statement, writer, options);
	}

	public void generateCodeFromNamespace(CodeNamespace codeNamespace, TextWriter writer, CodeGeneratorOptions options)
			throws Exception {
		this.createGeneratorHelper().generateCodeFromNamespace(codeNamespace, writer, options);
	}

	public void generateCodeFromCompileUnit(CodeCompileUnit compileUnit, TextWriter writer,
			CodeGeneratorOptions options) throws Exception {
		this.createGeneratorHelper().generateCodeFromCompileUnit(compileUnit, writer, options);
	}

	public void generateCodeFromType(CodeTypeDeclaration codeType, TextWriter writer, CodeGeneratorOptions options)
			throws Exception {
		this.createGeneratorHelper().generateCodeFromType(codeType, writer, options);
	}

	public void generateCodeFromMember(CodeTypeMember member, TextWriter writer, CodeGeneratorOptions options)
			throws Exception {
		throw new UnsupportedOperationException(SR.GetString("NotSupported_CodeDomAPI"));
	}

/*	public CodeCompileUnit parse(TextReader codeStream) {
		return this.CreateParserHelper().parse(codeStream);
	}*/

	private ICodeCompiler createCompilerHelper() {
		ICodeCompiler expr_06 = this.createCompiler();
		if (expr_06 == null) {
			throw new UnsupportedOperationException(SR.GetString("NotSupported_CodeDomAPI"));
		}
		return expr_06;
	}

	private ICodeGenerator createGeneratorHelper() {
		ICodeGenerator expr_06 = this.createGenerator();
		if (expr_06 == null) {
			throw new UnsupportedOperationException(SR.GetString("NotSupported_CodeDomAPI"));
		}
		return expr_06;
	}

	/*private ICodeParser CreateParserHelper() {
		ICodeParser expr_06 = this.CreateParser();
		if (expr_06 == null) {
			throw new UnsupportedOperationException(SR.GetString("NotSupported_CodeDomAPI"));
		}
		return expr_06;
	}*/
/*

	public static boolean tryGetProbableCoreAssemblyFilePath(CompilerParameters parameters,
			RefObject<String> coreAssemblyFilePath) {
		String text = null;
		// char[] separator = new char[] { java.io.File.separatorChar };
		String value = Path.Combine("Reference Assemblies", "Microsoft", "Framework");
		for (String current : parameters.getReferencedAssemblies()) {
			if (StringUtils.equals((new java.io.File(current)).getName(), "mscorlib.dll",
					StringComparison.OrdinalIgnoreCase)) {
				coreAssemblyFilePath.setRefObj(""); //= "";
				boolean result = false;
				return result;
			}
			if (current.indexOf(value) >= 0) {
				// String[] array = StringHelper.split(current, separator);
				String[] array = current.split(String.valueOf(java.io.File.separatorChar));
				for (int i = 0; i < array.length - 5; i++) {
					if (StringHelper.equals(array[i], "Reference Assemblies", StringComparison.OrdinalIgnoreCase)
							&& StringHelper.startWith(array[i + 4], "v", StringComparison.OrdinalIgnoreCase)) {
						if (text != null) {
							if (!StringHelper.equals(text, (new java.io.File(current)).getParent(),
									StringComparison.OrdinalIgnoreCase)) {
								coreAssemblyFilePath.setRefObj(""); //= "";
								boolean result = false;
								return result;
							}
						} else {
							text = (new java.io.File(current)).getParent();
						}
					}
				}
			}
		}
		if (text != null) {
			coreAssemblyFilePath.setRefObj(Path.Combine(text, "mscorlib.dll")); 
			return true;
		}
		coreAssemblyFilePath.setRefObj("");// = "";
		return false;
	}
*/
}