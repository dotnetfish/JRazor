package com.superstudio.jrazor.test;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.api.mockito.PowerMockito;

import com.superstudio.commons.CancellationTokenSource;
import com.superstudio.commons.TextReader;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.jrazor.GeneratorResults;
import com.superstudio.jrazor.ParserResults;
import com.superstudio.jrazor.RazorEngineHost;
import com.superstudio.jrazor.RazorTemplateEngine;
import com.superstudio.jrazor.generator.RazorCodeGenerator;
import com.superstudio.jrazor.parser.HtmlMarkupParser;
import com.superstudio.jrazor.parser.ParserBase;
import com.superstudio.jrazor.parser.RazorParser;
import com.superstudio.language.java.JavaRazorCodeGenerator;
import com.superstudio.language.java.JavaRazorCodeLanguage;
import com.superstudio.language.java.parser.JavaCodeParser;

public class JRazorTemplateEngineTest {

	@Test(expected = ArgumentNullException.class)
	public void constructorRequiresNonNullHost() throws Exception {
		new RazorTemplateEngine(null);
	}

	@Test
	public void constructorInitializesHost() throws Exception {
		// Arrange
		RazorEngineHost host = new RazorEngineHost(new JavaRazorCodeLanguage());

		// Act
		RazorTemplateEngine engine = new RazorTemplateEngine(host);

		// Assert
		Assert.assertSame(host, engine.getHost());
	}

	@Test
	public void createParserMethodIsConstructedFromHost() throws Exception {
		// Arrange
		RazorEngineHost host = createHost();
		RazorTemplateEngine engine = new RazorTemplateEngine(host);

		// Act
		RazorParser parser = engine.createParser();

		// Assert
		Assert.assertTrue(parser.getCodeParser() instanceof JavaCodeParser);
		Assert.assertTrue(parser.getMarkupParser() instanceof HtmlMarkupParser);

	}

	@Test
	public void createParserMethodSetsParserContextToDesignTimeModeIfHostSetToDesignTimeMode() throws Exception {
		// Arrange
		RazorEngineHost host = createHost();
		RazorTemplateEngine engine = new RazorTemplateEngine(host);
		host.setDesignTimeMode(true);

		// Act
		RazorParser parser = engine.createParser();

		// Assert
		Assert.assertTrue(parser.getDesignTimeMode());
	}

	@Test
	public void createParserMethodPassesParsersThroughDecoratorMethodsOnHost() throws Exception {
		// Arrange
		ParserBase expectedCode = PowerMockito.mock(ParserBase.class);
		ParserBase expectedMarkup = PowerMockito.mock(ParserBase.class);
		RazorEngineHost mockHost = PowerMockito.mock(RazorEngineHost.class);
		JavaRazorCodeLanguage language = new JavaRazorCodeLanguage();
		HtmlMarkupParser htmlparser = PowerMockito.mock(HtmlMarkupParser.class);
		JavaCodeParser parser = PowerMockito.mock(JavaCodeParser.class);
		PowerMockito.when(mockHost.decorateCodeParser(parser)).thenReturn(expectedCode);
		PowerMockito.when(mockHost.decorateCodeParser(htmlparser)).thenReturn(expectedMarkup);
		// ParserBase expectedCode = new Mock<ParserBase>().Object;
		// ParserBase expectedMarkup = new Mock<ParserBase>().Object;

		// var mockHost = new Mock<RazorEngineHost>(new JavaRazorCodeLanguage())
		// { CallBase = true };
		// mockHost.Setup(h ->
		// h.DecorateCodeParser(It.IsAny<CSharpCodeParser>()))
		// .Returns(expectedCode);
		// mockHost.Setup(h ->
		// h.DecorateMarkupParser(It.IsAny<HtmlMarkupParser>()))
		// .Returns(expectedMarkup);
		RazorTemplateEngine engine = new RazorTemplateEngine(mockHost);

		// Act
		RazorParser actual = engine.createParser();

		// Assert
		Assert.assertEquals(expectedCode, actual.getCodeParser());
		Assert.assertEquals(expectedMarkup, actual.getMarkupParser());
	}

