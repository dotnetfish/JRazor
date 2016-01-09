package com.superstudio.template.templatepages;

import com.superstudio.commons.*;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.io.Path;
import com.superstudio.template.mvc.JavaBuildManager;

import java.util.List;
import java.util.function.Supplier;
/** 
 Wraps the caching and instantiation of paths of the BuildManager. 
 In case of precompiled non-updateable sites, the only way to verify if a file exists is to call BuildManager.getObjectFactory. However this method is less performant than
 VirtualPathProvider.fileExists which is used for all other scenarios. In this class, we optimize for the first scenario by storing the results of getObjectFactory for a
 long duration.
*/
public final class BuildManagerWrapper implements IVirtualPathFactory
{
	public static final Guid KeyGuid = new Guid();
	private static final int _objectFactoryCacheDuration =60000;//Timespan.fromminutes(1)
	private IVirtualPathUtility _virtualPathUtility;
	private Supplier<VirtualPathProvider> _vppFunc;
	private boolean _isPrecompiled;
	private FileExistenceCache _vppCache;
	private List<String> _supportedExtensions;

	public BuildManagerWrapper()
	{
		this(() -> {return HostingEnvironment.getVirtualPathProvider();}, new VirtualPathUtilityWrapper());
	}

	public BuildManagerWrapper(VirtualPathProvider vpp, IVirtualPathUtility virtualPathUtility)
	{
		this(() -> vpp, virtualPathUtility);
		//Contract.Assert(vpp != null);
	}

	public BuildManagerWrapper(Supplier<VirtualPathProvider> vppFunc, IVirtualPathUtility virtualPathUtility)
	{
		/*Contract.Assert(vppFunc != null);
		Contract.Assert(virtualPathUtility != null);*/

		_vppFunc = vppFunc;
		_virtualPathUtility = virtualPathUtility;
		_isPrecompiled = isNonUpdatablePrecompiledApp();
		if (!_isPrecompiled)
		{
			_vppCache = new FileExistenceCache(vppFunc);
		}
	}

	public List<String> getSupportedExtensions()
	{
		return (_supportedExtensions != null) ? _supportedExtensions : WebPageHttpHandler.getRegisteredExtensions();
	}
	public void setSupportedExtensions(List<String> value)
	{
		_supportedExtensions = value;
	}

	/** 
	 Determines if a page exists in the website. 
	 This method switches between a long duration cache or a short duration FileExistenceCache depending on whether the site is precompiled. 
	 This is an optimization because BuildManager.getObjectFactory is comparably slower than performing VirtualPathFactory.exists
	*/
	public boolean exists(String virtualPath)
	{
		if (_isPrecompiled)
		{
			return existsInPrecompiledSite(virtualPath);
		}
		return existsInVpp(virtualPath);
	}

	public boolean isNonUpdatablePrecompiledApp()
	{
		VirtualPathProvider vpp = _vppFunc.get();
		// VirtualPathProvider currently null in some test scenarios e.g. PreApplicationStartCodeTest.StartTest
		if (vpp == null)
		{
			return false;
		}
		return isNonUpdateablePrecompiledApp(vpp, _virtualPathUtility);
	}

	/** 
	 An app's is precompiled for our purposes if 
	 (a) it has a PreCompiledApp.config file in the site root, 
	 (b) The PreCompiledApp.config says that the app is not Updatable.
	 
	 
	 This code is based on System.Web.DynamicData.Misc.IsNonUpdatablePrecompiledAppNoCache (DynamicData)
	 
	*/
	public static boolean isNonUpdateablePrecompiledApp(VirtualPathProvider vpp, IVirtualPathUtility virtualPathUtility)
	{
		String virtualPath = virtualPathUtility.toAbsolute("~/PrecompiledApp.config");
		if (!vpp.fileExists(virtualPath))
		{
			return false;
		}
//TODO edit to java xml
		/*XDocument document;
		try (InputStream stream = vpp.GetFile(virtualPath).Open())
		{
			try
			{
				document = XDocument.Load(stream);
			}
			catch (java.lang.Exception e)
			{
				// If we are unable to load the file, ignore it. The BuildManager behaves identically.
				return false;
			}
		}

		if (document.Root == null || !document.Root.Name.LocalName.equals("precompiledApp", StringComparison.OrdinalIgnoreCase))
		{
			return false;
		}

		var updatableAttribute = document.Root.Attribute("updatable");
		if (updatableAttribute != null)
		{
			boolean result = false;
			RefObject<Boolean> tempRef_result = new RefObject<Boolean>(result);
			boolean tempVar = Boolean.TryParse(updatableAttribute.Value, tempRef_result) && (result == false);
			result = tempRef_result.argValue;
			return tempVar;
		}*/
		return false;
	}

