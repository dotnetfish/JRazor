package com.superstudio.jrazor.parser.syntaxTree;

import java.util.ArrayList;
import java.util.List;

import com.superstudio.jrazor.generator.BlockCodeGenerator;
import com.superstudio.jrazor.generator.IBlockCodeGenerator;




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

 
	//[SuppressMessage("Microsoft.Naming", "CA1721:PropertyNamesShouldNotMatchGetMethods", Justification = "Type is the most appropriate name for this property and there is little chance of confusion with GetType")]
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