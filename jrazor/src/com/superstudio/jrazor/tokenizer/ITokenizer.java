package com.superstudio.jrazor.tokenizer;

import com.superstudio.jrazor.tokenizer.symbols.ISymbol;




public interface ITokenizer
{
	ISymbol nextSymbol();
}