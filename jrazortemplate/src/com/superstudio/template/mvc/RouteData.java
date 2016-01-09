package com.superstudio.template.mvc;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;



public class RouteData {
	private String name;

	private String uri;

	private String area;

	private String action;

	private String controller;

	private Map<String, String> parameters;


	private Route route;

	private Pattern mapPattern = Pattern.compile("\\{(\\w+)\\}");




	private Map<String, String> getMapKeys(String url) {
		Map<String, String> list = new HashMap<String, String>();

		// if(route.set)
		/*
		 * while(match.find()){ list.put("",match.group("name")); }
		 */

		return list;
	}

	public String tryGetValue(String name, String defaultValue) {
		// ValueProvider provider
		String result = this.parameters.get(name);
		if (result == null)
			result = defaultValue;
		return result;
	}

	public String getName() {
		return name;
	}

	public String getUri() {
		return uri;
	}

	public String getArea() {
		return area;
	}

	public Map<String, String> getParameters() {
		return parameters;
	}

	public String getController() {
		return tryGetValue("controller", this.route.getParameters().get("controller").toString());
		// return controller;
	}

	public String getAction() {
		return tryGetValue("action", this.route.getParameters().get("action").toString());
	}

	public String getRequiredString(String string) {
		// TODO Auto-generated method stub
		
		return tryGetValue(string,"");
	}

	public Route getRoute() {
		// TODO Auto-generated method stub
		return route;
	}

}
