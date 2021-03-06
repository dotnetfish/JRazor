﻿package com.superstudio.jrazor.editor;

import java.util.Set;
import java.util.function.Function;

import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.HashCodeCombiner;
import com.superstudio.commons.TextReader;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.jrazor.PartialParseResult;
import com.superstudio.jrazor.parser.ParserHelpers;
import com.superstudio.jrazor.parser.syntaxTree.AcceptedCharacters;
import com.superstudio.jrazor.parser.syntaxTree.Span;
import com.superstudio.jrazor.text.TextChange;
import com.superstudio.jrazor.tokenizer.symbols.ISymbol;



public class ImplicitExpressionEditHandler extends SpanEditHandler {
	 
	 
	// [SuppressMessage("Microsoft.Design",
	// "CA1006:DoNotNestGenericTypesInMemberSignatures", Justification =
	// "Func<T> is the recommended delegate type and requires this level of
	// nesting.")]
	public ImplicitExpressionEditHandler(Function<String, Iterable<? extends ISymbol>> tokenizer, Set<String> keywords,
			boolean acceptTrailingDot) {
		super(tokenizer);
		Initialize(keywords, acceptTrailingDot);
	}

	private boolean privateAcceptTrailingDot;

	public final boolean getAcceptTrailingDot() {
		return privateAcceptTrailingDot;
	}

	private void setAcceptTrailingDot(boolean value) {
		privateAcceptTrailingDot = value;
	}

	private Set<String> privateKeywords;

	public final Set<String> getKeywords() {
		return privateKeywords;
	}

	private void setKeywords(Set<String> value) {
		privateKeywords = value;
	}

	@Override
	public String toString() {
		return String.format("%s;ImplicitExpression[%s];K:%d", super.toString(), getAcceptTrailingDot() ? "ATD" : "RTD",
				getKeywords().size());
	}

	@Override
	public boolean equals(Object obj) {
		ImplicitExpressionEditHandler other = (ImplicitExpressionEditHandler) ((obj instanceof ImplicitExpressionEditHandler)
				? obj : null);
		return other != null && super.equals(other) && 
				getKeywords().equals(other.getKeywords())
				&& getAcceptTrailingDot() == other.getAcceptTrailingDot();
	}

	@Override
	public int hashCode() {
		return HashCodeCombiner.Start().Add(super.hashCode()).Add(getAcceptTrailingDot()).Add(getKeywords())
				.getCombinedHash();
	}

	@Override
	protected PartialParseResult canAcceptChange(Span target, TextChange normalizedChange) {
		if (getAcceptedCharacters() == AcceptedCharacters.Any) {
			return PartialParseResult.Rejected;
		}

		// In some editors intellisense insertions are handled as "dotless
		// commits". If an intellisense selection is confirmed
		// via something like '.' a dotless commit will append a '.' and then
		// insert the remaining intellisense selection prior
		// to the appended '.'. This 'if' statement attempts to accept the
		// intermediate steps of a dotless commit via
		// intellisense. It will accept two cases:
		// 1. '@foo.' -> '@foobaz.'.
		// 2. '@foobaz..' -> '@foobaz.bar.'. Includes Sub-cases '@foobaz()..' ->
		// '@foobaz().bar.' etc.
		// The key distinction being the double '.' in the second case.
		if (IsDotlessCommitInsertion(target, normalizedChange)) {
			return handleDotlessCommitInsertion(target);
		}

		if (isAcceptableReplace(target, normalizedChange)) {
			return handleReplacement(target, normalizedChange);
		}
		int changeRelativePosition = normalizedChange.getOldPosition() - target.getStart().getAbsoluteIndex();

		// Get the edit context
		Character lastChar = null;
		if (changeRelativePosition > 0 && target.getContent().length() > 0) {
			lastChar = target.getContent().charAt(changeRelativePosition - 1);
		}

		// Don't support 0->1 length edits
		if (lastChar == null) {
			return PartialParseResult.Rejected;
		}

		// Accepts cases when insertions are made at the end of a span or '.' is
		// inserted within a span.
		if (isAcceptableInsertion(target, normalizedChange)) {
			// Handle the insertion
			return handleInsertion(target, lastChar, normalizedChange);
		}

		if (isAcceptableDeletion(target, normalizedChange)) {
			return handleDeletion(target, lastChar, normalizedChange);
		}

		return PartialParseResult.Rejected;
	}

