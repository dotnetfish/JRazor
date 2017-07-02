package com.superstudio.web.razor.generator;

import com.superstudio.commons.csharpbridge.StringHelper;
import org.apache.commons.lang3.StringUtils;


public final class GeneratedClassContext {
	public static final String DefaultWriteMethodName = "write";
	public static final String DefaultWriteLiteralMethodName = "writeLiteral";
	public static final String DefaultExecuteMethodName = "execute";
	public static final String DefaultLayoutPropertyName = "layout";
	public static final String DefaultWriteAttributeMethodName = "writeAttribute";
	public static final String DefaultWriteAttributeToMethodName = "writeAttributeTo";

	public static final GeneratedClassContext Default = new GeneratedClassContext(DefaultExecuteMethodName,
			DefaultWriteMethodName, DefaultWriteLiteralMethodName);

	public GeneratedClassContext(String executeMethodName, String writeMethodName, String writeLiteralMethodName) {
		this();
		if (StringUtils.isBlank(executeMethodName)) {
			// throw new IllegalArgumentException(String.format(
			// CommonResources.getArgument_Cannot_Be_Null_Or_Empty(),
			// "executeMethodName"), "executeMethodName");
		}
		if (StringUtils.isBlank(writeMethodName)) {
			// throw new IllegalArgumentException(String.format(
			// CommonResources.getArgument_Cannot_Be_Null_Or_Empty(),
			// "writeMethodName"), "writeMethodName");
		}
		if (StringUtils.isBlank(writeLiteralMethodName)) {
			// throw new IllegalArgumentException(String.format(
			// CommonResources.getArgument_Cannot_Be_Null_Or_Empty(),
			// "writeLiteralMethodName"), "writeLiteralMethodName");
		}

		setWriteMethodName(writeMethodName);
		setWriteLiteralMethodName(writeLiteralMethodName);
		setExecuteMethodName(executeMethodName);

		setWriteToMethodName(null);
		setWriteLiteralToMethodName(null);
		setTemplateTypeName(null);
		setDefineSectionMethodName(null);

		setLayoutPropertyName(DefaultLayoutPropertyName);
		setWriteAttributeMethodName(DefaultWriteAttributeMethodName);
		setWriteAttributeToMethodName(DefaultWriteAttributeToMethodName);
	}

	public GeneratedClassContext(String executeMethodName, String writeMethodName, String writeLiteralMethodName,
			String writeToMethodName, String writeLiteralToMethodName, String templateTypeName) {
		this(executeMethodName, writeMethodName, writeLiteralMethodName);
		setWriteToMethodName(writeToMethodName);
		setWriteLiteralToMethodName(writeLiteralToMethodName);
		setTemplateTypeName(templateTypeName);
	}

	public GeneratedClassContext(String executeMethodName, String writeMethodName, String writeLiteralMethodName,
			String writeToMethodName, String writeLiteralToMethodName, String templateTypeName,
			String defineSectionMethodName) {
		this(executeMethodName, writeMethodName, writeLiteralMethodName, writeToMethodName, writeLiteralToMethodName,
				templateTypeName);
		setDefineSectionMethodName(defineSectionMethodName);
	}

	public GeneratedClassContext(String executeMethodName,
								 String writeMethodName,
								 String writeLiteralMethodName,
			String writeToMethodName,
								 String writeLiteralToMethodName,
								 String templateTypeName,
			String defineSectionMethodName,
								 String beginContextMethodName,
								 String endContextMethodName) {
		this(executeMethodName, writeMethodName, writeLiteralMethodName, writeToMethodName, writeLiteralToMethodName,
				templateTypeName, defineSectionMethodName);
		setBeginContextMethodName(beginContextMethodName);
		setendContextMethodName(endContextMethodName);
	}

	public GeneratedClassContext() {
		// TODO Auto-generated constructor stub
	}

	private String privateWriteMethodName;

	public String getWriteMethodName() {
		return privateWriteMethodName;
	}

	private void setWriteMethodName(String value) {
		privateWriteMethodName = value;
	}

	private String privateWriteLiteralMethodName;

	public String getWriteLiteralMethodName() {
		return privateWriteLiteralMethodName;
	}

	private void setWriteLiteralMethodName(String value) {
		privateWriteLiteralMethodName = value;
	}

	private String privateWriteToMethodName;

	public String getWriteToMethodName() {
		return privateWriteToMethodName;
	}

	private void setWriteToMethodName(String value) {
		privateWriteToMethodName = value;
	}

