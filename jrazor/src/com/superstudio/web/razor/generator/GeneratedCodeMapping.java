package com.superstudio.web.razor.generator;

import com.superstudio.commons.HashCodeCombiner;





public final class GeneratedCodeMapping
{
	public GeneratedCodeMapping(int startLine, int startColumn, int startGeneratedColumn, int codeLength)
	{
		this(0, startLine, startColumn, startGeneratedColumn, codeLength);
	}

	public GeneratedCodeMapping(int startOffset, int startLine, int startColumn, int startGeneratedColumn, int codeLength)
	{
		this((Integer)startOffset, startLine, startColumn, startGeneratedColumn, codeLength);
	}

	private GeneratedCodeMapping(Integer startOffset, int startLine, int startColumn, int startGeneratedColumn, int codeLength)
	{
		//this();
		if (startLine < 0)
		{
			//throw new ArgumentOutOfRangeException("startLine", String.format( CommonResources.getArgument_Must_Be_GreaterThanOrEqualTo(), "startLine", "0"));
		}
		if (startColumn < 0)
		{
			//throw new ArgumentOutOfRangeException("startColumn", String.format( CommonResources.getArgument_Must_Be_GreaterThanOrEqualTo(), "startColumn", "0"));
		}
		if (startGeneratedColumn < 0)
		{
			//throw new ArgumentOutOfRangeException("startGeneratedColumn", String.format( CommonResources.getArgument_Must_Be_GreaterThanOrEqualTo(), "startGeneratedColumn", "0"));
		}
		if (codeLength < 0)
		{
			//throw new ArgumentOutOfRangeException("codeLength", String.format( CommonResources.getArgument_Must_Be_GreaterThanOrEqualTo(), "codeLength", "0"));
		}

		setStartOffset(startOffset);
		setStartLine(startLine);
		setStartColumn(startColumn);
		setStartGeneratedColumn(startGeneratedColumn);
		setCodeLength(codeLength);
	}

	private Integer privateStartOffset;
	public Integer getStartOffset()
	{
		return privateStartOffset;
	}
	public void setStartOffset(Integer value)
	{
		privateStartOffset = value;
	}
	private int privateCodeLength;
	public int getCodeLength()
	{
		return privateCodeLength;
	}
	public void setCodeLength(int value)
	{
		privateCodeLength = value;
	}
	private int privateStartColumn;
	public int getStartColumn()
	{
		return privateStartColumn;
	}
	public void setStartColumn(int value)
	{
		privateStartColumn = value;
	}
	private int privateStartGeneratedColumn;
	public int getStartGeneratedColumn()
	{
		return privateStartGeneratedColumn;
	}
	public void setStartGeneratedColumn(int value)
	{
		privateStartGeneratedColumn = value;
	}
	private int privateStartLine;
	public int getStartLine()
	{
		return privateStartLine;
	}
	public void setStartLine(int value)
	{
		privateStartLine = value;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof GeneratedCodeMapping))
		{
			return false;
		}
		GeneratedCodeMapping other = (GeneratedCodeMapping)obj;
			   // Null means it matches the other no matter what.
		return getCodeLength() == other.getCodeLength() && getStartColumn() == other.getStartColumn() && getStartGeneratedColumn() == other.getStartGeneratedColumn() && getStartLine() == other.getStartLine() && (getStartOffset() == null || other.getStartOffset() == null || getStartOffset().equals(other.getStartOffset()));
	}

	@Override
	public String toString()
	{
		return String.format( "(%s, %d, %d) -> (?, %d) [%d]",
				getStartOffset() == null ? "?" : getStartOffset().toString(),
						getStartLine(), getStartColumn(), 
						getStartGeneratedColumn(), 
						getCodeLength());
	}

	@Override
	public int hashCode()
	{
		return HashCodeCombiner.Start().Add(getCodeLength()).Add(getStartColumn()).Add(getStartGeneratedColumn()).Add(getStartLine()).Add(getStartOffset()).getCombinedHash();
	}

	public static boolean opEquality(GeneratedCodeMapping left, GeneratedCodeMapping right)
	{
		return left.equals(right);
	}

	public static boolean opInequality(GeneratedCodeMapping left, GeneratedCodeMapping right)
	{
		return !left.equals(right);
	}
}