package com.superstudio.template.mvc.actionresult;

import java.util.List;

import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.template.mvc.ITemplate;

public class TemplateEngineResult {

	public TemplateEngineResult(List<String> searchedLocations) throws ArgumentNullException {
		if (searchedLocations == null) {
			throw new ArgumentNullException("searchedLocations");
		}

		this.searchedLocations = searchedLocations;//Arrays.asList(searchedLocations);
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

	public ITemplate getTemplate() {
		return template;
	}

	public ITemplateEngine getTemplateEngine() {
		return templateEngine;
	}

	private List<String> searchedLocations;

	private ITemplate template;
	private ITemplateEngine templateEngine;

}
