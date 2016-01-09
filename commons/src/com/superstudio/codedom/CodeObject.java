package com.superstudio.codedom;

import java.io.Serializable;
import java.util.*;


//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeObject
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