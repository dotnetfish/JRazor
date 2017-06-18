package com.superstudio.web.razor;


import com.superstudio.commons.Path;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.csharpbridge.action.ActionTwo;
import com.superstudio.web.RazorResources;
import com.superstudio.web.razor.editor.BackgroundParser;
import com.superstudio.web.razor.editor.EditResult;
import com.superstudio.web.razor.editor.RazorEditorTrace;
import com.superstudio.web.razor.editor.SpanEditHandler;
import com.superstudio.web.razor.parser.syntaxTree.AutoCompleteEditHandler;
import com.superstudio.web.razor.parser.syntaxTree.Block;
import com.superstudio.web.razor.parser.syntaxTree.Span;
import com.superstudio.web.razor.text.TextChange;



/** 
 Parser used by editors to avoid reparsing the entire document on each text change
 
 
 This parser is designed to allow editors to avoid having to worry about incremental parsing.
 The checkForStructureChanges method can be called with every change made by a user in an editor and
 the parser will provide a result indicating if it was able to incrementally reparse the document.
 
 The general workflow for editors with this parser is:
 0. User edits document
 1. Editor builds TextChange structure describing the edit and providing a reference to the _updated_ text buffer
 2. Editor calls checkForStructureChanges passing in that change.
 3. Parser determines if the change can be simply applied to an existing parse tree node
   a.  If it can, the Parser updates its parse tree and returns PartialParseResult.Accepted
   b.  If it can not, the Parser starts a background parse task and return PartialParseResult.Rejected
 NOTE: Additional flags can be applied to the PartialParseResult, see that enum for more details.  However,
	   the Accepted or Rejected flags will ALWAYS be present
 
 A change can only be incrementally parsed if a single, unique, Span (see System.Web.Razor.Parser.SyntaxTree) in the syntax tree can
 be identified as owning the entire change.  For example, if a change overlaps with multiple spans, the change cannot be
 parsed incrementally and a full reparse is necessary.  A Span "owns" a change if the change occurs either a) entirely
 within it's boundaries or b) it is a pure insertion (see TextChange) at the end of a Span whose CanGrow flag (see Span) is
 true.
 
 Even if a single unique Span owner can be identified, it's possible the edit will cause the Span to split or merge with other
 Spans, in which case, a full reparse is necessary to identify the extent of the changes to the tree.
 
 When the RazorEditorParser returns Accepted, it updates CurrentParseTree immediately.  However, the editor is expected to
 update it's own data structures independently.  It can use CurrentParseTree to do this, as soon as the editor returns from
 checkForStructureChanges, but it should (ideally) have logic for doing so without needing the new tree.
 
 When Rejected is returned by checkForStructureChanges, a background parse task has _already_ been started.  When that task
 finishes, the DocumentStructureChanged event will be fired containing the new generated code, parse tree and a reference to
 the original TextChange that caused the reparse, to allow the editor to resolve the new tree against any changes made since 
 calling checkForStructureChanges.
 
 If a call to checkForStructureChanges occurs while a reparse is already in-progress, the reparse is cancelled IMMEDIATELY
 and Rejected is returned without attempting to reparse.  This means that if a conusmer calls checkForStructureChanges, which
 returns Rejected, then calls it again before DocumentParseComplete is fired, it will only recieve one DocumentParseComplete
 event, for the second change.
 
*/
public class RazorEditorParser implements AutoCloseable
{
	// lock for this document
	private Span _lastChangeOwner;
	private Span _lastAutoCompleteSpan;
	private BackgroundParser _parser;
	private Block _currentParseTree;

	/** 
	 Constructs the editor parser.  One instance should be used per active editor.  This
	 instance _can_ be shared among reparses, but should _never_ be shared between documents.
	 
	 @param host The <see cref="RazorEngineHost"/> which defines the environment in which the generated code will live.  <see cref="F:RazorEngineHost.DesignTimeMode"/> should be set if design-time code mappings are desired
	 @param sourceFileName The physical path to use in line pragmas
	 * @throws Exception 
	*/
	public RazorEditorParser(RazorEngineHost host, String sourceFileName) throws Exception
	{
		if (host == null)
		{
			//throw new ArgumentNullException("host");
		}
		if (StringHelper.isNullOrEmpty(sourceFileName))
		{
			throw new IllegalArgumentException(
					StringHelper.format(RazorResources.getResource(RazorResources.Argument_Cannot_Be_Null_Or_Empty),sourceFileName));
		}

		setHost(host);
		setFileName(sourceFileName);
		_parser = new BackgroundParser(host, sourceFileName);

		_parser.setResultsReady((sender, args) -> onDocumentParseComplete(args));
		_parser.start();
	}

