package com.superstudio.jrazor;

import com.superstudio.commons.EventArgs;

public class CompilingPathEventArgs implements EventArgs
{
	private String VirtualPath;
	public final String getVirtualPath()
	{
		return VirtualPath;
	}
	private void setVirtualPath(String value)
	{
		VirtualPath = value;
	}
	private WebPageRazorHost host;
	public final WebPageRazorHost getHost()
	{
		return host;
	}
	public final void setHost(WebPageRazorHost value)
	{
		host = value;
	}
	public CompilingPathEventArgs(String virtualPath, WebPageRazorHost host)
	{
		this.setVirtualPath(virtualPath);
		this.setHost(host);
	}
}