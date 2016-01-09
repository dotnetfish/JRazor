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


public class _Page_Templates_shared__layout_jhtml extends WebTemplatePage {
    
//#line hidden
    
    public _Page_Templates_shared__layout_jhtml() {
    }
    
    @Override 
    public  void execute() {
        beginContext("Templates/shared/_layout.jhtml", 0, 5, true);

        writeLiteral("<html");

        endContext("Templates/shared/_layout.jhtml", 0, 5, true);

        beginContext("Templates/shared/_layout.jhtml", 5, 10, true);

        writeLiteral(" lang=\"en\"");

        endContext("Templates/shared/_layout.jhtml", 5, 10, true);

        beginContext("Templates/shared/_layout.jhtml", 15, 20, true);

        writeLiteral(">\r\n<head>\r\n    <meta");

        endContext("Templates/shared/_layout.jhtml", 15, 20, true);

        beginContext("Templates/shared/_layout.jhtml", 35, 16, true);

        writeLiteral(" charset=\"UTF-8\"");

        endContext("Templates/shared/_layout.jhtml", 35, 16, true);

        beginContext("Templates/shared/_layout.jhtml", 51, 59, true);

        writeLiteral(">\r\n    <title>title</title>\r\n</head>\r\n<body>\r\n这是中文，会乱码 么？\r\n");

        endContext("Templates/shared/_layout.jhtml", 51, 59, true);

        beginContext("Templates/shared/_layout.jhtml", 111, 12, false);

        write(renderBody());

        endContext("Templates/shared/_layout.jhtml", 111, 12, false);

        beginContext("Templates/shared/_layout.jhtml", 123, 2, true);

        writeLiteral("\r\n");

        endContext("Templates/shared/_layout.jhtml", 123, 2, true);

        beginContext("Templates/shared/_layout.jhtml", 126, 30, false);

        write(renderSection("scripts",false));

        endContext("Templates/shared/_layout.jhtml", 126, 30, false);

        beginContext("Templates/shared/_layout.jhtml", 156, 18, true);

        writeLiteral("\r\n</body>\r\n</html>");

        endContext("Templates/shared/_layout.jhtml", 156, 18, true);

    }
}
