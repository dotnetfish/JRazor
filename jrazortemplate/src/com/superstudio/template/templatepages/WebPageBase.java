package com.superstudio.template.templatepages;


import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.commons.exception.HttpException;
import com.superstudio.template.mvc.context.HostContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;


public abstract class WebPageBase extends WebPageRenderingBase {
		// Keep track of which sections renderSection has already been called on
	private final HashSet<String> _renderedSections = new HashSet<String>();
	// Keep track of whether renderBody has been called
	private boolean _renderedBody = false;
	// Runnable for rendering the body within a layout page
	private Consumer<Writer> _body;

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
	// Render(TemplateData,TextWriter)
	// is called.
	private HashMap<String, SectionWriter> getSectionWriters() {
		return getSectionWritersStack().peek();
	}

	private Stack<HashMap<String, SectionWriter>> getSectionWritersStack() {
		return getPageContext().getSectionWritersStack();
	}

	protected void configurePage(WebPageBase parentPage) {
	}

	public static WebPageBase createInstanceFromVirtualPath(String virtualPath) throws InstantiationException, IllegalAccessException, ClassNotFoundException,Exception {
		return createInstanceFromVirtualPath(virtualPath, VirtualPathFactoryManager.getInstance());
	}

	public static WebPageBase createInstanceFromVirtualPath(String virtualPath,
															IVirtualPathFactory virtualPathFactory) throws InstantiationException, IllegalAccessException, ClassNotFoundException,Exception {
		WebPageBase webPage = virtualPathFactory.createInstance(virtualPath);

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
	protected WebPageBase createPageFromVirtualPath(String virtualPath, HostContext httpContext,
													Predicate<String> virtualPathExists, DisplayModeProvider displayModeProvider, IDisplayMode displayMode)
					throws HttpException, InstantiationException, IllegalAccessException, ClassNotFoundException,Exception {
		DisplayInfo resolvedDisplayInfo = displayModeProvider.getDisplayInfoForVirtualPath(virtualPath, httpContext,
				virtualPathExists, displayMode);

		if (resolvedDisplayInfo != null) {
			WebPageBase webPage = getVirtualPathFactory().createInstance(resolvedDisplayInfo.getFilePath());

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
				String.format(WebPageResources.WebPage_InvalidPageType, virtualPath));
	}

	private WebPageContext createPageContextFromParameters(boolean isLayoutPage, Object... data) {
		Object first = null;
		if (data != null && data.length > 0) {
			first = data[0];
		}

		Map<Object, Object> pageData = createPageDataFromParameters(getPageData(), data);

		return WebPageContext.createNestedPageContext(getPageContext(), pageData, first, isLayoutPage);
	}

	private Map<Object, Object> createPageDataFromParameters(Map<Object, Object> pageData, Object[] data) {
		// TODO Auto-generated method stub
		return null;
	}

	public final void defineSection(String name, SectionWriter action)  {
		if (getSectionWriters().containsKey(name)) {
		//	throw new HttpException(
				//	String.format(Locale.InvariantCulture, WebPageResources.WebPage_SectionAleadyDefined, name));
		}
		getSectionWriters().put(name, action);
	}

	public final void ensurePageCanBeRequestedDirectly(String methodName) throws HttpException {
		if (getPreviousSectionWriters() == null) {
			throw new HttpException(String.format(WebPageResources.WebPage_CannotRequestDirectly, getVirtualPath(), methodName));
		}
	}

	public final void executePageHierarchy(WebPageContext pageContext, Writer writer) throws Exception {
		executePageHierarchy(pageContext, writer, null);
	}

	// This method is only used by WebPageBase to allow passing in the template
	// context and writer.
	public final void executePageHierarchy(WebPageContext pageContext,
										   Writer writer, WebPageRenderingBase startPage)
			throws Exception {

		pushContext(pageContext, writer);

		if (startPage != null) {
			if (startPage != this) {
				WebPageContext startPageContext = WebPageContext.createNestedPageContext(
						pageContext,
						null,
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
	public void executePageHierarchy() throws Exception {
		// Unlike InitPages, for a WebPage there is no hierarchy - it is always
		// the last file to get in the chain. There can still be layout
		// pages
		// and partial pages, but they are never part of the hierarchy.

		// (add server header for falcon debugging)
		// call to MapPath() is expensive. If we are not emiting source files to
		// header,
		// don't bother to populate the SourceFiles collection. This saves perf
		// significantly.
		if (WebPageHttpHandler.shouldGenerateSourceHeader(getContext())) {
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

		TemplateStack.push(getContext(), this);

		try{	// get the developer-written code of the WebPage

			execute();


		} finally {
			TemplateStack.pop(getContext());
		}
	}

	protected void initializePage() {
	}

	public final boolean isSectionDefined(String name) throws HttpException {
		ensurePageCanBeRequestedDirectly("isSectionDefined");
		return getPreviousSectionWriters().containsKey(name);
	}

	public final void popContext() throws Exception {
		// Using the copyTo extension method on the _tempWriter instead of
		// .ToString()
		// to avoid allocating large strings that then end up on the Large
		// object heap.
		getOutputStack().pop();

		if (!StringUtils.isBlank(getLayout())) {

			String layoutPagePath = getLayout();//normalizeLayoutPagePath(getLayout());

			// If a layout file was specified, render it passing our page
			// content.
			getOutputStack().push(_currentWriter);
			renderSurrounding(layoutPagePath, (p) -> {
				try {

					StringReader stream = new StringReader(_tempWriter.getBuffer().toString());
					IOUtils.copy(stream, p);

				} catch (Exception e) {
					e.printStackTrace();
					//throw e;
				}
			});
			getOutputStack().pop();
		} else {
			// Otherwise, just render the page.
			// _tempWriter.copyTo(_currentWriter);
			//long time=System.currentTimeMillis();
			StringReader stream = new StringReader(_tempWriter.getBuffer().toString());
			IOUtils.copy(stream, _currentWriter);
			//long time2=System.currentTimeMillis();
			//CodeExecuteTimeStatistic.evalute(this.getClass().getName()+".copyStream",time2-time);

		}

		verifyRenderedBodyOrSections();
		getSectionWritersStack().pop();
	}

	public final void pushContext(WebPageContext pageContext, Writer writer) throws FileNotFoundException {
		_currentWriter = writer;
		setPageContext(pageContext);
		pageContext.setPage(this);

		initializePage();

		// create a temporary writer
		_tempWriter = new StringWriter();

		// Render the page into it
		getOutputStack().push(_tempWriter);
		getSectionWritersStack().push(new HashMap<String, SectionWriter>());

		// If the body is defined in the TemplateData, remove it and store it on the
		// instance
		// so that it won't affect rendering of partial pages when they call
		// verifyRenderedBodyOrSections
		if (getPageContext().getBodyAction() != null) {
			_body = getPageContext().getBodyAction();
			getPageContext().setBodyAction(null);
		}
	}

	public final HelperResult renderBody()  {
		try {
			ensurePageCanBeRequestedDirectly("renderBody");

			if (_renderedBody) {
				throw new HttpException(WebPageResources.WebPage_RenderBodyAlreadyCalled);
			}
			_renderedBody = true;

			// _body should have previously been set in
			// Render(TemplateContext,TextWriter) if it
			// was available in the TemplateData.
			if (_body != null) {
				return new HelperResult(tw -> _body.accept(tw));
			} else {
				throw new HttpException(String.format(WebPageResources.WebPage_CannotRequestDirectly, getVirtualPath(), "renderBody"));
			}
		}catch (Exception ex){
			return  null;
		}
	}

	@Override
	public HelperResult renderPage(String path, Object... data) throws Exception {
		return renderPageCore(path, false, data);
	}

	private HelperResult renderPageCore(final String path1, boolean isLayoutPage, Object[] data) throws Exception {
		if (StringUtils.isBlank(path1)) {
			throw new IllegalArgumentException(CommonResources.Argument_Cannot_Be_Null_Or_Empty + "path");
		}

		return new HelperResult(writer -> {
			String path = path1;//normalizePath(path1);
			WebPageBase subPage = null;
			try {
				subPage = createPageFromVirtualPath(
						path,
						getContext(),
						(p) -> getVirtualPathFactory().exists(p),
						getDisplayModeProvider(), getDisplayMode());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			WebPageContext pageContext = createPageContextFromParameters(isLayoutPage, data);

			subPage.configurePage(this);
			try {
				subPage.executePageHierarchy(pageContext, writer);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	public final HelperResult renderSection(String name)  throws ArgumentNullException, HttpException {
		return renderSection(name, true);
	}

	public final HelperResult renderSection(String name, boolean required) throws ArgumentNullException, HttpException {

		ensurePageCanBeRequestedDirectly("renderSection");

		if (getPreviousSectionWriters().containsKey(name)) {
			HelperResult result = new HelperResult(tw -> {
				if (_renderedSections.contains(name)) {
return;
						//throw new HttpException(String.format(
						//		WebPageResources.WebPage_SectionAleadyRendered, name));

				}
				SectionWriter body = getPreviousSectionWriters().get(name);
				// Since the body can also call renderSection, we need to
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
					String.format(WebPageResources.WebPage_SectionNotDefined, name));
		} else {
			// If the section is optional and not found, then don't do anything.
			return null;
		}

	}

	private void renderSurrounding(String partialTemplateName, Consumer<Writer> body) throws Exception {
		// Save the previous body action and set ours instead.
		// This value will be retrieved by the sub-page being rendered when it
		// runs
		// Render(TemplateData, TextWriter).

		Consumer<Writer> priorValue = getPageContext().getBodyAction();
		getPageContext().setBodyAction(body);

		// Render the layout file
		write(renderPageCore(partialTemplateName, true, new Object[0]));

		// Restore the state
		getPageContext().setBodyAction(priorValue);
	}

	// Verifies that renderBody is called, or that renderSection is called for
	// all sections
	private void verifyRenderedBodyOrSections() throws HttpException {
		// The _body will be set within a layout page because
		// PageContext.BodyAction was set by renderSurrounding,
		// which is only called in the case of rendering layout pages.
		// Using renderPage will not result in a _body being set in a partial
		// page, thus the following checks for
		// sections should not apply when renderPage is called.
		// Dev10 bug 928341
		if (_body != null) {
			if (getSectionWritersStack().size() > 1 && getPreviousSectionWriters() != null
					&& getPreviousSectionWriters().size() > 0) {
				// There are sections defined. Check that all sections have been
				// rendered.
				StringBuilder sectionsNotRendered = new StringBuilder();

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
							String.format( WebPageResources.WebPage_SectionsNotRendered,
									getVirtualPath(), sectionsNotRendered.toString()));
				}
			} else if (!_renderedBody) {
				// There are no sections defined, but renderBody was NOT called.
				// If a body was defined, then renderBody should have been
				// called.
				throw new HttpException(String.format(
						WebPageResources.WebPage_RenderBodyNotCalled, getVirtualPath()));
			}
		}
	}

	@Override
	public void write(HelperResult result) {
		writeTo(getOutput(), result);
	}

	@Override
	public void write(Object value) {
		try {
			if (value == null)
				return;
			writeTo(getOutput(), value);
		} catch (Exception e) {

		}
	}

	public  void write(int value) throws IOException{
		getOutput().write(String.valueOf(value));
	}
	public  void write(long value) throws IOException{
		getOutput().write(String.valueOf(value));
	}
	public  void write(boolean value) throws IOException{
		getOutput().write(String.valueOf(value));
	}

	public  void write(byte value) throws IOException{
		getOutput().write(value);
	}

	public  void write(double value) throws IOException{
		getOutput().write(String.valueOf(value));
	}

	public  void write(short value) throws IOException{
		getOutput().write(String.valueOf(value));
	}

	public void write(Date value) throws  IOException{

		getOutput().write(String.valueOf(value));
	}


	@Override
	public void writeLiteral(Object value) throws IOException {

			if (value == null || StringUtils.isBlank(value.toString())){
				return;
			}
		//	getOutput().

			getOutput().write(value.toString());

	}

	@Override
	protected Writer getOutputWriter() {
		return getOutput();
	}
}