	/** 
	 Event fired when a full reparse of the document completes
	 
	*/

	private  ActionTwo<Object,DocumentParseCompleteEventArgs> documentParseComplete;

	private RazorEngineHost privateHost;
	public final RazorEngineHost getHost()
	{
		return privateHost;
	}
	private void setHost(RazorEngineHost value)
	{
		privateHost = value;
	}
	private String privateFileName;
	public final String getFileName()
	{
		return privateFileName;
	}
	private void setFileName(String value)
	{
		privateFileName = value;
	}
	private boolean privateLastResultProvisional;
	public final boolean getLastResultProvisional()
	{
		return privateLastResultProvisional;
	}
	private void setLastResultProvisional(boolean value)
	{
		privateLastResultProvisional = value;
	}
	public final Block getCurrentParseTree()
	{
		return _currentParseTree;
	}


	//[SuppressMessage("Microsoft.Design", "CA1024:UsePropertiesWhereAppropriate", Justification = "Since this method is heavily affected by side-effects, particularly calls to checkForStructureChanges, it should not be made into a property")]
	public String getAutoCompleteString()
	{
		if (_lastAutoCompleteSpan != null)
		{
			SpanEditHandler tempVar = _lastAutoCompleteSpan.getEditHandler();
			AutoCompleteEditHandler editHandler = (AutoCompleteEditHandler)((tempVar instanceof AutoCompleteEditHandler) ? tempVar : null);
			if (editHandler != null)
			{
				return editHandler.getAutoCompleteString();
			}
		}
		return null;
	}

