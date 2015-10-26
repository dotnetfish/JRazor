package com.superstudio.language.java;



public final class JavaHelpers {

	public static boolean isIdentifierStart(char character) {
		return Character.isLetter(character) || character == '_'
				|| Character.isLetterOrDigit(character); // Ln
	}

	public static boolean isIdentifierPart(char character) {
		return Character.isDigit(character) || isIdentifierStart(character)
				|| isIdentifierPartByUnicodeCategory(character);
	}

	public static boolean isRealLiteralSuffix(char character) {
		return character == 'F' || character == 'f' || character == 'D' || character == 'd' || character == 'M'
				|| character == 'm';
	}

	private static boolean isIdentifierPartByUnicodeCategory(char character) {
		int category = Character.getType(character);
		return category == Character.NON_SPACING_MARK || category == Character.COMBINING_SPACING_MARK
				|| category == Character.CONNECTOR_PUNCTUATION || category == Character.FORMAT; 			// Mn
	}
}