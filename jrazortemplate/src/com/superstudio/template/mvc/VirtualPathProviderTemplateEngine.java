package com.superstudio.template.mvc;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.MvcResources;
import com.superstudio.commons.VirtualPathProvider;
import com.superstudio.commons.VirtualPathUtility;
import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.web.HttpContext;
import com.superstudio.template.mvc.actionresult.ITemplateEngine;
import com.superstudio.template.mvc.actionresult.TemplateEngineResult;
import com.superstudio.template.mvc.context.RenderContext;
import com.superstudio.template.templatepages.DisplayInfo;
import com.superstudio.template.templatepages.DisplayModeProvider;
import com.superstudio.template.templatepages.IDisplayMode;
import com.superstudio.commons.HostingEnvironment;

public abstract class VirtualPathProviderTemplateEngine implements ITemplateEngine {
	// format is
	// ":TemplateCacheEntry:{cacheType}:{prefix}:{name}:{controllerName}:{areaName}:"
	private static final String CacheKeyFormat = ":TemplateCacheEntry:%s:%s:%s:%s:%s:";
	private static final String CacheKeyPrefixMaster = "Master";
	private static final String CacheKeyPrefixPartial = "Partial";
	private static final String CacheKeyPrefixTemplate = "template";
	private static final String[] _emptyLocations = new String[0];
	private DisplayModeProvider _displayModeProvider;

	private Supplier<VirtualPathProvider> _vppFunc = () -> HostingEnvironment.getVirtualPathProvider();
	public Function<String, String> GetExtensionThunk = (t) -> VirtualPathUtility.GetExtension(t);
	private ITemplateLocationCache _templateLocationCache;

	private String[] AreaMasterLocationFormats;

	public final String[] getAreaMasterLocationFormats() {
		return AreaMasterLocationFormats;
	}

	public final void setAreaMasterLocationFormats(String[] value) {
		AreaMasterLocationFormats = value;
	}

	private String[] AreaPartialTemplateLocationFormats;

	public final String[] getAreaPartialTemplateLocationFormats() {
		return AreaPartialTemplateLocationFormats;
	}

	public final void setAreaPartialTemplateLocationFormats(String[] value) {
		AreaPartialTemplateLocationFormats = value;
	}

	private String[] AreaTemplateLocationFormats;

	public final String[] getAreaTemplateLocationFormats() {
		return AreaTemplateLocationFormats;
	}

