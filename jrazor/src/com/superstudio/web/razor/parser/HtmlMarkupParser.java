package com.superstudio.web.razor.parser;


import com.superstudio.commons.Tuple;
import com.superstudio.commons.csharpbridge.StringComparison;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.web.RazorResources;
import com.superstudio.web.razor.editor.EditorHints;
import com.superstudio.web.razor.editor.SingleLineMarkupEditHandler;
import com.superstudio.web.razor.editor.SpanEditHandler;
import com.superstudio.web.razor.generator.*;
import com.superstudio.web.razor.parser.syntaxTree.AcceptedCharacters;
import com.superstudio.web.razor.parser.syntaxTree.BlockType;
import com.superstudio.web.razor.parser.syntaxTree.SpanBuilder;
import com.superstudio.web.razor.parser.syntaxTree.SpanKind;
import com.superstudio.web.razor.text.LocationTagged;
import com.superstudio.web.razor.text.SourceLocation;
import com.superstudio.web.razor.tokenizer.HtmlTokenizer;
import com.superstudio.web.razor.tokenizer.symbols.HtmlSymbol;
import com.superstudio.web.razor.tokenizer.symbols.HtmlSymbolType;
import com.superstudio.web.razor.tokenizer.symbols.ISymbol;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;


public class HtmlMarkupParser extends TokenizerBackedParser<HtmlTokenizer, HtmlSymbol, HtmlSymbolType> {
	private SourceLocation _lastTagStart = SourceLocation.Zero;
	private HtmlSymbol _bufferedOpenAngle;

