package com.superstudio.template.templatepages;

public class AttributeValue {
	public AttributeValue(PositionTagged<String> prefix, PositionTagged<Object> value, Boolean literal)
	{
		this.prefix = prefix;
		this.value = value;
		this.literal = literal;
	}

	private PositionTagged<String> prefix;
	private PositionTagged<Object> value;
	private Boolean literal ;
	public PositionTagged<Object> getValue() {
		// TODO Auto-generated method stub
		return this.value;
	}

	public PositionTagged<String> getPrefix() {
		// TODO Auto-generated method stub
		return this.prefix;
	}

	public boolean getLiteral() {
		// TODO Auto-generated method stub
		return this.literal;
	}

}
