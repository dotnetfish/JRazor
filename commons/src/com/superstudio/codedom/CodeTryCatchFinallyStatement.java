package com.superstudio.codedom;
import java.io.Serializable;
public class CodeTryCatchFinallyStatement extends CodeStatement implements Serializable
{
	private CodeStatementCollection tryStatments = new CodeStatementCollection();

	private CodeStatementCollection finallyStatments = new CodeStatementCollection();

	private CodeCatchClauseCollection catchClauses = new CodeCatchClauseCollection();

	public final CodeStatementCollection getTryStatements()
	{
		return this.tryStatments;
	}

	public final CodeCatchClauseCollection getCatchClauses()
	{
		return this.catchClauses;
	}

	public final CodeStatementCollection getFinallyStatements()
	{
		return this.finallyStatments;
	}

	public CodeTryCatchFinallyStatement()
	{
	}

	public CodeTryCatchFinallyStatement(CodeStatement[] tryStatements, CodeCatchClause[] catchClauses)
	{
		this.getTryStatements().addAll(tryStatements);
		this.getCatchClauses().AddRange(catchClauses);
	}

	public CodeTryCatchFinallyStatement(CodeStatement[] tryStatements, CodeCatchClause[] catchClauses, CodeStatement[] finallyStatements)
	{
		this.getTryStatements().addAll(tryStatements);
		this.getCatchClauses().AddRange(catchClauses);
		this.getFinallyStatements().addAll(finallyStatements);
	}
}