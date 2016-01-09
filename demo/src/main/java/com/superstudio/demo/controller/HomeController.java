package com.superstudio.demo.controller;

import com.superstudio.template.JRazorTemplateEngine;
import com.superstudio.template.mvc.context.HostContext;
import com.superstudio.template.mvc.context.RenderContext;
import com.superstudio.template.mvc.context.TemplateInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kenqu on 2016/1/9.
 */
@Controller
public class HomeController {
    @RequestMapping("/home")
    public ModelAndView index(@RequestParam(value="name", defaultValue="index") String name) throws Exception {

        HostContext host =HostContext.getCurrent();

        //host.setVirtualPathProvider(new DefaultVirtualPathProvider());
        // somewhere you want to render a template
        TemplateInfo template=new TemplateInfo();//or load templateInfo from database
        template.setTemplateCategory("home");
        template.setTemplateName(name);
        //autoCloseable
        String result="content is empty";
        try(JRazorTemplateEngine engine=new JRazorTemplateEngine(host)){
            RenderContext context=new RenderContext(host,template,null);
            context.setHttpContext(host);
            context.setTemplateInfo(template);
            Writer writer=new StringWriter();
            context.setWriter(writer);
            //context.setRequestContext(context);
            //context.setRouteData();
            //... prepare  parameters,which you can access them by @ViewBag.get("variantName")
            context.getTemplateData().getTemplateData().put("myVariant","variantValue");//in the the template @ViewBag.get("myVariant") would output variantValue
            context.getTemplateData().setModel(new Object());//set a  template page model entry,then you can access it by @Model

            result=engine.renderTemplate(context,"index","");
            //or you can just render it to buffered Stream
            //BufferedStringWriter writer=new BufferedStringWriter();
            //engine.renderTempalte(renderContext,writer);
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("html", result);
        return new ModelAndView("/render",map);
    }
}
