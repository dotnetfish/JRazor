package com.superstudio.web.razor.tokenizer.symbols;

import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.web.razor.text.LocationTagged;
import com.superstudio.web.razor.text.SourceLocation;

import java.util.List;


public interface ISymbol {
	SourceLocation getStart();

	String getContent();

	void offsetStart(SourceLocation documentStart);

	void changeStart(SourceLocation newStart);

	static LocationTagged<String> getContent(List<? extends ISymbol> symbols, SourceLocation spanStart) {
		if (symbols != null && symbols.iterator().hasNext()) {

			ISymbol first = CollectionHelper.firstOrDefault(symbols);
			List<String> symbolContents = CollectionHelper.select(symbols, s -> s.getContent());
			String contents = StringHelper.concat(symbolContents);
			
			SourceLocation firstStart=null;
		/*	if(first==null){
				firstStart=first.getStart();
			}*/
			firstStart=first.getStart();
			return new LocationTagged<String>(contents, SourceLocation.opAddition(spanStart, firstStart));
		} else {
			return new LocationTagged<String>("", spanStart);
		}
	}



	default LocationTagged<String> GetContent() {
		return new LocationTagged<String>(getContent(), getStart().clone());
	}
}