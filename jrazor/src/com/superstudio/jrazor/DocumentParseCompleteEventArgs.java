package com.superstudio.jrazor;

import com.superstudio.commons.EventArgs;
import com.superstudio.jrazor.text.TextChange;




/** 
 Arguments for the DocumentParseComplete event in RazorEditorParser
 
*/
public class DocumentParseCompleteEventArgs implements EventArgs
{
	/** 
	 Indicates if the tree structure has actually changed since the previous reparse.
	 
	*/
	private boolean privateTreeStructureChanged;
	public final boolean getTreeStructureChanged()
	{
		return privateTreeStructureChanged;
	}
	public final void setTreeStructureChanged(boolean value)
	{
		privateTreeStructureChanged = value;
	}

	/** 
	 The results of the code generation and parsing
	 
	*/
	private GeneratorResults privateGeneratorResults;
	public final GeneratorResults getGeneratorResults()
	{
		return privateGeneratorResults;
	}
	public final void setGeneratorResults(GeneratorResults value)
	{
		privateGeneratorResults = value;
	}

	/** 
	 The TextChange which triggered the reparse
	 
	*/
	private TextChange privateSourceChange;
	public final TextChange getSourceChange()
	{
		return privateSourceChange;
	}
	public final void setSourceChange(TextChange value)
	{
		privateSourceChange = value;
	}
}