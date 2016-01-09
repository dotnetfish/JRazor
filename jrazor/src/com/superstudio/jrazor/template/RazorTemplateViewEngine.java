package com.superstudio.jrazor.template;

import com.superstudio.jrazor.CompiledTemplateEngine;

import java.util.Arrays;

public class RazorTemplateViewEngine extends CompiledTemplateEngine {
	public static final String TemplateStartFileName = "_TemplateStart";

	public RazorTemplateViewEngine() {
		this(null);
	}

	public RazorTemplateViewEngine(ITemplatePageActivator viewPageActivator) {
		super(viewPageActivator);
		setAreaTemplateLocationFormats(new String[] { "~/Areas/{2}/Templates/{1}/{0}.cshtml", "~/Areas/{2}/Templates/{1}/{0}.jhtml",
				"~/Areas/{2}/Templates/Shared/{0}.cshtml", "~/Areas/{2}/Templates/Shared/{0}.jhtml" });
		setAreaMasterLocationFormats(
				new String[] { "~/Areas/{2}/Templates/{1}/{0}.cshtml", "~/Areas/{2}/Templates/{1}/{0}.jhtml",
						"~/Areas/{2}/Templates/Shared/{0}.cshtml", "~/Areas/{2}/Templates/Shared/{0}.jhtml" });
		setAreaPartialTemplateLocationFormats(
				new String[] { "~/Areas/{2}/Templates/{1}/{0}.cshtml", "~/Areas/{2}/Templates/{1}/{0}.jhtml",
						"~/Areas/{2}/Templates/Shared/{0}.cshtml", "~/Areas/{2}/Templates/Shared/{0}.jhtml" });

		setTemplateLocationFormats(new String[] { "~/Templates/{1}/{0}.cshtml", "~/Templates/{1}/{0}.jhtml",
				"~/Templates/Shared/{0}.cshtml", "~/Templates/Shared/{0}.jhtml" });
		setMasterLocationFormats(new String[] { "~/Templates/{1}/{0}.cshtml", "~/Templates/{1}/{0}.jhtml",
				"~/Templates/Shared/{0}.cshtml", "~/Templates/Shared/{0}.jhtml" });
		setPartialTemplateLocationFormats(new String[] { "~/Templates/{1}/{0}.cshtml", "~/Templates/{1}/{0}.jhtml",
				"~/Templates/Shared/{0}.cshtml", "~/Templates/Shared/{0}.jhtml" });

		setFileExtensions(Arrays.asList( "cshtml", "jhtml" ));
	}

	@Override
	protected ITemplate createPartialTemplate(TemplateContext templateContext, String partialPath) throws Exception {
		RazorTemplate tempVar = new RazorTemplate(templateContext.getContext(), partialPath, "", false, getFileExtensions(),
				getTemplatePageActivator());
		tempVar.setDisplayModeProvider(getDisplayModeProvider());
		return tempVar;
	}

	@Override
	protected ITemplate createTemplate(TemplateContext templateHostContext, String viewPath, String masterPath) throws Exception {
		RazorTemplate view = new RazorTemplate(templateHostContext.getContext(), viewPath, masterPath, true, getFileExtensions(),
				getTemplatePageActivator());
		view.setDisplayModeProvider(getDisplayModeProvider());
		return view;
	}

	

	

	


}