package com.superstudio.jrazor.template;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.csharpbridge.action.ActionOne;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.commons.exception.HttpException;
import com.superstudio.jrazor.resources.RazorResources;
import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;
import java.util.function.Predicate;


public abstract class TemplatePageBase extends TemplatePageRenderingBase {
	// private static final String VirtualPathFactory = null;
	// Keep track of which sections RenderSection has already been called on
	private final HashSet<String> _renderedSections = new HashSet<String>();
	// Keep track of whether RenderBody has been called
	private boolean _renderedBody = false;
	// Action for rendering the body within a layout page
	private ActionOne<Writer> _body;

	private StringWriter _tempWriter;
	private Writer _currentWriter;

	private Map<Object, Object> _dynamicPageData;

	private String layout;

	// @Override
	public String getLayout() {
		return layout;
	}

	// @Override
	public void setLayout(String value) {
		layout = value;
	}

	public final Writer getOutput() {
		return getOutputStack().peek();
	}

	public final Stack<Writer> getOutputStack() {
		return getPageContext().getOutputStack();
	}

	// @Override
	public Map<Object, Object> getPageData() {
		return getPageContext().getPageData();
	}

	// @Override
	public Map<Object, Object> getPage() {
		if (_dynamicPageData == null) {
			_dynamicPageData = new HashMap<Object, Object>(getPageData());
		}
		return _dynamicPageData;
	}

	// Retrieves the sections defined in the calling page. If this is null, that
	// means
	// this page has been requested directly.
	private HashMap<String, SectionWriter> getPreviousSectionWriters() {

		HashMap<String, SectionWriter> top = getSectionWritersStack().pop();
		HashMap<String, SectionWriter> previous = getSectionWritersStack().size() > 0 ? getSectionWritersStack().peek()
				: null;
		getSectionWritersStack().push(top);
		return previous;
	}

	// Retrieves the current Dictionary of sectionWriters on the stack without
	// poping it.
	// There should be at least one on the stack which is added when the
	// Render(ViewData,TextWriter)
	// is called.
	private HashMap<String, SectionWriter> getSectionWriters() {
		return getSectionWritersStack().peek();
	}

	private Stack<HashMap<String, SectionWriter>> getSectionWritersStack() {
		return getPageContext().getSectionWritersStack();
	}

	protected void ConfigurePage(TemplatePageBase parentPage) {
	}

