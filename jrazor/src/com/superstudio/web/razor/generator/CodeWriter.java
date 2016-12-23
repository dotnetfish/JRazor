package com.superstudio.web.razor.generator;

import com.superstudio.codedom.CodeLinePragma;
import com.superstudio.codedom.CodeSnippetStatement;
import com.superstudio.codedom.CodeSnippetTypeMember;

import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.web.razor.text.LocationTagged;

import java.io.IOException;
import java.io.StringWriter;


// Utility class which helps write code snippets
public abstract class CodeWriter implements AutoCloseable
{
	private StringWriter _writer;

	protected CodeWriter()
	{
	}

	private enum WriterMode
	{
		Constructor,
		MethodCall,
		LambdaDelegate,
		LambdaExpression;

		public int getValue()
		{
			return this.ordinal();
		}

		public static WriterMode forValue(int value)
		{
			return values()[value];
		}
	}

	public final String getContent()
	{
		return getInnerWriter().toString();
	}

	public final StringWriter getInnerWriter()
	{
		if (_writer == null)
		{
			_writer = new StringWriter();
		}
		return _writer;
	}

	public boolean getSupportsMidStatementLinePragmas()
	{
		return true;
	}

	public abstract void writeParameterSeparator();
	public abstract void writeReturn();
	public abstract void writeLinePragma(Integer lineNumber, String fileName);
	public abstract void writeHelperHeaderPrefix(String templateTypeName, boolean isStatic);
	public abstract void writeSnippet(String snippet);
	public abstract void writeStringLiteral(String literal) throws ArgumentNullException;
	public abstract int writeVariableDeclaration(String type, String name, String value);

	public void writeLinePragma()
	{
		writeLinePragma(null);
	}

	public void writeLinePragma(CodeLinePragma pragma)
	{
		if (pragma == null)
		{
			writeLinePragma(null, null);
		}
		else
		{
			writeLinePragma(pragma.getLineNumber(), pragma.getFileName());
		}
	}

	public void writeHiddenLinePragma()
	{
	}

	public void writeDisableUnusedFieldWarningPragma()
	{
	}

	public void writeRestoreUnusedFieldWarningPragma()
	{
	}

	public void writeIdentifier(String identifier)
	{
		getInnerWriter().write(identifier);
	}

	public void writeHelperHeaderSuffix(String templateTypeName)
	{
	}

	public void writeHelperTrailer()
	{
	}

	public final void writeStartMethodInvoke(String methodName)
	{
		emitStartMethodInvoke(methodName);
	}

	public final void writeStartMethodInvoke(String methodName, String... genericArguments)
	{
		emitStartMethodInvoke(methodName, genericArguments);
	}

	public final void writeEndMethodInvoke()
	{
		emitEndMethodInvoke();
	}

	public void writeEndStatement()
	{
	}

	public void writeStartAssignment(String variableName)
	{
		getInnerWriter().write(variableName);
		getInnerWriter().write(" = ");
	}

	public final void writeStartLambdaExpression(String... parameterNames) throws ArgumentNullException
	{
		emitStartLambdaExpression(parameterNames);
	}

	public final void writeStartConstructor(String typeName)throws ArgumentNullException
	{
		emitStartConstructor(typeName);
	}

	public final void writeStartLambdaDelegate(String... parameterNames)throws ArgumentNullException
	{
		emitStartLambdaDelegate(parameterNames);
	}

	public final void writeEndLambdaExpression()
	{
		emitEndLambdaExpression();
	}

	public final void writeEndConstructor()
	{
		emitEndConstructor();
	}

	public final void writeEndLambdaDelegate()
	{
		emitEndLambdaDelegate();
	}

	public void writeLineContinuation()
	{
	}

	public void writeBooleanLiteral(boolean value)
	{
		writeSnippet((new Boolean(value)).toString());
	}

	public final void dispose()
	{
		dispose(true);

	}

	public  final  void close(){
		dispose(true);
	}

	public final void clear()
	{
		if (getInnerWriter() != null)
		{
			getInnerWriter().getBuffer().delete(0, getInnerWriter().getBuffer().length());
		}
	}

	public final CodeSnippetStatement toStatement()
	{
		return new CodeSnippetStatement(getContent());
	}

	public final CodeSnippetTypeMember toTypeMember()
	{
		return new CodeSnippetTypeMember(getContent());
	}

	protected abstract void emitStartLambdaDelegate(String[] parameterNames) throws ArgumentNullException;
	protected abstract void emitStartLambdaExpression(String[] parameterNames) throws ArgumentNullException;
	protected abstract void emitStartConstructor(String typeName) throws ArgumentNullException;
	protected abstract void emitStartMethodInvoke(String methodName);

	protected void emitStartMethodInvoke(String methodName, String... genericArguments)
	{
		emitStartMethodInvoke(methodName);
	}

	protected abstract void emitEndLambdaDelegate();
	protected abstract void emitEndLambdaExpression();
	protected abstract void emitEndConstructor();
	protected abstract void emitEndMethodInvoke();

	protected void dispose(boolean disposing)
	{
		if (disposing && _writer != null)
		{
			try {
				_writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void writeLocationTaggedString(LocationTagged<String> value)
	{
		writeStartMethodInvoke("new PositionTagged");
		try {
			writeStringLiteral(value.getValue());
		} catch (ArgumentNullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeParameterSeparator();
		writeSnippet((new Integer(value.getLocation().getAbsoluteIndex())).toString());
		writeEndMethodInvoke();
	}
	
	
}