	@Test
	public void createCodeGeneratorMethodPassesCodeGeneratorThroughDecorateMethodOnHost() throws Exception {
		// Arrange
		JavaRazorCodeLanguage language = PowerMockito.mock(JavaRazorCodeLanguage.class);
		RazorEngineHost mockHost = PowerMockito.mock(RazorEngineHost.class);
		JavaRazorCodeGenerator generator = PowerMockito.mock(JavaRazorCodeGenerator.class);
		RazorCodeGenerator expected = PowerMockito.mock(RazorCodeGenerator.class);
		PowerMockito.whenNew(RazorCodeGenerator.class).withArguments("Foo", "Bar", "Baz", mockHost)
				.thenReturn(expected);
		// PowerMockito.when(engine)
		// RazorCodeGenerator expected = new RazorCodeGenerator("Foo", "Bar",
		// "Baz", mockHost);
		PowerMockito.when(mockHost.decorateCodeGenerator(generator)).thenReturn(expected);
		// mockHost.Setup(h -> h.DecorateCodeGenerator(It.IsAny<>()))
		// .Returns(expected);
		// PowerMockito.when(expected.get)
		RazorTemplateEngine engine = new RazorTemplateEngine(mockHost);

		// Act

		RazorCodeGenerator actual = engine.createCodeGenerator("Foo", "Bar", "Baz");

		// Assert
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void parseTemplateCopiesTextReaderContentToSeekableTextReaderAndPassesToParseTemplateCore()
			throws Exception {
		// Arrange
		RazorTemplateEngine mockEngine = PowerMockito.mock(RazorTemplateEngine.class);// (CreateHost());
		TextReader reader = new TextReader("foo");
		CancellationTokenSource source = new CancellationTokenSource();

		// Act
		mockEngine.parseTemplate(reader, source.getToken());

		// Assert
		// mockEngine.Verify(e ->
		// e.ParseTemplateCore(It.Is<SeekableTextReader>(l -> l.ReadToEnd() ==
		// "foo"),
		// source.Token));
	}

	@Test
	public void generateCodeCopiesTextReaderContentToSeekableTextReaderAndPassesToGenerateCodeCore() throws Exception {
		// Arrange
		RazorTemplateEngine mockEngine = PowerMockito.mock(RazorTemplateEngine.class);// >(CreateHost());
		TextReader reader = new TextReader("foo");
		CancellationTokenSource source = new CancellationTokenSource();
		String className = "Foo";
		String ns = "Bar";
		String src = "Baz";

		// Act
		mockEngine.generateCode(reader, className, ns, src, source.getToken());

		// Assert
		// mockEngine.Verify(e -> e.GenerateCodeCore(It.Is<SeekableTextReader>(l
		// -> l.ReadToEnd() == "foo"),
		// className, ns, src, source.Token));
	}

	@Test
	public void parseTemplateOutputsResultsOfParsingProvidedTemplateSource() throws Exception {
		// Arrange
		RazorTemplateEngine engine = new RazorTemplateEngine(createHost());

		// Act
		ParserResults results = engine.parseTemplate(new TextReader("foo @bar("));

		// Assert
		Assert.assertFalse(results.getSuccess());
		Assert.assertTrue(results.getParserErrors().size() == 1);
		Assert.assertNotNull(results.getDocument());
	}

	@Test
	public void generateOutputsResultsOfParsingAndGeneration() throws Exception {
		// Arrange
		RazorTemplateEngine engine = new RazorTemplateEngine(createHost());

		// Act
		GeneratorResults results = engine.generateCode(new TextReader("foo @bar("));

		// Assert
		Assert.assertFalse(results.getSuccess());
		Assert.assertTrue(results.getParserErrors().size() == 1);
		Assert.assertNotNull(results.getDocument());
		Assert.assertNotNull(results.getGeneratedCode());
		Assert.assertNull(results.getDesignTimeLineMappings());
	}

	@Test
	public void generateOutputsDesignTimeMappingsIfDesignTimeSetOnHost() throws Exception {
		// Arrange
		RazorTemplateEngine engine = new RazorTemplateEngine(createHost(true));

		// Act
		GeneratorResults results = engine.generateCode(new TextReader("foo @bar()"), null, null, "foo.cshtml");

		// Assert
		Assert.assertTrue(results.getSuccess());
		Assert.assertTrue(results.getParserErrors().size() == 0);
		Assert.assertNotNull(results.getDocument());
		Assert.assertNotNull(results.getGeneratedCode());
		Assert.assertNotNull(results.getDesignTimeLineMappings());
	}

	private static RazorEngineHost createHost(boolean designTime) throws Exception {
		JavaRazorCodeLanguage language = new JavaRazorCodeLanguage();
		// language
		return new RazorEngineHost(language);

	}

	private static RazorEngineHost createHost() throws Exception {
		JavaRazorCodeLanguage language = new JavaRazorCodeLanguage();
		// language
		return new RazorEngineHost(language);

	}

}
