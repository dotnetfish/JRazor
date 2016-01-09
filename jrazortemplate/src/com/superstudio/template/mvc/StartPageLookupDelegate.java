package com.superstudio.template.mvc;

import com.superstudio.template.templatepages.WebPageRenderingBase;

@FunctionalInterface
public interface StartPageLookupDelegate
{
	WebPageRenderingBase invoke(WebPageRenderingBase page,
			String fileName,
			Iterable<String> supportedExtensions);
	//T invoke(T page,String fileName,Iterable<String> s);
}