	private void Initialize(Set<String> keywords, boolean acceptTrailingDot) {
		setKeywords((keywords != null) ? keywords : new java.util.HashSet<String>());
		setAcceptTrailingDot(acceptTrailingDot);
	}

	// A dotless commit is the process of inserting a '.' with an intellisense
	// selection.
	private static boolean IsDotlessCommitInsertion(Span target, TextChange change) {
		return IsNewDotlessCommitInsertion(target, change) || IsSecondaryDotlessCommitInsertion(target, change);
	}

	// Completing 'DateTime' in intellisense with a '.' could result in:
	// '@DateT' -> '@DateT.' -> '@DateTime.' which is accepted.
	private static boolean IsNewDotlessCommitInsertion(Span target, TextChange change) {
		return !isAtEndOfSpan(target, change) && change.getNewPosition() > 0 && change.getNewLength() > 0
				&& target.getContent().endsWith(".") && ParserHelpers.isIdentifier(change.getNewText(), false)
				&& (change.getOldLength() == 0 || ParserHelpers.isIdentifier(change.getOldText(), false));
	}

	// Once a dotless commit has been performed you then have something like
	// '@DateTime.'. This scenario is used to detect the
	// situation when you try to perform another dotless commit resulting in a
	// textchange with '..'. Completing 'DateTime.Now'
	// in intellisense with a '.' could result in: '@DateTime.' -> '@DateTime..'
	// -> '@DateTime.Now.' which is accepted.
	private static boolean IsSecondaryDotlessCommitInsertion(Span target, TextChange change) {
		// Do not need to worry about other punctuation, just looking for double
		// '.' (after change)
		return change.getNewLength() == 1 && !StringHelper.isNullOrEmpty(target.getContent())
				&& target.getContent().endsWith(".") && change.getNewText().equals(".") && change.getOldLength() == 0;
	}

	private static boolean isAcceptableReplace(Span target, TextChange change) {
		return isEndReplace(target, change) || (change.getIsReplace() && remainingIsWhitespace(target, change));
	}

	private static boolean isAcceptableDeletion(Span target, TextChange change) {
		return isEndDeletion(target, change) || (change.getIsDelete() && remainingIsWhitespace(target, change));
	}

	// Acceptable insertions can occur at the end of a span or when a '.' is
	// inserted within a span.
	private static boolean isAcceptableInsertion(Span target, TextChange change) {
		return change.getIsInsert()
				&& (isAcceptableEndInsertion(target, change) || isAcceptableInnerInsertion(target, change));
	}

	// Accepts character insertions at the end of spans. AKA: '@foo' -> '@fooo'
	// or '@foo' -> '@foo ' etc.
	private static boolean isAcceptableEndInsertion(Span target, TextChange change) {
		assert change.getIsInsert();

		return isAtEndOfSpan(target, change) || remainingIsWhitespace(target, change);
	}

	// Accepts '.' insertions in the middle of spans. Ex: '@foo.baz.bar' ->
	// '@foo..baz.bar'
	// This is meant to allow intellisense when editing a span.
	 
	 
	// [SuppressMessage("Microsoft.Usage", "CA1801:ReviewUnusedParameters",
	// MessageId = "target", Justification = "The 'target' parameter is used in
	// //Debug to validate that the function is called in the correct
	// context.")]
	private static boolean isAcceptableInnerInsertion(Span target, TextChange change) {
		assert change.getIsInsert();

		// Ensure that we're actually inserting in the middle of a span and not
		// at the end.
		// This case will fail if the IsAcceptableEndInsertion does not capture
		// an end insertion correctly.
		assert !isAtEndOfSpan(target, change);

		return change.getNewPosition() > 0 && change.getNewText().equals(".");
	}

	private static boolean remainingIsWhitespace(Span target, TextChange change) {
		int offset = (change.getOldPosition() - target.getStart().getAbsoluteIndex()) + change.getOldLength();
		return StringHelper.isNullOrWhiteSpace(target.getContent().substring(offset));
	}

	private PartialParseResult handleDotlessCommitInsertion(Span target) {
		PartialParseResult result = PartialParseResult.Accepted;
		if (!getAcceptTrailingDot() && target.getContent().endsWith(".")) {
			result = PartialParseResult.forValue(result.getValue() | PartialParseResult.Provisional.getValue());
		}
		return result;
	}

