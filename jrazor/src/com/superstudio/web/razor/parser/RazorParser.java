package com.superstudio.web.razor.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.superstudio.commons.CancellationToken;
import com.superstudio.commons.SynchronizationContext;
import com.superstudio.commons.TextReader;
import com.superstudio.commons.csharpbridge.action.Action;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.web.razor.ParserResults;
import com.superstudio.web.razor.parser.syntaxTree.Block;
import com.superstudio.web.razor.parser.syntaxTree.RazorError;
import com.superstudio.web.razor.parser.syntaxTree.Span;
import com.superstudio.web.razor.text.ITextDocument;
import com.superstudio.web.razor.text.LookaheadTextReader;
import com.superstudio.web.razor.text.SeekableTextReader;


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

	public void Parse(TextReader input, ParserVisitor visitor) throws Exception {
		ParserResults results = ParseCore(new SeekableTextReader(input));

		// Replay the results on the visitor
		try {
			visitor.visit(results);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public ParserResults Parse(TextReader input) throws Exception {
		return ParseCore(new SeekableTextReader(input));
	}

	public ParserResults Parse(ITextDocument input) throws Exception {
		return ParseCore(input);
	}

	@Deprecated
	public void Parse(LookaheadTextReader input, ParserVisitor visitor) throws Exception {
		ParserResults results = ParseCore(new SeekableTextReader(input));

		// Replay the results on the visitor
		try {
			visitor.visit(results);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Deprecated
	public ParserResults Parse(LookaheadTextReader input) throws Exception {
		return ParseCore(new SeekableTextReader(input));
	}
	
	public Action CreateParseTask(TextReader input, Consumer<Span> spanCallback, Consumer<RazorError> errorCallback) {
		return CreateParseTask(input, new CallbackVisitor(spanCallback, errorCallback));
	}

	public Action CreateParseTask(TextReader input, Consumer<Span> spanCallback, Consumer<RazorError> errorCallback,
			SynchronizationContext context) {
		CallbackVisitor tempVar = new CallbackVisitor(spanCallback, errorCallback);
		tempVar.setSynchronizationContext(context);
		return CreateParseTask(input, tempVar);
	}

	public Action CreateParseTask(TextReader input, Consumer<Span> spanCallback, Consumer<RazorError> errorCallback,
			CancellationToken cancelToken) {
		CallbackVisitor tempVar = new CallbackVisitor(spanCallback, errorCallback);
		tempVar.setCancelToken(cancelToken);
		return CreateParseTask(input, tempVar);
	}

	public Action CreateParseTask(TextReader input, Consumer<Span> spanCallback, Consumer<RazorError> errorCallback,
			SynchronizationContext context, CancellationToken cancelToken) {
		CallbackVisitor tempVar = new CallbackVisitor(spanCallback, errorCallback);
		tempVar.setSynchronizationContext(context);
		tempVar.setCancelToken(cancelToken);
		return CreateParseTask(input, tempVar);
	}


		public Action CreateParseTask(TextReader input, ParserVisitor consumer) {

		// methods are not converted
		return new Action() {
			@Override
			public void execute() {
				try {
					Parse(input, consumer);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		};
	}

	private ParserResults ParseCore(ITextDocument input) throws Exception  {
		// Setup the parser context
		ParserContext tempVar = new ParserContext(input, getCodeParser(), getMarkupParser(), getMarkupParser());
		tempVar.setDesignTimeMode(getDesignTimeMode());
		ParserContext context = tempVar;

		getMarkupParser().setContext(context);
		getCodeParser().setContext(context);

		// Execute the parse
		//try {
			getMarkupParser().ParseDocument();
			//getCodeParser().ParseDocument();
	//	} catch (NotSupportedException e1) {
			// TODO Auto-generated catch block
		//	e1.printStackTrace();
		//} catch (InvalidOperationException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}

		// Get the result
		ParserResults results = null;
		try {
			results = context.CompleteParse();
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