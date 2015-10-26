package com.superstudio.jrazor.generator;



import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.jrazor.parser.syntaxTree.Block;
public class RazorCommentCodeGenerator extends BlockCodeGenerator
{
	@Override
	public void generateStartBlockCode(Block target, CodeGeneratorContext context)
	{
		// Flush the buffered statement since we're interrupting it with a comment.
		if (!StringHelper.isNullOrEmpty(context.getCurrentBufferedStatement()))
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