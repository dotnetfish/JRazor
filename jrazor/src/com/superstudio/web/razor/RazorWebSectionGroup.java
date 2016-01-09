package com.superstudio.web.razor;

public class RazorWebSectionGroup //extends ConfigurationSectionGroup
{
	public static final String GroupName = "system.web.webPages.razor";
	private boolean _hostSet;
	private boolean _pagesSet;
	private HostSection _host;
	private RazorPagesSection _pages;
	public final HostSection getHost()
	{
		if (!this._hostSet)
		{
			//return (HostSection)super.Sections["host"];
		}
		return this._host;
	}
	public final void setHost(HostSection value)
	{
		this._host = value;
		this._hostSet = true;
	}

	public final RazorPagesSection getPages()
	{
		if (!this._pagesSet)
		{
			//return (RazorPagesSection)super.Sections["pages"];
		}
		return this._pages;
	}
	public final void setPages(RazorPagesSection value)
	{
		this._pages = value;
		this._pagesSet = true;
	}
}