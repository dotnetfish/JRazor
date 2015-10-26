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
		//BuildProvider.RegisterBuildProvider(".cshtml", RazorBuildProvider.class);
		//BuildProvider.RegisterBuildProvider(".vbhtml", RazorBuildProvider.class);
	}
}