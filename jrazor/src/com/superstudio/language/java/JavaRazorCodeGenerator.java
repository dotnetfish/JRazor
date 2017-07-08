package com.superstudio.language.java;

import com.superstudio.codedom.CodeSnippetTypeMember;
import com.superstudio.web.razor.RazorEngineHost;
import com.superstudio.web.razor.generator.CodeGeneratorContext;
import com.superstudio.web.razor.generator.CodeWriter;
import com.superstudio.web.razor.generator.RazorCodeGenerator;

import java.util.function.Supplier;

public class JavaRazorCodeGenerator extends RazorCodeGenerator {
	private static final String HiddenLinePragma = "//#line hidden";

	public JavaRazorCodeGenerator(String className, String rootNamespaceName, String sourceFileName,
			RazorEngineHost host) throws Exception {
		super(className, rootNamespaceName, sourceFileName, host);
	}

	@Override
	public Supplier<CodeWriter> getCodeWriterFactory() {
		
		return () -> new JavaCodeWriter();
	}


	@Override
	protected void initialize(CodeGeneratorContext context) {
		super.initialize(context);

		context.getGeneratedClass().getMembers().add(0, new CodeSnippetTypeMember(HiddenLinePragma));
	}
}
