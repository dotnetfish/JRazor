package com.superstudio.web.razor.parser;

import com.superstudio.commons.CollectionHelper;

import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.web.RazorResources;
import com.superstudio.web.razor.ParserResults;
import com.superstudio.web.razor.parser.syntaxTree.*;
import com.superstudio.web.razor.text.ITextDocument;
import com.superstudio.web.razor.text.SourceLocation;
import com.superstudio.web.razor.text.TextDocumentReader;
import com.superstudio.web.razor.utils.DisposableAction;







public class ParserContext
{
	private Integer _ownerTaskId;

	private boolean _terminated = false;

	private java.util.Stack<BlockBuilder> _blockStack = new java.util.Stack<BlockBuilder>();

	public ParserContext(ITextDocument source, ParserBase codeParser, ParserBase markupParser, ParserBase activeParser)
	{
		if (source == null)
		{
			////throw new ArgumentNullException("source");
		}
		if (codeParser == null)
		{
			////throw new ArgumentNullException("codeParser");
		}
		if (markupParser == null)
		{
			////throw new ArgumentNullException("markupParser");
		}
		if (activeParser == null)
		{
			////throw new ArgumentNullException("activeParser");
		}
		if (activeParser != codeParser && activeParser != markupParser)
		{
			throw new IllegalArgumentException(RazorResources.getActiveParser_Must_Be_Code_Or_Markup_Parser()+ "activeParser");
		}

		CaptureOwnerTask();

		setSource(new TextDocumentReader(source));
		setCodeParser(codeParser);
		setMarkupParser(markupParser);
		setActiveParser(activeParser);
		setErrors(new java.util.ArrayList<RazorError>());
	}

	private java.util.List<RazorError> privateErrors;
	public final java.util.List<RazorError> getErrors()
	{
		return privateErrors;
	}
	private void setErrors(java.util.List<RazorError> value)
	{
		privateErrors = value;
	}
	private TextDocumentReader privateSource;
	public final TextDocumentReader getSource()
	{
		return privateSource;
	}
	public final void setSource(TextDocumentReader value)
	{
		privateSource = value;
	}
	private ParserBase privateCodeParser;
	public final ParserBase getCodeParser()
	{
		return privateCodeParser;
	}
	private void setCodeParser(ParserBase value)
	{
		privateCodeParser = value;
	}
	private ParserBase privateMarkupParser;
	public final ParserBase getMarkupParser()
	{
		return privateMarkupParser;
	}
	private void setMarkupParser(ParserBase value)
	{
		privateMarkupParser = value;
	}
	private ParserBase privateActiveParser;
	public final ParserBase getActiveParser()
	{
		return privateActiveParser;
	}
	private void setActiveParser(ParserBase value)
	{
		privateActiveParser = value;
	}
	private boolean privateDesignTimeMode;
	public final boolean getDesignTimeMode()
	{
		return privateDesignTimeMode;
	}
	public final void setDesignTimeMode(boolean value)
	{
		privateDesignTimeMode = value;
	}

	public final BlockBuilder getCurrentBlock()
	{
		return _blockStack.peek();
	}

	private Span privateLastSpan;
	public final Span getLastSpan()
	{
		return privateLastSpan;
	}
	private void setLastSpan(Span value)
	{
		privateLastSpan = value;
	}
	private boolean privateWhiteSpaceIsSignificantToAncestorBlock;
	public final boolean getWhiteSpaceIsSignificantToAncestorBlock()
	{
		return privateWhiteSpaceIsSignificantToAncestorBlock;
	}
	public final void setWhiteSpaceIsSignificantToAncestorBlock(boolean value)
	{
		privateWhiteSpaceIsSignificantToAncestorBlock = value;
	}

	public final AcceptedCharacters getLastAcceptedCharacters()
	{
		if (getLastSpan() == null)
		{
			return AcceptedCharacters.None;
		}
		return getLastSpan().getEditHandler().getAcceptedCharacters();
	}

	public final java.util.Stack<BlockBuilder> getBlockStack()
	{
		return _blockStack;
	}

	public final char getCurrentCharacter()
	{
		if (_terminated)
		{
			return '\0';
		}

//#if //Debug
		if (CheckInfiniteLoop())
		{
			return '\0';
		}
//#endif
		int ch = getSource().peek();
		if (ch == -1)
		{
			return '\0';
		}
		return (char)ch;
	}

	public final boolean getEndOfFile()
	{
		return _terminated || getSource().peek() == -1;
	}

	public final void addSpan(Span span) throws Exception
	{
		EnusreNotTerminated();
		if (_blockStack.empty())
		{
			throw new InvalidOperationException(RazorResources.getParserContext_NoCurrentBlock());
		}
		_blockStack.peek().getChildren().add(span);
		setLastSpan(span);
	}

