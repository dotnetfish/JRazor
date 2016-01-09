package com.superstudio.jrazor.editor;



import com.superstudio.commons.Environment;
import com.superstudio.commons.Trace;
import com.superstudio.jrazor.resources.RazorResources;




public final class RazorEditorTrace
{
	private static Boolean _enabled;

	private static boolean isEnabled()
	{
		if (_enabled == null)
		{
			boolean enabled  = Boolean.getBoolean(Environment.GetEnvironmentVariable("RAZOR_EDITOR_TRACE"));//(, tempRef_enabled);
			
			if (enabled)
			{
					_enabled = enabled;
			}
			else
			{
				_enabled = false;
			}
		}
		return _enabled;
	}

 
	//[Conditional("EDITOR_TRACING")]
	public static void traceLine(String format, Object... args)
	{
		if (isEnabled())
		{
			Trace.WriteLine(String.format( RazorResources.getTrace_Format(), String.format( format, args)));
		}
	}
}