package com.superstudio.language.java;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.ITypeDescriptorContext;
import com.superstudio.commons.SR;
import com.superstudio.commons.TypeConverter;
import com.superstudio.commons.exception.ArgumentNullException;

abstract class JavaModifierAttributeConverter extends TypeConverter {

	protected abstract Object[] getValues();

	protected abstract String[] getNames();

	protected abstract Object getDefaultValue();

	@Override
	public boolean canConvertFrom(ITypeDescriptorContext context, Class sourceType) {
		return sourceType == String.class || super.canConvertFrom(context, sourceType);
	}


	public Object convertFrom(ITypeDescriptorContext context, CultureInfo culture, Object value) {
		if (value instanceof String) {
			String value2 = (String) value;
			String[] names = this.getNames();
			for (int i = 0; i < names.length; i++) {
				if (names[i].equals(value2)) {
					return this.getValues()[i];
				}
			}
		}
		return this.getDefaultValue();
	}

	public Object convertTo(ITypeDescriptorContext context, CultureInfo culture, Object value, Class destinationType) throws ArgumentNullException {
		if (destinationType == null) {
			
			 throw new ArgumentNullException("destinationType");
		}
		if (destinationType == String.class) {
			Object[] values = this.getValues();
			for (int i = 0; i < values.length; i++) {
				if (values[i].equals(value)) {
					return this.getNames()[i];
				}
			}
			return SR.GetString("toStringUnknown");
		}
		return super.convertTo(context, culture, value, destinationType);
	}

	 public boolean getStandardValuesExclusive(ITypeDescriptorContext context) {
		return true;
	}


	public boolean getStandardValuesSupported(ITypeDescriptorContext context) {
		return true;
	}
	// @Override
	/*
	 * public StandardValuesCollection GetStandardValues(ITypeDescriptorContext
	 * context) { return new StandardValuesCollection(this.getValues()); }
	 */
}
