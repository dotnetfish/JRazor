package com.superstudio.template.templatepages;


public interface IVirtualPathUtility
{
	String combine(String basePath, String relativePath);

	String toAbsolute(String virtualPath);
}