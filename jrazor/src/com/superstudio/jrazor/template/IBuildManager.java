package com.superstudio.jrazor.template;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Created by Chaoqun on 2015/11/8.
 */

public interface IBuildManager
{
    boolean fileExists(String virtualPath);
    java.lang.Class getCompiledType(String virtualPath);
    Collection getReferencedPackages();
    InputStream readCachedFile(String fileName);
    OutputStream createCachedFile(String fileName);
}
