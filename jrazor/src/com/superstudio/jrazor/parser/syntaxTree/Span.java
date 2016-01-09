package com.superstudio.jrazor.parser.syntaxTree;

import java.util.List;

import com.superstudio.commons.csharpbridge.action.ActionOne;
import com.superstudio.commons.exception.OperationCanceledException;
import com.superstudio.jrazor.editor.*;
import com.superstudio.jrazor.generator.*;
import com.superstudio.jrazor.parser.ParserVisitor;
import com.superstudio.jrazor.text.SourceLocation;
import com.superstudio.jrazor.text.SourceLocationTracker;
import com.superstudio.jrazor.tokenizer.symbols.*;
import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.GroupCollection;
import com.superstudio.commons.HashCodeCombiner;
import com.superstudio.commons.csharpbridge.StringHelper;



public class Span extends SyntaxTreeNode {
	private SourceLocation _start;

	public Span(SpanBuilder builder) {
		replaceWith(builder);
	}

	private SpanKind privateKind = SpanKind.forValue(0);

	public final SpanKind getKind() {
		return privateKind;
	}

	protected final void setKind(SpanKind value) {
		privateKind = value;
	}

	private List<ISymbol> privateSymbols;

	public final List<ISymbol> getSymbols() {
		return privateSymbols;
	}

	protected final void setSymbols(List<ISymbol> value) {
		privateSymbols = value;
	}

	// Allow test code to re-link spans
	private Span privatePrevious;

	public final Span getPrevious() {
		return privatePrevious;
	}

	  public void setPrevious(Span value) {
		privatePrevious = value;
	}

	private Span privateNext;

	public final Span getNext() {
		return privateNext;
	}

	public final void setNext(Span value) {
		privateNext = value;
	}

	private SpanEditHandler privateEditHandler;

	public final SpanEditHandler getEditHandler() {
		return privateEditHandler;
	}

	protected final void setEditHandler(SpanEditHandler value) {
		privateEditHandler = value;
	}

	private ISpanCodeGenerator privateCodeGenerator;

	public final ISpanCodeGenerator getCodeGenerator() {
		return privateCodeGenerator;
	}

	protected final void setCodeGenerator(ISpanCodeGenerator value) {
		privateCodeGenerator = value;
	}

	@Override
	public boolean getIsBlock() {
		return false;
	}

	@Override
	public int getLength() {
		return getContent().length();
	}

	@Override
	public SourceLocation getStart() {
		return _start;
	}

	private String privateContent;

	public final String getContent() {
		return privateContent;
	}

	private void setContent(String value) {
		privateContent = value;
	}

	public final void change(ActionOne<SpanBuilder> changes) {
		SpanBuilder builder = new SpanBuilder(this);
		changes.execute(builder);
		replaceWith(builder);
	}

	public final void replaceWith(SpanBuilder builder) {
		 
		 
		// assert !builder.getSymbols().Any() || builder.getSymbols().All(s -> s
		// != null);

		setKind(builder.getKind());
		setSymbols(builder.getSymbols());
		setEditHandler(builder.getEditHandler());
		ISpanCodeGenerator tempVar = builder.getCodeGenerator();
		setCodeGenerator((tempVar != null) ? tempVar : SpanCodeGenerator.Null);
		_start = builder.getStart().clone();

		// Since we took references to the values in SpanBuilder, clear its
		// references out
		builder.reset();

		// Calculate other properties
		 
		 
		setContent(CollectionHelper.aggregate(getSymbols(), new StringBuilder(),
				(sb, sym) -> sb.append(sym.getContent()), sb -> sb.toString()));
	}

	/**
	 * Accepts the specified visitor
	 * 
	 * 
	 * Calls the VisitSpan method on the specified visitor, passing in this
	 * 
	 * @throws OperationCanceledException
	 * 
	 */
	@Override
	public void accept(ParserVisitor visitor) throws OperationCanceledException {
		try {
			visitor.visitSpan(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getKind());
		builder.append(String.format(" Span at %s::%s - [%s]", getStart(), getLength(), getContent()));
		builder.append(" Edit: <");
		builder.append(getEditHandler().toString());
		builder.append(">");
		builder.append(" Gen: <");
		builder.append(getCodeGenerator().toString());
		builder.append("> {");
		 
		 

		List<GroupCollection<Class<?>, ISymbol>> groups = CollectionHelper.groupBy(getSymbols(),
				sym -> sym.getClass());
		List<String> symString = CollectionHelper.select(groups,
				grp -> grp.getKey().getName().concat(":").concat(String.valueOf(grp.getItems().size())));
		//String.concat()
		//"".concat（":"）.concat(String.valueOf(23));
		if(symString.size()>0){
			builder.append(StringHelper.join(";", symString.toArray(new String[symString.size()])));
		}
		
		builder.append("}");
		return builder.toString();
	}

	public final void changeStart(SourceLocation newStart) {
		_start = newStart;
		Span current = this;
		SourceLocationTracker tracker = new SourceLocationTracker(newStart);
		tracker.UpdateLocation(getContent());
		while ((current = current.getNext()) != null) {
			current._start = tracker.getCurrentLocation().clone();
			tracker.UpdateLocation(current.getContent());
		}
	}

	public final void setStart(SourceLocation newStart) {
		_start = newStart;
	}

	/**
	 * Checks that the specified span is equivalent to the other in that it has
	 * the same start point and content.
	 * 
	 */
	@Override
	public boolean equivalentTo(SyntaxTreeNode node) {
		Span other = (Span) ((node instanceof Span) ? node : null);
		return other != null && getKind().equals(other.getKind()) && getStart().equals(other.getStart())
				&& getEditHandler().equals(other.getEditHandler()) && other.getContent().equalsIgnoreCase(getContent());
	}

	@Override
	public boolean equals(Object obj) {
		Span other = (Span) ((obj instanceof Span) ? obj : null);
		return other != null && getKind().equals(other.getKind()) && getEditHandler().equals(other.getEditHandler())
				&& getCodeGenerator().equals(other.getCodeGenerator())
				&& 
				CollectionHelper.sequeceEqual(getSymbols(),other.getSymbols());
				//getSymbols().SequenceEqual(other.getSymbols());
	}

	@Override
	public int hashCode() {
		return HashCodeCombiner.Start().Add(new Integer(getKind().getValue())).Add(getStart().clone()).Add(getContent())
				.getCombinedHash();
	}
}