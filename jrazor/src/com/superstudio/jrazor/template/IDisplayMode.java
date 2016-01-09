package com.superstudio.jrazor.template;

import java.util.function.Predicate;

import com.superstudio.jrazor.templateEngine.DisplayInfo;
import com.superstudio.web.HttpContextBase;




/** 
 An interface that provides DisplayInfo for a virtual path and request. An IDisplayMode may modify the virtual path before checking
 if it exists. CanHandleContext is called to determine if the Display Mode is available to return display info for the request.
 GetDisplayInfo should return null if the virtual path does not exist. For an example implementation, see DefaultDisplayMode.
 DisplayModeId is used to cache the non-null result of a call to GetDisplayInfo and should be unique for each Display Mode. See
 DisplayModes for the built-in Display Modes and their ids.
*/
public interface IDisplayMode
{
	String getDisplayModeId();
	boolean CanHandleContext(HttpContextBase httpContext);
	DisplayInfo GetDisplayInfo(HttpContextBase httpContext, String virtualPath, Predicate<String> virtualPathExists);
}