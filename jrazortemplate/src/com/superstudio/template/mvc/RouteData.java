package com.superstudio.template.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.superstudio.web.HttpRequestBase;

public class RouteData {
	private String name;

	private String uri;

	private String area;

	private String action;

	private String controller;

	private Map<String, String> parameters;

	private HttpRequestBase request;

	private Route route;

	private Pattern mapPattern = Pattern.compile("\\{(\\w+)\\}");

	public RouteData(Route route, HttpRequestBase request) {
		// TODO Auto-generated constructor stub
		this.name = route.getName();
		this.uri = request.getRequestURI();
		this.area = route.getArea();
		this.parameters = new HashMap<String, String>();
		this.parameters.put("area", this.area);
		// this.parameters.put("url", this.uri);
		this.parameters.put("name", this.name);
		this.request = request;
		this.route = route;
		/*
		 * for (Entry<String,Object> item : route.getParameters().entrySet()) {
		 * Object value=tryGetValue(item.getKey(),"");
		 * this.parameters.put(item.getKey(), (String) value); }
		 */
		mapRouteData();

	}

	private void mapRouteData() {
		//Map<String, String> routeDataMap = new HashMap<String, String>();
		// 将url pattern 改为正则
		String urlPattern = this.route.getUri().toLowerCase().replaceAll("\\{\\w+\\}", "(\\\\w+)");

		// Pattern keyPattern=Pattern.compile(mapPattern);
		Matcher keyMatcher = mapPattern.matcher(this.route.getUri());
		List<String> keys = new ArrayList<String>();
		while (keyMatcher.find()) {
			keys.add(keyMatcher.group(1));
		}

		urlPattern = urlPattern.replace(".", "\\.");
		Pattern pattern = Pattern.compile("^"+urlPattern);
		Matcher match = pattern.matcher(this.request.getServletPath());
		
		while (match.find()) {

			for (int index = 0; index < keys.size(); index++) {
				this.parameters.put(keys.get(index), match.group(index+1));
			}
		}
		/*
		 * if(match.find()){ //value=match.group(routeMap.) int index=0;
		 * for(String item : routeMap.keySet()){ routeDataMap.put(item,
		 * match.group(index++)); }
		 * 
		 * }
		 */
		// String[] strArray=this.uri.split("/");

		/*
		 * keys.forEach((str)->{ //List<ListExtension.filter(routeDataMap, ());
		 * });
		 */

	}

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
