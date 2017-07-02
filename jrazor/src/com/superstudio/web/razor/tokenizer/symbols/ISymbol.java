package com.superstudio.web.razor.tokenizer.symbols;

import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.web.razor.text.LocationTagged;
import com.superstudio.web.razor.text.SourceLocation;

import java.util.List;
import java.util.Optional;


public interface ISymbol {
	SourceLocation getStart();

	String getContent();

	void offsetStart(SourceLocation documentStart);

	void changeStart(SourceLocation newStart);

	static LocationTagged<String> getContent(List<? extends ISymbol> symbols, SourceLocation spanStart) {
		if (symbols != null && symbols.iterator().hasNext()) {

			//ISymbol first = CollectionHelper.firstOrDefault(symbols);
			ISymbol first=symbols.stream().findFirst().get();
				String contents=symbols.stream().collect(StringBuilder::new,
					(StringBuilder builder,ISymbol sym)->builder.append(sym.getContent()),StringBuilder::append).toString();
			SourceLocation firstStart=first.getStart();
			return new LocationTagged<String>(contents, SourceLocation.opAddition(spanStart, firstStart));
		} else {
			return new LocationTagged<String>("", spanStart);
		}
	}

default  LocationTagged<String> getLocationTaggedContent(){
		return  new LocationTagged<String>(getContent(),getStart().clone());
}

	//default LocationTagged<String> GetContent() {
		//return new LocationTagged<String>(getContent(), getStart().clone());
	//}
}