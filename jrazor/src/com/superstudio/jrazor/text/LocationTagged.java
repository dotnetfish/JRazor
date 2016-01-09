package com.superstudio.jrazor.text;

import com.superstudio.commons.HashCodeCombiner;
import com.superstudio.commons.csharpbridge.StringHelper;






 
//[//DebuggerDisplay("({Location})\"{Value}\"")]
public class LocationTagged<T> //implements IFormattable
{
	private LocationTagged()
	{
		setLocation(SourceLocation.Undefined);
		setValue(null);
	}

	public LocationTagged(T value, int offset, int line, int col)
	{
		this(value, new SourceLocation(offset, line, col));
	}

	public LocationTagged(T value, SourceLocation location)
	{
		if (value == null)
		{
			//throw new ArgumentNullException("value");
		}

		setLocation(location);
		setValue(value);
	}

	private SourceLocation privateLocation;
	public final SourceLocation getLocation()
	{
		return privateLocation;
	}
	private void setLocation(SourceLocation value)
	{
		privateLocation = value;
	}
	private T privateValue;
	public final T getValue()
	{
		return privateValue;
	}
	private void setValue(T value)
	{
		privateValue = value;
	}

	@Override
	public boolean equals(Object obj)
	{
		LocationTagged<T> other = (LocationTagged<T>)obj;
		return other != null && equals(other.getLocation().clone(), 
				getLocation().clone()) && equals(other.getValue(), getValue());
	}
	
	public boolean equals(Object obj,Object other){
		return obj.equals(other);
	}

	@Override
	public int hashCode()
	{
		return HashCodeCombiner.Start().Add(getLocation().clone()).Add(getValue()).getCombinedHash();
	}

	@Override
	public String toString()
	{
		return getValue().toString();
	}

	public final String toString(String format, String formatProvider)
	{
		if (StringHelper.isNullOrEmpty(format))
		{
			format = "P";
		}
		if (formatProvider == null)
		{
			formatProvider ="" ;
		}
//C# TO JAVA CONVERTER NOTE: The following 'switch' operated on a string member and was converted to Java 'if-else' logic:
//		switch (format.ToUpperInvariant())
//ORIGINAL LINE: case "F":
		if (format.toUpperCase().equals("F"))
		{
				return String.format( "%s@%s", getValue(), getLocation().clone());
		}
		else
		{
				return getValue().toString();
		}
	}

 
	/*public static implicit operator T(LocationTagged<T> value)
	{
		return value.getValue();
	}*/

	public  boolean opEquality(LocationTagged<T> left, LocationTagged<T> right)
	{
		return equals(left, right);
	}

	public  boolean opInequality(LocationTagged<T> left, LocationTagged<T> right)
	{
		return !equals(left, right);
	}
}