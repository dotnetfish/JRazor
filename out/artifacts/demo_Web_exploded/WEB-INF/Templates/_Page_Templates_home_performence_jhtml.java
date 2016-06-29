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
import com.superstudio.template.templatepages.PositionTagged;
import com.superstudio.demo.controller.StockModel;
import java.util.ArrayList;


public class _Page_Templates_home_performence_jhtml extends WebTemplatePage<ArrayList<com.superstudio.demo.controller.StockModel>> {
    
//#line hidden
    
    public _Page_Templates_home_performence_jhtml() {
    }
    
    @Override 
    public  void execute() throws Exception {
        beginContext("Templates/home/performence.jhtml", 145, 82, true);

        writeLiteral("<!DOCTYPE html>\r\n<html>\r\n<head>\r\n    <title>StockModel - JRazor</title>\r\n    <met" +
"a");

        endContext("Templates/home/performence.jhtml", 145, 82, true);

        beginContext("Templates/home/performence.jhtml", 227, 26, true);

        writeLiteral(" http-equiv=\"Content-Type\"");

        endContext("Templates/home/performence.jhtml", 227, 26, true);

        beginContext("Templates/home/performence.jhtml", 253, 35, true);

        writeLiteral(" content=\"text/html; charset=utf-8\"");

        endContext("Templates/home/performence.jhtml", 253, 35, true);

        beginContext("Templates/home/performence.jhtml", 288, 16, true);

        writeLiteral("/>\r\n\r\n    <style");

        endContext("Templates/home/performence.jhtml", 288, 16, true);

        beginContext("Templates/home/performence.jhtml", 304, 16, true);

        writeLiteral(" type=\"text/css\"");

        endContext("Templates/home/performence.jhtml", 304, 16, true);

        beginContext("Templates/home/performence.jhtml", 320, 709, true);

        writeLiteral(">\r\n        body {\r\n            color: #333333;\r\n            line-height: 150%;\r\n " +
"       }\r\n\r\n        td {\r\n            text-align: center;\r\n        }\r\n\r\n        " +
"thead {\r\n            font-weight: bold;\r\n            background-color: #C8FBAF;\r" +
"\n        }\r\n\r\n        .odd {\r\n            background-color: #F3DEFB;\r\n        }\r" +
"\n\r\n        .even {\r\n            background-color: #EFFFF8;\r\n        }\r\n    </sty" +
"le>\r\n</head>\r\n<body>\r\n<h1>StockModel - JRazor</h1>\r\n<table>\r\n    <thead>\r\n    <t" +
"r>\r\n        <th>#</th>\r\n        <th>id</th>\r\n        <th>code</th>\r\n        <th>" +
"name</th>\r\n        <th>price</th>\r\n        <th>range</th>\r\n        <th>amount</t" +
"h>\r\n        <th>gravity</th>\r\n    </tr>\r\n    </thead>\r\n    <tbody>\r\n");

        endContext("Templates/home/performence.jhtml", 320, 709, true);

            
        
    int index=0;
    String style="even";
          
        beginContext("Templates/home/performence.jhtml", 1086, 2, true);

        writeLiteral("\r\n");

        endContext("Templates/home/performence.jhtml", 1086, 2, true);

            
        for(StockModel item : getModel()){


     
        beginContext("Templates/home/performence.jhtml", 1133, 20, true);

        writeLiteral("                 <tr");

        endContext("Templates/home/performence.jhtml", 1133, 20, true);

        writeAttribute("class", new PositionTagged(" class=\"", 1153), new PositionTagged("\"", 1187)
        , toAttributeValue(new PositionTagged("", 1161), new PositionTagged(index%2==0?"odd":"even"
        , 1161), false)
        );

        beginContext("Templates/home/performence.jhtml", 1188, 19, true);

        writeLiteral(">\r\n            <td>");

        endContext("Templates/home/performence.jhtml", 1188, 19, true);

        beginContext("Templates/home/performence.jhtml", 1208, 5, false);

        write(           index);

        endContext("Templates/home/performence.jhtml", 1208, 5, false);

        beginContext("Templates/home/performence.jhtml", 1213, 23, true);

        writeLiteral("</td>\r\n            <td>");

        endContext("Templates/home/performence.jhtml", 1213, 23, true);

        beginContext("Templates/home/performence.jhtml", 1237, 12, false);

        write(           item.getId());

        endContext("Templates/home/performence.jhtml", 1237, 12, false);

        beginContext("Templates/home/performence.jhtml", 1249, 23, true);

        writeLiteral("</td>\r\n            <td>");

        endContext("Templates/home/performence.jhtml", 1249, 23, true);

        beginContext("Templates/home/performence.jhtml", 1273, 14, false);

        write(           item.getCode());

        endContext("Templates/home/performence.jhtml", 1273, 14, false);

        beginContext("Templates/home/performence.jhtml", 1287, 22, true);

        writeLiteral("</td>\r\n            <td");

        endContext("Templates/home/performence.jhtml", 1287, 22, true);

        beginContext("Templates/home/performence.jhtml", 1309, 26, true);

        writeLiteral(" style=\"text-align: left;\"");

        endContext("Templates/home/performence.jhtml", 1309, 26, true);

        beginContext("Templates/home/performence.jhtml", 1335, 1, true);

        writeLiteral(">");

        endContext("Templates/home/performence.jhtml", 1335, 1, true);

        beginContext("Templates/home/performence.jhtml", 1337, 14, false);

        write(                                     item.getName());

        endContext("Templates/home/performence.jhtml", 1337, 14, false);

        beginContext("Templates/home/performence.jhtml", 1351, 23, true);

        writeLiteral("</td>\r\n            <td>");

        endContext("Templates/home/performence.jhtml", 1351, 23, true);

        beginContext("Templates/home/performence.jhtml", 1375, 15, false);

        write(           item.getPrice());

        endContext("Templates/home/performence.jhtml", 1375, 15, false);

        beginContext("Templates/home/performence.jhtml", 1390, 24, true);

        writeLiteral("</td>\r\n            <td >");

        endContext("Templates/home/performence.jhtml", 1390, 24, true);

        beginContext("Templates/home/performence.jhtml", 1416, 15, false);

        write(             item.getRange());

        endContext("Templates/home/performence.jhtml", 1416, 15, false);

        beginContext("Templates/home/performence.jhtml", 1432, 24, true);

        writeLiteral("%</td>\r\n            <td>");

        endContext("Templates/home/performence.jhtml", 1432, 24, true);

        beginContext("Templates/home/performence.jhtml", 1458, 16, false);

        write(            item.getAmount());

        endContext("Templates/home/performence.jhtml", 1458, 16, false);

        beginContext("Templates/home/performence.jhtml", 1475, 7, true);

        writeLiteral("</td>\r\n");

        endContext("Templates/home/performence.jhtml", 1475, 7, true);

                    
        if(item.getGravity() >= 20) {
             
        beginContext("Templates/home/performence.jhtml", 1526, 15, true);

        writeLiteral("            <td");

        endContext("Templates/home/performence.jhtml", 1526, 15, true);

        beginContext("Templates/home/performence.jhtml", 1541, 20, true);

        writeLiteral(" style=\"color: red;\"");

        endContext("Templates/home/performence.jhtml", 1541, 20, true);

        beginContext("Templates/home/performence.jhtml", 1561, 1, true);

        writeLiteral(">");

        endContext("Templates/home/performence.jhtml", 1561, 1, true);

        beginContext("Templates/home/performence.jhtml", 1564, 17, false);

        write(                                item.getGravity());

        endContext("Templates/home/performence.jhtml", 1564, 17, false);

        beginContext("Templates/home/performence.jhtml", 1582, 8, true);

        writeLiteral("%</td>\r\n");

        endContext("Templates/home/performence.jhtml", 1582, 8, true);

                   } else {

        beginContext("Templates/home/performence.jhtml", 1611, 15, true);

        writeLiteral("            <td");

        endContext("Templates/home/performence.jhtml", 1611, 15, true);

        beginContext("Templates/home/performence.jhtml", 1626, 21, true);

        writeLiteral(" style=\"color: blue;\"");

        endContext("Templates/home/performence.jhtml", 1626, 21, true);

        beginContext("Templates/home/performence.jhtml", 1647, 1, true);

        writeLiteral(">");

        endContext("Templates/home/performence.jhtml", 1647, 1, true);

        beginContext("Templates/home/performence.jhtml", 1650, 17, false);

        write(                                 item.getGravity());

        endContext("Templates/home/performence.jhtml", 1650, 17, false);

        beginContext("Templates/home/performence.jhtml", 1668, 8, true);

        writeLiteral("%</td>\r\n");

        endContext("Templates/home/performence.jhtml", 1668, 8, true);

                    }

        beginContext("Templates/home/performence.jhtml", 1691, 15, true);

        writeLiteral("        </tr>\r\n");

        endContext("Templates/home/performence.jhtml", 1691, 15, true);

                index++;
    }

        beginContext("Templates/home/performence.jhtml", 1731, 40, true);

        writeLiteral("    </tbody>\r\n</table>\r\n</body>\r\n</html>");

        endContext("Templates/home/performence.jhtml", 1731, 40, true);

    }
}
