package com.superstudio.jrazor.template;

import com.superstudio.commons.*;
import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.jrazor.templateEngine.DisplayInfo;
import com.superstudio.jrazor.templateEngine.TemplateHostContext;
import com.superstudio.web.HttpContext;
import com.superstudio.web.mvc.IView;
import com.superstudio.web.mvc.actionResult.IViewEngine;
import com.superstudio.web.mvc.actionResult.ViewEngineResult;
import com.superstudio.web.webpages.DisplayModeProvider;
import com.superstudio.web.webpages.IDisplayMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class VirtualPathProviderViewEngine implements IViewEngine {
	// format is
	// ":ViewCacheEntry:{cacheType}:{prefix}:{name}:{controllerName}:{areaName}:"
	private static final String CacheKeyFormat = ":ViewCacheEntry:%s:%s:%s:%s:%s:";
	private static final String CacheKeyPrefixMaster = "Master";
	private static final String CacheKeyPrefixPartial = "Partial";
	private static final String CacheKeyPrefixView = "View";
	private static final String[] _emptyLocations = new String[0];
	private DisplayModeProvider _displayModeProvider;

	private Supplier<VirtualPathProvider> _vppFunc = () -> HostingEnvironment.getVirtualPathProvider();
	public Function<String, String> GetExtensionThunk = (t) -> VirtualPathUtility.GetExtension(t);
	private IViewLocationCache _viewLocationCache;

	private String[] AreaMasterLocationFormats;

	public final String[] getAreaMasterLocationFormats() {
		return AreaMasterLocationFormats;
	}

	public final void setAreaMasterLocationFormats(String[] value) {
		AreaMasterLocationFormats = value;
	}

	private String[] AreaPartialViewLocationFormats;

	public final String[] getAreaPartialViewLocationFormats() {
		return AreaPartialViewLocationFormats;
	}

	public final void setAreaPartialViewLocationFormats(String[] value) {
		AreaPartialViewLocationFormats = value;
	}

	private String[] AreaViewLocationFormats;

	public final String[] getAreaViewLocationFormats() {
		return AreaViewLocationFormats;
	}

	public final void setAreaViewLocationFormats(String[] value) {
		AreaViewLocationFormats = value;
	}

	private List<String> fileExtensions;

	public final List<String> getFileExtensions() {
		return fileExtensions;
	}

	public final void setFileExtensions(List<String> value) {
		fileExtensions = value;
	}

	private String[] MasterLocationFormats;

	public final String[] getMasterLocationFormats() {
		return MasterLocationFormats;
	}

	public final void setMasterLocationFormats(String[] value) {
		MasterLocationFormats = value;
	}

	private String[] PartialViewLocationFormats;

	public final String[] getPartialViewLocationFormats() {
		return PartialViewLocationFormats;
	}

	public final void setPartialViewLocationFormats(String[] value) {
		PartialViewLocationFormats = value;
	}

	// Neither DefaultViewLocationCache.Null nor a DefaultViewLocationCache
	// instance maintain internal state. Fine
	// if multiple threads race to initialize _viewLocationCache.
	public final IViewLocationCache getViewLocationCache() {
		if (_viewLocationCache == null) {
			try {
				if (HttpContext.getCurrent() == null || HttpContext.getCurrent().IsDebuggingEnabled()) {
					_viewLocationCache = DefaultViewLocationCache.Null;
				} else {
					_viewLocationCache = new DefaultViewLocationCache();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return _viewLocationCache;
	}

	public final void setViewLocationCache(IViewLocationCache value) {
		if (value == null) {
			// throw Error.ArgumentNull("value");
		}

		_viewLocationCache = value;
	}

	private String[] ViewLocationFormats;

	public final String[] getViewLocationFormats() {
		return ViewLocationFormats;
	}

	public final void setViewLocationFormats(String[] value) {
		ViewLocationFormats = value;
	}

	// Likely exists for testing only
	protected final VirtualPathProvider getVirtualPathProvider() {
		return _vppFunc.get();
	}

	protected final void setVirtualPathProvider(VirtualPathProvider value) {
		if (value == null) {
			// throw Error.ArgumentNull("value");
		}

		_vppFunc = () -> {
			return value;
		};
	}

	// Provided for testing only; setter used in BuildManagerViewEngine but only
	// for test scenarios
	public final Supplier<VirtualPathProvider> getVirtualPathProviderFunc() {
		return _vppFunc;
	}

	public final void setVirtualPathProviderFunc(Supplier<VirtualPathProvider> value) {
		if (value == null) {
			// throw Error.ArgumentNull("value");
		}

		_vppFunc = value;
	}

	protected final DisplayModeProvider getDisplayModeProvider() {
		return (_displayModeProvider != null) ? _displayModeProvider : DisplayModeProvider.getInstance();
	}

	protected final void setDisplayModeProvider(DisplayModeProvider value) {
		_displayModeProvider = value;
	}

	public String CreateCacheKey(String prefix, String name, String controllerName, String areaName) {
		//TODO it does not applicable for the c# string format {0}
		return String.format(CacheKeyFormat, this.getClass().getSigners(), prefix, name, controllerName, areaName);
	}

	public static String AppendDisplayModeToCacheKey(String cacheKey, String displayMode) {
		// key format is
		// ":ViewCacheEntry:{cacheType}:{prefix}:{name}:{controllerName}:{areaName}:"
		// so append "{displayMode}:" to the key
		return cacheKey + displayMode + ":";
	}

	protected abstract IView CreatePartialView(TemplateHostContext templateHostContext, String partialPath) throws Exception;

	protected abstract IView CreateView(TemplateHostContext templateHostContext, String viewPath, String masterPath) throws Exception;

	protected boolean FileExists(TemplateHostContext templateHostContext, String virtualPath) {
		return getVirtualPathProvider().fileExists(virtualPath);
	}

	public ViewEngineResult FindPartialView(TemplateHostContext templateHostContext, String partialViewName,
			boolean useCache) throws Exception {
		if (templateHostContext == null) {
			throw new IllegalArgumentException("templateHostContext");
		}
		if (StringHelper.isNullOrEmpty(partialViewName)) {
			throw new IllegalArgumentException(MvcResources.Common_NullOrEmpty + "partialViewName");
		}

		List<String> searched=new ArrayList<String>();
		String controllerName = templateHostContext.getRouteData().getRequiredString("controller");
		RefObject<String[]> tempRef_searched = new RefObject<String[]>(searched.toArray(new String[] {}));
		String partialPath = GetPath(templateHostContext, getPartialViewLocationFormats(),
				getAreaPartialViewLocationFormats(), "PartialViewLocationFormats", partialViewName, controllerName,
				CacheKeyPrefixPartial, useCache, tempRef_searched);
		searched = Arrays.asList(tempRef_searched.getRefObj());

		if (StringHelper.isNullOrEmpty(partialPath)) {
			return new ViewEngineResult(searched);
		}

		return new ViewEngineResult(CreatePartialView(templateHostContext, partialPath), this);
	}

	public ViewEngineResult FindView(TemplateHostContext templateHostContext, String viewName, String masterName,
			boolean useCache) throws Exception {
		if (templateHostContext == null) {
			throw new IllegalArgumentException("templateHostContext");
		}
		if (StringHelper.isNullOrEmpty(viewName)) {
			throw new IllegalArgumentException(MvcResources.Common_NullOrEmpty + "viewName");
		}

		String[] viewLocationsSearched=null;
		String[] masterLocationsSearched=null;

		String controllerName = templateHostContext.getRouteData().getRequiredString("controller");
		RefObject<String[]> tempRef_viewLocationsSearched = new RefObject<String[]>(viewLocationsSearched);
		String viewPath = GetPath(templateHostContext, getViewLocationFormats(), getAreaViewLocationFormats(),
				"ViewLocationFormats", viewName, controllerName, CacheKeyPrefixView, useCache,
				tempRef_viewLocationsSearched);
		viewLocationsSearched = tempRef_viewLocationsSearched.getRefObj();
		RefObject<String[]> tempRef_masterLocationsSearched = new RefObject<String[]>(masterLocationsSearched);
		String masterPath = GetPath(templateHostContext, getMasterLocationFormats(), getAreaMasterLocationFormats(),
				"MasterLocationFormats", masterName, controllerName, CacheKeyPrefixMaster, useCache,
				tempRef_masterLocationsSearched);
		masterLocationsSearched = tempRef_masterLocationsSearched.getRefObj();

		if (StringHelper.isNullOrEmpty(viewPath)
				|| (StringHelper.isNullOrEmpty(masterPath) && !StringHelper.isNullOrEmpty(masterName))) {
			List<String> strs = CollectionHelper.union(viewLocationsSearched, masterLocationsSearched);
			return new ViewEngineResult(strs);
		}

		return new ViewEngineResult(CreateView(templateHostContext, viewPath, masterPath), this);
	}

	private String GetPath(TemplateHostContext templateHostContext, String[] locations, String[] areaLocations,
			String locationsPropertyName, String name, String controllerName, String cacheKeyPrefix, boolean useCache,
			RefObject<String[]> searchedLocations) {
		searchedLocations.setRefObj(_emptyLocations);// = _emptyLocations;

		if (StringHelper.isNullOrEmpty(name)) {
			return "";
		}

		String areaName = templateHostContext.getRouteData().tryGetValue("area", "");// AreaHelpers.GetAreaName(templateHostContext.getRouteData());

		boolean usingAreas = !StringHelper.isNullOrEmpty(areaName);
		ArrayList<ViewLocation> viewLocations = GetViewLocations(locations, (usingAreas) ? areaLocations : null);

		if (viewLocations.isEmpty()) {
			throw new IllegalStateException(String.format(CultureInfo.CurrentCulture,
					MvcResources.Common_PropertyCannotBeNullOrEmpty, locationsPropertyName));
		}

		boolean nameRepresentsPath = IsSpecificPath(name);
		String cacheKey = CreateCacheKey(cacheKeyPrefix, name, (nameRepresentsPath) ? "" : controllerName, areaName);

		if (useCache) {
			// Only look at cached display modes that can handle the context.
			Iterable<IDisplayMode> possibleDisplayModes = getDisplayModeProvider().GetAvailableDisplayModesForContext(
					templateHostContext.getHttpContext(), templateHostContext.getDisplayMode());
			for (IDisplayMode displayMode : possibleDisplayModes) {
				String cachedLocation = getViewLocationCache().getViewLocation(templateHostContext.getHttpContext(),
						AppendDisplayModeToCacheKey(cacheKey, displayMode.getDisplayModeId()));

				if (cachedLocation == null) {
					// If any matching display mode location is not in the
					// cache, fall back to the uncached behavior, which will
					// repopulate all of our caches.
					return null;
				}

				// A non-empty cachedLocation indicates that we have a matching
				// file on disk. Return that result.
				if (cachedLocation.length() > 0) {
					if (templateHostContext.getDisplayMode() == null) {
						templateHostContext.setDisplayMode(displayMode);
					}

					return cachedLocation;
				}
				// An empty cachedLocation value indicates that we don't have a
				// matching file on disk. Keep going down the list of possible
				// display modes.
			}

			// GetPath is called again without using the cache.
			return null;
		} else {
			return nameRepresentsPath ? GetPathFromSpecificName(templateHostContext, name, cacheKey, searchedLocations)
					: GetPathFromGeneralName(templateHostContext, viewLocations, name, controllerName, areaName, cacheKey,
							searchedLocations);
		}
	}

	private String GetPathFromGeneralName(TemplateHostContext templateHostContext, ArrayList<ViewLocation> locations,
			String name, String controllerName, String areaName, String cacheKey,
			RefObject<String[]> searchedLocations) {
		String result = "";
		searchedLocations.setRefObj(new String[locations.size()]);

		for (int i = 0; i < locations.size(); i++) {
			ViewLocation location = locations.get(i);
			String virtualPath = location.format(name, controllerName, areaName);
			DisplayInfo virtualPathDisplayInfo = getDisplayModeProvider().GetDisplayInfoForVirtualPath(virtualPath,
					templateHostContext.getHttpContext(), path -> FileExists(templateHostContext, path),
					templateHostContext.getDisplayMode());

			if (virtualPathDisplayInfo != null) {
				String resolvedVirtualPath = virtualPathDisplayInfo.getFilePath();

				searchedLocations.setRefObj(_emptyLocations);
				result = resolvedVirtualPath;
				getViewLocationCache().insertViewLocation(templateHostContext.getHttpContext(),
						AppendDisplayModeToCacheKey(cacheKey,
								virtualPathDisplayInfo.getDisplayMode().getDisplayModeId()),
						result);

				if (templateHostContext.getDisplayMode() == null) {
					templateHostContext.setDisplayMode(virtualPathDisplayInfo.getDisplayMode());
				}

				// Populate the cache for all other display modes. We want to
				// cache both file system hits and misses so that we can
				// distinguish
				// in future requests whether a file's status was evicted from
				// the cache (null value) or if the file doesn't exist (empty
				// string).
				Iterable<IDisplayMode> allDisplayModes = getDisplayModeProvider().getModes();
				for (IDisplayMode displayMode : allDisplayModes) {
					if (displayMode.getDisplayModeId() != virtualPathDisplayInfo.getDisplayMode().getDisplayModeId()) {
						DisplayInfo displayInfoToCache = displayMode.GetDisplayInfo(templateHostContext.getHttpContext(),
								virtualPath, path -> FileExists(templateHostContext, path));

						String cacheValue = "";
						if (displayInfoToCache != null && displayInfoToCache.getFilePath() != null) {
							cacheValue = displayInfoToCache.getFilePath();
						}
						getViewLocationCache().InsertViewLocation(templateHostContext.getHttpContext(),
								AppendDisplayModeToCacheKey(cacheKey, displayMode.getDisplayModeId()), cacheValue);
					}
				}
				break;
			}

			searchedLocations.getRefObj()[i] = virtualPath;
		}

		return result;
	}

	private String GetPathFromSpecificName(TemplateHostContext templateHostContext, String name, String cacheKey,
			RefObject<String[]> searchedLocations) {
		String result = name;

		if (!(FilePathIsSupported(name) && FileExists(templateHostContext, name))) {
			result = "";
			searchedLocations.setRefObj(new String[] { name });
		}

		getViewLocationCache().InsertViewLocation(templateHostContext.getHttpContext(), cacheKey, result);
		return result;
	}

	private boolean FilePathIsSupported(String virtualPath) {
		if (getFileExtensions() == null) {
			// legacy behavior for custom ViewEngine that might not set the
			// FileExtensions property
			return true;
		} else {
			// get rid of the '.' because the FileExtensions property expects
			// extensions withouth a dot.
			String extension = StringHelper.trimStart(GetExtensionThunk.apply(virtualPath), '.');// .TrimStart('.');
			return getFileExtensions().contains(extension);
		}
	}

	private static ArrayList<ViewLocation> GetViewLocations(String[] viewLocationFormats,
			String[] areaViewLocationFormats) {
		ArrayList<ViewLocation> allLocations = new ArrayList<ViewLocation>();

		if (areaViewLocationFormats != null) {
			for (String areaViewLocationFormat : areaViewLocationFormats) {
				allLocations.add(new AreaAwareViewLocation(areaViewLocationFormat));
			}
		}

		if (viewLocationFormats != null) {
			for (String viewLocationFormat : viewLocationFormats) {
				allLocations.add(new ViewLocation(viewLocationFormat));
			}
		}

		return allLocations;
	}

	private static boolean IsSpecificPath(String name) {
		char c = name.charAt(0);
		return (c == '~' || c == '/');
	}

	@Override
	public void releaseView(TemplateHostContext templateHostContext, IView view) throws IOException {
		java.io.Closeable disposable = (java.io.Closeable) ((view instanceof java.io.Closeable) ? view : null);
		if (disposable != null) {
			disposable.close();
		}
	}

	private static class AreaAwareViewLocation extends ViewLocation {
		public AreaAwareViewLocation(String virtualPathFormatString) {
			super(virtualPathFormatString);
		}

		@Override
		public String format(String viewName, String controllerName, String areaName) {
			return StringHelper.format(CultureInfo.InvariantCulture, _virtualPathFormatString,new Object[]{ viewName, controllerName,
					areaName});
		}
	}

	private static class ViewLocation {
		protected String _virtualPathFormatString;

		public ViewLocation(String virtualPathFormatString) {
			_virtualPathFormatString = virtualPathFormatString;
		}

		public String format(String viewName, String controllerName, String areaName) {
			return StringHelper.format(CultureInfo.InvariantCulture, _virtualPathFormatString, viewName, controllerName);
		}
	}
}