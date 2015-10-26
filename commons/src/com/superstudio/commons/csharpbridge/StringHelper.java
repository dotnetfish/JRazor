package com.superstudio.commons.csharpbridge;

import java.util.ArrayList;
import java.util.List;

public final class StringHelper
{
	public static final String Empty = "";

	//------------------------------------------------------------------------------------
	//	This method replaces the .NET static string method 'IsNullOrEmpty'.
	//------------------------------------------------------------------------------------
	public static boolean isNullOrEmpty(String string)
	{
		return string == null || string.equals("");
	}
	
	public static boolean isNullOrWhiteSpace(String string)
	{
		return string == null || string.equals("");
	}

	//------------------------------------------------------------------------------------
	//	This method replaces the .NET static string method 'Join' (2 parameter version).
	//------------------------------------------------------------------------------------
	public static String join(String separator, String[] stringarray)
	{
		if (stringarray == null)
			return null;
		else
			return join(separator, stringarray, 0, stringarray.length);
	}

	//------------------------------------------------------------------------------------
	//	This method replaces the .NET static string method 'Join' (4 parameter version).
	//------------------------------------------------------------------------------------
	public static String join(String separator, String[] stringarray, int startindex, int count)
	{
		String result = "";

		if (stringarray == null)
			return null;

		for (int index = startindex; index < stringarray.length && index - startindex < count; index++)
		{
			if (separator != null && index > startindex)
				result += separator;

			if (stringarray[index] != null)
				result += stringarray[index];
		}

		return result;
	}

	//------------------------------------------------------------------------------------
	//	This method replaces the .NET static string method 'TrimEnd'.
	//------------------------------------------------------------------------------------
	public static String trimEnd(String string, Character... charsToTrim)
	{
		if (string == null || charsToTrim == null)
			return string;

		int lengthToKeep = string.length();
		for (int index = string.length() - 1; index >= 0; index--)
		{
			boolean removeChar = false;
			if (charsToTrim.length == 0)
			{
				if (Character.isWhitespace(string.charAt(index)))
				{
					lengthToKeep = index;
					removeChar = true;
				}
			}
			else
			{
				for (int trimCharIndex = 0; trimCharIndex < charsToTrim.length; trimCharIndex++)
				{
					if (string.charAt(index) == charsToTrim[trimCharIndex])
					{
						lengthToKeep = index;
						removeChar = true;
						break;
					}
				}
			}
			if ( ! removeChar)
				break;
		}
		return string.substring(0, lengthToKeep);
	}

	//------------------------------------------------------------------------------------
	//	This method replaces the .NET static string method 'TrimStart'.
	//------------------------------------------------------------------------------------
	public static String trimStart(String string, Character... charsToTrim)
	{
		if (string == null || charsToTrim == null)
			return string;

		int startingIndex = 0;
		for (int index = 0; index < string.length(); index++)
		{
			boolean removeChar = false;
			if (charsToTrim.length == 0)
			{
				if (Character.isWhitespace(string.charAt(index)))
				{
					startingIndex = index + 1;
					removeChar = true;
				}
			}
			else
			{
				for (int trimCharIndex = 0; trimCharIndex < charsToTrim.length; trimCharIndex++)
				{
					if (string.charAt(index) == charsToTrim[trimCharIndex])
					{
						startingIndex = index + 1;
						removeChar = true;
						break;
					}
				}
			}
			if ( ! removeChar)
				break;
		}
		return string.substring(startingIndex);
	}

	//------------------------------------------------------------------------------------
	//	This method replaces the .NET static string method 'Trim' when arguments are used.
	//------------------------------------------------------------------------------------
	public static String trim(String string, Character... charsToTrim)
	{
		return trimEnd(trimStart(string, charsToTrim), charsToTrim);
	}

	public static boolean stringsEqual(String s1, String s2)
	{
		return stringsEqual(s1,s2,StringComparison.Ordinal);
	}
	//------------------------------------------------------------------------------------
	//	This method is used for string equality comparisons when the option
	//	'Use helper 'stringsEqual' method to handle null strings' is selected
	//	(The Java String 'equals' method can't be called on a null instance).
	//------------------------------------------------------------------------------------
	public static boolean stringsEqual(String s1, String s2,StringComparison com)
	{
		if(com==StringComparison.OrdinalIgnoreCase){
			s1=s1.toLowerCase();
			s2=s2.toLowerCase();
		}
		if (s1 == null && s2 == null)
			return true;
		else
			return s1 != null && s1.equals(s2);
	}
	
