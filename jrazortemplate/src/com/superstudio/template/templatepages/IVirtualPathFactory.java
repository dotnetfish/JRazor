package com.superstudio.template.templatepages;


// Implemented by classes that can create object instances from virtual path.
// Those implementations can completely bypass the BuildManager (e.g. for dynamic language pages)
public interface IVirtualPathFactory
{
	boolean exists(String virtualPath);
	//Object createInstance(String virtualPath);
	default <T> T createInstance(String virtualPath) throws InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		VirtualPathFactoryManager virtualPathFactoryManager = (VirtualPathFactoryManager)((this instanceof VirtualPathFactoryManager) ? this : null);
		if (virtualPathFactoryManager != null)
		{
			return virtualPathFactoryManager.createInstanceOfType(virtualPath);
		}
		//BuildManagerWrapper buildManagerFactory = (BuildManagerWrapper)((this instanceof BuildManagerWrapper) ? this : null);
		BuildManagerWrapper buildManagerFactory=(this instanceof BuildManagerWrapper)?(BuildManagerWrapper)this:null;
		if (buildManagerFactory != null)
		{
			return buildManagerFactory.<T>createInstanceOfType(virtualPath);
		}

		Object tempVar = createInstance(virtualPath);
		return (T)tempVar;
	}
}