	private PartialParseResult handleReplacement(Span target, TextChange change) {
		// Special Case for IntelliSense commits.
		// When IntelliSense commits, we get two changes (for example user typed
		// "Date", then committed "DateTime" by pressing ".")
		// 1. Insert "." at the end of this span
		// 2. Replace the "Date." at the end of the span with "DateTime."
		// We need partial parsing to accept case #2.
		String oldText = getOldText(target, change);

		PartialParseResult result = PartialParseResult.Rejected;
		if (endsWithDot(oldText) && endsWithDot(change.getNewText())) {
			result = PartialParseResult.Accepted;
			if (!getAcceptTrailingDot()) {
				// result |= PartialParseResult.Provisional;
				result = PartialParseResult.forValue(result.getValue() | PartialParseResult.Provisional.getValue());

			}
		}
		return result;
	}

	private PartialParseResult handleDeletion(Span target, char previousChar, TextChange change) {
		// What's left after deleting?
		if (previousChar == '.') {
			return tryAcceptChange(target, change, PartialParseResult
					.forValue(PartialParseResult.Accepted.getValue() | PartialParseResult.Provisional.getValue()));
		} else if (ParserHelpers.isIdentifierPart(previousChar)) {
			return tryAcceptChange(target, change);
		} else {
			return PartialParseResult.Rejected;
		}
	}

	private PartialParseResult handleInsertion(Span target, char previousChar, TextChange change) {
		// What are we inserting after?
		if (previousChar == '.') {
			return handleInsertionAfterDot(target, change);
		} else if (ParserHelpers.isIdentifierPart(previousChar) || previousChar == ')' || previousChar == ']') {
			return handleInsertionAfterIdPart(target, change);
		} else {
			return PartialParseResult.Rejected;
		}
	}

	private PartialParseResult handleInsertionAfterIdPart(Span target, TextChange change) {
		// If the insertion is a full identifier part, accept it
		if (ParserHelpers.isIdentifier(change.getNewText(), false)) {
			return tryAcceptChange(target, change);
		} else if (endsWithDot(change.getNewText())) {
			// Accept it, possibly provisionally
			PartialParseResult result = PartialParseResult.Accepted;
			if (!getAcceptTrailingDot()) {
				// result |= PartialParseResult.Provisional;
				result = PartialParseResult.forValue(result.getValue() | PartialParseResult.Provisional.getValue());
			}
			return tryAcceptChange(target, change, result);
		} else {
			return PartialParseResult.Rejected;
		}
	}

	private static boolean endsWithDot(String content) {
		char[] charArray=content.substring(content.length() - 1).toCharArray();
		//ArrayList list=new ArrayList<Char>();
		//list.
		return (content.length() == 1 && content.charAt(0) == '.') 
				|| (content.charAt(content.length() - 1) == '.'
				&& CollectionHelper.all(charArray,(t)->ParserHelpers.isIdentifierPart(t)));
		
	}

	private PartialParseResult handleInsertionAfterDot(Span target, TextChange change) {
		// If the insertion is a full identifier or another dot, accept it
		if (ParserHelpers.isIdentifier(change.getNewText()) || change.getNewText().equals(".")) {
			return tryAcceptChange(target, change);
		}
		return PartialParseResult.Rejected;
	}

	// C# TO JAVA CONVERTER TODO TASK: C# optional parameters are not converted
	// to Java:
	// ORIGINAL LINE: private PartialParseResult TryAcceptChange(Span target,
	// TextChange change, PartialParseResult acceptResult =
	// PartialParseResult.Accepted)
	private PartialParseResult tryAcceptChange(Span target, TextChange change, PartialParseResult acceptResult)
	{
		String content = change.applyChange(target);
		if (startsWithKeyword(content))
		{
			//return PartialParseResult.Rejected | PartialParseResult.SpanContextChanged;
			return PartialParseResult.forValue(PartialParseResult.Rejected.getValue() | PartialParseResult.SpanContextChanged.getValue());
			
		}

		return acceptResult;
	}
	
	private PartialParseResult tryAcceptChange(Span target, TextChange change)
	{
		return tryAcceptChange(target,change,PartialParseResult.Accepted);
	}


	private boolean startsWithKeyword(String newContent)  {
		 
		 
		// using (StringReader reader = new StringReader(newContent))
		TextReader reader = new TextReader(newContent);
		try {
			String content=reader.readWhile((t)->ParserHelpers.isIdentifierPart(t));
			return getKeywords().contains(content);
		} finally {
			reader.close();
			//reader.
			//reader.dispose();
		}
	}
}