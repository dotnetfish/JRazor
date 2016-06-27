package com.superstudio.template.language.mvc;


import com.superstudio.language.java.JavaRazorCodeGenerator;
import com.superstudio.language.java.parser.JavaCodeParser;
import com.superstudio.web.razor.WebPageRazorHost;
import com.superstudio.web.razor.generator.RazorCodeGenerator;
import com.superstudio.web.razor.parser.ParserBase;

public class MvcJavaWebPageRazorHost extends WebPageRazorHost {
	public MvcJavaWebPageRazorHost(String virtualPath, String physicalPath) {
		super(virtualPath, physicalPath);
		this.getNamespaceImports().add("com.superstudio.template.templatepages.WebTemplatePage");
		this.getNamespaceImports().add("com.superstudio.commons.Tuple");
		 //super.registerSpecialFile(RazorViewEngine.ViewStartFileName,
		 //ViewStartPage.class);
		// super.DefaultPageBaseClass = WebViewPage.class.getName();
		super.setDefaultPageBaseClass("WebTemplatePage");
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