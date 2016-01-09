package com.superstudio.template.language.mvc;


@FunctionalInterface
public interface StartPageLookupDelegate<T>
{
	/*WebPageRenderingBase invoke(WebPageRenderingBase page,
			String fileName,
			Iterable<String> supportedExtensions);*/
	T invoke(T page,String fileName,Iterable<String> s);
}