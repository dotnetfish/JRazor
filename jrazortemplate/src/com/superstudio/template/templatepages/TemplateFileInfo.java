package com.superstudio.template.templatepages;

public class TemplateFileInfo {
	 private final String _virtualPath;

     public TemplateFileInfo(String virtualPath)
     {
         _virtualPath = virtualPath;
     }

     public String getVirtualPath()
     {
        return _virtualPath;
     }
}
