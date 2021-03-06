﻿package com.superstudio.jrazor.parser;

import java.util.Iterator;

import com.superstudio.commons.IEquatable;
import com.superstudio.commons.Tuple;
import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.jrazor.parser.syntaxTree.RazorError;
import com.superstudio.jrazor.text.ITextDocument;
import com.superstudio.jrazor.text.SeekableTextReader;
import com.superstudio.jrazor.text.SourceLocation;
import com.superstudio.jrazor.text.SourceLocationTracker;
import com.superstudio.jrazor.tokenizer.Tokenizer;
import com.superstudio.jrazor.tokenizer.symbols.KnownSymbolType;
import com.superstudio.jrazor.tokenizer.symbols.SymbolBase;



 
//[SuppressMessage("Microsoft.Design", "CA1005:AvoidExcessiveParametersOnGenericTypes", Justification = "All generic type parameters are required")]
public abstract class LanguageCharacteristics<TTokenizer extends Tokenizer<TSymbol, TSymbolType>, TSymbol
extends SymbolBase<TSymbolType> , TSymbolType> 
implements IEquatable<Object>

{
	public abstract String getSample(TSymbolType type);

	public abstract TTokenizer createTokenizer(ITextDocument source);

	public abstract TSymbolType flipBracket(TSymbolType bracket);

	public abstract TSymbol createMarkerSymbol(SourceLocation location);

	public Iterable<TSymbol> tokenizeString(String content) {
		return tokenizeString(SourceLocation.Zero, content);
	}

	public Iterable<TSymbol> tokenizeString(SourceLocation start, String input)
	{
		SeekableTextReader reader = new SeekableTextReader(input);
		TTokenizer tok = createTokenizer(reader);
		//TSymbol sym;
		final RefObject<TSymbol> refObj=new RefObject<TSymbol>(null);
		return new Iterable<TSymbol>(){

			@Override
			public Iterator<TSymbol> iterator() {
				// TODO Auto-generated method stub
				//private TSymbol current=null;
				return new Iterator<TSymbol>(){

					@Override
					public boolean hasNext() {
						// TODO Auto-generated method stub
						TSymbol current = (TSymbol) tok.nextSymbol();
						refObj.setRefObj(current);
						if(current==null){
							reader.close();
						}
						return current != null;
					}

					@Override
					public TSymbol next() {
						// TODO Auto-generated method stub
						TSymbol current=refObj.getRefObj();
						current.offsetStart(start);
						return current;
					}
					
				};
			}
			
		};
		
 
//		using (SeekableTextReader reader = new SeekableTextReader(input))
		/*
		try
		{
			
			while ((sym = (TSymbol) tok.NextSymbol()) != null)
			{
				sym.OffsetStart(start);
//C# TO JAVA CONVERTER TODO TASK: Java does not have an equivalent to the C# 'yield' keyword:
				//yield return ]]t ;
			}
		}
		finally
		{
			reader.close();
			//reader.dispose();
		}*/
	}

	public boolean isWhiteSpace(TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.WhiteSpace);
	}

	public boolean isNewLine(TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.NewLine);
	}

	public boolean isIdentifier(TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.Identifier);
	}

	public boolean isKeyword(TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.Keyword);
	}

	public boolean isTransition(TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.Transition);
	}

	public boolean isCommentStart(TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.CommentStart);
	}

	public boolean isCommentStar(TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.CommentStar);
	}

	public boolean isCommentBody(TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.CommentBody);
	}

	public boolean isUnknown(TSymbol symbol) {
		return isKnownSymbolType(symbol, KnownSymbolType.Unknown);
	}

	public boolean isKnownSymbolType(TSymbol symbol, KnownSymbolType type) {
		return symbol != null && equals(symbol.getType(), getKnownSymbolType(type));
	}

	public Tuple<TSymbol, TSymbol> splitSymbol(TSymbol symbol, int splitAt, TSymbolType leftType) {
		TSymbol left = createSymbol(symbol.getStart(), symbol.getContent().substring(0, splitAt), leftType, null);
		TSymbol right = null;
		if (splitAt < symbol.getContent().length()) {
			right = createSymbol(SourceLocationTracker.CalculateNewLocation(symbol.getStart(), left.getContent()),
					symbol.getContent().substring(splitAt), symbol.getType(), symbol.getErrors());
		}
		return Tuple.Create(left, right);
	}

	public abstract TSymbolType getKnownSymbolType(KnownSymbolType type);

	public boolean knowsSymbolType(KnownSymbolType type) {
		return type == KnownSymbolType.Unknown
				|| !equals(getKnownSymbolType(type), getKnownSymbolType(KnownSymbolType.Unknown));
	}
	
	

	protected abstract TSymbol createSymbol(SourceLocation location, String content, TSymbolType type,
			Iterable<RazorError> errors);
}