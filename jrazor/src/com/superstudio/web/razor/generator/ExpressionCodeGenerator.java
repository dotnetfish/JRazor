package com.superstudio.web.razor.generator;

import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.web.razor.parser.syntaxTree.*;

public class ExpressionCodeGenerator extends HybridCodeGenerator {
	@Override
	public void generateStartBlockCode(Block target, CodeGeneratorContext context) {
		if (context.getHost().getEnableInstrumentation()
				&& context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {

			Span contentSpan = (Span) CollectionHelper.firstOrDefault(target.getChildren(),
					s -> ((Span) s).getKind() == SpanKind.Code || ((Span) s).getKind() == SpanKind.Markup);

			if (contentSpan != null) {
				context.AddContextCall(contentSpan,
						context.getHost().getGeneratedClassContext().getBeginContextMethodName(), false);
			}
		}


		// methods are not converted
		String writeInvocation = context.BuildCodeString(cw -> {
			if (context.getHost().getDesignTimeMode()) {
				context.EnsureExpressionHelperVariable();
				cw.writeStartAssignment("__o");
			} else if (context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
				if (!StringHelper.isNullOrEmpty(context.getTargetWriterName())) {
					cw.writeStartMethodInvoke(context.getHost().getGeneratedClassContext().getWriteToMethodName());
					cw.writeSnippet(context.getTargetWriterName());
					cw.writeParameterSeparator();
				} else {
					cw.writeStartMethodInvoke(context.getHost().getGeneratedClassContext().getWriteMethodName());
				}
			}
		});

		context.BufferStatementFragment(writeInvocation);
		context.MarkStartOfGeneratedCode();
	}

	@Override
	public void generateEndBlockCode(Block target, CodeGeneratorContext context) {

		// methods are not converted
		String endBlock = context.BuildCodeString(cw -> {
			if (context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
				if (!context.getHost().getDesignTimeMode()) {
					cw.writeEndMethodInvoke();
				}
				cw.writeEndStatement();
			} else {
				cw.writeLineContinuation();
			}
		});

		context.MarkEndOfGeneratedCode();
		context.BufferStatementFragment(endBlock);
		context.FlushBufferedStatement();

		if (context.getHost().getEnableInstrumentation()
				&& context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
			Span contentSpan = (Span) CollectionHelper.firstOrDefault(target.getChildren(),
					s -> ((Span) s).getKind() == SpanKind.Code || ((Span) s).getKind() == SpanKind.Markup);// .FirstOrDefault();

			if (contentSpan != null) {
				context.AddContextCall(contentSpan,
						context.getHost().getGeneratedClassContext().getendContextMethodName(), false);
			}
		}
	}

	@Override
	public void generateCode(Span target, CodeGeneratorContext context) {
		Span sourceSpan = null;
		if (context.CreateCodeWriter().getSupportsMidStatementLinePragmas()
				|| context.getExpressionRenderingMode() == ExpressionRenderingMode.WriteToOutput) {
			sourceSpan = target;
		}
		context.BufferStatementFragment(target.getContent(), sourceSpan);
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