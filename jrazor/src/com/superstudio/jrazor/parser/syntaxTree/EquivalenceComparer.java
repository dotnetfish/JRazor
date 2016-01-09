package com.superstudio.jrazor.parser.syntaxTree;

import com.superstudio.commons.IEqualityComparer;

public class EquivalenceComparer implements IEqualityComparer<SyntaxTreeNode>
{
	@Override
	public final boolean equals(SyntaxTreeNode x, SyntaxTreeNode y)
	{
		return x.equivalentTo(y);
	}
	@Override
	public final int hashCode(SyntaxTreeNode obj)
	{
		return obj.hashCode();
	}
}