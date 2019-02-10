package com.superstudio.template.mvc;


import com.superstudio.codedom.compiler.CodeDomProvider;
import com.superstudio.codedom.compiler.CodeGeneratorOptions;
import com.superstudio.codedom.compiler.ICodeGenerator;
import com.superstudio.commons.IWebObjectFactory;
import com.superstudio.commons.JavaObjectFactory;
import com.superstudio.commons.TextReader;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.io.TextWriter;
import com.superstudio.language.java.JavaCodeProvider;
import com.superstudio.template.language.mvc.MvcJavaWebPageRazorHost;
import com.superstudio.template.mvc.context.HostContext;
import com.superstudio.web.razor.GeneratorResults;
import com.superstudio.web.razor.parser.ParserHelpers;

import javax.tools.SimpleJavaFileObject;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class JavaBuildManager {

	private static ConcurrentHashMap<String, Class> mapper = new ConcurrentHashMap<>();

	public static IWebObjectFactory getObjectFactory(String virtualPath, boolean b) {
		virtualPath=StringHelper.trimStart(virtualPath, '~', '/');

		String className = ParserHelpers
				.sanitizeClassName("_Page_" + virtualPath);

		return new JavaObjectFactory("JRazor." +className);
	}

	public static Class getCompiledType(String virtualPath) throws Exception {

/**/		Class clazz = mapper.get(virtualPath);
		if (null == clazz) {
			clazz=compilerPath(virtualPath);
			mapper.put(virtualPath, clazz) ;
		
		}
		return clazz;
	}

	private static Class compilerPath(String virtualPath) throws Exception {

		return compileTemplate(virtualPath);
	}

	public static Class compileTemplate(String templatePath) throws Exception {


			templatePath=StringHelper.trimStart(templatePath, '~', '/');
			String root = HostContext.getCurrent().mapPath("/");

			String className = ParserHelpers
					.sanitizeClassName("_Page_" + templatePath);

			MvcJavaWebPageRazorHost host = new MvcJavaWebPageRazorHost(templatePath, root);

			TemplateEngine engine = new TemplateEngine(host);
		String templateContent=org.apache.commons.io.FileUtils.readFileToString(new java.io.File(root+"WEB-INF/" + templatePath),"utf-8");
			//String templateContent =File.readAll(root+"WEB-INF/" + templatePath);

			TextReader reader = new TextReader(templateContent);
			GeneratorResults result = engine.GenerateCode(reader);

			CodeDomProvider provider = JavaCodeProvider.createProvider("java");
			String codePath = root + "WEB-INF/Templates/" + className + "_bak_.java";

			TextWriter writer = new TextWriter(codePath, "UTF-8");
			ICodeGenerator generator = provider.createGenerator(writer);
			CodeGeneratorOptions options = new CodeGeneratorOptions();
			options.setBlankLinesBetweenMembers(true);
			// result.getGeneratedCode().getNamespaces().stream().findFirst().
			generator.generateCodeFromCompileUnit(result.getGeneratedCode(), writer, options);
			writer.flush();
			writer.close();
        JavaBuilder builder = JavaBuilder.getInstance();
        Class clazz=builder.compilePath(root + "WEB-INF/Templates/" + className + ".java",templatePath);
        return  clazz;

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

	public static <T> T createInstanceFromVirtualPath(String virtualPath) throws Exception {

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
	private static Class getCompiledPath(String virtualPath) throws Exception {
		 Class clazz=virtualPathCache.get(virtualPath);
		if(clazz==null){
			clazz=compilerPath(virtualPath);
			virtualPathCache.put(virtualPath,clazz);
		}
		return  clazz;
	}

	static class  StringJavaFileObject extends SimpleJavaFileObject {

		final String code;

		StringJavaFileObject(String className, String code) {
			super(URI.create(className + Kind.SOURCE.extension), Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return code;
		}
	}

}
