package com.superstudio.jrazor.parser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import com.superstudio.commons.IDisposable;
import com.superstudio.commons.Tuple;
import com.superstudio.commons.csharpbridge.StringComparison;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.jrazor.editor.*;
import com.superstudio.jrazor.generator.*;
import com.superstudio.jrazor.parser.syntaxTree.*;
import com.superstudio.jrazor.resources.RazorResources;
import com.superstudio.jrazor.text.*;
import com.superstudio.jrazor.tokenizer.*;
import com.superstudio.jrazor.tokenizer.symbols.*;
import com.superstudio.web.*;

public class HtmlMarkupParser extends TokenizerBackedParser<HtmlTokenizer, HtmlSymbol, HtmlSymbolType> {
	private SourceLocation _lastTagStart = SourceLocation.Zero;
	private HtmlSymbol _bufferedOpenAngle;

	@Override
	public void parseBlock() throws Exception {
		if (getContext() == null) {
			 throw new 	 InvalidOperationException(RazorResources.getParser_Context_Not_Set());
		}

		// using (PushSpanConfig(DefaultMarkupSpan))
		IDisposable disposable = pushSpanConfig((t) -> defaultMarkupSpan(t));
		try {

			// using (Context.StartBlock(BlockType.Markup))
			IDisposable disposable2 = getContext().StartBlock(BlockType.Markup);
			try {
				if (!nextToken()) {
					return;
				}

				acceptWhile(isSpacingToken(true));

				if (getCurrentSymbol().getType() == HtmlSymbolType.OpenAngle) {
					// "<" -> Implicit Tag Block
					try {
						tagBlock(new java.util.Stack<Tuple<HtmlSymbol, SourceLocation>>());
					} catch (ArgumentNullException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (getCurrentSymbol().getType() == HtmlSymbolType.Transition) {
					// "@" -> Explicit Tag/Single Line Block OR Template
					output(SpanKind.Markup);

					// Definitely have a transition span
					Assert(HtmlSymbolType.Transition);
					acceptAndMoveNext();
					getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
					getSpan().setCodeGenerator(SpanCodeGenerator.Null);
					output(SpanKind.Transition);
					if (at(HtmlSymbolType.Transition)) {
						getSpan().setCodeGenerator(SpanCodeGenerator.Null);
						acceptAndMoveNext();
						output(SpanKind.MetaCode);
					}
					afterTransition();
				} else {
					getContext().onError(getCurrentSymbol().getStart(),
							RazorResources.getParseError_MarkupBlock_Must_Start_With_Tag());
				}
				output(SpanKind.Markup);
			} finally {
				disposable2.dispose();
			}
		} finally {
			disposable.dispose();
		}
	}

	private void defaultMarkupSpan(SpanBuilder span) {
		span.setCodeGenerator(new MarkupCodeGenerator());
		Function<String, Iterable<? extends ISymbol>> func = p -> getLanguage().tokenizeString(p);
		SpanEditHandler editHandler = new SpanEditHandler(func, AcceptedCharacters.Any);
		span.setEditHandler(editHandler);
	}

	private void afterTransition() throws Exception {
		// "@:" -> Explicit Single Line Block
		if (getCurrentSymbol().getType() == HtmlSymbolType.Text && getCurrentSymbol().getContent().length() > 0
				&& getCurrentSymbol().getContent().startsWith(":")) {
			// Split the token
			Tuple<HtmlSymbol, HtmlSymbol> split = getLanguage().splitSymbol(getCurrentSymbol(), 1,
					HtmlSymbolType.Colon);

			// The first part (left) is added to this span and we return a
			// MetaCode span
			accept(split.getItem1());
			getSpan().setCodeGenerator(SpanCodeGenerator.Null);
			output(SpanKind.MetaCode);
			if (split.getItem2() != null) {
				accept(split.getItem2());
			}
			nextToken();
			singleLineMarkup();
		} else if (getCurrentSymbol().getType() == HtmlSymbolType.OpenAngle) {
			try {
				tagBlock(new java.util.Stack<Tuple<HtmlSymbol, SourceLocation>>());
			} catch (ArgumentNullException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void singleLineMarkup() throws Exception {
		// Parse until a newline, it's that simple!
		// First, signal to code parser that whitespace is significant to us.
		boolean old = getContext().getWhiteSpaceIsSignificantToAncestorBlock();
		getContext().setWhiteSpaceIsSignificantToAncestorBlock(true);
		getSpan().setEditHandler(new SingleLineMarkupEditHandler(p -> getLanguage().tokenizeString(p)));
		skipToAndParseCode(HtmlSymbolType.NewLine);
		if (!getEndOfFile() && getCurrentSymbol().getType() == HtmlSymbolType.NewLine) {
			acceptAndMoveNext();
			getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
		}
		putCurrentBack();
		getContext().setWhiteSpaceIsSignificantToAncestorBlock(old);
		output(SpanKind.Markup);
	}

	private void tagBlock(java.util.Stack<Tuple<HtmlSymbol, SourceLocation>> tags) throws Exception {
		// Skip Whitespace and Text
		List<String> str = null;
		boolean complete = false;
		do {
			skipToAndParseCode(HtmlSymbolType.OpenAngle);
			if (getEndOfFile()) {
				endTagBlock(tags, true);
			} else {
				_bufferedOpenAngle = null;
				_lastTagStart = getCurrentLocation().clone();
				Assert(HtmlSymbolType.OpenAngle);
				_bufferedOpenAngle = getCurrentSymbol();
				SourceLocation tagStart = getCurrentLocation().clone();
				if (!nextToken()) {
					accept(_bufferedOpenAngle);
					endTagBlock(tags, false);
				} else {
					complete = afterTagStart(tagStart, tags);
				}
			}
		} while (tags.size() > 0);

		endTagBlock(tags, complete);
	}

	private boolean afterTagStart(SourceLocation tagStart, java.util.Stack<Tuple<HtmlSymbol, SourceLocation>> tags)
			throws Exception {
		if (!getEndOfFile()) {
			switch (getCurrentSymbol().getType()) {
			case Solidus:
				// End Tag
				return endTag(tagStart, tags);
			case Bang:
				// Comment
				accept(_bufferedOpenAngle);
				return bangTag();
			case QuestionMark:
				// XML PI
				accept(_bufferedOpenAngle);
				return xmlPI();
			default:
				// Start Tag
				return startTag(tags);
			}
		}
		if (tags.empty()) {
			getContext().onError(getCurrentLocation().clone(), RazorResources.getParseError_OuterTagMissingName());
		}
		return false;
	}

	private boolean xmlPI() throws Exception {
		// Accept "?"
		Assert(HtmlSymbolType.QuestionMark);
		acceptAndMoveNext();
		return acceptUntilAll(HtmlSymbolType.QuestionMark, HtmlSymbolType.CloseAngle);
	}

	private boolean bangTag() throws Exception {
		// Accept "!"
		Assert(HtmlSymbolType.Bang);

		if (acceptAndMoveNext()) {
			if (getCurrentSymbol().getType() == HtmlSymbolType.DoubleHyphen) {
				acceptAndMoveNext();
				return acceptUntilAll(HtmlSymbolType.DoubleHyphen, HtmlSymbolType.CloseAngle);
			} else if (getCurrentSymbol().getType() == HtmlSymbolType.LeftBracket) {
				if (acceptAndMoveNext()) {
					return cdata();
				}
			} else {
				acceptAndMoveNext();
				return acceptUntilAll(HtmlSymbolType.CloseAngle);
			}
		}

		return false;
	}

	private boolean cdata() throws Exception {
		if (getCurrentSymbol().getType() == HtmlSymbolType.Text && StringHelper
				.stringsEqual(getCurrentSymbol().getContent(), "cdata", StringComparison.OrdinalIgnoreCase)) {
			if (acceptAndMoveNext()) {
				if (getCurrentSymbol().getType() == HtmlSymbolType.LeftBracket) {
					return acceptUntilAll(HtmlSymbolType.RightBracket, HtmlSymbolType.RightBracket,
							HtmlSymbolType.CloseAngle);
				}
			}
		}

		return false;
	}

	private boolean endTag(SourceLocation tagStart, java.util.Stack<Tuple<HtmlSymbol, SourceLocation>> tags) {
		// Accept "/" and move next
		Assert(HtmlSymbolType.Solidus);
		HtmlSymbol solidus = getCurrentSymbol();
		if (!nextToken()) {
			accept(_bufferedOpenAngle);
			accept(solidus);
			return false;
		} else {
			String tagName = "";
			if (at(HtmlSymbolType.Text)) {
				tagName = getCurrentSymbol().getContent();
			}
			boolean matched = removeTag(tags, tagName, tagStart);

			if (tags.empty() && StringHelper.stringsEqual(tagName, SyntaxConstants.TextTagName,
					StringComparison.OrdinalIgnoreCase) && matched) {
				output(SpanKind.Markup);
				return endTextTag(solidus);
			}
			accept(_bufferedOpenAngle);
			accept(solidus);

			acceptUntil(HtmlSymbolType.CloseAngle);

			// Accept the ">"
			return optional(HtmlSymbolType.CloseAngle);
		}
	}

	private boolean endTextTag(HtmlSymbol solidus) {
		SourceLocation start = _bufferedOpenAngle.getStart().clone();

		accept(_bufferedOpenAngle);
		accept(solidus);

		Assert(HtmlSymbolType.Text);
		acceptAndMoveNext();

		boolean seenCloseAngle = optional(HtmlSymbolType.CloseAngle);

		if (!seenCloseAngle) {
			getContext().onError(start, RazorResources.getParseError_TextTagCannotContainAttributes());
		} else {
			getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
		}

		getSpan().setCodeGenerator(SpanCodeGenerator.Null);
		output(SpanKind.Transition);
		return seenCloseAngle;
	}

	private boolean isTagRecoveryStopPoint(HtmlSymbol sym) {
		return sym.getType() == HtmlSymbolType.CloseAngle || sym.getType() == HtmlSymbolType.Solidus
				|| sym.getType() == HtmlSymbolType.OpenAngle || sym.getType() == HtmlSymbolType.SingleQuote
				|| sym.getType() == HtmlSymbolType.DoubleQuote;
	}

	private void tagContent() throws Exception {
		if (!at(HtmlSymbolType.WhiteSpace)) {
			// We should be right after the tag name, so if there's no
			// whitespace, something is wrong
			recoverToEndOfTag();
		} else {
			// We are here ($): <tag$ foo="bar" biz="~/Baz" />
			while (!getEndOfFile() && !isEndOfTag()) {
				beforeAttribute();
			}
		}
	}

	private boolean isEndOfTag() {
		if (at(HtmlSymbolType.Solidus)) {
			if (nextIs(HtmlSymbolType.CloseAngle)) {
				return true;
			} else {
				acceptAndMoveNext();
			}
		}
		return at(HtmlSymbolType.CloseAngle) || at(HtmlSymbolType.OpenAngle);
	}

	private void beforeAttribute() throws Exception {
		// http://dev.w3.org/html5/spec/tokenization.html#before-attribute-name-state
		// Capture whitespace

		List<HtmlSymbol> whitespace = readWhile(
				sym -> sym.getType() == HtmlSymbolType.WhiteSpace || sym.getType() == HtmlSymbolType.NewLine);

		if (at(HtmlSymbolType.Transition)) {
			// Transition outside of attribute value -> Switch to recovery mode
			accept(whitespace);
			recoverToEndOfTag();
			return;
		}

		// http://dev.w3.org/html5/spec/tokenization.html#attribute-name-state
		// Read the 'name' (i.e. read until the '=' or whitespace/newline)

		List<HtmlSymbol> name = Collections.emptyList();
		if (at(HtmlSymbolType.Text)) {

			name = readWhile(sym -> sym.getType() != HtmlSymbolType.WhiteSpace
					&& sym.getType() != HtmlSymbolType.NewLine && sym.getType() != HtmlSymbolType.Equals
					&& sym.getType() != HtmlSymbolType.CloseAngle && sym.getType() != HtmlSymbolType.OpenAngle
					&& (sym.getType() != HtmlSymbolType.Solidus || !nextIs(HtmlSymbolType.CloseAngle)));
		} else {
			// Unexpected character in tag, enter recovery
			accept(whitespace);
			recoverToEndOfTag();
			return;
		}

		if (!at(HtmlSymbolType.Equals)) {
			// Saw a space or newline after the name, so just skip this
			// attribute and continue around the loop
			accept(whitespace);
			accept(name);
			return;
		}

		output(SpanKind.Markup);

		// Start a new markup block for the attribute

		// using (Context.StartBlock(BlockType.Markup))
		IDisposable disposable = getContext().StartBlock(BlockType.Markup);
		try {
			attributePrefix(whitespace, name);
		} finally {
			disposable.dispose();
		}
	}

	private void attributePrefix(List<HtmlSymbol> whitespace, List<HtmlSymbol> nameSymbols) throws Exception {
		// First, determine if this is a 'data-' attribute (since those can't
		// use conditional attributes)

		LocationTagged<String> name = ISymbol.getContent(nameSymbols, getSpan().getStart().clone());
		boolean attributeCanBeConditional = !StringHelper.startWith(name.getValue(), "data-",
				StringComparison.OrdinalIgnoreCase);

		// Accept the whitespace and name
		accept(whitespace);
		accept(nameSymbols);
		Assert(HtmlSymbolType.Equals); // We should be at "="
		acceptAndMoveNext();
		HtmlSymbolType quote = HtmlSymbolType.Unknown;
		if (at(HtmlSymbolType.SingleQuote) || at(HtmlSymbolType.DoubleQuote)) {
			quote = getCurrentSymbol().getType();
			acceptAndMoveNext();
		}

		// We now have the prefix: (i.e. ' foo="')
		LocationTagged<String> prefix = getSpan().getContent();

		if (attributeCanBeConditional) {
			getSpan().setCodeGenerator(SpanCodeGenerator.Null); // The block
																// code
																// generator
																// will render
																// the prefix
			output(SpanKind.Markup);

			// Read the values
			while (!getEndOfFile() && !isEndOfAttributeValue(quote, getCurrentSymbol())) {
				attributeValue(quote);
			}

			// Capture the suffix
			LocationTagged<String> suffix = new LocationTagged<String>("", getCurrentLocation().clone());
			if (quote != HtmlSymbolType.Unknown && at(quote)) {
				suffix = getCurrentSymbol().GetContent();
				acceptAndMoveNext();
			}

			if (getSpan().getSymbols().size() > 0) {
				getSpan().setCodeGenerator(SpanCodeGenerator.Null); // Again, //
																	// block
																	// code
																	// generator
																	// will
																	// render
																	// the
																	// suffix
				output(SpanKind.Markup);
			}

			// Create the block code generator
			getContext().getCurrentBlock().setCodeGenerator(new AttributeBlockCodeGenerator(name, prefix, suffix));
		} else {
			// Not a "conditional" attribute, so just read the value

			final HtmlSymbolType quoteTemp = quote;
			skipToAndParseCode(sym -> isEndOfAttributeValue(quoteTemp, sym));
			if (quote != HtmlSymbolType.Unknown) {
				optional(quote);
			}
			output(SpanKind.Markup);
		}
	}

	private void attributeValue(HtmlSymbolType quote) throws Exception {
		SourceLocation prefixStart = getCurrentLocation().clone();

		List<HtmlSymbol> prefix = readWhile(
				sym -> sym.getType() == HtmlSymbolType.WhiteSpace || sym.getType() == HtmlSymbolType.NewLine);
		accept(prefix);

		if (at(HtmlSymbolType.Transition)) {
			SourceLocation valueStart = getCurrentLocation().clone();
			putCurrentBack();

			// Output the prefix but as a null-span.
			// DynamicAttributeBlockCodeGenerator will render it
			getSpan().setCodeGenerator(SpanCodeGenerator.Null);

			// Dynamic value, start a new block and set the code generator
			// using (Context.StartBlock(BlockType.Markup))
			IDisposable disposable = getContext().StartBlock(BlockType.Markup);
			try {
				getContext().getCurrentBlock().setCodeGenerator(
						new DynamicAttributeBlockCodeGenerator(ISymbol.getContent(prefix, prefixStart), valueStart));

				otherParserBlock();
			} finally {
				disposable.dispose();
			}
		} else if (at(HtmlSymbolType.Text) && getCurrentSymbol().getContent().length() > 0
				&& getCurrentSymbol().getContent().charAt(0) == '~' && nextIs(HtmlSymbolType.Solidus)) {
			// Virtual Path value
			SourceLocation valueStart = getCurrentLocation().clone();
			virtualPath();
			getSpan().setCodeGenerator(new LiteralAttributeCodeGenerator(ISymbol.getContent(prefix, prefixStart),
					new LocationTagged<SpanCodeGenerator>(new ResolveUrlCodeGenerator(), valueStart)));
		} else {
			// Literal value
			// 'quote' should be "Unknown" if not quoted and symbols coming from
			// the tokenizer should never have "Unknown" type.
			// These three conditions find separators which break the attribute
			// value into portions
			// This condition checks for the end of the attribute value (it
			// repeats some of the checks above but for now that's ok)

			List<HtmlSymbol> value = readWhile(
					sym -> sym.getType() != HtmlSymbolType.WhiteSpace && sym.getType() != HtmlSymbolType.NewLine
							&& sym.getType() != HtmlSymbolType.Transition && !isEndOfAttributeValue(quote, sym));
			accept(value);
			getSpan().setCodeGenerator(LiteralAttributeCodeGenerator.create(ISymbol.getContent(prefix, prefixStart),
					ISymbol.getContent(value, prefixStart)));
		}
		output(SpanKind.Markup);
	}

	private boolean isEndOfAttributeValue(HtmlSymbolType quote, HtmlSymbol sym) {
		return getEndOfFile() || sym == null
				|| (quote != HtmlSymbolType.Unknown ? sym.getType() == quote : isUnquotedEndOfAttributeValue(sym)); // If
																													// quoted,
																													// just
																													// wait
																													// for
																													// the
																													// quote
	}

	private boolean isUnquotedEndOfAttributeValue(HtmlSymbol sym) {
		// If unquoted, we have a larger set of terminating characters:
		// http://dev.w3.org/html5/spec/tokenization.html#attribute-value-unquoted-state
		// Also we need to detect "/" and ">"
		return sym.getType() == HtmlSymbolType.DoubleQuote || sym.getType() == HtmlSymbolType.SingleQuote
				|| sym.getType() == HtmlSymbolType.OpenAngle || sym.getType() == HtmlSymbolType.Equals
				|| (sym.getType() == HtmlSymbolType.Solidus && nextIs(HtmlSymbolType.CloseAngle))
				|| sym.getType() == HtmlSymbolType.CloseAngle || sym.getType() == HtmlSymbolType.WhiteSpace
				|| sym.getType() == HtmlSymbolType.NewLine;
	}

	private void virtualPath() {
		Assert(HtmlSymbolType.Text);
		assert getCurrentSymbol().getContent().length() > 0 && getCurrentSymbol().getContent().charAt(0) == '~';

		// Parse until a transition symbol, whitespace, newline or quote. We
		// support only a fairly minimal subset of Virtual Paths
		acceptUntil(HtmlSymbolType.Transition, HtmlSymbolType.WhiteSpace, HtmlSymbolType.NewLine,
				HtmlSymbolType.SingleQuote, HtmlSymbolType.DoubleQuote);

		// Output a Virtual Path span
		getSpan().getEditHandler().setEditorHints(editorHints.VirtualPath);
	}

	private void recoverToEndOfTag() throws Exception {
		// Accept until ">", "/" or "<", but parse code
		while (!getEndOfFile()) {
			skipToAndParseCode((p) -> isTagRecoveryStopPoint(p));
			if (!getEndOfFile()) {
				ensureCurrent();
				switch (getCurrentSymbol().getType()) {
				case SingleQuote:
				case DoubleQuote:
					parseQuoted();
					break;
				case OpenAngle:
					// Another "<" means this tag is invalid.
				case Solidus:
					// Empty tag
				case CloseAngle:
					// End of tag
					return;
				default:
					acceptAndMoveNext();
					break;
				}
			}
		}
	}

	private void parseQuoted() throws Exception {
		HtmlSymbolType type = getCurrentSymbol().getType();
		acceptAndMoveNext();
		parseQuoted(type);
	}

	private void parseQuoted(HtmlSymbolType type) throws Exception {
		skipToAndParseCode(type);
		if (!getEndOfFile()) {
			Assert(type);
			acceptAndMoveNext();
		}
	}

	private boolean startTag(java.util.Stack<Tuple<HtmlSymbol, SourceLocation>> tags) throws Exception {
		// If we're at text, it's the name, otherwise the name is ""
		HtmlSymbol tagName;
		if (at(HtmlSymbolType.Text)) {
			tagName = getCurrentSymbol();
		} else {
			tagName = new HtmlSymbol(getCurrentLocation().clone(), "", HtmlSymbolType.Unknown);
		}

		Tuple<HtmlSymbol, SourceLocation> tag = Tuple.Create(tagName, _lastTagStart.clone());

		if (tags.empty() && StringHelper.stringsEqual(tag.getItem1().getContent(), SyntaxConstants.TextTagName,
				StringComparison.OrdinalIgnoreCase)) {
			output(SpanKind.Markup);
			getSpan().setCodeGenerator(SpanCodeGenerator.Null);

			accept(_bufferedOpenAngle);
			Assert(HtmlSymbolType.Text);

			acceptAndMoveNext();

			int bookmark = getCurrentLocation().getAbsoluteIndex();
			Iterable<HtmlSymbol> tokens = readWhile(isSpacingToken(true));
			boolean empty = at(HtmlSymbolType.Solidus);
			if (empty) {
				accept(tokens);
				Assert(HtmlSymbolType.Solidus);
				acceptAndMoveNext();
				bookmark = getCurrentLocation().getAbsoluteIndex();
				tokens = readWhile(isSpacingToken(true));
			}

			if (!optional(HtmlSymbolType.CloseAngle)) {
				getContext().getSource().setPosition(bookmark);
				nextToken();
				getContext().onError(tag.getItem2(), RazorResources.getParseError_TextTagCannotContainAttributes());
			} else {
				accept(tokens);
				getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
			}

			if (!empty) {
				tags.push(tag);
			}
			output(SpanKind.Transition);
			return true;
		}
		accept(_bufferedOpenAngle);
		optional(HtmlSymbolType.Text);
		return restOfTag(tag, tags);
	}

	private boolean restOfTag(Tuple<HtmlSymbol, SourceLocation> tag,
			java.util.Stack<Tuple<HtmlSymbol, SourceLocation>> tags) throws Exception {
		tagContent();

		// We are now at a possible end of the tag
		// Found '<', so we just abort this tag.
		if (at(HtmlSymbolType.OpenAngle)) {
			return false;
		}

		boolean isEmpty = at(HtmlSymbolType.Solidus);
		// Found a solidus, so don't accept it but DON'T push the tag to the
		// stack
		if (isEmpty) {
			acceptAndMoveNext();
		}

		// Check for the '>' to determine if the tag is finished
		boolean seenClose = optional(HtmlSymbolType.CloseAngle);
		if (!seenClose) {
			getContext().onError(tag.getItem2(), RazorResources.getParseError_UnfinishedTag(),
					tag.getItem1().getContent());
		} else {
			if (!isEmpty) {
				// Is this a void element?
				String tagName = tag.getItem1().getContent().trim();
				if (getVoidElements().contains(tagName)) {
					// Technically, void elements like "meta" are not allowed to
					// have end tags. Just in case they do,
					// we need to look ahead at the next set of tokens. If we
					// see "<", "/", tag name, accept it and the ">" following
					// it
					// Place a bookmark
					int bookmark = getCurrentLocation().getAbsoluteIndex();

					// Skip whitespace
					Iterable<HtmlSymbol> ws = readWhile(isSpacingToken(true));

					// Open Angle
					if (at(HtmlSymbolType.OpenAngle) && nextIs(HtmlSymbolType.Solidus)) {
						HtmlSymbol openAngle = getCurrentSymbol();
						nextToken();
						Assert(HtmlSymbolType.Solidus);
						HtmlSymbol solidus = getCurrentSymbol();
						nextToken();
						if (at(HtmlSymbolType.Text) && StringHelper.stringsEqual(getCurrentSymbol().getContent(),
								tagName, StringComparison.OrdinalIgnoreCase)) {
							// Accept up to here
							accept(ws);
							accept(openAngle);
							accept(solidus);
							acceptAndMoveNext();

							// Accept to '>', '<' or EOF
							acceptUntil(HtmlSymbolType.CloseAngle, HtmlSymbolType.OpenAngle);
							// Accept the '>' if we saw it. And if we do see it,
							// we're complete
							return optional(HtmlSymbolType.CloseAngle);
						} // At(HtmlSymbolType.Text) &&
							// String.Equals(CurrentSymbol.Content, tagName,
							// StringComparison.OrdinalIgnoreCase)
					} // At(HtmlSymbolType.OpenAngle) &&
						// NextIs(HtmlSymbolType.Solidus)

					// Go back to the bookmark and just finish this tag at the
					// close angle
					getContext().getSource().setPosition(bookmark);
					nextToken();
				} else if (StringHelper.stringsEqual(tagName, "script", StringComparison.OrdinalIgnoreCase)) {
					skipToEndScriptAndParseCode();
				} else {
					// Push the tag on to the stack
					tags.push(tag);
				}
			}
		}
		return seenClose;
	}

	private void skipToEndScriptAndParseCode() throws Exception {
		// Special case for <script>: Skip to end of script tag and parse code
		boolean seenEndScript = false;
		while (!seenEndScript && !getEndOfFile()) {
			skipToAndParseCode(HtmlSymbolType.OpenAngle);
			SourceLocation tagStart = getCurrentLocation().clone();
			acceptAndMoveNext();
			acceptWhile(HtmlSymbolType.WhiteSpace);
			if (optional(HtmlSymbolType.Solidus)) {
				acceptWhile(HtmlSymbolType.WhiteSpace);
				if (at(HtmlSymbolType.Text) && StringHelper.stringsEqual(getCurrentSymbol().getContent(), "script",
						StringComparison.OrdinalIgnoreCase)) {
					// </script!
					skipToAndParseCode(HtmlSymbolType.CloseAngle);
					if (!optional(HtmlSymbolType.CloseAngle)) {
						getContext().onError(tagStart, RazorResources.getParseError_UnfinishedTag(), "script");
					}
					seenEndScript = true;
				}
			}
		}
	}

	private boolean acceptUntilAll(HtmlSymbolType... endSequence) throws Exception {
		while (!getEndOfFile()) {
			skipToAndParseCode(endSequence[0]);
			if (acceptAll(endSequence)) {
				return true;
			}
		}
		assert getEndOfFile();
		getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
		return false;
	}

	private boolean removeTag(java.util.Stack<Tuple<HtmlSymbol, SourceLocation>> tags, String tagName,
			SourceLocation tagStart) {
		Tuple<HtmlSymbol, SourceLocation> currentTag = null;
		while (tags.size() > 0) {
			currentTag = tags.pop();
			if (StringHelper.stringsEqual(tagName, currentTag.getItem1().getContent(),
					StringComparison.OrdinalIgnoreCase)) {
				// Matched the tag
				return true;
			}
		}
		if (currentTag != null) {
			getContext().onError(currentTag.getItem2(), RazorResources.getParseError_MissingEndTag(),
					currentTag.getItem1().getContent());
		} else {
			getContext().onError(tagStart, RazorResources.getParseError_UnexpectedEndTag(), tagName);
		}
		return false;
	}

	private void endTagBlock(java.util.Stack<Tuple<HtmlSymbol, SourceLocation>> tags, boolean complete) {
		if (tags.size() > 0) {
			// Ended because of EOF, not matching close tag. Throw error for
			// last tag
			while (tags.size() > 1) {
				tags.pop();
			}
			Tuple<HtmlSymbol, SourceLocation> tag = tags.pop();
			getContext().onError(tag.getItem2(), RazorResources.getParseError_MissingEndTag(),
					tag.getItem1().getContent());
		} else if (complete) {
			getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
		}
		tags.clear();
		if (!getContext().getDesignTimeMode()) {
			acceptWhile(HtmlSymbolType.WhiteSpace);
			if (!getEndOfFile() && getCurrentSymbol().getType() == HtmlSymbolType.NewLine) {
				acceptAndMoveNext();
			}
		} else if (getSpan().getEditHandler().getAcceptedCharacters() == AcceptedCharacters.Any) {
			acceptWhile(HtmlSymbolType.WhiteSpace);
			optional(HtmlSymbolType.NewLine);
		}
		putCurrentBack();

		if (!complete) {
			addMarkerSymbolIfNecessary();
		}
		output(SpanKind.Markup);
	}

	@Override
	public void parseDocument() throws Exception {
		if (getContext() == null) {
			throw new InvalidOperationException(RazorResources.getParser_Context_Not_Set());
		}

		// using (PushSpanConfig(DefaultMarkupSpan))
		IDisposable spanConfig = pushSpanConfig((p) -> defaultMarkupSpan(p));
		try {

			// using (Context.StartBlock(BlockType.Markup))
			IDisposable dispose = getContext().StartBlock(BlockType.Markup);
			try {
				nextToken();
				while (!getEndOfFile()) {
					skipToAndParseCode(HtmlSymbolType.OpenAngle);
					scanTagInDocumentContext();
				}
				addMarkerSymbolIfNecessary();
				output(SpanKind.Markup);
			} finally {
				dispose.dispose();
			}
		} finally {
			spanConfig.dispose();
		}
	}

	/**
	 * Reads the content of a tag (if present) in the MarkupDocument (or
	 * MarkupSection) context, where we don't care about maintaining a stack of
	 * tags.
	 * 
	 * @return A boolean indicating if we scanned at least one tag.
	 * @throws Exception 
	 */
	private boolean scanTagInDocumentContext() throws Exception {
		if (optional(HtmlSymbolType.OpenAngle)) {
			if (at(HtmlSymbolType.Bang)) {
				bangTag();
				return true;
			} else if (at(HtmlSymbolType.QuestionMark)) {
				xmlPI();
				return true;
			} else if (!at(HtmlSymbolType.Solidus)) {
				boolean scriptTag = at(HtmlSymbolType.Text) && StringHelper
						.stringsEqual(getCurrentSymbol().getContent(), "script", StringComparison.OrdinalIgnoreCase);
				optional(HtmlSymbolType.Text);
				tagContent(); // Parse the tag, don't care about the content
				optional(HtmlSymbolType.Solidus);
				optional(HtmlSymbolType.CloseAngle);
				if (scriptTag) {
					skipToEndScriptAndParseCode();
				}
				return true;
			}
		}
		return false;
	}

	// From http://dev.w3.org/html5/spec/Overview.html#elements-0
	private Set<String> voidElements = new java.util.HashSet<String>(Arrays.asList("area", "base", "br", "col",
			"command", "embed", "hr", "img", "input", "keygen", "link", "meta", "param", "source", "track", "wbr"));

	public final Set<String> getVoidElements() {
		return voidElements;
	}

	@Override
	protected ParserBase getOtherParser() {
		return getContext().getCodeParser();
	}

	@Override
	protected LanguageCharacteristics<HtmlTokenizer, HtmlSymbol, HtmlSymbolType> getLanguage() {
		return HtmlLanguageCharacteristics.getInstance();
	}

	@Override
	public void buildSpan(SpanBuilder span, SourceLocation start, String content) {
		span.setKind(SpanKind.Markup);
		span.setCodeGenerator(new MarkupCodeGenerator());
		super.buildSpan(span, start, content);
	}

	@Override
	protected void outputSpanBeforeRazorComment() {
		output(SpanKind.Markup);
	}

	protected final void skipToAndParseCode(HtmlSymbolType type) throws Exception {

		skipToAndParseCode(sym -> sym.getType() == type);
	}

	protected final void skipToAndParseCode(Predicate<HtmlSymbol> condition) throws Exception {
		HtmlSymbol last = null;
		boolean startOfLine = false;
		while (!getEndOfFile() && !condition.test(getCurrentSymbol())) {
			if (at(HtmlSymbolType.NewLine)) {
				if (last != null) {
					accept(last);
				}

				// Mark the start of a new line
				startOfLine = true;
				last = null;
				acceptAndMoveNext();
			} else if (at(HtmlSymbolType.Transition)) {
				HtmlSymbol transition = getCurrentSymbol();
				nextToken();
				if (at(HtmlSymbolType.Transition)) {
					if (last != null) {
						accept(last);
						last = null;
					}
					output(SpanKind.Markup);
					accept(transition);
					getSpan().setCodeGenerator(SpanCodeGenerator.Null);
					output(SpanKind.Markup);
					acceptAndMoveNext();
					continue; // while
				} else {
					if (!getEndOfFile()) {
						putCurrentBack();
					}
					putBack(transition);
				}

				// Handle whitespace rewriting
				if (last != null) {
					if (!getContext().getDesignTimeMode() && last.getType() == HtmlSymbolType.WhiteSpace
							&& startOfLine) {
						// Put the whitespace back too
						startOfLine = false;
						putBack(last);
						last = null;
					} else {
						// Accept last
						accept(last);
						last = null;
					}
				}

				otherParserBlock();
			} else if (at(HtmlSymbolType.RazorCommentTransition)) {
				if (last != null) {
					accept(last);
					last = null;
				}
				addMarkerSymbolIfNecessary();
				output(SpanKind.Markup);
				razorComment();
			} else {
				// As long as we see whitespace, we're still at the "start" of
				// the line
				startOfLine &= at(HtmlSymbolType.WhiteSpace);

				// If there's a last token, accept it
				if (last != null) {
					accept(last);
					last = null;
				}

				// Advance
				last = getCurrentSymbol();
				nextToken();
			}
		}

		if (last != null) {
			accept(last);
		}
	}

	protected static Predicate<HtmlSymbol> isSpacingToken(boolean includeNewLines) {

		return sym -> sym.getType() == HtmlSymbolType.WhiteSpace
				|| (includeNewLines && sym.getType() == HtmlSymbolType.NewLine);
	}

	private void otherParserBlock() throws Exception {
		addMarkerSymbolIfNecessary();
		output(SpanKind.Markup);
		// using (PushSpanConfig())
		IDisposable disposable = pushSpanConfig();
		try {
			getContext().switchActiveParser();
			getContext().getCodeParser().parseBlock();
			getContext().switchActiveParser();
		} finally {
			disposable.dispose();
		}
		initialize(getSpan());
		nextToken();
	}

	private boolean privateCaseSensitive;

	private boolean getCaseSensitive() {
		return privateCaseSensitive;
	}

	private void setCaseSensitive(boolean value) {
		privateCaseSensitive = value;
	}

	private StringComparison getComparison() {
		return getCaseSensitive() ? StringComparison.Ordinal : StringComparison.OrdinalIgnoreCase;
	}

	@Override
	public void parseSection(Tuple<String, String> nestingSequences, boolean caseSensitive) throws Exception {
		if (getContext() == null) {
			// throw new
			// InvalidOperationException(RazorResources.getParser_Context_Not_Set());
		}

		// using (PushSpanConfig(DefaultMarkupSpan))
		IDisposable disposable = pushSpanConfig((p) -> defaultMarkupSpan(p));
		try {
			// using (Context.StartBlock(BlockType.Markup))
			IDisposable disposable2 = getContext().StartBlock(BlockType.Markup);
			try {
				nextToken();
				setCaseSensitive(caseSensitive);
				if (nestingSequences.getItem1() == null) {
					nonNestingSection(nestingSequences.getItem2().split(""));
				} else {
					nestingSection(nestingSequences);
				}
				addMarkerSymbolIfNecessary();
				output(SpanKind.Markup);
			} finally {
				disposable2.dispose();
			}
		} finally {
			disposable.dispose();
		}
	}

	private void nonNestingSection(String[] nestingSequenceComponents) throws Exception {
		do {

			skipToAndParseCode(sym -> sym.getType() == HtmlSymbolType.OpenAngle || atEnd(nestingSequenceComponents));
			scanTagInDocumentContext();
			if (!getEndOfFile() && atEnd(nestingSequenceComponents)) {
				break;
			}
		} while (!getEndOfFile());

		putCurrentBack();
	}

	private void nestingSection(Tuple<String, String> nestingSequences) throws Exception {
		int nesting = 1;
		while (nesting > 0 && !getEndOfFile()) {

			skipToAndParseCode(
					sym -> sym.getType() == HtmlSymbolType.Text || sym.getType() == HtmlSymbolType.OpenAngle);
			if (at(HtmlSymbolType.Text)) {
				nesting += processTextToken(nestingSequences, nesting);
				if (getCurrentSymbol() != null) {
					acceptAndMoveNext();
				} else if (nesting > 0) {
					nextToken();
				}
			} else {
				scanTagInDocumentContext();
			}
		}
	}

	private boolean atEnd(String[] nestingSequenceComponents) {
		ensureCurrent();
		if (StringHelper.stringsEqual(getCurrentSymbol().getContent(), nestingSequenceComponents[0], getComparison())) {
			int bookmark = getCurrentSymbol().getStart().getAbsoluteIndex();
			try {
				for (String component : nestingSequenceComponents) {
					if (!getEndOfFile() && !StringHelper.stringsEqual(getCurrentSymbol().getContent(), component,
							getComparison())) {
						return false;
					}
					nextToken();
					while (!getEndOfFile() && isSpacingToken(true).test(getCurrentSymbol())) {
						nextToken();
					}
				}
				return true;
			} finally {
				getContext().getSource().setPosition(bookmark);
				nextToken();
			}
		}
		return false;
	}

	private int processTextToken(Tuple<String, String> nestingSequences, int currentNesting) {
		for (int i = 0; i < getCurrentSymbol().getContent().length(); i++) {
			int nestingDelta = handleNestingSequence(nestingSequences.getItem1(), i, currentNesting, 1);
			if (nestingDelta == 0) {
				nestingDelta = handleNestingSequence(nestingSequences.getItem2(), i, currentNesting, -1);
			}

			if (nestingDelta != 0) {
				return nestingDelta;
			}
		}
		return 0;
	}

	private int handleNestingSequence(String sequence, int position, int currentNesting, int retIfMatched) {
		if (sequence != null && getCurrentSymbol().getContent().charAt(position) == sequence.charAt(0)
				&& position + sequence.length() <= getCurrentSymbol().getContent().length()) {
			String possibleStart = getCurrentSymbol().getContent().substring(position, position + sequence.length());
			if (StringHelper.stringsEqual(possibleStart, sequence, getComparison())) {
				// Capture the current symbol and "put it back" (really we just
				// want to clear CurrentSymbol)
				int bookmark = getContext().getSource().getPosition();
				HtmlSymbol sym = getCurrentSymbol();
				putCurrentBack();

				// Carve up the symbol
				Tuple<HtmlSymbol, HtmlSymbol> pair = getLanguage().splitSymbol(sym, position, HtmlSymbolType.Text);
				HtmlSymbol preSequence = pair.getItem1();
				assert pair.getItem2() != null;
				pair = getLanguage().splitSymbol(pair.getItem2(), sequence.length(), HtmlSymbolType.Text);
				HtmlSymbol sequenceToken = pair.getItem1();
				HtmlSymbol postSequence = pair.getItem2();

				// Accept the first chunk (up to the nesting sequence we just
				// saw)
				if (!StringHelper.isNullOrEmpty(preSequence.getContent())) {
					accept(preSequence);
				}

				if (currentNesting + retIfMatched == 0) {
					// This is 'popping' the final entry on the stack of nesting
					// sequences
					// A caller higher in the parsing stack will accept the
					// sequence token, so advance
					// to it
					getContext().getSource().setPosition(sequenceToken.getStart().getAbsoluteIndex());
				} else {
					// This isn't the end of the last nesting sequence, accept
					// the token and keep going
					accept(sequenceToken);

					// Position at the start of the postSequence symbol
					if (postSequence != null) {
						getContext().getSource().setPosition(postSequence.getStart().getAbsoluteIndex());
					} else {
						getContext().getSource().setPosition(bookmark);
					}
				}

				// Return the value we were asked to return if matched, since we
				// found a nesting sequence
				return retIfMatched;
			}
		}
		return 0;
	}

	@Override
	public boolean equals(Object obj, Object others) {
		// TODO Auto-generated method stub
		return obj.equals(others);
	}
}