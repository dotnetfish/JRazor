package com.superstudio.codedom;

import java.io.Serializable;
import java.util.*;

public class CodeObject implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5271481927286249928L;
	private Map userData;

	public final Map getUserData()
	{
		if (this.userData == null)
		{
			this.userData = new HashMap();
		}
		return this.userData;
	}
}