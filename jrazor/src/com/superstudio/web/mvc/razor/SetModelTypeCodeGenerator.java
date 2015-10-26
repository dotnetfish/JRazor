package com.superstudio.web.mvc.razor;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.HashCodeCombiner;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.jrazor.generator.CodeGeneratorContext;
import com.superstudio.jrazor.generator.SetBaseTypeCodeGenerator;


public class SetModelTypeCodeGenerator extends SetBaseTypeCodeGenerator
{
	private String _genericTypeFormat;
	public SetModelTypeCodeGenerator(String modelType, String genericTypeFormat)
	{
		super(modelType);
		this._genericTypeFormat = genericTypeFormat;
	}
	@Override
	protected String resolveType(CodeGeneratorContext context, String baseType)
	{
		return String.format(CultureInfo.InvariantCulture, 
				this._genericTypeFormat, new Object[] {
				context.getHost().getDefaultBaseClass(), baseType});
	}
	@Override
	public boolean equals(Object obj)
	{
		SetModelTypeCodeGenerator setModelTypeCodeGenerator = (SetModelTypeCodeGenerator)((obj instanceof SetModelTypeCodeGenerator) ? obj : null);
		return setModelTypeCodeGenerator != null && super.equals(obj) && StringHelper.stringsEqual(this._genericTypeFormat, setModelTypeCodeGenerator._genericTypeFormat);
	}
	@Override
	public int hashCode()
	{
		HashCodeCombiner hashCodeCombiner = new HashCodeCombiner();
		hashCodeCombiner.AddInt32(super.hashCode());
		hashCodeCombiner.AddObject(this._genericTypeFormat);
		return hashCodeCombiner.getCombinedHash();
	}
	@Override
	public String toString()
	{
		return "Model:" + super.getBaseType();
	}
}