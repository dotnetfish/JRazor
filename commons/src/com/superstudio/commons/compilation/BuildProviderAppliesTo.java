package com.superstudio.commons.compilation;

public enum BuildProviderAppliesTo {
		None(0),
		Web(1),
		Code (2),
		Resources (4),
		All( 7);
	
	private int intValue=0;
	BuildProviderAppliesTo(int value){
		intValue=value;
	}
	
}
