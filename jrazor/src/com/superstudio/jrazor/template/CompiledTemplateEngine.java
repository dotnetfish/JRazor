package com.superstudio.jrazor.template;

import java.util.function.Supplier;

import com.superstudio.commons.HostingEnvironment;
import com.superstudio.commons.VirtualPathProvider;
import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.jrazor.DependencyResolver;

public abstract class CompiledTemplateEngine extends VirtualPathProviderTemplateEngine {
	private static Object _isPrecompiledNonUpdateableSiteInitializedLock = new Object();
	private static boolean _isPrecompiledNonUpdateableSite;
	private static boolean _isPrecompiledNonUpdateableSiteInitialized;
	private static FileExistenceCache _sharedFileExistsCache;

	private IBuildManager _buildManager;
	private ITemplatePageActivator _viewPageActivator;
	private IResolver<ITemplatePageActivator> _activatorResolver;
	private FileExistenceCache _fileExistsCache;

	protected CompiledTemplateEngine()
	{
		this(null, null, null, null);
	}

	protected CompiledTemplateEngine(ITemplatePageActivator viewPageActivator)
	{
		this(viewPageActivator, null, null, null);
	}

	public CompiledTemplateEngine(ITemplatePageActivator viewPageActivator, IResolver<ITemplatePageActivator> activatorResolver, IDependencyResolver dependencyResolver, VirtualPathProvider pathProvider)
	{
		if (viewPageActivator != null)
		{
			_viewPageActivator = viewPageActivator;
		}
		else
		{
			_activatorResolver = (activatorResolver != null) ? activatorResolver : new SingleServiceResolver<ITemplatePageActivator>(() -> null, 
					new DefaultTemplateActivator(dependencyResolver), "BuildManagerViewEngine constructor");
		}

		if (pathProvider != null)
		{
			Supplier<VirtualPathProvider> providerFunc = () ->{return pathProvider;};
			_fileExistsCache = new FileExistenceCache(providerFunc);
			setVirtualPathProviderFunc (providerFunc);
		}
		else
		{
			if (_sharedFileExistsCache == null)
			{
				// Startup initialization race is OK providing service remains read-only
				_sharedFileExistsCache = new FileExistenceCache(() -> HostingEnvironment.getVirtualPathProvider());
			}

			_fileExistsCache = _sharedFileExistsCache;
		}
	}

	public final IBuildManager getBuildManager()
	{
		if (_buildManager == null)
		{
			_buildManager = new BuildManagerWrapper();
		}
		return _buildManager;
	}
	public final void setBuildManager(IBuildManager value)
	{
		_buildManager = value;
	}

	protected final ITemplatePageActivator getTemplatePageActivator()
	{
		if (_viewPageActivator != null)
		{
			return _viewPageActivator;
		}
		_viewPageActivator = _activatorResolver.getCurrent();
		return _viewPageActivator;
	}

	protected boolean getIsPrecompiledNonUpdateableSite()
	{
		RefObject<Boolean> tempRef__isPrecompiledNonUpdateableSite = new RefObject<Boolean>(_isPrecompiledNonUpdateableSite);
		RefObject<Boolean> tempRef__isPrecompiledNonUpdateableSiteInitialized = new RefObject<Boolean>(_isPrecompiledNonUpdateableSiteInitialized);
		RefObject<Object> tempRef__isPrecompiledNonUpdateableSiteInitializedLock = new RefObject<Object>(_isPrecompiledNonUpdateableSiteInitializedLock);
		boolean tempVar = LazyInitializer.EnsureInitialized(tempRef__isPrecompiledNonUpdateableSite, tempRef__isPrecompiledNonUpdateableSiteInitialized, 
				tempRef__isPrecompiledNonUpdateableSiteInitializedLock, 
				()->getPrecompiledNonUpdateable());
		_isPrecompiledNonUpdateableSite = tempRef__isPrecompiledNonUpdateableSite.getRefObj();
		_isPrecompiledNonUpdateableSiteInitialized = tempRef__isPrecompiledNonUpdateableSiteInitialized.getRefObj();
		_isPrecompiledNonUpdateableSiteInitializedLock = tempRef__isPrecompiledNonUpdateableSiteInitializedLock.getRefObj();
		return tempVar;
	}

	@Override
	protected boolean fileExists(TemplateHostContext templateHostContext, String virtualPath)
	{
		// When dealing with non-updateable precompiled views, the view files may not exist on disk. The correct
		// way to check for existence of a file in this case is by querying the BuildManager.
		// For all other scenarios, checking for files on disk is faster and should suffice.
		//Contract.Assert(_fileExistsCache != null);
		return _fileExistsCache.FileExists(virtualPath) || (getIsPrecompiledNonUpdateableSite() && getBuildManager().fileExists(virtualPath));
	}

	private static boolean getPrecompiledNonUpdateable()
	{
		IVirtualPathUtility virtualPathUtility = new VirtualPathUtilityWrapper();
		return BuildVirtualPathFactory.isNonUpdateablePrecompiledApp(HostingEnvironment.getVirtualPathProvider(), virtualPathUtility);
	}

	public static class DefaultTemplateActivator implements ITemplatePageActivator
	{
		private Supplier<IDependencyResolver> _resolverThunk;

		public DefaultTemplateActivator()
		{
			this(null);
		}

		public DefaultTemplateActivator(IDependencyResolver resolver)
		{
			if (resolver == null)
			{
				_resolverThunk = () -> DependencyResolver.getCurrent();
			}
			else
			{
				_resolverThunk = () -> resolver;
			}
		}

		public final Object Create(TemplateHostContext templateHostContext, java.lang.Class type) throws InstantiationException, IllegalAccessException
		{
			try
			{
				return ((_resolverThunk.get().getService(type)) != null) ? _resolverThunk.get().getService(type) : type.newInstance();
			}
			catch (NoSuchMethodError exception)
			{
				// Ensure thrown exception contains the type name.  Might be down a few levels.
				NoSuchMethodError replacementException = TypeHelpers.EnsureDebuggableException(exception, type.getName());
				if (replacementException != null)
				{
					throw replacementException;
				}

				throw exception;
			}
		}
	}
}

