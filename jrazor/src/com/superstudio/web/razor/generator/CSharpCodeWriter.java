package com.superstudio.web.razor.generator;

import com.superstudio.commons.Environment;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.exception.ArgumentNullException;

public class CSharpCodeWriter extends BaseCodeWriter
{
	@Override
	protected void writeStartGenerics()
	{
		getInnerWriter().write("<");
	}

	@Override
	protected void writeEndGenerics()
	{
		getInnerWriter().write(">");
	}

	@Override
	public int writeVariableDeclaration(String type, String name, String value)
	{
		getInnerWriter().write(type);
		getInnerWriter().write(" ");
		getInnerWriter().write(name);
		if (!StringHelper.isNullOrEmpty(value))
		{
			getInnerWriter().write(" = ");
			getInnerWriter().write(value);
		}
		else
		{
			getInnerWriter().write(" = null");
		}
		return 0;
	}

	@Override
	public void writeDisableUnusedFieldWarningPragma()
	{
		getInnerWriter().write("#pragma warning disable 219");
	}

	@Override
	public void writeRestoreUnusedFieldWarningPragma()
	{
		getInnerWriter().write("#pragma warning restore 219");
	}

	@Override
	public void writeStringLiteral(String literal) throws ArgumentNullException
	{
		if (literal == null)
		{
			throw new ArgumentNullException("literal");
		}

		// From CSharpCodeProvider in CodeDOM
		//  If the string is short, use C style quoting (e.g "\r\n")
		//  Also do it if it is too long to fit in one line
		//  If the string contains '\0', verbatim style won't work.
		if (literal.length() >= 256 && literal.length() <= 1500 && literal.indexOf('\0') == -1)
		{
			writeVerbatimStringLiteral(literal);
		}
		else
		{
			writeCStyleStringLiteral(literal);
		}
	}

	private void writeVerbatimStringLiteral(String literal)
	{
		// From CSharpCodeGenerator.QuoteSnippetStringVerbatim in CodeDOM
		/*getInnerWriter().write("@\"");
		for (int i = 0; i < literal.length(); i++)
		{
			if (literal.charAt(i) == '\"')
			{
				getInnerWriter().write("\"\"");
			}
			else
			{
				getInnerWriter().write(literal.charAt(i));
			}
		}
		getInnerWriter().write("\"");*/
		writeCStyleStringLiteral(literal);
	}

	private void writeCStyleStringLiteral(String literal)
	{
		// From CSharpCodeGenerator.QuoteSnippetStringCStyle in CodeDOM
		getInnerWriter().write("\"");
		for (int i = 0; i < literal.length(); i++)
		{

//			switch (literal[i])
//ORIGINAL LINE: case '\r':
			if (literal.charAt(i) == '\r')
			{
					getInnerWriter().write("\\r");
			}
//ORIGINAL LINE: case '\t':
			else if (literal.charAt(i) == '\t')
			{
					getInnerWriter().write("\\t");
			}
//ORIGINAL LINE: case '\"':
			else if (literal.charAt(i) == '\"')
			{
					getInnerWriter().write("\\\"");
			}
//ORIGINAL LINE: case '\'':
			else if (literal.charAt(i) == '\'')
			{
					getInnerWriter().write("\\\'");
			}
//ORIGINAL LINE: case '\\':
			else if (literal.charAt(i) == '\\')
			{
					getInnerWriter().write("\\\\");
			}
//ORIGINAL LINE: case '\0':
			else if (literal.charAt(i) == '\0')
			{
					getInnerWriter().write("\\\0");
			}
//ORIGINAL LINE: case '\n':
			else if (literal.charAt(i) == '\n')
			{
					getInnerWriter().write("\\n");
			}
//ORIGINAL LINE: case '\u2028':
			else if (literal.charAt(i) == '\u2028' || literal.charAt(i) == '\u2029')
			{
					// Inlined CSharpCodeGenerator.AppendEscapedChar
					getInnerWriter().write("\\u");
					//getInnerWriter().write((new Integer(literal.charAt(i))).toString("X4", CultureInfo.InvariantCulture));
					getInnerWriter().write((new Integer(literal.charAt(i))).toString());
					
			}
			else
			{
					getInnerWriter().write(literal.charAt(i));
			}
			if (i > 0 && i % 80 == 0)
			{
				// If current character is a high surrogate and the following 
				// character is a low surrogate, don't break them. 
				// Otherwise when we write the string to a file, we might lose 
				// the characters.
				if (Character.isHighSurrogate(literal.charAt(i)) && (i < literal.length() - 1) 
						&& Character.isLowSurrogate(literal.charAt(i + 1)))
				{
					getInnerWriter().write(literal.charAt(++i));
				}

				getInnerWriter().write("\" +");
				getInnerWriter().write(Environment.NewLine);
				getInnerWriter().write('\"');
			}
		}
		getInnerWriter().write("\"");
	}

