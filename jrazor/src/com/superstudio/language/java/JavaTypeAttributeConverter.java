package com.superstudio.language.java;

import com.superstudio.commons.TypeAttributes;

/**
 * @author cloudartisan
 */
public class JavaTypeAttributeConverter extends JavaModifierAttributeConverter {

	private static String[] names;
	private static Object[] values;
	private static JavaTypeAttributeConverter defaultConverter;

	public static JavaTypeAttributeConverter getDefault() {

		if (JavaTypeAttributeConverter.defaultConverter == null) {
			JavaTypeAttributeConverter.defaultConverter = new JavaTypeAttributeConverter();
		}
		return JavaTypeAttributeConverter.defaultConverter;

	}

	@Override
	protected String[] getNames() {

		if (JavaTypeAttributeConverter.names == null) {
			JavaTypeAttributeConverter.names = new String[] { "Public", "Internal" };
		}
		return JavaTypeAttributeConverter.names;

	}

	@Override
	protected Object[] getValues() {

		if (JavaTypeAttributeConverter.values == null) {
			JavaTypeAttributeConverter.values = new Object[] {
					TypeAttributes.Public, 
					TypeAttributes.NotPublic
			};
		}
		return JavaTypeAttributeConverter.values;

	}

	@Override
	protected Object getDefaultValue() {

		return TypeAttributes.NotPublic;

	}

	private JavaTypeAttributeConverter() {
	}
}
