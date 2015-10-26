/*package com.superstudio.jrazor.test;

import org.junit.Assert;
import org.junit.Test;

import com.superstudio.jrazor.RazorDirectiveAttribute;

public class RazorDirectiveAttributeTest {
	@Test
	        public void ConstructorThrowsIfNameIsNullOrEmpty()
	        {
	            // Act and Assert
	            Assert.ThrowsArgumentNullOrEmptyString(() => new RazorDirectiveAttribute(name: null, value: "blah"), "name");
	            Assert.ThrowsArgumentNullOrEmptyString(() => new RazorDirectiveAttribute(name: "", value: "blah"), "name");
	        }

	@Test
	public void EnsureRazorDirectiveProperties() {
		// Arrange
		AttributeUsageAttribute attribute = (AttributeUsageAttribute) RazorDirectiveAttribute.class
				.getCustomAttributes(AttributeUsageAttribute.class, false).SingleOrDefault();

		// Assert
		Assert.True(attribute.AllowMultiple);
		Assert.True(attribute.ValidOn == AttributeTargets.Class);
		Assert.True(attribute.Inherited);
	}

	@Test
	public void EqualsAndGetHashCodeIgnoresCase() {
		// Arrange
		RazorDirectiveAttribute attribute1 = new RazorDirectiveAttribute("foo", "bar");
		RazorDirectiveAttribute attribute2 = new RazorDirectiveAttribute("fOo", "BAr");

		// Act
		int hashCode1 = attribute1.hashCode();
		int hashCode2 = attribute2.hashCode();

		// Assert
		Assert.assertEquals(attribute1, attribute2);
		Assert.assertEquals(hashCode1, hashCode2);
	}

	@Test
	public void EqualsAndGetHashCodeDoNotThrowIfValueIsNullOrEmpty() {
		// Arrange
		RazorDirectiveAttribute attribute1 = new RazorDirectiveAttribute("foo", null);
		RazorDirectiveAttribute attribute2 = new RazorDirectiveAttribute("foo", "BAr");

		// Act
		boolean result = attribute1.equals(attribute2);
		int hashCode = attribute1.hashCode();

		// Assert
		Assert.assertFalse(result);
		// If we've got this far, GetHashCode did not throw
	}

	@Test
	public void EqualsAndGetHashCodeReturnDifferentValuesForNullAndEmpty() {
		// Arrange
		RazorDirectiveAttribute attribute1 = new RazorDirectiveAttribute("foo", null);
		RazorDirectiveAttribute attribute2 = new RazorDirectiveAttribute("foo", "");

		// Act
		boolean result = attribute1.equals(attribute2);
		int hashCode1 = attribute1.hashCode();
		int hashCode2 = attribute2.hashCode();

		// Assert
		Assert.assertFalse(result);
		Assert.assertNotEquals(hashCode1, hashCode2);
	}
}
*/