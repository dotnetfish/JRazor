package com.superstudio.codedom.compiler;

import com.superstudio.commons.XmlNode;
import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.commons.exception.ConfigurationErrorsException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.util.*;

public class CodeDomCompilationConfiguration
{
	public static class SectionHandler
	{
		private SectionHandler()
		{
		}

		public static Object createStatic(Object inheritedObject, XmlNode node)
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
			HandlerBase.checkForUnrecognizedAttributes(node);
			//for (XmlNode xmlNode : node.getChildNodes())
			int len=node.getChildNodes().getLength();
			NodeList nodes=node.getChildNodes();
			for(int i=0;i<len;i++)
			{
				if (!HandlerBase.isIgnorableAlsoCheckForNonElement(nodes.item(i)))
				{
					if (nodes.item(i).getNodeName().equals("compilers"))
					{
						CodeDomCompilationConfiguration.SectionHandler.processCompilersElement(result, nodes.item(i));
					}
					else
					{
						HandlerBase.throwUnrecognizedElement(nodes.item(i));
					}
				}
			}
			return result;
		}

		private static Map<String, String> getProviderOptions(XmlNode compilerNode)
		{
			HashMap<String, String> dictionary = new HashMap<String, String>();
			for (XmlNode xmlNode : compilerNode)
			{
				if (!xmlNode.getNodeName().equals("providerOption"))
				{
					HandlerBase.throwUnrecognizedElement(xmlNode);
				}
				String key = null;
				String value = null;
				RefObject<String> tempRef_key = new RefObject<String>(key);
				HandlerBase.getAndRemoveRequiredNonEmptyStringAttribute(xmlNode, "name", tempRef_key);
				key = tempRef_key.getRefObj();
				RefObject<String> tempRef_value = new RefObject<String>(value);
				HandlerBase.getAndRemoveRequiredNonEmptyStringAttribute(xmlNode, "value", tempRef_value);
				value = tempRef_value.getRefObj();
				HandlerBase.checkForUnrecognizedAttributes(xmlNode);
				HandlerBase.checkForChildNodes(xmlNode);
				dictionary.put(key, value);
			}
			return dictionary;
		}

		private static void processCompilersElement(CodeDomCompilationConfiguration result, Node node)
		{
			HandlerBase.checkForUnrecognizedAttributes(node);
			String filename = ConfigurationErrorsException.GetFilename(node);

			for(int nodeIndex=0;nodeIndex<node.getChildNodes().getLength();nodeIndex++)
			{
				Node xmlNode=node.getChildNodes().item(nodeIndex);
				int lineNumber = ConfigurationErrorsException.GetLineNumber(xmlNode);
				if (!HandlerBase.isIgnorableAlsoCheckForNonElement(xmlNode))
				{
					if (!xmlNode.getNodeName().equals("compiler"))
					{
						HandlerBase.throwUnrecognizedElement(xmlNode);
					}
					String empty = "";
					RefObject<String> tempRef_empty = new RefObject<String>(empty);
					HandlerBase.getAndRemoveRequiredNonEmptyStringAttribute(xmlNode, "language", tempRef_empty);
					empty = tempRef_empty.getRefObj();
					String empty2 = "";
					RefObject<String> tempRef_empty2 = new RefObject<String>(empty2);
					HandlerBase.getAndRemoveRequiredNonEmptyStringAttribute(xmlNode, "extension", tempRef_empty2);
					empty2 = tempRef_empty2.getRefObj();
					String text = null;
					RefObject<String> tempRef_text = new RefObject<String>(text);
					HandlerBase.getAndRemoveStringAttribute(xmlNode, "type", tempRef_text);
					text = tempRef_text.getRefObj();
					CompilerParameters compilerParameters = new CompilerParameters();
					int num = 0;
					RefObject<Integer> tempRef_num = new RefObject<Integer>(num);
					boolean tempVar = HandlerBase.getAndRemoveNonNegativeIntegerAttribute(xmlNode, "warningLevel", tempRef_num) != null;
						num = tempRef_num.getRefObj();
					if (tempVar)
					{
						compilerParameters.setWarningLevel(num);
						compilerParameters.setTreatWarningsAsErrors(num > 0) ;//= ();
					}
					String compilerOptions = null;
					RefObject<String> tempRef_compilerOptions = new RefObject<String>(compilerOptions);
					boolean tempVar2 = HandlerBase.getAndRemoveStringAttribute(xmlNode, "compilerOptions", tempRef_compilerOptions) != null;
						compilerOptions = tempRef_compilerOptions.getRefObj();
					if (tempVar2)
					{
						compilerParameters.setCompilerOptions(compilerOptions);// = ;
					}
					Map<String, String> providerOptions = CodeDomCompilationConfiguration.SectionHandler.getProviderOptions(xmlNode);
					HandlerBase.checkForUnrecognizedAttributes(xmlNode);
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
						compilerInfo = result.findExistingCompilerInfo(array, array2);
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
						result.addCompilerInfo(compilerInfo);
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
			result.removeUnmapped();
		}

		private static Map<String, String> getProviderOptions(Node xmlNode) {
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
		this._compilerLanguages = new Hashtable<>();
		this._compilerExtensions = new Hashtable<>();
		this._allCompilerInfo = new ArrayList<>();
		CompilerParameters compilerParameters = new CompilerParameters();

		String	 codeDomProviderTypeName="com.superstudio.language.java.JavaCodeProvider";
		CompilerInfo javaCompilerInfo = new CompilerInfo(compilerParameters, codeDomProviderTypeName);
		javaCompilerInfo._compilerLanguages = new String[] {"java"};
		javaCompilerInfo._compilerExtensions = new String[] {".java", "java"};
		javaCompilerInfo._providerOptions = new HashMap<String, String>();
		javaCompilerInfo._providerOptions.put("CompilerVersion",  "v4.0");
		this.addCompilerInfo(javaCompilerInfo);
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

	private void addCompilerInfo(CompilerInfo compilerInfo)
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

	private void removeUnmapped()
	{
		for (int i = 0; i < this._allCompilerInfo.size(); i++)
		{
			this._allCompilerInfo.get(i)._mapped = false;
		}
		Iterator<CompilerInfo> enumerator = this._compilerLanguages.values().iterator();
		try
		{
			while (enumerator.hasNext())
			{
				enumerator.next()._mapped = true;
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
				enumerator.next()._mapped = true;
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
			if (!this._allCompilerInfo.get(j)._mapped)
			{
				this._allCompilerInfo.remove(j);
			}
		}
	}

	private CompilerInfo findExistingCompilerInfo(String[] languageList, String[] extensionList)
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