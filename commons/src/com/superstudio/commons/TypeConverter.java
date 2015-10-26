package com.superstudio.commons;

import com.superstudio.commons.exception.ArgumentNullException;

public class TypeConverter {

	public Object convertTo(ITypeDescriptorContext context, CultureInfo culture, Object value, Class destinationType) throws ArgumentNullException {
	
		return null;
	}

	public boolean canConvertFrom(ITypeDescriptorContext context, Class sourceType) {
		
		return false;
	}

}
