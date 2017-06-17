package com.superstudio.compiler.java;

import com.superstudio.codedom.*;
import com.superstudio.codedom.compiler.CodeCompiler;
import com.superstudio.codedom.compiler.CompilerParameters;
import com.superstudio.codedom.compiler.CompilerResults;
import com.superstudio.codedom.compiler.GeneratorSupport;
import com.superstudio.commons.TypeAttributes;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public final class JavaCodeCompiler extends CodeCompiler {


	@Override
	protected String getFileExtension() {
		return ".java";
	}

	@Override
	protected String getCompilerName() {
		return "JavaCompiler";
	}

	@Override
	protected void processCompilerOutputLine(CompilerResults results, String line) {

	}

	@Override
	protected String cmdArgsFromParameters(CompilerParameters options) {
		throw new NotImplementedException();
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

	}

	@Override
	protected void generateConstructor(CodeConstructor e, CodeTypeDeclaration c) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateTypeConstructor(CodeTypeConstructor e) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void generateTypeStart(CodeTypeDeclaration e) {
		// TODO Auto-generated method stub

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

}
