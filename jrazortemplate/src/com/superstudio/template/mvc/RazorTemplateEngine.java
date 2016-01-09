package com.superstudio.template.mvc;

import java.util.Arrays;

import com.superstudio.template.mvc.context.RenderContext;

public class RazorTemplateEngine extends BuildManagerTemplateEngine {
	public static final String TemplateStartFileName = "_TemplateStart";

	public RazorTemplateEngine() {
		this(null);
	}

	public RazorTemplateEngine(ITemplatePageActivator templatePageActivator) {
		super(templatePageActivator);
		setAreaTemplateLocationFormats(new String[] { "~/Areas/{2}/Templates/{1}/{0}.jhtml", "~/Areas/{2}/Templates/{1}/{0}.jhtml",
				"~/Areas/{2}/Templates/Shared/{0}.jhtml", "~/Areas/{2}/Templates/Shared/{0}.jhtml" });
		setAreaMasterLocationFormats(
				new String[] { "~/Areas/{2}/Templates/{1}/{0}.jhtml", "~/Areas/{2}/Templates/{1}/{0}.jhtml",
						"~/Areas/{2}/Templates/Shared/{0}.jhtml", "~/Areas/{2}/Templates/Shared/{0}.jhtml" });
		setAreaPartialTemplateLocationFormats(
				new String[] { "~/Areas/{2}/Templates/{1}/{0}.jhtml", "~/Areas/{2}/Templates/{1}/{0}.jhtml",
						"~/Areas/{2}/Templates/Shared/{0}.jhtml", "~/Areas/{2}/Templates/Shared/{0}.jhtml" });

		setTemplateLocationFormats(new String[] { "~/Templates/{1}/{0}.jhtml", "~/Templates/{1}/{0}.jhtml",
				"~/Templates/Shared/{0}.jhtml", "~/Templates/Shared/{0}.jhtml" });
		setMasterLocationFormats(new String[] { "~/Templates/{1}/{0}.jhtml", "~/Templates/{1}/{0}.jhtml",
				"~/Templates/Shared/{0}.jhtml", "~/Templates/Shared/{0}.jhtml" });
		setPartialTemplateLocationFormats(new String[] { "~/Templates/{1}/{0}.jhtml", "~/Templates/{1}/{0}.jhtml",
				"~/Templates/Shared/{0}.jhtml", "~/Templates/Shared/{0}.jhtml" });

		setFileExtensions(Arrays.asList( ".jhtml", "jhtml" ));
	}

	@Override
	protected ITemplate createPartialTemplate(RenderContext renderContext, String partialPath) throws Exception {
		RazorTemplate tempVar = new RazorTemplate(renderContext, partialPath, "", false, getFileExtensions(),
				getTemplatePageActivator());
		tempVar.setDisplayModeProvider(getDisplayModeProvider());
		return tempVar;
	}

	@Override
	protected ITemplate createTemplate(RenderContext renderContext, String templatePath, String masterPath) throws Exception {
		RazorTemplate template = new RazorTemplate(renderContext, templatePath, masterPath, true, getFileExtensions(),
				getTemplatePageActivator());
		template.setDisplayModeProvider(getDisplayModeProvider());
		return template;
	}


}