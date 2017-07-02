package com.superstudio.web.razor.generator;


import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.web.razor.parser.syntaxTree.*;
import org.apache.commons.lang3.StringUtils;

public class RazorCommentCodeGenerator extends BlockCodeGenerator
{
	@Override
	public void generateStartBlockCode(Block target, CodeGeneratorContext context)
	{
		// flush the buffered statement since we're interrupting it with a comment.
		if (!StringUtils.isBlank(context.getCurrentBufferedStatement()))
		{
			context.markEndOfGeneratedCode();

			context.bufferStatementFragment(context.buildCodeString(cw -> cw.writeLineContinuation()));
		}
		context.flushBufferedStatement();
	}

	@Override
	public boolean equals(Object obj, Object others) {
		// TODO Auto-generated method stub
		return obj.equals(others);
	}
}