package com.superstudio.codedom;

import java.io.Serializable;


//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeTypeMember : CodeObject
public class CodeTypeMember extends CodeObject implements Serializable
{
	private MemberAttributes attributes = MemberAttributes.forValue(20482);

	private String name;

	private CodeCommentStatementCollection comments = new CodeCommentStatementCollection();

	private CodeAttributeDeclarationCollection customAttributes;

	private CodeLinePragma linePragma;


//ORIGINAL LINE: [OptionalField] private CodeDirectiveCollection startDirectives;
	private CodeDirectiveCollection startDirectives;


//ORIGINAL LINE: [OptionalField] private CodeDirectiveCollection endDirectives;
	private CodeDirectiveCollection endDirectives;

	public final String getName()
	{
		if (this.name != null)
		{
			return this.name;
		}
		return "";
	}
	public final void setName(String value)
	{
		this.name = value;
	}

	public final MemberAttributes getAttributes()
	{
		return this.attributes;
	}
	public final void setAttributes(MemberAttributes value)
	{
		this.attributes = value;
	}

	public final CodeAttributeDeclarationCollection getCustomAttributes()
	{
		if (this.customAttributes == null)
		{
			this.customAttributes = new CodeAttributeDeclarationCollection();
		}
		return this.customAttributes;
	}
	public final void setCustomAttributes(CodeAttributeDeclarationCollection value)
	{
		this.customAttributes = value;
	}

	public final CodeLinePragma getLinePragma()
	{
		return this.linePragma;
	}
	public final void setLinePragma(CodeLinePragma value)
	{
		this.linePragma= value;
	}

	public final CodeCommentStatementCollection getComments()
	{
		return this.comments;
	}

	public final CodeDirectiveCollection getStartDirectives()
	{
		if (this.startDirectives == null)
		{
			this.startDirectives = new CodeDirectiveCollection();
		}
		return this.startDirectives;
	}

	public final CodeDirectiveCollection getEndDirectives()
	{
		if (this.endDirectives == null)
		{
			this.endDirectives = new CodeDirectiveCollection();
		}
		return this.endDirectives;
	}
}