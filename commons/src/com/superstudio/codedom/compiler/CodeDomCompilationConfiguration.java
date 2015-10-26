package com.superstudio.codedom.compiler;

import java.io.IOException;
import java.util.*;

import org.w3c.dom.NodeList;

import org.w3c.dom.Node;

import com.superstudio.commons.XmlNode;
import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.commons.exception.ConfigurationErrorsException;

public class CodeDomCompilationConfiguration
{
	public static class SectionHandler
	{
		private SectionHandler()
		{
		}

		public static Object CreateStatic(Object inheritedObject, XmlNode node)
		{
			CodeDomCompilationConfiguration codeDomCompilationConfiguration = (CodeDomCompilationConfiguration)inheritedObject;
			CodeDomCompilationConfiguration result;
			if (codeDomCompilationConfiguration == null)
			{
				result = new CodeDomCompilationConfiguration();
			}
			else
			{
				result = new CodeDomCompilationConfiguration(codeDomCompilationConfiguration);
			}
			HandlerBase.CheckForUnrecognizedAttributes(node);
			//for (XmlNode xmlNode : node.getChildNodes())
			int len=node.getChildNodes().getLength();
			NodeList nodes=node.getChildNodes();
			for(int i=0;i<len;i++)
			{
				if (!HandlerBase.IsIgnorableAlsoCheckForNonElement(nodes.item(i)))
				{
					if (nodes.item(i).getNodeName().equals("compilers"))
					{
						CodeDomCompilationConfiguration.SectionHandler.ProcessCompilersElement(result, nodes.item(i));
					}
					else
					{
						HandlerBase.ThrowUnrecognizedElement(nodes.item(i));
					}
				}
			}
			return result;
		}

		private static Map<String, String> GetProviderOptions(XmlNode compilerNode)
		{
			HashMap<String, String> dictionary = new HashMap<String, String>();
			for (XmlNode xmlNode : compilerNode)
			{
				if (!xmlNode.getNodeName().equals("providerOption"))
				{
					HandlerBase.ThrowUnrecognizedElement(xmlNode);
				}
				String key = null;
				String value = null;
				RefObject<String> tempRef_key = new RefObject<String>(key);
				HandlerBase.GetAndRemoveRequiredNonEmptyStringAttribute(xmlNode, "name", tempRef_key);
				key = tempRef_key.getRefObj();
				RefObject<String> tempRef_value = new RefObject<String>(value);
				HandlerBase.GetAndRemoveRequiredNonEmptyStringAttribute(xmlNode, "value", tempRef_value);
				value = tempRef_value.getRefObj();
				HandlerBase.CheckForUnrecognizedAttributes(xmlNode);
				HandlerBase.CheckForChildNodes(xmlNode);
				dictionary.put(key, value);
			}
			return dictionary;
		}

