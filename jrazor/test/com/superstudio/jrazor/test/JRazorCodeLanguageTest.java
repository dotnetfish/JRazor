package com.superstudio.jrazor.test;

import org.junit.Assert;
import org.junit.Test;

import com.superstudio.jrazor.RazorCodeLanguage;
import com.superstudio.language.java.JavaRazorCodeLanguage;

public class JRazorCodeLanguageTest {

	@Test
	public void servicesPropertyContainsEntriesForJavaCodeLanguageService() {
		// Assert
		Assert.assertEquals(1, RazorCodeLanguage.getLanguages().size());
		Assert.assertEquals(1, RazorCodeLanguage.getLanguages().size());

		RazorCodeLanguage language = RazorCodeLanguage.getLanguages().get("jhtml");
		Assert.assertTrue(language instanceof JavaRazorCodeLanguage);
	}

	@Test
	public void getServiceByExtensionReturnsEntryMatchingExtensionWithoutPreceedingDot() {
		RazorCodeLanguage language = RazorCodeLanguage.getLanguageByExtension("jhtml");
		Assert.assertTrue(language instanceof JavaRazorCodeLanguage);
		
	}

	@Test

	public void getServiceByExtensionReturnsEntryMatchingExtensionWithPreceedingDot() {
		RazorCodeLanguage language = RazorCodeLanguage.getLanguageByExtension(".jhtml");
		Assert.assertTrue(language instanceof JavaRazorCodeLanguage);
		
	}

	@Test

	public void getServiceByExtensionReturnsNullIfNoServiceForSpecifiedExtension() {
		RazorCodeLanguage language = RazorCodeLanguage.getLanguageByExtension("helloworld");
		Assert.assertNull(language);
		
	}

	@Test
	public void multipleCallsToGetServiceWithSameExtensionReturnSameObject() {
		// Arrange
		RazorCodeLanguage expected = RazorCodeLanguage.getLanguageByExtension("jhtml");

		// Act
		RazorCodeLanguage actual = RazorCodeLanguage.getLanguageByExtension("jhtml");

		// Assert
		Assert.assertEquals(expected, actual);
	}
}
