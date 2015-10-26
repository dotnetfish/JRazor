package com.superstudio.commons;

public final class HostingEnvironmentWrapper implements IHostingEnvironment
{
	public String mapPath(String virtualPath)
	{
		return HostingEnvironment.MapPath(virtualPath);
	}
}