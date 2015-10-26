package com.superstudio.jrazor.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.IDisposable;
import com.superstudio.commons.IEquatable;
import com.superstudio.commons.Tuple;
import com.superstudio.commons.csharpbridge.action.ActionTwo;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.jrazor.editor.SpanEditHandler;
import com.superstudio.jrazor.generator.RazorCommentCodeGenerator;
import com.superstudio.jrazor.generator.SpanCodeGenerator;
import com.superstudio.jrazor.parser.syntaxTree.AcceptedCharacters;
import com.superstudio.jrazor.parser.syntaxTree.BlockType;
import com.superstudio.jrazor.parser.syntaxTree.RazorError;
import com.superstudio.jrazor.parser.syntaxTree.SpanBuilder;
import com.superstudio.jrazor.parser.syntaxTree.SpanKind;
import com.superstudio.jrazor.resources.RazorResources;
import com.superstudio.jrazor.text.SourceLocation;
import com.superstudio.jrazor.tokenizer.Tokenizer;
import com.superstudio.jrazor.tokenizer.TokenizerView;
import com.superstudio.jrazor.tokenizer.symbols.ISymbol;
import com.superstudio.jrazor.tokenizer.symbols.KnownSymbolType;
import com.superstudio.jrazor.tokenizer.symbols.SymbolBase;
import com.superstudio.jrazor.utils.DisposableAction;

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
		acceptAndMoveNext();
		if (getEndOfFile() && !mode.hasFlag(BalancingModes.NoErrorOnFailure)) {
			getContext().onError(start, RazorResources.getParseError_Expected_CloseBracket_Before_EOF(),
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
				if (isAtEmbeddedTransition(mode.hasFlag(BalancingModes.AllowCommentsAndTemplates),
						mode.hasFlag(BalancingModes.AllowEmbeddedTransitions))) {
					accept(syms);
					syms.clear();
					handleEmbeddedTransition();

					// Reset backtracking since we've already outputted some
					// spans.
					startPosition = getCurrentLocation().getAbsoluteIndex();
				}
				if (at(left)) {
					nesting++;
				} else if (at(right)) {
					nesting--;
				}
				if (nesting > 0) {
					syms.add(getCurrentSymbol());
				}
			} while (nesting > 0 && nextToken());

			if (nesting > 0) {
				if (!mode.hasFlag(BalancingModes.NoErrorOnFailure)) {
					getContext().onError(start, RazorResources.getParseError_Expected_CloseBracket_Before_EOF(),
							getLanguage().getSample(left), getLanguage().getSample(right));
				}
				if (mode.hasFlag(BalancingModes.BacktrackOnFailure)) {
					getContext().getSource().setPosition(startPosition);
					nextToken();
				} else {
					accept(syms);
				}
			} else {
				// Accept all the symbols we saw
				accept(syms);
			}
		}
		return nesting == 0;
	}

	protected final boolean nextIs(TSymbolType type) {
			return nextIs(sym -> sym != null && type==sym.getType());
	}

	protected final boolean nextIs(TSymbolType... types) {
		return nextIs(sym -> sym != null && CollectionHelper.any(Arrays.asList(types), t -> t==sym.getType()));
	}

	protected final boolean nextIs(Function<TSymbol, Boolean> condition) {
		TSymbol cur = getCurrentSymbol();
		nextToken();
		boolean result = condition.apply(getCurrentSymbol());
		putCurrentBack();
		putBack(cur);
		ensureCurrent();
		return result;
	}

	protected final boolean was(TSymbolType type) {
		boolean result = getPreviousSymbol().getType() == type;
		// boolean result2 = equals(getPreviousSymbol().getType(), type);

		return getPreviousSymbol() != null && result;
	}

	protected final boolean at(TSymbolType type) {

		return !getEndOfFile() && getCurrentSymbol() != null && getCurrentSymbol().getType() == type;
	}

	protected final boolean acceptAndMoveNext() {
		accept(getCurrentSymbol());
		return nextToken();
	}

	protected final TSymbol acceptSingleWhiteSpaceCharacter() {
		if (getLanguage().isWhiteSpace(getCurrentSymbol())) {
			Tuple<TSymbol, TSymbol> pair = getLanguage().splitSymbol(getCurrentSymbol(), 1,
					getLanguage().getKnownSymbolType(KnownSymbolType.WhiteSpace));
			accept(pair.getItem1());
			getSpan().getEditHandler().setAcceptedCharacters(AcceptedCharacters.None);
			nextToken();
			return pair.getItem2();
		}
		return null;
	}

	protected final void accept(Iterable<TSymbol> symbols) {
		/*for (TSymbol symbol : symbols) {
			Accept(symbol);
		}*/
		Iterator<TSymbol> symbolsIt=symbols.iterator();
		while(symbolsIt.hasNext()){
			accept(symbolsIt.next());
		}
		//for(TSymbol)
	}

	protected final void accept(TSymbol symbol) {
		if (symbol != null) {

			if (symbol.getErrors() != null) {
				for (RazorError error : symbol.getErrors()) {
					getContext().getErrors().add(error);
				}
			}

			getSpan().accept(symbol);
		}
	}

	protected final boolean acceptAll(TSymbolType... types) {
		for (TSymbolType type : types) {
			if (getCurrentSymbol() == null || !equals(getCurrentSymbol().getType(), type)) {
				return false;
			}
			acceptAndMoveNext();
		}
		return true;
	}

	protected final void addMarkerSymbolIfNecessary() {
		addMarkerSymbolIfNecessary(getCurrentLocation().clone());
	}

	protected final void addMarkerSymbolIfNecessary(SourceLocation location) {
		if (getSpan().getSymbols().size() == 0 && getContext().getLastAcceptedCharacters() != AcceptedCharacters.Any) {
			accept(getLanguage().createMarkerSymbol(location));
		}
	}

	protected final void output(SpanKind kind) {
		configure(kind, null);
		output();
	}

	protected final void output(SpanKind kind, AcceptedCharacters accepts) {
		configure(kind, accepts);
		output();
	}

	protected final void output(AcceptedCharacters accepts) {
		configure(null, accepts);
		output();
	}

	private void output() {
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

	protected final IDisposable pushSpanConfig() {
		return pushSpanConfig((ActionTwo<SpanBuilder, Consumer<SpanBuilder>>) null);
	}

	protected final IDisposable pushSpanConfig(Consumer<SpanBuilder> newConfig) {
			return pushSpanConfig(newConfig == null ? (ActionTwo<SpanBuilder, Consumer<SpanBuilder>>) null
				: (span, t) -> newConfig.accept(span));
	}

	protected final IDisposable pushSpanConfig(ActionTwo<SpanBuilder, Consumer<SpanBuilder>> newConfig) {
		/*
		 * Consumer<SpanBuilder> old = getSpanConfig();
		 * ConfigureSpan(newConfig); 
		 *  return new DisposableAction(() ->
		 * getSpanConfig.apply());
		 */
		Consumer<SpanBuilder> old = getSpanConfig();
		configureSpan(newConfig);
		return new DisposableAction(() -> setSpanConfig(old));
	}

	protected final void configureSpan(Consumer<SpanBuilder> config) {
		setSpanConfig(config);
		initialize(getSpan());
	}


	protected final void configureSpan(ActionTwo<SpanBuilder, Consumer<SpanBuilder>> config) {
		Consumer<SpanBuilder> prev = getSpanConfig();
		if (config == null) {
			setSpanConfig(null);
		} else {
				setSpanConfig(span -> config.execute(span, prev));
		}
		initialize(getSpan());
	}

	protected final void expected(KnownSymbolType type) {
		expected(getLanguage().getKnownSymbolType(type));
	}

	 
	 
		protected final void expected(TSymbolType... types) {
		assert !getEndOfFile() && getCurrentSymbol() != null
				&& Arrays.asList(types).contains(getCurrentSymbol().getType());
		acceptAndMoveNext();
	}

	protected final boolean optional(KnownSymbolType type) {
		return optional(getLanguage().getKnownSymbolType(type));
	}

	protected final boolean optional(TSymbolType type) {
		if (at(type)) {
			acceptAndMoveNext();
			return true;
		}
		return false;
	}

	protected final boolean required(TSymbolType expected, boolean errorIfNotFound, String errorBase) {
		boolean found = at(expected);
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

			getContext().onError(getCurrentLocation().clone(), errorBase, error);
		}
		return found;
	}

	protected final boolean ensureCurrent() {
		if (getCurrentSymbol() == null) {
			return nextToken();
		}
		return true;
	}

	protected final void acceptWhile(TSymbolType type) {
	
		acceptWhile(sym -> type==sym.getType());
	}

	// We want to avoid array allocations and enumeration where possible, so we
	// use the same technique as String.Format
	protected final void acceptWhile(TSymbolType type1, TSymbolType type2) {

		acceptWhile(sym -> equals(type1, sym.getType()) || equals(type2, sym.getType()));
	}

	protected final void acceptWhile(TSymbolType type1, TSymbolType type2, TSymbolType type3) {
			acceptWhile(
				sym -> type1==sym.getType() || type2==sym.getType() || type3==sym.getType());
	}

	protected final void acceptWhile(TSymbolType... types) {
		
		// AcceptWhile(sym -> CollectionHelper.any(Arrays.asList(types),
		// (expected -> equals(expected, sym.getType()))));
		
		acceptWhile(sym -> CollectionHelper.any(Arrays.asList(types), (expected -> expected == sym.getType())));
	}

	protected final void acceptUntil(TSymbolType type) {
		 
		 
		// AcceptWhile(sym -> !equals(type, sym.getType()));
		acceptWhile(sym -> type != sym.getType());
	}

	// We want to avoid array allocations and enumeration where possible, so we
	// use the same technique as String.Format
	protected final void acceptUntil(TSymbolType type1, TSymbolType type2) {
		 
		 
		// AcceptWhile(sym -> !equals(type1, sym.getType()) && !equals(type2,
		// sym.getType()));
		acceptWhile(sym -> type1 != sym.getType() && type2 != sym.getType());
	}

	protected final void acceptUntil(TSymbolType type1, TSymbolType type2, TSymbolType type3) {
		 
		 
		acceptWhile(
				sym -> type1!=sym.getType()
				&& type2!=sym.getType()
				&& type3!=sym.getType());
	}

	protected final void acceptUntil(TSymbolType... types) {
		 
		 
		//AcceptWhile(sym -> CollectionHelper.all(Arrays.asList(types),
		//		expected -> !equals(expected, sym.getType())));
		
		
		acceptWhile(sym -> CollectionHelper.all(Arrays.asList(types),
						expected -> expected!= sym.getType()));
	}

	protected final void acceptWhile(Predicate<TSymbol> condition) {
		accept(readWhileLazy(condition));
	}

	protected final List<TSymbol> readWhile(Predicate<TSymbol> condition) {
		//original code ReadWhileLazy(condition).ToList();
		//it does not lazy read .....
		Iterable<TSymbol> result=readWhileLazy(condition);
		List<TSymbol> symList=new ArrayList<TSymbol>();
		for(TSymbol sym:result){
			symList.add(sym);
		}
		return symList;
		//return ReadWhileLazy(condition);
	}

	protected final TSymbol acceptWhiteSpaceInLines() {
		TSymbol lastWs = null;
		while (getLanguage().isWhiteSpace(getCurrentSymbol()) || getLanguage().isNewLine(getCurrentSymbol())) {
			// Capture the previous whitespace node
			if (lastWs != null) {
				accept(lastWs);
			}

			if (getLanguage().isWhiteSpace(getCurrentSymbol())) {
				lastWs = getCurrentSymbol();
			} else if (getLanguage().isNewLine(getCurrentSymbol())) {
				// Accept newline and reset last whitespace tracker
				accept(getCurrentSymbol());
				lastWs = null;
			}

			getTokenizer().next();
		}
		return lastWs;
	}

	protected final boolean atIdentifier(boolean allowKeywords) {
		return getCurrentSymbol() != null && (getLanguage().isIdentifier(getCurrentSymbol())
				|| (allowKeywords && getLanguage().isKeyword(getCurrentSymbol())));
	}

	// Don't open this to sub classes because it's lazy but it looks eager.
	// You have to advance the Enumerable to read the next characters.
	public final Iterable<TSymbol> readWhileLazy(Predicate<TSymbol> condition) {
		return new Iterable<TSymbol>() {

			@Override
			public Iterator<TSymbol> iterator() {
				// TODO Auto-generated method stub
				return new Iterator<TSymbol>() {

					@Override
					public boolean hasNext() {
						
						return ensureCurrent() && condition.test(getCurrentSymbol());
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

	private void configure(SpanKind kind, AcceptedCharacters accepts) {
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

	private void commentSpanConfig(SpanBuilder span) {
		span.setCodeGenerator(SpanCodeGenerator.Null);
		span.setEditHandler(SpanEditHandler.createDefault(p -> getLanguage().tokenizeString(p)));
	}

	protected final void razorComment() {
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
		 
		 
		// using (PushSpanConfig(CommentSpanConfig))
		IDisposable disposable = pushSpanConfig((a) -> commentSpanConfig(a));
		try {
			 
			 
			// using (Context.StartBlock(BlockType.getComment()))
			IDisposable disposablew = getContext().StartBlock(BlockType.Comment);
			try {
				getContext().getCurrentBlock().setCodeGenerator(new RazorCommentCodeGenerator());
				SourceLocation start = getCurrentLocation().clone();

				expected(KnownSymbolType.CommentStart);
				output(SpanKind.Transition, AcceptedCharacters.None);

				expected(KnownSymbolType.CommentStar);
				output(SpanKind.MetaCode, AcceptedCharacters.None);

				optional(KnownSymbolType.CommentBody);
				addMarkerSymbolIfNecessary();
				output(SpanKind.Comment);

				boolean errorReported = false;
				if (!optional(KnownSymbolType.CommentStar)) {
					errorReported = true;
					getContext().onError(start, RazorResources.getParseError_RazorComment_Not_Terminated());
				} else {
					output(SpanKind.MetaCode, AcceptedCharacters.None);
				}

				if (!optional(KnownSymbolType.CommentStart)) {
					if (!errorReported) {
						errorReported = true;
						getContext().onError(start, RazorResources.getParseError_RazorComment_Not_Terminated());
					}
				} else {
					output(SpanKind.Transition, AcceptedCharacters.None);
				}
			} finally {
				disposablew.dispose();
			}
		} finally {
			disposable.dispose();
		}
		initialize(getSpan());
	}
}