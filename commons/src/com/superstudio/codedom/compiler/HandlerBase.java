package com.superstudio.codedom.compiler;

import  org.w3c.dom.Node;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.commons.csharpbridge.StringHelper;

public final class HandlerBase
{
	private static Node getAndRemoveAttribute(Node node, String attrib, boolean fRequired)
	{
		Node xmlNode = node.getAttributes().removeNamedItem(attrib);
		if (fRequired && xmlNode == null)
		{
			//throw new ConfigurationErrorsException(SR.GetString("Config_missing_required_attribute", new Object[] {attrib, node.getNodeName()}), node);
		}
		return xmlNode;
	}

	private static Node getAndRemoveStringAttributeInternal(Node node, String attrib, boolean fRequired, RefObject<String> val)
	{
		Node andRemoveAttribute = HandlerBase.getAndRemoveAttribute(node, attrib, fRequired);
		if (andRemoveAttribute != null)
		{
			val.setRefObj(andRemoveAttribute.getNodeValue()); 
		}
		return andRemoveAttribute;
	}

	public static Node getAndRemoveStringAttribute(Node node, String attrib, RefObject<String> val)
	{
		return HandlerBase.getAndRemoveStringAttributeInternal(node, attrib, false, val);
	}

	public static Node getAndRemoveRequiredNonEmptyStringAttribute(Node node, String attrib, RefObject<String> val)
	{
		return HandlerBase.getAndRemoveNonEmptyStringAttributeInternal(node, attrib, true, val);
	}

	private static Node getAndRemoveNonEmptyStringAttributeInternal(Node node, String attrib, boolean fRequired, RefObject<String> val)
	{
		Node andRemoveStringAttributeInternal = HandlerBase.getAndRemoveStringAttributeInternal(node, attrib, fRequired, val);
		if (andRemoveStringAttributeInternal != null && val.getRefObj().length() == 0)
		{
			//throw new ConfigurationErrorsException(SR.GetString("Empty_attribute", new Object[] {attrib}), andRemoveStringAttributeInternal);
		}
		return andRemoveStringAttributeInternal;
	}

	private static Node getAndRemoveIntegerAttributeInternal(Node node, String attrib, boolean fRequired, RefObject<Integer> val)
	{
		Node andRemoveAttribute = HandlerBase.getAndRemoveAttribute(node, attrib, fRequired);
		if (andRemoveAttribute != null)
		{
			if (!andRemoveAttribute.getNodeValue().trim().equals(andRemoveAttribute.getNodeValue()))
			{
				//throw new ConfigurationErrorsException(SR.GetString("Config_invalid_integer_attribute", new Object[] {andRemoveAttribute.getName()}), andRemoveAttribute);
			}
			try
			{
				val.setRefObj(Integer.parseInt(StringHelper.format(CultureInfo.InvariantCulture, andRemoveAttribute.getNodeValue())));
			}
			catch (RuntimeException inner)
			{
				//throw new ConfigurationErrorsException(SR.GetString("Config_invalid_integer_attribute", new Object[] {andRemoveAttribute.getName()}), inner, andRemoveAttribute);
			}
		}
		return andRemoveAttribute;
	}

	private static Node getAndRemoveNonNegativeAttributeInternal(Node node, String attrib, boolean fRequired, RefObject<Integer> val)
	{
		Node andRemoveIntegerAttributeInternal = HandlerBase.getAndRemoveIntegerAttributeInternal(node, attrib, fRequired, val);
		if (andRemoveIntegerAttributeInternal != null && val.getRefObj() < 0)
		{
			//throw new ConfigurationErrorsException(SR.GetString("Invalid_nonnegative_integer_attribute", new Object[] {attrib}), andRemoveIntegerAttributeInternal);
		}
		return andRemoveIntegerAttributeInternal;
	}

	public static Node getAndRemoveNonNegativeIntegerAttribute(Node node, String attrib, RefObject<Integer> val)
	{
		return HandlerBase.getAndRemoveNonNegativeAttributeInternal(node, attrib, false, val);
	}

	public static void checkForUnrecognizedAttributes(Node node)
	{
		if (node.getAttributes().getLength() != 0)
		{
			//throw new ConfigurationErrorsException(SR.GetString("Config_base_unrecognized_attribute", new Object[] {node.getAttributes().item(0).getNodeName()}), node.getAttributes().item(0));
		}
	}

	public static void checkForNonElement(Node node)
	{
		if (node.getNodeType() != Node.ELEMENT_NODE)
		{
			//throw new ConfigurationErrorsException(SR.GetString("Config_base_elements_only"),new Object[]{node});
		}
	}

	public static boolean isIgnorableAlsoCheckForNonElement(Node node)
	{
		
		//if (node.getNodeType() == Node.getComment()_NODE || node.NodeType == NodeType.Whitespace)
		if (node.getNodeType() == Node.COMMENT_NODE)
		{
			return true;
		}
		HandlerBase.checkForNonElement(node);
		return false;
	}

	public static void checkForChildNodes(Node node)
	{
		if (node.hasChildNodes())
		{
		//	throw new ConfigurationErrorsException(SR.GetString("Config_base_no_child_nodes"), node.getFirstChild());
		}
	}

	public static void throwUnrecognizedElement(Node node)
	{
		//throw new ConfigurationErrorsException(SR.GetString("Config_base_unrecognized_element"), node);
	}
}