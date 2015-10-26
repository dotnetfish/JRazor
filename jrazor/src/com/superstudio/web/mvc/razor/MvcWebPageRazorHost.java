package com.superstudio.web.mvc.razor;

import com.superstudio.codedom.*;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.jrazor.WebPageRazorHost;
import com.superstudio.jrazor.generator.CodeGeneratorContext;
import com.superstudio.jrazor.generator.RazorCodeGenerator;
import com.superstudio.jrazor.parser.*;
import com.superstudio.language.java.JavaRazorCodeGenerator;
import com.superstudio.language.java.mvc.MvcJavaRazorCodeGenerator;
import com.superstudio.language.java.mvc.MvcJavaRazorCodeParser;
import com.superstudio.language.java.parser.JavaCodeParser;

public class MvcWebPageRazorHost extends WebPageRazorHost {
	public MvcWebPageRazorHost(String virtualPath, String physicalPath) {
		super(virtualPath, physicalPath);
		// super.RegisterSpecialFile(RazorViewEngine.ViewStartFileName,
		// ViewStartPage.class);
		// super.DefaultPageBaseClass = WebViewPage.class.getName();
		super.setDefaultPageBaseClass("com.superstudio.web.webpages");
		this.getRidOfNamespace("System.Web.WebPages.Html");
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
	public void postProcessGeneratedCode(CodeGeneratorContext context) throws ArgumentNullException {
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