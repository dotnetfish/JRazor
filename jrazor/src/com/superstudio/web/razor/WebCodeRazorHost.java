package com.superstudio.web.razor;

import com.superstudio.codedom.CodeMemberProperty;
import com.superstudio.codedom.MemberAttributes;
import com.superstudio.commons.Path;
import com.superstudio.commons.csharpbridge.StringComparison;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.web.razor.generator.CodeGeneratorContext;
import com.superstudio.web.razor.parser.ParserHelpers;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

public class WebCodeRazorHost extends WebPageRazorHost {
	private static final String AppCodeDir = "App_Code";
	private static final String HttpContextAccessorName = "Context";
	// private static final String _helperPageBaseType =
	// HelperPage.class.getName();
	private static final String _helperPageBaseType = "helperClass";

	public WebCodeRazorHost(String virtualPath) {
		super(virtualPath);
		this.setDefaultBaseClass(WebCodeRazorHost._helperPageBaseType);
		this.setDefaultNamespace(WebCodeRazorHost.determineNamespace(virtualPath));
		super.setDefaultDebugCompilation(false);
		this.setStaticHelpers(true);
	}

	public WebCodeRazorHost(String virtualPath, String physicalPath) {
		super(virtualPath, physicalPath);
		this.setDefaultBaseClass(WebCodeRazorHost._helperPageBaseType);
		this.setDefaultNamespace(WebCodeRazorHost.determineNamespace(virtualPath));
		super.setDefaultDebugCompilation(false);
		this.setStaticHelpers(true);
	}

	@Override
	public void postProcessGeneratedCode(CodeGeneratorContext context) {
		super.postProcessGeneratedCode(context);
		context.getGeneratedClass().getMembers().Remove(context.getTargetMethod());
		/*CodeMemberProperty codeMemberProperty = (CodeMemberProperty) CollectionHelper.firstOrDefault(
				context.getGeneratedClass().getMembers(),
				(p) -> "ApplicationInstance".equals(((CodeMemberProperty) p).getName()));
		;*/
		Optional<CodeMemberProperty> codeMemberProperty1= context.getGeneratedClass().getMembers().stream().filter(p->"ApplicationInstance".equals(((CodeMemberProperty) p).getName())).findFirst();
		CodeMemberProperty codeMemberProperty=codeMemberProperty1.get();
		if (codeMemberProperty != null) {
			codeMemberProperty.setAttributes(
					MemberAttributes.forValue(codeMemberProperty.getAttributes().getValue() | MemberAttributes.Static));
		}
	}

	@Override
	protected String getClassName(String virtualPath) {
		return ParserHelpers.sanitizeClassName(Path.GetFileNameWithoutExtension(virtualPath));
	}

	private static String determineNamespace(String virtualPath) {
		virtualPath = virtualPath.replace(java.io.File.separatorChar, '/');
		virtualPath = WebCodeRazorHost.getDirectory(virtualPath);
		int num = StringUtils.indexOfIgnoreCase(virtualPath, "App_Code");
		if (num != -1) {
			virtualPath = virtualPath.substring(num + "App_Code".length());
		}
		// String[] enumerable =StringHelper.split(virtualPath,new char[]
		// {'/'});
		String[] enumerable = virtualPath.split("/");
		// Iterable<String> enumerable =virtualPath.split("/");
		// if (!enumerable.<String>Any())
		if (enumerable == null || enumerable.length == 0) {
			return "jrazor";
		}
		return "jrazor." + StringUtils.join(enumerable,".");
	}

	private static String getDirectory(String virtualPath) {
		int num = virtualPath.lastIndexOf('/');
		if (num != -1) {
			return virtualPath.substring(0, num);
		}
		return "";
	}
}