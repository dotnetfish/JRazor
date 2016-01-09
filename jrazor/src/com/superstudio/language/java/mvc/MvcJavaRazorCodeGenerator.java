package com.superstudio.language.java.mvc;

import com.superstudio.codedom.CodeTypeReference;
import com.superstudio.language.java.JavaRazorCodeGenerator;
import com.superstudio.web.mvc.razor.*;
import com.superstudio.web.razor.RazorEngineHost;


public class MvcJavaRazorCodeGenerator extends JavaRazorCodeGenerator
{
	//private static final String DefaultModelTypeName = "dynamic";
	public MvcJavaRazorCodeGenerator(String className, String rootNamespaceName, String sourceFileName, RazorEngineHost host) throws Exception
	{
		super(className, rootNamespaceName, sourceFileName, host);
		MvcWebPageRazorHost mvcWebPageRazorHost = (MvcWebPageRazorHost)((host instanceof MvcWebPageRazorHost) ? host : null);
		if (mvcWebPageRazorHost != null && !mvcWebPageRazorHost.getIsSpecialPage())
		{
			this.setBaseType("dynamic");
		}
	}
	private void setBaseType(String modelTypeName)
	{
		CodeTypeReference value = new CodeTypeReference(super.getContext().getHost().getDefaultBaseClass() + "<" + modelTypeName + ">");
		super.getContext().getGeneratedClass().getBaseTypes().Clear();
		super.getContext().getGeneratedClass().getBaseTypes().Add(value);
	}
}