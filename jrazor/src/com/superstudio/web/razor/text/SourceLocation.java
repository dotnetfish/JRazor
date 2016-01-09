package com.superstudio.web.razor.text;

import com.superstudio.commons.IEquatable;

import java.io.Serializable;




//ORIGINAL LINE: public struct SourceLocation : IEquatable<SourceLocation>, IComparable<SourceLocation>
public final class SourceLocation implements IEquatable<SourceLocation>, Comparable<SourceLocation>, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5953150470135370929L;
	public static final SourceLocation Undefined = createUndefined();
	public static final SourceLocation Zero = new SourceLocation(0, 0, 0);

	private int _absoluteIndex;
	private int _lineIndex;
	private int _characterIndex;

	public SourceLocation(int absoluteIndex, int lineIndex, int characterIndex)
	{
		_absoluteIndex = absoluteIndex;
		_lineIndex = lineIndex;
		_characterIndex = characterIndex;
	}

	public SourceLocation() {
		// TODO Auto-generated constructor stub
	}

	public SourceLocation(SourceLocation currentLocation) {
		// TODO Auto-generated constructor stub
		this._absoluteIndex=currentLocation._absoluteIndex;
		this._characterIndex=currentLocation._characterIndex;
		this._lineIndex=currentLocation._lineIndex;
	}

	public int getAbsoluteIndex()
	{
		return _absoluteIndex;
	}

	/** 
	 Gets the 1-based index of the line referred to by this Source Location.
	 
	*/
	public int getLineIndex()
	{
		return _lineIndex;
	}

	public int getCharacterIndex()
	{
		return _characterIndex;
	}

	@Override
	public String toString()
	{
		return String.format( "(%d:%d,%d)", getAbsoluteIndex(), getLineIndex(), getCharacterIndex());
	}

	@Override
	public boolean equals(Object obj)
	{
		return (obj instanceof SourceLocation) && equals((SourceLocation)obj);
	}

	@Override
	public int hashCode()
	{
		// LineIndex and CharacterIndex can be calculated from AbsoluteIndex and the document content.
		return getAbsoluteIndex();
	}

	public boolean equals(SourceLocation other)
	{
		return getAbsoluteIndex() == other.getAbsoluteIndex() && getLineIndex() == other.getLineIndex() && getCharacterIndex() == other.getCharacterIndex();
	}
	@Override
	public int compareTo(SourceLocation other)
	{
		return (new Integer(getAbsoluteIndex())).compareTo(other.getAbsoluteIndex());
	}

	public static SourceLocation advance(SourceLocation left, String text)
	{
		SourceLocationTracker tracker = new SourceLocationTracker(left);
		tracker.updateLocation(text);
		return tracker.getCurrentLocation();
	}

	public static SourceLocation add(SourceLocation left, SourceLocation right)
	{
		if (right.getLineIndex() > 0)
		{
			// Column index doesn't matter
			return new SourceLocation(left.getAbsoluteIndex() + right.getAbsoluteIndex(), left.getLineIndex() + right.getLineIndex(), right.getCharacterIndex());
		}
		else
		{
			return new SourceLocation(left.getAbsoluteIndex() + right.getAbsoluteIndex(), left.getLineIndex() + right.getLineIndex(), left.getCharacterIndex() + right.getCharacterIndex());
		}
	}

	public static SourceLocation subtract(SourceLocation left, SourceLocation right)
	{
		return new SourceLocation(left.getAbsoluteIndex() - right.getAbsoluteIndex(), left.getLineIndex() - right.getLineIndex(), left.getLineIndex() != right.getLineIndex() ? left.getCharacterIndex() : left.getCharacterIndex() - right.getCharacterIndex());
	}

	private static SourceLocation createUndefined()
	{
		SourceLocation sl = new SourceLocation();
		sl._absoluteIndex = -1;
		sl._lineIndex = -1;
		sl._characterIndex = -1;
		return sl;
	}

	public static boolean opLessThan(SourceLocation left, SourceLocation right)
	{
		return left.compareTo(right) < 0;
	}

	public static boolean opGreaterThan(SourceLocation left, SourceLocation right)
	{
		return left.compareTo(right) > 0;
	}

	public static boolean opEquality(SourceLocation left, SourceLocation right)
	{
		return left.equals(right);
	}

	public static boolean opInequality(SourceLocation left, SourceLocation right)
	{
		return !left.equals(right);
	}

	public static SourceLocation opAddition(SourceLocation left, SourceLocation right)
	{
		return add(left, right);
	}

	public static SourceLocation opSubtraction(SourceLocation left, SourceLocation right)
	{
		return subtract(left, right);
	}

	public SourceLocation clone()
	{
		SourceLocation varCopy = new SourceLocation();

		varCopy._absoluteIndex = this._absoluteIndex;
		varCopy._lineIndex = this._lineIndex;
		varCopy._characterIndex = this._characterIndex;

		return varCopy;
	}

	@Override
	public boolean equals(SourceLocation obj, SourceLocation others) {
		// TODO Auto-generated method stub
		return obj.equals(others);
	}

	
	
}