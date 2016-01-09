package com.superstudio.template.mvc;

import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.commons.csharpbridge.action.Func;

public class LazyInitializer {

	public static boolean ensureInitialized(RefObject<Boolean> tempRef__isPrecompiledNonUpdateableSite,
											RefObject<Boolean> tempRef__isPrecompiledNonUpdateableSiteInitialized,
											RefObject<Object> tempRef__isPrecompiledNonUpdateableSiteInitializedLock, Func<Boolean> object) {
		// TODO Auto-generated method stub
		return object.execute();
	}

}
