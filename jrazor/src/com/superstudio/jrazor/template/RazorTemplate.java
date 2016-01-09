package com.superstudio.jrazor.template;

import java.io.Writer;
import java.util.Collections;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.MvcResources;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.jrazor.*;

public class RazorTemplate extends CompiledTemplate {
	public RazorTemplate(TemplateHostContext templateHostContext, String viewPath, String layoutPath, boolean runViewStartPages,
			Iterable<String> viewStartFileExtensions)
					throws Exception {
		this(templateHostContext, viewPath, layoutPath, runViewStartPages, viewStartFileExtensions, null);
	}

	public RazorTemplate(TemplateHostContext templateHostContext, String viewPath, String layoutPath, boolean runViewStartPages,
			Iterable<String> viewStartFileExtensions, ITemplatePageActivator viewPageActivator)
					throws Exception {
		super(templateHostContext, viewPath, viewPageActivator);
		setLayoutPath((layoutPath != null) ? layoutPath : "");
		setRunViewStartPages(runViewStartPages);
		setStartPageLookup((a, b, c) -> {
			try {
				return StartPage.GetStartPage(a, b, c);
			} catch (Exception e) {
				return null;
			}
		});
		setViewStartFileExtensions(
				(viewStartFileExtensions != null) ? viewStartFileExtensions : Collections.emptyList());
	}

	private String LayoutPath;

	public final String getLayoutPath() {
		return LayoutPath;
	}

	private void setLayoutPath(String value) {
		LayoutPath = value;
	}

	private boolean RunViewStartPages;

	public final boolean getRunViewStartPages() {
		return RunViewStartPages;
	}

	private void setRunViewStartPages(boolean value) {
		RunViewStartPages = value;
	}

	private StartPageLookupDelegate StartPageLookup;

	public final StartPageLookupDelegate getStartPageLookup() {
		return StartPageLookup;
	}

	public final void setStartPageLookup(StartPageLookupDelegate value) {
		StartPageLookup = value;
	}

	private IVirtualPathFactory VirtualPathFactory;

	public final IVirtualPathFactory getVirtualPathFactory() {
		return VirtualPathFactory;
	}

	public final void setVirtualPathFactory(IVirtualPathFactory value) {
		VirtualPathFactory = value;
	}

	private DisplayModeProvider displayModeProvider;

	public final DisplayModeProvider getDisplayModeProvider() {
		return displayModeProvider;
	}

	public final void setDisplayModeProvider(DisplayModeProvider value) {
		displayModeProvider = value;
	}

	private Iterable<String> ViewStartFileExtensions;

	public final Iterable<String> getViewStartFileExtensions() {
		return ViewStartFileExtensions;
	}

	private void setViewStartFileExtensions(Iterable<String> value) {
		ViewStartFileExtensions = value;
	}

	@Override
	protected void renderTemplate(TemplateContext templateContext, Writer writer, Object instance) throws Exception {
		if (writer == null) {
			throw new IllegalArgumentException("writer");
		}

		TemplatePage webViewPage = (TemplatePage) ((instance instanceof TemplatePage) ? instance : null);
		if (webViewPage == null) {
			throw new IllegalStateException(StringHelper.format(CultureInfo.CurrentCulture,
					MvcResources.JhtmlView_WrongViewBase, new Object[] { getViewPath() }));
		}

		// An overriden master layout might have been specified when the
		// ViewActionResult got returned.
		// We need to hold on to it so that we can set it on the inner page once
		// it has executed.
		webViewPage.setOverridenLayoutPath(getLayoutPath());
		webViewPage.setVirtualPath(getViewPath());
		webViewPage.setTemplateContext(templateContext);
		//webViewPage.setViewData(templateContext.getTemplateData());

		webViewPage.InitHelpers();

		if (getVirtualPathFactory() != null) {
			webViewPage.setVirtualPathFactory(getVirtualPathFactory());
		}
		if (getDisplayModeProvider() != null) {
			webViewPage.setDisplayModeProvider(getDisplayModeProvider());
		}

		TemplatePageRenderingBase startPage = null;
		if (getRunViewStartPages()) {
			startPage = StartPageLookup.invoke(webViewPage, RazorTemplateViewEngine.TemplateStartFileName,
					getViewStartFileExtensions());
		}
		webViewPage.ExecutePageHierarchy(new TemplatePageContext(templateContext.getContext(), null, null), writer,
				startPage);
	}

}