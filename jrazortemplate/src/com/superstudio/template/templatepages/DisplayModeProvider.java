package com.superstudio.template.templatepages;

import com.superstudio.template.mvc.context.HostContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;


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
		mobile.setContextCondition((c) ->{
			return c.isMobileDevice();
		});

		_displayModes = new ArrayList<IDisplayMode>(
				Arrays.asList(new IDisplayMode[] {  new DefaultDisplayMode(),mobile }));
		// The type is a psuedo-singleton. A user would gain nothing from
		// constructing it since we won't use anything but
		// DisplayModeProvider.Instance internally.
	}

	/**
	 * Restricts the search for Display Info to Display Modes either equal to or
	 * following the current Display Mode in Modes. For example, a page being
	 * rendered in the getDefault Display Mode will not display Mobile partial
	 * templates in order to achieve a consistent look and feel.
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
	 * all Display Modes that are available to handle a request.
	 */
	public List<IDisplayMode> getModes() {
		return _displayModes;
	}

	private int findFirstAvailableDisplayMode(IDisplayMode currentDisplayMode, boolean requireConsistentDisplayMode) {
		if (requireConsistentDisplayMode && currentDisplayMode != null) {
			int first = _displayModes.indexOf(currentDisplayMode);
			return (first >= 0) ? first : _displayModes.size();
		}
		return 0;
	}

	/**
	 * Returns any IDisplayMode that can handle the given request.
	 */
	public Iterable<IDisplayMode> getAvailableDisplayModesForContext(HostContext httpContext,
																	 IDisplayMode currentDisplayMode) {
		return getAvailableDisplayModesForContext(httpContext, currentDisplayMode, getRequireConsistentDisplayMode());
	}

	public Iterable<IDisplayMode> getAvailableDisplayModesForContext(HostContext httpContext, IDisplayMode currentDisplayMode, boolean requireConsistentDisplayMode)
	{
		final int first = findFirstAvailableDisplayMode(currentDisplayMode, requireConsistentDisplayMode);

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
						while(index<len && !mode.canHandleContext(httpContext)){
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
	public DisplayInfo getDisplayInfoForVirtualPath(String virtualPath, HostContext httpContext,
													Predicate<String> virtualPathExists, IDisplayMode currentDisplayMode) {
		return getDisplayInfoForVirtualPath(virtualPath, httpContext, virtualPathExists, currentDisplayMode,
				getRequireConsistentDisplayMode());
	}

	public DisplayInfo getDisplayInfoForVirtualPath(String virtualPath, HostContext httpContext,
													Predicate<String> virtualPathExists, IDisplayMode currentDisplayMode,
													boolean requireConsistentDisplayMode) {
		// Performance sensitive
		int first = findFirstAvailableDisplayMode(currentDisplayMode, requireConsistentDisplayMode);
		for (int i = first; i < _displayModes.size(); i++) {
			IDisplayMode mode = _displayModes.get(i);
			if (mode.canHandleContext(httpContext)) {
				DisplayInfo info = mode.getDisplayInfo(httpContext, virtualPath, virtualPathExists);
				if (info != null) {
					return info;
				}
			}
		}
		return null;
	}

	public static IDisplayMode getDisplayMode(HostContext context) {
		return context != null ? (IDisplayMode) ((context.getItems().get(_displayModeKey) instanceof IDisplayMode)
				? context.getItems().get(_displayModeKey) : null) : null;
	}

	public static void SetDisplayMode(HostContext context, IDisplayMode displayMode) {
		if (context != null) {
			context.getItems().put(_displayModeKey,displayMode);
		}
	}
}