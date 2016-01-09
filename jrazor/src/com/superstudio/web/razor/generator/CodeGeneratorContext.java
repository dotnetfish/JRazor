package com.superstudio.web.razor.generator;

import com.superstudio.commons.csharpbridge.action.ActionOne;
import com.superstudio.commons.csharpbridge.action.ActionTwo;
import com.superstudio.commons.csharpbridge.action.Func;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.web.*;
import com.superstudio.web.razor.*;
import com.superstudio.web.razor.parser.syntaxTree.*;
import com.superstudio.web.razor.text.*;
import com.superstudio.web.razor.utils.DisposableAction;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.codedom.*;
import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.IDisposable;
import com.superstudio.commons.csharpbridge.RefObject;


public class CodeGeneratorContext {
	private static final String DesignTimeHelperMethodName = "__RazorDesignTimeHelpers__";

	private int _nextDesignTimePragmaId = 1;
	private boolean _expressionHelperVariableWriten;
	private CodeMemberMethod _designTimeHelperMethod;
	private StatementBuffer _currentBuffer = new StatementBuffer();

	private CodeGeneratorContext() {
		setExpressionRenderingMode(ExpressionRenderingMode.WriteToOutput);
	}

	// Internal/Private state. Technically consumers might want to use some of
	// these but they can implement them independently if necessary.
	// It's way safer to make them internal for now, especially with the code
	// generator stuff in a bit of flux.
	private ExpressionRenderingMode privateExpressionRenderingMode = ExpressionRenderingMode.forValue(0);

	public final ExpressionRenderingMode getExpressionRenderingMode() {
		return privateExpressionRenderingMode;
	}

	public final void setExpressionRenderingMode(ExpressionRenderingMode value) {
		privateExpressionRenderingMode = value;
	}

	private ActionTwo<String, CodeLinePragma> privateStatementCollector;

	private ActionTwo<String, CodeLinePragma> getStatementCollector() {
		return privateStatementCollector;
	}

	private void setStatementCollector(ActionTwo<String, CodeLinePragma> value) {
		privateStatementCollector = value;
	}

	private Func<CodeWriter> privateCodeWriterFactory;

	private Func<CodeWriter> getCodeWriterFactory() {
		return privateCodeWriterFactory;
	}

	private void setCodeWriterFactory(Func<CodeWriter> value) {
		privateCodeWriterFactory = value;
	}

	private String privateSourceFile;

	public final String getSourceFile() {
		return privateSourceFile;
	}

	public final void setSourceFile(String value) {
		privateSourceFile = value;
	}

	private CodeCompileUnit privateCompileUnit;

	public final CodeCompileUnit getCompileUnit() {
		return privateCompileUnit;
	}

	public final void setCompileUnit(CodeCompileUnit value) {
		privateCompileUnit = value;
	}

	private CodeNamespace privateNamespace;

	public final CodeNamespace getNamespace() {
		return privateNamespace;
	}

	public final void setNamespace(CodeNamespace value) {
		privateNamespace = value;
	}

	private CodeTypeDeclaration privateGeneratedClass;

	public final CodeTypeDeclaration getGeneratedClass() {
		return privateGeneratedClass;
	}

	public final void setGeneratedClass(CodeTypeDeclaration value) {
		privateGeneratedClass = value;
	}

	private RazorEngineHost privateHost;

	public final RazorEngineHost getHost() {
		return privateHost;
	}

	private void setHost(RazorEngineHost value) {
		privateHost = value;
	}

	private java.util.Map<Integer, GeneratedCodeMapping> privateCodeMappings;

	public final java.util.Map<Integer, GeneratedCodeMapping> getCodeMappings() {
		return privateCodeMappings;
	}

	private void setCodeMappings(java.util.Map<Integer, GeneratedCodeMapping> value) {
		privateCodeMappings = value;
	}

	private String privateTargetWriterName;

	public final String getTargetWriterName() {
		return privateTargetWriterName;
	}

	public final void setTargetWriterName(String value) {
		privateTargetWriterName = value;
	}

	private CodeMemberMethod privateTargetMethod;

	public final CodeMemberMethod getTargetMethod() {
		return privateTargetMethod;
	}

	public final void setTargetMethod(CodeMemberMethod value) {
		privateTargetMethod = value;
	}

	public final String getCurrentBufferedStatement() {
		return _currentBuffer == null ? "" : _currentBuffer.getBuilder().toString();
	}

	public static CodeGeneratorContext create(RazorEngineHost host, String className, String rootNamespace,
											  String sourceFile, boolean shouldGenerateLinePragmas) {
		return create(host, null, className, rootNamespace, sourceFile, shouldGenerateLinePragmas);
	}

