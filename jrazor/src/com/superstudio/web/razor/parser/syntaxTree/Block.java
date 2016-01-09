package com.superstudio.web.razor.parser.syntaxTree;

import com.superstudio.commons.CollectionHelper;
import com.superstudio.web.razor.generator.IBlockCodeGenerator;
import com.superstudio.web.razor.parser.ParserVisitor;
import com.superstudio.web.razor.text.SourceLocation;
import com.superstudio.web.razor.text.TextChange;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class Block extends SyntaxTreeNode {
	public Block(BlockBuilder source) {
		if (source.getType() == null) {
			// throw new
			// InvalidOperationException(RazorResources.getBlock_Type_Not_Specified());
			// throw new
			// InvalidOperationException(RazorResources.getBlock_Type_Not_Specified());
		}
		setType(source.getType());
		setChildren(source.getChildren());
		setName(source.getName());
		setCodeGenerator(source.getCodeGenerator());
		source.reset();

		for (SyntaxTreeNode node : getChildren()) {
			node.setParent(this);
		}
	}

	public Block(BlockType type, 
			List<SyntaxTreeNode> contents, IBlockCodeGenerator generator) {
		setType(type);
		setCodeGenerator(generator);
		setChildren(contents);
	}



	private BlockType privateType = BlockType.forValue(0);

	public final BlockType getType() {
		return privateType;
	}

	private void setType(BlockType value) {
		privateType = value;
	}

	private List<SyntaxTreeNode> privateChildren;

	public final List<SyntaxTreeNode> getChildren() {
		return privateChildren;
	}

	private void setChildren(List<SyntaxTreeNode> value) {
		privateChildren = value;
	}

	private String privateName;

	public final String getName() {
		return privateName;
	}

	private void setName(String value) {
		privateName = value;
	}

	private IBlockCodeGenerator privateCodeGenerator;

	public final IBlockCodeGenerator getCodeGenerator() {
		return privateCodeGenerator;
	}

	private void setCodeGenerator(IBlockCodeGenerator value) {
		privateCodeGenerator = value;
	}

	@Override
	public boolean getIsBlock() {
		return true;
	}

	@Override
	public SourceLocation getStart() {
		SyntaxTreeNode child = CollectionHelper.firstOrDefault(getChildren());// .firstOrDefault();
		if (child == null) {
			return SourceLocation.Zero;
		} else {
			return child.getStart();
		}
	}

	@Override
	public int getLength() {

		// methods are not converted
		return CollectionHelper.sum(getChildren(), child -> child.getLength());
	}

	public final Span findFirstDescendentSpan() {
		SyntaxTreeNode current = this;
		while (current != null && current.getIsBlock()) {
			current = CollectionHelper.firstOrDefault(((Block) current).getChildren());
		}
		return (Span) ((current instanceof Span) ? current : null);
	}

	public final Span findLastDescendentSpan() {
		SyntaxTreeNode current = this;
		while (current != null && current.getIsBlock()) {
			current = CollectionHelper.lastOrDefault(((Block) current).getChildren());
		}
		return (Span) ((current instanceof Span) ? current : null);
	}

	@Override
	public void accept(ParserVisitor visitor) throws  Exception {
		
			visitor.visitBlock(this);
		
		
		
	}

	@Override
	public String toString() {
		return String.format("%s Block at %s::%d (Gen:%s)", 
				getType(), getStart().clone(), getLength(),
				getCodeGenerator());
	}

	@Override
	public boolean equals(Object obj) {
		Block other = (Block) ((obj instanceof Block) ? obj : null);
		
		return  other != null && getType() == other.getType() 
				&& equals(getCodeGenerator(), other.getCodeGenerator())
				&& ChildrenEqual(getChildren(), other.getChildren());
	}

	@Override
	public int hashCode() {
		return getType().getValue();
	}

	public final Iterable<Span> Flatten() {
	
		List<Span> allItem=new ArrayList<Span>();
		List<SyntaxTreeNode> items=getChildren();
		for(SyntaxTreeNode item :items){
			if(item instanceof Span){
				allItem.add((Span)item);
			}else{
				Block block=(Block)item;
				Iterable<Span> subItems=block.Flatten();
				for(Span subItem:subItems){
						allItem.add(subItem);
				}
			}
		}
		
		return new Iterable<Span>() {

			@Override
			public Iterator<Span> iterator() {
				return new Iterator<Span>(){
					private int index=0;
					@Override
					public boolean hasNext() {
						
						return index<allItem.size();
					}

					@Override
					public Span next() {
						
						
						Span result= allItem.get(index);
						index++;
						return result;
					}
				};
				
			}
		};
		
		
	}

	public final Span LocateOwner(TextChange change) {
		// Ask each child recursively
		Span owner = null;
		for (SyntaxTreeNode element : getChildren()) {
			Span span = (Span) ((element instanceof Span) ? element : null);
			if (span == null) {
				owner = ((Block) element).LocateOwner(change);
			} else {
				if (change.getOldPosition() < span.getStart().getAbsoluteIndex()) {
					// Early escape for cases where changes overlap multiple
					// spans
					// In those cases, the span will return false, and we don't
					// want to search the whole tree
					// So if the current span starts after the change, we know
					// we've searched as far as we need to
					break;
				}
				owner = span.getEditHandler().ownsChange(span, change) ? span : owner;
			}

			if (owner != null) {
				break;
			}
		}
		return owner;
	}

	private static boolean ChildrenEqual(Iterable<SyntaxTreeNode> left, Iterable<SyntaxTreeNode> right) {
		java.util.Iterator<SyntaxTreeNode> leftEnum = left.iterator();
		java.util.Iterator<SyntaxTreeNode> rightEnum = right.iterator();
		while (leftEnum.hasNext()) {
			//if (!rightEnum.hasNext() || !equals(leftEnum.next(), rightEnum.next())) // More
			if (!rightEnum.hasNext() || !leftEnum.next().equals(rightEnum.next())) 																	// items
																					// in
																					// left
																					// than
																					// in
																					// right
			{
				// Nodes are not equal
				return false;
			}
		}
		return !rightEnum.hasNext();
	}

	@Override
	public boolean equivalentTo(SyntaxTreeNode node) {
		
		if(!(node instanceof Block))return false;
		Block other = (Block) node ;
		if (other == null || other.getType() != getType()) {
			return false;
		}
		
		return CollectionHelper.sequeceEqual(getChildren(), other.getChildren(), new EquivalenceComparer());
	}

	@Override
	public boolean equals(Object obj, Object others) {
		//

		return obj.equals(others);
	}
}