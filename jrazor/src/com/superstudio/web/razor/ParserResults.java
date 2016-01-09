package com.superstudio.web.razor;

import com.superstudio.web.razor.parser.syntaxTree.Block;
import com.superstudio.web.razor.parser.syntaxTree.RazorError;



/** 
 Represents the results of parsing a Razor document
 
*/
public class ParserResults
{
	public ParserResults(Block document, java.util.List<RazorError> parserErrors)
	{
		this(parserErrors == null || parserErrors.isEmpty(), document, parserErrors);
	}

	protected ParserResults(boolean success, Block document, java.util.List<RazorError> errors)
	{
		setSuccess(success);
		setDocument(document);
		setParserErrors((errors != null) ? errors : new java.util.ArrayList<RazorError>());
	}

	/** 
	 Indicates if parsing was successful (no errors)
	 
	*/
	private boolean privateSuccess;
	public final boolean getSuccess()
	{
		return privateSuccess;
	}
	private void setSuccess(boolean value)
	{
		privateSuccess = value;
	}

	/** 
	 The root node in the document's syntax tree
	 
	*/
	private Block privateDocument;
	public final Block getDocument()
	{
		return privateDocument;
	}
	private void setDocument(Block value)
	{
		privateDocument = value;
	}

	/** 
	 The list of errors which occurred during parsing.
	 
	*/
	private java.util.List<RazorError> privateParserErrors;
	public final java.util.List<RazorError> getParserErrors()
	{
		return privateParserErrors;
	}
	private void setParserErrors(java.util.List<RazorError> value)
	{
		privateParserErrors = value;
	}
}