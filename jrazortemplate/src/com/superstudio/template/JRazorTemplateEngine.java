package com.superstudio.template;

import com.superstudio.commons.exception.ArgumentException;
import com.superstudio.commons.exception.ArgumentNullException;
import com.superstudio.commons.exception.InvalidOperationException;
import com.superstudio.template.mvc.actionresult.TemplateDataDictionary;
import com.superstudio.template.mvc.actionresult.TemplateResult;
import com.superstudio.template.mvc.context.HostContext;
import com.superstudio.template.mvc.context.RenderContext;
import com.superstudio.template.mvc.context.TemplateInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

/**
 * Created by Chaoqun on 2015/11/11.
 */
public class JRazorTemplateEngine implements AutoCloseable {

    private HostContext hostContext;

    public JRazorTemplateEngine(HostContext hostContext) {
        this.hostContext = hostContext;
    }

    public String renderTemplate(RenderContext context, String templateName, String masterName) throws IllegalAccessException, IOException, ArgumentNullException, InvalidOperationException, InstantiationException, ArgumentException, ClassNotFoundException,Exception {
      /*  if (model != null) {
            getViewData().setModel(model);
        }*/

        TemplateResult result = new TemplateResult();
        result.setMasterName(masterName);
        result.setTemplateData(context.getTemplateData());
        result.setTemplateName(templateName);
        result.setTempData(context.getTemplateData().getTemplateData());
        result.setTemplateEngine(this.hostContext.getTemplateEngineCollection());

        result.execute(context);
        return context.getWriter().toString();
        //StringReader reader =new StringReader(context.getHttpContext().getResponse().getWriter().toString());

    }

    @Override
    public void close() throws Exception {

    }

    public static void render(TemplateInfo template, TemplateDataDictionary templateData, Writer writer) throws Exception {

           HostContext host = HostContext.getCurrent();
           RenderContext context = new RenderContext(host, template);

         context.setWriter(writer);
           context.setTemplateData(templateData);

          //... prepare  parameters,which you can access them by @ViewBag.get("variantName")

        //  String result=engine.renderTemplate(context,"index","");
           TemplateResult result = new TemplateResult(context);
           result.setMasterName("");
          // result.setTemplateData(context.getTemplateData());
           //result.setTemplateName(template.getTemplateName());
           //result.setTempData(context.getTemplateData().getTemplateData());
           //result.setTemplateEngine(host.getTemplateEngineCollection());
            result.execute(context);
          // result.execute(context);


    }
}
