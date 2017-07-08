package com.superstudio.template.mvc;

import com.apple.eio.FileManager;
import com.superstudio.commons.TextReader;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.web.razor.parser.ParserHelpers;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zj-db0720 on 2017/6/18.
 */
public class JavaBuilder {
    private static JavaBuilder ourInstance = new JavaBuilder();

    public static JavaBuilder getInstance() {
        return ourInstance;
    }
    private URLClassLoader parentClassLoader;
    private String classpath;
    private JavaBuilder() {
        this.parentClassLoader = (URLClassLoader) this.getClass().getClassLoader();
        this.buildClassPath();
    }
    private void buildClassPath() {
        this.classpath = null;
        StringBuilder sb = new StringBuilder();
        for (URL url : this.parentClassLoader.getURLs()) {
            String p = url.getFile();
            sb.append(p).append(File.pathSeparator);
        }
        this.classpath = sb.toString();
    }

    public  Class getClass(String fullClassName, TextReader reader) throws IllegalAccessError{
        long start = System.currentTimeMillis();

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        ClassFileManager fileManager = new ClassFileManager(compiler.getStandardFileManager(diagnostics, null, null));

        List<JavaFileObject> jfiles = new ArrayList<JavaFileObject>();
        jfiles.add(new CharSequenceJavaFileObject(fullClassName, reader.readToEnd()));

        List<String> options = new ArrayList<String>();
        options.add("-encoding");
        options.add("UTF-8");
        options.add("-classpath");
        options.add(this.classpath);

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, jfiles);
        boolean success = task.call();

        if (success) {
            JavaClassObject jco = fileManager.getJavaClassObject();
            DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(this.parentClassLoader);
            Class clazz = dynamicClassLoader.loadClass(fullClassName,jco);
           return clazz;
        } else {
            String error = "";
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                error = error + compilePrint(diagnostic);
            }
        }
        return  null;
    }


    public  Class compilePath(String codePath,String templatePath){

String fullClassName="JRazor." +  ParserHelpers
                .sanitizeClassName("_Page_" + StringHelper.trimStart(templatePath, '~', '/'));

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        ClassFileManager fileManager = new ClassFileManager(compiler.getStandardFileManager(diagnostics, null, null));
         Iterable jfiles = fileManager.getJavaFileObjects(codePath);

        List<String> options = new ArrayList<String>();
        options.add("-encoding");
        options.add("UTF-8");
        options.add("-classpath");
        options.add(this.classpath);

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, jfiles);
        boolean success = task.call();

        if (success) {
            JavaClassObject jco = fileManager.getJavaClassObject();
            DynamicClassLoader dynamicClassLoader = new DynamicClassLoader(this.parentClassLoader);
            Class clazz = dynamicClassLoader.loadClass(fullClassName,jco);
             File file=new File(codePath);
            file.delete();
            return clazz;
        } else {
            String error = "";
            for (Diagnostic diagnostic : diagnostics.getDiagnostics()) {
                error = error + compilePrint(diagnostic);
            }
        }
        return  null;
    }

    public Object javaCodeToObject(String fullClassName, String javaCode) throws IllegalAccessException, InstantiationException {
        Class clazz= getClass(fullClassName,new TextReader(javaCode));
        if(clazz ==null) {
            return  null;
        }
         return clazz.newInstance();
    }

    private String compilePrint(Diagnostic diagnostic) {
        System.out.println("Code:" + diagnostic.getCode());
        System.out.println("Kind:" + diagnostic.getKind());
        System.out.println("Position:" + diagnostic.getPosition());
        System.out.println("Start Position:" + diagnostic.getStartPosition());
        System.out.println("End Position:" + diagnostic.getEndPosition());
        System.out.println("Source:" + diagnostic.getSource());
        System.out.println("Message:" + diagnostic.getMessage(null));
        System.out.println("LineNumber:" + diagnostic.getLineNumber());
        System.out.println("ColumnNumber:" + diagnostic.getColumnNumber());
        StringBuffer res = new StringBuffer();
        res.append("Code:[" + diagnostic.getCode() + "]\n");
        res.append("Kind:[" + diagnostic.getKind() + "]\n");
        res.append("Position:[" + diagnostic.getPosition() + "]\n");
        res.append("Start Position:[" + diagnostic.getStartPosition() + "]\n");
        res.append("End Position:[" + diagnostic.getEndPosition() + "]\n");
        res.append("Source:[" + diagnostic.getSource() + "]\n");
        res.append("Message:[" + diagnostic.getMessage(null) + "]\n");
        res.append("LineNumber:[" + diagnostic.getLineNumber() + "]\n");
        res.append("ColumnNumber:[" + diagnostic.getColumnNumber() + "]\n");
        return res.toString();
    }

    public static class CharSequenceJavaFileObject extends SimpleJavaFileObject {

        private CharSequence content;


        public CharSequenceJavaFileObject(String className,
                                          CharSequence content) {
            super(URI.create("string:///" + className.replace('.', '/')
                    + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
            this.content = content;
        }

        @Override
        public CharSequence getCharContent(
                boolean ignoreEncodingErrors) {
            return content;
        }
    }

    public static class ClassFileManager extends
            ForwardingJavaFileManager {

        private StandardJavaFileManager standard;
        public JavaClassObject getJavaClassObject() {
            return jclassObject;
        }

        public Iterable<? extends JavaFileObject> getJavaFileObjects(String... file){
            return  standard.getJavaFileObjects(file);
        }

        private JavaClassObject jclassObject;


        public ClassFileManager(StandardJavaFileManager
                                        standardManager) {
            super(standardManager);
            this.standard=standardManager;
        }


        @Override
        public JavaFileObject getJavaFileForOutput(Location location,
                                                   String className, JavaFileObject.Kind kind, FileObject sibling)
                throws IOException {
            jclassObject = new JavaClassObject(className, kind);
            return jclassObject;
        }
    }

    public static class JavaClassObject extends SimpleJavaFileObject {

        protected final ByteArrayOutputStream bos =
                new ByteArrayOutputStream();


        public JavaClassObject(String name, JavaFileObject.Kind kind) {
            super(URI.create("string:///" + name.replace('.', '/')
                    + kind.extension), kind);
        }


        public byte[] getBytes() {
            return bos.toByteArray();
        }

        @Override
        public OutputStream openOutputStream() throws IOException {
            return bos;
        }
    }


    public static class DynamicClassLoader extends URLClassLoader {
        public DynamicClassLoader(ClassLoader parent) {
            super(new URL[0], parent);
        }

        public Class findClassByClassName(String className) throws ClassNotFoundException {
            return this.findClass(className);
        }

        public Class loadClass(String fullName, JavaClassObject jco) {
            byte[] classData = jco.getBytes();
            return this.defineClass(fullName, classData, 0, classData.length);
        }
    }

}
