package com.superstudio.template.mvc;

import java.util.Map;

public class Route {
	
	private String name;
	
	private String uri;
	
	private String area;
	
	private Map<String,Object> parameters;
	
	
	public Route(){
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public Map<String,Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String,Object> parameters) {
		this.parameters = parameters;
	}
}
