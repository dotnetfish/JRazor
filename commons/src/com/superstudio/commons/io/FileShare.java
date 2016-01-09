package com.superstudio.commons.io;

import com.superstudio.codedom.MemberAttributes;

public enum FileShare {
	Inheritable(0), Read(1), ReadAndWrite(2), ReadWrite(3);
	private int intValue;
	private static java.util.HashMap<Integer, FileShare> mappings;

	private static java.util.HashMap<Integer, FileShare> getMappings() {
		if (mappings == null) {
			synchronized (MemberAttributes.class) {
				if (mappings == null) {
					mappings = new java.util.HashMap<Integer, FileShare>();
				}
			}
		}
		return mappings;
	}

	FileShare(int value) {
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue() {
		return intValue;
	}

	public static FileShare forValue(int value) {
		return getMappings().get(value);
	}

}
