package com.superstudio.language.java;

import com.superstudio.codedom.CodeSnippetTypeMember;
import com.superstudio.commons.csharpbridge.action.Func;
import com.superstudio.jrazor.RazorEngineHost;
import com.superstudio.jrazor.generator.CodeGeneratorContext;
import com.superstudio.jrazor.generator.CodeWriter;
import com.superstudio.jrazor.generator.RazorCodeGenerator;

public class JavaRazorCodeGenerator extends RazorCodeGenerator {
	private static final String HiddenLinePragma = "//#line hidden";

	public JavaRazorCodeGenerator(String className, String rootNamespaceName, String sourceFileName,
			RazorEngineHost host) throws Exception {
		super(className, rootNamespaceName, sourceFileName, host);
	}

	@Override
	public Func<CodeWriter> getCodeWriterFactory() {
		
		return () -> new JavaCodeWriter();
	}


	@Override
	protected void initialize(CodeGeneratorContext context) {
		super.initialize(context);

		context.getGeneratedClass().getMembers().add(0, new CodeSnippetTypeMember(HiddenLinePragma));
	}
}
