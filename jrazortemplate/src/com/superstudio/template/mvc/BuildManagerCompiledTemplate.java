package com.superstudio.template.mvc;

import com.superstudio.commons.CodeExecuteTimeStatistic;
import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.MvcResources;
import com.superstudio.commons.RuntimeCache;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.template.mvc.context.RenderContext;
import org.apache.commons.lang3.StringUtils;

import java.io.Writer;


public abstract class BuildManagerCompiledTemplate implements ITemplate {
    public ITemplatePageActivator templatePageActivator;
    private IBuildManager buildManager;
    private RenderContext renderContext;

    protected BuildManagerCompiledTemplate(RenderContext renderContext, String templatePath) {
        this(renderContext, templatePath, null);
    }

    protected BuildManagerCompiledTemplate(RenderContext renderContext, String templatePath, ITemplatePageActivator templatePageActivator) {
        this(renderContext, templatePath, templatePageActivator, null);
    }

    public BuildManagerCompiledTemplate(RenderContext renderContext, String templatePath, ITemplatePageActivator templatePageActivator, IDependencyResolver dependencyResolver) {
        if (renderContext == null) {
            throw new IllegalArgumentException("renderContext");
        }
        if (StringUtils.isBlank(templatePath)) {
            throw new IllegalArgumentException(MvcResources.Common_NullOrEmpty + "templatePath");
        }

        this.renderContext = renderContext;

        setTemplatePath(templatePath);

        this.templatePageActivator = (templatePageActivator != null) ? templatePageActivator : new BuildManagerTemplateEngine.DefaultTemplatePageActivator(dependencyResolver);
    }

    public final IBuildManager getBuildManager() {
        if (buildManager == null) {
            buildManager = new BuildManagerWrapper();
        }
        return buildManager;
    }

    public final void setBuildManager(IBuildManager value) {
        buildManager = value;
    }

    private String TemplatePath;

    public final String getTemplatePath() {
        return TemplatePath;
    }

    protected final void setTemplatePath(String value) {
        TemplatePath = value;
    }

    @Override
    public void render(TemplateContext templateContext, Writer writer) throws InstantiationException, IllegalAccessException, Exception {
        if (templateContext == null) {
            throw new IllegalArgumentException("templateContext");
        }

        Object instance = null;

        String key = "_instance_" + getTemplatePath().replace("~", "__").replace("/", ".");
        instance = RuntimeCache.getInstance().get(key);
        if (instance == null) {
            java.lang.Class type = getBuildManager().getCompiledType(getTemplatePath());
            if (type != null) {
                instance = templatePageActivator.create(renderContext, type);
            }
            if (instance != null) {
                RuntimeCache.getInstance().set(key, instance);
            }

        }

        if (instance == null) {
            throw new IllegalStateException(String.format(
                    MvcResources.JhtmlTemplate_TemplateCouldNotBeCreated,
                    new Object[]{getTemplatePath()}));
        }

        try {

            renderTemplate(templateContext, writer, instance);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    protected abstract void renderTemplate(TemplateContext templateContext, Writer writer, Object instance) throws Exception;
}