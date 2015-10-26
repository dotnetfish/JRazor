package com.superstudio.jrazor.test;

import org.junit.Assert;
import org.junit.Test;

import com.superstudio.jrazor.RazorCodeLanguage;
import com.superstudio.jrazor.RazorEngineHost;
import com.superstudio.jrazor.generator.RazorCodeGenerator;
import com.superstudio.jrazor.parser.ParserBase;
import com.superstudio.language.java.JavaCodeProvider;
import com.superstudio.language.java.JavaRazorCodeLanguage;
import com.superstudio.language.java.parser.JavaCodeParser;

public class JavaRazorCodeLanguageTest {

	@Test
	public void createCodeParserReturnsNewCSharpCodeParser() {
		// Arrange
		RazorCodeLanguage service = new JavaRazorCodeLanguage();

		// Act
		ParserBase parser = service.createCodeParser();

		// Assert
		Assert.assertNotNull(parser);
		Assert.assertTrue(parser instanceof JavaCodeParser);
		
	}

	@Test
	public void createCodeGeneratorParserListenerReturnsNewCSharpCodeGeneratorParserListener() throws Exception {
		// Arrange
		RazorCodeLanguage service = new JavaRazorCodeLanguage();

		// Act
		RazorEngineHost host = new RazorEngineHost(service);
		RazorCodeGenerator generator = service.createCodeGenerator("Foo", "Bar", "Baz", host);

		// Assert
		Assert.assertNotNull(generator);

		Assert.assertEquals("Foo", generator.getClassName());
		Assert.assertEquals("Bar", generator.getRootNamespaceName());
		Assert.assertEquals("Baz", generator.getSourceFileName());
		Assert.assertEquals(host, generator.getHost());
	}

	@Test
	public void codeDomProviderTypeReturnsVBCodeProvider() {
		// Arrange
		RazorCodeLanguage service = new JavaRazorCodeLanguage();

		// Assert
		Assert.assertEquals(JavaCodeProvider.class, service.getCodeDomProviderType());
	}
}