	/** 
	 Determines if a change will cause a structural change to the document and if not, applies it to the existing tree.
	 If a structural change would occur, automatically starts a reparse
	 
	 
	 NOTE: The initial incremental parsing check and actual incremental parsing (if possible) occurs
	 on the callers thread.  However, if a full reparse is needed, this occurs on a background thread.
	 
	 @param change The change to apply to the parse tree
	 @return A PartialParseResult value indicating the result of the incremental parse
	*/
	public PartialParseResult checkForStructureChanges(TextChange change)
	{
		// Validate the change
		//Long elapsedMs = null;

//#if EDITOR_TRACING
		//Stopwatch sw = new Stopwatch();
		//sw.start();
//#endif
		RazorEditorTrace.traceLine(RazorResources.getResource(RazorResources.Trace_EditorReceivedChange), Path.GetFileName(getFileName()), change);
		if (change.getNewBuffer() == null)
		{
			throw new IllegalArgumentException(String.format(RazorResources.getResource(RazorResources.Structure_Member_CannotBeNull), "Buffer", "TextChange")+"change");
		}

		PartialParseResult result = PartialParseResult.Rejected;

		// If there isn't already a parse underway, try partial-parsing
		String changeString = "";

//		using (_parser.synchronizeMainThreadState())
;
		try(AutoCloseable disposable=_parser.synchronizeMainThreadState())
		{
			// Capture the string value of the change while we're synchronized
			changeString = change.toString();

			// Check if we can partial-parse
			if (getCurrentParseTree() != null && _parser.getIsIdle())
			{
				result = tryPartialParse(change);
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}

		// If partial parsing failed or there were outstanding parser tasks, start a full reparse
		if (result.HasFlag(PartialParseResult.Rejected))
		{
			_parser.queueChange(change);
		}

		// Otherwise, remember if this was provisionally accepted for next partial parse
		setLastResultProvisional(result.HasFlag(PartialParseResult.Provisional));
		verifyFlagsAreValid(result);


//#if EDITOR_TRACING
		//sw.stop();
		//elapsedMs = sw.ElapsedMilliseconds;
		//sw.reset();
//#endif
		//RazorEditorTrace.traceLine(RazorResources.getResource(RazorResources.Trace_EditorProcessedChange(), Path.GetFileName(getFileName()), changeString, elapsedMs != null ? elapsedMs.toString(CultureInfo.InvariantCulture) : "?", result.toString());
		return result;
	}

	/** 
	 Disposes of this parser.  Should be called when the editor window is closed and the document is unloaded.
	 
	*/
	

protected void dispose(boolean disposing)
	{
		if (disposing)
		{
			_parser.close();
		}
	}

	private PartialParseResult tryPartialParse(TextChange change)
	{
		PartialParseResult result = PartialParseResult.Rejected;

		// Try the last change owner
		if (_lastChangeOwner != null && _lastChangeOwner.getEditHandler().ownsChange(_lastChangeOwner, change))
		{
			EditResult editResult = _lastChangeOwner.getEditHandler().applyChange(_lastChangeOwner, change);
			result = editResult.getResult();
			if (!editResult.getResult().HasFlag(PartialParseResult.Rejected))
			{
				_lastChangeOwner.replaceWith(editResult.getEditedSpan());
			}

			return result;
		}

		// Locate the span responsible for this change
		_lastChangeOwner = getCurrentParseTree().LocateOwner(change);

		if (getLastResultProvisional())
		{
			// Last change owner couldn't accept this, so we must do a full reparse
			result = PartialParseResult.Rejected;
		}
		else if (_lastChangeOwner != null)
		{
			EditResult editRes = _lastChangeOwner.getEditHandler().applyChange(_lastChangeOwner, change);
			result = editRes.getResult();
			if (!editRes.getResult().HasFlag(PartialParseResult.Rejected))
			{
				_lastChangeOwner.replaceWith(editRes.getEditedSpan());
			}
			if (result.HasFlag(PartialParseResult.AutoCompleteBlock))
			{
				_lastAutoCompleteSpan = _lastChangeOwner;
			}
			else
			{
				_lastAutoCompleteSpan = null;
			}
		}
		return result;
	}


	private void onDocumentParseComplete(DocumentParseCompleteEventArgs args)
	{


		try(AutoCloseable disposable=_parser.synchronizeMainThreadState())
		{
			_currentParseTree = args.getGeneratorResults().getDocument();
			_lastChangeOwner = null;
		}catch (Exception ex){
			ex.printStackTrace();
		}

		////Debug.Assert(args != null, "Event arguments cannot be null");
		//TODO
		ActionTwo<Object,DocumentParseCompleteEventArgs> handler = getDocumentParseComplete();
		if (handler != null)
		{
			try
			{
				handler.execute(this, args);
				//DocumentParseComplete(this, args);
			}
			catch (RuntimeException ex)
			{
				////Debug.writeLine("[RzEd] Document parse Complete Handler Threw: " + ex.toString());
			}
		}
	}


	//[Conditional("//Debug")]
	private static void verifyFlagsAreValid(PartialParseResult result)
	{
		/*//Debug.Assert(result.HasFlag(PartialParseResult.Accepted) || result.HasFlag(PartialParseResult.Rejected), "Partial parse result does not have either of Accepted or Rejected flags set");
		//Debug.Assert(result.HasFlag(PartialParseResult.Rejected) || !result.HasFlag(PartialParseResult.SpanContextChanged), "Partial parse result was Accepted AND had SpanContextChanged flag set");
		//Debug.Assert(result.HasFlag(PartialParseResult.Rejected) || !result.HasFlag(PartialParseResult.AutoCompleteBlock), "Partial parse result was Accepted AND had AutoCompleteBlock flag set");
		//Debug.Assert(result.HasFlag(PartialParseResult.Accepted) || !result.HasFlag(PartialParseResult.Provisional), "Partial parse result was Rejected AND had Provisional flag set");
*/	}
	@Override
	public  void close(){
		dispose();
	}
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	public ActionTwo<Object,DocumentParseCompleteEventArgs> getDocumentParseComplete() {
		return documentParseComplete;
	}
	public void setDocumentParseComplete(ActionTwo<Object,DocumentParseCompleteEventArgs> documentParseComplete) {
		this.documentParseComplete = documentParseComplete;
	}
}