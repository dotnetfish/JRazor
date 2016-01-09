package com.superstudio.jrazor.template;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Created by Chaoqun on 2015/11/8.
 */

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
        return JavaBuildManager.getReferencedPackages();
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