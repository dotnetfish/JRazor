package com.superstudio.language.java;

import com.superstudio.commons.csharpbridge.StringComparison;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.superstudio.codedom.*;
import com.superstudio.codedom.compiler.*;
import com.superstudio.commons.Assembly;
import com.superstudio.commons.Encoding;
import com.superstudio.commons.Environment;
import com.superstudio.commons.FixedStringLookup;
import com.superstudio.commons.Path;
import com.superstudio.commons.SR;
import com.superstudio.commons.StreamReader;
import com.superstudio.commons.StreamWriter;
import com.superstudio.commons.TypeAttributes;
import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.exception.ArgumentException;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.commons.io.File;
import com.superstudio.commons.io.FileAccess;
import com.superstudio.commons.io.FileMode;
import com.superstudio.commons.io.FileShare;
import com.superstudio.commons.io.FileStream;
import com.superstudio.commons.io.TextWriter;

public class JavaCodeGenerator implements ICodeCompiler, ICodeGenerator {

	private IndentedTextWriter output;
	private CodeGeneratorOptions options;
	private CodeTypeDeclaration currentClass;
	private CodeTypeMember currentMember;
	private boolean inNestedBinary;
	private Map<String, String> provOptions;
	private static int ParameterMultilineThreshold = 15;
	private static int MaxLineLength = 80;
	private static GeneratorSupport LanguageSupport = GeneratorSupport
			.forValue(
					GeneratorSupport.ArraysOfArrays.getValue() | GeneratorSupport.EntryPointMethod.getValue()
							| GeneratorSupport.GotoStatements.getValue()
							| GeneratorSupport.MultidimensionalArrays.getValue()
							| GeneratorSupport.StaticConstructors.getValue() | GeneratorSupport.TryCatchStatements
									.getValue()
							| GeneratorSupport.ReturnTypeAttributes.getValue()
							| GeneratorSupport.DeclareValueTypes.getValue() | GeneratorSupport.DeclareEnums.getValue()
							| GeneratorSupport.DeclareDelegates.getValue()
							| GeneratorSupport.DeclareInterfaces.getValue() | GeneratorSupport.DeclareEvents.getValue()
							| GeneratorSupport.AssemblyAttributes.getValue()
							| GeneratorSupport.ParameterAttributes.getValue()
							| GeneratorSupport.ReferenceParameters.getValue()
							| GeneratorSupport.ChainedConstructorArguments.getValue()
							| GeneratorSupport.NestedTypes.getValue() | GeneratorSupport.MultipleInterfaceMembers
									.getValue()
							| GeneratorSupport.PublicStaticMembers.getValue()
							| GeneratorSupport.ComplexExpressions.getValue()
							| GeneratorSupport.Win32Resources.getValue() | GeneratorSupport.Resources.getValue()
							| GeneratorSupport.PartialTypes.getValue()
							| GeneratorSupport.GenericTypeReference.getValue()
							| GeneratorSupport.GenericTypeDeclaration.getValue()
							| GeneratorSupport.DeclareIndexerProperties.getValue());
	private static volatile Pattern outputRegWithFileAndLine;
	private static volatile Pattern outputRegSimple;
	private final static String[][] keywords = new String[][] { null, new String[] { "do", "if", "in", "instanceof" },
			new String[] { "for", "int", "new", "try" },
			new String[] { "super", "boolean", "byte", "case", "char", "else", "enum", "lock", "long", "null", "this",
					"true", "uint", "void", "synchonrized" },
			new String[] { "break", "catch", "class", "final", "false", "float", "sbyte", "short", "throw", "throws",
					"try", "while" },
			new String[] { "double", "extern", "object", "params", "public", "return", "sealed", "sizeof", "static",
					"String", "struct", "switch", "typeof", "ushort" },
			new String[] { "checked", "decimal", "default", "finally", "for", "private" },
			new String[] { "abstract", "continue", "explicit", "implicit", "internal", "operator", "override",
					"readonly", "volatile" },
			new String[] { "__arglist", "__makeref", "__reftype", "interface", "namespace", "protected", "unchecked" },
			new String[] { "__refvalue", "stackalloc" } };
	private boolean generatingForLoop;

	private String getFileExtension() {

		return ".java";

	}

	private String getCompilerName() {

		return "jawax.exe";

	}

	private String getCurrentTypeName() {

		if (this.currentClass != null) {
			return this.currentClass.getName();
		}
		return "<% unknown %>";

	}

	private int getIndent() {

		return this.output.getIndent();
	}

	private void setIndent(int value) {
		this.output.setIndent(value);
	}

	private boolean getIsCurrentInterface() {
		return this.currentClass != null && !(this.currentClass instanceof CodeTypeDelegate)
				&& this.currentClass.getIsInterface();
	}

	private boolean getIsCurrentClass() {
		return this.currentClass != null && !(this.currentClass instanceof CodeTypeDelegate)
				&& this.currentClass.getIsClass();
	}

	private boolean getIsCurrentStruct()

	{
		return this.currentClass != null && !(this.currentClass instanceof CodeTypeDelegate)
				&& this.currentClass.getIsStruct();
	}

	private boolean getIsCurrentEnum()

	{
		return this.currentClass != null && !(this.currentClass instanceof CodeTypeDelegate)
				&& this.currentClass.getIsEnum();
	}

	private boolean getIsCurrentDelegate()

	{
		return this.currentClass != null && this.currentClass instanceof CodeTypeDelegate;
	}

	private String getNullToken()

	{
		return "null";
	}

	private CodeGeneratorOptions getOptions() {
		return this.options;
	}

	private TextWriter getOutput() {
		return this.output;

	}

	JavaCodeGenerator() {
	}

	public JavaCodeGenerator(Map<String, String> providerOptions) {
		this.provOptions = providerOptions;
	}

	private String quoteSnippetStringCStyle(String value) throws Exception {
		StringBuilder StringBuilder = new StringBuilder(value.length() + 5);
		Indentation indentation = new Indentation((IndentedTextWriter) this.output, this.getIndent() + 1);
		StringBuilder.append("\"");
		int i = 0;
		while (i < value.length()) {
			char c = value.charAt(i);
			if (c <= '"') {
				if (c != '\0') {
					switch (c) {
					case '\t':
						StringBuilder.append("\\t");
						break;
					case '\n':
						StringBuilder.append("\\n");
						break;
					// case '\v':
					case '\f':
						StringBuilder.append(value.charAt(i));
						break;
					// goto IL_10C;
					case '\r':
						StringBuilder.append("\\r");
						break;
					default:
						if (c != '"') {
							// goto IL_10C;
							break;
						}
						StringBuilder.append("\\\"");
						break;
					}
				} else {
					StringBuilder.append("\\0");
				}
			} else {
				if (c <= '\\') {
					if (c != '\'') {
						if (c != '\\') {
							break;
						}
						StringBuilder.append("\\\\");
					} else {
						StringBuilder.append("\\'");
					}
				} else {
					if (c != '\u2028' && c != '\u2029') {
						// goto IL_10C;
						break;
						// StringBuilder.append(value[i]);
					}
					this.appendEscapedChar(StringBuilder, value.charAt(i));
				}
			}

			if (i > 0 && i % 80 == 0) {
				if (Character.isHighSurrogate(value.charAt(i)) && i < value.length() - 1
						&& Character.isLowSurrogate(value.charAt(i + 1))) {
					StringBuilder.append(value.charAt(++i));
				}
				StringBuilder.append("\" +");
				StringBuilder.append(Environment.NewLine);
				StringBuilder.append(indentation.getIndentationString());
				StringBuilder.append('"');
			}
			i++;
			continue;
			// IL_10C:

			// goto IL_11A;
		}
		StringBuilder.append("\"");
		return StringBuilder.toString();
	}

	private String QuoteSnippetStringVerbatimStyle(String value) {
		StringBuilder StringBuilder = new StringBuilder(value.length() + 5);
		StringBuilder.append("\"");
		for (int i = 0; i < value.length(); i++) {
			if (value.charAt(i) == '"') {
				StringBuilder.append("\"\"");
			} else if ((value.charAt(i) == '\r' && value.charAt(i + 1) == '\n')) {// \r\n
				StringBuilder.append("\"+\r\n+\"");
				i++;
			} else if (value.charAt(i) == '\n' || value.charAt(i) == '\n') {
				StringBuilder.append("\"+\r\n+\"");
			} else {
				StringBuilder.append(value.charAt(i));
			}
		}
		StringBuilder.append("\"");
		return StringBuilder.toString();
	}

	private String QuoteSnippetString(String value) throws Exception {
		if (value.length() < 256 || value.length() > 1500 || value.indexOf('\0') != -1) {
			return this.quoteSnippetStringCStyle(value);
		}
		return this.QuoteSnippetStringVerbatimStyle(value);
	}

	private void ProcessCompilerOutputLine(CompilerResults results, String line) {
		if (JavaCodeGenerator.outputRegSimple == null) {
			JavaCodeGenerator.outputRegWithFileAndLine = Pattern
					.compile("(^(.*)(\\(([0-9]+),([0-9]+)\\)): )(error|warning) ([A-Z]+[0-9]+) ?: (.*)");
			JavaCodeGenerator.outputRegSimple = Pattern.compile("(error|warning) ([A-Z]+[0-9]+) ?: (.*)");
		}
		Matcher match = JavaCodeGenerator.outputRegWithFileAndLine.matcher(line);// (line);
		boolean flag;
		if (match.find()) {
			flag = true;
		} else {
			match = JavaCodeGenerator.outputRegSimple.matcher(line);
			flag = false;
		}
		if (match.find()) {
			CompilerError compilerError = new CompilerError();
			if (flag) {
				compilerError.setFileName(match.group(2));
				compilerError.setLine(Integer.parseInt(match.group(4)));
				compilerError.setColumn(Integer.parseInt(match.group(5)));
			}
			if (StringHelper.Compare(match.group(flag ? 6 : 1), "warning", StringComparison.OrdinalIgnoreCase) == 0) {
				compilerError.setIsWarning(true);
			}
			compilerError.setErrorNumber(match.group(flag ? 7 : 2));
			compilerError.setErrorText(match.group(flag ? 8 : 3));
			results.getErrors().Add(compilerError);
		}
	}

	private String CmdArgsFromParameters(CompilerParameters options) {
		StringBuilder StringBuilder = new StringBuilder(128);
		if (options.getGenerateExecutable()) {
			StringBuilder.append("/t:exe ");
			if (options.getMainClass() != null && options.getMainClass().length() > 0) {
				StringBuilder.append("/main:");
				StringBuilder.append(options.getMainClass());
				StringBuilder.append(" ");
			}
		} else {
			StringBuilder.append("/t:library ");
		}
		StringBuilder.append("/utf8output ");
		String text = options.getCoreAssemblyFileName();
		String text2 = "";
		RefObject<String> refObj = new RefObject<String>(text2);

		if (StringHelper.isNullOrWhiteSpace(options.getCoreAssemblyFileName())
				&& CodeDomProvider.tryGetProbableCoreAssemblyFilePath(options, refObj)) {
			text = refObj.getRefObj();
		}
		if (!StringHelper.isNullOrWhiteSpace(text)) {
			StringBuilder.append("/nostdlib+ ");
			StringBuilder.append("/R:\"").append(text.trim()).append("\" ");
		}
		for (String current : options.getReferencedAssemblies()) {
			StringBuilder.append("/R:");
			StringBuilder.append("\"");
			StringBuilder.append(current);
			StringBuilder.append("\"");
			StringBuilder.append(" ");
		}
		StringBuilder.append("/out:");
		StringBuilder.append("\"");
		StringBuilder.append(options.getOutputAssembly());
		StringBuilder.append("\"");
		StringBuilder.append(" ");
		if (options.getIncludeDebugInformation()) {
			StringBuilder.append("/D:DEBUG ");
			StringBuilder.append("/debug+ ");
			StringBuilder.append("/optimize- ");
		} else {
			StringBuilder.append("/debug- ");
			StringBuilder.append("/optimize+ ");
		}
		if (options.getWin32Resource() != null) {
			StringBuilder.append("/win32res:\"" + options.getWin32Resource() + "\" ");
		}
		for (String current2 : options.getEmbeddedResources()) {
			StringBuilder.append("/res:\"");
			StringBuilder.append(current2);
			StringBuilder.append("\" ");
		}
		for (String current3 : options.getLinkedResources()) {
			StringBuilder.append("/linkres:\"");
			StringBuilder.append(current3);
			StringBuilder.append("\" ");
		}
		if (options.getTreatWarningsAsErrors()) {
			StringBuilder.append("/warnaserror ");
		}
		if (options.getWarningLevel() >= 0) {
			StringBuilder.append("/w:" + options.getWarningLevel() + " ");
		}
		if (options.getCompilerOptions() != null) {
			StringBuilder.append(options.getCompilerOptions() + " ");
		}
		return StringBuilder.toString();
	}

	private void ContinueOnNewLine(String st) throws Exception {
		this.output.WriteLine(st);
	}

	private String getResponseFileCmdArgs(CompilerParameters options, String cmdArgs) throws Exception {
		String text = options.getTempFiles().AddExtension("cmdline");
		FileStream stream = new FileStream(text, FileMode.Create, FileAccess.Write, FileShare.Read);
		try {
			try (StreamWriter streamWriter = new StreamWriter(stream, Encoding.UTF8)) {
				streamWriter.Write(cmdArgs);
				streamWriter.Flush();
			}
		} finally {
			stream.Close();
		}
		return "/noconfig /fullpaths @\"" + text + "\"";
	}

	private void outputIdentifier(String ident) throws Exception {
		this.output.Write(this.createEscapedIdentifier(ident));
	}

	private void OutputType(CodeTypeReference typeRef) throws Exception {
		this.output.Write(this.getTypeOutput(typeRef));
	}

