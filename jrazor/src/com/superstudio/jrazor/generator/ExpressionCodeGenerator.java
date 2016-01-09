package com.superstudio.jrazor.generator;

import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.jrazor.parser.syntaxTree.Block;
import com.superstudio.jrazor.parser.syntaxTree.Span;
import com.superstudio.jrazor.parser.syntaxTree.SpanKind;

public class ExpressionCodeGenerator extends HybridCodeGenerator {
	@Override
	public void generateStartBlockCode(Block target, CodeGeneratorContext context) {
		if (context.getHost().getEnableInstrumentation()
				&& context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
			 
			 
			Span contentSpan = (Span) CollectionHelper.firstOrDefault(target.getChildren(),
					s -> ((Span) s).getKind() == SpanKind.Code || ((Span) s).getKind() == SpanKind.Markup);

			if (contentSpan != null) {
				context.addContextCall(contentSpan,
						context.getHost().getGeneratedClassContext().getBeginContextMethodName(), false);
			}
		}

		 
		 
		String writeInvocation = context.buildCodeString(cw -> {
			if (context.getHost().getDesignTimeMode()) {
				context.ensureExpressionHelperVariable();
				cw.writeStartAssignment("__o");
			} else if (context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
				if (!StringHelper.isNullOrEmpty(context.getTargetWriterName())) {
					cw.WriteStartMethodInvoke(context.getHost().getGeneratedClassContext().getWriteToMethodName());
					cw.writeSnippet(context.getTargetWriterName());
					cw.writeParameterSeparator();
				} else {
					cw.WriteStartMethodInvoke(context.getHost().getGeneratedClassContext().getWriteMethodName());
				}
			}
		});

		context.bufferStatementFragment(writeInvocation);
		context.markStartOfGeneratedCode();
	}

	@Override
	public void generateEndBlockCode(Block target, CodeGeneratorContext context) {
		 
		 
		String endBlock = context.buildCodeString(cw -> {
			if (context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
				if (!context.getHost().getDesignTimeMode()) {
					cw.WriteEndMethodInvoke();
				}
				cw.writeEndStatement();
			} else {
				cw.writeLineContinuation();
			}
		});

		context.markEndOfGeneratedCode();
		context.bufferStatementFragment(endBlock);
		context.flushBufferedStatement();

		if (context.getHost().getEnableInstrumentation()
				&& context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
			Span contentSpan = (Span) CollectionHelper.firstOrDefault(target.getChildren(),
					s -> ((Span) s).getKind() == SpanKind.Code || ((Span) s).getKind() == SpanKind.Markup);// .FirstOrDefault();

			if (contentSpan != null) {
				context.addContextCall(contentSpan,
						context.getHost().getGeneratedClassContext().getEndContextMethodName(), false);
			}
		}
	}

	@Override
	public void generateCode(Span target, CodeGeneratorContext context) {
		Span sourceSpan = null;
		if (context.createCodeWriter().getSupportsMidStatementLinePragmas()
				|| context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
			sourceSpan = target;
		}
		context.bufferStatementFragment(target.getContent(), sourceSpan);
	}

	@Override
	public String toString() {
		return "Expr";
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ExpressionCodeGenerator;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}