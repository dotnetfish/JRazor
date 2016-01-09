package com.superstudio.web.mvc.razor;

import com.superstudio.web.*;

@FunctionalInterface
public interface StartPageLookupDelegate<T>
{
	/*WebPageRenderingBase invoke(WebPageRenderingBase page,
			String fileName,
			Iterable<String> supportedExtensions);*/
	T invoke(T page,String fileName,Iterable<String> s);
}