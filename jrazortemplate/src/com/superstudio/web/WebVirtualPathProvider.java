package com.superstudio.web;

import com.superstudio.commons.VirtualPathProvider;
import com.superstudio.template.mvc.context.HostContext;

import java.io.File;

public class WebVirtualPathProvider extends VirtualPathProvider {

	@Override
	public boolean fileExists(String virtualPath) {
		String path;
		try {
			
			path = HostContext.getCurrent().mapPath("/");
			path+=virtualPath.toLowerCase().replace("~/", "WEB-INF/").replace("\\", "//");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return new File(path).exists();
	}
}
