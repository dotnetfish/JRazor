package com.superstudio.jrazor.editor;

import java.util.Collections;
import java.util.function.Function;

import com.superstudio.commons.HashCodeCombiner;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.jrazor.PartialParseResult;
import com.superstudio.jrazor.parser.syntaxTree.AcceptedCharacters;
import com.superstudio.jrazor.parser.syntaxTree.Span;
import com.superstudio.jrazor.parser.syntaxTree.SpanBuilder;
import com.superstudio.jrazor.text.SourceLocation;
import com.superstudio.jrazor.text.SourceLocationTracker;
import com.superstudio.jrazor.text.TextChange;
import com.superstudio.jrazor.tokenizer.symbols.ISymbol;




// Manages edits to a span
public class SpanEditHandler
{
 
	//[SuppressMessage("Microsoft.Design", "CA1006:DoNotNestGenericTypesInMemberSignatures", Justification = "Func<T> is the recommended delegate type and requires this level of nesting.")]
	public SpanEditHandler(Function<String, Iterable<? extends ISymbol>> tokenizer)
	{
		this(tokenizer, AcceptedCharacters.Any);
	}

 
	//[SuppressMessage("Microsoft.Design", "CA1006:DoNotNestGenericTypesInMemberSignatures", Justification = "Func<T> is the recommended delegate type and requires this level of nesting.")]
	public SpanEditHandler(Function<String, Iterable<? extends ISymbol>> tokenizer, AcceptedCharacters accepted)
	{
		setAcceptedCharacters(accepted);
		setTokenizer(tokenizer);
	}

	private AcceptedCharacters privateAcceptedCharacters = AcceptedCharacters.forValue(0);
	public final AcceptedCharacters getAcceptedCharacters()
	{
		return privateAcceptedCharacters;
	}
	public final void setAcceptedCharacters(AcceptedCharacters value)
	{
		privateAcceptedCharacters = value;
	}

	/** 
	 Provides a set of hints to editors which may be manipulating the document in which this span is located.
	 
	*/
	private editorHints privateEditorHints = editorHints.forValue(0);
	public final editorHints getEditorHints()
	{
		return privateEditorHints;
	}
	public final void setEditorHints(editorHints value)
	{
		privateEditorHints = value;
	}

 
	//[SuppressMessage("Microsoft.Design", "CA1006:DoNotNestGenericTypesInMemberSignatures", Justification = "Func<T> is the recommended delegate type and requires this level of nesting.")]
	private Function<String, Iterable<? extends ISymbol>> privateTokenizer;
	public final Function<String, Iterable<? extends ISymbol>> getTokenizer()
	{
		return privateTokenizer;
	}
	public final void setTokenizer(Function<String, Iterable<? extends ISymbol>> value)
	{
		privateTokenizer = value;
	}

	public static SpanEditHandler createDefault()
	{
 
		return createDefault(s -> Collections.emptyList());
	}

 
	//[SuppressMessage("Microsoft.Design", "CA1006:DoNotNestGenericTypesInMemberSignatures", Justification = "Func<T> is the recommended delegate type and requires this level of nesting.")]
	public static SpanEditHandler createDefault(Function<String, Iterable<? extends ISymbol>> tokenizer)
	{
		return new SpanEditHandler(tokenizer);
	}

	public EditResult applyChange(Span target, TextChange change)
	{
		return applyChange(target, change, false);
	}

	public EditResult applyChange(Span target, TextChange change, boolean force)
	{
		PartialParseResult result = PartialParseResult.Accepted;
		TextChange normalized = change.normalize().clone();
		if (!force)
		{
			result = canAcceptChange(target, normalized);
		}

		// If the change is accepted then apply the change
		if (result.hasFlag(PartialParseResult.Accepted))
		{
			return new EditResult(result, updateSpan(target, normalized));
		}
		return new EditResult(result, new SpanBuilder(target));
	}

	public boolean ownsChange(Span target, TextChange change)
	{
		int end = target.getStart().getAbsoluteIndex() + target.getLength();
		int changeOldEnd = change.getOldPosition() + change.getOldLength();
		return change.getOldPosition() >= target.getStart().getAbsoluteIndex() && (changeOldEnd < end || (changeOldEnd == end && getAcceptedCharacters() != AcceptedCharacters.None));
	}