		private static void ProcessCompilersElement(CodeDomCompilationConfiguration result, Node node)
		{
			HandlerBase.CheckForUnrecognizedAttributes(node);
			String filename = ConfigurationErrorsException.GetFilename(node);
			//for (XmlNode xmlNode : node.getChildNodes())
			for(int nodeIndex=0;nodeIndex<node.getChildNodes().getLength();nodeIndex++)
			{
				Node xmlNode=node.getChildNodes().item(nodeIndex);
				int lineNumber = ConfigurationErrorsException.GetLineNumber(xmlNode);
				if (!HandlerBase.IsIgnorableAlsoCheckForNonElement(xmlNode))
				{
					if (!xmlNode.getNodeName().equals("compiler"))
					{
						HandlerBase.ThrowUnrecognizedElement(xmlNode);
					}
					String empty = "";
					RefObject<String> tempRef_empty = new RefObject<String>(empty);
					HandlerBase.GetAndRemoveRequiredNonEmptyStringAttribute(xmlNode, "language", tempRef_empty);
					empty = tempRef_empty.getRefObj();
					String empty2 = "";
					RefObject<String> tempRef_empty2 = new RefObject<String>(empty2);
					HandlerBase.GetAndRemoveRequiredNonEmptyStringAttribute(xmlNode, "extension", tempRef_empty2);
					empty2 = tempRef_empty2.getRefObj();
					String text = null;
					RefObject<String> tempRef_text = new RefObject<String>(text);
					HandlerBase.GetAndRemoveStringAttribute(xmlNode, "type", tempRef_text);
					text = tempRef_text.getRefObj();
					CompilerParameters compilerParameters = new CompilerParameters();
					int num = 0;
					RefObject<Integer> tempRef_num = new RefObject<Integer>(num);
					boolean tempVar = HandlerBase.GetAndRemoveNonNegativeIntegerAttribute(xmlNode, "warningLevel", tempRef_num) != null;
						num = tempRef_num.getRefObj();
					if (tempVar)
					{
						compilerParameters.setWarningLevel(num);
						compilerParameters.setTreatWarningsAsErrors(num > 0) ;//= ();
					}
					String compilerOptions = null;
					RefObject<String> tempRef_compilerOptions = new RefObject<String>(compilerOptions);
					boolean tempVar2 = HandlerBase.GetAndRemoveStringAttribute(xmlNode, "compilerOptions", tempRef_compilerOptions) != null;
						compilerOptions = tempRef_compilerOptions.getRefObj();
					if (tempVar2)
					{
						compilerParameters.setCompilerOptions(compilerOptions);// = ;
					}
					Map<String, String> providerOptions = CodeDomCompilationConfiguration.SectionHandler.GetProviderOptions(xmlNode);
					HandlerBase.CheckForUnrecognizedAttributes(xmlNode);
					String[] array = empty.split(java.util.regex.Pattern.quote(CodeDomCompilationConfiguration.s_fieldSeparators.toString()), -1);
					String[] array2 = empty2.split(java.util.regex.Pattern.quote(CodeDomCompilationConfiguration.s_fieldSeparators.toString()), -1);
					for (int i = 0; i < array.length; i++)
					{
						array[i] = array[i].trim();
					}
					for (int j = 0; j < array2.length; j++)
					{
						array2[j] = array2[j].trim();
					}
					String[] array3 = array;
					for (int k = 0; k < array3.length; k++)
					{
						if (array3[k].length() == 0)
						{
							//throw new ConfigurationErrorsException(SR.GetString("Language_Names_Cannot_Be_Empty"));
						}
					}
					array3 = array2;
					for (int k = 0; k < array3.length; k++)
					{
						String text2 = array3[k];
						if (text2.length() == 0 || text2.charAt(0) != '.')
						{
							//throw new ConfigurationErrorsException(SR.GetString("Extension_Names_Cannot_Be_Empty_Or_Non_Period_Based"));
						}
					}
					CompilerInfo compilerInfo;
					if (text != null)
					{
						compilerInfo = new CompilerInfo(compilerParameters, text);
					}
					else
					{
						compilerInfo = result.FindExistingCompilerInfo(array, array2);
						if (compilerInfo == null)
						{
							//throw new ConfigurationErrorsException();
						}
					}
					compilerInfo.configFileName = filename;
					compilerInfo.configFileLineNumber = lineNumber;
					if (text != null)
					{
						compilerInfo._compilerLanguages = array;
						compilerInfo._compilerExtensions = array2;
						compilerInfo._providerOptions = providerOptions;
						result.AddCompilerInfo(compilerInfo);
					}
					else
					{
						for (Map.Entry<String, String> current : providerOptions.entrySet())
						{
							compilerInfo._providerOptions.put(current.getKey(), current.getValue()) ;
						}
					}
				}
			}
			result.RemoveUnmapped();
		}

		private static Map<String, String> GetProviderOptions(Node xmlNode) {
			// TODO Auto-generated method stub
			return null;
		}
	}

	public static final String sectionName = "system.codedom";

	private static final char[] s_fieldSeparators = new char[] {';'};

