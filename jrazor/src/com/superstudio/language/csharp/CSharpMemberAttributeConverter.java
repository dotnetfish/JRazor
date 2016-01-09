package com.superstudio.language.csharp;

import com.superstudio.codedom.MemberAttributes;

public class CSharpMemberAttributeConverter extends CSharpModifierAttributeConverter {

	private static volatile String[] names;
	private static volatile Object[] values;
	private static volatile CSharpMemberAttributeConverter defaultConverter;

	public static CSharpMemberAttributeConverter Default() {

		if (CSharpMemberAttributeConverter.defaultConverter == null) {
			CSharpMemberAttributeConverter.defaultConverter = new CSharpMemberAttributeConverter();
		}
		return CSharpMemberAttributeConverter.defaultConverter;

	}

	protected String[] getNames() {

		if (CSharpMemberAttributeConverter.names == null) {
			CSharpMemberAttributeConverter.names = new String[] { "Public", "Protected", "Protected Internal",
					"Internal", "Private" };
		}
		return CSharpMemberAttributeConverter.names;

	}

	// @Override
	protected Object[] getValues() {

		if (CSharpMemberAttributeConverter.values == null) {
			CSharpMemberAttributeConverter.values = new Object[] { MemberAttributes.Public, MemberAttributes.Family,
					MemberAttributes.FamilyOrAssembly, MemberAttributes.Assembly, MemberAttributes.Private };
		}
		return CSharpMemberAttributeConverter.values;

	}

	protected Object getDefaultValue() {

		return MemberAttributes.Private;

	}

	private CSharpMemberAttributeConverter() {
	}

}
