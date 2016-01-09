package com.superstudio.jrazor.template;

import java.util.List;

import com.superstudio.commons.exception.ArgumentNullException;

public class TemplateEngineResult {
	public TemplateEngineResult(List<String> searchedLocations) throws ArgumentNullException {
		if (searchedLocations == null) {
			throw new ArgumentNullException("searchedLocations");
		}

		this.searchedLocations = searchedLocations;
	}

	public TemplateEngineResult(ITemplate template, ITemplateEngine templateEngine) throws ArgumentNullException {
		if (template == null) {
			throw new ArgumentNullException("template");
		}
		if (templateEngine == null) {
			throw new ArgumentNullException("templateEngine");
		}

		this.template = template;
		this.templateEngine = templateEngine;
	}

	public List<String> getSearchedLocations() {
		return searchedLocations;
	}

	public ITemplate getView() {
		return template;
	}

	public ITemplateEngine getViewEngine() {
		return templateEngine;
	}

	private List<String> searchedLocations;

	private ITemplate template;
	private ITemplateEngine templateEngine;
}