	public final void setAreaTemplateLocationFormats(String[] value) {
		AreaTemplateLocationFormats = value;
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

	private String[] PartialTemplateLocationFormats;

	public final String[] getPartialTemplateLocationFormats() {
		return PartialTemplateLocationFormats;
	}

	public final void setPartialTemplateLocationFormats(String[] value) {
		PartialTemplateLocationFormats = value;
	}

	// Neither DefaultTemplateLocationCache.Null nor a DefaultTemplateLocationCache
	// instance maintain internal state. Fine
	// if multiple threads race to initialize _templateLocationCache.
	public final ITemplateLocationCache getTemplateLocationCache() {
		if (_templateLocationCache == null) {
			try {
				if (HttpContext.getCurrent() == null || HttpContext.getCurrent().isDebuggingEnabled()) {
					_templateLocationCache = DefaultTemplateLocationCache.Null;
				} else {
					_templateLocationCache = new DefaultTemplateLocationCache();
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return _templateLocationCache;
	}

	public final void setTemplateLocationCache(ITemplateLocationCache value) {
		if (value == null) {
			// throw Error.ArgumentNull("value");
		}

		_templateLocationCache = value;
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

	protected abstract ITemplate createPartialTemplate(RenderContext renderContext, String partialPath) throws Exception;

	protected abstract ITemplate createTemplate(RenderContext renderContext, String templatePath, String masterPath) throws Exception;

	protected boolean fileExists(RenderContext renderContext, String virtualPath) {
		return getVirtualPathProvider().fileExists(virtualPath);
	}

	public TemplateEngineResult findPartialTemplate(RenderContext renderContext, String partialTemplateName,
													boolean useCache) throws Exception {
		if (renderContext == null) {
			throw new IllegalArgumentException("renderContext");
		}
		if (StringHelper.isNullOrEmpty(partialTemplateName)) {
			throw new IllegalArgumentException(MvcResources.Common_NullOrEmpty + "partialTemplateName");
		}

		List<String> searched=new ArrayList<String>();
		String controllerName = renderContext.getTemplateInfo().getTemplateCategory();
		RefObject<String[]> tempRef_searched = new RefObject<String[]>(searched.toArray(new String[] {}));
		String partialPath = getPath(renderContext, getPartialTemplateLocationFormats(),
				getAreaPartialTemplateLocationFormats(), "PartialTemplateLocationFormats", partialTemplateName, controllerName,
				CacheKeyPrefixPartial, useCache, tempRef_searched);
		searched = Arrays.asList(tempRef_searched.getRefObj());

		if (StringHelper.isNullOrEmpty(partialPath)) {
			return new TemplateEngineResult(searched);
		}

		return new TemplateEngineResult(createPartialTemplate(renderContext, partialPath), this);
	}

	public TemplateEngineResult findTemplate(RenderContext renderContext, String templateName, String masterName,
											 boolean useCache) throws Exception {
		if (renderContext == null) {
			throw new IllegalArgumentException("renderContext");
		}
		if (StringHelper.isNullOrEmpty(templateName)) {
			throw new IllegalArgumentException(MvcResources.Common_NullOrEmpty + "templateName");
		}

		String[] templateLocationsSearched=null;
		String[] masterLocationsSearched=null;

		String controllerName = renderContext.getTemplateInfo().getTemplateCategory();
		RefObject<String[]> tempRef_templateLocationsSearched = new RefObject<String[]>(templateLocationsSearched);
		String templatePath = getPath(renderContext, getTemplateLocationFormats(), getAreaTemplateLocationFormats(),
				"templateLocationFormats", templateName, controllerName, CacheKeyPrefixTemplate, useCache,
				tempRef_templateLocationsSearched);
		templateLocationsSearched = tempRef_templateLocationsSearched.getRefObj();
		RefObject<String[]> tempRef_masterLocationsSearched = new RefObject<String[]>(masterLocationsSearched);
		String masterPath = getPath(renderContext, getMasterLocationFormats(), getAreaMasterLocationFormats(),
				"MasterLocationFormats", masterName, controllerName, CacheKeyPrefixMaster, useCache,
				tempRef_masterLocationsSearched);
		masterLocationsSearched = tempRef_masterLocationsSearched.getRefObj();

		if (StringHelper.isNullOrEmpty(templatePath)
				|| (StringHelper.isNullOrEmpty(masterPath) && !StringHelper.isNullOrEmpty(masterName))) {
			List<String> strs = CollectionHelper.union(templateLocationsSearched, masterLocationsSearched);
			return new TemplateEngineResult(strs);
		}

		return new TemplateEngineResult(createTemplate(renderContext, templatePath, masterPath), this);
	}

	private String getPath(RenderContext renderContext, String[] locations, String[] areaLocations,
						   String locationsPropertyName, String name, String controllerName, String cacheKeyPrefix, boolean useCache,
						   RefObject<String[]> searchedLocations) {
		searchedLocations.setRefObj(_emptyLocations);// = _emptyLocations;

		if (StringHelper.isNullOrEmpty(name)) {
			return "";
		}

		String areaName = renderContext.getTemplateInfo().getArea();// AreaHelpers.GetAreaName(renderContext.getRouteData());

		boolean usingAreas = !StringHelper.isNullOrEmpty(areaName);
		ArrayList<TemplateLocation> templateLocations = getTemplateLocations(locations, (usingAreas) ? areaLocations : null);

		if (templateLocations.isEmpty()) {
			throw new IllegalStateException(String.format(CultureInfo.CurrentCulture,
					MvcResources.Common_PropertyCannotBeNullOrEmpty, locationsPropertyName));
		}

		boolean nameRepresentsPath = IsSpecificPath(name);
		String cacheKey = createCacheKey(cacheKeyPrefix, name, (nameRepresentsPath) ? "" : controllerName, areaName);

		if (useCache) {
			// Only look at cached display modes that can handle the context.
			Iterable<IDisplayMode> possibleDisplayModes = getDisplayModeProvider().getAvailableDisplayModesForContext(
					renderContext.getHttpContext(), renderContext.getDisplayMode());
			for (IDisplayMode displayMode : possibleDisplayModes) {
				String cachedLocation = getTemplateLocationCache().getTemplateLocation(renderContext.getHttpContext(),
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
					if (renderContext.getDisplayMode() == null) {
						renderContext.setDisplayMode(displayMode);
					}

					return cachedLocation;
				}
				// An empty cachedLocation value indicates that we don't have a
				// matching file on disk. Keep going down the list of possible
				// display modes.
			}

			// getPath is called again without using the cache.
			return null;
		} else {
			return nameRepresentsPath ? getPathFromSpecificName(renderContext, name, cacheKey, searchedLocations)
					: getPathFromGeneralName(renderContext, templateLocations, name, controllerName, areaName, cacheKey,
							searchedLocations);
		}
	}

	private String getPathFromGeneralName(RenderContext renderContext, ArrayList<TemplateLocation> locations,
										  String name, String controllerName, String areaName, String cacheKey,
										  RefObject<String[]> searchedLocations) {
		String result = "";
		searchedLocations.setRefObj(new String[locations.size()]);

		for (int i = 0; i < locations.size(); i++) {
			TemplateLocation location = locations.get(i);
			String virtualPath = location.format(name, controllerName, areaName);
			DisplayInfo virtualPathDisplayInfo = getDisplayModeProvider().getDisplayInfoForVirtualPath(virtualPath,
					renderContext.getHttpContext(), path -> fileExists(renderContext, path),
					renderContext.getDisplayMode());

			if (virtualPathDisplayInfo != null) {
				String resolvedVirtualPath = virtualPathDisplayInfo.getFilePath();

				searchedLocations.setRefObj(_emptyLocations);
				result = resolvedVirtualPath;
				getTemplateLocationCache().insertTemplateLocation(renderContext.getHttpContext(),
						appendDisplayModeToCacheKey(cacheKey,
								virtualPathDisplayInfo.getDisplayMode().getDisplayModeId()),
						result);

				if (renderContext.getDisplayMode() == null) {
					renderContext.setDisplayMode(virtualPathDisplayInfo.getDisplayMode());
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
						DisplayInfo displayInfoToCache = displayMode.getDisplayInfo(renderContext.getHttpContext(),
								virtualPath, path -> fileExists(renderContext, path));

						String cacheValue = "";
						if (displayInfoToCache != null && displayInfoToCache.getFilePath() != null) {
							cacheValue = displayInfoToCache.getFilePath();
						}
						getTemplateLocationCache().insertTemplateLocation(renderContext.getHttpContext(),
								appendDisplayModeToCacheKey(cacheKey, displayMode.getDisplayModeId()), cacheValue);
					}
				}
				break;
			}

			searchedLocations.getRefObj()[i] = virtualPath;
		}

		return result;
	}

	private String getPathFromSpecificName(RenderContext renderContext, String name, String cacheKey,
										   RefObject<String[]> searchedLocations) {
		String result = name;

		if (!(filePathIsSupported(name) && fileExists(renderContext, name))) {
			result = "";
			searchedLocations.setRefObj(new String[] { name });
		}

		getTemplateLocationCache().insertTemplateLocation(renderContext.getHttpContext(), cacheKey, result);
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

	private static ArrayList<TemplateLocation> getTemplateLocations(String[] templateLocationFormats,
															String[] areaTemplateLocationFormats) {
		ArrayList<TemplateLocation> allLocations = new ArrayList<TemplateLocation>();

		if (areaTemplateLocationFormats != null) {
			for (String areaTemplateLocationFormat : areaTemplateLocationFormats) {
				allLocations.add(new AreaAwareTemplateLocation(areaTemplateLocationFormat));
			}
		}

		if (templateLocationFormats != null) {
			for (String templateLocationFormat : templateLocationFormats) {
				allLocations.add(new TemplateLocation(templateLocationFormat));
			}
		}

		return allLocations;
	}

	private static boolean IsSpecificPath(String name) {
		char c = name.charAt(0);
		return (c == '~' || c == '/');
	}

	@Override
	public void releaseTemplate(RenderContext renderContext, ITemplate template) throws IOException {
		java.io.Closeable disposable = (java.io.Closeable) ((template instanceof java.io.Closeable) ? template : null);
		if (disposable != null) {
			disposable.close();
		}
	}

	private static class AreaAwareTemplateLocation extends TemplateLocation {
		public AreaAwareTemplateLocation(String virtualPathFormatString) {
			super(virtualPathFormatString);
		}

		@Override
		public String format(String templateName, String controllerName, String areaName) {
			return StringHelper.format(CultureInfo.InvariantCulture, _virtualPathFormatString,new Object[]{ templateName, controllerName,
					areaName});
		}
	}

	private static class TemplateLocation {
		protected String _virtualPathFormatString;

		public TemplateLocation(String virtualPathFormatString) {
			_virtualPathFormatString = virtualPathFormatString;
		}

		public String format(String templateName, String controllerName, String areaName) {
			return StringHelper.format(CultureInfo.InvariantCulture, _virtualPathFormatString, templateName, controllerName);
		}
	}
}