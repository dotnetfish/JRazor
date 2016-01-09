package com.superstudio.template.templatepages;

import com.superstudio.template.mvc.context.HostContext;

import java.util.function.Predicate;




/** 
 An interface that provides DisplayInfo for a virtual path and request. An IDisplayMode may modify the virtual path before checking
 if it exists. canHandleContext is called to determine if the Display Mode is available to return display info for the request.
 getDisplayInfo should return null if the virtual path does not exist. For an example implementation, see DefaultDisplayMode.
 DisplayModeId is used to cache the non-null result of a call to getDisplayInfo and should be unique for each Display Mode. See
 DisplayModes for the built-in Display Modes and their ids.
*/
public interface IDisplayMode
{
	String getDisplayModeId();
	boolean canHandleContext(HostContext httpContext);
	DisplayInfo getDisplayInfo(HostContext httpContext, String virtualPath, Predicate<String> virtualPathExists);
}