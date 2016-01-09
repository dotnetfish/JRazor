package com.superstudio.commons;

public class HostingEnvironment {

	//public static  VirtualPathProvider VirtualPathProvider = null;

	public static boolean getIsHosted() {
		// TODO Auto-generated method stub
		return false;
	}

	public static String MapPath(String virtualPath) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String MapPathInternal(VirtualPath virtualPath) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String MapPath(VirtualPath virtualPath) {
		// TODO Auto-generated method stub
		return null;
	}

	public static VirtualPathProvider getVirtualPathProvider() {
		// TODO Auto-generated method stub
		/*return new VirtualPathProvider(){
			
		};*/
		try{
			return (VirtualPathProvider)Class.forName("com.superstudio.web.WebVirtualPathProvider").newInstance();
		}catch(Exception ex){
			 return new VirtualPathProvider(){
				
			};
		}
		
	}

	public static String MapPathInternal(VirtualPath virtualPath, boolean permitNull) {
		// TODO Auto-generated method stub
		return null;
	}

	public static String MapPathInternal(VirtualPath virtualPath, VirtualPath baseVirtualDir,
			boolean allowCrossAppMapping) {
		// TODO Auto-generated method stub
		return null;
	}

}
