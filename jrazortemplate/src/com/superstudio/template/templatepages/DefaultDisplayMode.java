package com.superstudio.template.templatepages;

import com.superstudio.commons.Path;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.template.mvc.context.HostContext;

import java.util.function.Predicate;



/** 
 The <see cref="DefaultDisplayMode"/> can take any suffix and determine if there is a corresponding
 file that exists given a path and request by transforming the path to contain the suffix.
 add a new DefaultDisplayMode to the Modes collection to handle a new suffix or inherit from
 DefaultDisplayMode to provide custom logic to transform paths with a suffix.
*/
public class DefaultDisplayMode implements IDisplayMode
{
	private String _suffix;

	public DefaultDisplayMode()
	{
		this(DisplayModeProvider.DefaultDisplayModeId);
	}

	public DefaultDisplayMode(String suffix)
	{
		_suffix = (suffix != null) ? suffix : "";
	}

	/** 
	 When set, the <see cref="DefaultDisplayMode"/> will only be available to return Display Info for a request
	 if the ContextCondition evaluates to true.
	*/
	private Predicate<HostContext> ContextCondition;
	public final Predicate<HostContext> getContextCondition()
	{
		return ContextCondition;
	}
	public final void setContextCondition(Predicate<HostContext> value)
	{
		ContextCondition = value;
	}

	public String getDisplayModeId()
	{
		return _suffix;
	}

	public final boolean canHandleContext(HostContext httpContext)
	{
		return getContextCondition() == null || ContextCondition.test(httpContext);
	}

	/** 
	 Returns DisplayInfo with the transformed path if it exists.
	*/
	public DisplayInfo getDisplayInfo(HostContext httpContext, String virtualPath, Predicate<String> virtualPathExists)
	{
		String transformedFilename = transformPath(virtualPath, _suffix);
		if (transformedFilename != null && virtualPathExists.test(transformedFilename))
		{
			return new DisplayInfo(transformedFilename, this);
		}

		return null;
	}

	/** 
	 Transforms paths according to the following rules:
	 \some\path.blah\file.txt.zip -> \some\path.blah\file.txt.suffix.zip
	 \some\path.blah\file -> \some\path.blah\file.suffix
	*/
	protected String transformPath(String virtualPath, String suffix)
	{
		if (StringHelper.isNullOrEmpty(suffix))
		{
			return virtualPath;
		}

		String extension = Path.GetExtension(virtualPath);
		return Path.ChangeExtension(virtualPath, suffix + extension);
	}
}