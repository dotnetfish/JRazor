package com.superstudio.template.mvc;

import com.superstudio.commons.CodeExecuteTimeStatistic;
import com.superstudio.commons.MvcResources;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.template.mvc.context.RenderContext;
import com.superstudio.template.templatepages.*;

import java.io.Writer;
import java.util.Collections;

public class RazorTemplate extends BuildManagerCompiledTemplate {
	public RazorTemplate(RenderContext renderContext, String templatePath, String layoutPath, boolean runTemplateStartPages,
			Iterable<String> templateStartFileExtensions)
					throws Exception {
		this(renderContext, templatePath, layoutPath, runTemplateStartPages, templateStartFileExtensions, null);
	}

	public RazorTemplate(RenderContext renderContext, String templatePath, String layoutPath, boolean runTemplateStartPages,
			Iterable<String> templateStartFileExtensions, ITemplatePageActivator templatePageActivator)
					throws Exception {
		super(renderContext, templatePath, templatePageActivator);
		setLayoutPath((layoutPath != null) ? layoutPath : "");
		setRunTemplateStartPages(runTemplateStartPages);
		setStartPageLookup((a, b, c) -> {
			try {
				return StartPage.getStartPage(a, b, c);
			} catch (Exception e) {
				return null;
			}
		});
		setTemplateStartFileExtensions(
				(templateStartFileExtensions != null) ? templateStartFileExtensions : Collections.emptyList());
	}

	private String LayoutPath;

	public final String getLayoutPath() {
		return LayoutPath;
	}

	private void setLayoutPath(String value) {
		LayoutPath = value;
	}

	private boolean runTemplateStartPages;

	public final boolean getRunTemplateStartPages() {
		return runTemplateStartPages;
	}

	private void setRunTemplateStartPages(boolean value) {
		runTemplateStartPages = value;
	}

	private StartPageLookupDelegate startPageLookup;

	public final StartPageLookupDelegate getStartPageLookup() {
		return startPageLookup;
	}

	public final void setStartPageLookup(StartPageLookupDelegate value) {
		startPageLookup = value;
	}

	private IVirtualPathFactory virtualPathFactory;

	public final IVirtualPathFactory getVirtualPathFactory() {
		return virtualPathFactory;
	}

	public final void setVirtualPathFactory(IVirtualPathFactory value) {
		virtualPathFactory = value;
	}

	private DisplayModeProvider displayModeProvider;

	public final DisplayModeProvider getDisplayModeProvider() {
		return displayModeProvider;
	}

	public final void setDisplayModeProvider(DisplayModeProvider value) {
		displayModeProvider = value;
	}

	private Iterable<String> templateStartFileExtensions;

	public final Iterable<String> getTemplateStartFileExtensions() {
		return templateStartFileExtensions;
	}

	private void setTemplateStartFileExtensions(Iterable<String> value) {
		templateStartFileExtensions = value;
	}

	@Override
	protected void renderTemplate(TemplateContext templateContext, Writer writer, Object instance) throws Exception {
		if (writer == null) {
			throw new IllegalArgumentException("writer");
		}

		WebTemplatePage webTemplatePage = (WebTemplatePage) ((instance instanceof WebTemplatePage) ? instance : null);
		if (webTemplatePage == null) {
			throw new IllegalStateException(String.format(MvcResources.JhtmlTemplate_WrongTemplateBase, new Object[] { getTemplatePath() }));
		}

		// An overriden master layout might have been specified when the
		// TemplateActionResult got returned.
		// We need to hold on to it so that we can set it on the inner page once
		// it has executed.
		webTemplatePage.setOverridenLayoutPath(getLayoutPath());
		webTemplatePage.setVirtualPath(getTemplatePath());
		webTemplatePage.setTemplateContext(templateContext);
		webTemplatePage.setTemplateData(templateContext.getTemplateData());

		webTemplatePage.initHelpers();

		if (getVirtualPathFactory() != null) {
			webTemplatePage.setVirtualPathFactory(getVirtualPathFactory());
		}
		if (getDisplayModeProvider() != null) {
			webTemplatePage.setDisplayModeProvider(getDisplayModeProvider());
		}

		WebPageRenderingBase startPage = null;
		if (getRunTemplateStartPages()) {
			startPage = startPageLookup.invoke(webTemplatePage, RazorTemplateEngine.TemplateStartFileName,
					getTemplateStartFileExtensions());
		}
long stattime=System.currentTimeMillis();
		webTemplatePage.executePageHierarchy(
				new WebPageContext(templateContext.getHttpContext(),
						null,
						null), writer,
				startPage);
		long end=System.currentTimeMillis();
		CodeExecuteTimeStatistic.evalute("webTemplatePage.executePageHierarchy",end-stattime);

	}

}