package com.superstudio.jrazor.tokenizer;

import com.superstudio.jrazor.text.*;
import com.superstudio.jrazor.tokenizer.symbols.*;


 
//[SuppressMessage("Microsoft.Design", "CA1005:AvoidExcessiveParametersOnGenericTypes", Justification = "All generic parameters are required")]
public class TokenizerView<TTokenizer extends Tokenizer<TSymbol , TSymbolType>, 
TSymbol extends SymbolBase<TSymbolType>, TSymbolType>
{
	public TokenizerView(TTokenizer tokenizer)
	{
		setTokenizer(tokenizer);
	}

	private TTokenizer privateTokenizer;
	public final TTokenizer getTokenizer()
	{
		return privateTokenizer;
	}
	private void setTokenizer(TTokenizer value)
	{
		privateTokenizer = value;
	}
	private boolean privateEndOfFile;
	public final boolean getEndOfFile()
	{
		return privateEndOfFile;
	}
	private void setEndOfFile(boolean value)
	{
		privateEndOfFile = value;
	}
	private TSymbol privateCurrent;
	public final TSymbol getCurrent()
	{
		return privateCurrent;
	}
	private void setCurrent(TSymbol value)
	{
		privateCurrent = value;
	}

	public final ITextDocument getSource()
	{
		return getTokenizer().getSource();
	}

	public final boolean next()
	{
		setCurrent(getTokenizer().nextSymbol());
		setEndOfFile((getCurrent() == null));
		return !getEndOfFile();
	}

	public final void putBack(TSymbol symbol)
	{
		assert getSource().getPosition() == symbol.getStart().getAbsoluteIndex() + symbol.getContent().length();
		if (getSource().getPosition() != symbol.getStart().getAbsoluteIndex() + symbol.getContent().length())
		{
			// We've already passed this symbol
			
			//throw new InvalidOperationException(String.format( RazorResources.getTokenizerView_CannotPutBack(), symbol.getStart().getAbsoluteIndex() + symbol.getContent().length(), getSource().getPosition()));
		}
		getSource().setPosition(getSource().getPosition() - symbol.getContent().length());
		setCurrent(null);
		setEndOfFile(getSource().getPosition() >= getSource().getLength());
		getTokenizer().reset();
	}
}