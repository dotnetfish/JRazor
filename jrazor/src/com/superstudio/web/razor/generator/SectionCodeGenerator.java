package com.superstudio.web.razor.generator;

import com.superstudio.commons.HashCodeCombiner;
import com.superstudio.web.razor.parser.syntaxTree.*;

public class SectionCodeGenerator extends BlockCodeGenerator {
	public SectionCodeGenerator(String sectionName) {
		setSectionName(sectionName);
	}

	private String privateSectionName;

	public final String getSectionName() {
		return privateSectionName;
	}

	private void setSectionName(String value) {
		privateSectionName = value;
	}

	@Override
	public void generateStartBlockCode(Block target, CodeGeneratorContext context) throws Exception{
		String startBlock = context.BuildCodeString(cw -> {
			cw.writeStartMethodInvoke(context.getHost().getGeneratedClassContext().getDefineSectionMethodName());
			try {
				cw.writeStringLiteral(getSectionName());
				cw.writeParameterSeparator();
				cw.writeStartLambdaDelegate();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});
		context.AddStatement(startBlock);
	}

	@Override
	public void generateEndBlockCode(Block target, CodeGeneratorContext context) {
		String startBlock = context.BuildCodeString(cw -> {
			cw.writeEndLambdaDelegate();
			cw.writeEndMethodInvoke();
			cw.writeEndStatement();
		});
		context.AddStatement(startBlock);
	}

	@Override
	public boolean equals(Object obj) {
		SectionCodeGenerator other = (SectionCodeGenerator) ((obj instanceof SectionCodeGenerator) ? obj : null);
		return other != null && super.equals(other) && getSectionName().equals(other.getSectionName());
	}

	@Override
	public int hashCode() {
		return HashCodeCombiner.Start().Add(super.hashCode()).Add(getSectionName()).getCombinedHash();
	}

	@Override
	public String toString() {
		return "Section:" + getSectionName();
	}

	@Override
	public boolean equals(Object obj, Object others) {
		// TODO Auto-generated method stub
		return false;
	}
}