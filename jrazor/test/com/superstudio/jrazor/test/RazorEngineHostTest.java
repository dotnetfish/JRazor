package com.superstudio.jrazor.test;

import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.superstudio.codedom.CodeCompileUnit;
import com.superstudio.codedom.CodeMemberMethod;
import com.superstudio.codedom.CodeNamespace;
import com.superstudio.codedom.CodeTypeDeclaration;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.jrazor.RazorCodeLanguage;
import com.superstudio.jrazor.RazorEngineHost;
import com.superstudio.jrazor.generator.GeneratedClassContext;
import com.superstudio.jrazor.generator.RazorCodeGenerator;
import com.superstudio.jrazor.parser.HtmlMarkupParser;
import com.superstudio.jrazor.parser.ParserBase;
import com.superstudio.language.java.JavaRazorCodeGenerator;
import com.superstudio.language.java.JavaRazorCodeLanguage;
import com.superstudio.language.java.parser.JavaCodeParser;

public class RazorEngineHostTest {

	public ExpectedException exp = ExpectedException.none();

	@Test(expected = ArgumentNullException.class)
	public void constructorRequiresNonNullCodeLanguage() throws Exception {
		new RazorEngineHost(null);
		new RazorEngineHost(null, () -> new HtmlMarkupParser());
		exp.expect(ArgumentNullException.class);
		// Assert.ThrowsArgumentNull(() -> , "codeLanguage");
		// Assert.ThrowsArgumentNull(() -> , "codeLanguage");
	}

	@Test(expected = ArgumentNullException.class)
	public void constructorRequiresNonNullMarkupParser() throws Exception {
		new RazorEngineHost(new JavaRazorCodeLanguage(), null);
		// exp.expect(ArgumentNullException.class);
		// Assert.ThrowsArgumentNull(() -> , "markupParserFactory");
	}

	@Test
	public void constructorWithCodeLanguageSetsPropertiesAppropriately() throws Exception {
		// Arrange
		RazorCodeLanguage language = new JavaRazorCodeLanguage();

		// Act
		RazorEngineHost host = new RazorEngineHost(language);

		// Assert
		verifyCommonDefaults(host);
		Assert.assertEquals(language, host.getCodeLanguage());
		// Assert.IsType<HtmlMarkupParser>(host.CreateMarkupParser());
	}

	@Test
	public void constructorWithCodeLanguageAndMarkupParserSetsPropertiesAppropriately() throws Exception {
		// Arrange
		RazorCodeLanguage language = new JavaRazorCodeLanguage();
		ParserBase expected = new HtmlMarkupParser();

		// Act
		RazorEngineHost host = new RazorEngineHost(language, () -> expected);

		// Assert
		verifyCommonDefaults(host);
		Assert.assertEquals(language, host.getCodeLanguage());
		Assert.assertEquals(expected, host.createMarkupParser());
	}

	@Test(expected = ArgumentNullException.class)
	public void decorateCodeParserRequiresNonNullCodeParser() throws Exception {
		createHost().decorateCodeParser(null);
	}

	@Test(expected = ArgumentNullException.class)
	public void decorateMarkupParserRequiresNonNullMarkupParser() throws Exception {
		createHost().decorateMarkupParser(null);
	}

	@Test(expected = ArgumentNullException.class)
	public void decorateCodeGeneratorRequiresNonNullCodeGenerator() throws Exception {
		createHost().decorateCodeGenerator(null);
	}

	@Test(expected = ArgumentNullException.class)
	public void postProcessGeneratedCodeRequiresNonNullCompileUnit() throws Exception {
		createHost().postProcessGeneratedCode(null,
                 new CodeNamespace(),
                 new CodeTypeDeclaration(),
                 new CodeMemberMethod());
	}

	@Test(expected = ArgumentNullException.class)
	public void postProcessGeneratedCodeRequiresNonNullGeneratedNamespace() throws Exception {
		createHost().postProcessGeneratedCode(new CodeCompileUnit(), null, new CodeTypeDeclaration(),
				new CodeMemberMethod());
	}

	@Test(expected = ArgumentNullException.class)
	public void postProcessGeneratedCodeRequiresNonNullGeneratedClass() throws Exception {
		createHost().postProcessGeneratedCode(new CodeCompileUnit(), new CodeNamespace(), null, new CodeMemberMethod());

	}

	@Test(expected = ArgumentNullException.class)
	public void postProcessGeneratedCodeRequiresNonNullExecuteMethod() throws Exception {
		createHost().postProcessGeneratedCode(new CodeCompileUnit(), new CodeNamespace(), new CodeTypeDeclaration(),
				null);
	}

	@Test
	public void decorateCodeParserDoesNotModifyIncomingParser() throws Exception {
		// Arrange
		ParserBase expected = new JavaCodeParser();

		// Act
		ParserBase actual = createHost().decorateCodeParser(expected);

		// Assert
		Assert.assertSame(expected, actual);
	}

	@Test
	public void decorateMarkupParserReturnsIncomingParser() throws Exception {
		// Arrange
		ParserBase expected = new HtmlMarkupParser();

		// Act
		ParserBase actual = createHost().decorateMarkupParser(expected);

		// Assert
		Assert.assertSame(expected, actual);
	}

	@Test
	public void decorateCodeGeneratorReturnsIncomingCodeGenerator() throws Exception {
		// Arrange
		RazorCodeGenerator expected = new JavaRazorCodeGenerator("Foo", "Bar", "Baz", createHost());

		// Act
		RazorCodeGenerator actual = createHost().decorateCodeGenerator(expected);

		// Assert
		Assert.assertSame(expected, actual);
	}

	@Test
	public void postProcessGeneratedCodeDoesNotModifyCode() throws Exception {
		// Arrange
		CodeCompileUnit compileUnit = new CodeCompileUnit();
		CodeNamespace ns = new CodeNamespace();
		CodeTypeDeclaration typeDecl = new CodeTypeDeclaration();
		CodeMemberMethod execMethod = new CodeMemberMethod();

		// Act
		createHost().postProcessGeneratedCode(compileUnit, ns, typeDecl, execMethod);

		// Assert
		Assert.assertTrue(compileUnit.getNamespaces().size() == 0);
		Assert.assertTrue(ns.getImports().size() == 0);
		Assert.assertTrue(ns.getTypes().size() == 0);
		Assert.assertTrue(typeDecl.getMembers().size() == 0);
		Assert.assertTrue(execMethod.getStatements().size() == 0);
	}

	private static RazorEngineHost createHost() throws Exception {
		return new RazorEngineHost(new JavaRazorCodeLanguage());
	}

	private static void verifyCommonDefaults(RazorEngineHost host) {
		Assert.assertEquals(GeneratedClassContext.Default, host.getGeneratedClassContext());
		Assert.assertTrue(host.getNamespaceImports().size() == 0);
		Assert.assertFalse(host.getDesignTimeMode());
		Assert.assertEquals(RazorEngineHost.InternalDefaultClassName, host.getDefaultClassName());
		Assert.assertEquals(RazorEngineHost.InternalDefaultNamespace, host.getDefaultNamespace());
	}
}
