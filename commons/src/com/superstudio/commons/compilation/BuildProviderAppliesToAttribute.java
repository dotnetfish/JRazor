package com.superstudio.commons.compilation;

public class BuildProviderAppliesToAttribute {// extends Attribute{
	private BuildProviderAppliesTo _appliesTo;

	public BuildProviderAppliesTo getAppliesTo() {

		return this._appliesTo;

	}

	public BuildProviderAppliesToAttribute(BuildProviderAppliesTo appliesTo) {
		this._appliesTo = appliesTo;
	}
}
