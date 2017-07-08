package com.superstudio.commons;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ResourceManager {

	private static Map<String, Map<String, String>> resources = new HashMap<String, Map<String, String>>();

	public ResourceManager(String string, String lan) {
		// TODO Auto-generated constructor stub
		//loadXml();
	}

	private void loadXml() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Map<String, String> rMap = new HashMap<String, String>();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse("d:\\RazorResources.xml");

			NodeList dogList = doc.getElementsByTagName("data");

			for (int i = 0; i < dogList.getLength(); i++) {
				Node dog = dogList.item(i);
				Element elem = (Element) dog;
				for (Node node = dog.getFirstChild(); node != null; node = node.getNextSibling()) {
					if (node.getNodeType() == Node.ELEMENT_NODE) {

						String value = node.getFirstChild().getNodeValue();

						rMap.put(elem.getAttribute("name"), value);
					}
				}

			}
			resources.put("en", rMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String GetString(String string, Locale locale) {
		// TODO Auto-generated method stub
		//return resources.get("en").get(string);
		return string;
	}

}
