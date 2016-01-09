package com.superstudio.web.razor.parser.syntaxTree;

import com.superstudio.web.razor.editor.SpanEditHandler;
import com.superstudio.web.razor.generator.ISpanCodeGenerator;
import com.superstudio.web.razor.generator.SpanCodeGenerator;
import com.superstudio.web.razor.text.LocationTagged;
import com.superstudio.web.razor.text.SourceLocation;
import com.superstudio.web.razor.text.SourceLocationTracker;
import com.superstudio.web.razor.tokenizer.symbols.ISymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;



public class SpanBuilder
{
	private java.util.List<ISymbol> _symbols = new java.util.ArrayList<ISymbol>();
	private SourceLocationTracker _tracker = new SourceLocationTracker();

	public SpanBuilder(Span original)
	{
		setKind(original.getKind());
		_symbols = new java.util.ArrayList<ISymbol>();
		_symbols.addAll(original.getSymbols());
		setEditHandler(original.getEditHandler());
		setCodeGenerator(original.getCodeGenerator());
		setStart(original.getStart());
	}

	public SpanBuilder()
	{
		reset();
	}

	private SourceLocation privateStart;
	public final SourceLocation getStart()
	{
		return privateStart;
	}
	public final void setStart(SourceLocation value)
	{
		privateStart = value;
	}
	private SpanKind privateKind = SpanKind.forValue(0);
	public final SpanKind getKind()
	{
		return privateKind;
	}
	public final void setKind(SpanKind value)
	{
		privateKind = value;
	}

	public final List<ISymbol> getSymbols()
	{
		return new ArrayList<ISymbol>(_symbols);
	}

	private SpanEditHandler privateEditHandler;
	public final SpanEditHandler getEditHandler()
	{
		return privateEditHandler;
	}
	public final void setEditHandler(SpanEditHandler value)
	{
		privateEditHandler = value;
	}
	private ISpanCodeGenerator privateCodeGenerator;
	public final ISpanCodeGenerator getCodeGenerator()
	{
		return privateCodeGenerator;
	}
	public final void setCodeGenerator(ISpanCodeGenerator value)
	{
		privateCodeGenerator = value;
	}

	public final void reset()
	{
		_symbols = new java.util.ArrayList<ISymbol>();

		//setEditHandler(SpanEditHandler.createDefault(s -> Enumerable.<ISymbol>Empty()));
		setCodeGenerator(SpanCodeGenerator.Null);
		setStart(SourceLocation.Zero);
	}

	public final Span build()
	{
		return new Span(this);
	}

	public final void clearSymbols()
	{
		_symbols.clear();
	}

	// Short-cut method for adding a symbol
	public final void accept(ISymbol symbol)
	{
		if (symbol == null)
		{
			return;
		}

		if (_symbols.isEmpty())
		{
			setStart(symbol.getStart().clone());
			symbol.changeStart(SourceLocation.Zero);
			_tracker.setCurrentLocation(SourceLocation.Zero);
		}
		else
		{
			symbol.changeStart(_tracker.getCurrentLocation().clone());
		}

		_symbols.add(symbol);
		_tracker.updateLocation(symbol.getContent());
	}
	
	public  LocationTagged<String> getContent()
	{

		return getContent(e -> e);
	}

	public LocationTagged<String> getContent(
			Function<List<ISymbol>, List<ISymbol>> filter)
	{
		return ISymbol.getContent(filter.apply(getSymbols()), getStart().clone());
	}
	
	/*public static LocationTagged<String> getContent(SpanBuilder span,
			Function<List<ISymbol>, List<ISymbol>> filter)
	{
		return getContent(filter.apply(span.getSymbols()), span.getStart().clone());
	}*/


//ORIGINAL LINE: public static LocationTagged<string> getContent(this IEnumerable<ISymbol> symbols, SourceLocation spanStart)
	
}