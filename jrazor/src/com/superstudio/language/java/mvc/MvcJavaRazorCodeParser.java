package com.superstudio.language.java.mvc;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.MvcResources;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.language.java.parser.JavaCodeParser;
import com.superstudio.web.razor.generator.SpanCodeGenerator;
import com.superstudio.web.razor.text.SourceLocation;
public class MvcJavaRazorCodeParser extends JavaCodeParser
{
	//private static final String ModelKeyword = "model";
	//private static final String GenericTypeFormatString = "{0}<{1}>";
	private SourceLocation _endInheritsLocation = null;
	private boolean _modelStatementFound;
	public MvcJavaRazorCodeParser()
	{
		super.mapDirectives(()->this.modelDirective(), "model");
	}
	@Override
	protected void inheritsDirective()
	{
		super.AcceptAndMoveNext();
		this._endInheritsLocation = new SourceLocation(super.getCurrentLocation());
		super.inheritsDirectiveCore();
		this.checkForInheritsAndModelStatements();
	}
	private void checkForInheritsAndModelStatements()
	{
		if (this._modelStatementFound && this._endInheritsLocation != null)
		{
			this.getContext().OnError(this._endInheritsLocation, String.format(CultureInfo.CurrentCulture,
					MvcResources.MvcRazorCodeParser_CannotHaveModelAndInheritsKeyword, new Object[] {"model"}));
		}
	}
	protected void modelDirective()
	{
		super.AcceptAndMoveNext();
		SourceLocation currentLocation = super.getCurrentLocation();
		super.baseTypeDirective(StringHelper.format(CultureInfo.CurrentCulture, MvcResources.MvcRazorCodeParser_ModelKeywordMustBeFollowedByTypeName, new Object[] {"model"}),
				(a)->this.createModelCodeGenerator(a));
		if (this._modelStatementFound)
		{
			this.getContext().OnError(currentLocation, String.format(CultureInfo.CurrentCulture, MvcResources.MvcRazorCodeParser_OnlyOneModelStatementIsAllowed, new Object[] {"model"}));
		}
		this._modelStatementFound = true;
		this.checkForInheritsAndModelStatements();
	}
	private SpanCodeGenerator createModelCodeGenerator(String model)
	{
		return new SetModelTypeCodeGenerator(model, "{0}<{1}>");
	}
}