package com.superstudio.jrazor.generator;

import java.io.IOException;
import java.io.StringWriter;

import com.superstudio.codedom.CodeLinePragma;
import com.superstudio.codedom.CodeSnippetStatement;
import com.superstudio.codedom.CodeSnippetTypeMember;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.jrazor.text.LocationTagged;


// Utility class which helps write code snippets
public abstract class CodeWriter implements IDisposable
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

	public void WriteLinePragma()
	{
		WriteLinePragma(null);
	}

	public void WriteLinePragma(CodeLinePragma pragma)
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

	public void WriteHelperHeaderSuffix(String templateTypeName)
	{
	}

	public void WriteHelperTrailer()
	{
	}

	public final void WriteStartMethodInvoke(String methodName)
	{
		emitStartMethodInvoke(methodName);
	}

	public final void WriteStartMethodInvoke(String methodName, String... genericArguments)
	{
		emitStartMethodInvoke(methodName, genericArguments);
	}

	public final void WriteEndMethodInvoke()
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

	public final void writeStartLambdaExpression(String... parameterNames)
	{
		emitStartLambdaExpression(parameterNames);
	}

	public final void writeStartConstructor(String typeName)
	{
		emitStartConstructor(typeName);
	}

	public final void writeStartLambdaDelegate(String... parameterNames)
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
		//GC.SuppressFinalize(this);
		System.gc();
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

	protected abstract void emitStartLambdaDelegate(String[] parameterNames);
	protected abstract void emitStartLambdaExpression(String[] parameterNames);
	protected abstract void emitStartConstructor(String typeName);
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
		WriteStartMethodInvoke("Tuple.Create");
		try {
			writeStringLiteral(value.getValue());
		} catch (ArgumentNullException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		writeParameterSeparator();
		writeSnippet((new Integer(value.getLocation().getAbsoluteIndex())).toString());
		WriteEndMethodInvoke();
	}
	
	
}