	public static int indexOfAny(String str,char... chars){
		int index=0;
		for(char c:chars){
			
			if(str.contains(String.valueOf(c)))
				return index;
			index++;
		}
		return -1;
	}
	
	/**
	* 右补位，左对齐 
	* 
	* @param oriStr
	*            原字符串 
	* @param len
	*            目标字符串长度 
	* @param alexin
	*            补位字符 
	* @return 目标字符串 
	*/
	public static String padRight(String oriStr, int len, String alexin) {
		String str = "";
		int strlen = oriStr.length();
		if (strlen < len) {
			for (int i = 0; i < len - strlen; i++) {
				str = str + alexin;
			}
		}
		str = str + oriStr;
		return str;
	}

	/**
	* 左补位，右对齐
	* 
	* @param oriStr
	*            原字符串
	* @param len
	*            目标字符串长度
	* @param alexin
	*            补位字符
	* @return 目标字符串
	*/
	public static String padLeft(String oriStr, int len, String alexin) {
		String str = "";
		int strlen = oriStr.length();
		if (strlen < len) {
			for (int i = 0; i < len - strlen; i++) {
				str = str + alexin;
			}
		}
		str = oriStr + str;
		return str;
	}

	public static int endsWithAny(String previousContent, char[] newlinechars) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static int lastIndexOfAny(String previousContent, char[] newlinechars) {
		// TODO Auto-generated method stub、
		List<Character> list=new ArrayList<Character>();
		for(char ch :newlinechars){
			list.add(ch);
		}
		//char[] chars=previousContent.toCharArray();
		int len=previousContent.length();
		//new ArrayList<Character>(newlinechars);
		for(int i=len-1;i>-1;i--){
			if(list.contains(previousContent.charAt(i))){
				return i;
			}
		}
		return -1;
	}
	
	public static String concat(List<String> obj){
		StringBuilder builder=new StringBuilder();
		for(String item :obj){
			builder.append(item);
		}
		
		return builder.toString();
	}

	public static boolean startWith(String value, String prefix, StringComparison ordinalignorecase) {
		// TODO Auto-generated method stub
		if(ordinalignorecase==StringComparison.OrdinalIgnoreCase)
			return value.toLowerCase().startsWith(prefix.toLowerCase());
		return value.startsWith(prefix);
	}

	public static int Compare(String name, String string, StringComparison ordinalignorecase) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static boolean equals(String string, String string2, StringComparison ordinalignorecase) {
		// TODO Auto-generated method stub
		return stringsEqual(string,string2,ordinalignorecase);
	}

	/*public static String[] split(String current, char[] separator) {
		// TODO Auto-generated method stub
		return current.split(separator);
	}
*/
	public static String format(String pattern,Object str){
		return java.text.MessageFormat.format(pattern, str);
	}
	public static String format(String pattern,String str){
		return java.text.MessageFormat.format(pattern, str);
	}
	public static String format(String currentculture, String pattern,
			Object[] objects) {
		return java.text.MessageFormat.format(pattern, objects);
		//return String.format(pattern, objects);
	}

	public static String format(String currentculture, String pattern,
			String... others) {
		
		return java.text.MessageFormat.format(pattern, others);
	}
	public static int indexOf(String virtualPath, String string, StringComparison ordinalignorecase) {
		// TODO Auto-generated method stub
		if(ordinalignorecase==StringComparison.OrdinalIgnoreCase)
			return virtualPath.toLowerCase().indexOf(string.toLowerCase());
		return virtualPath.indexOf(string);
	}

	public static int HashCode(String virtualPathString, StringComparison ordinalignorecase) {
		// TODO Auto-generated method stub
		if(ordinalignorecase==StringComparison.OrdinalIgnoreCase)
			return virtualPathString.toLowerCase().hashCode();
		return virtualPathString.hashCode();
		
	}

	public static String remove(String path, int i, int j) {
		// TODO Auto-generated method stub
		return path.substring(0,i)+path.substring(j,path.length());
	}


}