package com.superstudio.web.razor.parser;

import com.superstudio.commons.CancellationToken;
import com.superstudio.commons.SynchronizationContext;
import com.superstudio.commons.TextReader;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.web.razor.ParserResults;
import com.superstudio.web.razor.parser.syntaxTree.Block;
import com.superstudio.web.razor.parser.syntaxTree.RazorError;
import com.superstudio.web.razor.parser.syntaxTree.Span;
import com.superstudio.web.razor.text.ITextDocument;
import com.superstudio.web.razor.text.LookaheadTextReader;
import com.superstudio.web.razor.text.SeekableTextReader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;


public class RazorParser {
	public RazorParser(ParserBase codeParser, ParserBase markupParser) throws Exception {
		if (codeParser == null) {
			// throw new ArgumentNullException("codeParser");
		}
		if (markupParser == null) {
			// throw new ArgumentNullException("markupParser");
		}

		setMarkupParser(markupParser);
		setCodeParser(codeParser);
		List<ISyntaxTreeRewriter> list = new ArrayList<ISyntaxTreeRewriter>();
		list.add(new WhiteSpaceRewriter((p, i, j) -> getMarkupParser().buildSpan(p, i, j)));

		list.add(new ConditionalAttributeCollapser((p, m, t) -> getMarkupParser().buildSpan(p, m, t)));

		setOptimizers(list);
		// Move whitespace from start of expression block to markup
		// Collapse conditional attributes where the entire value is literal
	}

	private ParserBase privateCodeParser;

	public final ParserBase getCodeParser() {
		return privateCodeParser;
	}

	public final void setCodeParser(ParserBase value) {
		privateCodeParser = value;
	}

	private ParserBase privateMarkupParser;

	public final ParserBase getMarkupParser() {
		return privateMarkupParser;
	}

	public final void setMarkupParser(ParserBase value) {
		privateMarkupParser = value;
	}

	private java.util.List<ISyntaxTreeRewriter> privateOptimizers;

	public final java.util.List<ISyntaxTreeRewriter> getOptimizers() {
		return privateOptimizers;
	}

	public final void setOptimizers(java.util.List<ISyntaxTreeRewriter> value) {
		privateOptimizers = value;
	}

	private boolean privateDesignTimeMode;

	public final boolean getDesignTimeMode() {
		return privateDesignTimeMode;
	}

	public final void setDesignTimeMode(boolean value) {
		privateDesignTimeMode = value;
	}

	public void parse(TextReader input, ParserVisitor visitor) throws Exception {
		ParserResults results = parseCore(new SeekableTextReader(input));

		// Replay the results on the visitor
		try {
			visitor.visit(results);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public ParserResults parse(TextReader input) throws Exception {
		return parseCore(new SeekableTextReader(input));
	}

	public ParserResults parse(ITextDocument input) throws Exception {
		return parseCore(input);
	}

	@Deprecated
	public void parse(LookaheadTextReader input, ParserVisitor visitor) throws Exception {
		ParserResults results = parseCore(new SeekableTextReader(input));

		// Replay the results on the visitor
		try {
			visitor.visit(results);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Deprecated
	public ParserResults parse(LookaheadTextReader input) throws Exception {
		return parseCore(new SeekableTextReader(input));
	}
	
	public Runnable createParseTask(TextReader input, Consumer<Span> spanCallback, Consumer<RazorError> errorCallback) {
		return createParseTask(input, new CallbackVisitor(spanCallback, errorCallback));
	}

	public Runnable createParseTask(TextReader input, Consumer<Span> spanCallback, Consumer<RazorError> errorCallback,
									SynchronizationContext context) {
		CallbackVisitor tempVar = new CallbackVisitor(spanCallback, errorCallback);
		tempVar.setSynchronizationContext(context);
		return createParseTask(input, tempVar);
	}

	public Runnable createParseTask(TextReader input, Consumer<Span> spanCallback, Consumer<RazorError> errorCallback,
									CancellationToken cancelToken) {
		CallbackVisitor tempVar = new CallbackVisitor(spanCallback, errorCallback);
		tempVar.setCancelToken(cancelToken);
		return createParseTask(input, tempVar);
	}

	public Runnable createParseTask(TextReader input, Consumer<Span> spanCallback, Consumer<RazorError> errorCallback,
									SynchronizationContext context, CancellationToken cancelToken) {
		CallbackVisitor tempVar = new CallbackVisitor(spanCallback, errorCallback);
		tempVar.setSynchronizationContext(context);
		tempVar.setCancelToken(cancelToken);
		return createParseTask(input, tempVar);
	}


		public Runnable createParseTask(TextReader input, ParserVisitor consumer) {

		// methods are not converted
			return () ->{
				try {
					parse(input, consumer);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			};


	}

	private ParserResults parseCore(ITextDocument input) throws Exception  {
		// Setup the parser context
		ParserContext tempVar = new ParserContext(input, getCodeParser(), getMarkupParser(), getMarkupParser());
		tempVar.setDesignTimeMode(getDesignTimeMode());
		ParserContext context = tempVar;

		getMarkupParser().setContext(context);
		getCodeParser().setContext(context);

		// Execute the parse

			getMarkupParser().parseDocument();

		ParserResults results = null;
		try {
			results = context.completeParse();
		} catch (InvalidOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// rewrite whitespace if supported
		Block current = results.getDocument();
		for (ISyntaxTreeRewriter rewriter : getOptimizers()) {
			current = rewriter.rewrite(current);
		}

		// Link the leaf nodes into a chain
		Span prev = null;
		for (Span node : current.Flatten()) {
			node.setPrevious(prev);
			if (prev != null) {
				prev.setNext(node);
			}
			prev = node;
		}

		// Return the new result
		return new ParserResults(current, results.getParserErrors());
	}
}