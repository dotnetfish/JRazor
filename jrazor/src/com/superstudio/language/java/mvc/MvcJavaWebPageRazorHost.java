package com.superstudio.language.java.mvc;

import com.superstudio.language.java.JavaRazorCodeGenerator;
import com.superstudio.web.razor.WebPageRazorHost;
import com.superstudio.web.razor.generator.RazorCodeGenerator;
import com.superstudio.web.razor.parser.ParserBase;

public class MvcJavaWebPageRazorHost extends WebPageRazorHost {
	public MvcJavaWebPageRazorHost(String virtualPath, String physicalPath) {
		super(virtualPath, physicalPath);
		//this.getn
		this.getNamespaceImports().add("com.superstudio.web.templatepages.WebTemplatePage");
		
		//super.registerSpecialFile(RazorTemplateEngine.ViewStartFileName,
		// TemplateStartPage.class);
		//super.DefaultPageBaseClass = WebTemplatePage.class.getName();
		super.setDefaultPageBaseClass("WebViewPage");
		this.getRidOfNamespace("com.superstudio.mvc.HtmlHelper");
	}

	 @Override
	public RazorCodeGenerator decorateCodeGenerator(RazorCodeGenerator incomingCodeGenerator) throws Exception {
		if (incomingCodeGenerator instanceof JavaRazorCodeGenerator) {
			return new MvcJavaRazorCodeGenerator(incomingCodeGenerator.getClassName(),
					incomingCodeGenerator.getRootNamespaceName(), incomingCodeGenerator.getSourceFileName(),
					incomingCodeGenerator.getHost());
		}
		return super.decorateCodeGenerator(incomingCodeGenerator);
	}

	public ParserBase decorateCodeParser(ParserBase incomingCodeParser) throws Exception {

		return super.decorateCodeParser(incomingCodeParser);
	}

	private void getRidOfNamespace(String ns) {
		if (this.getNamespaceImports().contains(ns)) {
			this.getNamespaceImports().remove(ns);
		}
	}
}