package com.superstudio.web.mvc.razor;

import com.superstudio.codedom.*;
import com.superstudio.language.java.mvc.MvcJavaRazorCodeParser;
import com.superstudio.language.java.parser.JavaCodeParser;
import com.superstudio.web.razor.WebPageRazorHost;
import com.superstudio.web.razor.generator.CodeGeneratorContext;
import com.superstudio.web.razor.generator.RazorCodeGenerator;
import com.superstudio.web.razor.parser.ParserBase;

public class MvcWebPageRazorHost extends WebPageRazorHost {
	public MvcWebPageRazorHost(String virtualPath, String physicalPath) {
		super(virtualPath, physicalPath);
		// super.registerSpecialFile(RazorViewEngine.ViewStartFileName,
		// ViewStartPage.class);
		// super.DefaultPageBaseClass = WebViewPage.class.getName();
		super.setDefaultPageBaseClass("com.superstudio.web.templatepages");
		//this.getRidOfNamespace("System.Web.WebPages.Html");
	}

	 @Override
	public RazorCodeGenerator decorateCodeGenerator(RazorCodeGenerator incomingCodeGenerator) throws Exception {

		return super.decorateCodeGenerator(incomingCodeGenerator);
	}
	 
@Override
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
	
	@Override
	public void postProcessGeneratedCode(CodeGeneratorContext context) {
		super.postProcessGeneratedCode(context);
		CodeMemberProperty codeMemberProperty = new CodeMemberProperty();
		codeMemberProperty.setName("ApplicationInstance");
		codeMemberProperty.setType(new CodeTypeReference(this.getGlobalAsaxTypeName()));
		codeMemberProperty.setHasGet(true);
		codeMemberProperty.setHasSet(false);
		codeMemberProperty.setAttributes(MemberAttributes.forValue(12290));
		codeMemberProperty.getGetStatements()
				.Add(new CodeMethodReturnStatement(new CodeCastExpression(
						new CodeTypeReference(this.getGlobalAsaxTypeName()), new CodePropertyReferenceExpression(
								new CodePropertyReferenceExpression(null, "Context"), "ApplicationInstance"))));
		context.getGeneratedClass().getMembers().Insert(0, codeMemberProperty);
	} 
}