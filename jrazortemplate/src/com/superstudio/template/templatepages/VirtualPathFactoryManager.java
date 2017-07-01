package com.superstudio.template.templatepages;

import java.util.*;
import java.util.function.Consumer;

// This class encapsulates the creation of objects from virtual paths.  The creation is either performed via BuildBanager API's, or
// by using explicitly registered factories (which happens through ApplicationPart.Register).
public class VirtualPathFactoryManager implements IVirtualPathFactory
{
	private static final Lazy<VirtualPathFactoryManager> _instance = new Lazy<VirtualPathFactoryManager>(() ->
			new VirtualPathFactoryManager(new BuildManagerWrapper()));
	private static Consumer<String> _instancePathExists;
	private final LinkedList<IVirtualPathFactory> _virtualPathFactories = new LinkedList<IVirtualPathFactory>();

	public VirtualPathFactoryManager(IVirtualPathFactory defaultFactory)
	{
		_virtualPathFactories.addFirst(defaultFactory);
	}

	// Get the VirtualPathFactoryManager singleton instance
	public static VirtualPathFactoryManager getInstance()
	{
		return _instance.getValue();
	}

	public static Consumer<String> getInstancePathExists()
	{
		if (_instancePathExists == null)
		{
			_instancePathExists = (t)->{ getInstance().exists(t);};
		}
		return _instancePathExists;
	}

	public final Iterable<IVirtualPathFactory> getRegisteredFactories()
	{
		return _virtualPathFactories;
	}

	public static void registerVirtualPathFactory(IVirtualPathFactory virtualPathFactory)
	{
		getInstance().registerVirtualPathFactoryInternal(virtualPathFactory);
	}

	public final void registerVirtualPathFactoryInternal(IVirtualPathFactory virtualPathFactory)
	{
		_virtualPathFactories.add(_virtualPathFactories.size()-2, virtualPathFactory);
	}

	public final boolean exists(String virtualPath)
	{
		// Performance sensitive so avoid Linq and delegates
		for (IVirtualPathFactory factory : _virtualPathFactories)
		{
			if (factory.exists(virtualPath))
			{
				return true;
			}
		}
		return false;
	}

	public final Object createInstance(String virtualPath) throws InstantiationException, IllegalAccessException, ClassNotFoundException,Exception
	{
		return this.createInstanceOfType(virtualPath);
	}

	public final <T> T createInstanceOfType(String virtualPath) throws InstantiationException, IllegalAccessException, ClassNotFoundException,Exception
	{
		Optional<IVirtualPathFactory> virtualPathFactory = _virtualPathFactories.stream().filter(f -> f.exists(virtualPath)).findFirst();//FirstOrDefault(f -> f.exists(virtualPath));
		if (virtualPathFactory.isPresent())
		{
			return virtualPathFactory.get().<T>createInstance(virtualPath);
		}
		return null;
	}
}