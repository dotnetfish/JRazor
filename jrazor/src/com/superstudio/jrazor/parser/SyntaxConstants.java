package com.superstudio.jrazor.parser;




public final class SyntaxConstants
{
	public static final String TextTagName = "text";
	public static final char TransitionCharacter = '@';
	public static final String TransitionString = "@";
	public static final String StartCommentSequence = "@*";
	public static final String EndCommentSequence = "*@";

 
	
	public static class Java
	{
		public static final int UsingKeywordLength = 7;
		public static final String InheritsKeyword = "inherits";
		public static final String FunctionsKeyword = "functions";
		public static final String SectionKeyword = "section";
		public static final String HelperKeyword = "helper";
		public static final String ElseIfKeyword = "else if";
		public static final String NamespaceKeyword = "package";
		public static final String ClassKeyword = "class";
		public static final String LayoutKeyword = "layout";
		public static final String SessionStateKeyword = "sessionstate";
	}


}