package com.superstudio.template.mvc.actionresult;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import com.superstudio.commons.CodeExecuteTimeStatistic;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.exception.ArgumentException;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.template.mvc.TemplateEngineCollection;
import com.superstudio.template.mvc.ITemplate;
import com.superstudio.template.mvc.TemplateContext;
import com.superstudio.template.mvc.context.RenderContext;
import com.superstudio.template.mvc.templateengine.*;

public abstract class TemplateResultBase extends ActionResult {
	private Map<String, Object> _dynamicTemplateData;
	private Map<String, Object> _tempData;
	private TemplateDataDictionary _templateData;
	private TemplateEngineCollection _templateEngineCollection;
	private String _templateName;

	private Object model;

	public Map<String, Object> getTempData() {

		if (_tempData == null) {
			_tempData = new HashMap<String, Object>();
		}
		return _tempData;

	}

	public void setTempData(Map<String, Object> tempData) {
		_tempData = tempData;
	}

	private ITemplate template;

	public Map<String, Object> TemplateBag;

	public TemplateDataDictionary getTemplateData()
	{

		if (_templateData == null) {
			_templateData = new TemplateDataDictionary(null);
		}
		return _templateData;

	}

	public void setTemplateData(TemplateDataDictionary value) {
		_templateData = value;
	}

	public TemplateEngineCollection getTemplateEngine() {
		return _templateEngineCollection == null ? TemplateEngines.Engines : _templateEngineCollection;

	}

	public void setTemplateEngine(TemplateEngineCollection value) {
		_templateEngineCollection = value;
	}

	private String templateName;

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	@Override
	public void execute(RenderContext context) throws IOException, ArgumentNullException, InvalidOperationException,
			ArgumentException, InstantiationException, IllegalAccessException, ClassNotFoundException,Exception {
		if (context == null) {
			throw new ArgumentNullException("context");
		}
		if (StringHelper.isNullOrEmpty(getTemplateName())) {
			 throw new ArgumentNullException("templateName");
			//templateName = context.getRouteData().getRequiredString("action");
		}

		TemplateEngineResult result = null;

		if (template == null) {
			result = findTemplate(context);
			template = result.getTemplate();
		}

		Writer writer = context.getWriter();
		TemplateContext templateContext = new TemplateContext(context, template, _templateData, getTempData(), writer);
			template.render(templateContext, writer);

		if (result != null) {
			result.getTemplateEngine().releaseTemplate(context, template);
		}
	}


	protected abstract TemplateEngineResult findTemplate(RenderContext context)
			throws InvalidOperationException, ArgumentNullException, ArgumentException, InstantiationException,
			IllegalAccessException, ClassNotFoundException;

	public Object getModel() {
		return model;
	}

	public void setModel(Object model) {
		this.model = model;
	}
}