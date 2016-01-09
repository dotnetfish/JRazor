package com.superstudio.template.templatepages;

import java.nio.file.Paths;

public class VirtualPathUtility {

	public static String getDirectory(String virtualPath) {
		
		return Paths.get(virtualPath).getParent().toString();
	}

	public static String combine(String virtualPath, String path) {
	
		return Paths.get(virtualPath).resolve(Paths.get(path)).toString();
	}

	public static String toAbsolute(String virtualPath) {
		
		return Paths.get(virtualPath).toAbsolutePath().toString();
	}

	

}
