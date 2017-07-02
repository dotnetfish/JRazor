package com.superstudio.template.language.mvc;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.MvcResources;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.language.java.parser.JavaCodeParser;
import com.superstudio.web.razor.generator.*;
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
		super.AcceptAndMoveNext();
		this._endInheritsLocation = new SourceLocation(super.getCurrentLocation());
		super.inheritsDirectiveCore();
		this.CheckForInheritsAndModelStatements();
	}
	private void CheckForInheritsAndModelStatements()
	{
		if (this._modelStatementFound && this._endInheritsLocation != null)
		{
			this.getContext().OnError(this._endInheritsLocation, String.format(CultureInfo.CurrentCulture,
					MvcResources.MvcRazorCodeParser_CannotHaveModelAndInheritsKeyword, new Object[] {"model"}));
		}
	}
	protected void ModelDirective()
	{
		super.AcceptAndMoveNext();
		SourceLocation currentLocation = super.getCurrentLocation();
		super.baseTypeDirective(String.format(MvcResources.MvcRazorCodeParser_ModelKeywordMustBeFollowedByTypeName, new Object[] {"model"}),
				(a)->this.CreateModelCodeGenerator(a));
		if (this._modelStatementFound)
		{
			this.getContext().OnError(currentLocation, String.format(CultureInfo.CurrentCulture, MvcResources.MvcRazorCodeParser_OnlyOneModelStatementIsAllowed, new Object[] {"model"}));
		}
		this._modelStatementFound = true;
		this.CheckForInheritsAndModelStatements();
	}
	private SpanCodeGenerator CreateModelCodeGenerator(String model)
	{
		return new SetModelTypeCodeGenerator(model,GenericTypeFormatString);
	}
}