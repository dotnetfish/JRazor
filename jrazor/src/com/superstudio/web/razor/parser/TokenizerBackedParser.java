package com.superstudio.web.razor.parser;

import com.superstudio.commons.CollectionHelper;

import com.superstudio.commons.IEquatable;
import com.superstudio.commons.Tuple;
import com.superstudio.commons.csharpbridge.action.ActionTwo;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.web.RazorResources;
import com.superstudio.web.razor.editor.SpanEditHandler;
import com.superstudio.web.razor.generator.RazorCommentCodeGenerator;
import com.superstudio.web.razor.generator.SpanCodeGenerator;
import com.superstudio.web.razor.parser.syntaxTree.*;
import com.superstudio.web.razor.text.SourceLocation;
import com.superstudio.web.razor.tokenizer.Tokenizer;
import com.superstudio.web.razor.tokenizer.TokenizerView;
import com.superstudio.web.razor.tokenizer.symbols.ISymbol;
import com.superstudio.web.razor.tokenizer.symbols.KnownSymbolType;
import com.superstudio.web.razor.tokenizer.symbols.SymbolBase;
import com.superstudio.web.razor.utils.DisposableAction;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class TokenizerBackedParser<TTokenizer extends Tokenizer<TSymbol, TSymbolType>, TSymbol extends SymbolBase<TSymbolType>, TSymbolType>
		extends ParserBase implements IEquatable<Object> {

	private TokenizerView<TTokenizer, TSymbol, TSymbolType> _tokenizer;

	protected TokenizerBackedParser() {
		setSpan(new SpanBuilder());
	}

	private SpanBuilder privateSpan;

	protected final SpanBuilder getSpan() {
		return privateSpan;
	}

	protected final void setSpan(SpanBuilder value) {
		privateSpan = value;
	}

	protected final TokenizerView<TTokenizer, TSymbol, TSymbolType> getTokenizer() {
		return (_tokenizer != null) ? _tokenizer : InitTokenizer();
	}

	private Consumer<SpanBuilder> privateSpanConfig;

	protected final Consumer<SpanBuilder> getSpanConfig() {
		return privateSpanConfig;
	}

	protected final void setSpanConfig(Consumer<SpanBuilder> value) {
		privateSpanConfig = value;
	}

	protected final TSymbol getCurrentSymbol() {
		return getTokenizer().getCurrent();
	}

	private TSymbol privatePreviousSymbol;

	protected final TSymbol getPreviousSymbol() {
		return privatePreviousSymbol;
	}

	private void setPreviousSymbol(TSymbol value) {
		privatePreviousSymbol = value;
	}

	protected final SourceLocation getCurrentLocation() {
		return (getEndOfFile() || getCurrentSymbol() == null) ? getContext().getSource().getLocation()
				: getCurrentSymbol().getStart();
	}

	protected final boolean getEndOfFile() {
		return getTokenizer().getEndOfFile();
	}

	protected abstract LanguageCharacteristics<TTokenizer, TSymbol, TSymbolType> getLanguage();

	protected void handleEmbeddedTransition() {
	}

	protected boolean isAtEmbeddedTransition(boolean allowTemplatesAndComments, boolean allowTransitions) {
		return false;
	}

	public void buildSpan(SpanBuilder span, SourceLocation start, String content) {
		for (ISymbol sym : getLanguage().tokenizeString(start, content)) {
			span.accept(sym);
		}
	}

	protected final void initialize(SpanBuilder span) {
		if (getSpanConfig() != null) {
			getSpanConfig().accept(span);
		}
	}

	protected final boolean nextToken() {
		setPreviousSymbol(getCurrentSymbol());
		return getTokenizer().next();
	}

	private TokenizerView<TTokenizer, TSymbol, TSymbolType> InitTokenizer() {
		return _tokenizer = new TokenizerView<TTokenizer, TSymbol, TSymbolType>(
				getLanguage().createTokenizer(getContext().getSource()));
	}

	// Helpers
	
	public final void Assert(TSymbolType expectedType) {
		assert !getEndOfFile() && getCurrentSymbol().getType()==expectedType;
	}

	protected final void putBack(TSymbol symbol) {
		if (symbol != null) {
			getTokenizer().putBack(symbol);
		}
	}

	/**
	 * Put the specified symbols back in the input stream. The provided list
	 * MUST be in the ORDER THE SYMBOLS WERE READ. The list WILL be reversed and
	 * the Putback(TSymbol) will be called on each item.
	 * 
	 * 
	 * If a document contains symbols: a, b, c, d, e, f and AcceptWhile or
	 * AcceptUntil is used to collect until d the list returned by
	 * AcceptWhile/Until will contain: a, b, c IN THAT ORDER that is the correct
	 * format for providing to this method. The caller of this method would, in
	 * that case, want to put c, b and a back into the stream, so "a, b, c" is
	 * the CORRECT order
	 * 
	 */
	protected final void putBack(Iterable<TSymbol> symbols) {
		List<TSymbol> reversedResult=new ArrayList<TSymbol>();
		if(symbols instanceof List){
			reversedResult =(List<TSymbol>)symbols;
					
		}else{
			for(TSymbol sym:symbols){
				reversedResult.add(sym);
			}
		}
		Collections.reverse(reversedResult);
		for (TSymbol symbol : symbols) {
			putBack(symbol);
		}
		
	}

	protected final void putCurrentBack() {
		if (!getEndOfFile() && getCurrentSymbol() != null) {
			putBack(getCurrentSymbol());
		}
	}

	protected final boolean balance(BalancingModes mode) {
		TSymbolType left = getCurrentSymbol().getType();
		TSymbolType right = getLanguage().flipBracket(left);
		SourceLocation start = getCurrentLocation().clone();
		AcceptAndMoveNext();
		if (getEndOfFile() && !mode.HasFlag(BalancingModes.NoErrorOnFailure)) {
			getContext().OnError(start, RazorResources.getParseError_Expected_CloseBracket_Before_EOF(),
					getLanguage().getSample(left), getLanguage().getSample(right));
		}

		return balance(mode, left, right, start);
	}

	protected final boolean balance(BalancingModes mode, TSymbolType left, TSymbolType right, SourceLocation start) {
		int startPosition = getCurrentLocation().getAbsoluteIndex();
		int nesting = 1;
		if (!getEndOfFile()) {
			java.util.List<TSymbol> syms = new java.util.ArrayList<TSymbol>();
			do {
				if (isAtEmbeddedTransition(mode.HasFlag(BalancingModes.AllowCommentsAndTemplates),
						mode.HasFlag(BalancingModes.AllowEmbeddedTransitions))) {
					Accept(syms);
					syms.clear();
					handleEmbeddedTransition();

					// reset backtracking since we've already outputted some
					// spans.
					startPosition = getCurrentLocation().getAbsoluteIndex();
				}
				if (At(left)) {
					nesting++;
				} else if (At(right)) {
					nesting--;
				}
				if (nesting > 0) {
					syms.add(getCurrentSymbol());
				}
			} while (nesting > 0 && nextToken());

			if (nesting > 0) {
				if (!mode.HasFlag(BalancingModes.NoErrorOnFailure)) {
					getContext().OnError(start, RazorResources.getParseError_Expected_CloseBracket_Before_EOF(),
							getLanguage().getSample(left), getLanguage().getSample(right));
				}
				if (mode.HasFlag(BalancingModes.BacktrackOnFailure)) {
					getContext().getSource().setPosition(startPosition);
					nextToken();
				} else {
					Accept(syms);
				}
			} else {
				// accept all the symbols we saw
				Accept(syms);
			}
		}
		return nesting == 0;
	}

	protected final boolean NextIs(TSymbolType type) {
			return NextIs(sym -> sym != null && type==sym.getType());
	}

	protected final boolean NextIs(TSymbolType... types) {
		return NextIs(sym -> sym != null && CollectionHelper.any(Arrays.asList(types), t -> t==sym.getType()));
	}

	protected final boolean NextIs(Function<TSymbol, Boolean> condition) {
		TSymbol cur = getCurrentSymbol();
		nextToken();
		boolean result = condition.apply(getCurrentSymbol());
		putCurrentBack();
		putBack(cur);
		EnsureCurrent();
		return result;
	}

	protected final boolean Was(TSymbolType type) {
		boolean result = getPreviousSymbol().getType() == type;
		// boolean result2 = equals(getPreviousSymbol().getType(), type);

		return getPreviousSymbol() != null && result;
	}

	protected final boolean At(TSymbolType type) {

		return !getEndOfFile() && getCurrentSymbol() != null && getCurrentSymbol().getType() == type;
	}

	protected final boolean AcceptAndMoveNext() {
		Accept(getCurrentSymbol());
		return nextToken();
	}

	protected final TSymbol AcceptSingleWhiteSpaceCharacter() {
		if (getLanguage().isWhiteSpace(getCurrentSymbol())) {
			Tuple<TSymbol, TSymbol> pair = getLanguage().splitSymbol(getCurrentSymbol(), 1,
					getLanguage().getKnownSymbolType(KnownSymbolType.WhiteSpace));
			Accept(pair.getItem1());
			getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
			nextToken();
			return pair.getItem2();
		}
		return null;
	}

	protected final void Accept(Iterable<TSymbol> symbols) {
		/*for (TSymbol symbol : symbols) {
			accept(symbol);
		}*/
		Iterator<TSymbol> symbolsIt=symbols.iterator();
		while(symbolsIt.hasNext()){
			Accept(symbolsIt.next());
		}
		//for(TSymbol)
	}

	protected final void Accept(TSymbol symbol) {
		if (symbol != null) {

			if (symbol.getErrors() != null) {
				for (RazorError error : symbol.getErrors()) {
					getContext().getErrors().add(error);
				}
			}

			getSpan().accept(symbol);
		}
	}

	protected final boolean AcceptAll(TSymbolType... types) {
		for (TSymbolType type : types) {
			if (getCurrentSymbol() == null || !equals(getCurrentSymbol().getType(), type)) {
				return false;
			}
			AcceptAndMoveNext();
		}
		return true;
	}

	protected final void AddMarkerSymbolIfNecessary() {
		AddMarkerSymbolIfNecessary(getCurrentLocation().clone());
	}

	protected final void AddMarkerSymbolIfNecessary(SourceLocation location) {
		if (getSpan().getSymbols().size() == 0 && getContext().getLastAcceptedCharacters() != AcceptedCharacters.Any) {
			Accept(getLanguage().createMarkerSymbol(location));
		}
	}

	protected final void Output(SpanKind kind) {
		Configure(kind, null);
		Output();
	}

	protected final void Output(SpanKind kind, AcceptedCharacters accepts) {
		Configure(kind, accepts);
		Output();
	}

	protected final void Output(AcceptedCharacters accepts) {
		Configure(null, accepts);
		Output();
	}

	private void Output() {
		if (getSpan().getSymbols().size() > 0) {
			try {
				getContext().addSpan(getSpan().build());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			initialize(getSpan());
		}
	}

	protected final AutoCloseable PushSpanConfig() {
		return PushSpanConfig((ActionTwo<SpanBuilder, Consumer<SpanBuilder>>) null);
	}

	protected final AutoCloseable PushSpanConfig(Consumer<SpanBuilder> newConfig) {
			return PushSpanConfig(newConfig == null ? (ActionTwo<SpanBuilder, Consumer<SpanBuilder>>) null
				: (span, t) -> newConfig.accept(span));
	}

	protected final AutoCloseable PushSpanConfig(ActionTwo<SpanBuilder, Consumer<SpanBuilder>> newConfig) {
		/*
		 * Consumer<SpanBuilder> old = getSpanConfig();
		 * ConfigureSpan(newConfig); 
		 *  return new DisposableAction(() ->
		 * getSpanConfig.apply());
		 */
		Consumer<SpanBuilder> old = getSpanConfig();
		ConfigureSpan(newConfig);
		return new DisposableAction(() -> setSpanConfig(old));
	}

	protected final void ConfigureSpan(Consumer<SpanBuilder> config) {
		setSpanConfig(config);
		initialize(getSpan());
	}


	protected final void ConfigureSpan(ActionTwo<SpanBuilder, Consumer<SpanBuilder>> config) {
		Consumer<SpanBuilder> prev = getSpanConfig();
		if (config == null) {
			setSpanConfig(null);
		} else {
				setSpanConfig(span -> config.execute(span, prev));
		}
		initialize(getSpan());
	}

	protected final void Expected(KnownSymbolType type) {
		Expected(getLanguage().getKnownSymbolType(type));
	}



	protected final void Expected(TSymbolType... types) {
		assert !getEndOfFile() && getCurrentSymbol() != null
				&& Arrays.asList(types).contains(getCurrentSymbol().getType());
		AcceptAndMoveNext();
	}

	protected final boolean Optional(KnownSymbolType type) {
		return Optional(getLanguage().getKnownSymbolType(type));
	}

	protected final boolean Optional(TSymbolType type) {
		if (At(type)) {
			AcceptAndMoveNext();
			return true;
		}
		return false;
	}

	protected final boolean Required(TSymbolType expected, boolean errorIfNotFound, String errorBase) {
		boolean found = At(expected);
		if (!found && errorIfNotFound) {
			String error;
			if (getLanguage().isNewLine(getCurrentSymbol())) {
				error = RazorResources.getErrorComponent_Newline();
			} else if (getLanguage().isWhiteSpace(getCurrentSymbol())) {
				error = RazorResources.getErrorComponent_Whitespace();
			} else if (getEndOfFile()) {
				error = RazorResources.getErrorComponent_EndOfFile();
			} else {
				error = String.format(RazorResources.getErrorComponent_Character(), getCurrentSymbol().getContent());
			}

			getContext().OnError(getCurrentLocation().clone(), errorBase, error);
		}
		return found;
	}

	protected final boolean EnsureCurrent() {
		if (getCurrentSymbol() == null) {
			return nextToken();
		}
		return true;
	}

	protected final void AcceptWhile(TSymbolType type) {
	
		AcceptWhile(sym -> type==sym.getType());
	}

	// We want to avoid array allocations and enumeration where possible, so we
	// use the same technique as String.Format
	protected final void AcceptWhile(TSymbolType type1, TSymbolType type2) {

		AcceptWhile(sym -> equals(type1, sym.getType()) || equals(type2, sym.getType()));
	}

	protected final void AcceptWhile(TSymbolType type1, TSymbolType type2, TSymbolType type3) {
			AcceptWhile(
				sym -> type1==sym.getType() || type2==sym.getType() || type3==sym.getType());
	}

	protected final void AcceptWhile(TSymbolType... types) {
		
		// AcceptWhile(sym -> CollectionHelper.any(Arrays.asList(types),
		// (expected -> equals(expected, sym.getType()))));
		
		AcceptWhile(sym -> CollectionHelper.any(Arrays.asList(types), (expected -> expected == sym.getType())));
	}

	protected final void AcceptUntil(TSymbolType type) {

		// methods are not converted
		// AcceptWhile(sym -> !equals(type, sym.getType()));
		AcceptWhile(sym -> type != sym.getType());
	}

	// We want to avoid array allocations and enumeration where possible, so we
	// use the same technique as String.Format
	protected final void AcceptUntil(TSymbolType type1, TSymbolType type2) {

		// methods are not converted
		// AcceptWhile(sym -> !equals(type1, sym.getType()) && !equals(type2,
		// sym.getType()));
		AcceptWhile(sym -> type1 != sym.getType() && type2 != sym.getType());
	}

	protected final void AcceptUntil(TSymbolType type1, TSymbolType type2, TSymbolType type3) {

		// methods are not converted
		AcceptWhile(
				sym -> type1!=sym.getType()
				&& type2!=sym.getType()
				&& type3!=sym.getType());
	}

	protected final void AcceptUntil(TSymbolType... types) {

		// methods are not converted
		//AcceptWhile(sym -> CollectionHelper.all(Arrays.asList(types),
		//		expected -> !equals(expected, sym.getType())));
		
		
		AcceptWhile(sym -> CollectionHelper.all(Arrays.asList(types),
						expected -> expected!= sym.getType()));
	}

	protected final void AcceptWhile(Predicate<TSymbol> condition) {
		Accept(ReadWhileLazy(condition));
	}

	protected final List<TSymbol> ReadWhile(Predicate<TSymbol> condition) {
		//original code ReadWhileLazy(condition).ToList();
		//it does not lazy read .....
		Iterable<TSymbol> result=ReadWhileLazy(condition);
		List<TSymbol> symList=new ArrayList<TSymbol>();
		for(TSymbol sym:result){
			symList.add(sym);
		}
		return symList;
		//return ReadWhileLazy(condition);
	}

	protected final TSymbol AcceptWhiteSpaceInLines() {
		TSymbol lastWs = null;
		while (getLanguage().isWhiteSpace(getCurrentSymbol()) || getLanguage().isNewLine(getCurrentSymbol())) {
			// Capture the previous whitespace node
			if (lastWs != null) {
				Accept(lastWs);
			}

			if (getLanguage().isWhiteSpace(getCurrentSymbol())) {
				lastWs = getCurrentSymbol();
			} else if (getLanguage().isNewLine(getCurrentSymbol())) {
				// accept newline and reset last whitespace tracker
				Accept(getCurrentSymbol());
				lastWs = null;
			}

			getTokenizer().next();
		}
		return lastWs;
	}

	protected final boolean AtIdentifier(boolean allowKeywords) {
		return getCurrentSymbol() != null && (getLanguage().isIdentifier(getCurrentSymbol())
				|| (allowKeywords && getLanguage().isKeyword(getCurrentSymbol())));
	}

	// Don't open this to sub classes because it's lazy but it looks eager.
	// You have to advance the Enumerable to read the next characters.
	public final Iterable<TSymbol> ReadWhileLazy(Predicate<TSymbol> condition) {
		return new Iterable<TSymbol>() {

			@Override
			public Iterator<TSymbol> iterator() {
				// TODO Auto-generated method stub
				return new Iterator<TSymbol>() {

					@Override
					public boolean hasNext() {
						
						return EnsureCurrent() && condition.test(getCurrentSymbol());
					}

					@Override
					public TSymbol next() {
						
						TSymbol item = getCurrentSymbol();
						nextToken();
						return item;
					}

				};
			}

		};

	}

	private void Configure(SpanKind kind, AcceptedCharacters accepts) {
		if (kind != null) {
			getSpan().setKind(kind);
		}
		if (accepts != null) {
			getSpan().getEditHandler().setAcceptedCharacters(accepts);
		}
	}

	protected void outputSpanBeforeRazorComment() throws Exception {
		throw new InvalidOperationException(RazorResources.getLanguage_Does_Not_Support_RazorComment());
	}

	private void CommentSpanConfig(SpanBuilder span) {
		span.setCodeGenerator(SpanCodeGenerator.Null);
		span.setEditHandler(SpanEditHandler.createDefault(p -> getLanguage().tokenizeString(p)));
	}

	protected final void RazorComment() {
		if (!getLanguage().knowsSymbolType(KnownSymbolType.CommentStart)
				|| !getLanguage().knowsSymbolType(KnownSymbolType.CommentStar)
				|| !getLanguage().knowsSymbolType(KnownSymbolType.CommentBody)) {
			// throw new
			// InvalidOperationException(RazorResources.getLanguage_Does_Not_Support_RazorComment());
		}
		try {
			outputSpanBeforeRazorComment();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		try (AutoCloseable disposable = PushSpanConfig((a) -> CommentSpanConfig(a))){

			try(AutoCloseable disposablew = getContext().startBlock(BlockType.Comment)) {
				getContext().getCurrentBlock().setCodeGenerator(new RazorCommentCodeGenerator());
				SourceLocation start = getCurrentLocation().clone();

				Expected(KnownSymbolType.CommentStart);
				Output(SpanKind.Transition, AcceptedCharacters.None);

				Expected(KnownSymbolType.CommentStar);
				Output(SpanKind.MetaCode, AcceptedCharacters.None);

				Optional(KnownSymbolType.CommentBody);
				AddMarkerSymbolIfNecessary();
				Output(SpanKind.Comment);

				boolean errorReported = false;
				if (!Optional(KnownSymbolType.CommentStar)) {
					errorReported = true;
					getContext().OnError(start, RazorResources.getParseError_RazorComment_Not_Terminated());
				} else {
					Output(SpanKind.MetaCode, AcceptedCharacters.None);
				}

				if (!Optional(KnownSymbolType.CommentStart)) {
					if (!errorReported) {
						errorReported = true;
						getContext().OnError(start, RazorResources.getParseError_RazorComment_Not_Terminated());
					}
				} else {
					Output(SpanKind.Transition, AcceptedCharacters.None);
				}
			}catch (Exception ex){
				ex.printStackTrace();
			}
		} catch (Exception ex){
            ex.printStackTrace();
        }
		initialize(getSpan());
	}
}