package com.superstudio.codedom;
import java.io.Serializable;

//ORIGINAL LINE: [ClassInterface(ClassInterfaceType.AutoDispatch), ComVisible(true)][Serializable] public class CodeIterationStatement : CodeStatement
public class CodeIterationStatement extends CodeStatement implements Serializable
{
	private CodeStatement initStatement;

	private CodeExpression testExpression;

	private CodeStatement incrementStatement;

	private CodeStatementCollection statements = new CodeStatementCollection();

	public final CodeStatement getInitStatement()
	{
		return this.initStatement;
	}
	public final void setInitStatement(CodeStatement value)
	{
		this.initStatement = value;
	}

	public final CodeExpression getTestExpression()
	{
		return this.testExpression;
	}
	public final void setTestExpression(CodeExpression value)
	{
		this.testExpression = value;
	}

	public final CodeStatement getIncrementStatement()
	{
		return this.incrementStatement;
	}
	public final void setIncrementStatement(CodeStatement value)
	{
		this.incrementStatement = value;
	}

	public final CodeStatementCollection getStatements()
	{
		return this.statements;
	}

	public CodeIterationStatement()
	{
	}

	public CodeIterationStatement(CodeStatement initStatement, CodeExpression testExpression, CodeStatement incrementStatement, CodeStatement... statements)
	{
		this.setInitStatement(initStatement);
		this.setTestExpression(testExpression);
		this.setIncrementStatement(incrementStatement);
		this.getStatements().addAll(statements);
	}
}