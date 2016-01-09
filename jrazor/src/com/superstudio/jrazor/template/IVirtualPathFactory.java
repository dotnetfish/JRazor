package com.superstudio.jrazor.template;


// Implemented by classes that can create object instances from virtual path.
// Those implementations can completely bypass the BuildManager (e.g. for dynamic language pages)
public interface IVirtualPathFactory
{
	boolean Exists(String virtualPath);
	//Object CreateInstance(String virtualPath);
	default <T> T CreateInstance(String virtualPath) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		VirtualPathFactoryManager virtualPathFactoryManager = (VirtualPathFactoryManager)((this instanceof VirtualPathFactoryManager) ? this : null);
		if (virtualPathFactoryManager != null)
		{
			return virtualPathFactoryManager.CreateInstanceOfType(virtualPath);
		}
		//BuildManagerWrapper buildManagerFactory = (BuildManagerWrapper)((this instanceof BuildManagerWrapper) ? this : null);
		BuildManagerWrapper buildManagerFactory=(this instanceof BuildManagerWrapper)?(BuildManagerWrapper)this:null;
		if (buildManagerFactory != null)
		{
			return buildManagerFactory.<T>CreateInstanceOfType(virtualPath);
		}

		Object tempVar = CreateInstance(virtualPath);
		return (T)tempVar;
	}
}