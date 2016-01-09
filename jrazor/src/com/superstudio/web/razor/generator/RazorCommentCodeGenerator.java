package com.superstudio.web.razor.generator;


import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.web.razor.parser.syntaxTree.*;

public class RazorCommentCodeGenerator extends BlockCodeGenerator
{
	@Override
	public void generateStartBlockCode(Block target, CodeGeneratorContext context)
	{
		// flush the buffered statement since we're interrupting it with a comment.
		if (!StringHelper.isNullOrEmpty(context.getCurrentBufferedStatement()))
		{
			context.MarkEndOfGeneratedCode();

			context.BufferStatementFragment(context.BuildCodeString(cw -> cw.writeLineContinuation()));
		}
		context.FlushBufferedStatement();
	}

	@Override
	public boolean equals(Object obj, Object others) {
		// TODO Auto-generated method stub
		return obj.equals(others);
	}
}