package com.superstudio.jrazor.generator;

import com.superstudio.codedom.CodeNamespaceImport;
import com.superstudio.commons.CollectionHelper;
import com.superstudio.commons.HashCodeCombiner;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.jrazor.parser.syntaxTree.Span;
import com.superstudio.jrazor.text.LocationTagged;



public class AddImportCodeGenerator extends SpanCodeGenerator {
	public AddImportCodeGenerator(String ns, int namespaceKeywordLength) {
		setNamespace(ns);
		setNamespaceKeywordLength(namespaceKeywordLength);
	}

	public AddImportCodeGenerator(LocationTagged<String> getContent, int usingkeywordlength) {
		setNamespace(getContent.getValue());
		setNamespaceKeywordLength(usingkeywordlength);
	}

	private String privateNamespace;

	public final String getNamespace() {
		return privateNamespace;
	}

	private void setNamespace(String value) {
		privateNamespace = value;
	}

	private int privateNamespaceKeywordLength;

	public final int getNamespaceKeywordLength() {
		return privateNamespaceKeywordLength;
	}

	public final void setNamespaceKeywordLength(int value) {
		privateNamespaceKeywordLength = value;
	}

	@Override
	public void generateCode(Span target, CodeGeneratorContext context) {
		// Try to find the namespace in the existing imports
		String ns = getNamespace();
		if (!StringHelper.isNullOrEmpty(ns) && Character.isWhitespace(ns.charAt(0))) {
			ns = ns.substring(1);
		}
		final String tempNS = ns;
		Object obj=CollectionHelper.firstOrDefault(context.getNamespace().getImports(),
				i -> i.getNamespace().equals(tempNS.trim()));
		
		CodeNamespaceImport importNamespace = null;

		if (obj == null) {
			// It doesn't exist, create it
			importNamespace = new CodeNamespaceImport(ns);
			context.getNamespace().getImports().Add(importNamespace);
		}else{
			importNamespace = new CodeNamespaceImport(ns);
		}

		// Attach our info to the existing/new import.
		importNamespace.setLinePragma(context.generateLinePragma(target));
	}

	@Override
	public String toString() {
		return "Import:" + getNamespace() + ";KwdLen:" + getNamespaceKeywordLength();
	}

	@Override
	public boolean equals(Object obj) {
		AddImportCodeGenerator other = (AddImportCodeGenerator) ((obj instanceof AddImportCodeGenerator) ? obj : null);
		return other != null && getNamespace().equals(other.getNamespace())
				&& getNamespaceKeywordLength() == other.getNamespaceKeywordLength();
	}

	@Override
	public int hashCode() {
		return HashCodeCombiner.Start().Add(getNamespace()).Add(getNamespaceKeywordLength()).getCombinedHash();
	}
}