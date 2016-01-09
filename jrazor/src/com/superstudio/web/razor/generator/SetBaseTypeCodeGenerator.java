package com.superstudio.web.razor.generator;

import com.superstudio.web.razor.parser.syntaxTree.*;
import com.superstudio.codedom.*;
import com.superstudio.commons.csharpbridge.RefObject;

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

			// methods are not converted
			RefObject<Integer> refObj=new RefObject<Integer>(0);
			
			String code = context.BuildCodeString(cw -> {
				refObj.setRefObj( cw.writeVariableDeclaration(target.getContent(), "__inheritsHelper", null));
				cw.writeEndStatement();
			});

			int paddingCharCount = 0;

			RefObject<Integer> tempRef_paddingCharCount = new RefObject<Integer>(paddingCharCount);
			CodeSnippetStatement tempVar = new CodeSnippetStatement(CodeGeneratorPaddingHelper.pad(context.getHost(),
					code, target, generatedCodeStart, tempRef_paddingCharCount));

			paddingCharCount = tempRef_paddingCharCount.getRefObj();
			tempVar.setLinePragma(context.GenerateLinePragma(target, generatedCodeStart + paddingCharCount));
			CodeSnippetStatement stmt = tempVar;

			context.AddDesignTimeHelperStatement(stmt);
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