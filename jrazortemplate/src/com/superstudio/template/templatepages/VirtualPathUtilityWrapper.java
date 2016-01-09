package com.superstudio.template.templatepages;



public final class VirtualPathUtilityWrapper implements IVirtualPathUtility
{
	public String combine(String basePath, String relativePath)
	{
		return VirtualPathUtility.combine(basePath, relativePath);
	}

	public String toAbsolute(String virtualPath)
	{
		return VirtualPathUtility.toAbsolute(virtualPath);
	}
}