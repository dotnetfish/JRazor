package com.superstudio.jrazor.parser;

import java.util.function.Predicate;



public final class ParserHelpers {
	public static boolean isNewLine(char value) {
		return value == '\r' || value == '\n' || value == '\u0085' || value == '\u2028' || value == '\u2029'; // Paragraph
																												// separator
																												// -
																												// Line
																												// separator
																												// -
																												// Next
																												// Line
																												// -
																												// Linefeed
																												// -
																												// Carriage
																												// return
	}

	public static boolean isNewLine(String value) {
		
		return (value.length() == 1 && (isNewLine(value.charAt(0))))
				|| value.equals("\r\n");
	}

	// Returns true if the character is Whitespace and NOT a newline
	
	public static boolean isWhitespace(char value) {
		return value == ' ' || value == '\f' || value == '\t' || value == '\u000B'
				|| Character.getType(value) == Character.SPACE_SEPARATOR; // Vertical
																							// Tab
	}

	 
	
	public static boolean isWhitespaceOrNewLine(char value) {
		return isWhitespace(value) || isNewLine(value);
	}

	public static boolean isIdentifier(String value) {
		return isIdentifier(value, true);
	}

	public static boolean isIdentifier(String value, boolean requireIdentifierStart) {
		CharSequence identifierPart = value;
		//String identifierPart=value;
		if (requireIdentifierStart) {
			identifierPart = value.subSequence(1, value.length()-1);
		}
		return (!requireIdentifierStart || isIdentifierStart(value.charAt(0)))
				&& all(identifierPart,(t)->isIdentifierPart((char)t));
	}
	
	public static  boolean all(CharSequence source,Predicate<Character> pr){
		int loop=source.length();
		for(int i=0;i<loop;i++){
			if(!pr.test(source.charAt(i))){
				return false;
			}
		}
		return true;
	}

	public static boolean isHexDigit(char value) {
		return (value >= '0' && value <= '9') || (value >= 'A' && value <= 'F') || (value >= 'a' && value <= 'f');
	}

	public static boolean isIdentifierStart(char value) {
		return value == '_' || isLetter(value);
	}

	public static boolean isIdentifierPart(char value) {
		return isLetter(value) || isDecimalDigit(value) || isConnecting(value) || isCombining(value)
				|| isFormatting(value);
	}

	public static boolean isTerminatingCharToken(char value) {
		return isNewLine(value) || value == '\'';
	}

	public static boolean isTerminatingQuotedStringToken(char value) {
		return isNewLine(value) || value == '"';
	}

	public static boolean isDecimalDigit(char value) {
		return Character.getType(value)==Character.DECIMAL_DIGIT_NUMBER;
		//return Character.isDigit(value);
		// return Character.getName(value) ==UnicodeCategory.DecimalDigitNumber;
		// return Character.GetUnicodeCategory(value) ==
		// UnicodeCategory.DecimalDigitNumber;
	}

	public static boolean isLetterOrDecimalDigit(char value) {
		return isLetter(value) || isDecimalDigit(value);
	}

	public static boolean isLetter(char value) {
		
		//return Character.isLetter(value);
		int type=Character.getType(value);
		return type==Character.UPPERCASE_LETTER ||
				type==Character.LOWERCASE_LETTER||
				type==Character.TITLECASE_LETTER||
				type==Character.MODIFIER_LETTER ||
				type==Character.LETTER_NUMBER ||
				type==Character.OTHER_LETTER ;
		// var cat = Character.GetUnicodeCategory(value);
		// return cat == UnicodeCategory.UppercaseLetter || cat ==
		// UnicodeCategory.LowercaseLetter || cat ==
		// UnicodeCategory.TitlecaseLetter || cat ==
		// UnicodeCategory.ModifierLetter || cat == UnicodeCategory.OtherLetter
		// || cat == UnicodeCategory.LetterNumber;
	}

	public static boolean isFormatting(char value) {
		// return Character.isUnicodeIdentifierPart(value);
		int category = Character.getType(value);
		// Character.FORMAT
		return category == Character.FORMAT;
	}

	public static boolean isCombining(char value) {
		
		int category = Character.getType(value);

		return category == Character.COMBINING_SPACING_MARK || category == Character.NON_SPACING_MARK;
		
	}

	public static boolean isConnecting(char value) {
		return Character.getType(value) == Character.CONNECTOR_PUNCTUATION;
	}

	public static String sanitizeClassName(String inputName) {
		if (!isIdentifierStart(inputName.charAt(0)) && isIdentifierPart(inputName.charAt(0))) {
			inputName = "_" + inputName;
		}
		StringBuilder str = new StringBuilder();
		for (Character c : inputName.toCharArray()) {
			str.append(isIdentifierPart(c) ? c : '_');
		}
		
		return str.toString();
	}

	public static boolean isEmailPart(char character) {
		// Source: http://tools.ietf.org/html/rfc5322#section-3.4.1
		// We restrict the allowed characters to alpha-numerics and '_' in order
		// to ensure we cover most of the cases where an
		// email address is intended without restricting the usage of code
		// within JavaScript, CSS, and other contexts.
		return Character.isLetter(character) || Character.isDigit(character) || character == '_';
	}
}