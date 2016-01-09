package com.superstudio.web.razor.tokenizer;


public final class XmlHelpers
{
	public static boolean isXmlNameStartChar(char chr)
	{
		// [4] NameStartChar    ::= ":" | [A-Z] | "_" | [a-z] | [#xC0-#xD6] | [#xD8-#xF6] | [#xF8-#x2FF] | [#x370-#x37D] | 
		//                          [#x37F-#x1FFF] | [#x200C-#x200D] | [#x2070-#x218F] | [#x2C00-#x2FEF] | [#x3001-#xD7FF] | 
		//                          [#xF900-#xFDCF] | [#xFDF0-#xFFFD] | [#x10000-#xEFFFF]
		// http://www.w3.org/TR/REC-xml/#NT-Name

		return Character.isLetter(chr) || chr == ':' || chr == '_' || isInRange(chr, 0xC0, 0xD6) || isInRange(chr, 0xD8, 0xF6) || isInRange(chr, 0xF8, 0x2FF) || isInRange(chr, 0x370, 0x37D) || isInRange(chr, 0x37F, 0x1FFF) || isInRange(chr, 0x200C, 0x200D) || isInRange(chr, 0x2070, 0x218F) || isInRange(chr, 0x2C00, 0x2FEF) || isInRange(chr, 0x3001, 0xD7FF) || isInRange(chr, 0xF900, 0xFDCF) || isInRange(chr, 0xFDF0, 0xFFFD) || isInRange(chr, 0x10000, 0xEFFFF);
	}

	public static boolean isXmlNameChar(char chr)
	{
		// [4a] NameChar     ::= NameStartChar | "-" | "." | [0-9] | #xB7 | [#x0300-#x036F] | [#x203F-#x2040]
		// http://www.w3.org/TR/REC-xml/#NT-Name
		return Character.isDigit(chr) || isXmlNameStartChar(chr) || chr == '-' || chr == '.' || chr == '·' || isInRange(chr, 0x0300, 0x036F) || isInRange(chr, 0x203F, 0x2040); // (U+00B7 is middle dot: ·)
	}

	public static boolean isInRange(char chr, int low, int high)
	{
		return ((int)chr >= low) && ((int)chr <= high);
	}
}