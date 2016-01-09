package com.superstudio.compiler.java;

import com.superstudio.codedom.*;
import com.superstudio.codedom.compiler.CodeCompiler;
import com.superstudio.codedom.compiler.CompilerParameters;
import com.superstudio.codedom.compiler.CompilerResults;
import com.superstudio.codedom.compiler.GeneratorSupport;
import com.superstudio.commons.TypeAttributes;

public final class JavaCodeCompiler extends CodeCompiler {// implements Opcodes
															// {

	/*
	 * public void Generate(String file) { try { RazorEngineHost host = new
	 * RazorEngineHost(); host.setDesignTimeMode(false); RazorTemplateEngine
	 * engine = new RazorTemplateEngine(host); TextReader reader = new
	 * TextReader(
	 * "foo @Model.say() \r\n@{\r\n if(true)\r\n{\r\n<span>compiler with java</span>\r\n}\r\n}"
	 * );
	 * 
	 * GeneratorResults result = engine.generateCode(reader); CodeCompileUnit
	 * unit = result.getGeneratedCode(); StringBuilder builder = new
	 * StringBuilder();
	 * 
	 * List<CodeNamespace> namespaces = unit.getNamespaces(); for(CodeNamespace
	 * ns :namespaces){ builder.delete(0, builder.length());
	 * 
	 * builder.append("import System.Web.*;\r\n"); for(Object
	 * item:ns.getTypes()){ CodeTypeDeclaration
	 * typeDecl=(CodeTypeDeclaration)item; CodeLinePragma
	 * linePragma=typeDecl.getLinePragma(); builder.append("package "
	 * +typeDecl.getName()+typeDecl.getLinePragma()); builder.append("\r\n");
	 * TypeAttributes attr=typeDecl.getTypeAttributes(); switch(attr){ case
	 * Public: builder.append("public "); break; case NestedPrivate://private
	 * builder.append("public "); break; case NestedPublic://protected
	 * builder.append("protected "); default: break; }
	 * if(typeDecl.getIsClass())builder.append("class ");
	 * if(typeDecl.getIsInterface())builder.append("interface ");
	 * if(typeDecl.getIsEnum())builder.append("enum ");
	 * builder.append(typeDecl.getName()); builder.append("{\r\n");
	 * List<CodeTypeMember> methods=typeDecl.getMembers(); for(CodeTypeMember
	 * method:methods){ String name= method.getName(); MemberAttributes
	 * mAttr=method.getAttributes(); //method.getComments().get }
	 * builder.append("@Override\r\n"); builder.append(
	 * "public String execute(Object model,Map viewBag){\r\n"); builder.append(
	 * "StringBuilder builder=new StringBuilder()\r\n");
	 * //writeLine(builder,"builder.append(\""+typeDecl+"\")",linePragma);
	 * 
	 * //if(typeDecl.)
	 * 
	 * //builder.append() //builder.append("imports"typeDecl.getName())
	 * //typeDecl } } CodeAttributeDeclaration class_type
	 * =(CodeAttributeDeclaration)((CodeNamespace)
	 * unit.getNamespaces().get(0)).getTypes().get(0); // String
	 * className=class_type.get
	 * 
	 * } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); } }
	 * 
	 * private void writeLine(StringBuilder builder, String code, String
	 * linePragma) { builder.append(code); builder.append(linePragma);
	 * builder.append("\r\n"); }
	 * 
	 * public void compile() throws IOException, ClassNotFoundException { String
	 * rt = "\r\n"; String source = "package com.cjb.proxy;" + "" + rt +
	 * "public class Dealer implements Store" + rt + "{" + rt +
	 * "private Store s;" + rt + "public Dealer(Store s)" + rt + " {" +
	 * "  this.s = s;" + rt + " }" + rt +
	 * 
	 * " public void sell()" + " {" + rt +
	 * "  System.out.println(\"price markup....\");" + rt + "  s.sell();" + " }"
	 * + rt + "}";
	 * 
	 * String fileName = System.getProperty("user.dir")// 获取到项目的根路径 +
	 * "/src/com/cjb/proxy/Dealer.java"; File f = new File(fileName); FileWriter
	 * fw = new FileWriter(f); fw.write(source); fw.flush(); fw.close();//
	 * 这里只是产生一个JAVA文件,简单的IO操作
	 * 
	 * // compile下面开始编译这个Store.java JavaCompiler compiler =
	 * ToolProvider.getSystemJavaCompiler(); StandardJavaFileManager fileMgr =
	 * compiler.getStandardFileManager(null, null, null); Iterable units =
	 * fileMgr.getJavaFileObjects(fileName); CompilationTask t =
	 * compiler.getTask(null, fileMgr, null, null, null, units); t.call();
	 * fileMgr.close();
	 * 
	 * // load into memory and create an instance URL[] urls = new URL[] { new
	 * URL("file:/" + System.getProperty("user.dir") + "/src") }; URLClassLoader
	 * ul = new URLClassLoader(urls); Class c =
	 * ul.loadClass("com.cjb.proxy.Dealer"); System.out.println(c);
	 * 
	 * // 客户端调用
	 * 
	 * // Constructor ctr = c.getConstructor(Store.class); // Store s =
	 * (Store)ctr.newInstance(new // Supermarket());//这里看到,这个我们这个代理类必须实现Store的原因
	 * // s.sell(); }
	 */
	@Override
	protected String getFileExtension() {
		// TODO Auto-generated method stub
		return ".java";
	}

