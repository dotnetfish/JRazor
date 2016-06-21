package com.superstudio.template.mvc;


import com.superstudio.codedom.compiler.CodeDomProvider;
import com.superstudio.codedom.compiler.CodeGeneratorOptions;
import com.superstudio.codedom.compiler.ICodeGenerator;
import com.superstudio.commons.IWebObjectFactory;
import com.superstudio.commons.JavaObjectFactory;
import com.superstudio.commons.TextReader;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.io.File;
import com.superstudio.commons.io.TextWriter;
import com.superstudio.language.java.JavaCodeProvider;
import com.superstudio.template.language.mvc.MvcJavaWebPageRazorHost;
import com.superstudio.template.mvc.context.HostContext;
import com.superstudio.web.razor.GeneratorResults;
import com.superstudio.web.razor.parser.ParserHelpers;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

public class JavaBuildManager {

	private static Map<String, Class> mapper = new HashMap<String, Class>();

	public static IWebObjectFactory getObjectFactory(String virtualPath, boolean b) {
		virtualPath=StringHelper.trimStart(virtualPath, '~', '/');

		String className = ParserHelpers
				.sanitizeClassName("_Page_" + virtualPath);

		return new JavaObjectFactory("JRazor." +className);
	}

	public static Class getCompiledType(String virtualPath) {

		Class clazz = mapper.get(virtualPath);
		if (null == clazz) {
			clazz=compilerPath(virtualPath);
			mapper.put(virtualPath, clazz) ;
		
		}
		return clazz;
	}

	private static Class compilerPath(String virtualPath) {

		return compileTemplate(virtualPath);
	}

	public static Class compileTemplate(String templatePath) {

		try {
			templatePath=StringHelper.trimStart(templatePath, '~', '/');
			String root = HostContext.getCurrent().mapPath("/");
			System.out.println(root);
			String className = ParserHelpers
					.sanitizeClassName("_Page_" + templatePath);

			MvcJavaWebPageRazorHost host = new MvcJavaWebPageRazorHost(templatePath, root);

			TemplateEngine engine = new TemplateEngine(host);

			String templateContent =File.readAll(root+"WEB-INF/" + templatePath);

			TextReader reader = new TextReader(templateContent);
			GeneratorResults result = engine.GenerateCode(reader);

			CodeDomProvider provider = JavaCodeProvider.createProvider("java");
			String codePath = root + "WEB-INF/templates/" + className + ".java";

			TextWriter writer = new TextWriter(codePath, "UTF-8");
			ICodeGenerator generator = provider.createGenerator(writer);
			CodeGeneratorOptions options = new CodeGeneratorOptions();
			options.setBlankLinesBetweenMembers(true);
			// result.getGeneratedCode().getNamespaces().stream().findFirst().
			generator.generateCodeFromCompileUnit(result.getGeneratedCode(), writer, options);
			writer.flush();
			writer.close();
			return renderTemplates(codePath, templatePath);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public static Class renderTemplates(String codePath, String templatePath) throws Exception {

		// FileInputStream writer=new FileInputStream("d:\\test.java");
		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		// URI uri=new URI("file://d:/ _Page_java_cshtml.java");
		StandardJavaFileManager javaFileManager = javaCompiler.getStandardFileManager(null, null, Charset.forName("UTF-8"));
		// 5.文件管理器根与文件连接起来
		/*
		 * String classPath="./jua/WEB-INF/lib/mvc-0.0.1-SNAPSHOT.jar";
		 * javaFileManager.setLocation(StandardLocation.CLASS_PATH,
		 * Arrays.asList( new java.io.File(classPath)));
		 */
		Iterable it = javaFileManager.getJavaFileObjects(new java.io.File(codePath));

		String root = HostContext.getCurrent().mapPath("/");
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

		CompilationTask task = javaCompiler.getTask(null, null, null, options, null, it);
		boolean success = task.call();
		if (!success) {
			System.out.println("编译失败");
		} else {
			System.out.println("编译成功");
		}

		URL[] urls = urlItem;

		List<java.io.File> files = Arrays.asList(tempList);


		String className = ParserHelpers
				.sanitizeClassName("_Page_" + StringHelper.trimStart(templatePath, '~', '/'));
		File.Delete(codePath);
		return Class.forName("JRazor." + className);


	}

	public static Collection getReferencedAssemblies() {
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

		Class clazz=compilerPath(virtualPath);
		try {
			return  (T)clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return  null;
	}
private static   HashMap<String, Class> virtualPathCache=new HashMap<>();
	private static Class getCompiledPath(String virtualPath) {
		 Class clazz=virtualPathCache.get(virtualPath);
		if(clazz==null){
			clazz=compilerPath(virtualPath);
			virtualPathCache.put(virtualPath,clazz);
		}
		return  clazz;
	}

}
