//------------------------------------------------------------------------------
// <//<descripts>
//     //<descripts>
//     //<descripts>v1.0.0
//
//     //<descripts>
//     //<descripts>
// <///<descripts>
//------------------------------------------------------------------------------

package JRazor;
import com.superstudio.template.templatepages.WebTemplatePage;
import java.util.Date;
import com.superstudio.demo.controller.TestEntry;


public class _Page_Templates_home_index_jhtml extends WebTemplatePage<com.superstudio.demo.controller.TestEntry> {
    
//#line hidden
    
    public _Page_Templates_home_index_jhtml() {
    }
    
    @Override 
    public  void execute() {
        beginContext("Templates/home/index.jhtml", 0, 1, true);

        writeLiteral("﻿");

        endContext("Templates/home/index.jhtml", 0, 1, true);

        beginContext("Templates/home/index.jhtml", 130, 18, true);

        writeLiteral("data from variant:");

        endContext("Templates/home/index.jhtml", 130, 18, true);

        beginContext("Templates/home/index.jhtml", 149, 34, false);

        write(             getTemplateData().get("myVariant"));

        endContext("Templates/home/index.jhtml", 149, 34, false);

        beginContext("Templates/home/index.jhtml", 183, 2, true);

        writeLiteral("\r\n");

        endContext("Templates/home/index.jhtml", 183, 2, true);

    }
}
