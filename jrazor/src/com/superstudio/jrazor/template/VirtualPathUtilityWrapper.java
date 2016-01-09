package com.superstudio.jrazor.template;



public final class VirtualPathUtilityWrapper implements IVirtualPathUtility
{
	public String Combine(String basePath, String relativePath)
	{
		return VirtualPathUtility.combine(basePath, relativePath);
	}

	public String ToAbsolute(String virtualPath)
	{
		return VirtualPathUtility.toAbsolute(virtualPath);
	}
}