	@Override
	public void parseBlock() throws InvalidOperationException {
		if (getContext() == null) {
			 throw new
			InvalidOperationException(RazorResources.getResource(RazorResources.Parser_Context_Not_Set));
		}
		try( AutoCloseable	disposable= PushSpanConfig((t) -> defaultMarkupSpan(t))) {

			try(AutoCloseable	disposable2=getContext().startBlock(BlockType.Markup)) {
				if (!nextToken()) {
					return;
				}

				AcceptWhile(IsSpacingToken(true));

				if (getCurrentSymbol().getType() == HtmlSymbolType.OpenAngle) {
					// "<" -> Implicit Tag Block
					try {
						tagBlock(new java.util.Stack<Tuple<HtmlSymbol, SourceLocation>>());
					} catch (ArgumentNullException e) {

						e.printStackTrace();
					}
				} else if (getCurrentSymbol().getType() == HtmlSymbolType.Transition) {
					// "@" -> Explicit Tag/single Line Block OR Template
					Output(SpanKind.Markup);

					// Definitely have a transition span
					Assert(HtmlSymbolType.Transition);
					AcceptAndMoveNext();
					getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
					getSpan().setCodeGenerator(SpanCodeGenerator.Null);
					Output(SpanKind.Transition);
					if (At(HtmlSymbolType.Transition)) {
						getSpan().setCodeGenerator(SpanCodeGenerator.Null);
						AcceptAndMoveNext();
						Output(SpanKind.MetaCode);
					}
					afterTransition();
				} else {
					getContext().OnError(getCurrentSymbol().getStart(),
							RazorResources.getResource(RazorResources.ParseError_MarkupBlock_Must_Start_With_Tag));
				}
				Output(SpanKind.Markup);
			}catch (Exception ex){
				ex.printStackTrace();
			}
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}

	private void defaultMarkupSpan(SpanBuilder span) {
		span.setCodeGenerator(new MarkupCodeGenerator());
		Function<String,Iterable<? extends ISymbol>> func=p ->  getLanguage().tokenizeString(p);
		SpanEditHandler editHandler = new SpanEditHandler(
				func,
				AcceptedCharacters.Any);
		span.setEditHandler(editHandler);
	}

	private void afterTransition() {
		// "@:" -> Explicit single Line Block
		if (getCurrentSymbol().getType() == HtmlSymbolType.Text && getCurrentSymbol().getContent().length() > 0
				&& getCurrentSymbol().getContent().startsWith(":")) {
			// Split the token
			Tuple<HtmlSymbol, HtmlSymbol> split = getLanguage().splitSymbol(getCurrentSymbol(), 1,
					HtmlSymbolType.Colon);

			// The first part (left) is added to this span and we return a
			// MetaCode span
			Accept(split.getItem1());
			getSpan().setCodeGenerator(SpanCodeGenerator.Null);
			Output(SpanKind.MetaCode);
			if (split.getItem2() != null) {
				Accept(split.getItem2());
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

	private void singleLineMarkup() {
		// parse until a newline, it's that simple!
		// First, signal to code parser that whitespace is significant to us.
		boolean old = getContext().getWhiteSpaceIsSignificantToAncestorBlock();
		getContext().setWhiteSpaceIsSignificantToAncestorBlock(true);
		getSpan().setEditHandler(new SingleLineMarkupEditHandler(p -> getLanguage().tokenizeString(p)));
		SkipToAndParseCode(HtmlSymbolType.NewLine);
		if (!getEndOfFile() && getCurrentSymbol().getType() == HtmlSymbolType.NewLine) {
			AcceptAndMoveNext();
			getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
		}
		putCurrentBack();
		getContext().setWhiteSpaceIsSignificantToAncestorBlock(old);
		Output(SpanKind.Markup);
	}

	private void tagBlock(java.util.Stack<Tuple<HtmlSymbol, SourceLocation>> tags) throws ArgumentNullException {
		// Skip Whitespace and Text
		List<String> str = null;
		boolean complete = false;
		do {
			SkipToAndParseCode(HtmlSymbolType.OpenAngle);
			if (getEndOfFile()) {
				EndTagBlock(tags, true);
			} else {
				_bufferedOpenAngle = null;
				_lastTagStart = getCurrentLocation().clone();
				Assert(HtmlSymbolType.OpenAngle);
				_bufferedOpenAngle = getCurrentSymbol();
				SourceLocation tagStart = getCurrentLocation().clone();
				if (!nextToken()) {
					Accept(_bufferedOpenAngle);
					EndTagBlock(tags, false);
				} else {
					complete = afterTagStart(tagStart, tags);
				}
			}
		} while (tags.size() > 0);

		EndTagBlock(tags, complete);
	}

	private boolean afterTagStart(SourceLocation tagStart, java.util.Stack<Tuple<HtmlSymbol, SourceLocation>> tags) throws ArgumentNullException {
		if (!getEndOfFile()) {
			switch (getCurrentSymbol().getType()) {
			case Solidus:
				// End Tag /
				return endTag(tagStart, tags);
			case Bang:
				// Comment!
				Accept(_bufferedOpenAngle);
				return bangTag();
			case QuestionMark:
				// XML PI？
				Accept(_bufferedOpenAngle);
				return xmlPI();
			default:
				// start Tag
				return StartTag(tags);
			}
		}
		if (tags.empty()) {
			getContext().OnError(getCurrentLocation().clone(), RazorResources.getResource(RazorResources.ParseError_OuterTagMissingName));
		}
		return false;
	}

	private boolean xmlPI() {
		// accept "?"
		Assert(HtmlSymbolType.QuestionMark);
		AcceptAndMoveNext();
		return AcceptUntilAll(HtmlSymbolType.QuestionMark, HtmlSymbolType.CloseAngle);
	}

	private boolean bangTag() {
		// accept "!"
		Assert(HtmlSymbolType.Bang);

		if (AcceptAndMoveNext()) {
			if (getCurrentSymbol().getType() == HtmlSymbolType.DoubleHyphen) {
				AcceptAndMoveNext();
				return AcceptUntilAll(HtmlSymbolType.DoubleHyphen, HtmlSymbolType.CloseAngle);
			} else if (getCurrentSymbol().getType() == HtmlSymbolType.LeftBracket) {
				if (AcceptAndMoveNext()) {
					return cdata();
				}
			} else {
				AcceptAndMoveNext();
				return AcceptUntilAll(HtmlSymbolType.CloseAngle);
			}
		}

		return false;
	}

	private boolean cdata() {
		if (getCurrentSymbol().getType() == HtmlSymbolType.Text && StringUtils
				.equalsIgnoreCase(getCurrentSymbol().getContent(), "cdata")) {
			if (AcceptAndMoveNext()) {
				if (getCurrentSymbol().getType() == HtmlSymbolType.LeftBracket) {
					return AcceptUntilAll(HtmlSymbolType.RightBracket, HtmlSymbolType.RightBracket,
							HtmlSymbolType.CloseAngle);
				}
			}
		}

		return false;
	}

	private boolean endTag(SourceLocation tagStart, java.util.Stack<Tuple<HtmlSymbol, SourceLocation>> tags) {
		// accept "/" and move next
		Assert(HtmlSymbolType.Solidus);
		HtmlSymbol solidus = getCurrentSymbol();
		if (!nextToken()) {
			Accept(_bufferedOpenAngle);
			Accept(solidus);
			return false;
		} else {
			String tagName = "";
			if (At(HtmlSymbolType.Text)) {
				tagName = getCurrentSymbol().getContent();
			}
			boolean matched = RemoveTag(tags, tagName, tagStart);

			if (tags.empty() && StringUtils.equalsIgnoreCase(tagName, SyntaxConstants.TextTagName) && matched) {
				Output(SpanKind.Markup);
				return endTextTag(solidus);
			}
			Accept(_bufferedOpenAngle);
			Accept(solidus);

			AcceptUntil(HtmlSymbolType.CloseAngle);

			// accept the ">"
			return Optional(HtmlSymbolType.CloseAngle);
		}
	}

	private boolean endTextTag(HtmlSymbol solidus) {
		SourceLocation start = _bufferedOpenAngle.getStart().clone();

		Accept(_bufferedOpenAngle);
		Accept(solidus);

		Assert(HtmlSymbolType.Text);
		AcceptAndMoveNext();

		boolean seenCloseAngle = Optional(HtmlSymbolType.CloseAngle);

		if (!seenCloseAngle) {
			getContext().OnError(start, RazorResources.getResource(RazorResources.ParseError_TextTagCannotContainAttributes));
		} else {
			getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
		}

		getSpan().setCodeGenerator(SpanCodeGenerator.Null);
		Output(SpanKind.Transition);
		return seenCloseAngle;
	}

	private boolean isTagRecoveryStopPoint(HtmlSymbol sym) {
		return sym.getType() == HtmlSymbolType.CloseAngle || sym.getType() == HtmlSymbolType.Solidus
				|| sym.getType() == HtmlSymbolType.OpenAngle || sym.getType() == HtmlSymbolType.SingleQuote
				|| sym.getType() == HtmlSymbolType.DoubleQuote;
	}

	private void tagContent() {
		if (!At(HtmlSymbolType.WhiteSpace)) {
			// We should be right after the tag name, so if there's no
			// whitespace, something is wrong
			RecoverToEndOfTag();
		} else {
			// We are here ($): <tag$ foo="bar" biz="~/Baz" />
			while (!getEndOfFile() && !isEndOfTag()) {
				beforeAttribute();
			}
		}
	}

	private boolean isEndOfTag() {
		if (At(HtmlSymbolType.Solidus)) {
			if (NextIs(HtmlSymbolType.CloseAngle)) {
				return true;
			} else {
				AcceptAndMoveNext();
			}
		}
		return At(HtmlSymbolType.CloseAngle) || At(HtmlSymbolType.OpenAngle);
	}

	private void beforeAttribute() {
		// http://dev.w3.org/html5/spec/tokenization.html#before-attribute-name-state
		// Capture whitespace

		// methods are not converted
		List<HtmlSymbol> whitespace = ReadWhile(
				sym -> sym.getType() == HtmlSymbolType.WhiteSpace || sym.getType() == HtmlSymbolType.NewLine);

		if (At(HtmlSymbolType.Transition)) {
			// transition outside of attribute value -> Switch to recovery mode
			Accept(whitespace);
			RecoverToEndOfTag();
			return;
		}

		// http://dev.w3.org/html5/spec/tokenization.html#attribute-name-state
		// read the 'name' (i.e. read until the '=' or whitespace/newline)

		List<HtmlSymbol> name = Collections.emptyList();
		if (At(HtmlSymbolType.Text)) {

			// methods are not converted
			name = ReadWhile(sym -> sym.getType() != HtmlSymbolType.WhiteSpace
					&& sym.getType() != HtmlSymbolType.NewLine && sym.getType() != HtmlSymbolType.Equals
					&& sym.getType() != HtmlSymbolType.CloseAngle && sym.getType() != HtmlSymbolType.OpenAngle
					&& (sym.getType() != HtmlSymbolType.Solidus || !NextIs(HtmlSymbolType.CloseAngle)));
		} else {
			// Unexpected character in tag, enter recovery
			Accept(whitespace);
			RecoverToEndOfTag();
			return;
		}

		if (!At(HtmlSymbolType.Equals)) {
			// Saw a space or newline after the name, so just skip this
			// attribute and continue around the loop
			Accept(whitespace);
			Accept(name);
			return;
		}

		Output(SpanKind.Markup);

		// start a new markup block for the attribute
		try(AutoCloseable	disposable= getContext().startBlock(BlockType.Markup)) {
			attributePrefix(whitespace, name);
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	private void attributePrefix(List<HtmlSymbol> whitespace, List<HtmlSymbol> nameSymbols) {
		// First, determine if this is a 'data-' attribute (since those can't
		// use conditional attributes)
		
		LocationTagged<String> name = ISymbol.getContent(nameSymbols, getSpan().getStart().clone());
		boolean attributeCanBeConditional = !StringUtils.startsWithIgnoreCase(name.getValue(), "data-");

		// accept the whitespace and name
		Accept(whitespace);
		Accept(nameSymbols);
		Assert(HtmlSymbolType.Equals); // We should be at "="
		AcceptAndMoveNext();
		HtmlSymbolType quote = HtmlSymbolType.Unknown;
		if (At(HtmlSymbolType.SingleQuote) || At(HtmlSymbolType.DoubleQuote)) {
			quote = getCurrentSymbol().getType();
			AcceptAndMoveNext();
		}

		// We now have the prefix: (i.e. ' foo="')
		LocationTagged<String> prefix = getSpan().getContent();

		if (attributeCanBeConditional) {
			// The block code generator will render the prefix
			getSpan().setCodeGenerator(SpanCodeGenerator.Null);
			Output(SpanKind.Markup);

			// read the values
			while (!getEndOfFile() && !isEndOfAttributeValue(quote, getCurrentSymbol())) {
				attributeValue(quote);
			}

			// Capture the suffix
			LocationTagged<String> suffix = new LocationTagged<String>("", getCurrentLocation().clone());
			if (quote != HtmlSymbolType.Unknown && At(quote)) {
				suffix = getCurrentSymbol().getLocationTaggedContent();
				AcceptAndMoveNext();
			}

			if (getSpan().getSymbols().size() > 0) {
				getSpan().setCodeGenerator(SpanCodeGenerator.Null); // Again,
																	// block
																	// code
																	// generator
																	// will
																	// render
																	// the
																	// suffix
				Output(SpanKind.Markup);
			}

			// create the block code generator
			getContext().getCurrentBlock().setCodeGenerator(new AttributeBlockCodeGenerator(name, prefix, suffix));
		} else {
			// Not a "conditional" attribute, so just read the value

			// methods are not converted
			final HtmlSymbolType quoteTemp=quote;
			SkipToAndParseCode(sym -> isEndOfAttributeValue(quoteTemp, sym));
			if (quote != HtmlSymbolType.Unknown) {
				Optional(quote);
			}
			Output(SpanKind.Markup);
		}
	}

	private void attributeValue(HtmlSymbolType quote) {
		SourceLocation prefixStart = getCurrentLocation().clone();

		// methods are not converted
		List<HtmlSymbol> prefix = ReadWhile(
				sym -> sym.getType() == HtmlSymbolType.WhiteSpace || sym.getType() == HtmlSymbolType.NewLine);
		Accept(prefix);

		if (At(HtmlSymbolType.Transition)) {
			SourceLocation valueStart = getCurrentLocation().clone();
			putCurrentBack();

			// Output the prefix but as a null-span.
			// DynamicAttributeBlockCodeGenerator will render it
			getSpan().setCodeGenerator(SpanCodeGenerator.Null);

			// Dynamic value, start a new block and set the code generator
			try(AutoCloseable	disposable=getContext().startBlock(BlockType.Markup)) {
				getContext().getCurrentBlock().setCodeGenerator(
						new DynamicAttributeBlockCodeGenerator(
								ISymbol.getContent(prefix,prefixStart), valueStart));

				OtherParserBlock();
			} catch (Exception ex){
				ex.printStackTrace();
			}
		} else if (At(HtmlSymbolType.Text) && getCurrentSymbol().getContent().length() > 0
				&& getCurrentSymbol().getContent().charAt(0) == '~' && NextIs(HtmlSymbolType.Solidus)) {
			// Virtual Path value
			SourceLocation valueStart = getCurrentLocation().clone();
			virtualPath();
			getSpan().setCodeGenerator(new LiteralAttributeCodeGenerator(
					ISymbol.getContent(prefix, prefixStart),
					new LocationTagged<SpanCodeGenerator>(new ResolveUrlCodeGenerator(), valueStart)));
		} else {
			// Literal value
			// 'quote' should be "Unknown" if not quoted and symbols coming from
			// the tokenizer should never have "Unknown" type.
			// These three conditions find separators which break the attribute
			// value into portions
			// This condition checks for the end of the attribute value (it
			// repeats some of the checks above but for now that's ok)
		
			List<HtmlSymbol> value = ReadWhile(
					sym -> sym.getType() != HtmlSymbolType.WhiteSpace
							&& sym.getType() != HtmlSymbolType.NewLine
							&& sym.getType() != HtmlSymbolType.Transition
							&& !isEndOfAttributeValue(quote, sym));
			Accept(value);
			getSpan().setCodeGenerator(LiteralAttributeCodeGenerator.create(
					ISymbol.getContent(prefix,prefixStart),
					ISymbol.getContent(value, prefixStart)));
		}
		Output(SpanKind.Markup);
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
				|| (sym.getType() == HtmlSymbolType.Solidus && NextIs(HtmlSymbolType.CloseAngle))
				|| sym.getType() == HtmlSymbolType.CloseAngle || sym.getType() == HtmlSymbolType.WhiteSpace
				|| sym.getType() == HtmlSymbolType.NewLine;
	}

	private void virtualPath() {
		Assert(HtmlSymbolType.Text);
		assert getCurrentSymbol().getContent().length() > 0 && getCurrentSymbol().getContent().charAt(0) == '~';

		// parse until a transition symbol, whitespace, newline or quote. We
		// support only a fairly minimal subset of Virtual Paths
		AcceptUntil(HtmlSymbolType.Transition, HtmlSymbolType.WhiteSpace, HtmlSymbolType.NewLine,
				HtmlSymbolType.SingleQuote, HtmlSymbolType.DoubleQuote);

		// Output a Virtual Path span
		getSpan().getEditHandler().setEditorHints(EditorHints.VirtualPath);
	}

	private void RecoverToEndOfTag() {
		// accept until ">", "/" or "<", but parse code
		while (!getEndOfFile()) {
			SkipToAndParseCode((p) -> isTagRecoveryStopPoint(p));
			if (!getEndOfFile()) {
				EnsureCurrent();
				switch (getCurrentSymbol().getType()) {
				case SingleQuote:
				case DoubleQuote:
					ParseQuoted();
					break;
				case OpenAngle:
					// Another "<" means this tag is invalid.
				case Solidus:
					// Empty tag
				case CloseAngle:
					// End of tag
					return;
				default:
					AcceptAndMoveNext();
					break;
				}
			}
		}
	}

	private void ParseQuoted() {
		HtmlSymbolType type = getCurrentSymbol().getType();
		AcceptAndMoveNext();
		ParseQuoted(type);
	}

	private void ParseQuoted(HtmlSymbolType type) {
		SkipToAndParseCode(type);
		if (!getEndOfFile()) {
			Assert(type);
			AcceptAndMoveNext();
		}
	}

	private boolean StartTag(java.util.Stack<Tuple<HtmlSymbol, SourceLocation>> tags) throws ArgumentNullException {
		// If we're at text, it's the name, otherwise the name is ""
		HtmlSymbol tagName;
		if (At(HtmlSymbolType.Text)) {
			tagName = getCurrentSymbol();
		} else {
			tagName = new HtmlSymbol(getCurrentLocation().clone(), "", HtmlSymbolType.Unknown);
		}

		Tuple<HtmlSymbol, SourceLocation> tag = Tuple.create(tagName, _lastTagStart.clone());

		if (tags.empty() && StringUtils.equalsIgnoreCase(tag.getItem1().getContent(),
				SyntaxConstants.TextTagName)) {
			Output(SpanKind.Markup);
			getSpan().setCodeGenerator(SpanCodeGenerator.Null);

			Accept(_bufferedOpenAngle);
			Assert(HtmlSymbolType.Text);

			AcceptAndMoveNext();

			int bookmark = getCurrentLocation().getAbsoluteIndex();
			Iterable<HtmlSymbol> tokens = ReadWhile(IsSpacingToken(true));
			boolean empty = At(HtmlSymbolType.Solidus);
			if (empty) {
				Accept(tokens);
				Assert(HtmlSymbolType.Solidus);
				AcceptAndMoveNext();
				bookmark = getCurrentLocation().getAbsoluteIndex();
				tokens = ReadWhile(IsSpacingToken(true));
			}

			if (!Optional(HtmlSymbolType.CloseAngle)) {
				getContext().getSource().setPosition(bookmark);
				nextToken();
				getContext().OnError(tag.getItem2(), RazorResources.getResource(RazorResources.ParseError_TextTagCannotContainAttributes));
			} else {
				Accept(tokens);
				getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
			}

			if (!empty) {
				tags.push(tag);
			}
			Output(SpanKind.Transition);
			return true;
		}
		Accept(_bufferedOpenAngle);
		Optional(HtmlSymbolType.Text);
		return RestOfTag(tag, tags);
	}

	private boolean RestOfTag(Tuple<HtmlSymbol, SourceLocation> tag,
			java.util.Stack<Tuple<HtmlSymbol, SourceLocation>> tags) {
		tagContent();

		// We are now at a possible end of the tag
		// Found '<', so we just abort this tag.
		if (At(HtmlSymbolType.OpenAngle)) {
			return false;
		}

		boolean isEmpty = At(HtmlSymbolType.Solidus);
		// Found a solidus, so don't accept it but DON'T push the tag to the
		// stack
		if (isEmpty) {
			AcceptAndMoveNext();
		}

		// Check for the '>' to determine if the tag is finished
		boolean seenClose = Optional(HtmlSymbolType.CloseAngle);
		if (!seenClose) {
			getContext().OnError(tag.getItem2(), RazorResources.getResource(RazorResources.ParseError_UnfinishedTag),
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
					Iterable<HtmlSymbol> ws = ReadWhile(IsSpacingToken(true));

					// Open Angle
					if (At(HtmlSymbolType.OpenAngle) && NextIs(HtmlSymbolType.Solidus)) {
						HtmlSymbol openAngle = getCurrentSymbol();
						nextToken();
						Assert(HtmlSymbolType.Solidus);
						HtmlSymbol solidus = getCurrentSymbol();
						nextToken();
						if (At(HtmlSymbolType.Text) && StringUtils.equalsIgnoreCase(getCurrentSymbol().getContent(),
								tagName)) {
							// accept up to here
							Accept(ws);
							Accept(openAngle);
							Accept(solidus);
							AcceptAndMoveNext();

							// accept to '>', '<' or EOF
							AcceptUntil(HtmlSymbolType.CloseAngle, HtmlSymbolType.OpenAngle);
							// accept the '>' if we saw it. And if we do see it,
							// we're complete
							return Optional(HtmlSymbolType.CloseAngle);
						} // at(HtmlSymbolType.Text) &&
							// String.Equals(CurrentSymbol.Content, tagName,
							// StringComparison.OrdinalIgnoreCase)
					} // at(HtmlSymbolType.OpenAngle) &&
						// NextIs(HtmlSymbolType.Solidus)

					// Go back to the bookmark and just finish this tag at the
					// close angle
					getContext().getSource().setPosition(bookmark);
					nextToken();
				} else if (StringUtils.equalsIgnoreCase(tagName, "script")) {
					SkipToEndScriptAndParseCode();
				} else {
					// Push the tag on to the stack
					tags.push(tag);
				}
			}
		}
		return seenClose;
	}

	private void SkipToEndScriptAndParseCode() {
		// Special case for <script>: Skip to end of script tag and parse code
		boolean seenEndScript = false;
		while (!seenEndScript && !getEndOfFile()) {
			SkipToAndParseCode(HtmlSymbolType.OpenAngle);
			SourceLocation tagStart = getCurrentLocation().clone();
			AcceptAndMoveNext();
			AcceptWhile(HtmlSymbolType.WhiteSpace);
			if (Optional(HtmlSymbolType.Solidus)) {
				AcceptWhile(HtmlSymbolType.WhiteSpace);
				if (At(HtmlSymbolType.Text) && StringUtils.equalsIgnoreCase(getCurrentSymbol().getContent(), "script"
						)) {
					// </script!
					SkipToAndParseCode(HtmlSymbolType.CloseAngle);
					if (!Optional(HtmlSymbolType.CloseAngle)) {
						getContext().OnError(tagStart,
								RazorResources.getResource(RazorResources.ParseError_UnfinishedTag), "script");
					}
					seenEndScript = true;
				}
			}
		}
	}

	private boolean AcceptUntilAll(HtmlSymbolType... endSequence) {
		while (!getEndOfFile()) {
			SkipToAndParseCode(endSequence[0]);
			if (AcceptAll(endSequence)) {
				return true;
			}
		}
		assert getEndOfFile();
		getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.Any);
		return false;
	}

	private boolean RemoveTag(java.util.Stack<Tuple<HtmlSymbol, SourceLocation>> tags, String tagName,
			SourceLocation tagStart) {
		Tuple<HtmlSymbol, SourceLocation> currentTag = null;
		while (tags.size() > 0) {
			currentTag = tags.pop();
			if (StringUtils.equalsIgnoreCase(tagName, currentTag.getItem1().getContent()
					)) {
				// Matched the tag
				return true;
			}
		}
		if (currentTag != null) {
			getContext().OnError(currentTag.getItem2(),
					RazorResources.getResource(RazorResources.ParseError_MissingEndTag),
					currentTag.getItem1().getContent());
		} else {
			getContext().OnError(tagStart, RazorResources.getResource(RazorResources.ParseError_UnexpectedEndTag), tagName);
		}
		return false;
	}

	private void EndTagBlock(java.util.Stack<Tuple<HtmlSymbol, SourceLocation>> tags, boolean complete) {
		if (tags.size() > 0) {
			// Ended because of EOF, not matching close tag. Throw error for
			// last tag
			while (tags.size() > 1) {
				tags.pop();
			}
			Tuple<HtmlSymbol, SourceLocation> tag = tags.pop();
			getContext().OnError(tag.getItem2(), RazorResources.getResource(RazorResources.ParseError_MissingEndTag),
					tag.getItem1().getContent());
		} else if (complete) {
			getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
		}
		tags.clear();
		if (!getContext().getDesignTimeMode()) {
			AcceptWhile(HtmlSymbolType.WhiteSpace);
			if (!getEndOfFile() && getCurrentSymbol().getType() == HtmlSymbolType.NewLine) {
				AcceptAndMoveNext();
			}
		} else if (getSpan().getEditHandler().getAcceptedCharacters() == AcceptedCharacters.Any) {
			AcceptWhile(HtmlSymbolType.WhiteSpace);
			Optional(HtmlSymbolType.NewLine);
		}
		putCurrentBack();

		if (!complete) {
			AddMarkerSymbolIfNecessary();
		}
		Output(SpanKind.Markup);
	}

	@Override
	public void ParseDocument() throws InvalidOperationException {
		if (getContext() == null) {
			throw new InvalidOperationException(RazorResources.getResource(RazorResources.Parser_Context_Not_Set));
		}


		try(AutoCloseable spanConfig =PushSpanConfig((p) -> defaultMarkupSpan(p))) {

			try(AutoCloseable dispose=getContext().startBlock(BlockType.Markup)) {
				nextToken();
				while (!getEndOfFile()) {
					SkipToAndParseCode(HtmlSymbolType.OpenAngle);
					ScanTagInDocumentContext();
				}
				AddMarkerSymbolIfNecessary();
				Output(SpanKind.Markup);
			} catch (Exception ex){
				ex.printStackTrace();
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	/**
	 * Reads the content of a tag (if present) in the MarkupDocument (or
	 * MarkupSection) context, where we don't care about maintaining a stack of
	 * tags.
	 * 
	 * @return A boolean indicating if we scanned at least one tag.
	 */
	private boolean ScanTagInDocumentContext() {
		if (Optional(HtmlSymbolType.OpenAngle)) {
			if (At(HtmlSymbolType.Bang)) {
				bangTag();
				return true;
			} else if (At(HtmlSymbolType.QuestionMark)) {
				xmlPI();
				return true;
			} else if (!At(HtmlSymbolType.Solidus)) {
				boolean scriptTag = At(HtmlSymbolType.Text) && StringUtils
						.equalsIgnoreCase(getCurrentSymbol().getContent(), "script");
				Optional(HtmlSymbolType.Text);
				tagContent(); // parse the tag, don't care about the content
				Optional(HtmlSymbolType.Solidus);
				Optional(HtmlSymbolType.CloseAngle);
				if (scriptTag) {
					SkipToEndScriptAndParseCode();
				}
				return true;
			}
		}
		return false;
	}


	private Set<String> _voidElements = new java.util.HashSet<String>(Arrays.asList("area", "base", "br", "col",
			"command", "embed", "hr", "img", "input", "keygen", "link", "meta", "param", "source", "track", "wbr"));

	public final Set<String> getVoidElements() {
		return _voidElements;
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
		Output(SpanKind.Markup);
	}

	protected final void SkipToAndParseCode(HtmlSymbolType type) {
		
		SkipToAndParseCode(sym -> sym.getType() == type);
	}

	protected final void SkipToAndParseCode(Predicate<HtmlSymbol> condition) {
		HtmlSymbol last = null;
		boolean startOfLine = false;
		while (!getEndOfFile() && !condition.test(getCurrentSymbol())) {
			if (At(HtmlSymbolType.NewLine)) {
				if (last != null) {
					Accept(last);
				}

				// Mark the start of a new line
				startOfLine = true;
				last = null;
				AcceptAndMoveNext();
			} else if (At(HtmlSymbolType.Transition)) {
				HtmlSymbol transition = getCurrentSymbol();
				nextToken();
				if (At(HtmlSymbolType.Transition)) {
					if (last != null) {
						Accept(last);
						last = null;
					}
					Output(SpanKind.Markup);
					Accept(transition);
					getSpan().setCodeGenerator(SpanCodeGenerator.Null);
					Output(SpanKind.Markup);
					AcceptAndMoveNext();
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
						// accept last
						Accept(last);
						last = null;
					}
				}

				OtherParserBlock();
			} else if (At(HtmlSymbolType.RazorCommentTransition)) {
				if (last != null) {
					Accept(last);
					last = null;
				}
				AddMarkerSymbolIfNecessary();
				Output(SpanKind.Markup);
				RazorComment();
			} else {
				// As long as we see whitespace, we're still at the "start" of
				// the line
				startOfLine &= At(HtmlSymbolType.WhiteSpace);

				// If there's a last token, accept it
				if (last != null) {
					Accept(last);
					last = null;
				}

				// advance
				last = getCurrentSymbol();
				nextToken();
			}
		}

		if (last != null) {
			Accept(last);
		}
	}

	protected static Predicate<HtmlSymbol> IsSpacingToken(boolean includeNewLines) {
		
		return sym -> sym.getType() == HtmlSymbolType.WhiteSpace
				|| (includeNewLines && sym.getType() == HtmlSymbolType.NewLine);
	}

	private void OtherParserBlock() {
		AddMarkerSymbolIfNecessary();
		Output(SpanKind.Markup);

		try (AutoCloseable disposable=PushSpanConfig()){
			getContext().SwitchActiveParser();
			getContext().getCodeParser().parseBlock();
			getContext().SwitchActiveParser();
		} catch (Exception ex){
			ex.printStackTrace();
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
	public void parseSection(Tuple<String, String> nestingSequences, boolean caseSensitive) {
		if (getContext() == null) {
			//throw new InvalidOperationException(RazorResources.getResource(RazorResources.Parser_Context_Not_Set());
		}


		try (AutoCloseable	disposable=PushSpanConfig((p) -> defaultMarkupSpan(p))){
			// using (Context.startBlock(BlockType.Markup))
			AutoCloseable	disposable2=getContext().startBlock(BlockType.Markup);
			try {
				nextToken();
				setCaseSensitive(caseSensitive);
				if (nestingSequences.getItem1() == null) {
					NonNestingSection(nestingSequences.getItem2().split(""));
				} else {
					NestingSection(nestingSequences);
				}
				AddMarkerSymbolIfNecessary();
				Output(SpanKind.Markup);
			} catch (Exception ex){
					ex.printStackTrace();
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}

	private void NonNestingSection(String[] nestingSequenceComponents) {
		do {

			// methods are not converted
			SkipToAndParseCode(sym -> sym.getType() == HtmlSymbolType.OpenAngle || AtEnd(nestingSequenceComponents));
			ScanTagInDocumentContext();
			if (!getEndOfFile() && AtEnd(nestingSequenceComponents)) {
				break;
			}
		} while (!getEndOfFile());

		putCurrentBack();
	}

	private void NestingSection(Tuple<String, String> nestingSequences) {
		int nesting = 1;
		while (nesting > 0 && !getEndOfFile()) {

			// methods are not converted
			SkipToAndParseCode(
					sym -> sym.getType() == HtmlSymbolType.Text || sym.getType() == HtmlSymbolType.OpenAngle);
			if (At(HtmlSymbolType.Text)) {
				nesting += ProcessTextToken(nestingSequences, nesting);
				if (getCurrentSymbol() != null) {
					AcceptAndMoveNext();
				} else if (nesting > 0) {
					nextToken();
				}
			} else {
				ScanTagInDocumentContext();
			}
		}
	}

	private boolean stringEquals(String str,String strB){
		if(getCaseSensitive())
			return StringUtils.equals(str,strB);

		return  StringUtils.equalsIgnoreCase(str,strB);
	}

	private boolean AtEnd(String[] nestingSequenceComponents) {
		EnsureCurrent();
		//StringUtils.equals(getCurrentSymbol().getContent(),nestingSequenceComponents)
		if (stringEquals(getCurrentSymbol().getContent(), nestingSequenceComponents[0])) {
			int bookmark = getCurrentSymbol().getStart().getAbsoluteIndex();
			try {
				for (String component : nestingSequenceComponents) {
					if (!getEndOfFile() && !stringEquals(getCurrentSymbol().getContent(), component)) {
						return false;
					}
					nextToken();
					while (!getEndOfFile() && IsSpacingToken(true).test(getCurrentSymbol())) {
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

	private int ProcessTextToken(Tuple<String, String> nestingSequences, int currentNesting) {
		for (int i = 0; i < getCurrentSymbol().getContent().length(); i++) {
			int nestingDelta = HandleNestingSequence(nestingSequences.getItem1(), i, currentNesting, 1);
			if (nestingDelta == 0) {
				nestingDelta = HandleNestingSequence(nestingSequences.getItem2(), i, currentNesting, -1);
			}

			if (nestingDelta != 0) {
				return nestingDelta;
			}
		}
		return 0;
	}

	private int HandleNestingSequence(String sequence, int position, int currentNesting, int retIfMatched) {
		if (sequence != null && getCurrentSymbol().getContent().charAt(position) == sequence.charAt(0)
				&& position + sequence.length() <= getCurrentSymbol().getContent().length()) {
			String possibleStart = getCurrentSymbol().getContent().substring(position, position + sequence.length());
			if (stringEquals(possibleStart, sequence)) {
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

				// accept the first chunk (up to the nesting sequence we just
				// saw)
				if (!StringUtils.isBlank(preSequence.getContent())) {
					Accept(preSequence);
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
					Accept(sequenceToken);

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

		return obj.equals(others);
	}
}