package com.superstudio.web.razor.generator;


import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.HashCodeCombiner;
import com.superstudio.web.razor.parser.syntaxTree.Block;
import com.superstudio.web.razor.parser.syntaxTree.BlockType;
import com.superstudio.web.razor.text.LocationTagged;
import com.superstudio.web.razor.text.SourceLocation;



public class DynamicAttributeBlockCodeGenerator extends BlockCodeGenerator
{
	private static final String ValueWriterName = "__razor_attribute_value_writer";
	private String _oldTargetWriter;
	private boolean _isExpression;
	private ExpressionRenderingMode _oldRenderingMode = ExpressionRenderingMode.forValue(0);

	public DynamicAttributeBlockCodeGenerator(LocationTagged<String> prefix, int offset, int line, int col)
	{
		this(prefix, new SourceLocation(offset, line, col));
	}

	public DynamicAttributeBlockCodeGenerator(LocationTagged<String> prefix, SourceLocation valueStart)
	{
		setPrefix(prefix);
		setValueStart(valueStart);
	}

	private LocationTagged<String> privatePrefix;
	public final LocationTagged<String> getPrefix()
	{
		return privatePrefix;
	}
	private void setPrefix(LocationTagged<String> value)
	{
		privatePrefix = value;
	}
	private SourceLocation privateValueStart;
	public final SourceLocation getValueStart()
	{
		return privateValueStart;
	}
	private void setValueStart(SourceLocation value)
	{
		privateValueStart = value;
	}

	@Override
	public void generateStartBlockCode(Block target, CodeGeneratorContext context)
	{
		if (context.getHost().getDesignTimeMode())
		{
			return; // Don't generate anything!
		}

		// What kind of block is nested within
		String generatedCode;


		Block child = (Block) CollectionHelper.firstOrDefault(target.getChildren()
				,n -> n.getIsBlock());//.FirstOrDefault();
		if (child != null && child.getType() == BlockType.Expression)
		{
			_isExpression = true;

			generatedCode = context.BuildCodeString(cw ->
			{
				cw.writeParameterSeparator();
				cw.writeStartMethodInvoke("Tuple.create");
				cw.writeLocationTaggedString(getPrefix());
				cw.writeParameterSeparator();
				//cw.writeStartMethodInvoke("Tuple.create", "Object", "Int32");
				cw.writeStartMethodInvoke("Tuple.create", "Object", "Integer");
			}
		   );

			_oldRenderingMode = context.getExpressionRenderingMode();
			context.setExpressionRenderingMode(ExpressionRenderingMode.InjectCode);
		}
		else
		{

			generatedCode = context.BuildCodeString(cw ->
			{
				cw.writeParameterSeparator();
				cw.writeStartMethodInvoke("Tuple.create");
				cw.writeLocationTaggedString(getPrefix());
				cw.writeParameterSeparator();
				cw.writeStartMethodInvoke("Tuple.create", "Object", "Integer");
				cw.writeStartConstructor(context.getHost().getGeneratedClassContext().getTemplateTypeName());
				cw.writeStartLambdaDelegate(ValueWriterName);
			}
		   );
		}

		context.MarkEndOfGeneratedCode();
		context.BufferStatementFragment(generatedCode);

		_oldTargetWriter = context.getTargetWriterName();
		context.setTargetWriterName(ValueWriterName);
	}

	@Override
	public void generateEndBlockCode(Block target, CodeGeneratorContext context)
	{
		if (context.getHost().getDesignTimeMode())
		{
			return; // Don't generate anything!
		}

		String generatedCode;
		if (_isExpression)
		{
				// literal: false - This attribute value is not a literal value, it is dynamically generated

			generatedCode = context.BuildCodeString(cw ->
			{
				cw.writeParameterSeparator();
				cw.writeSnippet((new Integer(getValueStart().getAbsoluteIndex())).toString());
				cw.writeEndMethodInvoke();
				cw.writeParameterSeparator();
				cw.writeBooleanLiteral(false);
				cw.writeEndMethodInvoke();
				cw.writeLineContinuation();
			}
		   );
			context.setExpressionRenderingMode(_oldRenderingMode);
		}
		else
		{
				// literal: false - This attribute value is not a literal value, it is dynamically generated

			generatedCode = context.BuildCodeString(cw ->
			{
				cw.writeEndLambdaDelegate();
				cw.writeEndConstructor();
				cw.writeParameterSeparator();
				cw.writeSnippet((new Integer(getValueStart().getAbsoluteIndex())).toString());
				cw.writeEndMethodInvoke();
				cw.writeParameterSeparator();
				cw.writeBooleanLiteral(false);
				cw.writeEndMethodInvoke();
				cw.writeLineContinuation();
			}
		   );
		}

		context.AddStatement(generatedCode);
		context.setTargetWriterName(_oldTargetWriter);
	}

	@Override
	public String toString()
	{
		return String.format( "DynAttr:%s", getPrefix());
	}

	@Override
	public boolean equals(Object obj)
	{
		DynamicAttributeBlockCodeGenerator other = (DynamicAttributeBlockCodeGenerator)((obj instanceof DynamicAttributeBlockCodeGenerator) ? obj : null);
		return other != null && equals(other.getPrefix(), getPrefix());
	}

	@Override
	public int hashCode()
	{
		return HashCodeCombiner.Start().Add(getPrefix()).getCombinedHash();
	}

	@Override
	public boolean equals(Object obj, Object others) {
		// TODO Auto-generated method stub
		return obj.equals(others);
	}
}