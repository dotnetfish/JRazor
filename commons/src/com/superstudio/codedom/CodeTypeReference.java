package com.superstudio.codedom;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.csharpbridge.StringHelper;


//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeTypeReference : CodeObject
public class CodeTypeReference extends CodeObject implements Serializable
{
	private String baseType;


//ORIGINAL LINE: [OptionalField] private bool isInterface;
	private boolean isInterface;

	private int arrayRank;

	private CodeTypeReference arrayElementType;


//ORIGINAL LINE: [OptionalField] private CodeTypeReferenceCollection typeArguments;
	private CodeTypeReferenceCollection typeArguments;


//ORIGINAL LINE: [OptionalField] private CodeTypeReferenceOptions referenceOptions;
	private CodeTypeReferenceOptions referenceOptions = CodeTypeReferenceOptions.forValue(1);


//ORIGINAL LINE: [OptionalField] private bool needsFixup;
	private boolean needsFixup;

	public final CodeTypeReference getArrayElementType()
	{
		return this.arrayElementType;
	}
	public final void setArrayElementType(CodeTypeReference value)
	{
		this.arrayElementType = value;
	}

	public final int getArrayRank()
	{
		return this.arrayRank;
	}
	public final void setArrayRank(int value)
	{
		this.arrayRank = value;
	}

	public final int getNestedArrayDepth()
	{
		if (this.arrayElementType == null)
		{
			return 0;
		}
		return 1 + this.arrayElementType.getNestedArrayDepth();
	}

	public final String getBaseType()
	{
		if (this.arrayRank > 0 && this.arrayElementType != null)
		{
			return this.arrayElementType.getBaseType();
		}
		if (StringHelper.isNullOrEmpty(this.baseType))
		{
			return "";
		}
		String text = this.baseType;
		if (this.needsFixup && this.getTypeArguments().size() > 0)
		{
			text = text + '`' + String.valueOf(this.getTypeArguments().size());
		}
		return text;
	}
	public final void setBaseType(String value)
	{
		this.baseType = value;
		this.Initialize(this.baseType);
	}


//ORIGINAL LINE: [ComVisible(false)] public CodeTypeReferenceOptions Options
	public final CodeTypeReferenceOptions getOptions()
	{
		return this.referenceOptions;
	}
	public final void setOptions(CodeTypeReferenceOptions value)
	{
		this.referenceOptions = value;
	}


//ORIGINAL LINE: [ComVisible(false)] public CodeTypeReferenceCollection TypeArguments
	public final CodeTypeReferenceCollection getTypeArguments()
	{
		if (this.arrayRank > 0 && this.arrayElementType != null)
		{
			return this.arrayElementType.getTypeArguments();
		}
		if (this.typeArguments == null)
		{
			this.typeArguments = new CodeTypeReferenceCollection();
		}
		return this.typeArguments;
	}

	public final boolean getIsInterface()
	{
		return this.isInterface;
	}

	public CodeTypeReference()
	{
		this.baseType = "";
		this.arrayRank = 0;
		this.arrayElementType = null;
	}

	public CodeTypeReference(java.lang.Class<?> type)
	{
		if (type == null)
		{
			throw new IllegalArgumentException("type");
		}
		if (type.isArray())
		{
			//TODO
			//this.arrayRank = type.GetArrayRank();
			//this.arrayRank = type.();
			this.arrayElementType = new CodeTypeReference(type.getComponentType());
			this.baseType = null;
		}
		else
		{
			this.InitializeFromType(type);
			this.arrayRank = 0;
			this.arrayElementType = null;
		}
		this.isInterface = type.isInterface();
	}

	public CodeTypeReference(java.lang.Class<?> type, CodeTypeReferenceOptions codeTypeReferenceOption)
	{
		this(type);
		this.referenceOptions = codeTypeReferenceOption;
	}

	public CodeTypeReference(String typeName, CodeTypeReferenceOptions codeTypeReferenceOption)
	{
		this.Initialize(typeName, codeTypeReferenceOption);
	}

	public CodeTypeReference(String typeName)
	{
		this.Initialize(typeName);
	}

	private void InitializeFromType(java.lang.Class type)
	{
		this.baseType = type.getSimpleName();
		//if (!type.IsGenericParameter)
		if(!type.isPrimitive())
		{
			java.lang.Class type2 = type;
			//while (type2.IsNested)
			//java 泛型擦除，只有编译期间的类型检查，嵌套类型即可认为是泛型基类
			/*while(type2.IsNested)
			{
				type2 = type2.getDeclaringClass();
				this.baseType = type2.getSimpleName() + "+" + this.baseType;
			}*/
			if (!StringHelper.isNullOrEmpty(type.getPackage().getName()))
			{
				this.baseType = type.getPackage().getName() + "." + this.baseType;
			}
		}
		//if (type.IsGenericType && !type.getContainsGenericParameters())
		TypeVariable<Class<?>>[] tparameters=type.getTypeParameters();
		
		if(tparameters.length>0  )
		{
			//java.lang.Class[] genericArguments = type.GetGenericArguments();
			for (int i = 0; i < tparameters.length; i++)
			{
				this.getTypeArguments().Add(new CodeTypeReference(tparameters[i].getGenericDeclaration()));
			}
			return;
		}
		//if (!type.IsGenericTypeDefinition) //List<> 这种类似于List<?>
		if(tparameters.length>0 && tparameters[0].getGenericDeclaration().getName()=="?")
		{
			this.needsFixup = true;
		}
	}

