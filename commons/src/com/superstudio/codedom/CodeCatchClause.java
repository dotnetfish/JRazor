package com.superstudio.codedom;

import java.io.Serializable;

 
//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeCatchClause
public class CodeCatchClause implements Serializable {
	private CodeStatementCollection statements;

	private CodeTypeReference catchExceptionType;

	private String localName;

	public final String getLocalName() {
		if (this.localName != null) {
			return this.localName;
		}
		return "";
	}

	public final void setLocalName(String value) {
		this.localName = value;
	}

	public final CodeTypeReference getCatchExceptionType() {
		if (this.catchExceptionType == null) {
			this.catchExceptionType = new CodeTypeReference(RuntimeException.class);
		}
		return this.catchExceptionType;
	}

	public final void setCatchExceptionType(CodeTypeReference value) {
		this.catchExceptionType = value;
	}

	public final CodeStatementCollection getStatements() {
		if (this.statements == null) {
			this.statements = new CodeStatementCollection();
		}
		return this.statements;
	}

	public CodeCatchClause() {
	}

	public CodeCatchClause(String localName) {
		this.localName = localName;
	}

	public CodeCatchClause(String localName, CodeTypeReference catchExceptionType) {
		this.localName = localName;
		this.catchExceptionType = catchExceptionType;
	}

	public CodeCatchClause(String localName, CodeTypeReference catchExceptionType, CodeStatement... statements) {
		this.localName = localName;
		this.catchExceptionType = catchExceptionType;
		this.getStatements().addAll(statements);
	}
}