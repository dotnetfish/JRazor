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
			path+=virtualPath.toLowerCase().replace("~/", "web-inf/").replace("\\", "//");
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}
		
		return new File(path).exists();
	}
}
