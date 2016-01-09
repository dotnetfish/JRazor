package com.superstudio.jrazor;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.ResourceManager;

 
public class RazorWebResources
{
	public static final String Could_Not_Locate_FactoryType = "Could_Not_Locate_FactoryType";
	public static final String BuildProvider_No_CodeLanguageService_For_Path = "BuildProvider_No_CodeLanguageService_For_Path";
	private static ResourceManager resourceMan;
	private static CultureInfo resourceCulture;
 
	public static ResourceManager getResourceManager()
	{
		//if (Object.ReferenceEquals(RazorWebResources.resourceMan, null))
		{
			//ResourceManager resourceManager = new ResourceManager("System.Web.WebPages.Razor.Resources.RazorWebResources", 
				//	RazorWebResources.class.getPackage());
			//RazorWebResources.resourceMan = resourceManager;
		}
		return RazorWebResources.resourceMan;
	}
 
	public static CultureInfo getCulture()
	{
		return RazorWebResources.resourceCulture;
	}
	public static void setCulture(CultureInfo value)
	{
		RazorWebResources.resourceCulture = value;
	}
	public static String getBuildProvider_No_CodeLanguageService_For_Path()
	{
		return RazorWebResources.getResourceManager().GetString("BuildProvider_No_CodeLanguageService_For_Path", RazorWebResources.resourceCulture);
	}
	public static String getCould_Not_Locate_FactoryType()
	{
		return RazorWebResources.getResourceManager().GetString("Could_Not_Locate_FactoryType", RazorWebResources.resourceCulture);
	}
	public RazorWebResources()
	{
	}
}