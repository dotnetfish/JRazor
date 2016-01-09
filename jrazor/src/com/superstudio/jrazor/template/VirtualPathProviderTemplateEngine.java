package com.superstudio.jrazor.template;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.MvcResources;
import com.superstudio.commons.VirtualPathProvider;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.HostingEnvironment;
import com.superstudio.jrazor.DefaultTemplateLocationCache;
import com.superstudio.jrazor.DisplayInfo;
import com.superstudio.jrazor.DisplayModeProvider;
import com.superstudio.jrazor.IDisplayMode;

public abstract class VirtualPathProviderTemplateEngine implements ITemplateEngine {
	// format is
	// ":TemplateCacheEntry:{cacheType}:{prefix}:{name}:{controllerName}:{areaName}:"
	private static final String CacheKeyFormat = ":TemplateCacheEntry:%s:%s:%s:%s:%s:";
	private static final String CacheKeyPrefixMaster = "Master";
	private static final String CacheKeyPrefixPartial = "Partial";
	private static final String CacheKeyPrefixTemplate = "Template";
	private static final String[] _emptyLocations = new String[0];
	private DisplayModeProvider _displayModeProvider;

	private Supplier<VirtualPathProvider> _vppFunc = () -> HostingEnvironment.getVirtualPathProvider();
	public Function<String, String> GetExtensionThunk = (t) -> com.superstudio.commons.VirtualPathUtility.GetExtension(t);
	private ITemplateLocationCache _viewLocationCache;

	private String[] areaMasterLocationFormats;

	public final String[] getAreaMasterLocationFormats() {
		return areaMasterLocationFormats;
	}

	public final void setAreaMasterLocationFormats(String[] value) {
		areaMasterLocationFormats = value;
	}

	private String[] areaPartialTemplateLocationFormats;

	public final String[] getAreaPartialTemplateLocationFormats() {
		return areaPartialTemplateLocationFormats;
	}

	public final void setAreaPartialTemplateLocationFormats(String[] value) {
		areaPartialTemplateLocationFormats = value;
	}

	private String[] areaTemplateLocationFormats;

	public final String[] getAreaTemplateLocationFormats() {
		return areaTemplateLocationFormats;
	}

	public final void setAreaTemplateLocationFormats(String[] value) {
		areaTemplateLocationFormats = value;
	}

	private List<String> fileExtensions;

	public final List<String> getFileExtensions() {
		return fileExtensions;
	}

	public final void setFileExtensions(List<String> value) {
		fileExtensions = value;
	}

	private String[] masterLocationFormats;

	public final String[] getMasterLocationFormats() {
		return masterLocationFormats;
	}

	public final void setMasterLocationFormats(String[] value) {
		masterLocationFormats = value;
	}

	private String[] partialTemplateLocationFormats;

	public final String[] getPartialTemplateLocationFormats() {
		return partialTemplateLocationFormats;
	}

	public final void setPartialTemplateLocationFormats(String[] value) {
		partialTemplateLocationFormats = value;
	}

