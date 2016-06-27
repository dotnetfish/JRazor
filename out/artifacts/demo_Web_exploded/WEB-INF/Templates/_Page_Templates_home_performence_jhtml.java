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
import com.superstudio.commons.Tuple;
import com.superstudio.demo.controller.StockModel;
import java.util.ArrayList;


public class _Page_Templates_home_performence_jhtml extends WebTemplatePage<ArrayList<com.superstudio.demo.controller.StockModel>> {
    
//#line hidden
    
    public _Page_Templates_home_performence_jhtml() {
    }
    
    @Override 
    public  void execute() {
        beginContext("Templates/home/performence.jhtml", 147, 65, true);

        writeLiteral("<html>\r\n<head>\r\n    <title>StockModel - JRazor</title>\r\n    <meta");

        endContext("Templates/home/performence.jhtml", 147, 65, true);

        beginContext("Templates/home/performence.jhtml", 212, 26, true);

        writeLiteral(" http-equiv=\"Content-Type\"");

        endContext("Templates/home/performence.jhtml", 212, 26, true);

        beginContext("Templates/home/performence.jhtml", 238, 35, true);

        writeLiteral(" content=\"text/html; charset=utf-8\"");

        endContext("Templates/home/performence.jhtml", 238, 35, true);

        beginContext("Templates/home/performence.jhtml", 273, 16, true);

        writeLiteral("/>\r\n\r\n    <style");

        endContext("Templates/home/performence.jhtml", 273, 16, true);

        beginContext("Templates/home/performence.jhtml", 289, 16, true);

        writeLiteral(" type=\"text/css\"");

        endContext("Templates/home/performence.jhtml", 289, 16, true);

        beginContext("Templates/home/performence.jhtml", 305, 709, true);

        writeLiteral(">\r\n        body {\r\n            color: #333333;\r\n            line-height: 150%;\r\n " +
"       }\r\n\r\n        td {\r\n            text-align: center;\r\n        }\r\n\r\n        " +
"thead {\r\n            font-weight: bold;\r\n            background-color: #C8FBAF;\r" +
"\n        }\r\n\r\n        .odd {\r\n            background-color: #F3DEFB;\r\n        }\r" +
"\n\r\n        .even {\r\n            background-color: #EFFFF8;\r\n        }\r\n    </sty" +
"le>\r\n</head>\r\n<body>\r\n<h1>StockModel - JRazor</h1>\r\n<table>\r\n    <thead>\r\n    <t" +
"r>\r\n        <th>#</th>\r\n        <th>id</th>\r\n        <th>code</th>\r\n        <th>" +
"name</th>\r\n        <th>price</th>\r\n        <th>range</th>\r\n        <th>amount</t" +
"h>\r\n        <th>gravity</th>\r\n    </tr>\r\n    </thead>\r\n    <tbody>\r\n");

        endContext("Templates/home/performence.jhtml", 305, 709, true);

            
        
    int index=0;
    String style="even";
          
        beginContext("Templates/home/performence.jhtml", 1071, 2, true);

        writeLiteral("\r\n");

        endContext("Templates/home/performence.jhtml", 1071, 2, true);

            
        for(StockModel item : getModel()){


     
        beginContext("Templates/home/performence.jhtml", 1118, 20, true);

        writeLiteral("                 <tr");

        endContext("Templates/home/performence.jhtml", 1118, 20, true);

        beginContext("Templates/home/performence.jhtml", 1138, 13, true);

        writeLiteral(" class=\"even\"");

        endContext("Templates/home/performence.jhtml", 1138, 13, true);

        beginContext("Templates/home/performence.jhtml", 1151, 19, true);

        writeLiteral(">\r\n            <td>");

        endContext("Templates/home/performence.jhtml", 1151, 19, true);

        beginContext("Templates/home/performence.jhtml", 1171, 5, false);

        write(           index);

        endContext("Templates/home/performence.jhtml", 1171, 5, false);

        beginContext("Templates/home/performence.jhtml", 1176, 23, true);

        writeLiteral("</td>\r\n            <td>");

        endContext("Templates/home/performence.jhtml", 1176, 23, true);

        beginContext("Templates/home/performence.jhtml", 1200, 12, false);

        write(           item.getId());

        endContext("Templates/home/performence.jhtml", 1200, 12, false);

        beginContext("Templates/home/performence.jhtml", 1212, 23, true);

        writeLiteral("</td>\r\n            <td>");

        endContext("Templates/home/performence.jhtml", 1212, 23, true);

        beginContext("Templates/home/performence.jhtml", 1236, 14, false);

        write(           item.getCode());

        endContext("Templates/home/performence.jhtml", 1236, 14, false);

        beginContext("Templates/home/performence.jhtml", 1250, 22, true);

        writeLiteral("</td>\r\n            <td");

        endContext("Templates/home/performence.jhtml", 1250, 22, true);

        beginContext("Templates/home/performence.jhtml", 1272, 26, true);

        writeLiteral(" style=\"text-align: left;\"");

        endContext("Templates/home/performence.jhtml", 1272, 26, true);

        beginContext("Templates/home/performence.jhtml", 1298, 1, true);

        writeLiteral(">");

        endContext("Templates/home/performence.jhtml", 1298, 1, true);

        beginContext("Templates/home/performence.jhtml", 1300, 14, false);

        write(                                     item.getName());

        endContext("Templates/home/performence.jhtml", 1300, 14, false);

        beginContext("Templates/home/performence.jhtml", 1314, 23, true);

        writeLiteral("</td>\r\n            <td>");

        endContext("Templates/home/performence.jhtml", 1314, 23, true);

        beginContext("Templates/home/performence.jhtml", 1338, 15, false);

        write(           item.getPrice());

        endContext("Templates/home/performence.jhtml", 1338, 15, false);

        beginContext("Templates/home/performence.jhtml", 1353, 24, true);

        writeLiteral("</td>\r\n            <td >");

        endContext("Templates/home/performence.jhtml", 1353, 24, true);

        beginContext("Templates/home/performence.jhtml", 1379, 15, false);

        write(             item.getRange());

        endContext("Templates/home/performence.jhtml", 1379, 15, false);

        beginContext("Templates/home/performence.jhtml", 1395, 24, true);

        writeLiteral("%</td>\r\n            <td>");

        endContext("Templates/home/performence.jhtml", 1395, 24, true);

        beginContext("Templates/home/performence.jhtml", 1421, 16, false);

        write(            item.getAmount());

        endContext("Templates/home/performence.jhtml", 1421, 16, false);

        beginContext("Templates/home/performence.jhtml", 1438, 7, true);

        writeLiteral("</td>\r\n");

        endContext("Templates/home/performence.jhtml", 1438, 7, true);

                    
        if(item.getGravity() >= 20) {
             
        beginContext("Templates/home/performence.jhtml", 1489, 15, true);

        writeLiteral("            <td");

        endContext("Templates/home/performence.jhtml", 1489, 15, true);

        beginContext("Templates/home/performence.jhtml", 1504, 20, true);

        writeLiteral(" style=\"color: red;\"");

        endContext("Templates/home/performence.jhtml", 1504, 20, true);

        beginContext("Templates/home/performence.jhtml", 1524, 1, true);

        writeLiteral(">");

        endContext("Templates/home/performence.jhtml", 1524, 1, true);

        beginContext("Templates/home/performence.jhtml", 1527, 17, false);

        write(                                item.getGravity());

        endContext("Templates/home/performence.jhtml", 1527, 17, false);

        beginContext("Templates/home/performence.jhtml", 1545, 8, true);

        writeLiteral("%</td>\r\n");

        endContext("Templates/home/performence.jhtml", 1545, 8, true);

                   } else {

        beginContext("Templates/home/performence.jhtml", 1574, 15, true);

        writeLiteral("            <td");

        endContext("Templates/home/performence.jhtml", 1574, 15, true);

        beginContext("Templates/home/performence.jhtml", 1589, 21, true);

        writeLiteral(" style=\"color: blue;\"");

        endContext("Templates/home/performence.jhtml", 1589, 21, true);

        beginContext("Templates/home/performence.jhtml", 1610, 1, true);

        writeLiteral(">");

        endContext("Templates/home/performence.jhtml", 1610, 1, true);

        beginContext("Templates/home/performence.jhtml", 1613, 17, false);

        write(                                 item.getGravity());

        endContext("Templates/home/performence.jhtml", 1613, 17, false);

        beginContext("Templates/home/performence.jhtml", 1631, 8, true);

        writeLiteral("%</td>\r\n");

        endContext("Templates/home/performence.jhtml", 1631, 8, true);

                    }

        beginContext("Templates/home/performence.jhtml", 1654, 15, true);

        writeLiteral("        </tr>\r\n");

        endContext("Templates/home/performence.jhtml", 1654, 15, true);

                index++;
    }

        beginContext("Templates/home/performence.jhtml", 1694, 40, true);

        writeLiteral("    </tbody>\r\n</table>\r\n</body>\r\n</html>");

        endContext("Templates/home/performence.jhtml", 1694, 40, true);

    }
}
