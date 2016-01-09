package com.superstudio.jrazor.template;

/**
 * Created by Chaoqun on 2015/11/8.
 */

import com.superstudio.codedom.compiler.CodeDomProvider;
import com.superstudio.codedom.compiler.CodeGeneratorOptions;
import com.superstudio.codedom.compiler.ICodeGenerator;
import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.IWebObjectFactory;
import com.superstudio.commons.JavaObjectFactory;
import com.superstudio.commons.TextReader;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.io.File;
import com.superstudio.commons.io.TextWriter;
import com.superstudio.jrazor.GeneratorResults;
import com.superstudio.jrazor.parser.ParserHelpers;
import com.superstudio.language.java.JavaCodeProvider;
import com.superstudio.language.java.mvc.MvcJavaWebPageRazorHost;
import org.apache.logging.log4j.Logger;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;



public class JavaBuildManager {

    private static Map<String, Class> mapper = new HashMap<String, Class>();
     private static Logger log =org.apache.logging.log4j.LogManager.getLogger(JavaBuildManager.class);
    // LoggerFactory.getLogger(J);

    public static IWebObjectFactory getObjectFactory(String virtualPath, boolean b) {

        String className = virtualPath;
        return new JavaObjectFactory(className);
    }

    public static Class getCompiledType(String virtualPath) {
        // TODO Auto-generated method stub
        Class clazz = mapper.get(virtualPath);
        if (null == clazz) {
            clazz = compilePath(virtualPath);
            mapper.put(virtualPath, clazz);

        }
        return clazz;
    }

    private static Class compilePath(String virtualPath) {
        // TODO Auto-generated method stub
        return compileTemplate(virtualPath);
    }

    public static Class compileTemplate(String templatePath) {

        try {
            // templatePath = StringHelper.trimStart(templatePath, new
            // Character[] { '~', '/' });
            // String root =
            // HttpContext.getCurrent().getRequest().getRealPath("/");
            String className = ParserHelpers.sanitizeClassName("_Page_" + templatePath);

            MvcJavaWebPageRazorHost host = new MvcJavaWebPageRazorHost(templatePath, root);

            RazorTemplateEngine engine = new RazorTemplateEngine(host);

            byte[] stream = File.ReadAllBytes(root + "WEB-INF/" + templatePath);
            // byte[] stream =VirtualPathProvider.OpenFile(templatePath);
            String templateContent = new String(stream, "UTF-8");
            TextReader reader = new TextReader(templateContent);
            GeneratorResults result = engine.generateCode(reader);// (reader);
            // host.get
            CodeDomProvider provider = JavaCodeProvider.createProvider("java");
            String codePath = root + "/WEB-INF/views/" + className + ".java";
			/*
			 * if(!Paths.get(codePath).toFile().exists()){
			 *
			 * }
			 */
            TextWriter writer = new TextWriter(codePath, CultureInfo.InvariantCulture);
            ICodeGenerator generator = provider.createGenerator(writer);
            CodeGeneratorOptions options = new CodeGeneratorOptions();
            options.setBlankLinesBetweenMembers(true);
            // result.getGeneratedCode().getNamespaces().stream().findFirst().
            generator.generateCodeFromCompileUnit(result.getGeneratedCode(), writer, options);
            return renderTemplates(codePath, templatePath);
        } catch (Exception e) {

          //  e.printStackTrace();
            log.error("",e);
            return null;
        }

    }

    public static Class renderTemplates(String codePath, String templatePath) throws Exception {

        // FileInputStream writer=new FileInputStream("d:\\test.java");
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        // URI uri=new URI("file://d:/ _Page_java_cshtml.java");
        StandardJavaFileManager javaFileManager = javaCompiler.getStandardFileManager(null, null, null);
        // 5.文件管理器根与文件连接起来
		/*
		 * String classPath="./jua/WEB-INF/lib/mvc-0.0.1-SNAPSHOT.jar";
		 * javaFileManager.setLocation(StandardLocation.CLASS_PATH,
		 * Arrays.asList( new java.io.File(classPath)));
		 */
        Iterable it = javaFileManager.getJavaFileObjects(new java.io.File(codePath));
        // JavaFileObject fileObject = new TestFileObject(uri,
        // JavaFileObject.Kind.SOURCE);
        // String root = HttpContext.getCurrent().getRequest().getRealPath("/");
        java.io.File file = new java.io.File(root + "/WEB-INF/lib");
        java.io.File[] tempList = file.listFiles();
        System.out.println(file.getAbsolutePath());
        String classPaths = "";
        URL[] urlItem = new URL[tempList.length + 1];
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                classPaths += (tempList[i].getAbsolutePath() + ";");
            }
            urlItem[i] = tempList[i].toURL();
        }
        classPaths += root + "/WEB-INF/classes";
        urlItem[tempList.length] = new URL("file:/" + root + "/WEB-INF/classes/");
        Iterable<String> options = Arrays.asList("-classpath", classPaths, "-d", root + "/WEB-INF/classes");

        JavaCompiler.CompilationTask task = javaCompiler.getTask(null, null, null, options, null, it);
        boolean success = task.call();
        if (!success) {
            System.out.println("编译失败");
        } else {
            System.out.println("编译成功");
        }

        URL[] urls = urlItem;
        // URL[] urls = new URL[] { new
        // URL("file:/"+root+"/WEB-INF/classes/"),new
        // URL("file:/"+root+"/WEB-INF/lib/")};
        List<java.io.File> files = Arrays.asList(tempList);

		/*
		 * for(URL url:urls){ System.out.println(url.toString());
		 * System.out.println(url.getFile()); }
		 */
		/* URLClassLoader classLoader = new URLClassLoader(urls); */
        String className = ParserHelpers
                .sanitizeClassName("_Page_" + StringHelper.trimStart(templatePath, '~', '/'));
        File.Delete(codePath);
        // Package pkg=Package.getPackage("JRazor");
        // ClassLoader.getSystemClassLoader().setp
        return Class.forName("JRazor." + className);

		/*
		 * Object targetObj = target.newInstance();
		 *
		 * WebViewPage page = (WebViewPage) target.newInstance(); HttpContext
		 * httpContext = HttpContext.getCurrent();
		 *
		 * Writer writer =
		 * controllerContext.getRequestContext().getResponse().getWriter();
		 * WebPageContext context = new WebPageContext(); ViewContext
		 * viewcontext = new ViewContext(); viewcontext.setWriter(writer);
		 * page.setViewContext(viewcontext);
		 *
		 * HttpContextBase base = new HttpContextBase(httpContext);
		 *
		 * // base. page.setContext(base);
		 *
		 * // context.
		 *
		 * page.ExecutePageHierarchy(context, writer);
		 */

    }

    public static Collection getReferencedPackages() {
        // TODO Auto-generated method stub
        return null;
    }

    public static InputStream readCachedFile(String fileName) {
        // TODO Auto-generated method stub
        return null;
    }

    public static OutputStream createCachedFile(String fileName) {
        // TODO Auto-generated method stub
        return null;
    }

    public static <T> T createInstanceFromVirtualPath(String virtualPath) {
        // TODO Auto-generated method stub
        return null;
    }

}
