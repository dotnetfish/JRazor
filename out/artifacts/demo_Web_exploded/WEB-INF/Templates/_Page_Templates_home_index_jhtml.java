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

        
       setLayout("~/Templates/shared/_layout.jhtml");
      String  title="test page";
  
        beginContext("Templates/home/index.jhtml", 222, 4, true);

        writeLiteral("\r\n\r\n");

        endContext("Templates/home/index.jhtml", 222, 4, true);

        defineSection("scripts", () -> {

        beginContext("Templates/home/index.jhtml", 243, 11, true);

        writeLiteral("\r\n  <script");

        endContext("Templates/home/index.jhtml", 243, 11, true);

        beginContext("Templates/home/index.jhtml", 254, 23, true);

        writeLiteral(" type=\"text/javascript\"");

        endContext("Templates/home/index.jhtml", 254, 23, true);

        beginContext("Templates/home/index.jhtml", 277, 69, true);

        writeLiteral(">\r\n  function sayHello(){\r\n alert(\"hello world\");\r\n  }\r\n  </script>\r\n");

        endContext("Templates/home/index.jhtml", 277, 69, true);

        beginContext("Templates/home/index.jhtml", 349, 5, true);

        writeLiteral("<font");

        endContext("Templates/home/index.jhtml", 349, 5, true);

        beginContext("Templates/home/index.jhtml", 354, 18, true);

        writeLiteral(" style=\"color:red\"");

        endContext("Templates/home/index.jhtml", 354, 18, true);

        beginContext("Templates/home/index.jhtml", 372, 76, true);

        writeLiteral(">this is from templates/index.jhtml  getViewBag.get(\"variantName\") </font>\r\n");

        endContext("Templates/home/index.jhtml", 372, 76, true);

        
	String name="superstudio89";
	int lineCount=10;
	Date date = new Date();

  
        beginContext("Templates/home/index.jhtml", 532, 25, true);

        writeLiteral("<br/>\r\ndata from variant:");

        endContext("Templates/home/index.jhtml", 532, 25, true);

        beginContext("Templates/home/index.jhtml", 558, 34, false);

        write(             getTemplateData().get("myVariant"));

        endContext("Templates/home/index.jhtml", 558, 34, false);

        beginContext("Templates/home/index.jhtml", 592, 126, true);

        writeLiteral("\r\n\r\n<br/>\r\n--------------------------------------------------------<br/>\r\naccess " +
"data with get :get(\"myVariant\")<br/>\r\nresult:");

        endContext("Templates/home/index.jhtml", 592, 126, true);

        beginContext("Templates/home/index.jhtml", 719, 16, false);

        write(  get("myVariant"));

        endContext("Templates/home/index.jhtml", 719, 16, false);

        beginContext("Templates/home/index.jhtml", 735, 205, true);

        writeLiteral("\r\n<br/>-----------------------------------------------------\r\n<br/>\r\n<br/>\r\n-----" +
"---------------------------------------------------<br/>\r\naccess model data with" +
" model :model().getName()<br/>\r\nresult:Name:");

        endContext("Templates/home/index.jhtml", 735, 205, true);

        beginContext("Templates/home/index.jhtml", 942, 17, false);

        write(        model().getName());

        endContext("Templates/home/index.jhtml", 942, 17, false);

        beginContext("Templates/home/index.jhtml", 960, 11, true);

        writeLiteral("\r\n<br/>Age:");

        endContext("Templates/home/index.jhtml", 960, 11, true);

        beginContext("Templates/home/index.jhtml", 973, 16, false);

        write(     model().getAge());

        endContext("Templates/home/index.jhtml", 973, 16, false);

        beginContext("Templates/home/index.jhtml", 990, 14, true);

        writeLiteral("\r\n<br/>Gender:");

        endContext("Templates/home/index.jhtml", 990, 14, true);

        beginContext("Templates/home/index.jhtml", 1006, 19, false);

        write(        model().getGender());

        endContext("Templates/home/index.jhtml", 1006, 19, false);

        beginContext("Templates/home/index.jhtml", 1026, 19, true);

        writeLiteral("\r\n<br/>Father Name:");

        endContext("Templates/home/index.jhtml", 1026, 19, true);

        beginContext("Templates/home/index.jhtml", 1047, 29, false);

        write(             model().getFather().getName());

        endContext("Templates/home/index.jhtml", 1047, 29, false);

        beginContext("Templates/home/index.jhtml", 1077, 69, true);

        writeLiteral("\r\n<br/>-----------------------------------------------------\r\n<br/>\r\n");

        endContext("Templates/home/index.jhtml", 1077, 69, true);

        beginContext("Templates/home/index.jhtml", 1147, 4, false);

        write(date);

        endContext("Templates/home/index.jhtml", 1147, 4, false);

        beginContext("Templates/home/index.jhtml", 1151, 9, true);

        writeLiteral("\r\n<br/>\r\n");

        endContext("Templates/home/index.jhtml", 1151, 9, true);

        beginContext("Templates/home/index.jhtml", 1161, 4, false);

        write(name);

        endContext("Templates/home/index.jhtml", 1161, 4, false);

        beginContext("Templates/home/index.jhtml", 1165, 8, true);

        writeLiteral("\r\n<ul>\r\n");

        endContext("Templates/home/index.jhtml", 1165, 8, true);

        while(lineCount>0){
	lineCount--;
 
        beginContext("Templates/home/index.jhtml", 1210, 5, true);

        writeLiteral("\t<li>");

        endContext("Templates/home/index.jhtml", 1210, 5, true);

        beginContext("Templates/home/index.jhtml", 1216, 9, false);

        write(   lineCount);

        endContext("Templates/home/index.jhtml", 1216, 9, false);

        beginContext("Templates/home/index.jhtml", 1225, 7, true);

        writeLiteral("</li>\r\n");

        endContext("Templates/home/index.jhtml", 1225, 7, true);

        }

        beginContext("Templates/home/index.jhtml", 1235, 13, true);

        writeLiteral("</ul>\r\n<input");

        endContext("Templates/home/index.jhtml", 1235, 13, true);

        beginContext("Templates/home/index.jhtml", 1248, 14, true);

        writeLiteral(" type=\"button\"");

        endContext("Templates/home/index.jhtml", 1248, 14, true);

        beginContext("Templates/home/index.jhtml", 1262, 17, true);

        writeLiteral(" value=\"sayHello\"");

        endContext("Templates/home/index.jhtml", 1262, 17, true);

        beginContext("Templates/home/index.jhtml", 1279, 22, true);

        writeLiteral(" onclick=\"sayHello();\"");

        endContext("Templates/home/index.jhtml", 1279, 22, true);

        beginContext("Templates/home/index.jhtml", 1301, 2, true);

        writeLiteral("/>");

        endContext("Templates/home/index.jhtml", 1301, 2, true);

        });

    }
}
