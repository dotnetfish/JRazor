package com.superstudio.template.mvc;

import com.superstudio.commons.HostingEnvironment;
import com.superstudio.commons.VirtualPathProvider;
import com.superstudio.template.mvc.context.RenderContext;
import com.superstudio.template.templatepages.FileExistenceCache;

import java.util.function.Supplier;


public abstract class BuildManagerTemplateEngine extends VirtualPathProviderTemplateEngine
{
	private static Object isPrecompiledNonUpdateableSiteInitializedLock = new Object();
	private static boolean isPrecompiledNonUpdateableSite;
	private static boolean isPrecompiledNonUpdateableSiteInitialized;
	private static FileExistenceCache fileExistenceCache;

	private IBuildManager buildManager;
	private ITemplatePageActivator templatePageActivator;
	private IResolver<ITemplatePageActivator> templatePageActivatorIResolver;
	private FileExistenceCache _fileExistsCache;

	protected BuildManagerTemplateEngine()
	{
		this(null, null, null, null);
	}

	protected BuildManagerTemplateEngine(ITemplatePageActivator templatePageActivator)
	{
		this(templatePageActivator, null, null, null);
	}

	public BuildManagerTemplateEngine(ITemplatePageActivator templatePageActivator, IResolver<ITemplatePageActivator> activatorResolver, IDependencyResolver dependencyResolver, VirtualPathProvider pathProvider)
	{
		if (templatePageActivator != null)
		{
			this.templatePageActivator = templatePageActivator;
		}
		else
		{
			templatePageActivatorIResolver = (activatorResolver != null) ? activatorResolver : new SingleServiceResolver<ITemplatePageActivator>(() -> null, new DefaultTemplatePageActivator(dependencyResolver), "BuildManagerTemplateEngine constructor");
		}

		if (pathProvider != null)
		{
			Supplier<VirtualPathProvider> providerFunc = () ->{return pathProvider;};
			_fileExistsCache = new FileExistenceCache(providerFunc);
			setVirtualPathProviderFunc (providerFunc);
		}
		else
		{
			if (fileExistenceCache == null)
			{
				// Startup initialization race is OK providing service remains read-only
				fileExistenceCache = new FileExistenceCache(() -> HostingEnvironment.getVirtualPathProvider());
			}

			_fileExistsCache = fileExistenceCache;
		}
	}

	public final IBuildManager getBuildManager()
	{
		if (buildManager == null)
		{
			buildManager = new com.superstudio.template.mvc.BuildManagerWrapper();
		}
		return buildManager;
	}
	public final void setBuildManager(IBuildManager value)
	{
		buildManager = value;
	}

	protected final ITemplatePageActivator getTemplatePageActivator()
	{
		if (templatePageActivator != null)
		{
			return templatePageActivator;
		}
		templatePageActivator = templatePageActivatorIResolver.getCurrent();
		return templatePageActivator;
	}

	/*protected boolean getIsPrecompiledNonUpdateableSite()
	{
		RefObject<Boolean> tempRef__isPrecompiledNonUpdateableSite = new RefObject<Boolean>(isPrecompiledNonUpdateableSite);
		RefObject<Boolean> tempRef__isPrecompiledNonUpdateableSiteInitialized = new RefObject<Boolean>(isPrecompiledNonUpdateableSiteInitialized);
		RefObject<Object> tempRef__isPrecompiledNonUpdateableSiteInitializedLock = new RefObject<Object>(isPrecompiledNonUpdateableSiteInitializedLock);
		boolean tempVar = LazyInitializer.ensureInitialized(tempRef__isPrecompiledNonUpdateableSite, tempRef__isPrecompiledNonUpdateableSiteInitialized,
				tempRef__isPrecompiledNonUpdateableSiteInitializedLock, 
				()-> getPrecompiledNonUpdateable());
		isPrecompiledNonUpdateableSite = tempRef__isPrecompiledNonUpdateableSite.getRefObj();
		isPrecompiledNonUpdateableSiteInitialized = tempRef__isPrecompiledNonUpdateableSiteInitialized.getRefObj();
		isPrecompiledNonUpdateableSiteInitializedLock = tempRef__isPrecompiledNonUpdateableSiteInitializedLock.getRefObj();
		return tempVar;
	}*/

	@Override
	protected boolean fileExists(RenderContext renderContext, String virtualPath)
	{
		// When dealing with non-updateable precompiled templates, the template files may not exist on disk. The correct
		// way to check for existence of a file in this case is by querying the BuildManager.
		// For all other scenarios, checking for files on disk is faster and should suffice.
		//Contract.Assert(_fileExistsCache != null);
		return _fileExistsCache.fileExists(virtualPath) ;
	}

	/*private static boolean getPrecompiledNonUpdateable()
	{
		IVirtualPathUtility virtualPathUtility = new VirtualPathUtilityWrapper();
		return com.superstudio.template.templatepages.BuildManagerWrapper.isNonUpdateablePrecompiledApp(HostingEnvironment.getVirtualPathProvider(), virtualPathUtility);
	}*/

	public static class DefaultTemplatePageActivator implements ITemplatePageActivator
	{
		private Supplier<IDependencyResolver> _resolverThunk;

		public DefaultTemplatePageActivator()
		{
			this(null);
		}

		public DefaultTemplatePageActivator(IDependencyResolver resolver)
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

		public final Object create(RenderContext renderContext, java.lang.Class type) throws InstantiationException, IllegalAccessException
		{
			try
			{
				return ((_resolverThunk.get().getService(type)) != null) ? _resolverThunk.get().getService(type) : type.newInstance();
			}
			catch (NoSuchMethodError exception)
			{
				// Ensure thrown exception contains the type name.  Might be down a few levels.
				NoSuchMethodError replacementException = TypeHelpers.ensureDebuggableException(exception, type.getName());
				if (replacementException != null)
				{
					throw replacementException;
				}

				throw exception;
			}
		}
	}
}