package com.superstudio.template.templatepages;

import com.superstudio.template.mvc.context.HostContext;

import java.util.ArrayList;
import java.util.List;


public class WebPageHttpHandler {

	public static boolean shouldGenerateSourceHeader(HostContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	public static List<String> getRegisteredExtensions() {
		// TODO Auto-generated method stub
		ArrayList<String> extenions=new ArrayList();
		extenions.add("jhtml");
		return extenions ;
	}

}