	@Override
	public void writeEndStatement()
	{
		getInnerWriter().write(";\r\n");
	}

	@Override
	public void writeIdentifier(String identifier)
	{
		getInnerWriter().write("@" + identifier);
	}


	//[SuppressMessage("Microsoft.Globalization", "CA1308:NormalizeStringsToUppercase", Justification = "Lowercase is intended here. C# boolean literals are all lowercase")]
	@Override
	public void writeBooleanLiteral(boolean value)
	{
		writeSnippet((new Boolean(value)).toString().toLowerCase());
	}

	@Override
	protected void emitStartLambdaExpression(String[] parameterNames)
	{
		if (parameterNames == null)
		{
			//throw new ArgumentNullException("parameterNames");
		}

		if (parameterNames.length == 0 || parameterNames.length > 1)
		{
			getInnerWriter().write("(");
		}
		writeCommaSeparatedList(parameterNames, (t)->getInnerWriter().write(t));
		if (parameterNames.length == 0 || parameterNames.length > 1)
		{
			getInnerWriter().write(")");
		}
		getInnerWriter().write(" => ");
	}

	@Override
	protected void emitStartLambdaDelegate(String[] parameterNames) throws ArgumentNullException
	{
		if (parameterNames == null)
		{
		throw new ArgumentNullException("parameterNames");
		}

		emitStartLambdaExpression(parameterNames);
		getInnerWriter().write("{\r\n");
	}

	@Override
	protected void emitEndLambdaDelegate()
	{
		getInnerWriter().write("}");
	}

	@Override
	protected void emitStartConstructor(String typeName) throws ArgumentNullException
	{
		if (typeName == null)
		{
			throw new ArgumentNullException("typeName");
		}

		getInnerWriter().write("new ");
		getInnerWriter().write(typeName);
		getInnerWriter().write("(");
	}

	@Override
	public void writeReturn()
	{
		getInnerWriter().write("return ");
	}

	@Override
	public void writeLinePragma(Integer lineNumber, String fileName)
	{
		getInnerWriter().write("\r\n");
		if (lineNumber != null)
		{
			getInnerWriter().write("#line ");
			getInnerWriter().write(lineNumber);
			getInnerWriter().write(" \"");
			getInnerWriter().write(fileName);
			getInnerWriter().write("\"");
			getInnerWriter().write("\r\n");
			//getInnerWriter().writeLine();
		}
		else
		{
			//getInnerWriter().write("\r\n");
			getInnerWriter().write("#line default");
			getInnerWriter().write("\r\n");
			getInnerWriter().write("#line hidden");
			getInnerWriter().write("\r\n");
		}
	}

	@Override
	public void writeHiddenLinePragma()
	{
		getInnerWriter().write("#line hidden\r\n");
	}

	@Override
	public void writeHelperHeaderPrefix(String templateTypeName, boolean isStatic)
	{
		getInnerWriter().write("public ");
		if (isStatic)
		{
			getInnerWriter().write("static ");
		}
		getInnerWriter().write(templateTypeName);
		getInnerWriter().write(" ");
	}
}