	public static CodeGeneratorContext create(RazorEngineHost host, Func<CodeWriter> writerFactory, String className,
											  String rootNamespace, String sourceFile, boolean shouldGenerateLinePragmas) {
		CodeGeneratorContext tempVar = new CodeGeneratorContext();
		tempVar.setHost(host);
		tempVar.setCodeWriterFactory(writerFactory);
		tempVar.setSourceFile(shouldGenerateLinePragmas ? sourceFile : null);
		tempVar.setCompileUnit(new CodeCompileUnit());
		tempVar.setNamespace(new CodeNamespace(rootNamespace));
		CodeTypeDeclaration typeDecl = new CodeTypeDeclaration(className);
		typeDecl.setIsClass(true);
		tempVar.setGeneratedClass(typeDecl);
		CodeMemberMethod methodDef = new CodeMemberMethod();
		methodDef.setName(host.getGeneratedClassContext().getExecuteMethodName());
		methodDef.setAttributes(
				MemberAttributes.forValue(MemberAttributes.Override | MemberAttributes.Public));
		tempVar.setTargetMethod(methodDef);
		tempVar.setCodeMappings(new java.util.HashMap<Integer, GeneratedCodeMapping>());
		CodeGeneratorContext context = tempVar;
		context.getCompileUnit().getNamespaces().add(context.getNamespace());
		context.getNamespace().getTypes().add(context.getGeneratedClass());
		context.getGeneratedClass().getMembers().add(context.getTargetMethod());

		CodeNamespaceImport[] array = new CodeNamespaceImport[host.getNamespaceImports().size()];
		context.getNamespace().getImports().AddRange(CollectionHelper
				.select(host.getNamespaceImports(), (String s) -> new CodeNamespaceImport(s)).toArray(array));

		return context;
	}

	public final void AddDesignTimeHelperStatement(CodeSnippetStatement statement) {
		if (_designTimeHelperMethod == null) {
			CodeMemberMethod tempVar = new CodeMemberMethod();
			tempVar.setName(DesignTimeHelperMethodName);
			tempVar.setAttributes( MemberAttributes.forValue(MemberAttributes.Private));
			_designTimeHelperMethod = tempVar;
			_designTimeHelperMethod.getStatements()
					.add(new CodeSnippetStatement(BuildCodeString(cw -> cw.writeDisableUnusedFieldWarningPragma())));
				_designTimeHelperMethod.getStatements()
					.add(new CodeSnippetStatement(BuildCodeString(cw -> cw.writeRestoreUnusedFieldWarningPragma())));
			getGeneratedClass().getMembers().add(0, _designTimeHelperMethod);
		}
		_designTimeHelperMethod.getStatements().add(_designTimeHelperMethod.getStatements().size() - 1, statement);
	}

	public final int AddCodeMapping(SourceLocation sourceLocation, int generatedCodeStart, int generatedCodeLength) {
		if (generatedCodeStart == Integer.MAX_VALUE) {
			// throw new ArgumentOutOfRangeException("generatedCodeStart");
		}

		GeneratedCodeMapping mapping = new GeneratedCodeMapping(sourceLocation.getAbsoluteIndex(),
				sourceLocation.getLineIndex() + 1, sourceLocation.getCharacterIndex() + 1, generatedCodeStart + 1,
				generatedCodeLength);

		int id = _nextDesignTimePragmaId++;
		getCodeMappings().put(id, mapping);
		return id;
	}

	public final CodeLinePragma GenerateLinePragma(Span target) {
		return GenerateLinePragma(target, 0);
	}

	public final CodeLinePragma GenerateLinePragma(Span target, int generatedCodeStart) {
		return GenerateLinePragma(target, generatedCodeStart, target.getContent().length());
	}

	public final CodeLinePragma GenerateLinePragma(Span target, int generatedCodeStart, int codeLength) {
		return GenerateLinePragma(target.getStart(), generatedCodeStart, codeLength);
	}

	public final CodeLinePragma GenerateLinePragma(SourceLocation start, int generatedCodeStart, int codeLength) {
		if (!StringHelper.isNullOrEmpty(getSourceFile())) {
			if (getHost().getDesignTimeMode()) {
				int mappingId = AddCodeMapping(start, generatedCodeStart, codeLength);
				return new CodeLinePragma(getSourceFile(), mappingId);
			}
			return new CodeLinePragma(getSourceFile(), start.getLineIndex() + 1);
		}
		return null;
	}

	public final void BufferStatementFragment(Span sourceSpan) {
		BufferStatementFragment(sourceSpan.getContent(), sourceSpan);
	}

	public final void BufferStatementFragment(String fragment) {
		BufferStatementFragment(fragment, null);
	}

	public final void BufferStatementFragment(String fragment, Span sourceSpan) {
		if (sourceSpan != null && _currentBuffer.getLinePragmaSpan() == null) {
			_currentBuffer.setLinePragmaSpan(sourceSpan);

			// pad the output as necessary
			int start = _currentBuffer.getBuilder().length();
			if (_currentBuffer.GeneratedCodeStart != null) {
				start = _currentBuffer.GeneratedCodeStart;
			}

			int paddingLength = 0; // unused, in this case there is enough
									// context in the original code to calculate
									// the right padding length
			// (padded.Length - _currentBuffer.Builder.Length)

			RefObject<Integer> tempRef_paddingLength = new RefObject<Integer>(paddingLength);
			String padded = CodeGeneratorPaddingHelper.pad(getHost(), _currentBuffer.getBuilder().toString(),
					sourceSpan, start, tempRef_paddingLength);
			
			paddingLength = tempRef_paddingLength.getRefObj();
			_currentBuffer.GeneratedCodeStart = start + (padded.length() - _currentBuffer.getBuilder().length());
			_currentBuffer.getBuilder().delete(0, _currentBuffer.getBuilder().length());
			_currentBuffer.getBuilder().append(padded);
		}
		_currentBuffer.getBuilder().append(fragment);
	}

