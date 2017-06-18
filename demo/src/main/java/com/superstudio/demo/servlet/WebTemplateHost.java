package com.superstudio.demo.servlet;

import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.template.mvc.context.HostContext;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by kenqu on 2015/12/27.
 */
public class WebTemplateHost extends HostContext {
    static  final  String pathSplit="/";
    public  WebTemplateHost(){
        super();
    }
    @Override
    public String mapPath(String path){
      String root=  this.getClass()
              .getClassLoader()
              .getResource("../../")
              .getPath();

return root+StringUtils.removeStart(path,pathSplit);

    }
}