	@Override
	protected String getCompilerName() {
		// TODO Auto-generated method stub
		return "JavaCompiler";
	}

	@Override
	protected void processCompilerOutputLine(CompilerResults results, String line) {
		// TODO Auto-generated method stub

	}

	@Override
	protected String cmdArgsFromParameters(CompilerParameters options) {
		// TODO Auto-generated method stub
		return "sdss  jkfj ";
	}

	@Override
	protected String getNullToken() {
		// TODO Auto-generated method stub
		return "null";
	}

	@Override
	protected void outputType(CodeTypeReference typeRef) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateArrayCreateExpression(CodeArrayCreateExpression e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateBaseReferenceExpression(CodeBaseReferenceExpression e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateCastExpression(CodeCastExpression e) {
		// TODO Auto-generated method stub
		// e.setExpression();
	}

	@Override
	protected void generateDelegateCreateExpression(CodeDelegateCreateExpression e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateFieldReferenceExpression(CodeFieldReferenceExpression e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateArgumentReferenceExpression(CodeArgumentReferenceExpression e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateVariableReferenceExpression(CodeVariableReferenceExpression e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateIndexerExpression(CodeIndexerExpression e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateArrayIndexerExpression(CodeArrayIndexerExpression e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateSnippetExpression(CodeSnippetExpression e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateMethodInvokeExpression(CodeMethodInvokeExpression e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateMethodReferenceExpression(CodeMethodReferenceExpression e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateEventReferenceExpression(CodeEventReferenceExpression e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateDelegateInvokeExpression(CodeDelegateInvokeExpression e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateObjectCreateExpression(CodeObjectCreateExpression e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generatePropertyReferenceExpression(CodePropertyReferenceExpression e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generatePropertySetValueReferenceExpression(CodePropertySetValueReferenceExpression e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateThisReferenceExpression(CodeThisReferenceExpression e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateExpressionStatement(CodeExpressionStatement e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateIterationStatement(CodeIterationStatement e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateThrowExceptionStatement(CodeThrowExceptionStatement e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateComment(CodeComment e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateMethodReturnStatement(CodeMethodReturnStatement e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateConditionStatement(CodeConditionStatement e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateTryCatchFinallyStatement(CodeTryCatchFinallyStatement e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateAssignStatement(CodeAssignStatement e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateAttachEventStatement(CodeAttachEventStatement e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateRemoveEventStatement(CodeRemoveEventStatement e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateGotoStatement(CodeGotoStatement e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateLabeledStatement(CodeLabeledStatement e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateVariableDeclarationStatement(CodeVariableDeclarationStatement e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateLinePragmaStart(CodeLinePragma e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateLinePragmaEnd(CodeLinePragma e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateEvent(CodeMemberEvent e, CodeTypeDeclaration c) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateField(CodeMemberField e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateSnippetMember(CodeSnippetTypeMember e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateEntryPointMethod(CodeEntryPointMethod e, CodeTypeDeclaration c) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateMethod(CodeMemberMethod e, CodeTypeDeclaration c)  {
		// TODO Auto-generated method stub
		/*e.setName("dfsf");
		c.setName("method");*/
		try{
		super.getOutput().writeLine("// generator method from  GeneratorMethod");
		getOutput().write(c.getTypeAttributes().getValue()==TypeAttributes.Public?"public ":" ");
		getOutput().write(e.getReturnType().getBaseType()+" ");
		getOutput().write(e.getName());
		getOutput().write("(");
		if(e.getParameters().size()>0){
			getOutput().write(e.getParameters().get(0).getClass().getName()+" arg0)");
		}else{
			getOutput().write(")");
		}
		
		getOutput().write("{\r\n\r\n}");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	protected void generateProperty(CodeMemberProperty e, CodeTypeDeclaration c) {
		// TODO Auto-generated method stub
		e.setName("dfsf");
		c.setName("method");
	}

	@Override
	protected void generateConstructor(CodeConstructor e, CodeTypeDeclaration c) {
		// TODO Auto-generated method stub
		/*e.setName("dfsf");
		c.setName("method");*/
	}

	@Override
	protected void generateTypeConstructor(CodeTypeConstructor e) {
		// TODO Auto-generated method stub
		/*e.setName("dfsf");*/
		//e.setReturnType("returnType");
		//c.setName("method");
	}

	@Override
	protected void generateTypeStart(CodeTypeDeclaration e) {
		// TODO Auto-generated method stub
		/*e.setName("dfsf");
		e.setIsClass(true);*/
		
		//c.setName("method");
	}

	@Override
	protected void generateTypeEnd(CodeTypeDeclaration e) {
		// TODO Auto-generated method stub
		
		// c.setName("method");
	}

	@Override
	protected void generateNamespaceStart(CodeNamespace e) {
		// TODO Auto-generated method stub
		
		// c.setName("method");
	}

	@Override
	protected void generateNamespaceEnd(CodeNamespace e) {
		// TODO Auto-generated method stub
		
		// c.setName("method");
	}

	@Override
	protected void generateNamespaceImport(CodeNamespaceImport e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateAttributeDeclarationsStart(CodeAttributeDeclarationCollection attributes) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateAttributeDeclarationsEnd(CodeAttributeDeclarationCollection attributes) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean supports(GeneratorSupport support) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isValidIdentifier(String value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String createEscapedIdentifier(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createValidIdentifier(String value) {
		// TODO Auto-generated method stub
		return "ccEl";
	}

	@Override
	public String getTypeOutput(CodeTypeReference value) {
		// TODO Auto-generated method stub
		return value.getBaseType();
	}

	@Override
	protected String QuoteSnippetString(String value) {
		// TODO Auto-generated method stub
		return  value;
	}

	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(DalClassLoader.class);

	// 这里省略了其它的代码....
	/*
	 * protected static Class<RowMapper> loadRowMapperClass(EntityMeta
	 * entityMeta) { ClassWriter cw = new ClassWriter(0); MethodVisitor mv;
	 * 
	 * final String entityClassName =
	 * entityMeta.getEntityClass().getName().replace('.', '/'); final String
	 * entityAsmClassDesc = "L" + entityClassName + ";";
	 * 
	 * final String rowMapperClass = entityMeta.getEntityClass().getName() +
	 * "RowMapper"; final String rowMapperClassName =
	 * rowMapperClass.replace('.', '/'); final String rowMapperClassAsmDesc =
	 * "L" + rowMapperClassName + ";";
	 * 
	 * final String classNameResultSet = ResultSet.class.getName().replace('.',
	 * '/'); final String asmDescResultSet = "L" + classNameResultSet + ";";
	 *//**
		 * com/tianjiaguo/site
		 *//*
		 * final String classNameRowMapper =
		 * RowMapper.class.getName().replace('.', '/');
		 * 
		 * cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, rowMapperClassName, null,
		 * CLASS_NAME_OBJECT, new String[] { classNameRowMapper });
		 * 
		 * cw.visitSource("$$EntityHelper.java", null); int line = 1;
		 * 
		 * { mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		 * mv.visitCode(); Label l0 = new Label(); mv.visitLabel(l0);
		 * mv.visitLineNumber(line++, l0); mv.visitVarInsn(ALOAD, 0);
		 * mv.visitMethodInsn(INVOKESPECIAL, CLASS_NAME_OBJECT, "<init>",
		 * "()V"); mv.visitInsn(RETURN); Label l1 = new Label();
		 * mv.visitLabel(l1); mv.visitLocalVariable("this",
		 * rowMapperClassAsmDesc, null, l0, l1, 0); mv.visitMaxs(1, 1);
		 * mv.visitEnd(); } { mv = cw.visitMethod(ACC_PUBLIC, "mapRow",
		 * String.format("(%sI)%s", asmDescResultSet, ASM_DESC_OBJECT), null,
		 * new String[] { CLASS_NAME_SQL_EXCEPTION }); mv.visitCode(); Label l0
		 * = new Label(); mv.visitLabel(l0); mv.visitLineNumber(line++, l0);
		 * mv.visitTypeInsn(NEW, entityClassName); mv.visitInsn(DUP);
		 * mv.visitMethodInsn(INVOKESPECIAL, entityClassName, "<init>", "()V");
		 * mv.visitVarInsn(ASTORE, 3);
		 * 
		 * Label l1 = new Label(); { ImmutableMap.Builder<String, ColumnMeta>
		 * builder = ImmutableMap.builder();
		 * builder.putAll(entityMeta.getPrimaryMetas());
		 * builder.putAll(entityMeta.getNormalMetas()); ImmutableMap<String,
		 * ColumnMeta> metaMap = builder.build(); ImmutableSet<Map.Entry<String,
		 * ColumnMeta>> entries = metaMap.entrySet(); for (Map.Entry<String,
		 * ColumnMeta> entry : entries) { ColumnMeta columnMeta =
		 * entry.getValue();
		 * 
		 * Label label = new Label(); mv.visitLabel(label);
		 * mv.visitLineNumber(line++, label); mv.visitVarInsn(ALOAD, 3);
		 * mv.visitVarInsn(ALOAD, 1);
		 * mv.visitLdcInsn(columnMeta.getColumnName());
		 * TypeConstant.getType()Wrapper typeWrapper =
		 * TypeConstant.TYPE_WRAPPERS.get(columnMeta.getFieldType());
		 * mv.visitMethodInsn(INVOKEINTERFACE, classNameResultSet,
		 * typeWrapper.getMethodName, String.format("(%s)%s", ASM_DESC_STRING,
		 * typeWrapper.asmDesc)); mv.visitMethodInsn(INVOKEVIRTUAL,
		 * entityClassName, columnMeta.getFieldSetMethod().getName(),
		 * String.format("(%s)V", typeWrapper.asmDesc)); } } Label l4 = new
		 * Label(); mv.visitLabel(l4); mv.visitLineNumber(line++, l4);
		 * mv.visitVarInsn(ALOAD, 3); mv.visitInsn(ARETURN); Label l5 = new
		 * Label(); mv.visitLabel(l5); mv.visitLocalVariable("this",
		 * rowMapperClassAsmDesc, null, l0, l5, 0); mv.visitLocalVariable("rs",
		 * asmDescResultSet, null, l0, l5, 1); mv.visitLocalVariable("rowNum",
		 * TypeConstant.getAsmDesc(Integer.TYPE), null, l0, l5, 2);
		 * mv.visitLocalVariable("entity", entityAsmClassDesc, null, l1, l5, 3);
		 * mv.visitMaxs(3, 4); mv.visitEnd(); } cw.visitEnd(); return
		 * ClassLoaderUtil.toClass(cw.toByteArray(), rowMapperClass,
		 * java.lang.ClassLoader.getSystemClassLoader(), null); }
		 */
}
