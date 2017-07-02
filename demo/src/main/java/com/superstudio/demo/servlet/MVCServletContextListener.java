package com.superstudio.demo.servlet;

/**
 * Created by kenqu on 2016/1/9.
 */

import com.superstudio.template.mvc.RazorTemplateEngine;
import com.superstudio.template.mvc.context.HostContext;
import com.superstudio.template.mvc.templateengine.TemplateEngines;
import com.superstudio.web.HostBuilder;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MVCServletContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {

        HostBuilder builder=new HostBuilder();
        WebTemplateHost templateHost=new WebTemplateHost();
       HostContext host= builder.useHost(templateHost)
               .useAutoCompiler(templateHost.mapPath("/web-inf/templates"))
               .useTemplateEngine(new RazorTemplateEngine()).build();

    }

}
