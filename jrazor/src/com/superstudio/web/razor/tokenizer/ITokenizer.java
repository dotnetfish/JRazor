package com.superstudio.web.razor.tokenizer;

import com.superstudio.web.razor.tokenizer.symbols.ISymbol;



public interface ITokenizer
{
	ISymbol nextSymbol();
}