	public Hashtable<String, CompilerInfo> _compilerLanguages;

	public Hashtable<String, CompilerInfo> _compilerExtensions;

	public ArrayList<CompilerInfo> _allCompilerInfo;

	private static CodeDomCompilationConfiguration defaultInstance = new CodeDomCompilationConfiguration();

	public static CodeDomCompilationConfiguration getDefault()
	{
		return CodeDomCompilationConfiguration.defaultInstance;
	}

	public CodeDomCompilationConfiguration()
	{
		this._compilerLanguages = new Hashtable<String, CompilerInfo>();
		this._compilerExtensions = new Hashtable<String, CompilerInfo>();
		this._allCompilerInfo = new ArrayList<CompilerInfo>();
		CompilerParameters expr_36 = new CompilerParameters();
		expr_36.setWarningLevel(4);
		//String codeDomProviderTypeName = "Microsoft.CSharp.CSharpCodeProvider, System, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089";
		String codeDomProviderTypeName="System.Language.CSharp.CSharpCodeProvider";
		CompilerInfo compilerInfo = new CompilerInfo(expr_36, codeDomProviderTypeName);
		compilerInfo._compilerLanguages = new String[] {"c#", "cs", "csharp"};
		compilerInfo._compilerExtensions = new String[] {".cs", "cs"};
		compilerInfo._providerOptions = new HashMap<String, String>();
		compilerInfo._providerOptions.put("CompilerVersion",  "v4.0");
		this.AddCompilerInfo(compilerInfo);
		CompilerParameters expr_B6 = new CompilerParameters();
		expr_B6.setWarningLevel(4);
		codeDomProviderTypeName = "Microsoft.VisualBasic.VBCodeProvider, System, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089";
		compilerInfo = new CompilerInfo(expr_B6, codeDomProviderTypeName);
		compilerInfo._compilerLanguages = new String[] {"vb", "vbs", "visualbasic", "vbscript"};
		compilerInfo._compilerExtensions = new String[] {".vb", "vb"};
		compilerInfo._providerOptions = new HashMap<String, String>();
		compilerInfo._providerOptions.put("CompilerVersion","v4.0");
		this.AddCompilerInfo(compilerInfo);
		CompilerParameters expr_13E = new CompilerParameters();
		expr_13E.setWarningLevel(4) ;
		codeDomProviderTypeName = "Microsoft.JScript.JScriptCodeProvider, Microsoft.JScript, Version=8.0.0.0, Culture=neutral, PublicKeyToken=b03f5f7f11d50a3a";
		CompilerInfo tempVar = new CompilerInfo(expr_13E, codeDomProviderTypeName);
		tempVar._compilerLanguages = new String[] {"js", "jscript", "javascript"};
		tempVar._compilerExtensions = new String[] {".js", "js"};
		tempVar._providerOptions = new HashMap<String, String>();
		this.AddCompilerInfo(tempVar);
		CompilerParameters expr_1A9 = new CompilerParameters();
		expr_1A9.setWarningLevel(4);
		codeDomProviderTypeName = "Microsoft.VisualC.CppCodeProvider, CppCodeProvider, Version=10.0.0.0, Culture=neutral, PublicKeyToken=b03f5f7f11d50a3a";
		CompilerInfo tempVar2 = new CompilerInfo(expr_1A9, codeDomProviderTypeName);
		tempVar2._compilerLanguages = new String[] {"c++", "mc", "cpp"};
		tempVar2._compilerExtensions = new String[] {".h", "h"};
		tempVar2._providerOptions = new HashMap<String, String>();
		this.AddCompilerInfo(tempVar2);
		CompilerParameters javaCP = new CompilerParameters();
		expr_36.setWarningLevel(4);
		//String codeDomProviderTypeName = "Microsoft.CSharp.CSharpCodeProvider, System, Version=4.0.0.0, Culture=neutral, PublicKeyToken=b77a5c561934e089";
		 codeDomProviderTypeName="com.superstudio.language.java.JavaCodeProvider";
		CompilerInfo javaCompilerInfo = new CompilerInfo(expr_36, codeDomProviderTypeName);
		javaCompilerInfo._compilerLanguages = new String[] {"java"};
		javaCompilerInfo._compilerExtensions = new String[] {".java", "java"};
		javaCompilerInfo._providerOptions = new HashMap<String, String>();
		javaCompilerInfo._providerOptions.put("CompilerVersion",  "v4.0");
		this.AddCompilerInfo(javaCompilerInfo);
	}

