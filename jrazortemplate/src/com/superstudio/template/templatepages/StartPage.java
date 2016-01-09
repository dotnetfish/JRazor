package com.superstudio.template.templatepages;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.HttpRuntime;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.exception.HttpException;
import com.superstudio.commons.io.Path;
import com.superstudio.template.mvc.context.HostContext;
import com.superstudio.web.HttpContextBase;

/**
 * Wrapper class to be used by _pagestart.cshtml files to call into the actual
 * page. Most of the properties and methods just delegate the call to
 * childPage.XXX
 */
public abstract class StartPage extends WebPageRenderingBase {
	private WebPageRenderingBase childPage;

	public final WebPageRenderingBase getChildPage() {
		return childPage;
	}

	public final void setChildPage(WebPageRenderingBase value) {
		childPage = value;
	}

	@Override
	public HostContext getContext() {
		return getChildPage().getContext();
	}

	@Override
	public void setContext(HostContext value) {
		getChildPage().setContext(value);
	}

	@Override
	public String getLayout() {
		return getChildPage().getLayout();
	}

	@Override
	public void setLayout(String value) throws HttpException {
		if (value == null) {
			getChildPage().setLayout(null);
		} else {
			getChildPage().setLayout(normalizeLayoutPagePath(value));
		}
	}

	@Override
	public Map<Object, Object> getPageData() {
		return getChildPage().getPageData();
	}

	@Override
	public Object getPage() {
		return getChildPage().getPage();
	}

	private boolean RunPageCalled;

	public final boolean getRunPageCalled() {
		return RunPageCalled;
	}

	public final void setRunPageCalled(boolean value) {
		RunPageCalled = value;
	}

	@Override
	public void executePageHierarchy() throws Exception {
		// push the current pagestart on the stack.
		TemplateStack.push(getContext(), this);
		try {
			// execute the developer-written code of the InitPage
			execute();

			// If the child page wasn't explicitly run by the developer of the
			// InitPage, then run it now.
			// The child page is either the next InitPage, or the final WebPage.
			if (!getRunPageCalled()) {
				runPage();
			}
		} finally {
			TemplateStack.pop(getContext());
		}
	}

	/**
	 * Returns either the root-most init page, or the provided page itself if no
	 * init page is found
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	
	public static WebPageRenderingBase getStartPage(WebPageRenderingBase page, String fileName,
													Iterable<String> supportedExtensions) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		if (page == null) {
			throw new IllegalArgumentException("page");
		}
		if (StringHelper.isNullOrEmpty(fileName)) {
			throw new IllegalArgumentException(StringHelper.format(CultureInfo.CurrentCulture,
					CommonResources.Argument_Cannot_Be_Null_Or_Empty,new Object[]{ "fileName"})+ " fileName");
		}
		if (supportedExtensions == null) {
			throw new IllegalArgumentException("supportedExtensions");
		}

		// Use the page's VirtualPathFactory if available
		return getStartPage(page,
				(page.getVirtualPathFactory() != null) ? page.getVirtualPathFactory() : VirtualPathFactoryManager.getInstance(),
				HttpRuntime.getAppDomainAppVirtualPath(), fileName, supportedExtensions);
	}

	public static WebPageRenderingBase getStartPage(WebPageRenderingBase page, IVirtualPathFactory virtualPathFactory,
													String appDomainAppVirtualPath, String fileName, Iterable<String> supportedExtensions) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		// build up a list of pages to execute, such as one of the following:
		// ~/somepage.cshtml
		// ~/_pageStart.cshtml --> ~/somepage.cshtml
		// ~/_pageStart.cshtml --> ~/sub/_pageStart.cshtml -->
		// ~/sub/somepage.cshtml
		WebPageRenderingBase currentPage = page;
		
		String pageDirectory = VirtualPathUtility.getDirectory(page.getVirtualPath());

		// start with the requested page's directory, find the init page,
		// and then traverse up the hierarchy to find init pages all the
		// way up to the root of the app.
		while (!StringHelper.isNullOrEmpty(pageDirectory) && !pageDirectory.equals("/")
				&& Path.IsWithinAppRoot(appDomainAppVirtualPath, pageDirectory)) {
			// Go through the list of  supported extensions
		
			for (String extension : supportedExtensions) {
				
				String virtualPath = VirtualPathUtility.combine(pageDirectory, fileName + "." + extension);

				// Can we build a file from the current path?
				if (virtualPathFactory.exists(virtualPath)) {
					
					StartPage parentStartPage = virtualPathFactory.<StartPage>createInstance(virtualPath);
					parentStartPage.setVirtualPath(virtualPath);
					parentStartPage.setChildPage(currentPage);
					parentStartPage.setVirtualPathFactory(virtualPathFactory);
					currentPage = parentStartPage;

					break;
				}
			}

			pageDirectory = currentPage.getDirectory(pageDirectory);
		}

		// at this point 'currentPage' is the root-most StartPage (if there were
		// any StartPages at all) or it is the requested page itself.
		return currentPage;
	}

	@Override
	public HelperResult renderPage(String path, Object... data) throws Exception {
		return getChildPage().renderPage(normalizePath(path), data);
	}

	public final void runPage() throws Exception {
		setRunPageCalled(true);
		// childPage.PageContext = PageContext;
		getChildPage().executePageHierarchy();
	}

	@Override
	public void write(HelperResult result) {
		getChildPage().write(result);
	}

	@Override
	public void writeLiteral(Object value) throws IOException {
		getChildPage().writeLiteral(value);
	}

	@Override
	public void write(Object value) throws IOException {
		getChildPage().write(value);
	}

	@Override
	protected Writer getOutputWriter() {
		return getChildPage().getOutputWriter();
	}
}