package com.superstudio.language.csharp;

import com.superstudio.commons.TypeAttributes;

public class CSharpTypeAttributeConverter extends CSharpModifierAttributeConverter {

	private static String[] names;
	private static Object[] values;
	private static CSharpTypeAttributeConverter defaultConverter;

	public static CSharpTypeAttributeConverter Default() {

		if (CSharpTypeAttributeConverter.defaultConverter == null) {
			CSharpTypeAttributeConverter.defaultConverter = new CSharpTypeAttributeConverter();
		}
		return CSharpTypeAttributeConverter.defaultConverter;

	}

	protected String[] getNames() {

		if (CSharpTypeAttributeConverter.names == null) {
			CSharpTypeAttributeConverter.names = new String[] { "Public", "Internal" };
		}
		return CSharpTypeAttributeConverter.names;

	}

	// @Override
	protected Object[] getValues() {

		if (CSharpTypeAttributeConverter.values == null) {
			CSharpTypeAttributeConverter.values = new Object[] {
					TypeAttributes.Public, 
					TypeAttributes.NotPublic
			};
		}
		return CSharpTypeAttributeConverter.values;

	}

	// @Override
	protected Object getDefaultValue() {

		return TypeAttributes.NotPublic;

	}

	private CSharpTypeAttributeConverter() {
	}
}
