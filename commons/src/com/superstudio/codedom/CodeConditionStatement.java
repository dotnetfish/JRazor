package com.superstudio.codedom;
import java.io.Serializable;

public class CodeConditionStatement extends CodeStatement implements Serializable
{
	private CodeExpression condition;

	private CodeStatementCollection trueStatments = new CodeStatementCollection();

	private CodeStatementCollection falseStatments = new CodeStatementCollection();

	public final CodeExpression getCondition()
	{
		return this.condition;
	}
	public final void setCondition(CodeExpression value)
	{
		this.condition = value;
	}

	public final CodeStatementCollection getTrueStatements()
	{
		return this.trueStatments;
	}

	public final CodeStatementCollection getFalseStatements()
	{
		return this.falseStatments;
	}

	public CodeConditionStatement()
	{
	}

	public CodeConditionStatement(CodeExpression condition, CodeStatement... trueStatements)
	{
		this.setCondition(condition);
		this.getTrueStatements().addAll(trueStatements);
	}

	public CodeConditionStatement(CodeExpression condition, CodeStatement[] trueStatements, CodeStatement[] falseStatements)
	{
		this.setCondition(condition);
		this.getTrueStatements().addAll(trueStatements);
		this.getFalseStatements().addAll(falseStatements);
	}
}