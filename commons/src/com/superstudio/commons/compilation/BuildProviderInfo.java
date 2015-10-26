package com.superstudio.commons.compilation;

public abstract class BuildProviderInfo {
	private BuildProviderAppliesTo _appliesTo;

	abstract Class getType();

	BuildProviderAppliesTo getAppliesTo() {

		if (this._appliesTo != BuildProviderAppliesTo.None) {
			return this._appliesTo;
		}
		//Object[] customAttributes = this.getType().GetCustomAttributes(
			//	BuildProviderAppliesToAttribute.class, true);
		/*Annotation[] customAttributes=this.getType().getAnnotations();
		if (customAttributes != null && customAttributes.length != 0) {
			this._appliesTo = ((BuildProviderAppliesToAttribute) customAttributes[0]).AppliesTo;
		} else {
			this._appliesTo = BuildProviderAppliesTo.All;
		}*/
		this._appliesTo = BuildProviderAppliesTo.All;
		return this._appliesTo;

	}
}
