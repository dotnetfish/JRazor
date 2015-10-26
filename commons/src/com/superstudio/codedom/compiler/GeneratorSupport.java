package com.superstudio.codedom.compiler;

public class GeneratorSupport
{
	public static final GeneratorSupport ArraysOfArrays = new GeneratorSupport(1);
	public static final GeneratorSupport EntryPointMethod = new GeneratorSupport(2);
	public static final GeneratorSupport GotoStatements = new GeneratorSupport(4);
	public static final GeneratorSupport MultidimensionalArrays = new GeneratorSupport(8);
	public static final GeneratorSupport StaticConstructors = new GeneratorSupport(16);
	public static final GeneratorSupport TryCatchStatements = new GeneratorSupport(32);
	public static final GeneratorSupport ReturnTypeAttributes = new GeneratorSupport(64);
	public static final GeneratorSupport DeclareValueTypes = new GeneratorSupport(128);
	public static final GeneratorSupport DeclareEnums = new GeneratorSupport(256);
	public static final GeneratorSupport DeclareDelegates = new GeneratorSupport(512);
	public static final GeneratorSupport DeclareInterfaces = new GeneratorSupport(1024);
	public static final GeneratorSupport DeclareEvents = new GeneratorSupport(2048);
	public static final GeneratorSupport AssemblyAttributes = new GeneratorSupport(4096);
	public static final GeneratorSupport ParameterAttributes = new GeneratorSupport(8192);
	public static final GeneratorSupport ReferenceParameters = new GeneratorSupport(16384);
	public static final GeneratorSupport ChainedConstructorArguments = new GeneratorSupport(32768);
	public static final GeneratorSupport NestedTypes = new GeneratorSupport(65536);
	public static final GeneratorSupport MultipleInterfaceMembers = new GeneratorSupport(131072);
	public static final GeneratorSupport PublicStaticMembers = new GeneratorSupport(262144);
	public static final GeneratorSupport ComplexExpressions = new GeneratorSupport(524288);
	public static final GeneratorSupport Win32Resources = new GeneratorSupport(1048576);
	public static final GeneratorSupport Resources = new GeneratorSupport(2097152);
	public static final GeneratorSupport PartialTypes = new GeneratorSupport(4194304);
	public static final GeneratorSupport GenericTypeReference = new GeneratorSupport(8388608);
	public static final GeneratorSupport GenericTypeDeclaration = new GeneratorSupport(16777216);
	public static final GeneratorSupport DeclareIndexerProperties = new GeneratorSupport(33554432);

	private int intValue;
	private static java.util.HashMap<Integer, GeneratorSupport> mappings;
	private static java.util.HashMap<Integer, GeneratorSupport> getMappings()
	{
		if (mappings == null)
		{
			synchronized (GeneratorSupport.class)
			{
				if (mappings == null)
				{
					mappings = new java.util.HashMap<Integer, GeneratorSupport>();
				}
			}
		}
		return mappings;
	}

	private GeneratorSupport(int value)
	{
		intValue = value;
		synchronized (GeneratorSupport.class)
		{
			getMappings().put(value, this);
		}
	}

	public int getValue()
	{
		return intValue;
	}

	public static GeneratorSupport forValue(int value)
	{
		synchronized (GeneratorSupport.class)
		{
			GeneratorSupport enumObj = getMappings().get(value);
			if (enumObj == null)
			{
				return new GeneratorSupport(value);
			}
			else
			{
				return enumObj;
			}
		}
	}
}