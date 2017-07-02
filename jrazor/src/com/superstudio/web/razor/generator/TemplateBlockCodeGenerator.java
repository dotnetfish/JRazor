package com.superstudio.web.razor.generator;

import com.superstudio.web.razor.parser.syntaxTree.*;


public class TemplateBlockCodeGenerator extends BlockCodeGenerator
{
	private static final String TemplateWriterName = "__razor_template_writer";
	private static final String ItemParameterName = "item";

	private String _oldTargetWriter;

	@Override
	public void generateStartBlockCode(Block target, CodeGeneratorContext context)
	{

		String generatedCode = context.buildCodeString(cw ->
		{
			try {
				cw.writeStartLambdaExpression(ItemParameterName);
				cw.writeStartConstructor(context.getHost().getGeneratedClassContext().getTemplateTypeName());
				cw.writeStartLambdaDelegate(TemplateWriterName);
			}catch(Exception e){
				e.printStackTrace();
				//context.get
			}
		}
	   );

		context.markEndOfGeneratedCode();
		context.bufferStatementFragment(generatedCode);
		context.flushBufferedStatement();

		_oldTargetWriter = context.getTargetWriterName();
		context.setTargetWriterName(TemplateWriterName);
	}

	@Override
	public void generateEndBlockCode(Block target, CodeGeneratorContext context)
	{
		String generatedCode = context.buildCodeString(cw ->
		{
			cw.writeEndLambdaDelegate();
			cw.writeEndConstructor();
			cw.writeEndLambdaExpression();
		}
	   );

		context.bufferStatementFragment(generatedCode);
		context.setTargetWriterName(_oldTargetWriter);
	}

	@Override
	public boolean equals(Object obj, Object others) {
		// TODO Auto-generated method stub
		return obj.equals(others);
	}
}