	protected PartialParseResult canAcceptChange(Span target, TextChange normalizedChange)
	{
		return PartialParseResult.Rejected;
	}

	protected SpanBuilder updateSpan(Span target, TextChange normalizedChange)
	{
		String newContent = normalizedChange.applyChange(target);
		SpanBuilder newSpan = new SpanBuilder(target);
		newSpan.clearSymbols();
		for (ISymbol sym : getTokenizer().apply(newContent))
		{
			sym.offsetStart(target.getStart());
			newSpan.accept(sym);
		}
		if (target.getNext() != null)
		{
			SourceLocation newEnd = SourceLocationTracker.CalculateNewLocation(target.getStart(), newContent);
			target.getNext().changeStart(newEnd);
		}
		return newSpan;
	}

	protected static boolean isAtEndOfFirstLine(Span target, TextChange change)
	{
		int endOfFirstLine =StringHelper.indexOfAny(target.getContent(), (char)0x000d, (char)0x000a, (char)0x2028, (char)0x2029 );
		return (endOfFirstLine == -1 || (change.getOldPosition() - target.getStart().getAbsoluteIndex()) <= endOfFirstLine);
	}

	/** 
	 Returns true if the specified change is an insertion of text at the end of this span.
	 
	*/
	protected static boolean isEndInsertion(Span target, TextChange change)
	{
		return change.getIsInsert() && isAtEndOfSpan(target, change);
	}

	/** 
	 Returns true if the specified change is an insertion of text at the end of this span.
	 
	*/
	protected static boolean isEndDeletion(Span target, TextChange change)
	{
		return change.getIsDelete() && isAtEndOfSpan(target, change);
	}

	/** 
	 Returns true if the specified change is a replacement of text at the end of this span.
	 
	*/
	protected static boolean isEndReplace(Span target, TextChange change)
	{
		return change.getIsReplace() && isAtEndOfSpan(target, change);
	}

 
	//[SuppressMessage("Microsoft.Design", "CA1011:ConsiderPassingBaseTypesAsParameters", Justification = "This method should only be used on Spans")]
	protected static boolean isAtEndOfSpan(Span target, TextChange change)
	{
		return (change.getOldPosition() + change.getOldLength()) == (target.getStart().getAbsoluteIndex() + target.getLength());
	}

	/** 
	 Returns the old text referenced by the change.
	 
	 
	 If the content has already been updated by applying the change, this data will be _invalid_
	 
	*/
	protected static String getOldText(Span target, TextChange change)
	{
		return target.getContent().substring(change.getOldPosition() - target.getStart().getAbsoluteIndex(), change.getOldPosition() - target.getStart().getAbsoluteIndex() + change.getOldLength());
	}

	// Is the specified span to the right of this span and immediately adjacent?
	public static boolean isAdjacentOnRight(Span target, Span other)
	{
		return target.getStart().getAbsoluteIndex() < other.getStart().getAbsoluteIndex() && target.getStart().getAbsoluteIndex() + target.getLength() == other.getStart().getAbsoluteIndex();
	}

	// Is the specified span to the left of this span and immediately adjacent?
	public static boolean isAdjacentOnLeft(Span target, Span other)
	{
		return other.getStart().getAbsoluteIndex() < target.getStart().getAbsoluteIndex() && other.getStart().getAbsoluteIndex() + other.getLength() == target.getStart().getAbsoluteIndex();
	}

	@Override
	public String toString()
	{
		return getClass().getName() + ";Accepts:" + getAcceptedCharacters() + ((getEditorHints() == editorHints.None) ? "" : (";Hints: " + getEditorHints().toString()));
	}

	@Override
	public boolean equals(Object obj)
	{
		SpanEditHandler other = (SpanEditHandler)((obj instanceof SpanEditHandler) ? obj : null);
		return other != null && getAcceptedCharacters() == other.getAcceptedCharacters() && getEditorHints() == other.getEditorHints();
	}

	@Override
	public int hashCode()
	{
		return HashCodeCombiner.Start().Add(getAcceptedCharacters()).Add(getEditorHints()).getCombinedHash();
	}
}