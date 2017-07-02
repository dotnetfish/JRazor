package com.superstudio.demo.controller;

import com.superstudio.commons.CodeExecuteTimeStatistic;
import com.superstudio.template.JRazorTemplateEngine;
import com.superstudio.template.mvc.actionresult.TemplateDataDictionary;
import com.superstudio.template.mvc.context.TemplateInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kenqu on 2016/1/9.
 */
@Controller
@RequestMapping({"/home","/"})
public class HomeController {
    @RequestMapping({"/index","/"})
    public ModelAndView index(@RequestParam(value="name", defaultValue="performence") String name,@RequestParam(value="test", defaultValue="no")String test) throws Exception {

      //  HostContext host =HostContext.getCurrent();

        //host.setVirtualPathProvider(new DefaultVirtualPathProvider());
        // somewhere you want to render a template
        TemplateInfo template=new TemplateInfo();//or load templateInfo from database
        template.setTemplateCategory("home");
        template.setTemplateName(name);
        //autoCloseable
        //String result="content is empty";
        TestEntry entry=new TestEntry();
        TestEntry father=new TestEntry();
        father.setName("lily's father");
        father.setAge(40);
        father.setGender(1);

        entry.setName("Lily");
        entry.setAge(18);
        entry.setGender(0);
        entry.setFather(father);
        StringWriter writer=new StringWriter();

        TemplateDataDictionary data=new TemplateDataDictionary(null);
        data.setModel(StockModel.dummyItems());

        CodeExecuteTimeStatistic.reset();
        JRazorTemplateEngine.render(template,data,writer);
        String result1=writer.toString();
        if("yes".equals(test)){
            long timeStart=System.currentTimeMillis();
            for (int i = 0;
                 i<200000;i++){
                StringWriter writer2=new StringWriter();
                JRazorTemplateEngine.render(template,data,writer2);
            }
            long timeEnd= System.currentTimeMillis();
                 Map<String,Long> result= CodeExecuteTimeStatistic.getList();
            for (Map.Entry v: result.entrySet()
                 ) {
                result1+="<br/> time evalute:"+v.getKey()+"="+String.valueOf(v.getValue());
            }
           result1+= result1+" it took "+String.valueOf(timeEnd-timeStart);
        }


      //  StringReader sr=new StringReader();

        Map<String, String> map = new HashMap<String, String>();
        map.put("html", result1);
        return new ModelAndView("/render",map);
    }


}
