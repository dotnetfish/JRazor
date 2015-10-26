package com.superstudio.commons;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Date;

import com.superstudio.commons.io.CacheDependency;

//
// 摘要:
//     Provides a set of methods that enable a Web application to retrieve resources
//     from a virtual file system.

//public abstract class VirtualPathProvider {
	public  class VirtualPathProvider {
	//
	// 摘要:
	// Initializes the class for use by an inherited class instance. This
	// constructor
	// can be called only by an inherited class.
	protected VirtualPathProvider() {

	}

	//
	// 摘要:
	// Gets a reference to a previously registered
	// System.Web.Hosting.VirtualPathProvider
	// object in the compilation system.
	//
	// 返回结果:
	// The next System.Web.Hosting.VirtualPathProvider object in the compilation
	// system.
	private VirtualPathProvider previous;

	//
	// 摘要:
	// Returns a stream from a virtual file.
	//
	// 参数:
	// virtualPath:
	// The path to the virtual file.
	//
	// 返回结果:
	// A read-only System.IO.Stream object for the specified virtual file or
	// resource.
	public static InputStream OpenFile(String virtualPath) throws FileNotFoundException {
		return new FileInputStream(Paths.get(virtualPath).toAbsolutePath().toString());
	}

	//
	// 摘要:
	// Combines a base path with a relative path to return a complete path to a
	// virtual
	// resource.
	//
	// 参数:
	// basePath:
	// The base path for the application.
	//
	// relativePath:
	// The path to the virtual resource, relative to the base path.
	//
	// 返回结果:
	// The complete path to a virtual resource.
	public String CombineVirtualPaths(String basePath, String relativePath) {
		return Paths.get(basePath,relativePath).toAbsolutePath().toString();
	}

	//
	// 摘要:
	// Gets a value that indicates whether a directory exists in the virtual
	// file system.
	//
	// 参数:
	// virtualDir:
	// The path to the virtual directory.
	//
	// 返回结果:
	// true if the directory exists in the virtual file system; otherwise,
	// false.
	public boolean DirectoryExists(String virtualDir) {
		return true;
	}

	//
	// 摘要:
	// Gets a value that indicates whether a file exists in the virtual file
	// system.
	//
	// 参数:
	// virtualPath:
	// The path to the virtual file.
	//
	// 返回结果:
	// true if the file exists in the virtual file system; otherwise, false.
	public boolean fileExists(String virtualPath) {
		
		String root=getClass().getResource("/").toString();
		String url=Paths.get(root).toFile().getParentFile().toString()+
				virtualPath.replace("~","").toLowerCase();
	File file=new File(url);
		Boolean result=file.exists();
		//String relative
		Trace.WriteLine(url);
		Trace.WriteLine("----------------------------");
		//Trace.WriteLine(" virtualpath="+getClass().getResource("/").getPath().+ "  result="+result);
		//Trace.WriteLine(" virtualpath="+result+"  result"+Paths.get("./jua/WEB-INF/views/home/index.jhtml").toFile().exists());
		//Trace.WriteLine(" virtualpath=/jua/WEB-INF/views/home/index.jhtml  result"+Paths.get("/jua/WEB-INF/views/home/index.jhtml").toFile().exists());
		
		return result;
	}

	//
	// 摘要:
	// Creates a cache dependency based on the specified virtual paths.
	//
	// 参数:
	// virtualPath:
	// The path to the primary virtual resource.
	//
	// virtualPathDependencies:
	// An array of paths to other resources required by the primary virtual
	// resource.
	//
	// utcStart:
	// The UTC time at which the virtual resources were read.
	//
	// 返回结果:
	// A System.Web.Caching.CacheDependency object for the specified virtual
	// resources.
	public CacheDependency GetCacheDependency(String virtualPath, Iterable virtualPathDependencies, Date utcStart) {
		return new CacheDependency();
	}

	//
	// 摘要:
	// Returns a cache key to use for the specified virtual path.
	//
	// 参数:
	// virtualPath:
	// The path to the virtual resource.
	//
	// 返回结果:
	// A cache key for the specified virtual resource.
	public String GetCacheKey(String virtualPath) {
		return virtualPath.hashCode() + virtualPath.replace('/', '_');
	}

	//
	// 摘要:
	// Gets a virtual directory from the virtual file system.
	//
	// 参数:
	// virtualDir:
	// The path to the virtual directory.
	//
	// 返回结果:
	// A descendent of the System.Web.Hosting.VirtualDirectory class that
	// represents
	// a directory in the virtual file system.
	public VirtualDirectory GetDirectory(String virtualDir) {
		File file = Paths.get(virtualDir).toFile();
		if (file.exists() && file.isDirectory())
			return new VirtualDirectory(virtualDir);
		return null;
	}

	//
	// 摘要:
	// Gets a virtual file from the virtual file system.
	//
	// 参数:
	// virtualPath:
	// The path to the virtual file.
	//
	// 返回结果:
	// A descendent of the System.Web.Hosting.VirtualFile class that represents
	// a file
	// in the virtual file system.
	public VirtualFile GetFile(String virtualPath) {
		return new VirtualFile(virtualPath);
	}

	//
	// 摘要:
	// Returns a hash of the specified virtual paths.
	//
	// 参数:
	// virtualPath:
	// The path to the primary virtual resource.
	//
	// virtualPathDependencies:
	// An array of paths to other virtual resources required by the primary
	// virtual
	// resource.
	//
	// 返回结果:
	// A hash of the specified virtual paths.
	public String getFileHash(String virtualPath, Iterable virtualPathDependencies) {
		return String.valueOf(Paths.get(virtualPath).toFile().hashCode());
	}

	//
	// 摘要:
	// Gives the System.Web.Hosting.VirtualPathProvider object an infinite
	// lifetime
	// by preventing a lease from being created.
	//
	// 返回结果:
	// Always null.
	public Object InitializeLifetimeService() {
		return null;
	}

	//
	// 摘要:
	// Initializes the System.Web.Hosting.VirtualPathProvider instance.
	protected void Initialize() {

	}

	public VirtualPathProvider getPrevious() {
		return previous;
	}

	public void setPrevious(VirtualPathProvider previous) {
		this.previous = previous;
	}
}
