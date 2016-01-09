package com.superstudio.codedom;

import java.io.Serializable;


public class CodeCastExpression extends CodeExpression implements Serializable {
	private CodeTypeReference targetType;

	private CodeExpression expression;

	public final CodeTypeReference getTargetType() {
		if (this.targetType == null) {
			this.targetType = new CodeTypeReference("");
		}
		return this.targetType;
	}

	public final void setTargetType(CodeTypeReference value) {
		this.targetType = value;
	}

	public final CodeExpression getExpression() {
		return this.expression;
	}

	public final void setExpression(CodeExpression value) {
		this.expression = value;
	}

	public CodeCastExpression() {
	}

	public CodeCastExpression(CodeTypeReference targetType, CodeExpression expression) {
		this.setTargetType(targetType);
		this.setExpression(expression);
	}

	public CodeCastExpression(String targetType, CodeExpression expression) {
		this.setTargetType(new CodeTypeReference(targetType));
		this.setExpression(expression);
	}

	public CodeCastExpression(java.lang.Class targetType, CodeExpression expression) {
		this.setTargetType(new CodeTypeReference(targetType));
		this.setExpression(expression);
	}
}