	private void Initialize(String typeName)
	{
		this.Initialize(typeName, this.referenceOptions);
	}

	private void Initialize(String typeName, CodeTypeReferenceOptions options)
	{
		this.setOptions(options);
		if (typeName == null || typeName.length() == 0)
		{
			//void.class.getName()
			typeName = void.class.getName();
			this.baseType = typeName;
			this.arrayRank = 0;
			this.arrayElementType = null;
			return;
		}
		typeName = this.RipOffAssemblyInformationFromTypeName(typeName);
		int num = typeName.length() - 1;
		int i = num;
		this.needsFixup = true;
		LinkedList<Integer> queue = new LinkedList<Integer>();
		while (i >= 0)
		{
			int num2 = 1;
			if (typeName.charAt(i--) != ']')
			{
				break;
			}
			while (i >= 0 && typeName.charAt(i) == ',')
			{
				num2++;
				i--;
			}
			if (i < 0 || typeName.charAt(i) != '[')
			{
				break;
			}
			queue.offer(num2);
			i--;
			num = i;
		}
		i = num;
		ArrayList<CodeTypeReference> arrayList = new ArrayList<CodeTypeReference>();
		Stack<String> stack = new Stack<String>();
		if (i > 0 && typeName.charAt(i--) == ']')
		{
			this.needsFixup = false;
			int num3 = 1;
			int num4 = num;
			while (i >= 0)
			{
				if (typeName.charAt(i) == '[')
				{
					if (--num3 == 0)
					{
						break;
					}
				}
				else if (typeName.charAt(i) == ']')
				{
					num3++;
				}
				else if (typeName.charAt(i) == ',' && num3 == 1)
				{
					if (i + 1 < num4)
					{
						stack.push(typeName.substring(i + 1, i + 1 + num4 - i - 1));
					}
					num4 = i;
				}
				i--;
			}
			if (i > 0 && num - i - 1 > 0)
			{
				if (i + 1 < num4)
				{
					stack.push(typeName.substring(i + 1, i + 1 + num4 - i - 1));
				}
				while (stack.size() > 0)
				{
					String typeName2 = this.RipOffAssemblyInformationFromTypeName(stack.pop());
					arrayList.add(new CodeTypeReference(typeName2));
				}
				num = i - 1;
			}
		}
		if (num < 0)
		{
			this.baseType = typeName;
			return;
		}
		if (queue.size() > 0)
		{
			CodeTypeReference codeTypeReference = new CodeTypeReference(typeName.substring(0, num + 1), this.getOptions());
			for (int j = 0; j < arrayList.size(); j++)
			{
				codeTypeReference.getTypeArguments().Add(arrayList.get(j));
			}
			while (queue.size() > 1)
			{
				codeTypeReference = new CodeTypeReference(codeTypeReference, queue.poll());
			}
			this.baseType = null;
			this.arrayRank = queue.poll();
			this.arrayElementType = codeTypeReference;
		}
		else if (arrayList.size() > 0)
		{
			for (int k = 0; k < arrayList.size(); k++)
			{
				this.getTypeArguments().Add(arrayList.get(k));
			}
			this.baseType = typeName.substring(0, num + 1);
		}
		else
		{
			this.baseType = typeName;
		}
		if (this.baseType != null && this.baseType.indexOf('`') != -1)
		{
			this.needsFixup = false;
		}
	}

	public CodeTypeReference(String typeName, CodeTypeReference... typeArguments)
	{
		this(typeName);
		if (typeArguments != null && typeArguments.length != 0)
		{
			this.getTypeArguments().AddRange(typeArguments);
		}
	}

	public CodeTypeReference(CodeTypeParameter typeParameter)
	{
		this((typeParameter == null) ? null : typeParameter.getName());
		this.referenceOptions = CodeTypeReferenceOptions.GenericTypeParameter;
	}

	public CodeTypeReference(String baseType, int rank)
	{
		this.baseType = null;
		this.arrayRank = rank;
		this.arrayElementType = new CodeTypeReference(baseType);
	}

	public CodeTypeReference(CodeTypeReference arrayType, int rank)
	{
		this.baseType = null;
		this.arrayRank = rank;
		this.arrayElementType = arrayType;
	}

	private String RipOffAssemblyInformationFromTypeName(String typeName)
	{
		int i = 0;
		int num = typeName.length() - 1;
		String result = typeName;
		while (i < typeName.length())
		{
			if (!Character.isWhitespace(typeName.charAt(i)))
			{
				break;
			}
			i++;
		}
		while (num >= 0 && Character.isWhitespace(typeName.charAt(num)))
		{
			num--;
		}
		if (i < num)
		{
			if (typeName.charAt(i) == '[' && typeName.charAt(num) == ']')
			{
				i++;
				num--;
			}
			if (typeName.charAt(num) != ']')
			{
				int num2 = 0;
				for (int j = num; j >= i; j--)
				{
					if (typeName.charAt(j) == ',')
					{
						num2++;
						if (num2 == 4)
						{
							result = typeName.substring(i, j);
							break;
						}
					}
				}
			}
		}
		return result;
	}
}