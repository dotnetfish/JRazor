package com.superstudio.template.language.mvc;


import com.superstudio.commons.MvcResources;
import com.superstudio.language.java.parser.JavaCodeParser;
import com.superstudio.web.razor.generator.SpanCodeGenerator;
import com.superstudio.web.razor.text.SourceLocation;
public class MvcJavaRazorCodeParser extends JavaCodeParser
{
	private static final String ModelKeyword = "model";
	private static final String GenericTypeFormatString = "%s<%s>";
	private SourceLocation _endInheritsLocation = null;
	private boolean _modelStatementFound;
	public MvcJavaRazorCodeParser()
	{
		super.mapDirectives(()->this.ModelDirective(), ModelKeyword);
	}
	@Override
	protected void inheritsDirective()
	{
		super.acceptAndMoveNext();
		this._endInheritsLocation = new SourceLocation(super.getCurrentLocation());
		super.inheritsDirectiveCore();
		this.CheckForInheritsAndModelStatements();
	}
	private void CheckForInheritsAndModelStatements()
	{
		if (this._modelStatementFound && this._endInheritsLocation != null)
		{
			this.getContext().onError(this._endInheritsLocation, String.format(MvcResources.MvcRazorCodeParser_CannotHaveModelAndInheritsKeyword, new Object[] {"model"}));
		}
	}
	protected void ModelDirective()
	{
		super.acceptAndMoveNext();
		SourceLocation currentLocation = super.getCurrentLocation();
		super.baseTypeDirective(String.format(MvcResources.MvcRazorCodeParser_ModelKeywordMustBeFollowedByTypeName, new Object[] {"model"}),
				(a)->this.CreateModelCodeGenerator(a));
		if (this._modelStatementFound)
		{
			this.getContext().onError(currentLocation, String.format(MvcResources.MvcRazorCodeParser_OnlyOneModelStatementIsAllowed, new Object[] {"model"}));
		}
		this._modelStatementFound = true;
		this.CheckForInheritsAndModelStatements();
	}
	private SpanCodeGenerator CreateModelCodeGenerator(String model)
	{
		return new SetModelTypeCodeGenerator(model,GenericTypeFormatString);
	}
}