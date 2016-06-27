////------------------------------------------------------------------------------
//// <//<descripts>
////     //<descripts>
////     //<descripts>v1.0.0
////
////     //<descripts>
////     //<descripts>
//// <///<descripts>
////------------------------------------------------------------------------------
//
//package JRazor;
//import com.superstudio.template.templatepages.WebTemplatePage;
//import com.superstudio.commons.Tuple;
//import com.superstudio.demo.controller.StockModel;
//import java.util.ArrayList;
//
//
//public class _Page_Templates_home_performence_jhtml extends WebTemplatePage<ArrayList<com.superstudio.demo.controller.StockModel>> {
//
////#line hidden
//
//    public _Page_Templates_home_performence_jhtml() {
//    }
//
//    @Override
//    public  void execute() {
//        beginContext("Templates/home/performence.jhtml", 147, 65, true);
//
//        writeLiteral("<html>\r\n<head>\r\n    <title>StockModel - JRazor</title>\r\n    <meta");
//
//        endContext("Templates/home/performence.jhtml", 147, 65, true);
//
//        beginContext("Templates/home/performence.jhtml", 212, 26, true);
//
//        writeLiteral(" http-equiv=\"Content-Type\"");
//
//        endContext("Templates/home/performence.jhtml", 212, 26, true);
//
//        beginContext("Templates/home/performence.jhtml", 238, 35, true);
//
//        writeLiteral(" content=\"text/html; charset=utf-8\"");
//
//        endContext("Templates/home/performence.jhtml", 238, 35, true);
//
//        beginContext("Templates/home/performence.jhtml", 273, 16, true);
//
//        writeLiteral("/>\r\n\r\n    <style");
//
//        endContext("Templates/home/performence.jhtml", 273, 16, true);
//
//        beginContext("Templates/home/performence.jhtml", 289, 16, true);
//
//        writeLiteral(" type=\"text/css\"");
//
//        endContext("Templates/home/performence.jhtml", 289, 16, true);
//
//        beginContext("Templates/home/performence.jhtml", 305, 709, true);
//
//        writeLiteral(">\r\n        body {\r\n            color: #333333;\r\n            line-height: 150%;\r\n " +
//                "       }\r\n\r\n        td {\r\n            text-align: center;\r\n        }\r\n\r\n        " +
//                "thead {\r\n            font-weight: bold;\r\n            background-color: #C8FBAF;\r" +
//                "\n        }\r\n\r\n        .odd {\r\n            background-color: #F3DEFB;\r\n        }\r" +
//                "\n\r\n        .even {\r\n            background-color: #EFFFF8;\r\n        }\r\n    </sty" +
//                "le>\r\n</head>\r\n<body>\r\n<h1>StockModel - JRazor</h1>\r\n<table>\r\n    <thead>\r\n    <t" +
//                "r>\r\n        <th>#</th>\r\n        <th>id</th>\r\n        <th>code</th>\r\n        <th>" +
//                "name</th>\r\n        <th>price</th>\r\n        <th>range</th>\r\n        <th>amount</t" +
//                "h>\r\n        <th>gravity</th>\r\n    </tr>\r\n    </thead>\r\n    <tbody>\r\n");
//
//        endContext("Templates/home/performence.jhtml", 305, 709, true);
//
//
//
//        int index=0;
//
//        beginContext("Templates/home/performence.jhtml", 1045, 2, true);
//
//        writeLiteral("\r\n");
//
//        endContext("Templates/home/performence.jhtml", 1045, 2, true);
//
//
//        for(StockModel item : getModel()){
//
//
//            beginContext("Templates/home/performence.jhtml", 1090, 9, true);
//
//            writeLiteral("      <tr");
//
//            endContext("Templates/home/performence.jhtml", 1090, 9, true);
//
//            writeAttribute("class",
//                    Tuple.create(" class=\"", 1099),
//                    Tuple.create("\"", 1133)
//                    , Tuple.create(Tuple.create("", 1107),
//                            Tuple.<Object, Integer>create(index%2==0?"odd":"even"
//                            , 1107), false)
//            );
//
//            beginContext("Templates/home/performence.jhtml", 1134, 19, true);
//
//            writeLiteral(">\r\n            <td>");
//
//            endContext("Templates/home/performence.jhtml", 1134, 19, true);
//
//            beginContext("Templates/home/performence.jhtml", 1154, 5, false);
//
//            write(           index);
//
//            endContext("Templates/home/performence.jhtml", 1154, 5, false);
//
//            beginContext("Templates/home/performence.jhtml", 1159, 23, true);
//
//            writeLiteral("</td>\r\n            <td>");
//
//            endContext("Templates/home/performence.jhtml", 1159, 23, true);
//
//            beginContext("Templates/home/performence.jhtml", 1183, 12, false);
//
//            write(           item.getId());
//
//            endContext("Templates/home/performence.jhtml", 1183, 12, false);
//
//            beginContext("Templates/home/performence.jhtml", 1195, 23, true);
//
//            writeLiteral("</td>\r\n            <td>");
//
//            endContext("Templates/home/performence.jhtml", 1195, 23, true);
//
//            beginContext("Templates/home/performence.jhtml", 1219, 14, false);
//
//            write(           item.getCode());
//
//            endContext("Templates/home/performence.jhtml", 1219, 14, false);
//
//            beginContext("Templates/home/performence.jhtml", 1233, 22, true);
//
//            writeLiteral("</td>\r\n            <td");
//
//            endContext("Templates/home/performence.jhtml", 1233, 22, true);
//
//            beginContext("Templates/home/performence.jhtml", 1255, 26, true);
//
//            writeLiteral(" style=\"text-align: left;\"");
//
//            endContext("Templates/home/performence.jhtml", 1255, 26, true);
//
//            beginContext("Templates/home/performence.jhtml", 1281, 1, true);
//
//            writeLiteral(">");
//
//            endContext("Templates/home/performence.jhtml", 1281, 1, true);
//
//            beginContext("Templates/home/performence.jhtml", 1283, 14, false);
//
//            write(                                     item.getName());
//
//            endContext("Templates/home/performence.jhtml", 1283, 14, false);
//
//            beginContext("Templates/home/performence.jhtml", 1297, 23, true);
//
//            writeLiteral("</td>\r\n            <td>");
//
//            endContext("Templates/home/performence.jhtml", 1297, 23, true);
//
//            beginContext("Templates/home/performence.jhtml", 1321, 15, false);
//
//            write(           item.getPrice());
//
//            endContext("Templates/home/performence.jhtml", 1321, 15, false);
//
//            beginContext("Templates/home/performence.jhtml", 1336, 22, true);
//
//            writeLiteral("</td>\r\n            <td");
//
//            endContext("Templates/home/performence.jhtml", 1336, 22, true);
//
//            writeAttribute("style", Tuple.create(" style=\"", 1358), Tuple.create("\"", 1409)
//                    , Tuple.create(Tuple.create("", 1366), Tuple.create("color:", 1366), true)
//                    , Tuple.create(Tuple.create(" ", 1372), Tuple.<Object, Integer>create(item.getRange()>=10?"red":"blue"
//                            , 1373), false)
//                    , Tuple.create(Tuple.create("", 1408), Tuple.create(";", 1408), true)
//            );
//
//            beginContext("Templates/home/performence.jhtml", 1410, 1, true);
//
//            writeLiteral(">");
//
//            endContext("Templates/home/performence.jhtml", 1410, 1, true);
//
//            beginContext("Templates/home/performence.jhtml", 1413, 15, false);
//
//            write(                                                                item.getRange());
//
//            endContext("Templates/home/performence.jhtml", 1413, 15, false);
//
//            beginContext("Templates/home/performence.jhtml", 1429, 24, true);
//
//            writeLiteral("%</td>\r\n            <td>");
//
//            endContext("Templates/home/performence.jhtml", 1429, 24, true);
//
//            beginContext("Templates/home/performence.jhtml", 1455, 16, false);
//
//            write(            item.getAmount());
//
//            endContext("Templates/home/performence.jhtml", 1455, 16, false);
//
//            beginContext("Templates/home/performence.jhtml", 1472, 7, true);
//
//            writeLiteral("</td>\r\n");
//
//            endContext("Templates/home/performence.jhtml", 1472, 7, true);
//
//
//            if(item.gravity >= 20) {
//
//                beginContext("Templates/home/performence.jhtml", 1518, 15, true);
//
//                writeLiteral("            <td");
//
//                endContext("Templates/home/performence.jhtml", 1518, 15, true);
//
//                beginContext("Templates/home/performence.jhtml", 1533, 20, true);
//
//                writeLiteral(" style=\"color: red;\"");
//
//                endContext("Templates/home/performence.jhtml", 1533, 20, true);
//
//                beginContext("Templates/home/performence.jhtml", 1553, 1, true);
//
//                writeLiteral(">");
//
//                endContext("Templates/home/performence.jhtml", 1553, 1, true);
//
//                beginContext("Templates/home/performence.jhtml", 1556, 17, false);
//
//                write(                                item.getGravity());
//
//                endContext("Templates/home/performence.jhtml", 1556, 17, false);
//
//                beginContext("Templates/home/performence.jhtml", 1574, 8, true);
//
//                writeLiteral("%</td>\r\n");
//
//                endContext("Templates/home/performence.jhtml", 1574, 8, true);
//
//            } else {
//
//                beginContext("Templates/home/performence.jhtml", 1603, 15, true);
//
//                writeLiteral("            <td");
//
//                endContext("Templates/home/performence.jhtml", 1603, 15, true);
//
//                beginContext("Templates/home/performence.jhtml", 1618, 21, true);
//
//                writeLiteral(" style=\"color: blue;\"");
//
//                endContext("Templates/home/performence.jhtml", 1618, 21, true);
//
//                beginContext("Templates/home/performence.jhtml", 1639, 1, true);
//
//                writeLiteral(">");
//
//                endContext("Templates/home/performence.jhtml", 1639, 1, true);
//
//                beginContext("Templates/home/performence.jhtml", 1642, 17, false);
//
//                write(                                 item.getGravity());
//
//                endContext("Templates/home/performence.jhtml", 1642, 17, false);
//
//                beginContext("Templates/home/performence.jhtml", 1660, 8, true);
//
//                writeLiteral("%</td>\r\n");
//
//                endContext("Templates/home/performence.jhtml", 1660, 8, true);
//
//            }
//
//            beginContext("Templates/home/performence.jhtml", 1683, 15, true);
//
//            writeLiteral("        </tr>\r\n");
//
//            endContext("Templates/home/performence.jhtml", 1683, 15, true);
//
//            index++;
//        }
//
//        beginContext("Templates/home/performence.jhtml", 1723, 40, true);
//
//        writeLiteral("    </tbody>\r\n</table>\r\n</body>\r\n</html>");
//
//        endContext("Templates/home/performence.jhtml", 1723, 40, true);
//
//    }
//}
