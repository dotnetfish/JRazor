package com.superstudio.jrazor.template;



public interface IVirtualPathUtility
{
	String Combine(String basePath, String relativePath);

	String ToAbsolute(String virtualPath);
}