	private boolean existsInPrecompiledSite(String virtualPath)
	{
		String key = getKeyFromVirtualPath(virtualPath);

		// We assume that the key is unique enough to avoid collisions.
		BuildManagerResult buildManagerResult = (BuildManagerResult)RuntimeCache.getInstance().get(key);
		if (buildManagerResult == null)
		{
			// For precompiled apps, we cache the ObjectFactory and use it in the createInstance method.
			IWebObjectFactory objectFactory = getObjectFactory(virtualPath);
			buildManagerResult = new BuildManagerResult();
			buildManagerResult.setObjectFactory(objectFactory);
			buildManagerResult.setExists(objectFactory != null);
			// Cache the result with a sliding expiration for a long duration. 
			RuntimeCache.getInstance().set(key, buildManagerResult);//, null, Cache.NoAbsoluteExpiration, _objectFactoryCacheDuration, CacheItemPriority.Low, null);
		}
		return buildManagerResult.getExists();
	}

	/** 
	 Determines if a site exists in the VirtualPathProvider.
	 Results of hits are cached for a very short amount of time in the FileExistenceCache.
	*/
	private boolean existsInVpp(String virtualPath)
	{
		assert _vppCache != null;
		return _vppCache.fileExists(virtualPath);
	}

	/** 
	 Determines if an ObjectFactory exists for the virtualPath. 
	 The BuildManager complains if we pass in extensions that aren't registered for compilation. So we ensure that the virtual path is not 
	 extensionless and that it is one of the extension
	*/
	private IWebObjectFactory getObjectFactory(String virtualPath)
	{
		if (isPathExtensionSupported(virtualPath))
		{
			return JavaBuildManager.getObjectFactory(virtualPath, false);
		}
		return null;
	}

	public Object createInstance(String virtualPath) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		return this.createInstanceOfType(virtualPath);
	}

	public <T> T createInstanceOfType(String virtualPath) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		if (_isPrecompiled)
		{
			BuildManagerResult buildManagerResult = (BuildManagerResult)RuntimeCache.getInstance().get(getKeyFromVirtualPath(virtualPath));
			// The cache could have evicted our results. In this case, we'll simply fall through to createInstanceFromVirtualPath
			if (buildManagerResult != null)
			{
				//Debug.Assert(buildManagerResult.getExists() && buildManagerResult.getObjectFactory() != null, "This method must only be called if the file exists.");
				Object tempVar = buildManagerResult.getObjectFactory().CreateInstance();
				return (T)tempVar;
			}
		}
  
		return JavaBuildManager.<T>createInstanceFromVirtualPath(virtualPath);
	}

	/** 
	 Determines if the extension is one of the extensions registered with WebPageHttpHandler. 
	*/
	public boolean isPathExtensionSupported(String virtualPath)
	{
		String extension = Path.GetExtension(virtualPath);
		return !StringHelper.isNullOrEmpty(extension) && 
				getSupportedExtensions().stream().anyMatch(p->p.toLowerCase().equals(extension.substring(1).toLowerCase()));
	}

	/** 
	 Creates a reasonably unique key for a given virtual path by concatenating it with a Guid.
	*/
	private static String getKeyFromVirtualPath(String virtualPath)
	{
		return KeyGuid.toString() + "_" + virtualPath;
	}

	private static class BuildManagerResult
	{
		private boolean Exists;
		public final boolean getExists()
		{
			return Exists;
		}
		public final void setExists(boolean value)
		{
			Exists = value;
		}

		private IWebObjectFactory ObjectFactory;
		public final IWebObjectFactory getObjectFactory()
		{
			return ObjectFactory;
		}
		public final void setObjectFactory(IWebObjectFactory value)
		{
			ObjectFactory = value;
		}
	}
}