	private void GenerateArrayCreateExpression(CodeArrayCreateExpression e) throws Exception {
		this.output.Write("new ");
		CodeExpressionCollection initializers = e.getInitializers();
		if (initializers.size() > 0) {
			this.OutputType(e.getCreateType());
			if (e.getCreateType().getArrayRank() == 0) {
				this.output.Write("[]");
			}
			this.output.WriteLine(" {");
			int indent = this.getIndent();
			this.setIndent(indent + 1);
			this.outputExpressionList(initializers, true);
			indent = this.getIndent();
			this.setIndent(indent - 1);
			this.output.Write("}");
			return;
		}
		this.output.Write(this.getBaseTypeOutput(e.getCreateType()));
		this.output.Write("[");
		if (e.getSizeExpression() != null) {
			this.generateExpression(e.getSizeExpression());
		} else {
			this.output.Write(e.getSize());
		}
		this.output.Write("]");
		int nestedArrayDepth = e.getCreateType().getNestedArrayDepth();
		for (int i = 0; i < nestedArrayDepth - 1; i++) {
			this.output.Write("[]");
		}
	}

	private void GenerateBaseReferenceExpression(CodeBaseReferenceExpression e) throws Exception {
		this.output.Write("super");
	}

	private void GenerateBinaryOperatorExpression(CodeBinaryOperatorExpression e) throws Exception {
		boolean flag = false;
		this.output.Write("(");
		this.generateExpression(e.getLeft());
		this.output.Write(" ");
		if (e.getLeft() instanceof CodeBinaryOperatorExpression
				|| e.getRight() instanceof CodeBinaryOperatorExpression) {
			if (!this.inNestedBinary) {
				flag = true;
				this.inNestedBinary = true;
				this.setIndent(this.getIndent() + 3);
			}
			this.ContinueOnNewLine("");
		}
		this.outputOperator(e.getOperator());
		this.output.Write(" ");
		this.generateExpression(e.getRight());
		this.output.Write(")");
		if (flag) {
			this.setIndent(this.getIndent() - 3);
			this.inNestedBinary = false;
		}
	}

	private void GenerateCastExpression(CodeCastExpression e) throws Exception {
		this.output.Write("((");
		this.OutputType(e.getTargetType());
		this.output.Write(")(");
		this.generateExpression(e.getExpression());
		this.output.Write("))");
	}

	public void GenerateCodeFromMember(CodeTypeMember member, TextWriter writer, CodeGeneratorOptions options)
			throws Exception {
		if (this.output != null) {
			throw new Exception(SR.GetString("CodeGenReentrance"));
		}
		this.options = ((options == null) ? new CodeGeneratorOptions() : options);
		this.output = new IndentedTextWriter(writer, this.options.getIndentString());
		try {
			CodeTypeDeclaration declaredType = new CodeTypeDeclaration();
			this.currentClass = declaredType;
			this.generateTypeMember(member, declaredType);
		} finally {
			this.currentClass = null;
			this.output = null;
			this.options = null;
		}
	}

	private void GenerateDefaultValueExpression(CodeDefaultValueExpression e) throws Exception {
		/*
		 * this.output.Write("default("); this.OutputType(e.getType());
		 * this.output.Write(")");
		 */
		this.output.Write("null");
	}

	private void GenerateDelegateCreateExpression(CodeDelegateCreateExpression e) throws Exception {
		this.output.Write("new ");
		this.OutputType(e.getDelegateType());
		this.output.Write("(");
		this.generateExpression(e.getTargetObject());
		this.output.Write(".");
		this.outputIdentifier(e.getMethodName());
		this.output.Write(")");
	}

	private void GenerateEvents(CodeTypeDeclaration e) throws Exception {
		Iterable enumerators = e.getMembers();
		for (Object enumerator : enumerators) {
			if (enumerator instanceof CodeMemberEvent) {
				this.currentMember = (CodeTypeMember) enumerator;
				if (this.options.getBlankLinesBetweenMembers()) {
					this.output.WriteLine();
				}
				if (this.currentMember.getStartDirectives().size() > 0) {
					this.generateDirectives(this.currentMember.getStartDirectives());
				}
				this.generateCommentStatements(this.currentMember.getComments());
				CodeMemberEvent codeMemberEvent = (CodeMemberEvent) enumerator;
				if (codeMemberEvent.getLinePragma() != null) {
					this.generateLinePragmaStart(codeMemberEvent.getLinePragma());
				}
				this.generateEvent(codeMemberEvent, e);
				if (codeMemberEvent.getLinePragma() != null) {
					this.generateLinePragmaEnd(codeMemberEvent.getLinePragma());
				}
				if (this.currentMember.getEndDirectives().size() > 0) {
					this.generateDirectives(this.currentMember.getEndDirectives());
				}
			}
		}
	}

	private void GenerateFields(CodeTypeDeclaration e) throws Exception {
		// IEnumerator enumerator = e.getMembers.GetEnumerator();
		for (Object enumerator : e.getMembers()) {
			if (enumerator instanceof CodeMemberField) {
				this.currentMember = (CodeTypeMember) enumerator;
				if (this.options.getBlankLinesBetweenMembers()) {
					this.output.WriteLine();
				}
				if (this.currentMember.getStartDirectives().size() > 0) {
					this.generateDirectives(this.currentMember.getStartDirectives());
				}
				this.generateCommentStatements(this.currentMember.getComments());
				CodeMemberField codeMemberField = (CodeMemberField) enumerator;
				if (codeMemberField.getLinePragma() != null) {
					this.generateLinePragmaStart(codeMemberField.getLinePragma());
				}
				this.generateField(codeMemberField);
				if (codeMemberField.getLinePragma() != null) {
					this.generateLinePragmaEnd(codeMemberField.getLinePragma());
				}
				if (this.currentMember.getEndDirectives().size() > 0) {
					this.generateDirectives(this.currentMember.getEndDirectives());
				}
			}
		}
	}

	private void GenerateFieldReferenceExpression(CodeFieldReferenceExpression e) throws Exception {
		if (e.getTargetObject() != null) {
			this.generateExpression(e.getTargetObject());
			this.output.Write(".");
		}
		this.outputIdentifier(e.getFieldName());
	}

	private void GenerateArgumentReferenceExpression(CodeArgumentReferenceExpression e) throws Exception {
		this.outputIdentifier(e.getParameterName());
	}

	private void GenerateVariableReferenceExpression(CodeVariableReferenceExpression e) throws Exception {
		this.outputIdentifier(e.getVariableName());
	}

	private void GenerateIndexerExpression(CodeIndexerExpression e) throws Exception {
		this.generateExpression(e.getTargetObject());
		this.output.Write("[");
		boolean flag = true;
		for (Object e2 : e.getIndices()) {
			if (flag) {
				flag = false;
			} else {
				this.output.Write(", ");
			}
			this.generateExpression((CodeExpression) e2);
		}
		this.output.Write("]");
	}

	private void GenerateArrayIndexerExpression(CodeArrayIndexerExpression e) throws Exception {
		this.generateExpression(e.getTargetObject());
		this.output.Write("[");
		boolean flag = true;
		for (Object e2 : e.getIndices()) {
			if (flag) {
				flag = false;
			} else {
				this.output.Write(", ");
			}
			this.generateExpression((CodeExpression) e2);
		}
		this.output.Write("]");
	}

	private void GenerateSnippetCompileUnit(CodeSnippetCompileUnit e) throws Exception {
		this.generateDirectives(e.getStartDirectives());
		if (e.getLinePragma() != null) {
			this.generateLinePragmaStart(e.getLinePragma());
		}
		this.output.WriteLine(e.getValue());
		if (e.getLinePragma() != null) {
			this.generateLinePragmaEnd(e.getLinePragma());
		}
		if (e.getEndDirectives().size() > 0) {
			this.generateDirectives(e.getEndDirectives());
		}
	}

	private void GenerateSnippetExpression(CodeSnippetExpression e) throws Exception {
		this.output.Write(e.getValue());
	}

	private void GenerateMethodInvokeExpression(CodeMethodInvokeExpression e) throws Exception {
		this.GenerateMethodReferenceExpression(e.getMethod());
		this.output.Write("(");
		this.outputExpressionList(e.getParameters());
		this.output.Write(")");
	}

	private void GenerateMethodReferenceExpression(CodeMethodReferenceExpression e) throws Exception {
		if (e.getTargetObject() != null) {
			if (e.getTargetObject() instanceof CodeBinaryOperatorExpression) {
				this.output.Write("(");
				this.generateExpression(e.getTargetObject());
				this.output.Write(")");
			} else {
				this.generateExpression(e.getTargetObject());
			}
			this.output.Write(".");
		}
		this.outputIdentifier(e.getMethodName());
		if (e.getTypeArguments().size() > 0) {
			this.output.Write(this.getTypeArgumentsOutput(e.getTypeArguments()));
		}
	}

	private boolean getUserData(CodeObject e, String property, boolean defaultValue) {
		Object obj = e.getUserData().get(property);
		if (obj != null && obj instanceof Boolean) {
			return (boolean) obj;
		}
		return defaultValue;
	}

	private void generateNamespace(CodeNamespace e) throws Exception {
		this.generateCommentStatements(e.getComments());
		this.generateNamespaceStart(e);
		if (this.getUserData(e, "GenerateImports", true)) {
			this.enerateNamespaceImports(e);
		}
		// TODO fro debug;

		this.output.WriteLine();
		this.generateTypes(e);

		this.generateNamespaceEnd(e);
	}

