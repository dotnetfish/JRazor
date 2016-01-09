package com.superstudio.jrazor.template;

import java.util.*;
import java.util.function.Predicate;

import com.superstudio.jrazor.templateEngine.DefaultDisplayMode;
import com.superstudio.jrazor.templateEngine.DisplayInfo;
import com.superstudio.web.HttpContextBase;



public final class DisplayModeProvider {
	public static final String MobileDisplayModeId = "Mobile";
	public static final String DefaultDisplayModeId = "";
	private static final Object _displayModeKey = new Object();
	private static final DisplayModeProvider _instance = new DisplayModeProvider();

	private final ArrayList<IDisplayMode> _displayModes;// = new
														// ArrayList<IDisplayMode>(Arrays.asList(new
														// IDisplayMode[]

	public DisplayModeProvider() {
		DefaultDisplayMode mobile = new DefaultDisplayMode(MobileDisplayModeId);
		mobile.setContextCondition((c) ->{ return c.GetOverriddenBrowser().isMobileDevice();});

		_displayModes = new ArrayList<IDisplayMode>(
				Arrays.asList(new IDisplayMode[] { mobile, new DefaultDisplayMode() }));
		// The type is a psuedo-singleton. A user would gain nothing from
		// constructing it since we won't use anything but
		// DisplayModeProvider.Instance internally.
	}

	/**
	 * Restricts the search for Display Info to Display Modes either equal to or
	 * following the current Display Mode in Modes. For example, a page being
	 * rendered in the Default Display Mode will not display Mobile partial
	 * views in order to achieve a consistent look and feel.
	 */
	private boolean RequireConsistentDisplayMode;

	public boolean getRequireConsistentDisplayMode() {
		return RequireConsistentDisplayMode;
	}

	public void setRequireConsistentDisplayMode(boolean value) {
		RequireConsistentDisplayMode = value;
	}

	public static DisplayModeProvider getInstance() {
		return _instance;
	}

	/**
	 * All Display Modes that are available to handle a request.
	 */
	public List<IDisplayMode> getModes() {
		return _displayModes;
	}

	private int FindFirstAvailableDisplayMode(IDisplayMode currentDisplayMode, boolean requireConsistentDisplayMode) {
		if (requireConsistentDisplayMode && currentDisplayMode != null) {
			int first = _displayModes.indexOf(currentDisplayMode);
			return (first >= 0) ? first : _displayModes.size();
		}
		return 0;
	}

	/**
	 * Returns any IDisplayMode that can handle the given request.
	 */
	public Iterable<IDisplayMode> GetAvailableDisplayModesForContext(HttpContextBase httpContext,
			IDisplayMode currentDisplayMode) {
		return GetAvailableDisplayModesForContext(httpContext, currentDisplayMode, getRequireConsistentDisplayMode());
	}

	public Iterable<IDisplayMode> GetAvailableDisplayModesForContext(HttpContextBase httpContext, IDisplayMode currentDisplayMode, boolean requireConsistentDisplayMode)
	{
		final int first = FindFirstAvailableDisplayMode(currentDisplayMode, requireConsistentDisplayMode);
		
		return new Iterable<IDisplayMode>(){

			@Override
			public Iterator<IDisplayMode> iterator() {
			
				 final int len=_displayModes.size();
				return new Iterator<IDisplayMode>(){
					private int index=first;
					@Override
					public boolean hasNext() {
						if(index>=len)return false;
						IDisplayMode mode = _displayModes.get(index);
						while(index<len && !mode.CanHandleContext(httpContext)){
							index++;
						}
						return index<len;
					}

					@Override
					public IDisplayMode next() {
						
						return _displayModes.get(index);
					}
					
				};
			}
			
		};
	}

	/**
	 * Returns DisplayInfo from the first IDisplayMode in Modes that can handle
	 * the given request and locate the virtual path. If currentDisplayMode is
	 * not null and RequireConsistentDisplayMode is set to true the search for
	 * DisplayInfo will only start with the currentDisplayMode.
	 */
	public DisplayInfo GetDisplayInfoForVirtualPath(String virtualPath, HttpContextBase httpContext,
			Predicate<String> virtualPathExists, IDisplayMode currentDisplayMode) {
		return GetDisplayInfoForVirtualPath(virtualPath, httpContext, virtualPathExists, currentDisplayMode,
				getRequireConsistentDisplayMode());
	}

	public DisplayInfo GetDisplayInfoForVirtualPath(String virtualPath, HttpContextBase httpContext,
			Predicate<String> virtualPathExists, IDisplayMode currentDisplayMode,
			boolean requireConsistentDisplayMode) {
		// Performance sensitive
		int first = FindFirstAvailableDisplayMode(currentDisplayMode, requireConsistentDisplayMode);
		for (int i = first; i < _displayModes.size(); i++) {
			IDisplayMode mode = _displayModes.get(i);
			if (mode.CanHandleContext(httpContext)) {
				DisplayInfo info = mode.GetDisplayInfo(httpContext, virtualPath, virtualPathExists);
				if (info != null) {
					return info;
				}
			}
		}
		return null;
	}

	public static IDisplayMode GetDisplayMode(HttpContextBase context) {
		return context != null ? (IDisplayMode) ((context.getItems().get(_displayModeKey) instanceof IDisplayMode)
				? context.getItems().get(_displayModeKey) : null) : null;
	}

	public static void SetDisplayMode(HttpContextBase context, IDisplayMode displayMode) {
		if (context != null) {
			context.getItems().put(_displayModeKey,displayMode);
		}
	}
}