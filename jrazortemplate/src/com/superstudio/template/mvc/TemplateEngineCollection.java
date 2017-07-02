package com.superstudio.template.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.superstudio.commons.MvcResources;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.exception.ArgumentException;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.template.mvc.actionresult.ITemplateEngine;
import com.superstudio.template.mvc.actionresult.TemplateEngineResult;
import com.superstudio.template.mvc.context.RenderContext;
import org.apache.commons.lang3.StringUtils;

public class TemplateEngineCollection extends ArrayList<ITemplateEngine> {

	// private ITemplateEngine[] _combinedItems;
	private IDependencyResolver _dependencyResolver;

	public TemplateEngineCollection() {
	}

	public TemplateEngineCollection(List<ITemplateEngine> list)

	{
		// super(list);
		super(list);
		// _combinedItems=list.toArray(new ITemplateEngine[]{});
	}

	TemplateEngineCollection(List<ITemplateEngine> list, IDependencyResolver dependencyResolver)

	{
		// super(lsit);
		super(list);
		// _combinedItems=list.toArray(new ITemplateEngine[]{});

		_dependencyResolver = dependencyResolver;
	}

	// private ITemplateEngine[] combinedItems;

	/*
	 * { get { ITemplateEngine[] combinedItems = _combinedItems; if (combinedItems
	 * == null) { combinedItems =
	 * MultiServiceResolver.GetCombined<ITemplateEngine>(Items,
	 * _dependencyResolver); _combinedItems = combinedItems; } return
	 * combinedItems; } }
	 */
	protected void clearItems() {
		// _combinedItems = null;
		super.clear();
		// super.();;
	}

	protected void insertItem(int index, ITemplateEngine item) throws ArgumentNullException {
		if (item == null) {
			throw new ArgumentNullException("item");
		}
		//_combinedItems = null;
		super.add(index, item);
	}

	protected void removeItem(int index) {
		//_combinedItems = null;
		super.remove(index);
	}

	protected void setItem(int index, ITemplateEngine item) throws ArgumentNullException {
		if (item == null) {
			throw new ArgumentNullException("item");
		}
		//_combinedItems = null;
		super.add(index, item);
	}

	private TemplateEngineResult find(Function<ITemplateEngine, TemplateEngineResult> cacheLocator,
								  Function<ITemplateEngine, TemplateEngineResult> locator) throws ArgumentNullException {
		// First, look up using the cacheLocator and do not track the searched
		// paths in non-matching template engines
		// Then, look up using the normal locator and track the searched paths
		// so that an error template engine can be returned
		TemplateEngineResult result = find(cacheLocator, false);
		if (result == null)
			result = find(locator, true);
		return result;

	}

	private TemplateEngineResult find(Function<ITemplateEngine, TemplateEngineResult> lookup, boolean trackSearchedPaths)
			throws ArgumentNullException {
		// Returns
		// 1st result
		// OR list of searched paths (if trackSearchedPaths == true)
		// OR null
		TemplateEngineResult result;

		List<String> searched = null;
		if (trackSearchedPaths) {
			searched = new ArrayList<String>();
		}

		for (ITemplateEngine engine : this) {
			if (engine != null) {
				result = lookup.apply(engine);

				if (result.getTemplate() != null) {
					return result;
				}

				if (trackSearchedPaths) {
					searched.addAll(result.getSearchedLocations());
				}
			}
		}

		if (trackSearchedPaths) {
			// remove duplicate search paths since multiple template engines could
			// have potentially looked at the same path
			return new TemplateEngineResult(searched.stream().distinct().collect(Collectors.toList()));
		} else {
			return null;
		}
	}

	public TemplateEngineResult findPartialTemplate(RenderContext renderContext, String partialTemplateName)
			throws ArgumentNullException, ArgumentException {
		if (renderContext == null) {
			throw new ArgumentNullException("renderContext");
		}
		if (StringUtils.isBlank(partialTemplateName)) {
			throw new ArgumentException(MvcResources.Common_NullOrEmpty, "partialTemplateName");
		}

		return find(e -> {
			try {
				return e.findPartialTemplate(renderContext, partialTemplateName, true);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}
		} , e -> {
			try {
				return e.findPartialTemplate(renderContext, partialTemplateName, false);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				return null;
			}
		});
	}

	// @Override
	public TemplateEngineResult findTemplate(RenderContext renderContext, String templateName, String masterName)
			throws ArgumentNullException, ArgumentException, InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		if (renderContext == null) {
			throw new ArgumentNullException("renderContext");
		}
		if (StringUtils.isBlank(templateName)) {
			throw new ArgumentException(MvcResources.Common_NullOrEmpty, "templateName");
		}

		try {
			return find(e -> {
				try {
					return e.findTemplate(renderContext, templateName, masterName, true);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return null;
				}
			} , e -> {
				try {
					return e.findTemplate(renderContext, templateName, masterName, false);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return null;
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
