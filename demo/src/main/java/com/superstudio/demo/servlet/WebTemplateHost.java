package com.superstudio.demo.servlet;

import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.template.mvc.context.HostContext;

/**
 * Created by kenqu on 2015/12/27.
 */
public class WebTemplateHost extends HostContext {
    public  WebTemplateHost(){
        super();
    }
    @Override
    public String mapPath(String path){
        return StringHelper.trimStart(this.getClass().getClassLoader().getResource("../../").getPath(),'/')+ StringHelper.trimStart(path,'/');

        // return System.getProperty("catalina.home")+path;
    }
}
