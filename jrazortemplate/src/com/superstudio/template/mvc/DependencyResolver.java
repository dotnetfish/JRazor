package com.superstudio.template.mvc;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.MvcResources;
import com.superstudio.commons.csharpbridge.StringHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.function.Function;


public class DependencyResolver {
	private static DependencyResolver _instance = new DependencyResolver();

	private IDependencyResolver _current;

	/**
	 * Cache should always be a new CacheDependencyResolver(_current).
	 */
	private CacheDependencyResolver _currentCache;

	public DependencyResolver() {
		innerSetResolver(new DefaultDependencyResolver());
	}

	public static IDependencyResolver getCurrent() {
		return _instance.getInnerCurrent();
	}

	public static IDependencyResolver getCurrentCache() {
		return _instance.getInnerCurrentCache();
	}

	public final IDependencyResolver getInnerCurrent() {
		return _current;
	}

	/**
	 * Provides caching over results returned by Current.
	 */
	public final IDependencyResolver getInnerCurrentCache() {
		return _currentCache;
	}

	public static void setResolver(IDependencyResolver resolver) {
		_instance.innerSetResolver(resolver);
	}

	public static void setResolver(Object commonServiceLocator) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		_instance.innerSetResolver(commonServiceLocator);
	}

	public static void setResolver(Function<java.lang.Class<?>, Object> getService,
								   Function<java.lang.Class<?>, Iterable<Object>> getServices) {
		_instance.innerSetResolver(getService, getServices);
	}

	public final void innerSetResolver(IDependencyResolver resolver) {
		if (resolver == null) {
			throw new IllegalArgumentException("resolver");
		}

		_current = resolver;
		_currentCache = new CacheDependencyResolver(_current);
	}

	public final void innerSetResolver(Object commonServiceLocator) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if (commonServiceLocator == null) {
			throw new IllegalArgumentException("commonServiceLocator");
		}

		java.lang.Class locatorType = commonServiceLocator.getClass();

		
		java.lang.reflect.Method getInstance = locatorType.getMethod("GetInstance", Class.class);
		java.lang.reflect.Method getInstances = locatorType.getMethod("GetAllInstances", Class.class);

		if (getInstance == null || getInstance.getReturnType() != Object.class || getInstances == null
				|| getInstances.getReturnType() != Iterable.class) {
			throw new IllegalArgumentException(StringHelper.format(CultureInfo.CurrentCulture,
					MvcResources.DependencyResolver_DoesNotImplementICommonServiceLocator,
					new Object[] { locatorType.getName() }) + "commonServiceLocator");
		}


		Function<java.lang.Class<?>, Object> getService = (p) -> {
			try {
				return getInstance.invoke(commonServiceLocator, p);
			} catch (Exception e) {

				e.printStackTrace();
				return null;
			}
		};
		Function<java.lang.Class<?>, Iterable<Object>> getServices = (p) -> {
			try {
				return (Iterable<Object>) getInstances.invoke(commonServiceLocator, p);
			} catch (Exception e) {

				return Collections.emptyList();
			}
		};
		innerSetResolver(new DelegateBasedDependencyResolver(getService, getServices));
	}

	public final void innerSetResolver(Function<java.lang.Class<?>, Object> getService,
									   Function<java.lang.Class<?>, Iterable<Object>> getServices) {
		if (getService == null) {
			throw new IllegalArgumentException("getService");
		}
		if (getServices == null) {
			throw new IllegalArgumentException("getServices");
		}

		innerSetResolver(new DelegateBasedDependencyResolver(getService, getServices));
	}

	/**
	 * Wraps an IDependencyResolver and ensures single instance per-type.
	 * 
	 * 
	 * Note it's possible for multiple threads to race and call the _resolver
	 * service multiple times. We'll pick one winner and ignore the others and
	 * still guarantee a unique instance.
	 * 
	 */
	private final static class CacheDependencyResolver implements IDependencyResolver {
		private final java.util.concurrent.ConcurrentHashMap<java.lang.Class<?>, Object> _cache = new java.util.concurrent.ConcurrentHashMap<java.lang.Class<?>, Object>();
		private final java.util.concurrent.ConcurrentHashMap<java.lang.Class<?>, Iterable<Object>> _cacheMultiple = new java.util.concurrent.ConcurrentHashMap<java.lang.Class<?>, Iterable<Object>>();
		private Function<java.lang.Class<?>, Object> _getServiceDelegate;
		private Function<java.lang.Class<?>, Iterable> _getServicesDelegate;

		private IDependencyResolver _resolver;

		public CacheDependencyResolver(IDependencyResolver resolver) {
			_resolver = resolver;
			_getServiceDelegate = (t) -> _resolver.getService(t);
			_getServicesDelegate = (t) -> _resolver.getServices(t);
		}

		public Object getService(java.lang.Class<?> serviceType) {
			// Use a saved delegate to prevent per-call delegate allocation
			return _cache.putIfAbsent(serviceType, _getServiceDelegate.apply(serviceType));
		}

		public Iterable<Object> getServices(java.lang.Class<?> serviceType) {

			// Use a saved delegate to prevent per-call delegate allocation
			return _cacheMultiple.putIfAbsent(serviceType, _getServicesDelegate.apply(serviceType));
		}

	}

	private static class DefaultDependencyResolver implements IDependencyResolver {
		public final Object getService(java.lang.Class<?> serviceType) {
			// Since attempting to create an instance of an interface or an
			// abstract type results in an exception, immediately return null
			// to improve performance and the debugging experience with
			// first-chance exceptions enabled.
			if (serviceType.isInterface() || Modifier.isAbstract(serviceType.getModifiers())) {
				return null;
			}

			try {
				return serviceType.newInstance();

			} catch (java.lang.Exception e) {
				return null;
			}
		}

		public final Iterable<Object> getServices(java.lang.Class<?> serviceType) {
			return Collections.emptyList();
		}
	}

	private static class DelegateBasedDependencyResolver implements IDependencyResolver {
		private Function<java.lang.Class<?>, Object> _getService;
		private Function<java.lang.Class<?>, Iterable<Object>> _getServices;

		public DelegateBasedDependencyResolver(Function<java.lang.Class<?>, Object> getService,
				Function<java.lang.Class<?>, Iterable<Object>> getServices) {
			_getService = getService;
			_getServices = getServices;
		}

		public final Object getService(java.lang.Class<?> type) {
			try {
				return _getService.apply(type);
			} catch (java.lang.Exception e) {
				return null;
			}
		}

		public final Iterable<Object> getServices(java.lang.Class<?> type) {
			return _getServices.apply(type);
		}
	}
}