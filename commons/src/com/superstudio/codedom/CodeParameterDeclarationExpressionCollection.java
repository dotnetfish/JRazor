package com.superstudio.codedom;

import java.io.Serializable;


public class CodeParameterDeclarationExpressionCollection extends CollectionBase<CodeParameterDeclarationExpression> implements Serializable {
	public final CodeParameterDeclarationExpression getItem(int index) {
		return (CodeParameterDeclarationExpression) get(index);
	}

	public final void setItem(int index, CodeParameterDeclarationExpression value) {
		//set[index] = value;
		add(index, value);
	}

	public CodeParameterDeclarationExpressionCollection() {
	}

	public CodeParameterDeclarationExpressionCollection(CodeParameterDeclarationExpressionCollection value) {
		this.addRange(value);
	}

	public CodeParameterDeclarationExpressionCollection(CodeParameterDeclarationExpression[] value) {
		this.addRange(value);
	}



	public final void addRange(CodeParameterDeclarationExpression[] value) {
		if (value == null) {
			throw new IllegalArgumentException("value");
		}
		for (int i = 0; i < value.length; i++) {
			this.add(value[i]);
		}
	}

	public final void addRange(CodeParameterDeclarationExpressionCollection value) {
		if (value == null) {
			throw new IllegalArgumentException("value");
		}
		int count = value.size();
		for (int i = 0; i < count; i++) {
			this.add(value.getItem(i));
		}
	}


	public final void copyto(CodeParameterDeclarationExpression[] array, int index) {
		copyTo(array, index);
	}


}