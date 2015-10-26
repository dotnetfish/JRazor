package com.superstudio.language.java.mvc;

import com.superstudio.jrazor.WebPageRazorHost;
import com.superstudio.jrazor.generator.RazorCodeGenerator;
import com.superstudio.jrazor.parser.*;
import com.superstudio.language.java.JavaRazorCodeGenerator;
import com.superstudio.language.java.parser.JavaCodeParser;

public class MvcJavaWebPageRazorHost extends WebPageRazorHost {
	public MvcJavaWebPageRazorHost(String virtualPath, String physicalPath) {
		super(virtualPath, physicalPath);
		this.getNamespaceImports().add("com.superstudio.web.webpages.WebViewPage");
		
		// super.RegisterSpecialFile(RazorViewEngine.ViewStartFileName,
		// ViewStartPage.class);
		// super.DefaultPageBaseClass = WebViewPage.class.getName();
		super.setDefaultPageBaseClass("WebViewPage");
		//this.GetRidOfNamespace("System.Web.Mvc.HtmlHelper");
	}

	// @Override
	public RazorCodeGenerator decorateCodeGenerator(RazorCodeGenerator incomingCodeGenerator) throws Exception {
		if (incomingCodeGenerator instanceof JavaRazorCodeGenerator) {
			return new MvcJavaRazorCodeGenerator(incomingCodeGenerator.getClassName(),
					incomingCodeGenerator.getRootNamespaceName(), incomingCodeGenerator.getSourceFileName(),
					incomingCodeGenerator.getHost());
		}
		return super.decorateCodeGenerator(incomingCodeGenerator);
	}

	public ParserBase decorateCodeParser(ParserBase incomingCodeParser) throws Exception {
		if (incomingCodeParser instanceof JavaCodeParser) {
			return new MvcJavaRazorCodeParser();
		}
		
		return super.decorateCodeParser(incomingCodeParser);
	}

	private void getRidOfNamespace(String ns) {
		if (this.getNamespaceImports().contains(ns)) {
			this.getNamespaceImports().remove(ns);
		}
	}
}