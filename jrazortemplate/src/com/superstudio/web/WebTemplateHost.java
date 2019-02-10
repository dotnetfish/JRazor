package com.superstudio.web;

import com.superstudio.template.mvc.context.HostContext;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by kenqu on 2015/12/27.
 */
public class WebTemplateHost extends HostContext {
    static  final  String pathSplit="/";
    public WebTemplateHost(){

        super();
        this.rootPath= this.getClass()
                .getClassLoader()
                .getResource("../../")
                .getPath();
    }
    private String rootPath="";
    @Override
    public String mapPath(String path){

       // CodeExecuteTimeStatistic.evaluteTick(this.getClass().getName());

return this.rootPath+StringUtils.removeStart(path,pathSplit);

    }
}
