package com.superstudio.language.java;

import com.sun.deploy.util.StringUtils;
import com.superstudio.commons.Environment;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.web.razor.generator.BaseCodeWriter;

public class JavaCodeWriter extends BaseCodeWriter {

	@Override
	protected void writeStartGenerics() {
		getInnerWriter().write("<");
	}

	@Override
	protected void writeEndGenerics() {
		getInnerWriter().write(">");
	}

	@Override
	public int writeVariableDeclaration(String type, String name, String value) {
		getInnerWriter().write(type);
		getInnerWriter().write(" ");
		getInnerWriter().write(name);
		if (!StringHelper.isNullOrEmpty(value)) {
			getInnerWriter().write(" = ");
			getInnerWriter().write(value);
		} else {
			getInnerWriter().write(" = null");
		}
		return 0;
	}

	@Override
	public void writeDisableUnusedFieldWarningPragma() {
		getInnerWriter().write("//#pragma warning disable 219");
	}

	@Override
	public void writeRestoreUnusedFieldWarningPragma() {
		getInnerWriter().write("//#pragma warning restore 219");
	}

	@Override
	public void writeStringLiteral(String literal) {
		if (literal == null) {
			// throw new ArgumentNullException("literal");
		}

			writeCStyleStringLiteral(literal);

	}

	private void writeVerbatimStringLiteral(String literal) {
		//getInnerWriter().write("@\"");
		for (int i = 0; i < literal.length(); i++) {
			if (literal.charAt(i) == '\"') {
				getInnerWriter().write("\"\"");
			} else {
				getInnerWriter().write(literal.charAt(i));
			}
		}
		getInnerWriter().write("\"");
	}

	private void writeCStyleStringLiteral(String literal) {
			getInnerWriter().write("\"");
		for (int i = 0; i < literal.length(); i++) {

			// ORIGINAL LINE: case '\r':
			if (literal.charAt(i) == '\r') {
				getInnerWriter().write("\\r");
			}
			// ORIGINAL LINE: case '\t':
			else if (literal.charAt(i) == '\t') {
				getInnerWriter().write("\\t");
			}
			// ORIGINAL LINE: case '\"':
			else if (literal.charAt(i) == '\"') {
				getInnerWriter().write("\\\"");
			}
			// ORIGINAL LINE: case '\'':
			else if (literal.charAt(i) == '\'') {
				getInnerWriter().write("\\\'");
			}
			// ORIGINAL LINE: case '\\':
			else if (literal.charAt(i) == '\\') {
				getInnerWriter().write("\\\\");
			}
			// ORIGINAL LINE: case '\0':
			else if (literal.charAt(i) == '\0') {
				getInnerWriter().write("\\\0");
			}
			// ORIGINAL LINE: case '\n':
			else if (literal.charAt(i) == '\n') {
				getInnerWriter().write("\\n");
			}
			// ORIGINAL LINE: case '\u2028': 
			else if (literal.charAt(i) == '\u2028' || literal.charAt(i) == '\u2029') {

				getInnerWriter().write("\\u");
				String result=Integer.toHexString(new Integer(literal.charAt(i)));
				StringHelper.padLeft(result.toUpperCase(),4,"0");
				getInnerWriter().write(result);

			} else {
				getInnerWriter().write(literal.charAt(i));
			}
			/*if (i > 0 && i % 80 == 0) {
				// If current character is a high surrogate and the following
				// character is a low surrogate, don't break them.
				// Otherwise when we write the string to a file, we might lose
				// the characters.
				if (Character.isHighSurrogate(literal.charAt(i)) && (i < literal.length() - 1)
						&& Character.isLowSurrogate(literal.charAt(i + 1))) {
					getInnerWriter().write(literal.charAt(++i));
				}

				getInnerWriter().write("\" +");
				getInnerWriter().write(Environment.NewLine);
				getInnerWriter().write('\"');
			}*/
		}

	getInnerWriter().write("\"");
	}

	@Override
	public void writeEndStatement() {
		getInnerWriter().write(";\r\n");
	}

	@Override
	public void writeIdentifier(String identifier) {
		getInnerWriter().write("@" + identifier);
	}



		@Override
	public void writeBooleanLiteral(boolean value) {
		writeSnippet((new Boolean(value)).toString().toLowerCase());
	}

	@Override
	protected void emitStartLambdaExpression(String[] parameterNames) throws ArgumentNullException{
		if (parameterNames == null) {
			throw new ArgumentNullException("parameterNames");
		}

		if (parameterNames.length == 0 || parameterNames.length > 1) {
			getInnerWriter().write("(");
		}
		writeCommaSeparatedList(parameterNames, (t) -> getInnerWriter().write(t));
		if (parameterNames.length == 0 || parameterNames.length > 1) {
			getInnerWriter().write(")");
		}
		getInnerWriter().write(" -> ");
	}

	@Override
	protected void emitStartLambdaDelegate(String[] parameterNames) throws ArgumentNullException {
		if (parameterNames == null) {
			throw new ArgumentNullException("parameterNames");
		}

		emitStartLambdaExpression(parameterNames);
		getInnerWriter().write("{\r\n");
	}

	@Override
	protected void emitEndLambdaDelegate() {
		getInnerWriter().write("}");
	}

	@Override
	protected void emitStartConstructor(String typeName) throws ArgumentNullException {
		if (typeName == null) {
			 throw new ArgumentNullException("typeName");
		}

		getInnerWriter().write("new ");
		getInnerWriter().write(typeName);
		getInnerWriter().write("(");
	}

	@Override
	public void writeReturn() {
		getInnerWriter().write("return ");
	}

	@Override
	public void writeLinePragma(Integer lineNumber, String fileName) {
		getInnerWriter().write("\r\n");
		if (lineNumber != null) {
			getInnerWriter().write("//#line ");
			getInnerWriter().write(lineNumber);
			getInnerWriter().write(" \"");
			getInnerWriter().write(fileName);
			getInnerWriter().write("\"");
			getInnerWriter().write("\r\n");
			// getInnerWriter().writeLine();
		} else {
			// getInnerWriter().write("\r\n");
			getInnerWriter().write("//#line default");
			getInnerWriter().write("\r\n");
			getInnerWriter().write("//#line hidden");
			getInnerWriter().write("\r\n");
		}
	}

	@Override
	public void writeHiddenLinePragma() {
		getInnerWriter().write("//#line hidden\r\n");
	}

	@Override
	public void writeHelperHeaderPrefix(String templateTypeName, boolean isStatic) {
		getInnerWriter().write("public ");
		if (isStatic) {
			getInnerWriter().write("static ");
		}
		getInnerWriter().write(templateTypeName);
		getInnerWriter().write(" ");
	}
}