package com.superstudio.web;

import com.superstudio.commons.VirtualPathProvider;
import com.superstudio.template.mvc.context.HostContext;

import java.io.File;

public class WebVirtualPathProvider extends VirtualPathProvider {

	@Override
	public boolean fileExists(String virtualPath) {
		//CodeExecuteTimeStatistic.evaluteTick(this.getClass().getName());
		String path;
		try {
			
			path = HostContext.getCurrent().mapPath("/");
			path+=virtualPath.replace("~/", "WEB-INF/").replace("\\", "//");
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}
		//System.out.println(path);
		return new File(path).exists();
	}
}
