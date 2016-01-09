package com.superstudio.jrazor.template;

import java.util.*;
import java.util.function.Consumer;

import com.superstudio.jrazor.templateEngine.Lazy;

// This class encapsulates the creation of objects from virtual paths.  The creation is either performed via BuildBanager API's, or
// by using explicitly registered factories (which happens through ApplicationPart.Register).
public class VirtualPathFactoryManager implements IVirtualPathFactory
{
	private static final Lazy<VirtualPathFactoryManager> _instance = new Lazy<VirtualPathFactoryManager>(() -> new VirtualPathFactoryManager(new BuildManagerWrapper()));
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
			_instancePathExists = (t)->{ getInstance().Exists(t);};
		}
		return _instancePathExists;
	}

	public final Iterable<IVirtualPathFactory> getRegisteredFactories()
	{
		return _virtualPathFactories;
	}

	public static void RegisterVirtualPathFactory(IVirtualPathFactory virtualPathFactory)
	{
		getInstance().RegisterVirtualPathFactoryInternal(virtualPathFactory);
	}

	public final void RegisterVirtualPathFactoryInternal(IVirtualPathFactory virtualPathFactory)
	{
		_virtualPathFactories.add(_virtualPathFactories.size()-2, virtualPathFactory);
	}

	public final boolean Exists(String virtualPath)
	{
		// Performance sensitive so avoid Linq and delegates
		for (IVirtualPathFactory factory : _virtualPathFactories)
		{
			if (factory.Exists(virtualPath))
			{
				return true;
			}
		}
		return false;
	}

	public final Object CreateInstance(String virtualPath) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		return this.<Object>CreateInstanceOfType(virtualPath);
	}

	public final <T> T CreateInstanceOfType(String virtualPath) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		Optional<IVirtualPathFactory> virtualPathFactory = _virtualPathFactories.stream().filter(f -> f.Exists(virtualPath)).findFirst();//FirstOrDefault(f -> f.Exists(virtualPath));
		if (virtualPathFactory.isPresent())
		{
			return virtualPathFactory.get().<T>CreateInstance(virtualPath);
		}
		return null;
	}
}