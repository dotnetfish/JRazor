package com.superstudio.web.razor.editor;


/** 
 Used within <see cref="F:SpanEditHandler.EditorHints"/>.
 
*/

//[Flags]
public enum EditorHints
{
	/** 
	 The default (Markup or Code) editor behavior for Statement completion should be used.
	 Editors can always use the default behavior, even if the span is labeled with a different CompletionType.
	 
	*/
	None(0), // 0000 0000

	/** 
	 Indicates that Virtual Path completion should be used for this span if the editor supports it.
	 Editors need not support this mode of completion, and will use the default (<see cref="F:EditorHints.None"/>) behavior
	 if they do not support it.
	 
	*/
	VirtualPath(1), // 0000 0001

	/** 
	 Indicates that this span's content contains the path to the layout page for this document.
	 
	*/
	LayoutPage(2); // 0000 0010

	private int intValue;
	private static java.util.HashMap<Integer, EditorHints> mappings;
	private synchronized static java.util.HashMap<Integer, EditorHints> getMappings()
	{
		if (mappings == null)
		{
			mappings = new java.util.HashMap<Integer, EditorHints>();
		}
		return mappings;
	}

	EditorHints(int value)
	{
		intValue = value;
		EditorHints.getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static EditorHints forValue(int value)
	{
		return getMappings().get(value);
	}
}