	private CodeDomCompilationConfiguration(CodeDomCompilationConfiguration original)
	{
		if (original._compilerLanguages != null)
		{
			this._compilerLanguages = (Hashtable<String, CompilerInfo>)original._compilerLanguages.clone();
		}
		if (original._compilerExtensions != null)
		{
			this._compilerExtensions = (Hashtable<String, CompilerInfo>)original._compilerExtensions.clone();
		}
		if (original._allCompilerInfo != null)
		{
			this._allCompilerInfo = (ArrayList<CompilerInfo>)original._allCompilerInfo.clone();
		}
	}

	private void AddCompilerInfo(CompilerInfo compilerInfo)
	{
		String[] array = compilerInfo._compilerLanguages;
		for (int i = 0; i < array.length; i++)
		{
			String key = array[i];
			this._compilerLanguages.put(key, compilerInfo);
		}
		array = compilerInfo._compilerExtensions;
		for (int i = 0; i < array.length; i++)
		{
			String key2 = array[i];
			this._compilerExtensions.put(key2, compilerInfo);
		}
		this._allCompilerInfo.add(compilerInfo);
	}

	private void RemoveUnmapped()
	{
		for (int i = 0; i < this._allCompilerInfo.size(); i++)
		{
			((CompilerInfo)this._allCompilerInfo.get(i))._mapped = false;
		}
		Iterator<CompilerInfo> enumerator = this._compilerLanguages.values().iterator();
		try
		{
			while (enumerator.hasNext())
			{
				((CompilerInfo)enumerator.next())._mapped = true;
			}
		}
		finally
		{
			java.io.Closeable disposable = (java.io.Closeable)((enumerator instanceof java.io.Closeable) ? enumerator : null);
			if (disposable != null)
			{
				try {
					disposable.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		enumerator = this._compilerExtensions.values().iterator();
		try
		{
			while (enumerator.hasNext())
			{
				((CompilerInfo)enumerator.next())._mapped = true;
			}
		}
		finally
		{
			java.io.Closeable disposable = (java.io.Closeable)((enumerator instanceof java.io.Closeable) ? enumerator : null);
			if (disposable != null)
			{
				try {
					disposable.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		for (int j = this._allCompilerInfo.size() - 1; j >= 0; j--)
		{
			if (!((CompilerInfo)this._allCompilerInfo.get(j))._mapped)
			{
				this._allCompilerInfo.remove(j);
			}
		}
	}

	private CompilerInfo FindExistingCompilerInfo(String[] languageList, String[] extensionList)
	{
		CompilerInfo result = null;
		for (CompilerInfo compilerInfo : this._allCompilerInfo)
		{
			if (compilerInfo._compilerExtensions.length == extensionList.length && compilerInfo._compilerLanguages.length == languageList.length)
			{
				boolean flag = false;
				for (int i = 0; i < compilerInfo._compilerExtensions.length; i++)
				{
					if (!extensionList[i].equals(compilerInfo._compilerExtensions[i]))
					{
						flag = true;
						break;
					}
				}
				for (int j = 0; j < compilerInfo._compilerLanguages.length; j++)
				{
					if (!languageList[j].equals(compilerInfo._compilerLanguages[j]))
					{
						flag = true;
						break;
					}
				}
				if (!flag)
				{
					result = compilerInfo;
					break;
				}
			}
		}
		return result;
	}
}