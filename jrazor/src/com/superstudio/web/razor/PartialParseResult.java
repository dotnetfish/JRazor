package com.superstudio.web.razor;



// Flags:
//  Provisional, ContextChanged, Accepted, Rejected
//  000001 1  - Rejected,
//  000010 2  - Accepted
//  000100 4  - Provisional
//  001000 8  - Context Changed
//  010000 16 - Auto Complete Block

/** 
 The result of attempting an incremental parse
 
 
 Either the Accepted or Rejected flag is ALWAYS set.  
 Additionally, Provisional may be set with Accepted and SpanContextChanged may be set with Rejected.
 Provisional may NOT be set with Rejected and SpanContextChanged may NOT be set with Accepted.
 
*/
public enum PartialParseResult
{
	/** 
	 Indicates that the edit could not be accepted and that a reparse is underway.
	 
	*/
	Rejected(1),

	/** 
	 Indicates that the edit was accepted and has been added to the parse tree
	 
	*/
	Accepted(2),

	/** 
	 Indicates that the edit was accepted, but that a reparse should be forced when idle time is available
	 since the edit may be misclassified
	 
	 
	 This generally occurs when a "." is typed in an Implicit Expression, since editors require that this
	 be assigned to Code in order to properly support features like IntelliSense.  However, if no further edits
	 occur following the ".", it should be treated as Markup.
	 
	*/
	Provisional(4),

	/** 
	 Indicates that the edit caused a change in the span's context and that if any statement completions were active prior to starting this
	 partial parse, they should be reinitialized.
	 
	*/
	SpanContextChanged(8),

	/** 
	 Indicates that the edit requires an auto completion to occur
	 
	*/
	AutoCompleteBlock(16);

	private int intValue;
	private static java.util.HashMap<Integer, PartialParseResult> mappings;
	private synchronized static java.util.HashMap<Integer, PartialParseResult> getMappings()
	{
		if (mappings == null)
		{
			mappings = new java.util.HashMap<Integer, PartialParseResult>();
		}
		return mappings;
	}

	PartialParseResult(int value)
	{
		intValue = value;
		PartialParseResult.getMappings().put(value, this);
	}

	public int getValue()
	{
		return intValue;
	}

	public static PartialParseResult forValue(int value)
	{
		return getMappings().get(value);
	}

	public boolean HasFlag(PartialParseResult target) {
		// TODO logic refactor
		return ((this.intValue & target.getValue()) == target.getValue());
	}
}