	public final void MarkStartOfGeneratedCode() {
		_currentBuffer.MarkStart();
	}

	public final void MarkEndOfGeneratedCode() {
		_currentBuffer.MarkEnd();
	}

	public final void FlushBufferedStatement() {
		if (_currentBuffer.getBuilder().length() > 0) {
			CodeLinePragma pragma = null;
			if (_currentBuffer.getLinePragmaSpan() != null) {
				int start = _currentBuffer.getBuilder().length();
				if (_currentBuffer.GeneratedCodeStart != null) {
					start = _currentBuffer.GeneratedCodeStart;
				}
				int len = _currentBuffer.getBuilder().length() - start;
				if (_currentBuffer.CodeLength != null) {
					len = _currentBuffer.CodeLength;
				}
				pragma = GenerateLinePragma(_currentBuffer.getLinePragmaSpan(), start, len);
			}
			AddStatement(_currentBuffer.getBuilder().toString(), pragma);
			_currentBuffer.Reset();
		}
	}

	public final void AddStatement(String generatedCode) {
		AddStatement(generatedCode, null);
	}

	public final void AddStatement(String body, CodeLinePragma pragma) {
		if (getStatementCollector() == null) {
			CodeSnippetStatement tempVar = new CodeSnippetStatement(body);
			tempVar.setLinePragma(pragma);
			getTargetMethod().getStatements().add(tempVar);
		} else {
			getStatementCollector().execute(body, pragma);
		}
	}

	public final void EnsureExpressionHelperVariable() {
		if (!_expressionHelperVariableWriten) {
			CodeMemberField tempVar = new CodeMemberField(Object.class, "__o");
			tempVar.setAttributes(MemberAttributes
					.forValue(MemberAttributes.Private | MemberAttributes.Static));
			getGeneratedClass().getMembers().add(0, tempVar);
			_expressionHelperVariableWriten = true;
		}
	}

	public final IDisposable ChangeStatementCollector(ActionTwo<String, CodeLinePragma> collector) {
		ActionTwo<String, CodeLinePragma> oldCollector = getStatementCollector();
		setStatementCollector(collector);

		return new DisposableAction(() -> {
			setStatementCollector(oldCollector);
		});
	}

	public final void AddContextCall(Span contentSpan, String methodName, boolean isLiteral) {

		AddStatement(BuildCodeString(cw -> {
			cw.writeStartMethodInvoke(methodName);
			if (!StringHelper.isNullOrEmpty(getTargetWriterName())) {
				cw.writeSnippet(getTargetWriterName());
				cw.writeParameterSeparator();
			}
			try {
				cw.writeStringLiteral(getHost().getInstrumentedSourceFilePath());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cw.writeParameterSeparator();
			cw.writeSnippet((new Integer(contentSpan.getStart().getAbsoluteIndex())).toString());
			cw.writeParameterSeparator();
			cw.writeSnippet(String.valueOf(contentSpan.getContent().length()));
			cw.writeParameterSeparator();
			cw.writeSnippet((new Boolean(isLiteral)).toString().toLowerCase());
			cw.writeEndMethodInvoke();
			cw.writeEndStatement();
		}));
	}

	public final CodeWriter CreateCodeWriter() {
		assert getCodeWriterFactory() != null;
		if (getCodeWriterFactory() == null) {
			// return null;
			try {
				throw new InvalidOperationException(RazorResources.getCreateCodeWriter_NoCodeWriter());
			} catch (InvalidOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return getCodeWriterFactory().execute();
	}

	public final String BuildCodeString(ActionOne<CodeWriter> action) {

		// using (CodeWriter cw = CodeWriterFactory())
		CodeWriter cw = getCodeWriterFactory().execute();
		try {
			action.execute(cw);
			return cw.getContent();
		} finally {
			cw.dispose();
		}
	}

	private static class StatementBuffer {
		private StringBuilder builder = new StringBuilder();
		private Integer GeneratedCodeStart=0;
		private Integer CodeLength;
		private Span linePragmaSpan;

		public final void Reset() {
			builder.delete(0, builder.length());
			GeneratedCodeStart = 0;
			CodeLength = 0;
			setLinePragmaSpan(null);
		}

		public final void MarkStart() {
			GeneratedCodeStart = builder.length();
		}

		public final void MarkEnd() {

			CodeLength = builder.length() - GeneratedCodeStart;
		}

		public Span getLinePragmaSpan() {
			return linePragmaSpan;
		}

		public void setLinePragmaSpan(Span linePragmaSpan1) {
			linePragmaSpan = linePragmaSpan1;
		}

		public StringBuilder getBuilder() {
			return builder;
		}

	}
}