	private String privateWriteLiteralToMethodName;

	public String getWriteLiteralToMethodName() {
		return privateWriteLiteralToMethodName;
	}

	private void setWriteLiteralToMethodName(String value) {
		privateWriteLiteralToMethodName = value;
	}

	private String privateExecuteMethodName;

	public String getExecuteMethodName() {
		return privateExecuteMethodName;
	}

	private void setExecuteMethodName(String value) {
		privateExecuteMethodName = value;
	}

	// Optional Items
	private String privateBeginContextMethodName;

	public String getBeginContextMethodName() {
		return privateBeginContextMethodName;
	}

	public void setBeginContextMethodName(String value) {
		privateBeginContextMethodName = value;
	}

	private String privateendContextMethodName;

	public String getendContextMethodName() {
		return privateendContextMethodName;
	}

	public void setendContextMethodName(String value) {
		privateendContextMethodName = value;
	}

	private String privateLayoutPropertyName;

	public String getLayoutPropertyName() {
		return privateLayoutPropertyName;
	}

	public void setLayoutPropertyName(String value) {
		privateLayoutPropertyName = value;
	}

	private String privateDefineSectionMethodName;

	public String getDefineSectionMethodName() {
		return privateDefineSectionMethodName;
	}

	public void setDefineSectionMethodName(String value) {
		privateDefineSectionMethodName = value;
	}

	private String privateTemplateTypeName;

	public String getTemplateTypeName() {
		return privateTemplateTypeName;
	}

	public void setTemplateTypeName(String value) {
		privateTemplateTypeName = value;
	}

	private String privateWriteAttributeMethodName;

	public String getWriteAttributeMethodName() {
		return privateWriteAttributeMethodName;
	}

	public void setWriteAttributeMethodName(String value) {
		privateWriteAttributeMethodName = value;
	}

	private String privateWriteAttributeToMethodName;

	public String getWriteAttributeToMethodName() {
		return privateWriteAttributeToMethodName;
	}

	public void setWriteAttributeToMethodName(String value) {
		privateWriteAttributeToMethodName = value;
	}

	
	private String privateResolveUrlMethodName;

	public String getResolveUrlMethodName() {
		return privateResolveUrlMethodName;
	}

	public void setResolveUrlMethodName(String value) {
		privateResolveUrlMethodName = value;
	}

	public boolean getAllowSections() {
		return !StringUtils.isBlank(getDefineSectionMethodName());
	}

	public boolean getAllowTemplates() {
		return !StringUtils.isBlank(getTemplateTypeName());
	}

	public boolean getSupportsInstrumentation() {
		return !StringUtils.isBlank(getBeginContextMethodName())
				&& !StringUtils.isBlank(getendContextMethodName());
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof GeneratedClassContext)) {
			return false;
		}
		GeneratedClassContext other = (GeneratedClassContext) obj;
		return StringUtils.equals(getDefineSectionMethodName(), other.getDefineSectionMethodName())
				&& StringUtils.equals(getWriteMethodName(), other.getWriteMethodName())
				&& StringUtils.equals(getWriteLiteralMethodName(), other.getWriteLiteralMethodName())
				&& StringUtils.equals(getWriteToMethodName(), other.getWriteToMethodName())
				&& StringUtils.equals(getWriteLiteralToMethodName(),
						other.getWriteLiteralToMethodName())
				&& StringUtils.equals(getExecuteMethodName(), other.getExecuteMethodName())
				&& StringUtils.equals(getTemplateTypeName(), other.getTemplateTypeName())
				&& StringUtils.equals(getBeginContextMethodName(), other.getBeginContextMethodName())
				&& StringUtils.equals(getendContextMethodName(), other.getendContextMethodName());
	}

	@Override
	public int hashCode() {
		// TODO: Use HashCodeCombiner
		return getDefineSectionMethodName().hashCode() ^ getWriteMethodName().hashCode()
				^ getWriteLiteralMethodName().hashCode() ^ getWriteToMethodName().hashCode()
				^ getWriteLiteralToMethodName().hashCode() ^ getExecuteMethodName().hashCode()
				^ getTemplateTypeName().hashCode() ^ getBeginContextMethodName().hashCode()
				^ getendContextMethodName().hashCode();
	}

	public static boolean OpEquality(GeneratedClassContext left, GeneratedClassContext right) {
		return left.equals(right);
	}

	public static boolean OpInequality(GeneratedClassContext left, GeneratedClassContext right) {
		return !left.equals(right);
	}
}