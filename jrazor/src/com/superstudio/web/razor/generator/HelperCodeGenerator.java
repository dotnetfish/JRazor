package com.superstudio.web.razor.generator;

import com.superstudio.codedom.CodeLinePragma;
import com.superstudio.codedom.CodeSnippetTypeMember;
import com.superstudio.commons.Environment;
import com.superstudio.commons.HashCodeCombiner;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.web.razor.parser.syntaxTree.Block;
import com.superstudio.web.razor.text.LocationTagged;
import org.apache.commons.lang3.StringUtils;


public class HelperCodeGenerator extends BlockCodeGenerator {
	private static final String HelperWriterName = "__razor_helper_writer";

	private CodeWriter _writer;
	private String _oldWriter;
	private AutoCloseable _statementCollectorToken;

	public HelperCodeGenerator(LocationTagged<String> signature, boolean headerComplete) {
		setSignature(signature);
		setHeaderComplete(headerComplete);
	}

	private LocationTagged<String> privateSignature;

	public final LocationTagged<String> getSignature() {
		return privateSignature;
	}

	private void setSignature(LocationTagged<String> value) {
		privateSignature = value;
	}

	private LocationTagged<String> privateFooter;

	public final LocationTagged<String> getFooter() {
		return privateFooter;
	}

	public final void setFooter(LocationTagged<String> value) {
		privateFooter = value;
	}

	private boolean privateHeaderComplete;

	public final boolean getHeaderComplete() {
		return privateHeaderComplete;
	}

	private void setHeaderComplete(boolean value) {
		privateHeaderComplete = value;
	}

	@Override
	public void generateStartBlockCode(Block target, CodeGeneratorContext context) throws Exception {
		_writer = context.CreateCodeWriter();


		// methods are not converted
		String prefix = context.buildCodeString(
				cw -> cw.writeHelperHeaderPrefix(context.getHost().getGeneratedClassContext().getTemplateTypeName(),
						context.getHost().getStaticHelpers()));

		_writer.writeLinePragma(context.generateLinePragma(getSignature().getLocation().clone(), prefix.length(),
				getSignature().getValue().length()));
		_writer.writeSnippet(prefix);
		_writer.writeSnippet(getSignature().toString());
		if (getHeaderComplete()) {
			_writer.writeHelperHeaderSuffix(context.getHost().getGeneratedClassContext().getTemplateTypeName());
		}
		_writer.writeLinePragma(null);
		if (getHeaderComplete()) {
			_writer.writeReturn();
			_writer.writeStartConstructor(context.getHost().getGeneratedClassContext().getTemplateTypeName());
			_writer.writeStartLambdaDelegate(HelperWriterName);
		}

		_statementCollectorToken = context.changeStatementCollector((p, m) -> AddStatementToHelper(p, m));
		_oldWriter = context.getTargetWriterName();
		context.setTargetWriterName(HelperWriterName);
	}

	@Override
	public void generateEndBlockCode(Block target, CodeGeneratorContext context) throws Exception {
		_statementCollectorToken.close();
		if (getHeaderComplete()) {
			_writer.writeEndLambdaDelegate();
			_writer.writeEndConstructor();
			_writer.writeEndStatement();
		}
		if (getFooter() != null && !StringUtils.isBlank(getFooter().getValue())) {
			_writer.writeLinePragma(context.generateLinePragma(getFooter().getLocation().clone(), 0,
					getFooter().getValue().length()));
			_writer.writeSnippet(getFooter().toString());
			_writer.writeLinePragma();
		}
		_writer.writeHelperTrailer();

		context.getGeneratedClass().getMembers().add(new CodeSnippetTypeMember(_writer.getContent()));
		context.setTargetWriterName(_oldWriter);
	}

	@Override
	public boolean equals(Object obj) {
		HelperCodeGenerator other = (HelperCodeGenerator) ((obj instanceof HelperCodeGenerator) ? obj : null);
		return other != null && super.equals(other) && getHeaderComplete() == other.getHeaderComplete()
				&& equals(getSignature(), other.getSignature());
	}

	@Override
	public int hashCode() {
		return HashCodeCombiner.Start().Add(super.hashCode()).Add(getSignature()).getCombinedHash();
	}

	@Override
	public String toString()
	{
		//return "Helper:" + getSignature().toString("F") + ";" + (getHeaderComplete() ? "C" : "I");
		return "Helper:" + getSignature().toString() + ";" + (getHeaderComplete() ? "C" : "I");
	}

	private void AddStatementToHelper(String statement, CodeLinePragma pragma) {
		if (pragma != null) {
			_writer.writeLinePragma(pragma);
		}
		_writer.writeSnippet(statement);
		_writer.getInnerWriter().write(Environment.NewLine);

		if (pragma != null) {
			_writer.writeLinePragma();
		}
	}

	@Override
	public boolean equals(Object obj, Object others) {
		// TODO Auto-generated method stub
		return false;
	}
}