	/** 
	 Starts a block of the specified type
	 
	 @param blockType The type of the block to start
	*/
	public final AutoCloseable startBlock(BlockType blockType)
	{
		try {
			EnusreNotTerminated();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AssertOnOwnerTask();
		BlockBuilder tempVar = new BlockBuilder();
		tempVar.setType(blockType); 
		_blockStack.push(tempVar);
		return new DisposableAction(()->EndBlock());
	}

	/** 
	 Starts a block
	 * @throws Exception 
	 
	*/
	public final AutoCloseable startBlock() throws Exception
	{
		EnusreNotTerminated();
		AssertOnOwnerTask();
		_blockStack.push(new BlockBuilder());
		return new DisposableAction(()->EndBlock());
	}

	/** 
	 Ends the current block
	 * @throws InvalidOperationException 
	 
	*/
	public final void EndBlock() 
	{
		try {
			EnusreNotTerminated();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AssertOnOwnerTask();

		if (_blockStack.empty())
		{
			//throw new InvalidOperationException(RazorResources.getEndBlock_Called_Without_Matching_StartBlock());
		}
		if (_blockStack.size() > 1)
		{
			BlockBuilder block = _blockStack.pop();
			_blockStack.peek().getChildren().add(block.build());
		}
		else
		{
			// If we're at 1, terminate the parser
			_terminated = true;
		}
	}

	/** 
	 Gets a boolean indicating if any of the ancestors of the current block is of the specified type
	 
	*/
	public final boolean IsWithin(BlockType type)
	{
		//Collections.

		return CollectionHelper.any(_blockStack, b -> b.getType() == type);//_blockStack.Any();
		
	}

	public final void SwitchActiveParser()
	{
		try {
			EnusreNotTerminated();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AssertOnOwnerTask();
		//if (ReferenceEquals(getActiveParser(), getCodeParser()))
	
		if (getActiveParser().equals(getCodeParser()) )
		{
			
			setActiveParser(getMarkupParser());
		}
		else
		{
		
			setActiveParser(getCodeParser());
		}
	}

	public final void OnError(SourceLocation location, String message)
	{
		try {
			EnusreNotTerminated();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AssertOnOwnerTask();
		getErrors().add(new RazorError(message, location));
	}

	public final void OnError(SourceLocation location, String message, Object... args)
	{
		try {
			EnusreNotTerminated();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AssertOnOwnerTask();
		OnError(location, String.format( message, args));
	}

	public final ParserResults CompleteParse() throws InvalidOperationException
	{
		if (_blockStack.empty())
		{
			throw new InvalidOperationException(RazorResources.getParserContext_CannotCompleteTree_NoRootBlock());
		}
		if (_blockStack.size() != 1)
		{
			throw new InvalidOperationException(RazorResources.getParserContext_CannotCompleteTree_OutstandingBlocks());
		}
		return new ParserResults(_blockStack.pop().build(), getErrors());
	}


	//[Conditional("//Debug")]
	public final void CaptureOwnerTask()
	{
		/*if (Task.CurrentId != null)
		{
			_ownerTaskId = Task.CurrentId;
		}*/
	}


	//[Conditional("//Debug")]
	public final void AssertOnOwnerTask()
	{
		if (_ownerTaskId != null)
		{

			//assert _ownerTaskId.equals(Task.getCurrentId());
		}
	}


	//[Conditional("//Debug"), SuppressMessage("Microsoft.Performance", "CA1822:MarkMembersAsStatic", Justification = "The method body is empty in Release builds")]
	public final void AssertCurrent(char expected)
	{
		assert getCurrentCharacter() == expected;
	}

	private void EnusreNotTerminated() throws Exception
	{
		if (_terminated)
		{
			throw new InvalidOperationException(RazorResources.getParserContext_ParseComplete());
		}
	}


	private static final int InfiniteLoopCountThreshold = 1000;
	private int _infiniteLoopGuardCount = 0;
	private SourceLocation _infiniteLoopGuardLocation = null;

	public final String getUnparsed()
	{
		String remaining = getSource().readToEnd();
		getSource().setPosition(getSource().getPosition() - remaining.length());
		return remaining;
	}

	private boolean CheckInfiniteLoop()
	{
		// Infinite loop guard
		//  Basically, if this property is accessed 1000 times in a row without having advanced the source reader to the next position, we
		//  cause a parser error
		if (_infiniteLoopGuardLocation != null)
		{
			if (getSource().getLocation() == _infiniteLoopGuardLocation)
			{
				_infiniteLoopGuardCount++;
				if (_infiniteLoopGuardCount > InfiniteLoopCountThreshold)
				{
					////Debug.Fail("An internal parser error is causing an infinite loop at this location.");
					_terminated = true;
					return true;
				}
			}
			else
			{
				_infiniteLoopGuardCount = 0;
			}
		}
		_infiniteLoopGuardLocation = getSource().getLocation().clone();
		return false;
	}
}