package com.superstudio.codedom.compiler;

import  org.w3c.dom.Node;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.commons.csharpbridge.StringHelper;

public final class HandlerBase
{
	private static Node GetAndRemoveAttribute(Node node, String attrib, boolean fRequired)
	{
		Node xmlNode = node.getAttributes().removeNamedItem(attrib);
		if (fRequired && xmlNode == null)
		{
			//throw new ConfigurationErrorsException(SR.GetString("Config_missing_required_attribute", new Object[] {attrib, node.getNodeName()}), node);
		}
		return xmlNode;
	}

	private static Node GetAndRemoveStringAttributeInternal(Node node, String attrib, boolean fRequired, RefObject<String> val)
	{
		Node andRemoveAttribute = HandlerBase.GetAndRemoveAttribute(node, attrib, fRequired);
		if (andRemoveAttribute != null)
		{
			val.setRefObj(andRemoveAttribute.getNodeValue()); 
		}
		return andRemoveAttribute;
	}

	public static Node GetAndRemoveStringAttribute(Node node, String attrib, RefObject<String> val)
	{
		return HandlerBase.GetAndRemoveStringAttributeInternal(node, attrib, false, val);
	}

	public static Node GetAndRemoveRequiredNonEmptyStringAttribute(Node node, String attrib, RefObject<String> val)
	{
		return HandlerBase.GetAndRemoveNonEmptyStringAttributeInternal(node, attrib, true, val);
	}

	private static Node GetAndRemoveNonEmptyStringAttributeInternal(Node node, String attrib, boolean fRequired, RefObject<String> val)
	{
		Node andRemoveStringAttributeInternal = HandlerBase.GetAndRemoveStringAttributeInternal(node, attrib, fRequired, val);
		if (andRemoveStringAttributeInternal != null && val.getRefObj().length() == 0)
		{
			//throw new ConfigurationErrorsException(SR.GetString("Empty_attribute", new Object[] {attrib}), andRemoveStringAttributeInternal);
		}
		return andRemoveStringAttributeInternal;
	}

	private static Node GetAndRemoveIntegerAttributeInternal(Node node, String attrib, boolean fRequired, RefObject<Integer> val)
	{
		Node andRemoveAttribute = HandlerBase.GetAndRemoveAttribute(node, attrib, fRequired);
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

	private static Node GetAndRemoveNonNegativeAttributeInternal(Node node, String attrib, boolean fRequired, RefObject<Integer> val)
	{
		Node andRemoveIntegerAttributeInternal = HandlerBase.GetAndRemoveIntegerAttributeInternal(node, attrib, fRequired, val);
		if (andRemoveIntegerAttributeInternal != null && val.getRefObj() < 0)
		{
			//throw new ConfigurationErrorsException(SR.GetString("Invalid_nonnegative_integer_attribute", new Object[] {attrib}), andRemoveIntegerAttributeInternal);
		}
		return andRemoveIntegerAttributeInternal;
	}

	public static Node GetAndRemoveNonNegativeIntegerAttribute(Node node, String attrib, RefObject<Integer> val)
	{
		return HandlerBase.GetAndRemoveNonNegativeAttributeInternal(node, attrib, false, val);
	}

	public static void CheckForUnrecognizedAttributes(Node node)
	{
		if (node.getAttributes().getLength() != 0)
		{
			//throw new ConfigurationErrorsException(SR.GetString("Config_base_unrecognized_attribute", new Object[] {node.getAttributes().item(0).getNodeName()}), node.getAttributes().item(0));
		}
	}

	public static void CheckForNonElement(Node node)
	{
		if (node.getNodeType() != Node.ELEMENT_NODE)
		{
			//throw new ConfigurationErrorsException(SR.GetString("Config_base_elements_only"),new Object[]{node});
		}
	}

	public static boolean IsIgnorableAlsoCheckForNonElement(Node node)
	{
		
		//if (node.getNodeType() == Node.getComment()_NODE || node.NodeType == NodeType.Whitespace)
		if (node.getNodeType() == Node.COMMENT_NODE)
		{
			return true;
		}
		HandlerBase.CheckForNonElement(node);
		return false;
	}

	public static void CheckForChildNodes(Node node)
	{
		if (node.hasChildNodes())
		{
		//	throw new ConfigurationErrorsException(SR.GetString("Config_base_no_child_nodes"), node.getFirstChild());
		}
	}

	public static void ThrowUnrecognizedElement(Node node)
	{
		//throw new ConfigurationErrorsException(SR.GetString("Config_base_unrecognized_element"), node);
	}
}