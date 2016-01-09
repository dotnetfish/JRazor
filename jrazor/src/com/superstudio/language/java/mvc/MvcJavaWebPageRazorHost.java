package com.superstudio.language.java.mvc;

import com.superstudio.web.razor.WebPageRazorHost;
import com.superstudio.web.razor.generator.CSharpRazorCodeGenerator;
import com.superstudio.web.razor.generator.RazorCodeGenerator;
import com.superstudio.web.razor.parser.*;

public class MvcJavaWebPageRazorHost extends WebPageRazorHost {
	public MvcJavaWebPageRazorHost(String virtualPath, String physicalPath) {
		super(virtualPath, physicalPath);
		this.getNamespaceImports().add("com.superstudio.web.templatepages.WebViewPage");
		
		// super.registerSpecialFile(RazorViewEngine.ViewStartFileName,
		// ViewStartPage.class);
		// super.DefaultPageBaseClass = WebViewPage.class.getName();
		super.setDefaultPageBaseClass("WebViewPage");
		//this.getRidOfNamespace("com.superstudio.mvc.HtmlHelper");
	}

	 @Override
	public RazorCodeGenerator decorateCodeGenerator(RazorCodeGenerator incomingCodeGenerator) throws Exception {
		if (incomingCodeGenerator instanceof CSharpRazorCodeGenerator) {
			return new MvcJavaRazorCodeGenerator(incomingCodeGenerator.getClassName(),
					incomingCodeGenerator.getRootNamespaceName(), incomingCodeGenerator.getSourceFileName(),
					incomingCodeGenerator.getHost());
		}
		return super.decorateCodeGenerator(incomingCodeGenerator);
	}

	public ParserBase decorateCodeParser(ParserBase incomingCodeParser) throws Exception {
	/*	if (incomingCodeParser instanceof CSharpCodeParser) {
			return new MvcJavaRazorCodeParser();
		}*/

		return super.decorateCodeParser(incomingCodeParser);
	}

	private void getRidOfNamespace(String ns) {
		if (this.getNamespaceImports().contains(ns)) {
			this.getNamespaceImports().remove(ns);
		}
	}
}