	public static TemplatePageBase createInstanceFromVirtualPath(String virtualPath) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return createInstanceFromVirtualPath(virtualPath, VirtualPathFactoryManager.getInstance());
	}

	public static TemplatePageBase createInstanceFromVirtualPath(String virtualPath,
																 IVirtualPathFactory virtualPathFactory) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		TemplatePageBase webPage = virtualPathFactory.createInstance(virtualPath);

		// Give it its virtual path
		webPage.setVirtualPath(virtualPath);

		// Assign it the VirtualPathFactory
		webPage.setVirtualPathFactory(virtualPathFactory);

		return webPage;
	}

	/**
	 * Attempts to create a WebPageBase instance from a virtualPath and wraps
	 * complex compiler exceptions with simpler messages
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	protected TemplatePageBase createPageFromVirtualPath(String virtualPath, TemplateHostContext context,
			Predicate<String> virtualPathExists, DisplayModeProvider displayModeProvider, IDisplayMode displayMode)
					throws HttpException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		DisplayInfo resolvedDisplayInfo = displayModeProvider.getDisplayInfoForVirtualPath(virtualPath, context,
				virtualPathExists, displayMode);

		if (resolvedDisplayInfo != null) {
			TemplatePageBase webPage = getVirtualPathFactory().createInstance(resolvedDisplayInfo.getFilePath());

			if (webPage != null) {
				// Give it its virtual path
				webPage.setVirtualPath(virtualPath);
				webPage.setVirtualPathFactory(getVirtualPathFactory());
				webPage.setDisplayModeProvider(getDisplayModeProvider());

				return webPage;
			}
		}
		// The page is missing, could not be compiled or is of an invalid type.
		throw new HttpException(
				String.format(CultureInfo.CurrentCulture, WebPageResources.WebPage_InvalidPageType, virtualPath));
	}

	private TemplatePageContext createPageContextFromParameters(boolean isLayoutPage, Object... data) {
		Object first = null;
		if (data != null && data.length > 0) {
			first = data[0];
		}

		Map<Object, Object> pageData = createPageDataFromParameters(getPageData(), data);

		return TemplatePageContext.CreateNestedPageContext(getPageContext(), pageData, first, isLayoutPage);
	}

	private Map<Object, Object> createPageDataFromParameters(Map<Object, Object> pageData, Object[] data) {
		// TODO Auto-generated method stub
		return null;
	}

	public final void defineSection(String name, SectionWriter action) throws HttpException {
		if (getSectionWriters().containsKey(name)) {
			throw new HttpException(
					String.format(CultureInfo.InvariantCulture, WebPageResources.WebPage_SectionAleadyDefined, name));
		}
		getSectionWriters().put(name, action);
	}

	public final void ensurePageCanBeRequestedDirectly(String methodName) throws HttpException {
		if (getPreviousSectionWriters() == null) {
			throw new HttpException(String.format(CultureInfo.CurrentCulture,
					WebPageResources.WebPage_CannotRequestDirectly, getVirtualPath(), methodName));
		}
	}

	public final void ExecutePageHierarchy(TemplatePageContext pageContext, Writer writer) throws Exception {
		ExecutePageHierarchy(pageContext, writer, null);
	}

	// This method is only used by WebPageBase to allow passing in the view
	// context and writer.
	public final void ExecutePageHierarchy(TemplatePageContext pageContext, Writer writer, TemplatePageRenderingBase startPage)
			throws Exception {
		pushContext(pageContext, writer);

		if (startPage != null) {
			if (startPage != this) {
				TemplatePageContext startPageContext = TemplatePageContext.CreateNestedPageContext(pageContext, null,
						null, false);
				startPageContext.setPage(startPage);
				startPage.setPageContext(startPageContext);
			}
			startPage.executePageHierarchy();
		} else {
			executePageHierarchy();
		}
		popContext();
	}

	@Override
	public void executePageHierarchy() throws ArgumentNullException {
		// Unlike InitPages, for a WebPage there is no hierarchy - it is always
		// the last file to execute in the chain. There can still be layout
		// pages
		// and partial pages, but they are never part of the hierarchy.

		// (add server header for falcon debugging)
		// call to MapPath() is expensive. If we are not emiting source files to
		// header,
		// don't bother to populate the SourceFiles collection. This saves perf
		// significantly.
		if (RazorHostEnvirenments.shouldGenerateSourceHeader(getContext())) {
			try {
				String vp = getVirtualPath();
				if (vp != null) {
					String path = getContext().mapPath(vp);
					if (!path.isEmpty()) {
						getPageContext().getSourceFiles().add(path);
					}
				}
			} catch (java.lang.Exception e) {
				// we really don't care if this ever fails, so we swallow all
				// exceptions
			}
		}

		TemplateStack.Push(getContext(), this);
		try {
			// Execute the developer-written code of the WebPage
			execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			TemplateStack.Pop(getContext());
		}
	}

	protected void initializePage() {
	}

	public final boolean isSectionDefined(String name) throws HttpException {
		ensurePageCanBeRequestedDirectly("IsSectionDefined");
		return getPreviousSectionWriters().containsKey(name);
	}

	public final void popContext() throws Exception {
		// Using the CopyTo extension method on the _tempWriter instead of
		// .ToString()
		// to avoid allocating large strings that then end up on the Large
		// object heap.
		getOutputStack().pop();

		if (!StringHelper.isNullOrEmpty(getLayout())) {

			String layoutPagePath = NormalizeLayoutPagePath(getLayout());

			// If a layout file was specified, render it passing our page
			// content.
			getOutputStack().push(_currentWriter);
			renderSurrounding(layoutPagePath, (p) -> {
				try {

					StringReader stream = new StringReader(_tempWriter.getBuffer().toString());
					IOUtils.copy(stream, p);
					// _tempWriter.CopyTo(p);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			getOutputStack().pop();
		} else {
			// Otherwise, just render the page.
			// _tempWriter.CopyTo(_currentWriter);
			StringReader stream = new StringReader(_tempWriter.getBuffer().toString());
			IOUtils.copy(stream, _currentWriter);
		}

		VerifyRenderedBodyOrSections();
		getSectionWritersStack().pop();
	}

	public final void pushContext(TemplatePageContext pageContext, Writer writer) throws FileNotFoundException {
		_currentWriter = writer;
		setPageContext(pageContext);
		pageContext.setPage(this);

		initializePage();

		// Create a temporary writer
		_tempWriter = new StringWriter();

		// Render the page into it
		getOutputStack().push(_tempWriter);
		getSectionWritersStack().push(new HashMap<String, SectionWriter>());

		// If the body is defined in the ViewData, remove it and store it on the
		// instance
		// so that it won't affect rendering of partial pages when they call
		// VerifyRenderedBodyOrSections
		if (getPageContext().getBodyAction() != null) {
			_body = getPageContext().getBodyAction();
			getPageContext().setBodyAction(null);
		}
	}

	public final HelperResult renderBody() throws ArgumentNullException, HttpException {
		ensurePageCanBeRequestedDirectly("RenderBody");

		if (_renderedBody) {
			throw new HttpException(WebPageResources.WebPage_RenderBodyAlreadyCalled);
		}
		_renderedBody = true;

		// _body should have previously been set in
		// Render(ViewContext,TextWriter) if it
		// was available in the ViewData.
		if (_body != null) {
			return new HelperResult(tw -> _body.execute(tw));
		} else {
			throw new HttpException(String.format(CultureInfo.CurrentCulture,
					WebPageResources.WebPage_CannotRequestDirectly, getVirtualPath(), "RenderBody"));
		}
	}

	@Override
	public HelperResult renderPage(String path, Object... data) throws Exception {
		return renderPageCore(path, false, data);
	}

	private HelperResult renderPageCore(final String path1, boolean isLayoutPage, Object[] data) throws Exception {
		if (StringHelper.isNullOrEmpty(path1)) {
			throw new IllegalArgumentException(RazorResources.Argument_Cannot_Be_Null_Or_Empty + "path");
		}

		return new HelperResult(writer -> {
			String path = NormalizePath(path1);
			TemplatePageBase subPage = null;
			try {
				subPage = createPageFromVirtualPath(path, getContext(), (p) -> getVirtualPathFactory().exists(p),
						getDisplayModeProvider(), getDisplayMode());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TemplatePageContext pageContext = createPageContextFromParameters(isLayoutPage, data);

			subPage.ConfigurePage(this);
			try {
				subPage.ExecutePageHierarchy(pageContext, writer);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	public final HelperResult renderSection(String name) throws ArgumentNullException, HttpException {
		return renderSection(name, true);
	}

	public final HelperResult renderSection(String name, boolean required) throws ArgumentNullException, HttpException {
		ensurePageCanBeRequestedDirectly("RenderSection");

		if (getPreviousSectionWriters().containsKey(name)) {
			HelperResult result = new HelperResult(tw -> {
				if (_renderedSections.contains(name)) {
					try {
						throw new HttpException(String.format(CultureInfo.InvariantCulture,
								WebPageResources.WebPage_SectionAleadyRendered, name));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				SectionWriter body = getPreviousSectionWriters().get(name);
				// Since the body can also call RenderSection, we need to
				// temporarily remove
				// the current sections from the stack.

				HashMap<String, SectionWriter> top = getSectionWritersStack().pop();

				boolean pushed = false;
				try {
					if (getOutput() != tw) {
						getOutputStack().push(tw);
						pushed = true;
					}

					body.execute();
				} finally {
					if (pushed) {
						getOutputStack().pop();
					}
				}
				getSectionWritersStack().push(top);
				_renderedSections.add(name);
			});
			return result;
		} else if (required) {
			// If the section is not found, and it is not optional, throw an
			// error.
			throw new HttpException(
					String.format(CultureInfo.InvariantCulture, WebPageResources.WebPage_SectionNotDefined, name));
		} else {
			// If the section is optional and not found, then don't do anything.
			return null;
		}
	}

	private void renderSurrounding(String partialViewName, ActionOne<Writer> body) throws Exception {
		// Save the previous body action and set ours instead.
		// This value will be retrieved by the sub-page being rendered when it
		// runs
		// Render(ViewData, TextWriter).

		ActionOne<Writer> priorValue = getPageContext().getBodyAction();
		getPageContext().setBodyAction(body);

		// Render the layout file
		Write(renderPageCore(partialViewName, true, new Object[0]));

		// Restore the state
		getPageContext().setBodyAction(priorValue);
	}

	// Verifies that RenderBody is called, or that RenderSection is called for
	// all sections
	private void VerifyRenderedBodyOrSections() throws HttpException {
		// The _body will be set within a layout page because
		// PageContext.BodyAction was set by RenderSurrounding,
		// which is only called in the case of rendering layout pages.
		// Using RenderPage will not result in a _body being set in a partial
		// page, thus the following checks for
		// sections should not apply when RenderPage is called.
		// Dev10 bug 928341
		if (_body != null) {
			if (getSectionWritersStack().size() > 1 && getPreviousSectionWriters() != null
					&& getPreviousSectionWriters().size() > 0) {
				// There are sections defined. Check that all sections have been
				// rendered.
				StringBuilder sectionsNotRendered = new StringBuilder();
				// C# TO JAVA CONVERTER TODO TASK: There is no equivalent to
				// implicit typing in Java:
				for (String name : getPreviousSectionWriters().keySet()) {
					if (!_renderedSections.contains(name)) {
						if (sectionsNotRendered.length() > 0) {
							sectionsNotRendered.append("; ");
						}
						sectionsNotRendered.append(name);
					}
				}
				if (sectionsNotRendered.length() > 0) {
					throw new HttpException(
							String.format(CultureInfo.CurrentCulture, WebPageResources.WebPage_SectionsNotRendered,
									getVirtualPath(), sectionsNotRendered.toString()));
				}
			} else if (!_renderedBody) {
				// There are no sections defined, but RenderBody was NOT called.
				// If a body was defined, then RenderBody should have been
				// called.
				throw new HttpException(String.format(CultureInfo.CurrentCulture,
						WebPageResources.WebPage_RenderBodyNotCalled, getVirtualPath()));
			}
		}
	}

	@Override
	public void Write(HelperResult result) {
		TemplatePageExecutingBase.WriteTo(getOutput(), result);
	}

	@Override
	public void Write(Object value) {
		try {
			if (value == null)
				return;
			TemplatePageExecutingBase.WriteTo(getOutput(), value);
		} catch (Exception e) {

		}
	}

	@Override
	public void WriteLiteral(Object value) {
		try {
			if (value == null)
				return;
			getOutput().write(value.toString());
		} catch (Exception e) {

		}
	}

	@Override
	protected Writer GetOutputWriter() {
		return getOutput();
	}
}