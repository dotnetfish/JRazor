package com.superstudio.language.java;

import com.superstudio.codedom.MemberAttributes;

public class JavaMemberAttributeConverter extends JavaModifierAttributeConverter {

	private static volatile String[] names;
	private static volatile Object[] values;
	private static volatile JavaMemberAttributeConverter defaultConverter;

	public static JavaMemberAttributeConverter Default() {

		if (JavaMemberAttributeConverter.defaultConverter == null) {
			JavaMemberAttributeConverter.defaultConverter = new JavaMemberAttributeConverter();
		}
		return JavaMemberAttributeConverter.defaultConverter;

	}

	protected String[] getNames() {

		if (JavaMemberAttributeConverter.names == null) {
			JavaMemberAttributeConverter.names = new String[] { "Public", "Protected", "Protected Internal",
					"Internal", "Private" };
		}
		return JavaMemberAttributeConverter.names;

	}

	// @Override
	protected Object[] getValues() {

		if (JavaMemberAttributeConverter.values == null) {
			JavaMemberAttributeConverter.values = new Object[] { MemberAttributes.Public, MemberAttributes.Family,
					MemberAttributes.FamilyOrAssembly, MemberAttributes.Assembly, MemberAttributes.Private };
		}
		return JavaMemberAttributeConverter.values;

	}

	protected Object getDefaultValue() {

		return MemberAttributes.Private;

	}

	private JavaMemberAttributeConverter() {
	}

}
