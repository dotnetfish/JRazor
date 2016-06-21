package com.superstudio.template.templatepages;

import com.oracle.xmlns.internal.webservices.jaxws_databinding.ObjectFactory;
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
	//private boolean _isPrecompiled;//是否预编译
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
		//_isPrecompiled = isNonUpdatablePrecompiledApp();
		//if (!_isPrecompiled)
		//{
			_vppCache = new FileExistenceCache(vppFunc);
		//}
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

		return existsInVpp(virtualPath);
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
			BuildManagerResult buildManagerResult = (BuildManagerResult)RuntimeCache.getInstance().get(getKeyFromVirtualPath(virtualPath));
			// The cache could have evicted our results. In this case, we'll simply fall through to createInstanceFromVirtualPath
			if (buildManagerResult != null)
			{
				//Debug.Assert(buildManagerResult.getExists() && buildManagerResult.getObjectFactory() != null, "This method must only be called if the file exists.");
				Object tempVar = buildManagerResult.getObjectFactory().CreateInstance();
				return (T)tempVar;
			}
		T result= JavaBuildManager.<T>createInstanceFromVirtualPath(virtualPath);
		BuildManagerResult buildResult =new BuildManagerResult();
		IWebObjectFactory objectFactory=getObjectFactory(virtualPath);
		buildResult.setObjectFactory(objectFactory);
		buildResult.setExists(objectFactory != null);
		RuntimeCache.getInstance().set(getKeyFromVirtualPath(virtualPath),buildResult);
		System.out.println(result.getClass().getName());
		return  result;
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