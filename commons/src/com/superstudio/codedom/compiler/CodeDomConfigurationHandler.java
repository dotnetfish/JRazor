package com.superstudio.codedom.compiler;

import com.superstudio.commons.IConfigurationSectionHandler;
import com.superstudio.commons.XmlNode;

public class CodeDomConfigurationHandler implements IConfigurationSectionHandler
{
	public CodeDomConfigurationHandler()
	{
	}

	public Object Create(Object inheritedObject, Object configContextObj, XmlNode node)
	{
		return CodeDomCompilationConfiguration.SectionHandler.CreateStatic(inheritedObject, node);
	}
}