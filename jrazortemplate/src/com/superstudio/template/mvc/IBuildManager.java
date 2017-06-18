package com.superstudio.template.mvc;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Stream;



public interface IBuildManager
{
	boolean fileExists(String virtualPath);
	java.lang.Class getCompiledType(String virtualPath) throws Exception;
	Collection getReferencedPackages();
	InputStream readCachedFile(String fileName);
	OutputStream createCachedFile(String fileName);
}