	// Neither DefaultTemplateLocationCache.Null nor a DefaultTemplateLocationCache
	// instance maintain internal state. Fine
	// if multiple threads race to initialize _viewLocationCache.
	public final ITemplateLocationCache getTemplateLocationCache() {
		if (_viewLocationCache == null) {
			try {
				
					_viewLocationCache = new DefaultTemplateLocationCache();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return _viewLocationCache;
	}

	public final void setTemplateLocationCache(ITemplateLocationCache value) {
		if (value == null) {
			// throw Error.ArgumentNull("value");
		}

		_viewLocationCache = value;
	}

	private String[] templateLocationFormats;

	public final String[] getTemplateLocationFormats() {
		return templateLocationFormats;
	}

	public final void setTemplateLocationFormats(String[] value) {
		templateLocationFormats = value;
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

	// Provided for testing only; setter used in BuildManagerTemplateEngine but only
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

	public String createCacheKey(String prefix, String name, String controllerName, String areaName) {
		//TODO it does not applicable for the c# string format {0}
		return String.format(CacheKeyFormat, this.getClass().getSigners(), prefix, name, controllerName, areaName);
	}

	public static String appendDisplayModeToCacheKey(String cacheKey, String displayMode) {
		// key format is
		// ":TemplateCacheEntry:{cacheType}:{prefix}:{name}:{controllerName}:{areaName}:"
		// so append "{displayMode}:" to the key
		return cacheKey + displayMode + ":";
	}

	protected abstract ITemplate createPartialTemplate(TemplateContext templateHostContext, String partialPath) throws Exception;

	protected abstract ITemplate createTemplate(TemplateContext templateContext, String viewPath, String masterPath) throws Exception;

	protected boolean fileExists(TemplateHostContext templateHostContext, String virtualPath) {
		return getVirtualPathProvider().fileExists(virtualPath);
	}

	public TemplateEngineResult findPartialTemplate(TemplateContext templateContext, String partialTemplateName,
			boolean useCache) throws Exception {
		if (templateContext == null) {
			throw new IllegalArgumentException("templateContext");
		}
		if (StringHelper.isNullOrEmpty(partialTemplateName)) {
			throw new IllegalArgumentException(MvcResources.Common_NullOrEmpty + "partialTemplateName");
		}

		List<String> searched=new ArrayList<String>();
		String moduleName = templateContext.getRouteData().GetRequiredString("module");
		//String path=templateHostContext.mapPath();
		RefObject<String[]> tempRef_searched = new RefObject<String[]>(searched.toArray(new String[] {}));
		String partialPath = getPath(templateContext.getContext(),getPartialTemplateLocationFormats(),
				getAreaPartialTemplateLocationFormats(), "PartialTemplateLocationFormats", partialTemplateName, moduleName,
				CacheKeyPrefixPartial, useCache, tempRef_searched);
		searched = Arrays.asList(tempRef_searched.getRefObj());

		if (StringHelper.isNullOrEmpty(partialPath)) {
			return new TemplateEngineResult(searched);
		}

		return new TemplateEngineResult(createPartialTemplate(templateContext, partialPath), this);
	}

	public TemplateEngineResult findTemplate(TemplateContext templateContext, String viewName, String masterName,
			boolean useCache) throws Exception {
		if (templateContext == null) {
			throw new IllegalArgumentException("templateHostContext");
		}
		if (StringHelper.isNullOrEmpty(viewName)) {
			throw new IllegalArgumentException(MvcResources.Common_NullOrEmpty + "viewName");
		}

		String[] viewLocationsSearched=null;
		String[] masterLocationsSearched=null;

		String controllerName = templateContext.getRouteData().GetRequiredString("module");
		RefObject<String[]> tempRef_viewLocationsSearched = new RefObject<String[]>(viewLocationsSearched);
		String viewPath = getPath(templateContext.getContext(), getTemplateLocationFormats(), getAreaTemplateLocationFormats(),
				"TemplateLocationFormats", viewName, controllerName, CacheKeyPrefixTemplate, useCache,
				tempRef_viewLocationsSearched);
		viewLocationsSearched = tempRef_viewLocationsSearched.getRefObj();
		RefObject<String[]> tempRef_masterLocationsSearched = new RefObject<String[]>(masterLocationsSearched);
		String masterPath = getPath(templateHostContext, getMasterLocationFormats(), getAreaMasterLocationFormats(),
				"MasterLocationFormats", masterName, controllerName, CacheKeyPrefixMaster, useCache,
				tempRef_masterLocationsSearched);
		masterLocationsSearched = tempRef_masterLocationsSearched.getRefObj();

		if (StringHelper.isNullOrEmpty(viewPath)
				|| (StringHelper.isNullOrEmpty(masterPath) && !StringHelper.isNullOrEmpty(masterName))) {
			List<String> strs = CollectionHelper.union(viewLocationsSearched, masterLocationsSearched);
			return new TemplateEngineResult(strs);
		}

		return new TemplateEngineResult(createTemplate(templateHostContext, viewPath, masterPath), this);
	}

	private String getPath(TemplateHostContext templateHostContext, String[] locations, String[] areaLocations,
			String locationsPropertyName, String name, String controllerName, String cacheKeyPrefix, boolean useCache,
			RefObject<String[]> searchedLocations) {
		searchedLocations.setRefObj(_emptyLocations);// = _emptyLocations;

		if (StringHelper.isNullOrEmpty(name)) {
			return "";
		}

		String areaName = templateHostContext.getRouteData().tryGetValue("area", "");// AreaHelpers.GetAreaName(templateHostContext.getRouteData());

		boolean usingAreas = !StringHelper.isNullOrEmpty(areaName);
		ArrayList<TemplateLocation> viewLocations = getTemplateLocations(locations, (usingAreas) ? areaLocations : null);

		if (viewLocations.isEmpty()) {
			throw new IllegalStateException(String.format(CultureInfo.CurrentCulture,
					MvcResources.Common_PropertyCannotBeNullOrEmpty, locationsPropertyName));
		}

		boolean nameRepresentsPath = isSpecificPath(name);
		String cacheKey = createCacheKey(cacheKeyPrefix, name, (nameRepresentsPath) ? "" : controllerName, areaName);

		if (useCache) {
			// Only look at cached display modes that can handle the context.
			Iterable<IDisplayMode> possibleDisplayModes = getDisplayModeProvider().getAvailableDisplayModesForContext(
					templateHostContext.getContext(), templateHostContext.getDisplayMode());
			for (IDisplayMode displayMode : possibleDisplayModes) {
				String cachedLocation = getTemplateLocationCache().GetTemplateLocation(templateHostContext.getContext(),
						appendDisplayModeToCacheKey(cacheKey, displayMode.getDisplayModeId()));

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
					: getPathFromGeneralName(templateHostContext, viewLocations, name, controllerName, areaName, cacheKey,
							searchedLocations);
		}
	}

	private String getPathFromGeneralName(TemplateHostContext templateHostContext, ArrayList<TemplateLocation> locations,
			String name, String controllerName, String areaName, String cacheKey,
			RefObject<String[]> searchedLocations) {
		String result = "";
		searchedLocations.setRefObj(new String[locations.size()]);

		for (int i = 0; i < locations.size(); i++) {
			TemplateLocation location = locations.get(i);
			String virtualPath = location.format(name, controllerName, areaName);
			DisplayInfo virtualPathDisplayInfo = getDisplayModeProvider().getDisplayInfoForVirtualPath(virtualPath,
					templateHostContext, path -> fileExists(templateHostContext, path),
					templateHostContext.getDisplayMode());

			if (virtualPathDisplayInfo != null) {
				String resolvedVirtualPath = virtualPathDisplayInfo.getFilePath();

				searchedLocations.setRefObj(_emptyLocations);
				result = resolvedVirtualPath;
				getTemplateLocationCache().InsertTemplateLocation(templateHostContext.getContext(),
						appendDisplayModeToCacheKey(cacheKey,
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
						DisplayInfo displayInfoToCache = displayMode.GetDisplayInfo(templateHostContext.getContext(),
								virtualPath, path -> FileExists(templateHostContext, path));

						String cacheValue = "";
						if (displayInfoToCache != null && displayInfoToCache.getFilePath() != null) {
							cacheValue = displayInfoToCache.getFilePath();
						}
						getTemplateLocationCache().InsertTemplateLocation(templateHostContext.getContext(),
								appendDisplayModeToCacheKey(cacheKey, displayMode.getDisplayModeId()), cacheValue);
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

		if (!(filePathIsSupported(name) && fileExists(templateHostContext, name))) {
			result = "";
			searchedLocations.setRefObj(new String[] { name });
		}

		getTemplateLocationCache().InsertTemplateLocation(templateHostContext.getContext(), cacheKey, result);
		return result;
	}

	private boolean filePathIsSupported(String virtualPath) {
		if (getFileExtensions() == null) {
			// legacy behavior for custom TemplateEngine that might not set the
			// FileExtensions property
			return true;
		} else {
			// get rid of the '.' because the FileExtensions property expects
			// extensions withouth a dot.
			String extension = StringHelper.trimStart(GetExtensionThunk.apply(virtualPath), '.');// .TrimStart('.');
			return getFileExtensions().contains(extension);
		}
	}

	private static ArrayList<TemplateLocation> getTemplateLocations(String[] viewLocationFormats,
			String[] areaTemplateLocationFormats) {
		ArrayList<TemplateLocation> allLocations = new ArrayList<TemplateLocation>();

		if (areaTemplateLocationFormats != null) {
			for (String areaTemplateLocationFormat : areaTemplateLocationFormats) {
				allLocations.add(new AreaAwareTemplateLocation(areaTemplateLocationFormat));
			}
		}

		if (viewLocationFormats != null) {
			for (String viewLocationFormat : viewLocationFormats) {
				allLocations.add(new TemplateLocation(viewLocationFormat));
			}
		}

		return allLocations;
	}

	private static boolean isSpecificPath(String name) {
		char c = name.charAt(0);
		return (c == '~' || c == '/');
	}

	@Override
	public void releaseTemplate(TemplateContext templateContext, ITemplate view) throws IOException {
		java.io.Closeable disposable = (java.io.Closeable) ((view instanceof java.io.Closeable) ? view : null);
		if (disposable != null) {
			disposable.close();
		}
	}

	private static class AreaAwareTemplateLocation extends TemplateLocation {
		public AreaAwareTemplateLocation(String virtualPathFormatString) {
			super(virtualPathFormatString);
		}

		@Override
		public String format(String viewName, String controllerName, String areaName) {
			return StringHelper.format(CultureInfo.InvariantCulture, _virtualPathFormatString,new Object[]{ viewName, controllerName,
					areaName});
		}
	}

	private static class TemplateLocation {
		protected String _virtualPathFormatString;

		public TemplateLocation(String virtualPathFormatString) {
			_virtualPathFormatString = virtualPathFormatString;
		}

		public String format(String viewName, String controllerName, String areaName) {
			return StringHelper.format(CultureInfo.InvariantCulture, _virtualPathFormatString, viewName, controllerName);
		}
	}
}