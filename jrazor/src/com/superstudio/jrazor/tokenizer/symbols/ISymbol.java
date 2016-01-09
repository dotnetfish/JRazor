package com.superstudio.jrazor.tokenizer.symbols;

import java.util.List;

import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.jrazor.text.LocationTagged;
import com.superstudio.jrazor.text.SourceLocation;



public interface ISymbol {
	SourceLocation getStart();

	String getContent();

	void offsetStart(SourceLocation documentStart);

	void changeStart(SourceLocation newStart);

	static LocationTagged<String> getContent(List<? extends ISymbol> symbols, SourceLocation spanStart) {
		if (symbols != null && symbols.iterator().hasNext()) {
			// Trace.WriteLine("here we got "+s.getContent());
			 
			 
			ISymbol first = CollectionHelper.firstOrDefault(symbols);
			List<String> symbolContents = CollectionHelper.select(symbols, s -> s.getContent());
			String contents = StringHelper.concat(symbolContents);
			
			SourceLocation firstStart=null;
			if(first!=null){
				firstStart=first.getStart();
			}
			//firstStart=first.getStart();
			return new LocationTagged<String>(contents, SourceLocation.OpAddition(spanStart, firstStart));
		} else {
			return new LocationTagged<String>("", spanStart);
		}
	}

	 

	default LocationTagged<String> GetContent() {
		return new LocationTagged<String>(getContent(), getStart().clone());
	}
}