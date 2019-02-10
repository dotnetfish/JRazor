package com.superstudio.web.razor.parser.syntaxTree;

import com.superstudio.commons.IEquatable;
import com.superstudio.commons.exception.OperationCanceledException;
import com.superstudio.web.razor.parser.ParserVisitor;
import com.superstudio.web.razor.text.SourceLocation;



public abstract class SyntaxTreeNode implements IEquatable<Object>
{
	private Block privateParent;
	public final Block getParent()
	{
		return privateParent;
	}
	public final void setParent(Block value)
	{
		privateParent = value;
	}

	/** 
	 Returns true if this element is a block (to avoid casting)
	 
	*/
	public abstract boolean getIsBlock();

	/** 
	 The length of all the content contained in this node
	 
	*/
	public abstract int getLength();

	/** 
	 The start point of this node
	 
	*/
	public abstract SourceLocation getStart();

	/** 
	 Accepts a parser visitor, calling the appropriate visit method and passing in this instance
	 
	 @param visitor The visitor to accept
	 * @throws OperationCanceledException 
	 * @throws Exception 
	*/
	public abstract void accept(ParserVisitor visitor) throws Exception;

	/** 
	 Determines if the specified node is equivalent to this node
	 
	 @param node The node to compare this node with
	 @return 
	 true if the provided node has all the same content and metadata, though the specific quantity and type of symbols may be different.
	 
	*/
	public abstract boolean equivalentTo(SyntaxTreeNode node);
	
	@Override
	public  boolean equals(Object obj, Object other){
		return obj.equals(other);
	}
	
}