package com.superstudio.jrazor.generator;

import com.superstudio.commons.HashCodeCombiner;
import com.superstudio.jrazor.parser.syntaxTree.*;
import com.superstudio.jrazor.text.*;



public class LiteralAttributeCodeGenerator extends SpanCodeGenerator {
	public LiteralAttributeCodeGenerator(LocationTagged<String> prefix,
			LocationTagged<SpanCodeGenerator> valueGenerator) {
		setPrefix(prefix);
		setValueGenerator(valueGenerator);
	}

	// TODO how to resolve gennic type conflict;

	/*
	 * public LiteralAttributeCodeGenerator(LocationTagged<String> prefix,
	 * LocationTagged<String> value) { setPrefix(prefix); setValue(value); }
	 */

	private LiteralAttributeCodeGenerator() {
		// TODO Auto-generated constructor stub
	}

	public static LiteralAttributeCodeGenerator create(LocationTagged<String> prefix, LocationTagged<String> value) {
		LiteralAttributeCodeGenerator instance = new LiteralAttributeCodeGenerator();
		instance.setPrefix(prefix);
		instance.setValue(value);
		return instance;
	}

	private LocationTagged<String> privatePrefix;

	public final LocationTagged<String> getPrefix() {
		return privatePrefix;
	}

	private void setPrefix(LocationTagged<String> value) {
		privatePrefix = value;
	}

	private LocationTagged<String> privateValue;

	public final LocationTagged<String> getValue() {
		return privateValue;
	}

	private void setValue(LocationTagged<String> value) {
		privateValue = value;
	}

	private LocationTagged<SpanCodeGenerator> privateValueGenerator;

	public final LocationTagged<SpanCodeGenerator> getValueGenerator() {
		return privateValueGenerator;
	}

	private void setValueGenerator(LocationTagged<SpanCodeGenerator> value) {
		privateValueGenerator = value;
	}

	@Override
	public void generateCode(Span target, CodeGeneratorContext context) {
		if (context.getHost().getDesignTimeMode()) {
			return;
		}
		ExpressionRenderingMode oldMode = context.getExpressionRenderingMode();
		// literal: true - This attribute value is a literal value
		// In VB, we need a line continuation
		 
		 
		context.bufferStatementFragment(context.buildCodeString(cw -> {
			cw.writeParameterSeparator();
			cw.WriteStartMethodInvoke("Tuple.Create");
			cw.writeLocationTaggedString(getPrefix());
			cw.writeParameterSeparator();
			if (getValueGenerator() != null) {
				cw.WriteStartMethodInvoke("Tuple.Create", "System.Object", "System.Int32");
				context.setExpressionRenderingMode(ExpressionRenderingMode.InjectCode);
			} else {
				cw.writeLocationTaggedString(getValue());
				cw.writeParameterSeparator();
				cw.writeBooleanLiteral(true);
				cw.WriteEndMethodInvoke();
				cw.writeLineContinuation();
			}
		}));
		if (getValueGenerator() != null) {
			getValueGenerator().getValue().generateCode(target, context);
			context.flushBufferedStatement();
			context.setExpressionRenderingMode(oldMode);
			// literal: false - This attribute value is not a literal value, it
			// is dynamically generated
			// In VB, we need a line continuation
			 
			 
			context.addStatement(context.buildCodeString(cw -> {
				cw.writeParameterSeparator();
				cw.writeSnippet((new Integer(getValueGenerator().getLocation().getAbsoluteIndex())).toString());
				cw.WriteEndMethodInvoke();
				cw.writeParameterSeparator();
				cw.writeBooleanLiteral(false);
				cw.WriteEndMethodInvoke();
				cw.writeLineContinuation();
			}));
		} else {
			context.flushBufferedStatement();
		}
	}

	@Override
	public String toString() {
		if (getValueGenerator() == null) {
			return String.format("LitAttr:{0:F},{1:F}", getPrefix(), getValue());
		} else {
			return String.format("LitAttr:{0:F},<Sub:{1:F}>", getPrefix(), getValueGenerator());
		}
	}

	@Override
	public boolean equals(Object obj) {
		LiteralAttributeCodeGenerator other = (LiteralAttributeCodeGenerator) ((obj instanceof LiteralAttributeCodeGenerator)
				? obj : null);
		// return other != null && equals(other.getPrefix(), getPrefix()) &&
		// equals(other.getValue(), getValue()) &&
		// equals(other.getValueGenerator(), getValueGenerator());
		return other != null && other.getPrefix().equals(getPrefix()) && other.getValue().equals(getValue())
				&& other.getValueGenerator().equals(getValueGenerator());

	}

	@Override
	public int hashCode() {
		return HashCodeCombiner.Start().Add(getPrefix()).Add(getValue()).Add(getValueGenerator()).getCombinedHash();
	}
}