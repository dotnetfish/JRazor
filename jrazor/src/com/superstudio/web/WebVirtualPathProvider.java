package com.superstudio.web;

import com.superstudio.commons.VirtualPathProvider;

import java.io.File;

public class WebVirtualPathProvider extends VirtualPathProvider {

	@Override
	public boolean fileExists(String virtualPath) {
		String path;
		try {
			
			path = HttpContext.getCurrent().getRequest().mapPath("/");
			path+=virtualPath.toLowerCase().replace("~/", "WEB-INF/").replace("\\", "//");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return new File(path).exists();
	}
}
