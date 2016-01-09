package com.superstudio.web.razor.utils;


public final class CharUtils
{
	public static boolean isNonNewLineWhitespace(char c)
	{
		return Character.isWhitespace(c) && !isNewLine(c);
	}

	public static boolean isNewLine(char c)
	{
		return c == 0x000d || c == 0x000a || c == 0x2028 || c == 0x2029; // Paragraph separator -  Line separator -  Linefeed -  Carriage return
	}
}