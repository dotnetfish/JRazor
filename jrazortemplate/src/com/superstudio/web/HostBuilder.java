package com.superstudio.web;

import com.superstudio.template.mvc.TemplateEngine;
import com.superstudio.template.mvc.actionresult.ITemplateEngine;
import com.superstudio.template.mvc.context.HostContext;
import com.superstudio.template.mvc.templateengine.TemplateEngines;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.util.concurrent.TimeUnit;

/**
 * Created by T440P on 2016-6-20.
 */
public class HostBuilder {
    private HostContext host = null;

    public HostBuilder useTemplateEngine(ITemplateEngine engine) {
        TemplateEngines.registe(engine);
        return this;
    }

    public  HostBuilder useWebHost(){
        this.host=new WebTemplateHost();
        HostContext.initRequestContext(host);

        return this;
    }

    public HostBuilder useHost(HostContext host) {
        this.host = host;
        HostContext.initRequestContext(host);
        return this;
    }

    public HostBuilder configure(){
        return  this;
    }

    public  HostBuilder configure(String configurePath){
        return  this;
    }
/*
build host
 */
    public HostContext build() {
        return this.host;
    }
   static FileAlterationMonitor monitor=null;
    /*
    使用文件监控自动编译更新模板
     */
    public HostBuilder autoCompile(String templateRoot) {
        // 监控目录
        String rootDir = templateRoot;
        // 轮询间隔 1 秒
        long interval = TimeUnit.SECONDS.toMillis(1);
        // 创建一个文件观察器用于处理文件的格式
        FileAlterationObserver _observer = new FileAlterationObserver(
                rootDir,
                FileFilterUtils.or(FileFilterUtils.and(
                        FileFilterUtils.fileFileFilter(),
                        FileFilterUtils.suffixFileFilter(".jhtml")),
                        FileFilterUtils.directoryFileFilter()),  //过滤文件格式
                null);
        FileAlterationObserver observer = new FileAlterationObserver(rootDir);

        observer.addListener(new FileListener()); //设置文件变化监听器
        //创建文件变化监听器
       monitor= new FileAlterationMonitor(interval, observer);
        // 开始监控
        try {
            monitor.start();
        }catch (Exception ex){

        }
        return  this;
    }

}
