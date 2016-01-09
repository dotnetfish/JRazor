package com.superstudio.commons;


public final class PreApplicationStartCode
{
	private static boolean _startWasCalled;
	public static void Start() throws Exception
	{
		if (PreApplicationStartCode._startWasCalled)
		{
			return;
		}
		PreApplicationStartCode._startWasCalled = true;
		//BuildProvider.registerBuildProvider(".cshtml", RazorBuildProvider.class);
		//BuildProvider.registerBuildProvider(".vbhtml", RazorBuildProvider.class);
	}
}