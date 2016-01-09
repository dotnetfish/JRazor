package com.superstudio.jrazor.generator;


import com.superstudio.codedom.*;
import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.jrazor.parser.syntaxTree.*;

public class SetBaseTypeCodeGenerator extends SpanCodeGenerator {
	public SetBaseTypeCodeGenerator(String baseType) {
		setBaseType(baseType);
	}

	private String privateBaseType;

	public final String getBaseType() {
		return privateBaseType;
	}

	private void setBaseType(String value) {
		privateBaseType = value;
	}

	@Override
	public void generateCode(Span target, CodeGeneratorContext context) {
		context.getGeneratedClass().getBaseTypes().clear();// (0,
															// context.getGeneratedClass().getBaseTypes().size());
		context.getGeneratedClass().getBaseTypes()
				.add(new CodeTypeReference(resolveType(context, getBaseType().trim())));

		if (context.getHost().getDesignTimeMode()) {
			int generatedCodeStart = 0;
			 
			 
			RefObject<Integer> refObj=new RefObject<Integer>(0);
			
			String code = context.buildCodeString(cw -> {
				refObj.setRefObj( cw.writeVariableDeclaration(target.getContent(), "__inheritsHelper", null));
				cw.writeEndStatement();
			});

			int paddingCharCount = 0;

			RefObject<Integer> tempRef_paddingCharCount = new RefObject<Integer>(paddingCharCount);
			CodeSnippetStatement tempVar = new CodeSnippetStatement(CodeGeneratorPaddingHelper.pad(context.getHost(),
					code, target, generatedCodeStart, tempRef_paddingCharCount));

			paddingCharCount = tempRef_paddingCharCount.getRefObj();
			tempVar.setLinePragma(context.generateLinePragma(target, generatedCodeStart + paddingCharCount));
			CodeSnippetStatement stmt = tempVar;

			context.addDesignTimeHelperStatement(stmt);
		}
	}

	protected String resolveType(CodeGeneratorContext context, String baseType) {
		return baseType;
	}

	@Override
	public String toString() {
		return "Base:" + getBaseType();
	}

	@Override
	public boolean equals(Object obj) {
		SetBaseTypeCodeGenerator other = (SetBaseTypeCodeGenerator) ((obj instanceof SetBaseTypeCodeGenerator) ? obj
				: null);
		return other != null && (getBaseType().equals(other.getBaseType()));
	}

	@Override
	public int hashCode() {
		return getBaseType().hashCode();
	}
}