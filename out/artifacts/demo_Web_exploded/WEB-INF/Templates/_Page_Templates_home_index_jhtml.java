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


public class _Page_Templates_home_index_jhtml extends WebTemplatePage {
    
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
  
        beginContext("Templates/home/index.jhtml", 120, 4, true);

        writeLiteral("\r\n\r\n");

        endContext("Templates/home/index.jhtml", 120, 4, true);

        defineSection("scripts", () -> {

        beginContext("Templates/home/index.jhtml", 141, 11, true);

        writeLiteral("\r\n  <script");

        endContext("Templates/home/index.jhtml", 141, 11, true);

        beginContext("Templates/home/index.jhtml", 152, 23, true);

        writeLiteral(" type=\"text/javascript\"");

        endContext("Templates/home/index.jhtml", 152, 23, true);

        beginContext("Templates/home/index.jhtml", 175, 47, true);

        writeLiteral(">\r\n  function sayHello(){\r\n\r\n  }\r\n  </script>\r\n");

        endContext("Templates/home/index.jhtml", 175, 47, true);

        });

        beginContext("Templates/home/index.jhtml", 225, 5, true);

        writeLiteral("<font");

        endContext("Templates/home/index.jhtml", 225, 5, true);

        beginContext("Templates/home/index.jhtml", 230, 18, true);

        writeLiteral(" style=\"color:red\"");

        endContext("Templates/home/index.jhtml", 230, 18, true);

        beginContext("Templates/home/index.jhtml", 248, 76, true);

        writeLiteral(">this is from templates/index.jhtml  getViewBag.get(\"variantName\") </font>\r\n");

        endContext("Templates/home/index.jhtml", 248, 76, true);

        
	String name="superstudio89";
	int lineCount=10;
	Date date = new Date();

  
        beginContext("Templates/home/index.jhtml", 408, 25, true);

        writeLiteral("<br/>\r\ndata from variant:");

        endContext("Templates/home/index.jhtml", 408, 25, true);

        beginContext("Templates/home/index.jhtml", 434, 34, false);

        write(             getTemplateData().get("myVariant"));

        endContext("Templates/home/index.jhtml", 434, 34, false);

        beginContext("Templates/home/index.jhtml", 468, 11, true);

        writeLiteral("\r\n\r\n<br/>\r\n");

        endContext("Templates/home/index.jhtml", 468, 11, true);

        beginContext("Templates/home/index.jhtml", 480, 4, false);

        write(date);

        endContext("Templates/home/index.jhtml", 480, 4, false);

        beginContext("Templates/home/index.jhtml", 484, 9, true);

        writeLiteral("\r\n<br/>\r\n");

        endContext("Templates/home/index.jhtml", 484, 9, true);

        beginContext("Templates/home/index.jhtml", 494, 4, false);

        write(name);

        endContext("Templates/home/index.jhtml", 494, 4, false);

        beginContext("Templates/home/index.jhtml", 498, 8, true);

        writeLiteral("\r\n<ul>\r\n");

        endContext("Templates/home/index.jhtml", 498, 8, true);

        while(lineCount>0){
	lineCount--;
 
        beginContext("Templates/home/index.jhtml", 543, 5, true);

        writeLiteral("\t<li>");

        endContext("Templates/home/index.jhtml", 543, 5, true);

        beginContext("Templates/home/index.jhtml", 549, 9, false);

        write(   lineCount);

        endContext("Templates/home/index.jhtml", 549, 9, false);

        beginContext("Templates/home/index.jhtml", 558, 7, true);

        writeLiteral("</li>\r\n");

        endContext("Templates/home/index.jhtml", 558, 7, true);

        }

        beginContext("Templates/home/index.jhtml", 568, 13, true);

        writeLiteral("</ul>\r\n<input");

        endContext("Templates/home/index.jhtml", 568, 13, true);

        beginContext("Templates/home/index.jhtml", 581, 14, true);

        writeLiteral(" type=\"button\"");

        endContext("Templates/home/index.jhtml", 581, 14, true);

        beginContext("Templates/home/index.jhtml", 595, 17, true);

        writeLiteral(" value=\"sayHello\"");

        endContext("Templates/home/index.jhtml", 595, 17, true);

        beginContext("Templates/home/index.jhtml", 612, 22, true);

        writeLiteral(" onclick=\"sayHello();\"");

        endContext("Templates/home/index.jhtml", 612, 22, true);

        beginContext("Templates/home/index.jhtml", 634, 2, true);

        writeLiteral("/>");

        endContext("Templates/home/index.jhtml", 634, 2, true);

    }
}
