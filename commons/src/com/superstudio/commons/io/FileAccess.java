package com.superstudio.commons.io;

import com.superstudio.codedom.MemberAttributes;

public enum FileAccess {
	Write(1), ReadAndWrite(2), Read(0), ReadWrite(3);
	private int intValue;
	private static java.util.HashMap<Integer, FileAccess> mappings;

	private static java.util.HashMap<Integer, FileAccess> getMappings() {
		if (mappings == null) {
			synchronized (MemberAttributes.class) {
				if (mappings == null) {
					mappings = new java.util.HashMap<Integer, FileAccess>();
				}
			}
		}
		return mappings;
	}

	FileAccess(int value) {
		intValue = value;
		getMappings().put(value, this);
	}

	public int getValue() {
		return intValue;
	}

	public static FileAccess forValue(int value) {
		return getMappings().get(value);
	}

}
