package com.superstudio.web.razor.generator;

import com.superstudio.codedom.CodeSnippetTypeMember;
import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.web.razor.parser.syntaxTree.Span;

public class TypeMemberCodeGenerator extends SpanCodeGenerator
{
	@Override
	public void generateCode(Span target, CodeGeneratorContext context)
	{

		String generatedCode = context.BuildCodeString(cw ->
		{
			cw.writeSnippet(target.getContent());
		}
	   );

		int paddingCharCount = 0;
		RefObject<Integer> tempRef_paddingCharCount = new RefObject<Integer>(paddingCharCount);
		String paddedCode = CodeGeneratorPaddingHelper.pad(context.getHost(), generatedCode, target, tempRef_paddingCharCount);
		paddingCharCount = tempRef_paddingCharCount.getRefObj();

		//Contract.Assert(paddingCharCount > 0);

		CodeSnippetTypeMember tempVar = new CodeSnippetTypeMember(paddedCode);
		tempVar.setLinePragma(context.GenerateLinePragma(target, paddingCharCount));
		context.getGeneratedClass().getMembers().add(tempVar);
	}

	@Override
	public String toString()
	{
		return "TypeMember";
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof TypeMemberCodeGenerator;
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}
}