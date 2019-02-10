package com.superstudio.commons;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author cloudartisan
 */
public class Resource {

	public static String getString(String string, Object[] objects) {

		return string;
	}

	public static String getString(String key) {

		Locale myLocale = Locale.getDefault();
		ResourceBundle bundle = ResourceBundle.getBundle("jrazor" , myLocale);
		return bundle.getString(key);

	}

}
