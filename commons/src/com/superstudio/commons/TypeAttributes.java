package com.superstudio.commons;

public class TypeAttributes {
	public static final int VisibilityMask = 7;
	public static final int NotPublic = 0;
	public static final int Public = 1;
	public static final int NestedPublic = 2;
	public static final int NestedPrivate = 3;
	public static final int NestedFamily = 4;
	public static final int NestedAssembly = 5;
	public static final int NestedFamANDAssem = 6;
	public static final int NestedFamORAssem = 7;
	public static final int LayoutMask = 24;
	public static final int AutoLayout = 0;
	public static final int SequentialLayout = 8;
	public static final int ExplicitLayout = 16;
	public static final int ClassSemanticsMask = 32;
	public static final int Class = 0;
	public static final int Interface = 32;
	public static final int Abstract = 128;
	public static final int Sealed = 256;
	public static final int SpecialName = 1024;
	public static final int Import = 4096;
	public static final int Serializable = 8192;
	public static final int WindowsRuntime = 16384;
	public static final int StringFormatMask = 196608;
	public static final int AnsiClass = 0;
	public static final int UnicodeClass = 65536;
	public static final int AutoClass = 131072;
	public static final int CustomFormatClass = 196608;
	public static final int CustomFormatMask = 12582912;
	public static final int BeforeFieldInit = 1048576;
	public static final int ReservedMask = 264192;
	public static final int RTSpecialName = 2048;

	public static final int HasSecurity = 262144;

	private int intValue;
	private static java.util.HashMap<Integer, TypeAttributes> mappings;

	private static java.util.HashMap<Integer, TypeAttributes> getMappings() {
		if (mappings == null) {
			synchronized (TypeAttributes.class) {
				if (mappings == null) {
					mappings = new java.util.HashMap<Integer, TypeAttributes>();
				}
			}
		}
		return mappings;
	}

	private TypeAttributes(int value) {
		intValue = value;
		synchronized (TypeAttributes.class) {
			getMappings().put(value, this);
		}
	}

	public int getValue() {
		return intValue;
	}

	public static TypeAttributes forValue(int value) {
		synchronized (TypeAttributes.class) {
			TypeAttributes enumObj = getMappings().get(value);
			// return enumObj;
			if (enumObj == null) {
				return new TypeAttributes(value);
			} else {
				return enumObj;
			}
		}
	}
}