package com.superstudio.template.templatepages;

/**
 DisplayInfo wraps the resolved file path and IDisplayMode for a request and path.
 The returned IDisplayMode can be used to resolve other page elements for the request.
*/
public class DisplayInfo
{
	public DisplayInfo(String filePath, IDisplayMode displayMode)
	{
		if (filePath == null)
		{
			throw new IllegalArgumentException("filePath");
		}

		if (displayMode == null)
		{
			throw new IllegalArgumentException("displayMode");
		}

		setFilePath(filePath);
		setDisplayMode(displayMode);
	}

	/** 
	 The Display Mode used to resolve a virtual path.
	*/
	private IDisplayMode DisplayMode;
	public final IDisplayMode getDisplayMode()
	{
		return DisplayMode;
	}
	private void setDisplayMode(IDisplayMode value)
	{
		DisplayMode = value;
	}

	/** 
	 Resolved path of a file that exists.
	*/
	private String filePath;
	public final String getFilePath()
	{
		return filePath;
	}
	private void setFilePath(String value)
	{
		filePath = value;
	}
}