package com.superstudio.commons;

import com.superstudio.commons.exception.ArgumentNullException;

import java.util.Locale;

public class TypeConverter {

	public Object convertTo(ITypeDescriptorContext context, Locale locale, Object value, Class destinationType) throws ArgumentNullException {
	
		return null;
	}

	public boolean canConvertFrom(ITypeDescriptorContext context, Class sourceType) {
		
		return false;
	}

}
