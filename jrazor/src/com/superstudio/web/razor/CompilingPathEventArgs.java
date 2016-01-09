package com.superstudio.web.razor;

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
	private WebPageRazorHost Host;
	public final WebPageRazorHost getHost()
	{
		return Host;
	}
	public final void setHost(WebPageRazorHost value)
	{
		Host = value;
	}
	public CompilingPathEventArgs(String virtualPath, WebPageRazorHost host)
	{
		this.setVirtualPath(virtualPath);
		this.setHost(host);
	}
}