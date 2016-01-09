package com.superstudio.web.razor.generator;

import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.web.razor.parser.syntaxTree.*;

public class StatementCodeGenerator extends SpanCodeGenerator
{
	@Override
	public void generateCode(Span target, CodeGeneratorContext context)
	{
		context.FlushBufferedStatement();
		String generatedCode = context.BuildCodeString(cw ->
		{
			cw.writeSnippet(target.getContent());
		}
	   );

		int startGeneratedCode = target.getStart().getCharacterIndex();
		int paddingCharCount = 0;
		RefObject<Integer> tempRef_startGeneratedCode = new RefObject<Integer>(startGeneratedCode);
		RefObject<Integer> tempRef_paddingCharCount = new RefObject<Integer>(paddingCharCount);
		generatedCode = CodeGeneratorPaddingHelper.padStatement(context.getHost(), generatedCode, target, tempRef_startGeneratedCode, tempRef_paddingCharCount);
		startGeneratedCode = tempRef_startGeneratedCode.getRefObj();
		paddingCharCount = tempRef_paddingCharCount.getRefObj();

		context.AddStatement(generatedCode, context.GenerateLinePragma(target, paddingCharCount));
	}

	@Override
	public String toString()
	{
		return "Stmt";
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof StatementCodeGenerator;
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
}