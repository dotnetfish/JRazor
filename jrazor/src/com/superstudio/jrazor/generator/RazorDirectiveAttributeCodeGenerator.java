package com.superstudio.jrazor.generator;

import com.superstudio.codedom.CodeAttributeArgument;
import com.superstudio.codedom.CodeAttributeDeclaration;
import com.superstudio.codedom.CodePrimitiveExpression;
import com.superstudio.codedom.CodeTypeReference;
import com.superstudio.commons.Tuple;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.jrazor.RazorDirectiveAttribute;
import com.superstudio.jrazor.parser.syntaxTree.Span;



public class RazorDirectiveAttributeCodeGenerator extends SpanCodeGenerator {
	public RazorDirectiveAttributeCodeGenerator(String name, String value) {
		if (StringHelper.isNullOrEmpty(name)) {
			//throw new IllegalArgumentException(CommonResources.getArgument_Cannot_Be_Null_Or_Empty(), "name");
		}
		setName(name);
		setValue((value != null) ? value : ""); // Coerce to empty string if it
												// was null.
	}

	private String privateName;

	public final String getName() {
		return privateName;
	}

	private void setName(String value) {
		privateName = value;
	}

	private String privateValue;

	public final String getValue() {
		return privateValue;
	}

	private void setValue(String value) {
		privateValue = value;
	}

	@Override
	public void generateCode(Span target, CodeGeneratorContext context) {
		CodeTypeReference attributeType = new CodeTypeReference(RazorDirectiveAttribute.class);
		CodeAttributeDeclaration attributeDeclaration = new CodeAttributeDeclaration(attributeType,
				new CodeAttributeArgument(new CodePrimitiveExpression(getName())),
				new CodeAttributeArgument(new CodePrimitiveExpression(getValue())));
		context.getGeneratedClass().getCustomAttributes().add(attributeDeclaration);
	}

	@Override
	public String toString() {
		return "Directive: " + getName() + ", Value: " + getValue();
	}

	@Override
	public boolean equals(Object obj) {
		RazorDirectiveAttributeCodeGenerator other = (RazorDirectiveAttributeCodeGenerator) ((obj instanceof RazorDirectiveAttributeCodeGenerator)
				? obj : null);
		return other != null && getName().equals(other.getName())
				&& getValue().equals(other.getValue());
	}

	@Override
	public int hashCode() {
		return Tuple.Create(getName().toUpperCase(), 
				getValue().toUpperCase()).hashCode();
	}
}