package com.superstudio.web.razor.generator;

import com.superstudio.commons.HashCodeCombiner;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.web.razor.parser.syntaxTree.*;
import com.superstudio.web.razor.text.*;



public class AttributeBlockCodeGenerator extends BlockCodeGenerator
{
	public AttributeBlockCodeGenerator(String name, LocationTagged<String> prefix, LocationTagged<String> suffix)
	{
		setName(name);
		setPrefix(prefix);
		setSuffix(suffix);
	}

	public AttributeBlockCodeGenerator(LocationTagged<String> name, LocationTagged<String> prefix,
			LocationTagged<String> suffix) {
		// TODO Auto-generated constructor stub
		setName(name.getValue());
		setPrefix(prefix);
		setSuffix(suffix);
	}

	private String privateName;
	public final String getName()
	{
		return privateName;
	}
	private void setName(String value)
	{
		privateName = value;
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
	private LocationTagged<String> privateSuffix;
	public final LocationTagged<String> getSuffix()
	{
		return privateSuffix;
	}
	private void setSuffix(LocationTagged<String> value)
	{
		privateSuffix = value;
	}

	@Override
	public void generateStartBlockCode(Block target, CodeGeneratorContext context)
	{
		if (context.getHost().getDesignTimeMode())
		{
			return; // Don't generate anything!
		}
		context.FlushBufferedStatement();
			// In VB, we need a line continuation
		context.AddStatement(context.BuildCodeString(cw ->
		{
			if (!StringHelper.isNullOrEmpty(context.getTargetWriterName()))
			{
				cw.writeStartMethodInvoke(context.getHost().getGeneratedClassContext().getWriteAttributeToMethodName());
				cw.writeSnippet(context.getTargetWriterName());
				cw.writeParameterSeparator();
			}
			else
			{
				cw.writeStartMethodInvoke(context.getHost().getGeneratedClassContext().getWriteAttributeMethodName());
			}
			try {
				cw.writeStringLiteral(getName());
			} catch (Exception e) {
				
				e.printStackTrace();
			}
			cw.writeParameterSeparator();
			cw.writeLocationTaggedString(getPrefix());
			cw.writeParameterSeparator();
			cw.writeLocationTaggedString(getSuffix());
			cw.writeLineContinuation();
		}
	   ));
	}

	@Override
	public void generateEndBlockCode(Block target, CodeGeneratorContext context)
	{
		if (context.getHost().getDesignTimeMode())
		{
			return; // Don't generate anything!
		}
		context.FlushBufferedStatement();

		context.AddStatement(context.BuildCodeString(cw ->
		{
			cw.writeEndMethodInvoke();
			cw.writeEndStatement();
		}
	   ));
	}

	@Override
	public String toString()
	{
		return String.format( "Attr:{0},{1:F},{2:F}", getName(), getPrefix(), getSuffix());
	}

	@Override
	public boolean equals(Object obj)
	{
		AttributeBlockCodeGenerator other = (AttributeBlockCodeGenerator)((obj instanceof AttributeBlockCodeGenerator) ? obj : null);
		return other != null && StringHelper.stringsEqual(other.getName(), getName()) 
				&& equals(other.getPrefix(), getPrefix()) 
				&& equals(other.getSuffix(), getSuffix());
	}

	@Override
	public int hashCode()
	{
		return HashCodeCombiner.Start().Add(getName()).Add(getPrefix()).Add(getSuffix()).getCombinedHash();
	}
	@Override
	public boolean equals(Object obj, Object others) {
		// TODO Auto-generated method stub
		
		return obj.equals(others);
	}
}