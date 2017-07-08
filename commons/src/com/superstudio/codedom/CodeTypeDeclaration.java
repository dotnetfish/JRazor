package com.superstudio.codedom;
import java.io.Serializable;


import com.superstudio.commons.EventArgs;
import com.superstudio.commons.EventListener;
import com.superstudio.commons.TypeAttributes;
public class CodeTypeDeclaration extends CodeTypeMember implements Serializable
{
	private TypeAttributes attributes = TypeAttributes.forValue(TypeAttributes.Public);

	private CodeTypeReferenceCollection baseTypes = new CodeTypeReferenceCollection();

	private CodeTypeMemberCollection members = new CodeTypeMemberCollection();

	private boolean isEnum;

	private boolean isStruct;

	private int populated;


//ORIGINAL LINE: [OptionalField] private CodeTypeParameterCollection typeParameters;
	private CodeTypeParameterCollection typeParameters;


//ORIGINAL LINE: [OptionalField] private bool isPartial;
	private boolean isPartial;



//ORIGINAL LINE: [method: CompilerGenerated][CompilerGenerated] public event EventListener PopulateBaseTypes;
	private EventListener<EventArgs.EmptyEventArgs> populateBaseTypes;

public final TypeAttributes getTypeAttributes()
	{
		return this.attributes;
	}
	public final void setTypeAttributes(TypeAttributes value)
	{
		this.attributes = value;
	}

	public final CodeTypeReferenceCollection getBaseTypes()
	{
		if ((this.populated & 1) == 0)
		{
			this.populated |= 1;
			if (this.getPopulateBaseTypes() != null)
			{
				this.getPopulateBaseTypes().execute(this, EventArgs.Empty);
			}
		}
		return this.baseTypes;
	}

	public final boolean getIsClass()
	{
		return (this.attributes.getValue() & TypeAttributes.ClassSemanticsMask) == TypeAttributes.NotPublic && !this.isEnum && !this.isStruct;
	}
	public final void setIsClass(boolean value)
	{
		if (value)
		{
			this.attributes = TypeAttributes.forValue(this.attributes.getValue() & ~TypeAttributes.ClassSemanticsMask);
			this.attributes = TypeAttributes.forValue(this.attributes.getValue() | TypeAttributes.NotPublic);
			this.isStruct = false;
			this.isEnum = false;
		}
	}

	public final boolean getIsStruct()
	{
		return this.isStruct;
	}
	public final void setIsStruct(boolean value)
	{
		if (value)
		{
			this.attributes = TypeAttributes.forValue(this.attributes.getValue() & ~TypeAttributes.ClassSemanticsMask);
			this.isStruct = true;
			this.isEnum = false;
			return;
		}
		this.isStruct = false;
	}

	public final boolean getIsEnum()
	{
		return this.isEnum;
	}
	public final void setIsEnum(boolean value)
	{
		if (value)
		{
			this.attributes = TypeAttributes.forValue(this.attributes.getValue() & ~TypeAttributes.ClassSemanticsMask);
			this.isStruct = false;
			this.isEnum = true;
			return;
		}
		this.isEnum = false;
	}

	public final boolean getIsInterface()
	{
		return (this.attributes.getValue() & TypeAttributes.ClassSemanticsMask) == TypeAttributes.ClassSemanticsMask;
	}
	public final void setIsInterface(boolean value)
	{
		if (value)
		{
			this.attributes = TypeAttributes.forValue(this.attributes.getValue() & ~TypeAttributes.ClassSemanticsMask);
			this.attributes = TypeAttributes.forValue(this.attributes.getValue() | TypeAttributes.ClassSemanticsMask);
			this.isStruct = false;
			this.isEnum = false;
			return;
		}
		this.attributes = TypeAttributes.forValue(this.attributes.getValue() & ~TypeAttributes.ClassSemanticsMask);
	}

	public final boolean getIsPartial()
	{
		return this.isPartial;
	}
	public final void setIsPartial(boolean value)
	{
		this.isPartial = value;
	}

	public final CodeTypeMemberCollection getMembers()
	{
		if ((this.populated & 2) == 0)
		{
			this.populated |= 2;
			if (this.getPopulateBaseTypes() != null)
			{
				this.getPopulateBaseTypes().execute(this, EventArgs.Empty);
			}
		}
		return this.members;
	}


//ORIGINAL LINE: [ComVisible(false)] public CodeTypeParameterCollection TypeParameters
	public final CodeTypeParameterCollection getTypeParameters()
	{
		if (this.typeParameters == null)
		{
			this.typeParameters = new CodeTypeParameterCollection();
		}
		return this.typeParameters;
	}

	public CodeTypeDeclaration()
	{
	}

	public CodeTypeDeclaration(String name)
	{
		super.setName(name);
	}
	public EventListener<EventArgs.EmptyEventArgs> getPopulateBaseTypes() {
		return populateBaseTypes;
	}
	public void setPopulateBaseTypes(EventListener<EventArgs.EmptyEventArgs> populateBaseTypes) {
		this.populateBaseTypes = populateBaseTypes;
	}
}