	private void generateStatement(CodeStatement e) throws Exception {
		if (e.getStartDirectives().size() > 0) {
			this.generateDirectives(e.getStartDirectives());
		}
		if (e.getLinePragma() != null) {
			this.generateLinePragmaStart(e.getLinePragma());
		}
		if (e instanceof CodeCommentStatement) {
			this.generateCommentStatement((CodeCommentStatement) e);
		} else {
			if (e instanceof CodeMethodReturnStatement) {
				this.generateMethodReturnStatement((CodeMethodReturnStatement) e);
			} else {
				if (e instanceof CodeConditionStatement) {
					this.generateConditionStatement((CodeConditionStatement) e);
				} else {
					if (e instanceof CodeTryCatchFinallyStatement) {
						this.generateTryCatchFinallyStatement((CodeTryCatchFinallyStatement) e);
					} else {
						if (e instanceof CodeAssignStatement) {
							this.generateAssignStatement((CodeAssignStatement) e);
						} else {
							if (e instanceof CodeExpressionStatement) {
								this.generateExpressionStatement((CodeExpressionStatement) e);
							} else {
								if (e instanceof CodeIterationStatement) {
									this.generateIterationStatement((CodeIterationStatement) e);
								} else {
									if (e instanceof CodeThrowExceptionStatement) {
										this.generateThrowExceptionStatement((CodeThrowExceptionStatement) e);
									} else {
										if (e instanceof CodeSnippetStatement) {
											// int indent = this.getIndent();
											// this.setIndent(0);
											this.generateSnippetStatement((CodeSnippetStatement) e);
											// this.setIndent(indent);
										} else {
											if (e instanceof CodeVariableDeclarationStatement) {
												this.generateVariableDeclarationStatement(
														(CodeVariableDeclarationStatement) e);
											} else {
												if (e instanceof CodeAttachEventStatement) {
													this.generateAttachEventStatement((CodeAttachEventStatement) e);
												} else {
													if (e instanceof CodeRemoveEventStatement) {
														this.generateRemoveEventStatement((CodeRemoveEventStatement) e);
													} else {
														if (e instanceof CodeGotoStatement) {
															this.generateGotoStatement((CodeGotoStatement) e);
														} else {
															if (!(e instanceof CodeLabeledStatement)) {

																throw new ArgumentException(SR.GetString(
																		"InvalidElementType",
																		new Object[] { e.getClass().getName() }), "e");

															}
															this.generateLabeledStatement((CodeLabeledStatement) e);
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		if (e.getLinePragma() != null) {
			this.generateLinePragmaEnd(e.getLinePragma());
		}
		if (e.getEndDirectives().size() > 0) {
			this.generateDirectives(e.getEndDirectives());
		}
	}

	private void generateStatements(CodeStatementCollection stms) throws Exception, InvalidOperationException {
		// IEnumerator enumerator = stms.GetEnumerator();
		// if(stms==null)return;
		for (Object enumerator : stms) {
			((ICodeGenerator) this).generateCodeFromStatement((CodeStatement) enumerator, this.output.getInnerWriter(),
					this.options);
		}
	}

	private void enerateNamespaceImports(CodeNamespace e) throws Exception {
		// IEnumerator enumerator = e.Imports.GetEnumerator();
		for (Object enumerator : e.getImports()) {
			CodeNamespaceImport codeNamespaceImport = (CodeNamespaceImport) enumerator;
			if (codeNamespaceImport.getLinePragma() != null) {
				this.generateLinePragmaStart(codeNamespaceImport.getLinePragma());
			}
			this.generateNamespaceImport(codeNamespaceImport);
			if (codeNamespaceImport.getLinePragma() != null) {
				this.generateLinePragmaEnd(codeNamespaceImport.getLinePragma());
			}
		}
	}

	private void generateEventReferenceExpression(CodeEventReferenceExpression e) throws Exception {
		if (e.getTargetObject() != null) {
			this.generateExpression(e.getTargetObject());
			this.output.Write(".");
		}
		this.outputIdentifier(e.getEventName());
	}

	private void generateDelegateInvokeExpression(CodeDelegateInvokeExpression e) throws Exception {
		if (e.getTargetObject() != null) {
			this.generateExpression(e.getTargetObject());
		}
		this.output.Write("(");
		this.outputExpressionList(e.getParameters());
		this.output.Write(")");
	}

	private void generateObjectCreateExpression(CodeObjectCreateExpression e) throws Exception {
		this.output.Write("new ");
		this.OutputType(e.getCreateType());
		this.output.Write("(");
		this.outputExpressionList(e.getParameters());
		this.output.Write(")");
	}

	private void generatePrimitiveExpression(CodePrimitiveExpression e) throws Exception {
		if (e.getValue() instanceof Character) {
			this.generatePrimitiveChar((char) e.getValue());
			return;
		}
		if (e.getValue() instanceof Byte) {
			this.output.Write(((Byte) e.getValue()).toString());
			return;
		}
		/*
		 * if (e.getValue() instanceof ) {
		 * this.output.Write(((ushort)e.getValue()).ToString(CultureInfo.
		 * InvariantCulture)); return; } if (e.getValue() instanceof uint) {
		 * this.output.Write(((uint)e.getValue()).ToString(CultureInfo.
		 * InvariantCulture)); this.output.Write("u"); return; }
		 */
		if (e.getValue() instanceof Long) {
			// this.output.Write(((Long)e.getValue()).toString(CultureInfo.InvariantCulture));
			this.output.Write(String.valueOf(e.getValue()));
			this.output.Write("l");
			return;
		}
		this.generatePrimitiveExpressionBase(e);
	}

	private void generatePrimitiveExpressionBase(CodePrimitiveExpression e) throws Exception {
		if (e.getValue() == null) {
			this.output.Write(this.getNullToken());
			return;
		}
		if (e.getValue() instanceof String) {
			this.output.Write(this.QuoteSnippetString((String) e.getValue()));
			return;
		}
		if (e.getValue() instanceof Character) {
			this.output.Write("'" + e.getValue().toString() + "'");
			return;
		}
		if (e.getValue() instanceof Byte) {
			// this.output.Write(((Byte) e.getValue()).toString());
			this.output.Write(String.valueOf(e.getValue()));
			return;
		}
		if (e.getValue() instanceof Short) {
			this.output.Write(String.valueOf(e.getValue()));
			// this.output.Write(((Short)e.getValue()).toString(CultureInfo.InvariantCulture));
			return;
		}
		if (e.getValue() instanceof Integer) {
			this.output.Write(String.valueOf(e.getValue()));
			// this.output.Write(((int)e.getValue()).ToString(CultureInfo.InvariantCulture));
			return;
		}
		if (e.getValue() instanceof Long) {
			this.output.Write(String.valueOf(e.getValue()));
			// this.output.Write(((long)e.getValue()).ToString(CultureInfo.InvariantCulture));
			return;
		}
		/*
		 * if (e.getValue() instanceof float) {
		 * this.GenerateSingleFloatValue((float)e.getValue()); return; }
		 */
		if (e.getValue() instanceof Double) {
			this.generateDoubleValue((Double) e.getValue());
			return;
		}
		/*
		 * if (e.getValue() instanceof decimal) {
		 * this.GenerateDecimalValue((decimal)e.getValue()); return; }
		 */
		if (!(e.getValue() instanceof Boolean)) {
			/*
			 * throw new ArgumentException(SR.GetString("InvalidPrimitiveType",
			 * new object[] { e.getValue().GetType().ToString() }));
			 */
		}
		if ((boolean) e.getValue()) {
			this.output.Write("true");
			return;
		}
		this.output.Write("false");
	}

	private void generatePrimitiveChar(char c) throws Exception {
		this.output.Write('\'');
		if (c > '\'') {
			if (c <= '\u0084') {
				if (c == '\\') {
					this.output.Write("\\\\");
					// goto IL_143;
					this.output.Write('\'');
				}
				if (c != '\u0084') {
					// goto IL_125;
					if (Character.isSurrogate(c)) {
						this.appendEscapedChar(null, c);
					} else {
						this.output.Write(c);
					}
				}
			} else {
				if (c != '\u0085' && c != '\u2028' && c != '\u2029') {
					// goto IL_125;
					if (Character.isSurrogate(c)) {
						this.appendEscapedChar(null, c);
					} else {
						this.output.Write(c);
					}
				}
			}
			this.appendEscapedChar(null, c);
			this.output.Write('\'');
			// goto IL_143;
		}
		if (c <= '\r') {
			if (c == '\0') {
				this.output.Write("\\0");
				this.output.Write('\'');
				// goto IL_143;
			}
			switch (c) {
			case '\t':
				this.output.Write("\\t");
				this.output.Write('\'');
				// goto IL_143;
			case '\n':
				this.output.Write("\\n");
				this.output.Write('\'');
				// goto IL_143;
			case '\r':
				this.output.Write("\\r");
				// goto IL_143;
				this.output.Write('\'');
			}
		} else {
			if (c == '"') {
				this.output.Write("\\\"");
				// goto IL_143;
				this.output.Write('\'');
			}
			if (c == '\'') {
				this.output.Write("\\'");
				this.output.Write('\'');
				// goto IL_143;
			}
		}

	}

	private void appendEscapedChar(StringBuilder b, char value) throws Exception {
		int num;
		if (b == null) {
			this.output.Write("\\u");
			// TextWriter arg_2C_0 = this.output;
			num = (int) value;
			this.output.Write(String.valueOf(num));
			// arg_2C_0.Write(num.toString("X4", CultureInfo.InvariantCulture));
			return;
		}
		b.append("\\u");
		num = (int) value;
		// b.Append(num.ToString("X4", CultureInfo.InvariantCulture));
		b.append(String.valueOf(num));
	}

	private void generatePropertySetValueReferenceExpression(CodePropertySetValueReferenceExpression e)
			throws Exception {
		this.output.Write("value");
	}

	private void generateThisReferenceExpression(CodeThisReferenceExpression e) throws Exception {
		this.output.Write("this");
	}

	private void generateExpressionStatement(CodeExpressionStatement e) throws Exception {
		this.generateExpression(e.getExpression());
		if (!this.generatingForLoop) {
			this.output.WriteLine(";");
		}
	}

	private void generateIterationStatement(CodeIterationStatement e) throws Exception, Exception {
		this.generatingForLoop = true;
		this.output.Write("for (");
		this.generateStatement(e.getInitStatement());
		this.output.Write("; ");
		this.generateExpression(e.getTestExpression());
		this.output.Write("; ");
		this.generateStatement(e.getIncrementStatement());
		this.output.Write(")");
		this.outputStartingBrace();
		this.generatingForLoop = false;
		int indent = this.getIndent();
		this.setIndent(indent + 1);
		this.generateStatements(e.getStatements());
		indent = this.getIndent();
		this.setIndent(indent - 1);
		this.output.WriteLine("}");
	}

	private void generateThrowExceptionStatement(CodeThrowExceptionStatement e) throws Exception {
		this.output.Write("throw");
		if (e.getToThrow() != null) {
			this.output.Write(" ");
			this.generateExpression(e.getToThrow());
		}
		this.output.WriteLine(";");
	}

	private void generateComment(CodeComment e) throws Exception {
		String value = e.getDocComment() ? "///" : "//";
		this.output.Write(value);
		this.output.Write(" ");
		String text = e.getText();
		for (int i = 0; i < text.length(); i++) {
			if (text.charAt(i) != '\0') {
				this.output.Write(text.charAt(i));
				if (text.charAt(i) == '\r') {
					if (i < text.length() - 1 && text.charAt(i + 1) == '\n') {
						this.output.Write('\n');
						i++;
					}
					((IndentedTextWriter) this.output).InternalOutputTabs();
					this.output.Write(value);
				} else {
					if (text.charAt(i) == '\n') {
						((IndentedTextWriter) this.output).InternalOutputTabs();
						this.output.Write(value);
					} else {
						if (text.charAt(i) == '\u2028' || text.charAt(i) == '\u2029' || text.charAt(i) == '\u0085') {
							this.output.Write(value);
						}
					}
				}
			}
		}
		this.output.WriteLine();
	}

	private void generateCommentStatement(CodeCommentStatement e) throws Exception {
		if (e.getComment() == null) {

			throw new ArgumentException(SR.GetString("Argument_NullComment", new Object[] { "e" }), "e");
		}
		this.generateComment(e.getComment());
	}

	private void generateCommentStatements(CodeCommentStatementCollection e) throws Exception {
		for (Object e2 : e) {
			this.generateCommentStatement((CodeCommentStatement) e2);
		}
	}

	private void generateMethodReturnStatement(CodeMethodReturnStatement e) throws Exception {
		this.output.Write("return");
		if (e.getExpression() != null) {
			this.output.Write(" ");
			this.generateExpression(e.getExpression());
		}
		this.output.WriteLine(";");
	}

	private void generateConditionStatement(CodeConditionStatement e) throws Exception, InvalidOperationException {
		this.output.Write("if (");
		this.generateExpression(e.getCondition());
		this.output.Write(")");
		this.outputStartingBrace();
		int indent = this.getIndent();
		this.setIndent(indent + 1);
		this.generateStatements(e.getTrueStatements());
		indent = this.getIndent();
		this.setIndent(indent - 1);
		if (e.getFalseStatements().size() > 0) {
			this.output.Write("}");
			if (this.getOptions().getElseOnClosing()) {
				this.output.Write(" ");
			} else {
				this.output.WriteLine("");
			}
			this.output.Write("else");
			this.outputStartingBrace();
			indent = this.getIndent();
			this.setIndent(indent + 1);
			this.generateStatements(e.getFalseStatements());
			indent = this.getIndent();
			this.setIndent(indent - 1);
		}
		this.output.WriteLine("//----if else end----------");
		this.output.WriteLine("}");
	}

	private void generateTryCatchFinallyStatement(CodeTryCatchFinallyStatement e)
			throws Exception, InvalidOperationException {
		this.output.Write("try");
		this.outputStartingBrace();
		int indent = this.getIndent();
		this.setIndent(indent + 1);
		this.generateStatements(e.getTryStatements());
		indent = this.getIndent();
		this.setIndent(indent - 1);
		CodeCatchClauseCollection catchClauses = e.getCatchClauses();
		if (catchClauses.size() > 0) {
			// IEnumerator enumerator = catchClauses.GetEnumerator();
			for (Object enumerator : catchClauses) {
				this.output.Write("}");
				if (this.options.getElseOnClosing()) {
					this.output.Write(" ");
				} else {
					this.output.WriteLine("");
				}
				CodeCatchClause codeCatchClause = (CodeCatchClause) enumerator;
				this.output.Write("catch (");
				this.OutputType(codeCatchClause.getCatchExceptionType());
				this.output.Write(" ");
				this.outputIdentifier(codeCatchClause.getLocalName());
				this.output.Write(")");
				this.outputStartingBrace();
				indent = this.getIndent();
				this.setIndent(indent + 1);
				this.generateStatements(codeCatchClause.getStatements());
				indent = this.getIndent();
				this.setIndent(indent - 1);
			}
		}
		CodeStatementCollection finallyStatements = e.getFinallyStatements();
		if (finallyStatements.size() > 0) {
			this.output.Write("}");
			if (this.options.getElseOnClosing()) {
				this.output.Write(" ");
			} else {
				this.output.WriteLine("");
			}
			this.output.Write("finally");
			this.outputStartingBrace();
			indent = this.getIndent();
			this.setIndent(indent + 1);
			this.generateStatements(finallyStatements);
			indent = this.getIndent();
			this.setIndent(indent - 1);
		}
		this.output.WriteLine("}");
	}

	private void generateAssignStatement(CodeAssignStatement e) throws Exception {
		this.generateExpression(e.getLeft());
		this.output.Write(" = ");
		this.generateExpression(e.getRight());
		if (!this.generatingForLoop) {
			this.output.WriteLine(";");
		}
	}

	private void generateAttachEventStatement(CodeAttachEventStatement e) throws Exception {
		this.generateEventReferenceExpression(e.getEvent());
		this.output.Write(" += ");
		this.generateExpression(e.getListener());
		this.output.WriteLine(";");
	}

	private void generateRemoveEventStatement(CodeRemoveEventStatement e) throws Exception {
		this.generateEventReferenceExpression(e.getEvent());
		this.output.Write(" -= ");
		this.generateExpression(e.getListener());
		this.output.WriteLine(";");
	}

	private void generateSnippetStatement(CodeSnippetStatement e) throws Exception {
		this.output.WriteLine(e.getValue());
	}

	private void generateGotoStatement(CodeGotoStatement e) throws Exception {
		this.output.Write("goto ");
		this.output.Write(e.getLabel());
		this.output.WriteLine(";");
	}

	private void generateLabeledStatement(CodeLabeledStatement e) throws Exception {
		int indent = this.getIndent();
		this.setIndent(indent - 1);
		this.output.Write(e.getLabel());
		this.output.WriteLine(":");
		indent = this.getIndent();
		this.setIndent(indent + 1);
		if (e.getStatement() != null) {
			this.generateStatement(e.getStatement());
		}
	}

	private void generateVariableDeclarationStatement(CodeVariableDeclarationStatement e) throws Exception {
		this.outputTypeNamePair(e.getType(), e.getName());
		if (e.getInitExpression() != null) {
			this.output.Write(" = ");
			this.generateExpression(e.getInitExpression());
		}
		if (!this.generatingForLoop) {
			this.output.WriteLine(";");
		}
	}

	private void generateLinePragmaStart(CodeLinePragma e) throws Exception {
		this.output.WriteLine("");
		this.output.Write("#line ");
		this.output.Write(e.getLineNumber());
		this.output.Write(" \"");
		this.output.Write(e.getFileName());
		this.output.Write("\"");
		this.output.WriteLine("");
	}

	private void generateLinePragmaEnd(CodeLinePragma e) throws Exception {
		this.output.WriteLine();
		this.output.WriteLine("//#line default");
		this.output.WriteLine("//#line hidden");
	}

	private void generateEvent(CodeMemberEvent e, CodeTypeDeclaration c) throws Exception {
		if (this.getIsCurrentDelegate() || this.getIsCurrentEnum()) {
			return;
		}
		if (e.getCustomAttributes().size() > 0) {
			this.generateAttributes(e.getCustomAttributes());
		}
		if (e.getPrivateImplementationType() == null) {
			this.outputMemberAccessModifier(e.getAttributes());
		}
		this.output.Write("event ");
		String text = e.getName();
		if (e.getPrivateImplementationType() != null) {
			text = this.getBaseTypeOutput(e.getPrivateImplementationType()) + "." + text;
		}
		this.outputTypeNamePair(e.getType(), text);
		this.output.WriteLine(";");
	}

	private void generateExpression(CodeExpression e) throws Exception {
		if (e instanceof CodeArrayCreateExpression) {
			this.GenerateArrayCreateExpression((CodeArrayCreateExpression) e);
			return;
		}
		if (e instanceof CodeBaseReferenceExpression) {
			this.GenerateBaseReferenceExpression((CodeBaseReferenceExpression) e);
			return;
		}
		if (e instanceof CodeBinaryOperatorExpression) {
			this.GenerateBinaryOperatorExpression((CodeBinaryOperatorExpression) e);
			return;
		}
		if (e instanceof CodeCastExpression) {
			this.GenerateCastExpression((CodeCastExpression) e);
			return;
		}
		if (e instanceof CodeDelegateCreateExpression) {
			this.GenerateDelegateCreateExpression((CodeDelegateCreateExpression) e);
			return;
		}
		if (e instanceof CodeFieldReferenceExpression) {
			this.GenerateFieldReferenceExpression((CodeFieldReferenceExpression) e);
			return;
		}
		if (e instanceof CodeArgumentReferenceExpression) {
			this.GenerateArgumentReferenceExpression((CodeArgumentReferenceExpression) e);
			return;
		}
		if (e instanceof CodeVariableReferenceExpression) {
			this.GenerateVariableReferenceExpression((CodeVariableReferenceExpression) e);
			return;
		}
		if (e instanceof CodeIndexerExpression) {
			this.GenerateIndexerExpression((CodeIndexerExpression) e);
			return;
		}
		if (e instanceof CodeArrayIndexerExpression) {
			this.GenerateArrayIndexerExpression((CodeArrayIndexerExpression) e);
			return;
		}
		if (e instanceof CodeSnippetExpression) {
			this.GenerateSnippetExpression((CodeSnippetExpression) e);
			return;
		}
		if (e instanceof CodeMethodInvokeExpression) {
			this.GenerateMethodInvokeExpression((CodeMethodInvokeExpression) e);
			return;
		}
		if (e instanceof CodeMethodReferenceExpression) {
			this.GenerateMethodReferenceExpression((CodeMethodReferenceExpression) e);
			return;
		}
		if (e instanceof CodeEventReferenceExpression) {
			this.generateEventReferenceExpression((CodeEventReferenceExpression) e);
			return;
		}
		if (e instanceof CodeDelegateInvokeExpression) {
			this.generateDelegateInvokeExpression((CodeDelegateInvokeExpression) e);
			return;
		}
		if (e instanceof CodeObjectCreateExpression) {
			this.generateObjectCreateExpression((CodeObjectCreateExpression) e);
			return;
		}
		if (e instanceof CodeParameterDeclarationExpression) {
			this.generateParameterDeclarationExpression((CodeParameterDeclarationExpression) e);
			return;
		}
		if (e instanceof CodeDirectionExpression) {
			this.generateDirectionExpression((CodeDirectionExpression) e);
			return;
		}
		if (e instanceof CodePrimitiveExpression) {
			this.generatePrimitiveExpression((CodePrimitiveExpression) e);
			return;
		}
		if (e instanceof CodePropertyReferenceExpression) {
			this.generatePropertyReferenceExpression((CodePropertyReferenceExpression) e);
			return;
		}
		if (e instanceof CodePropertySetValueReferenceExpression) {
			this.generatePropertySetValueReferenceExpression((CodePropertySetValueReferenceExpression) e);
			return;
		}
		if (e instanceof CodeThisReferenceExpression) {
			this.generateThisReferenceExpression((CodeThisReferenceExpression) e);
			return;
		}
		if (e instanceof CodeTypeReferenceExpression) {
			this.generateTypeReferenceExpression((CodeTypeReferenceExpression) e);
			return;
		}
		if (e instanceof CodeTypeOfExpression) {
			this.generateTypeOfExpression((CodeTypeOfExpression) e);
			return;
		}
		if (e instanceof CodeDefaultValueExpression) {
			this.GenerateDefaultValueExpression((CodeDefaultValueExpression) e);
			return;
		}
		if (e == null) {

			throw new ArgumentNullException("e");
		}

		throw new ArgumentException(SR.GetString("InvalidElementType", new Object[] { e.getClass().getName() }), "e");

	}

	private void generateField(CodeMemberField e) throws Exception {
		if (this.getIsCurrentDelegate() || this.getIsCurrentInterface()) {
			return;
		}
		if (this.getIsCurrentEnum()) {
			if (e.getCustomAttributes().size() > 0) {
				this.generateAttributes(e.getCustomAttributes());
			}
			this.outputIdentifier(e.getName());
			if (e.getInitExpression() != null) {
				this.output.Write(" = ");
				this.generateExpression(e.getInitExpression());
			}
			this.output.WriteLine(",");
			return;
		}
		if (e.getCustomAttributes().size() > 0) {
			this.generateAttributes(e.getCustomAttributes());
		}
		this.outputMemberAccessModifier(e.getAttributes());
		this.outputVTableModifier(e.getAttributes());
		this.outputFieldScopeModifier(e.getAttributes());
		this.outputTypeNamePair(e.getType(), e.getName());
		if (e.getInitExpression() != null) {
			this.output.Write(" = ");
			this.generateExpression(e.getInitExpression());
		}
		this.output.WriteLine(";");
	}

	private void generateSnippetMember(CodeSnippetTypeMember e) throws Exception {
		this.output.Write(e.getText());
	}

	private void generateParameterDeclarationExpression(CodeParameterDeclarationExpression e) throws Exception {
		if (e.getCustomAttributes().size() > 0) {
			this.generateAttributes(e.getCustomAttributes(), null, true);
		}
		this.outputDirection(e.getDirection());
		this.outputTypeNamePair(e.getType(), e.getName());
	}

	private void generateEntryPointMethod(CodeEntryPointMethod e, CodeTypeDeclaration c) throws Exception, Exception {
		if (e.getCustomAttributes().size() > 0) {
			this.generateAttributes(e.getCustomAttributes());
		}
		this.output.Write("public static ");
		this.OutputType(e.getReturnType());
		this.output.Write(" Main()");
		this.outputStartingBrace();
		int indent = this.getIndent();
		this.setIndent(indent + 1);
		this.generateStatements(e.getStatements());
		indent = this.getIndent();
		this.setIndent(indent - 1);
		this.output.WriteLine("}");
	}

	private void generateMethods(CodeTypeDeclaration e) throws Exception {
		// IEnumerator enumerator = e.Members.GetEnumerator();
		for (Object enumerator : e.getMembers()) {
			if (enumerator instanceof CodeMemberMethod && !(enumerator instanceof CodeTypeConstructor)
					&& !(enumerator instanceof CodeConstructor)) {
				this.currentMember = (CodeTypeMember) enumerator;
				if (this.options.getBlankLinesBetweenMembers()) {
					this.output.WriteLine();
				}
				if (this.currentMember.getStartDirectives().size() > 0) {
					this.generateDirectives(this.currentMember.getStartDirectives());
				}
				this.generateCommentStatements(this.currentMember.getComments());
				CodeMemberMethod codeMemberMethod = (CodeMemberMethod) enumerator;
				if (codeMemberMethod.getLinePragma() != null) {
					this.generateLinePragmaStart(codeMemberMethod.getLinePragma());
				}
				if (enumerator instanceof CodeEntryPointMethod) {
					this.generateEntryPointMethod((CodeEntryPointMethod) enumerator, e);
				} else {
					this.generateMethod(codeMemberMethod, e);
				}
				if (codeMemberMethod.getLinePragma() != null) {
					this.generateLinePragmaEnd(codeMemberMethod.getLinePragma());
				}
				if (this.currentMember.getEndDirectives().size() > 0) {
					this.generateDirectives(this.currentMember.getEndDirectives());
				}
			}
		}
	}

	private void generateMethod(CodeMemberMethod e, CodeTypeDeclaration c) throws Exception, InvalidOperationException {
		if (!this.getIsCurrentClass() && !this.getIsCurrentStruct() && !this.getIsCurrentInterface()) {
			return;
		}
		if (e.getCustomAttributes().size() > 0) {
			this.generateAttributes(e.getCustomAttributes());
		}
		if (e.getReturnTypeCustomAttributes().size() > 0) {
			this.generateAttributes(e.getReturnTypeCustomAttributes(), "return: ");
		}
		if (!this.getIsCurrentInterface()) {
			this.outPutOverride(e.getAttributes());
			if (e.getPrivateImplementationType() == null) {
				this.outputMemberAccessModifier(e.getAttributes());
				// this.OutputVTableModifier(e.getAttributes());
				this.outputMemberScopeModifier(e.getAttributes());
			}
		} /*
			 * else { //this.OutputVTableModifier(e.getAttributes()); }
			 */
		this.OutputType(e.getReturnType());
		this.output.Write(" ");
		if (e.getPrivateImplementationType() != null) {
			this.output.Write(this.getBaseTypeOutput(e.getPrivateImplementationType()));
			this.output.Write(".");
		}
		this.outputIdentifier(e.getName());
		this.outputTypeParameters(e.getTypeParameters());
		this.output.Write("(");
		this.outputParameters(e.getParameters());
		this.output.Write(")");
		this.outputTypeParameterConstraints(e.getTypeParameters());
		if (!this.getIsCurrentInterface()
				&& (e.getAttributes().getValue() & MemberAttributes.ScopeMask) != MemberAttributes.Abstract) {
			this.outputStartingBrace();
			int indent = this.getIndent();
			this.setIndent(indent + 1);
			this.generateStatements(e.getStatements());
			indent = this.getIndent();
			this.setIndent(indent - 1);
			this.output.WriteLine("}");
			return;
		}
		this.output.WriteLine(";");
	}

	private void outPutOverride(MemberAttributes attributes) throws IOException {

		if ((attributes.getValue() & MemberAttributes.ScopeMask) == MemberAttributes.Override) {
			this.output.WriteLine("@Override ");
		}

	}

	private void generateProperties(CodeTypeDeclaration e) throws Exception, Exception {
		// IEnumerator enumerator = e.Members.GetEnumerator();
		for (Object enumerator : e.getMembers()) {
			if (enumerator instanceof CodeMemberProperty) {
				this.currentMember = (CodeTypeMember) enumerator;
				if (this.options.getBlankLinesBetweenMembers()) {
					this.output.WriteLine();
				}
				if (this.currentMember.getStartDirectives().size() > 0) {
					this.generateDirectives(this.currentMember.getStartDirectives());
				}
				this.generateCommentStatements(this.currentMember.getComments());
				CodeMemberProperty codeMemberProperty = (CodeMemberProperty) enumerator;
				if (codeMemberProperty.getLinePragma() != null) {
					this.generateLinePragmaStart(codeMemberProperty.getLinePragma());
				}
				this.generateProperty(codeMemberProperty, e);
				if (codeMemberProperty.getLinePragma() != null) {
					this.generateLinePragmaEnd(codeMemberProperty.getLinePragma());
				}
				if (this.currentMember.getEndDirectives().size() > 0) {
					this.generateDirectives(this.currentMember.getEndDirectives());
				}
			}
		}
	}

	private void generateProperty(CodeMemberProperty e, CodeTypeDeclaration c)
			throws Exception, InvalidOperationException {
		if (!this.getIsCurrentClass() && !this.getIsCurrentStruct() && !this.getIsCurrentInterface()) {
			return;
		}
		if (e.getCustomAttributes().size() > 0) {
			this.generateAttributes(e.getCustomAttributes());
		}
		if (!this.getIsCurrentInterface()) {
			if (e.getPrivateImplementationType() == null) {
				this.outputMemberAccessModifier(e.getAttributes());
				this.outputVTableModifier(e.getAttributes());
				this.outputMemberScopeModifier(e.getAttributes());
			}
		} else {
			this.outputVTableModifier(e.getAttributes());
		}
		this.OutputType(e.getType());
		this.output.Write(" ");
		if (e.getPrivateImplementationType() != null && !this.getIsCurrentInterface()) {
			this.output.Write(this.getBaseTypeOutput(e.getPrivateImplementationType()));
			this.output.Write(".");
		}
		if (e.getParameters().size() > 0
				&& StringHelper.Compare(e.getName(), "Item", StringComparison.OrdinalIgnoreCase) == 0) {
			this.output.Write("this[");
			this.outputParameters(e.getParameters());
			this.output.Write("]");
		} else {
			this.outputIdentifier(e.getName());
		}
		this.outputStartingBrace();
		int indent = this.getIndent();
		this.setIndent(indent + 1);
		if (e.getHasGet()) {
			if (this.getIsCurrentInterface()
					|| (e.getAttributes().getValue() & MemberAttributes.ScopeMask) == MemberAttributes.Abstract) {
				this.output.WriteLine("get;");
			} else {
				this.output.Write("get");
				this.outputStartingBrace();
				indent = this.getIndent();
				this.setIndent(indent + 1);
				this.generateStatements(e.getGetStatements());
				indent = this.getIndent();
				this.setIndent(indent - 1);
				this.output.WriteLine("}");
			}
		}
		if (e.getHasSet()) {
			if (this.getIsCurrentInterface()
					|| (e.getAttributes().getValue() & MemberAttributes.ScopeMask) == MemberAttributes.Abstract) {
				this.output.WriteLine("set;");
			} else {
				this.output.Write("set");
				this.outputStartingBrace();
				indent = this.getIndent();
				this.setIndent(indent + 1);
				this.generateStatements(e.getSetStatements());
				indent = this.getIndent();
				this.setIndent(indent - 1);
				this.output.WriteLine("}");
			}
		}
		indent = this.getIndent();
		this.setIndent(indent - 1);
		this.output.WriteLine("}");
	}

	private void generateSingleFloatValue(float s) throws Exception {

		if (Float.isNaN(s)) {
			this.output.Write("float.NaN");
			return;
		}
		if (Float.isFinite(s)) {
			this.output.Write("float.NegativeInfinity");
			return;
		}
		if (Float.isInfinite(s)) {
			this.output.Write("float.PositiveInfinity");
			return;
		}
		this.output.Write(String.valueOf(s));
		this.output.Write('F');
	}

	private void generateDoubleValue(double d) throws Exception {
		if (Double.isNaN(d)) {
			this.output.Write("double.NaN");
			return;
		}
		if (Double.isFinite(d)) {
			this.output.Write("double.NegativeInfinity");
			return;
		}
		if (Double.isInfinite(d)) {
			this.output.Write("double.PositiveInfinity");
			return;
		}
		// this.output.Write(d.ToString("R", CultureInfo.InvariantCulture));
		this.output.Write(String.valueOf(d));
		this.output.Write("D");
	}

	private void generateDecimalValue(BigDecimal d) throws Exception {
		this.output.Write(String.valueOf(d));
		this.output.Write('m');
	}

	private void outputVTableModifier(MemberAttributes attributes) throws Exception {
		int memberAttributes = attributes == null ? 0 : attributes.getValue() & MemberAttributes.VTableMask;
		if (memberAttributes == MemberAttributes.New) {
			this.output.Write("new ");
		}
	}

	private void outputMemberAccessModifier(MemberAttributes attributes) throws Exception {
		/*
		 * if(attributes==null){ //this.output.Write("public "); return; }
		 */
		int memberAttributes = (attributes == null ? 0 : attributes.getValue() & MemberAttributes.AccessMask);
		if (memberAttributes <= MemberAttributes.Family) {
			if (memberAttributes == MemberAttributes.Assembly) {
				this.output.Write(" ");
				return;
			}
			if (memberAttributes == MemberAttributes.FamilyAndAssembly) {
				this.output.Write(" ");
				return;
			}
			if (memberAttributes != MemberAttributes.Family) {
				return;
			}
			this.output.Write("protected ");
			return;
		} else {
			if (memberAttributes == MemberAttributes.FamilyOrAssembly) {
				this.output.Write("protected  ");
				return;
			}
			if (memberAttributes == MemberAttributes.Private) {
				this.output.Write("private ");
				return;
			}
			if (memberAttributes != MemberAttributes.Public) {
				return;
			}
			this.output.Write("public ");
			return;
		}
	}

	private void outputMemberScopeModifier(MemberAttributes attributes) throws Exception {
		switch (((attributes == null ? 0 : attributes.getValue()) & MemberAttributes.ScopeMask)) {
		case MemberAttributes.Abstract:
			this.output.Write("abstract ");
			return;
		case MemberAttributes.Final:
			this.output.Write("final ");
			return;
		case MemberAttributes.Static:
			this.output.Write("static ");
			return;
		case MemberAttributes.Override:
			this.output.Write(" ");
			return;
		default: {
			MemberAttributes memberAttributes = MemberAttributes
					.forValue(attributes.getValue() & MemberAttributes.AccessMask);
			if (memberAttributes.getValue() == MemberAttributes.Assembly
					|| memberAttributes.getValue() == MemberAttributes.Family
					|| memberAttributes.getValue() == MemberAttributes.Public) {
				this.output.Write(" ");
			}
			return;
		}
		}
	}

	private void outputOperator(CodeBinaryOperatorType op) throws Exception {
		switch (op) {
		case Add:
			this.output.Write("+");
			return;
		case Subtract:
			this.output.Write("-");
			return;
		case Multiply:
			this.output.Write("*");
			return;
		case Divide:
			this.output.Write("/");
			return;
		case Modulus:
			this.output.Write("%");
			return;
		case Assign:
			this.output.Write("=");
			return;
		case IdentityInequality:
			this.output.Write("!=");
			return;
		case IdentityEquality:
			this.output.Write("==");
			return;
		case ValueEquality:
			this.output.Write("==");
			return;
		case BitwiseOr:
			this.output.Write("|");
			return;
		case BitwiseAnd:
			this.output.Write("&");
			return;
		case BooleanOr:
			this.output.Write("||");
			return;
		case BooleanAnd:
			this.output.Write("&&");
			return;
		case LessThan:
			this.output.Write("<");
			return;
		case LessThanOrEqual:
			this.output.Write("<=");
			return;
		case GreaterThan:
			this.output.Write(">");
			return;
		case GreaterThanOrEqual:
			this.output.Write(">=");
			return;
		default:
			return;
		}
	}

	private void outputFieldScopeModifier(MemberAttributes attributes) throws Exception {
		switch ((attributes.getValue() & MemberAttributes.ScopeMask)) {
		case MemberAttributes.Final:
		case MemberAttributes.Override:
			break;
		case MemberAttributes.Static:
			this.output.Write("static ");
			return;
		case MemberAttributes.Const:
			this.output.Write("const ");
			break;
		default:
			return;
		}
	}

	private void generatePropertyReferenceExpression(CodePropertyReferenceExpression e) throws Exception {
		if (e.getTargetObject() != null) {
			this.generateExpression(e.getTargetObject());
			this.output.Write(".");
		}
		this.outputIdentifier(e.getPropertyName());
	}

	private void generateConstructors(CodeTypeDeclaration e) throws Exception, Exception {
		// IEnumerator enumerator = e.Members.GetEnumerator();
		for (Object enumerator : e.getMembers()) {
			if (enumerator instanceof CodeConstructor) {
				this.currentMember = (CodeTypeMember) enumerator;
				if (this.options.getBlankLinesBetweenMembers()) {
					this.output.WriteLine();
				}
				if (this.currentMember.getStartDirectives().size() > 0) {
					this.generateDirectives(this.currentMember.getStartDirectives());
				}
				this.generateCommentStatements(this.currentMember.getComments());
				CodeConstructor codeConstructor = (CodeConstructor) enumerator;
				if (codeConstructor.getLinePragma() != null) {
					this.generateLinePragmaStart(codeConstructor.getLinePragma());
				}
				this.generateConstructor(codeConstructor, e);
				if (codeConstructor.getLinePragma() != null) {
					this.generateLinePragmaEnd(codeConstructor.getLinePragma());
				}
				if (this.currentMember.getEndDirectives().size() > 0) {
					this.generateDirectives(this.currentMember.getEndDirectives());
				}
			}
		}
	}

	private void generateConstructor(CodeConstructor e, CodeTypeDeclaration c)
			throws Exception, InvalidOperationException {
		if (!this.getIsCurrentClass() && !this.getIsCurrentStruct()) {
			return;
		}
		if (e.getCustomAttributes().size() > 0) {
			this.generateAttributes(e.getCustomAttributes());
		}
		this.outputMemberAccessModifier(e.getAttributes());
		this.outputIdentifier(this.getCurrentTypeName());
		this.output.Write("(");
		this.outputParameters(e.getParameters());
		this.output.Write(")");
		CodeExpressionCollection baseConstructorArgs = e.getBaseConstructorArgs();
		CodeExpressionCollection chainedConstructorArgs = e.getChainedConstructorArgs();
		int indent;
		if (baseConstructorArgs.size() > 0) {
			this.output.WriteLine(" : ");
			indent = this.getIndent();
			this.setIndent(indent + 1);
			indent = this.getIndent();
			this.setIndent(indent + 1);
			this.output.Write("base(");
			this.outputExpressionList(baseConstructorArgs);
			this.output.Write(")");
			indent = this.getIndent();
			this.setIndent(indent - 1);
			indent = this.getIndent();
			this.setIndent(indent - 1);
		}
		if (chainedConstructorArgs.size() > 0) {
			this.output.WriteLine(" : ");
			indent = this.getIndent();
			this.setIndent(indent + 1);
			indent = this.getIndent();
			this.setIndent(indent + 1);
			this.output.Write("this(");
			this.outputExpressionList(chainedConstructorArgs);
			this.output.Write(")");
			indent = this.getIndent();
			this.setIndent(indent - 1);
			indent = this.getIndent();
			this.setIndent(indent - 1);
		}
		this.outputStartingBrace();
		indent = this.getIndent();
		this.setIndent(indent + 1);
		this.generateStatements(e.getStatements());
		indent = this.getIndent();
		this.setIndent(indent - 1);
		this.output.WriteLine("}");
	}

	private void generateTypeConstructor(CodeTypeConstructor e) throws Exception, InvalidOperationException {
		if (!this.getIsCurrentClass() && !this.getIsCurrentStruct()) {
			return;
		}
		if (e.getCustomAttributes().size() > 0) {
			this.generateAttributes(e.getCustomAttributes());
		}
		this.output.Write("static ");
		this.output.Write(this.getCurrentTypeName());
		this.output.Write("()");
		this.outputStartingBrace();
		int indent = this.getIndent();
		this.setIndent(indent + 1);
		this.generateStatements(e.getStatements());
		indent = this.getIndent();
		this.setIndent(indent - 1);
		this.output.WriteLine("}");
	}

	private void generateTypeReferenceExpression(CodeTypeReferenceExpression e) throws Exception {
		this.OutputType(e.getType());
	}

	private void generateTypeOfExpression(CodeTypeOfExpression e) throws Exception {
		this.output.Write("typeof(");
		this.OutputType(e.getType());
		this.output.Write(")");
	}

	private void generateType(CodeTypeDeclaration e) throws Exception {
		this.currentClass = e;
		if (e.getStartDirectives().size() > 0) {
			this.generateDirectives(e.getStartDirectives());
		}
		this.generateCommentStatements(e.getComments());
		if (e.getLinePragma() != null) {
			this.generateLinePragmaStart(e.getLinePragma());
		}
		this.generateTypeStart(e);
		if (this.getOptions().getVerbatimOrder()) {
			// IEnumerator enumerator = e.Members.GetEnumerator();
			CodeTypeMemberCollection collection = e.getMembers();
			try {
				for (Object enumerator : collection) {
					CodeTypeMember member = (CodeTypeMember) enumerator;
					this.generateTypeMember(member, e);
				}
				this.currentClass = e;
				this.generateTypeEnd(e);
				if (e.getLinePragma() != null) {
					this.generateLinePragmaEnd(e.getLinePragma());
				}
				if (e.getEndDirectives().size() > 0) {
					this.generateDirectives(e.getEndDirectives());
				}
				return;
			} finally {
				/*
				 * if(collection!=null) for(Object e)
				 */
				/*
				 * IDisposable disposable = enumerator as IDisposable;
				 * 
				 * if (disposable != null) { disposable.Dispose(); }
				 */

			}
		}
		this.GenerateFields(e);
		this.generateSnippetMembers(e);
		this.generateTypeConstructors(e);
		this.generateConstructors(e);
		this.generateProperties(e);
		this.GenerateEvents(e);
		this.generateMethods(e);
		this.generateNestedTypes(e);
		// IL_CA:
		this.currentClass = e;
		this.generateTypeEnd(e);
		if (e.getLinePragma() != null) {
			this.generateLinePragmaEnd(e.getLinePragma());
		}
		if (e.getEndDirectives().size() > 0) {
			this.generateDirectives(e.getEndDirectives());
		}
	}

	private void generateTypes(CodeNamespace e) throws Exception, Exception {
		for (Object e2 : e.getTypes()) {
			if (this.options.getBlankLinesBetweenMembers()) {
				this.output.WriteLine();
			}
			((ICodeGenerator) this).generateCodeFromType((CodeTypeDeclaration) e2, this.output.getInnerWriter(),
					this.options);
		}
	}

	private void generateTypeStart(CodeTypeDeclaration e) throws Exception {
		if (e.getCustomAttributes().size() > 0) {
			this.generateAttributes(e.getCustomAttributes());
		}
		if (this.getIsCurrentDelegate()) {
			TypeAttributes typeAttributes = TypeAttributes
					.forValue(e.getTypeAttributes().getValue() & TypeAttributes.VisibilityMask);
			if (typeAttributes.getValue() != TypeAttributes.NotPublic
					&& typeAttributes.getValue() == TypeAttributes.Public) {
				this.output.Write("public ");
			}
			CodeTypeDelegate codeTypeDelegate = (CodeTypeDelegate) e;
			this.output.Write("delegate ");
			this.OutputType(codeTypeDelegate.getReturnType());
			this.output.Write(" ");
			this.outputIdentifier(e.getName());
			this.output.Write("(");
			this.outputParameters(codeTypeDelegate.getParameters());
			this.output.WriteLine(");");
			return;
		}
		this.outputTypeAttributes(e);
		this.outputIdentifier(e.getName());
		this.outputTypeParameters(e.getTypeParameters());
		boolean flag = true;
		for (Object typeRef : e.getBaseTypes()) {
			if (flag) {
				this.output.Write(" extends ");
				flag = false;
			} else {
				this.output.Write(", ");
			}
			this.OutputType((CodeTypeReference) typeRef);
		}
		this.outputTypeParameterConstraints(e.getTypeParameters());
		this.outputStartingBrace();
		int indent = this.getIndent();
		this.setIndent(indent + 1);
	}

	private void generateTypeMember(CodeTypeMember member, CodeTypeDeclaration declaredType) throws Exception {
		if (this.options.getBlankLinesBetweenMembers()) {
			this.output.WriteLine();
		}
		if (member instanceof CodeTypeDeclaration) {
			((ICodeGenerator) this).generateCodeFromType((CodeTypeDeclaration) member, this.output.getInnerWriter(),
					this.options);
			this.currentClass = declaredType;
			return;
		}
		if (member.getStartDirectives().size() > 0) {
			this.generateDirectives(member.getStartDirectives());
		}
		this.generateCommentStatements(member.getComments());
		if (member.getLinePragma() != null) {
			this.generateLinePragmaStart(member.getLinePragma());
		}
		if (member instanceof CodeMemberField) {
			this.generateField((CodeMemberField) member);
		} else {
			if (member instanceof CodeMemberProperty) {
				this.generateProperty((CodeMemberProperty) member, declaredType);
			} else {
				if (member instanceof CodeMemberMethod) {
					if (member instanceof CodeConstructor) {
						this.generateConstructor((CodeConstructor) member, declaredType);
					} else {
						if (member instanceof CodeTypeConstructor) {
							this.generateTypeConstructor((CodeTypeConstructor) member);
						} else {
							if (member instanceof CodeEntryPointMethod) {
								this.generateEntryPointMethod((CodeEntryPointMethod) member, declaredType);
							} else {
								this.generateMethod((CodeMemberMethod) member, declaredType);
							}
						}
					}
				} else {
					if (member instanceof CodeMemberEvent) {
						this.generateEvent((CodeMemberEvent) member, declaredType);
					} else {
						if (member instanceof CodeSnippetTypeMember) {
							int indent = this.getIndent();
							this.setIndent(0);
							this.generateSnippetMember((CodeSnippetTypeMember) member);
							this.setIndent(indent);
							this.output.WriteLine();
						}
					}
				}
			}
		}
		if (member.getLinePragma() != null) {
			this.generateLinePragmaEnd(member.getLinePragma());
		}
		if (member.getEndDirectives().size() > 0) {
			this.generateDirectives(member.getEndDirectives());
		}
	}

	private void generateTypeConstructors(CodeTypeDeclaration e) throws Exception, InvalidOperationException {
		// IEnumerator enumerator = e.Members.GetEnumerator();
		for (Object enumerator : e.getMembers()) {
			if (enumerator instanceof CodeTypeConstructor) {
				this.currentMember = (CodeTypeMember) enumerator;
				if (this.options.getBlankLinesBetweenMembers()) {
					this.output.WriteLine();
				}
				if (this.currentMember.getStartDirectives().size() > 0) {
					this.generateDirectives(this.currentMember.getStartDirectives());
				}
				this.generateCommentStatements(this.currentMember.getComments());
				CodeTypeConstructor codeTypeConstructor = (CodeTypeConstructor) enumerator;
				if (codeTypeConstructor.getLinePragma() != null) {
					this.generateLinePragmaStart(codeTypeConstructor.getLinePragma());
				}
				this.generateTypeConstructor(codeTypeConstructor);
				if (codeTypeConstructor.getLinePragma() != null) {
					this.generateLinePragmaEnd(codeTypeConstructor.getLinePragma());
				}
				if (this.currentMember.getEndDirectives().size() > 0) {
					this.generateDirectives(this.currentMember.getEndDirectives());
				}
			}
		}
	}

	private void generateSnippetMembers(CodeTypeDeclaration e) throws Exception {
		// IEnumerator enumerator = e.Members.GetEnumerator();
		boolean flag = false;
		for (Object enumerator : e.getMembers()) {
			if (enumerator instanceof CodeSnippetTypeMember) {
				flag = true;
				this.currentMember = (CodeTypeMember) enumerator;
				if (this.options.getBlankLinesBetweenMembers()) {
					this.output.WriteLine();
				}
				if (this.currentMember.getStartDirectives().size() > 0) {
					this.generateDirectives(this.currentMember.getStartDirectives());
				}
				this.generateCommentStatements(this.currentMember.getComments());
				CodeSnippetTypeMember codeSnippetTypeMember = (CodeSnippetTypeMember) enumerator;
				if (codeSnippetTypeMember.getLinePragma() != null) {
					this.generateLinePragmaStart(codeSnippetTypeMember.getLinePragma());
				}
				int indent = this.getIndent();
				this.setIndent(0);
				this.generateSnippetMember(codeSnippetTypeMember);
				this.setIndent(indent);
				if (codeSnippetTypeMember.getLinePragma() != null) {
					this.generateLinePragmaEnd(codeSnippetTypeMember.getLinePragma());
				}
				if (this.currentMember.getEndDirectives().size() > 0) {
					this.generateDirectives(this.currentMember.getEndDirectives());
				}
			}
		}
		if (flag) {
			this.output.WriteLine();
		}
	}

	private void generateNestedTypes(CodeTypeDeclaration e) throws Exception, Exception {
		// IEnumerator enumerator = e.Members.GetEnumerator();
		for (Object enumerator : e.getMembers()) {
			if (enumerator instanceof CodeTypeDeclaration) {
				if (this.options.getBlankLinesBetweenMembers()) {
					this.output.WriteLine();
				}
				CodeTypeDeclaration e2 = (CodeTypeDeclaration) enumerator;
				((ICodeGenerator) this).generateCodeFromType(e2, this.output.getInnerWriter(), this.options);
			}
		}
	}

	private void generateNamespaces(CodeCompileUnit e) throws Exception, InvalidOperationException {
		for (Object e2 : e.getNamespaces()) {
			((ICodeGenerator) this).generateCodeFromNamespace((CodeNamespace) e2, this.output.getInnerWriter(),
					this.options);
		}
	}

	private void outputAttributeArgument(CodeAttributeArgument arg) throws Exception {
		if (arg.getName() != null && arg.getName().length() > 0) {
			this.outputIdentifier(arg.getName());
			this.output.Write("=");
		}
		((ICodeGenerator) this).generateCodeFromExpression(arg.getValue(), this.output.getInnerWriter(), this.options);
	}

	private void outputDirection(FieldDirection dir) throws Exception {
		switch (dir) {
		case In:
			break;
		case Out:
			this.output.Write("out ");
			return;
		case Ref:
			this.output.Write("ref ");
			break;
		default:
			return;
		}
	}

	private void outputExpressionList(CodeExpressionCollection expressions) throws Exception {
		this.outputExpressionList(expressions, false);
	}

	private void outputExpressionList(CodeExpressionCollection expressions, boolean newlineBetweenItems)
			throws Exception {
		boolean flag = true;
		// IEnumerator enumerator = expressions.GetEnumerator();
		int indent = this.getIndent();
		this.setIndent(indent + 1);
		for (Object enumerator : expressions) {
			if (flag) {
				flag = false;
			} else {
				if (newlineBetweenItems) {
					this.ContinueOnNewLine(",");
				} else {
					this.output.Write(", ");
				}
			}
			((ICodeGenerator) this).generateCodeFromExpression((CodeExpression) enumerator,
					this.output.getInnerWriter(), this.options);
		}
		indent = this.getIndent();
		this.setIndent(indent - 1);
	}

	private void outputParameters(CodeParameterDeclarationExpressionCollection parameters) throws Exception {
		boolean flag = true;
		boolean flag2 = parameters.size() > 15;
		if (flag2) {
			this.setIndent(this.getIndent() + 3);
		}
		/// IEnumerator enumerator = parameters.GetEnumerator();
		for (Object enumerator : parameters) {
			CodeParameterDeclarationExpression e = (CodeParameterDeclarationExpression) enumerator;
			if (flag) {
				flag = false;
			} else {
				this.output.Write(", ");
			}
			if (flag2) {
				this.ContinueOnNewLine("");
			}
			this.generateExpression(e);
		}
		if (flag2) {
			this.setIndent(this.getIndent() - 3);
		}
	}

	private void outputTypeNamePair(CodeTypeReference typeRef, String name) throws Exception {
		this.OutputType(typeRef);
		this.output.Write(" ");
		this.outputIdentifier(name);
	}

	private void outputTypeParameters(CodeTypeParameterCollection typeParameters) throws Exception {
		if (typeParameters.size() == 0) {
			return;
		}
		this.output.Write('<');
		boolean flag = true;
		for (int i = 0; i < typeParameters.size(); i++) {
			if (flag) {
				flag = false;
			} else {
				this.output.Write(", ");
			}
			if (typeParameters.getItem(i).getCustomAttributes().size() > 0) {
				this.generateAttributes(typeParameters.getItem(i).getCustomAttributes(), null, true);
				this.output.Write(' ');
			}
			this.output.Write(typeParameters.getItem(i).getName());
		}
		this.output.Write('>');
	}

	private void outputTypeParameterConstraints(CodeTypeParameterCollection typeParameters) throws Exception {
		if (typeParameters.size() == 0) {
			return;
		}
		for (int i = 0; i < typeParameters.size(); i++) {
			this.output.WriteLine();
			int indent = this.getIndent();
			this.setIndent(indent + 1);
			boolean flag = true;
			if (typeParameters.getItem(i).getConstraints().size() > 0) {
				for (Object typeRef : typeParameters.getItem(i).getConstraints()) {
					if (flag) {
						this.output.Write("where ");
						this.output.Write(typeParameters.getItem(i).getName());
						this.output.Write(" : ");
						flag = false;
					} else {
						this.output.Write(", ");
					}
					this.OutputType((CodeTypeReference) typeRef);
				}
			}
			if (typeParameters.getItem(i).getHasConstructorConstraint()) {
				if (flag) {
					this.output.Write("where ");
					this.output.Write(typeParameters.getItem(i).getName());
					this.output.Write(" : new()");
				} else {
					this.output.Write(", new ()");
				}
			}
			indent = this.getIndent();
			this.setIndent(indent - 1);
		}
	}

	private void outputTypeAttributes(CodeTypeDeclaration e) throws Exception {
		// if ((e.getAttributes().getValue() & MemberAttributes.New.getValue())
		// != 0) {
		if (e.getAttributes() != null && (e.getAttributes().getValue() & MemberAttributes.New) != 0) {
			this.output.Write("new ");
		}
		TypeAttributes typeAttributes = e.getTypeAttributes();
		switch ((typeAttributes.getValue() & TypeAttributes.VisibilityMask)) {
		case TypeAttributes.NotPublic:
		case TypeAttributes.NestedAssembly:
		case TypeAttributes.NestedFamANDAssem:
			this.output.Write("internal ");
			break;
		case TypeAttributes.Public:
		case TypeAttributes.NestedPublic:
			this.output.Write("public ");
			break;
		case TypeAttributes.NestedPrivate:
			this.output.Write("private ");
			break;
		case TypeAttributes.NestedFamily:
			this.output.Write("protected ");
			break;
		case TypeAttributes.VisibilityMask:
			this.output.Write("protected internal ");
			break;
		}
		if (e.getIsStruct()) {
			if (e.getIsPartial()) {
				this.output.Write("partial ");
			}
			this.output.Write("struct ");
			return;
		}
		if (e.getIsEnum()) {
			this.output.Write("enum ");
			return;
		}
		TypeAttributes typeAttributes2 = TypeAttributes
				.forValue(typeAttributes.getValue() & TypeAttributes.ClassSemanticsMask);
		if (typeAttributes2.getValue() == TypeAttributes.NotPublic) {
			if ((typeAttributes.getValue() & TypeAttributes.Sealed) == TypeAttributes.Sealed) {
				this.output.Write("sealed ");
			}
			if ((typeAttributes.getValue() & TypeAttributes.Abstract) == TypeAttributes.Abstract) {
				this.output.Write("abstract ");
			}
			if (e.getIsPartial()) {
				this.output.Write("partial ");
			}
			this.output.Write("class ");
			return;
		}
		if (typeAttributes2.getValue() != TypeAttributes.ClassSemanticsMask) {
			return;
		}
		if (e.getIsPartial()) {
			this.output.Write("partial ");
		}
		this.output.Write("interface ");
	}

	private void generateTypeEnd(CodeTypeDeclaration e) throws Exception {
		if (!this.getIsCurrentDelegate()) {
			int indent = this.getIndent();
			this.setIndent(indent - 1);
			this.output.WriteLine("}");
		}
	}

	/*
	 * 
	 * 
	 */
	private void generateNamespaceStart(CodeNamespace e) throws Exception {
		if (e.getName() != null && e.getName().length() > 0) {
			this.output.Write("package ");
			// String[] array = StringHelper.split(e.getName(), new char[] { '.'
			// });
			String[] array = e.getName().split(".");
			if (array.length == 0)
				this.output.Write(e.getName());
			;
			if (array.length > 0)
				this.outputIdentifier(array[0]);
			for (int i = 1; i < array.length; i++) {
				this.output.Write(".");
				this.outputIdentifier(array[i]);
			}
			this.output.WriteLine(";");
			// this.OutputStartingBrace();
			// int indent = this.getIndent();
			// this.setIndent(indent + 1);
		}
	}

	private void generateCompileUnit(CodeCompileUnit e) throws Exception, InvalidOperationException {
		this.generateCompileUnitStart(e);
		this.generateNamespaces(e);
		this.generateCompileUnitEnd(e);
	}

	private void generateCompileUnitStart(CodeCompileUnit e) throws Exception {
		if (e.getStartDirectives().size() > 0) {
			this.generateDirectives(e.getStartDirectives());
		}
		
		// SortedList sortedList = new SortedList(StringComparer.Ordinal);
		SortedMap<String, String> sortedList = new TreeMap<String, String>();
		for (Object codeNamespaceO : e.getNamespaces()) {
			CodeNamespace codeNamespace = (CodeNamespace) codeNamespaceO;
			if (StringHelper.isNullOrEmpty(codeNamespace.getName())) {
				codeNamespace.getUserData().put("GenerateImports", false);
				for (Object codeNamespaceImport1 : codeNamespace.getImports()) {
					CodeNamespaceImport codeNamespaceImport = (CodeNamespaceImport) codeNamespaceImport1;
					if (!sortedList.containsKey(codeNamespaceImport.getNamespace())) {
						sortedList.put(codeNamespaceImport.getNamespace(), codeNamespaceImport.getNamespace());
					}
				}
			}
		}
		for (String ident : sortedList.keySet()) {
			this.output.Write("import ");
			this.outputIdentifier(ident);
			this.output.Write(";");
		}
		if (sortedList.keySet().size() > 0) {
			this.output.WriteLine("");
		}
		if (e.getAssemblyCustomAttributes().size() > 0) {
			this.generateAttributes(e.getAssemblyCustomAttributes(), "assembly: ");
			this.output.WriteLine("");
		}
	}

	private void generateCompileUnitEnd(CodeCompileUnit e) throws Exception {
		if (e.getEndDirectives().size() > 0) {
			this.generateDirectives(e.getEndDirectives());
		}
	}

	private void generateDirectionExpression(CodeDirectionExpression e) throws Exception {
		this.outputDirection(e.getDirection());
		this.generateExpression(e.getExpression());
	}

	private void generateDirectives(CodeDirectiveCollection directives) throws Exception {
		for (int i = 0; i < directives.size(); i++) {
			CodeDirective codeDirective = directives.getItem(i);
			if (codeDirective instanceof CodeChecksumPragma) {
				this.generateChecksumPragma((CodeChecksumPragma) codeDirective);
			} else {
				if (codeDirective instanceof CodeRegionDirective) {
					this.generateCodeRegionDirective((CodeRegionDirective) codeDirective);
				}
			}
		}
	}

	private void generateChecksumPragma(CodeChecksumPragma checksumPragma) throws Exception {
		this.output.Write("#pragma checksum \"");
		this.output.Write(checksumPragma.getFileName());
		this.output.Write("\" \"");
		// this.output.Write(checksumPragma.getChecksumAlgorithmId().toString("B",
		// CultureInfo.InvariantCulture));
		this.output.Write(checksumPragma.getChecksumAlgorithmId().toString());
		this.output.Write("\" \"");
		if (checksumPragma.getChecksumData() != null) {
			byte[] checksumData = checksumPragma.getChecksumData();
			for (int i = 0; i < checksumData.length; i++) {
				byte b = checksumData[i];
				// this.output.Write(b.ToString("X2",
				// CultureInfo.InvariantCulture));
				this.output.Write(String.valueOf(b));
			}
		}
		this.output.WriteLine("\"");
	}

	private void generateCodeRegionDirective(CodeRegionDirective regionDirective) throws Exception {
		if (regionDirective.getRegionMode() == CodeRegionMode.Start) {
			this.output.Write("#region ");
			this.output.WriteLine(regionDirective.getRegionText());
			return;
		}
		if (regionDirective.getRegionMode() == CodeRegionMode.End) {
			this.output.WriteLine("#endregion");
		}
	}

	private void generateNamespaceEnd(CodeNamespace e) throws Exception {
		if (e.getName() != null && e.getName().length() > 0) {
			/*
			 * int indent = this.getIndent(); this.setIndent(indent - 1);
			 * this.output.WriteLine("}");
			 */
		}
	}

	private void generateNamespaceImport(CodeNamespaceImport e) throws Exception {
		this.output.Write("import ");
		this.outputIdentifier(e.getNamespace());
		this.output.WriteLine(";");
	}

	private void generateAttributeDeclarationsStart(CodeAttributeDeclarationCollection attributes) throws Exception {
		this.output.Write("[");
	}

	private void generateAttributeDeclarationsEnd(CodeAttributeDeclarationCollection attributes) throws Exception {
		this.output.Write("]");
	}

	private void generateAttributes(CodeAttributeDeclarationCollection attributes) throws Exception {
		this.generateAttributes(attributes, null, false);
	}

	private void generateAttributes(CodeAttributeDeclarationCollection attributes, String prefix) throws Exception {
		this.generateAttributes(attributes, prefix, false);
	}

	private void generateAttributes(CodeAttributeDeclarationCollection attributes, String prefix, boolean inLine)
			throws Exception {
		if (attributes.size() == 0) {
			return;
		}
		// IEnumerator enumerator = attributes.GetEnumerator();
		boolean flag = false;
		for (Object enumerator : attributes) {
			CodeAttributeDeclaration codeAttributeDeclaration = (CodeAttributeDeclaration) enumerator;
			if (StringHelper.equals(codeAttributeDeclaration.getName(), "system.paramarrayattribute",
					StringComparison.OrdinalIgnoreCase)) {
				flag = true;
			} else {
				this.generateAttributeDeclarationsStart(attributes);
				if (prefix != null) {
					this.output.Write(prefix);
				}
				if (codeAttributeDeclaration.getAttributeType() != null) {
					this.output.Write(this.getTypeOutput(codeAttributeDeclaration.getAttributeType()));
				}
				this.output.Write("(");
				boolean flag2 = true;
				for (Object arg : codeAttributeDeclaration.getArguments()) {
					if (flag2) {
						flag2 = false;
					} else {
						this.output.Write(", ");
					}
					this.outputAttributeArgument((CodeAttributeArgument) arg);
				}
				this.output.Write(")");
				this.generateAttributeDeclarationsEnd(attributes);
				if (inLine) {
					this.output.Write(" ");
				} else {
					this.output.WriteLine();
				}
			}
		}
		if (flag) {
			if (prefix != null) {
				this.output.Write(prefix);
			}
			this.output.Write("params");
			if (inLine) {
				this.output.Write(" ");
				return;
			}
			this.output.WriteLine();
		}
	}

	private static boolean isKeyword(String value) {
		// return false;
		return FixedStringLookup.Contains(JavaCodeGenerator.keywords, value, false);
	}

	private static boolean isPrefixTwoUnderscore(String value) {
		return value.length() >= 3 && (value.charAt(0) == '_' && value.charAt(1) == '_') && value.charAt(2) != '_';
	}

	public boolean supports(GeneratorSupport support) {
		return GeneratorSupport.forValue(support.getValue() & (GeneratorSupport.ArraysOfArrays.getValue()
				| GeneratorSupport.EntryPointMethod.getValue() | GeneratorSupport.GotoStatements.getValue()
				| GeneratorSupport.MultidimensionalArrays.getValue() | GeneratorSupport.StaticConstructors.getValue()
				| GeneratorSupport.TryCatchStatements.getValue() | GeneratorSupport.ReturnTypeAttributes.getValue()
				| GeneratorSupport.DeclareValueTypes.getValue() | GeneratorSupport.DeclareEnums.getValue()
				| GeneratorSupport.DeclareDelegates.getValue() | GeneratorSupport.DeclareInterfaces.getValue()
				| GeneratorSupport.DeclareEvents.getValue() | GeneratorSupport.AssemblyAttributes.getValue()
				| GeneratorSupport.ParameterAttributes.getValue() | GeneratorSupport.ReferenceParameters.getValue()
				| GeneratorSupport.ChainedConstructorArguments.getValue() | GeneratorSupport.NestedTypes.getValue()
				| GeneratorSupport.MultipleInterfaceMembers.getValue() | GeneratorSupport.PublicStaticMembers.getValue()
				| GeneratorSupport.ComplexExpressions.getValue() | GeneratorSupport.Win32Resources.getValue()
				| GeneratorSupport.Resources.getValue() | GeneratorSupport.PartialTypes.getValue()
				| GeneratorSupport.GenericTypeReference.getValue() | GeneratorSupport.GenericTypeDeclaration.getValue()
				| GeneratorSupport.DeclareIndexerProperties.getValue())) == support;
	}

	public boolean isValidIdentifier(String value) {
		if (value == null || value.length() == 0) {
			return false;
		}
		if (value.length() > 512) {
			return false;
		}
		if (value.charAt(0) != '@') {
			if (JavaCodeGenerator.isKeyword(value)) {
				return false;
			}
		} else {
			value = value.substring(1);// (1,value.length()-1);
		}
		return CodeGenerator.IsValidLanguageIndependentIdentifier(value);
	}

	public void validateIdentifier(String value) throws ArgumentException {
		if (!this.isValidIdentifier(value)) {
			throw new ArgumentException(SR.GetString("InvalidIdentifier", new Object[] { value }));
		}
	}

	public String createValidIdentifier(String name) {
		if (JavaCodeGenerator.isPrefixTwoUnderscore(name)) {
			name = "_" + name;
		}
		while (JavaCodeGenerator.isKeyword(name)) {
			name = "_" + name;
		}
		return name;
	}

	public String createEscapedIdentifier(String name) {
		if (JavaCodeGenerator.isKeyword(name) || JavaCodeGenerator.isPrefixTwoUnderscore(name)) {
			return "@" + name;
		}
		return name;
	}

	private String getBaseTypeOutput(CodeTypeReference typeRef) {
		String text = typeRef.getBaseType();
		if (text.length() == 0 || text == "void") {
			return "void";
		}
		String text2 = text.toLowerCase();// (CultureInfo.InvariantCulture).Trim();
		// unit num =
		// <PrivateImplementationDetails>.$$method0x6000001-ComputeStringHash(text2);
		long num = text2.hashCode();
		if (num <= 2218649502l) {
			if (num <= 574663925l) {
				if (num <= 503664103l) {
					if (num != 425110298l) {
						if (num == 503664103l) {
							if (text2 == "system.String") {
								text = "String";
								return text;
							}
						}
					} else {
						if (text2 == "system.char") {
							text = "char";
							return text;
						}
					}
				} else {
					if (num != 507700544l) {
						if (num == 574663925l) {
							if (text2 == "system.uint16") {
								text = "ushort";
								return text;
							}
						}
					} else {
						if (text2 == "system.uint64") {
							text = "ulong";
							return text;
						}
					}
				}
			} else {
				if (num <= 872348156l) {
					if (num != 801448826l) {
						if (num == 872348156l) {
							if (text2 == "system.byte") {
								text = "byte";
								return text;
							}
						}
					} else {
						if (text2 == "system.int32") {
							text = "int";
							return text;
						}
					}
				} else {
					if (num != 1487069339l) {
						if (num == 2218649502l) {
							if (text2 == "system.booleanean") {
								text = "boolean";
								return text;
							}
						}
					} else {
						if (text2 == "system.double") {
							text = "double";
							return text;
						}
					}
				}
			}
		} else {
			if (num <= 2679997701l) {
				if (num <= 2613725868l) {
					if (num != 2446023237l) {
						if (num == 2613725868l) {
							if (text2 == "system.int16") {
								text = "short";
								return text;
							}
						}
					} else {
						if (text2 == "system.decimal") {
							text = "decimal";
							return text;
						}
					}
				} else {
					if (num != 2647511797l) {
						if (num == 2679997701l) {
							if (text2 == "system.int64") {
								text = "long";
								return text;
							}
						}
					} else {
						if (text2 == "system.object") {
							text = "object";
							return text;
						}
					}
				}
			} else {
				if (num <= 2923133227l) {
					if (num != 2790997960l) {
						if (num == 2923133227l) {
							if (text2 == "system.uint32") {
								text = "uint";
								return text;
							}
						}
					} else {
						if (text2 == "system.void") {
							text = "void";
							return text;
						}
					}
				} else {
					if (num != 3248684926l) {
						if (num == 3680803037l) {
							if (text2 == "system.sbyte") {
								text = "sbyte";
								return text;
							}
						}
					} else {
						if (text2 == "system.single") {
							text = "float";
							return text;
						}
					}
				}
			}
		}
		StringBuilder StringBuilder = new StringBuilder(text.length() + 10);
		if ((typeRef.getOptions().getValue() & CodeTypeReferenceOptions.GlobalReference.getValue()) != 0) {
			StringBuilder.append("global::");
			// StringBuilder.append(" ");
		}
		String baseType = typeRef.getBaseType();
		int num2 = 0;
		int num3 = 0;
		for (int i = 0; i < baseType.length(); i++) {
			char c = baseType.charAt(i);
			if (c != '+' && c != '.') {
				if (c == '`') {

					StringBuilder.append(this.createEscapedIdentifier(baseType.substring(num2, i - num2)));
					i++;
					int num4 = 0;
					while (i < baseType.length() && baseType.charAt(i) >= '0' && baseType.charAt(i) <= '9') {
						num4 = num4 * 10 + (int) (baseType.charAt(i) - '0');
						i++;
					}
					this.getTypeArgumentsOutput(typeRef.getTypeArguments(), num3, num4, StringBuilder);
					num3 += num4;
					if (i < baseType.length() && (baseType.charAt(i) == '+' || baseType.charAt(i) == '.')) {
						StringBuilder.append('.');
						i++;
					}
					num2 = i;
				}
			} else {
				StringBuilder.append(this.createEscapedIdentifier(baseType.substring(num2, i - num2)));
				StringBuilder.append('.');
				i++;
				num2 = i;
			}
		}
		if (num2 < baseType.length()) {
			StringBuilder.append(this.createEscapedIdentifier(baseType.substring(num2)));
		}
		if (StringBuilder.indexOf("global::", 0) >= 0) {
			StringBuilder.replace(StringBuilder.indexOf("global::", 0), 8, "");
		}
		return StringBuilder.toString();
	}

	private String getTypeArgumentsOutput(CodeTypeReferenceCollection typeArguments) {
		StringBuilder StringBuilder = new StringBuilder(128);
		this.getTypeArgumentsOutput(typeArguments, 0, typeArguments.size(), StringBuilder);
		return StringBuilder.toString();
	}

	private void getTypeArgumentsOutput(CodeTypeReferenceCollection typeArguments, int start, int length,
			StringBuilder sb) {
		sb.append('<');
		boolean flag = true;
		for (int i = start; i < start + length; i++) {
			if (flag) {
				flag = false;
			} else {
				sb.append(", ");
			}
			if (i < typeArguments.size()) {
				sb.append(this.getTypeOutput(typeArguments.getItem(i)));
			}
		}
		sb.append('>');
	}

	public String getTypeOutput(CodeTypeReference typeRef) {
		String text = StringHelper.Empty;
		CodeTypeReference codeTypeReference = typeRef;
		while (codeTypeReference.getArrayElementType() != null) {
			codeTypeReference = codeTypeReference.getArrayElementType();
		}
		text += this.getBaseTypeOutput(codeTypeReference);
		while (typeRef != null && typeRef.getArrayRank() > 0) {
			char[] array = new char[typeRef.getArrayRank() + 1];
			array[0] = '[';
			array[typeRef.getArrayRank()] = ']';
			for (int i = 1; i < typeRef.getArrayRank(); i++) {
				array[i] = ',';
			}
			text += new String(array);
			typeRef = typeRef.getArrayElementType();
		}
		return text;
	}

	private void outputStartingBrace() throws Exception {
		if (this.getOptions().getBracingStyle() == "C") {
			this.output.WriteLine("");
			this.output.WriteLine("{");
			return;
		}
		this.output.WriteLine(" {");
	}

	private CompilerResults fromFileBatch(CompilerParameters options, String[] fileNames) throws Exception {
		if (options == null) {
			// throw new ArgumentNullException("options");
		}
		if (fileNames == null) {
			// throw new ArgumentNullException("fileNames");
		}
		// new
		// SecurityPermission(SecurityPermissionFlag.UnmanagedCode).Demand();
		String file = null;
		int num = 0;
		CompilerResults compilerResults = new CompilerResults(options.getTempFiles());
		// new
		// SecurityPermission(SecurityPermissionFlag.ControlEvidence).Assert();
		try {
			compilerResults.setEvidence(options.getEvidence());
		} finally {
			// CodeAccessPermission.RevertAssert();
		}
		boolean flag = false;
		if (options.getOutputAssembly() == null || options.getOutputAssembly().length() == 0) {
			String fileExtension = options.getGenerateExecutable() ? "exe" : "dll";
			options.setOutputAssembly(
					compilerResults.getTempFiles().AddExtension(fileExtension, !options.getGenerateInMemory()));
			new FileStream(options.getOutputAssembly(), FileMode.Create, FileAccess.ReadWrite).close();
			flag = true;
		}
		String text = "pdb";
		// if (options.getCompilerOptions() != null && -1 !=
		// CultureInfo.InvariantCulture.CompareInfo.IndexOf(options.CompilerOptions,
		// "/debug:pdbonly", CompareOptions.IgnoreCase))
		if (options.getCompilerOptions() != null && true) {
			compilerResults.getTempFiles().AddExtension(text, true);
		} else {
			compilerResults.getTempFiles().AddExtension(text);
		}
		String text2 = this.CmdArgsFromParameters(options) + " " + JavaCodeGenerator.joinStringArray(fileNames, " ");
		String responseFileCmdArgs = this.getResponseFileCmdArgs(options, text2);
		String trueArgs = null;
		if (responseFileCmdArgs != null) {
			trueArgs = text2;
			text2 = responseFileCmdArgs;
		}
		RefObject<String> refFile = new RefObject<String>(file);
		RefObject<Integer> refNum = new RefObject<Integer>(num);
		this.compile(options, RedistVersionInfo.GetCompilerPath(this.provOptions, this.getCompilerName()),
				this.getCompilerName(), text2, refFile, refNum, trueArgs);
		num = refNum.getRefObj();
		file = refFile.getRefObj();
		compilerResults.setNativeCompilerReturnValue(num);
		if (num != 0 || options.getWarningLevel() > 0) {
			String[] array = JavaCodeGenerator.readAllLines(file, Encoding.UTF8, FileShare.ReadWrite);
			for (int i = 0; i < array.length; i++) {
				String text3 = array[i];
				compilerResults.getOutput().add(text3);
				this.ProcessCompilerOutputLine(compilerResults, text3);
			}
			if (num > 0 & flag) {
				File.Delete(options.getOutputAssembly());
			}
		}
		if (compilerResults.getErrors().getHasErrors() || !options.getGenerateInMemory()) {
			compilerResults.setPathToAssembly(options.getOutputAssembly());
			return compilerResults;
		}
		byte[] rawAssembly = File.ReadAllBytes(options.getOutputAssembly());
		byte[] rawSymbolStore = null;
		try {
			String path = options.getTempFiles().getBasePath() + "." + text;
			if (File.Exists(path)) {
				rawSymbolStore = File.ReadAllBytes(path);
			}
		} catch (Exception e) {
			rawSymbolStore = null;
		}
		// new
		// SecurityPermission(SecurityPermissionFlag.ControlEvidence).Assert();
		try {
			compilerResults.setCompiledAssembly(Assembly.Load(rawAssembly, rawSymbolStore, options.getEvidence()));
			// compilerResults.CompiledAssembly = Assembly.Load(rawAssembly,
			// rawSymbolStore, options.Evidence);
		} finally {
			// CodeAccessPermission.RevertAssert();
		}
		return compilerResults;
	}

	private static String[] readAllLines(String file, String encoding, FileShare share)
			throws FileNotFoundException, Exception {
		String[] result = null;
		try (FileStream fileStream = File.Open(file, FileMode.Open, FileAccess.Read, share)) {
			List<String> list = new ArrayList<String>();
			try (StreamReader streamReader = new StreamReader(fileStream, encoding)) {
				String item;
				while ((item = streamReader.ReadLine()) != null) {
					list.add(item);
				}
			}
			result = list.toArray(result);
		}
		return result;
	}

	public CompilerResults compileAssemblyFromDom(CompilerParameters options, CodeCompileUnit e)
			throws ArgumentNullException {
		if (options == null) {
			throw new ArgumentNullException("options");
		}
		CompilerResults result;
		try {
			result = this.fromDom(options, e);
		} finally {
			options.getTempFiles().SafeDelete();
		}
		return result;
	}

	public CompilerResults compileAssemblyFromFile(CompilerParameters options, String fileName) throws Exception {
		if (options == null) {
			throw new ArgumentNullException("options");
		}
		CompilerResults result;
		try {
			result = this.fromFile(options, fileName);
		} finally {
			options.getTempFiles().SafeDelete();
		}
		return result;
	}

	public CompilerResults compileAssemblyFromSource(CompilerParameters options, String source) throws Exception {
		if (options == null) {
			// throw new ArgumentNullException("options");
		}
		CompilerResults result;
		try {
			result = this.fromSource(options, source);
		} finally {
			options.getTempFiles().SafeDelete();
		}
		return result;
	}

	public CompilerResults compileAssemblyFromSourceBatch(CompilerParameters options, String[] sources)
			throws ArgumentNullException {
		if (options == null) {
			throw new ArgumentNullException("options");
		}
		CompilerResults result;
		try {
			result = this.fromSourceBatch(options, sources);
		} finally {
			options.getTempFiles().SafeDelete();
		}
		return result;
	}

	public CompilerResults compileAssemblyFromFileBatch(CompilerParameters options, String[] fileNames)
			throws FileNotFoundException, Exception {
		if (options == null) {
			throw new ArgumentNullException("options");
		}
		if (fileNames == null) {
			throw new ArgumentNullException("fileNames");
		}
		CompilerResults result;
		try {
			for (int i = 0; i < fileNames.length; i++) {
				try (AutoCloseable closeable = File.OpenRead(fileNames[i])) {
				}
			}
			result = this.fromFileBatch(options, fileNames);
		} finally {
			options.getTempFiles().SafeDelete();
		}
		return result;
	}

	public CompilerResults compileAssemblyFromDomBatch(CompilerParameters options, CodeCompileUnit[] ea)
			throws Exception {
		if (options == null) {
			throw new ArgumentNullException("options");
		}
		CompilerResults result;
		try {
			result = this.fromDomBatch(options, ea);
		} finally {
			options.getTempFiles().SafeDelete();
		}
		return result;
	}

	void compile(CompilerParameters options, String compilerDirectory, String compilerExe, String arguments,
			RefObject<String> outputFile, RefObject<Integer> nativeReturnValue, String trueArgs) throws Exception {
		String text = null;
		outputFile.setRefObj(options.getTempFiles().AddExtension("out"));
		// RefObject<String> refObject = new RefObject<String>(outputFile);

		String text2 = Path.Combine(compilerDirectory, compilerExe);
		if (File.Exists(text2)) {
			String trueCmdLine = null;
			if (trueArgs != null) {
				trueCmdLine = "\"" + text2 + "\" " + trueArgs;
			}
			RefObject<String> refText = new RefObject<String>(text);
			nativeReturnValue.setRefObj(
					(Executor.ExecWaitWithCapture(options.getSafeUserToken(), "\"" + text2 + "\" " + arguments,
							Environment.CurrentDirectory, options.getTempFiles(), outputFile, refText, trueCmdLine)));
			text = refText.getRefObj();
			return;
		}
		throw new InvalidOperationException(SR.GetString("CompilerNotFound", new Object[] { text2 }));
	}

	private CompilerResults fromDom(CompilerParameters options, CodeCompileUnit e) throws ArgumentNullException {
		if (options == null) {
			throw new ArgumentNullException("options");
		}
		// new
		// SecurityPermission(SecurityPermissionFlag.UnmanagedCode).Demand();
		return this.fromDomBatch(options, new CodeCompileUnit[] { e });
	}

	private CompilerResults fromFile(CompilerParameters options, String fileName) throws Exception {
		if (options == null) {
			throw new ArgumentNullException("options");
		}
		if (fileName == null) {
			throw new ArgumentNullException("fileName");
		}
		// new
		// SecurityPermission(SecurityPermissionFlag.UnmanagedCode).Demand();
		try (AutoCloseable stream = File.OpenRead(fileName)) {
		}
		return this.fromFileBatch(options, new String[] { fileName });
	}

	private CompilerResults fromSource(CompilerParameters options, String source) throws Exception {
		if (options == null) {
			// throw new ArgumentNullException("options");
		}
		// new
		// SecurityPermission(SecurityPermissionFlag.UnmanagedCode).Demand();
		return this.fromSourceBatch(options, new String[] { source });
	}

	private CompilerResults fromDomBatch(CompilerParameters options, CodeCompileUnit[] ea) {
		if (options == null) {
			// throw new ArgumentNullException("options");
		}
		if (ea == null) {
			// throw new ArgumentNullException("ea");
		}
		// new
		// SecurityPermission(SecurityPermissionFlag.UnmanagedCode).Demand();
		String[] array = new String[ea.length];
		CompilerResults result = null;
		try {
			// WindowsImpersonationContext impersonation =
			// Executor.RevertImpersonation();
			try {
				for (int i = 0; i < ea.length; i++) {
					if (ea[i] != null) {
						this.resolveReferencedAssemblies(options, ea[i]);
						array[i] = options.getTempFiles().AddExtension(i + this.getFileExtension());
						FileStream stream = new FileStream(array[i], FileMode.Create, FileAccess.Write, FileShare.Read);
						try {
							try (StreamWriter streamWriter = new StreamWriter(stream, Encoding.UTF8)) {
								((ICodeGenerator) this).generateCodeFromCompileUnit(ea[i], streamWriter, this.options);
								streamWriter.Flush();
							}
						} finally {
							stream.Close();
						}
					}
				}
				result = this.fromFileBatch(options, array);
			} finally {
				// Executor.ReImpersonate(impersonation);
			}
		} catch (Exception ex) {
			// throw;
		}
		return result;
	}

	private void resolveReferencedAssemblies(CompilerParameters options, CodeCompileUnit e) {
		if (e.getReferencedAssemblies().size() > 0) {
			for (String current : e.getReferencedAssemblies()) {
				if (!options.getReferencedAssemblies().contains(current)) {
					options.getReferencedAssemblies().add(current);
				}
			}
		}
	}

	private CompilerResults fromSourceBatch(CompilerParameters options, String[] sources) throws ArgumentNullException {
		if (options == null) {
			throw new ArgumentNullException("options");
		}
		if (sources == null) {
			throw new ArgumentNullException("sources");
		}
		// new
		// SecurityPermission(SecurityPermissionFlag.UnmanagedCode).Demand();
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
						try (StreamWriter streamWriter = new StreamWriter(stream, Encoding.UTF8)) {
							streamWriter.Write(sources[i]);
							streamWriter.Flush();
						}
					} finally {
						stream.Close();
					}
					array[i] = text;
				}
				result = this.fromFileBatch(options, array);
			} finally {
				// Executor.ReImpersonate(impersonation);
			}
		} catch (Exception ex) {
			// throw;
		}
		return result;
	}

	private static String joinStringArray(String[] sa, String separator) {
		if (sa == null || sa.length == 0) {
			return StringHelper.Empty;
		}
		if (sa.length == 1) {
			return "\"" + sa[0] + "\"";
		}
		StringBuilder StringBuilder = new StringBuilder();
		for (int i = 0; i < sa.length - 1; i++) {
			StringBuilder.append("\"");
			StringBuilder.append(sa[i]);
			StringBuilder.append("\"");
			StringBuilder.append(separator);
		}
		StringBuilder.append("\"");
		StringBuilder.append(sa[sa.length - 1]);
		StringBuilder.append("\"");
		return StringBuilder.toString();
	}

	public void generateCodeFromType(CodeTypeDeclaration e, TextWriter w, CodeGeneratorOptions o) throws Exception {
		boolean flag = false;
		if (this.output != null && w != this.output.getInnerWriter()) {
			throw new InvalidOperationException(SR.GetString("CodeGenOutputWriter"));
		}
		if (this.output == null) {
			flag = true;
			this.options = ((o == null) ? new CodeGeneratorOptions() : o);
			this.output = new IndentedTextWriter(w, this.options.getIndentString());
		}
		try {
			this.generateType(e);
		} finally {
			if (flag) {
				this.output = null;
				this.options = null;
			}
		}
	}

	public void generateCodeFromExpression(CodeExpression e, TextWriter w, CodeGeneratorOptions o) throws Exception {
		boolean flag = false;
		if (this.output != null && w != this.output.getInnerWriter()) {
			throw new InvalidOperationException(SR.GetString("CodeGenOutputWriter"));
		}
		if (this.output == null) {
			flag = true;
			this.options = ((o == null) ? new CodeGeneratorOptions() : o);
			this.output = new IndentedTextWriter(w, this.options.getIndentString());
		}
		try {
			this.generateExpression(e);
		} finally {
			if (flag) {
				this.output = null;
				this.options = null;
			}
		}
	}

	public void generateCodeFromCompileUnit(CodeCompileUnit e, TextWriter w, CodeGeneratorOptions o) throws Exception {
		boolean flag = false;
		if (this.output != null && w != this.output.getInnerWriter()) {
			throw new InvalidOperationException(SR.GetString("CodeGenOutputWriter"));
		}
		if (this.output == null) {
			flag = true;
			this.options = ((o == null) ? new CodeGeneratorOptions() : o);
			this.output = new IndentedTextWriter(w, this.options.getIndentString());
		}
		try {
			if (e instanceof CodeSnippetCompileUnit) {
				this.GenerateSnippetCompileUnit((CodeSnippetCompileUnit) e);
			} else {
				this.generateCompileUnit(e);
			}
		} finally {
			if (flag) {
				this.output = null;
				this.options = null;
			}
		}
	}

	public void generateCodeFromNamespace(CodeNamespace e, TextWriter w, CodeGeneratorOptions o) throws Exception {
		boolean flag = false;
		if (this.output != null && w != this.output.getInnerWriter()) {
			throw new InvalidOperationException(SR.GetString("CodeGenOutputWriter"));
		}
		if (this.output == null) {
			flag = true;
			this.options = ((o == null) ? new CodeGeneratorOptions() : o);
			this.output = new IndentedTextWriter(w, this.options.getIndentString());
		}
		try {
			this.generateNamespace(e);
		} finally {
			if (flag) {
				this.output = null;
				this.options = null;
			}
		}
	}

	public void generateCodeFromStatement(CodeStatement e, TextWriter w, CodeGeneratorOptions o) throws Exception {
		boolean flag = false;
		if (this.output != null && w != this.output.getInnerWriter()) {
			throw new InvalidOperationException(SR.GetString("CodeGenOutputWriter"));
		}
		if (this.output == null) {
			flag = true;
			this.options = ((o == null) ? new CodeGeneratorOptions() : o);
			this.output = new IndentedTextWriter(w, this.options.getIndentString());
		}
		try {
			this.generateStatement(e);
		} finally {
			if (flag) {
				this.output = null;
				this.options = null;
			}
		}
	}
}
