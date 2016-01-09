package com.superstudio.template.templatepages;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.superstudio.commons.VirtualPathProvider;

/**
 * This class caches the result of VirtualPathProvider.fileExists for a short
 * period of time, and recomputes it if necessary.
 * 
 * The default VPP MapPathBasedVirtualPathProvider caches the result of the
 * fileExists call with the appropriate dependencies, so it is less expensive on
 * subsequent calls, but it still needs to do MapPath which can take quite some
 * time.
 */
public class FileExistenceCache {
	private static final int TicksPerMillisecond = 10000;
	private Supplier<VirtualPathProvider> _virtualPathProviderFunc;
	private Predicate<String> _virtualPathFileExists;
	private java.util.concurrent.ConcurrentHashMap<String, Boolean> _cache;
	private long _creationTick;
	private int _ticksBeforeReset;

	// Overload used mainly for testing

	public FileExistenceCache(VirtualPathProvider virtualPathProvider) {
		this(virtualPathProvider, 1000);
	}

	public FileExistenceCache(VirtualPathProvider virtualPathProvider, int milliSecondsBeforeReset) {
		this(() -> virtualPathProvider, milliSecondsBeforeReset);
		// Contract.Assert(virtualPathProvider != null);
	}

	public FileExistenceCache(Supplier<VirtualPathProvider> virtualPathProviderFunc) {
		this(virtualPathProviderFunc, 1000);
	}

	public FileExistenceCache(Supplier<VirtualPathProvider> virtualPathProviderFunc, int milliSecondsBeforeReset) {
		// Contract.Assert(virtualPathProviderFunc != null);

		_virtualPathProviderFunc = virtualPathProviderFunc;
		_virtualPathFileExists = path -> {
			boolean value=_virtualPathProviderFunc.get().fileExists(path);
			return value;
		};
		_ticksBeforeReset = milliSecondsBeforeReset * TicksPerMillisecond;
		reset();
	}

	// Use the VPP returned by the HostingEnvironment unless a custom vpp is
	// passed in (mainly for testing purposes)
	public final VirtualPathProvider getVirtualPathProvider() {
		return _virtualPathProviderFunc.get();
	}

	public final int getMilliSecondsBeforeReset() {
		return _ticksBeforeReset / TicksPerMillisecond;
	}

	public final void setMilliSecondsBeforeReset(int value) {
		_ticksBeforeReset = value * TicksPerMillisecond;
	}

	public final Map<String, Boolean> getCacheInternal() {
		return _cache;
	}

	public final boolean getTimeExceeded() {
		/*
		 * RefObject<Long> tempRef__creationTick = new
		 * RefObject<Long>(_creationTick); boolean tempVar =
		 * (java.time.LocalDateTime.now().getLong(field) -
		 * Interlocked.read(tempRef__creationTick)) > _ticksBeforeReset;
		 * _creationTick = tempRef__creationTick.getRefObj(); return tempVar;
		 */
		return true;
	}

	public final void reset() {
		_cache = new java.util.concurrent.ConcurrentHashMap<String, Boolean>();
		/*
		 * _ java.time.LocalDateTime now =
		 * java.time.LocalDateTime.now(ZoneId.systemDefault()); long tick =
		 * now.getLong();
		 * 
		 * RefObject<Long> tempRef__creationTick = new
		 * RefObject<Long>(_creationTick);
		 * Interlocked.Exchange(tempRef__creationTick, tick); _creationTick =
		 * tempRef__creationTick.getRefObj();
		 */
	}

	public final boolean fileExists(String virtualPath) {
		if (getTimeExceeded()) {
			reset();
		}
		
		boolean result=_virtualPathFileExists.test(virtualPath);
		 _cache.putIfAbsent(virtualPath, result);
		 return result;
	}
}