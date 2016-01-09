package com.superstudio.jrazor.template;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.MvcResources;
import com.superstudio.commons.csharpbridge.StringHelper;


import java.io.Writer;


public abstract class BuildManagerCompiledView implements ITemplate
{
	public ITemplatePageActivator viewPageActivator;
	private IBuildManager buildManager;
	private TemplateRenderContext _controllerContext;

	protected BuildManagerCompiledView(TemplateRenderContext controllerContext, String viewPath)
	{
		this(controllerContext, viewPath, null);
	}

	protected BuildManagerCompiledView(TemplateRenderContext controllerContext, String viewPath, ITemplatePageActivator viewPageActivator)
	{
		this(controllerContext, viewPath, viewPageActivator, null);
	}

	public BuildManagerCompiledView(TemplateRenderContext controllerContext, String viewPath, ITemplatePageActivator viewPageActivator, IDependencyResolver dependencyResolver)
	{
		if (controllerContext == null)
		{
			throw new IllegalArgumentException("controllerContext");
		}
		if (StringHelper.isNullOrEmpty(viewPath))
		{
			throw new IllegalArgumentException(MvcResources.Common_NullOrEmpty+"viewPath");
		}

		_controllerContext = controllerContext;

		setViewPath(viewPath);

		this.viewPageActivator = (viewPageActivator != null) ? viewPageActivator :
				new CompiledTemplateEngine().DefaultViewPageActivator(dependencyResolver);
	}

	public final IBuildManager getBuildManager()
	{
		if (buildManager == null)
		{
			buildManager = new BuildManagerWrapper();
		}
		return buildManager;
	}
	public final void setBuildManager(IBuildManager value)
	{
		buildManager = value;
	}

	private String ViewPath;
	public final String getViewPath()
	{
		return ViewPath;
	}
	protected final void setViewPath(String value)
	{
		ViewPath = value;
	}

	@Override
	public void render(TemplateContext viewContext, Writer writer) throws InstantiationException, IllegalAccessException
	{
		if (viewContext == null)
		{
			throw new IllegalArgumentException("viewContext");
		}

		Object instance = null;

		java.lang.Class type = getBuildManager().getCompiledType(getViewPath());
		if (type != null)
		{
			instance = viewPageActivator.Create(_controllerContext, type);
		}

		if (instance == null)
		{
			throw new IllegalStateException(StringHelper.format(CultureInfo.CurrentCulture, MvcResources.JhtmlView_ViewCouldNotBeCreated, new Object[]{getViewPath()}));
		}

		try {
			renderView(viewContext, writer, instance);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected abstract void renderView(TemplateContext viewContext, Writer writer, Object instance) throws Exception;
}