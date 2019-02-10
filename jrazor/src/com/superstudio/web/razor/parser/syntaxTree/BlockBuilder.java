package com.superstudio.web.razor.parser.syntaxTree;

import com.superstudio.web.razor.generator.BlockCodeGenerator;
import com.superstudio.web.razor.generator.IBlockCodeGenerator;

import java.util.ArrayList;
import java.util.List;



/**
 * @author cloudartisan
 */
public class BlockBuilder
{
	public BlockBuilder()
	{
		reset();
	}

	public BlockBuilder(Block original)
	{
		setType(original.getType());
		List<SyntaxTreeNode> list=new ArrayList<SyntaxTreeNode>();
		for(SyntaxTreeNode node :original.getChildren()){
			list.add(node);
		}	
		
		setChildren(list);
		setName(original.getName());
		setCodeGenerator(original.getCodeGenerator());
	}


		private BlockType privateType;
	public final BlockType getType()
	{
		return privateType;
	}
	public final void setType(BlockType value)
	{
		privateType = value;
	}

	private java.util.List<SyntaxTreeNode> privateChildren;
	public final java.util.List<SyntaxTreeNode> getChildren()
	{
		return privateChildren;
	}
	private void setChildren(java.util.List<SyntaxTreeNode> value)
	{
		privateChildren = value;
	}
	private String privateName;
	public final String getName()
	{
		return privateName;
	}
	public final void setName(String value)
	{
		privateName = value;
	}
	private IBlockCodeGenerator privateCodeGenerator;
	public final IBlockCodeGenerator getCodeGenerator()
	{
		return privateCodeGenerator;
	}
	public final void setCodeGenerator(IBlockCodeGenerator value)
	{
		privateCodeGenerator = value;
	}

	public final Block build()
	{
		return new Block(this);
	}

	public final void reset()
	{
		setType(null);
		setName(null);
		setChildren(new java.util.ArrayList<SyntaxTreeNode>());
		setCodeGenerator(BlockCodeGenerator.Null);
	}
}