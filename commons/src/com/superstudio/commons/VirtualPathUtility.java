package com.superstudio.commons;



public class VirtualPathUtility {

	public static String GetExtension(String t) {
		return Path.GetExtension(t);
		//return Paths.get(t)
	}

}
