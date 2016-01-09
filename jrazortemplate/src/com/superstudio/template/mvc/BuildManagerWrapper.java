package com.superstudio.template.mvc;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;



public final class BuildManagerWrapper implements IBuildManager
{
	public boolean fileExists(String virtualPath)
	{
		return JavaBuildManager.getObjectFactory(virtualPath, false) != null;
	}

	public java.lang.Class getCompiledType(String virtualPath)
	{
		return JavaBuildManager.getCompiledType(virtualPath);
	}

	public Collection getReferencedPackages()
	{
		return JavaBuildManager.getReferencedAssemblies();
	}

	public InputStream readCachedFile(String fileName)
	{
		return JavaBuildManager.readCachedFile(fileName);
	}

	public OutputStream createCachedFile(String fileName)
	{
		return JavaBuildManager.createCachedFile(fileName);
	}
}