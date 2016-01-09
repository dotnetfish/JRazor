package com.superstudio.web.razor.generator;

import com.superstudio.codedom.*;
import com.superstudio.commons.csharpbridge.action.Func;
import com.superstudio.web.razor.*;


public class CSharpRazorCodeGenerator extends RazorCodeGenerator {
	private static final String HiddenLinePragma = "#line hidden";

	public CSharpRazorCodeGenerator(String className, String rootNamespaceName, String sourceFileName,
			RazorEngineHost host) throws Exception {
		super(className, rootNamespaceName, sourceFileName, host);
	}

	@Override
	public Func<CodeWriter> getCodeWriterFactory() {

		// methods are not converted
		return () -> new CSharpCodeWriter();
	}


	// .NET attributes:
	// [SuppressMessage("Microsoft.Globalization", "CA1303:Do not pass literals
	// as localized parameters", MessageId =
	// "System.CodeDom.CodeSnippetTypeMember.#ctor(System.String)",
	// Justification = "Value is never to be localized")]
	@Override
	protected void initialize(CodeGeneratorContext context) {
		super.initialize(context);

		context.getGeneratedClass().getMembers().add(0, new CodeSnippetTypeMember(HiddenLinePragma));
	}
}