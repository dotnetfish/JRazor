package com.superstudio.demo.servlet;

/**
 * Created by kenqu on 2016/1/9.
 */

import com.superstudio.template.mvc.RazorTemplateEngine;
import com.superstudio.template.mvc.context.HostContext;
import com.superstudio.template.mvc.templateengine.TemplateEngines;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MVCServletContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        // TODO Auto-generated method stub

        HostContext host =new WebTemplateHost();
        HostContext.initRequestContext(host);
        TemplateEngines.registe(new RazorTemplateEngine());
    }

}
