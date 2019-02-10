package com.superstudio.commons;

import com.superstudio.commons.exception.HttpException;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public final class VirtualPath implements  Serializable {
    private String _appRelativeVirtualPath;
    private String _virtualPath;
    private static final int isWithinAppRootComputed = 1;
    private static final int isWithinAppRoot = 2;
    private static final int appRelativeAttempted = 4;

    private SimpleBitVector32 flags;
    //public static VirtualPath RootVirtualPath = VirtualPath.Create("/");

    public String getVirtualPathString() {
        if (this._virtualPath == null) {
            if (HttpRuntime.getAppDomainAppVirtualPathObject() == null) {
                //TODO
                //throw new HttpException(Resource.getString("VirtualPath_CantMakeAppAbsolute", new Object[] {this._appRelativeVirtualPath}));
            }
            if (this._appRelativeVirtualPath.length() == 1) {
                this._virtualPath = HttpRuntime.getAppDomainAppVirtualPath();
            } else {
                this._virtualPath = HttpRuntime.getAppDomainAppVirtualPathString() + this._appRelativeVirtualPath.substring(2);
            }
        }
        return this._virtualPath;
    }

    /*public String getVirtualPathStringNoTrailingSlash() {
        return UrlPath.RemoveSlashFromPathIfNeeded(this.getVirtualPathString());
    }*/

   /* public String getVirtualPathStringIfAvailable() {
        return this._virtualPath;
    }
*/
    /*public String getAppRelativeVirtualPathStringOrNull() throws HttpException {
        if (this._appRelativeVirtualPath == null) {
            if (this.flags.get(4)) {
                return null;
            }
            if (HttpRuntime.getAppDomainAppVirtualPathObject() == null) {
                throw new HttpException(Resource.getString("VirtualPath_CantMakeAppRelative", new Object[]{this._virtualPath}));
            }
            this._appRelativeVirtualPath = UrlPath.MakeVirtualPathAppRelativeOrNull(this._virtualPath);
            this.flags.set(4, true); //= true;
            if (this._appRelativeVirtualPath == null) {
                return null;
            }
        }
        return this._appRelativeVirtualPath;
    }
*/
    /*public String getAppRelativeVirtualPathString() throws HttpException {
        String appRelativeVirtualPathStringOrNull = this.getAppRelativeVirtualPathStringOrNull();
        if (appRelativeVirtualPathStringOrNull == null) {
            return this._virtualPath;
        }
        return appRelativeVirtualPathStringOrNull;
    }

    public String getAppRelativeVirtualPathStringIfAvailable() {
        return this._appRelativeVirtualPath;
    }

    public String getVirtualPathStringWhicheverAvailable() {
        if (this._virtualPath == null) {
            return this._appRelativeVirtualPath;
        }
        return this._virtualPath;
    }
*/
   /* public String getExtension() {
        return UrlPath.GetExtension(this.getVirtualPathString());
    }*/

   /* public String getFileName() {
        return UrlPath.GetFileName(this.getVirtualPathStringNoTrailingSlash());
    }*/

    /*public boolean getHasTrailingSlash() {
        if (this._virtualPath != null) {
            return UrlPath.HasTrailingSlash(this._virtualPath);
        }
        return UrlPath.HasTrailingSlash(this._appRelativeVirtualPath);
    }

    public boolean getIsWithinAppRoot() {
        if (!this.flags.get(1)) {
            if (HttpRuntime.getAppDomainIdInternal() == null) {
                return true;
            }
            if (this.flags.get(4)) {
                this.flags.set(2, this._appRelativeVirtualPath != null);
            } else {
                this.flags.set(2, UrlPath.IsEqualOrSubpath(HttpRuntime.getAppDomainAppVirtualPathString(), this.getVirtualPathString()));//2] = ;
            }
            this.flags.set(1, true);//1] = true;
        }
        return this.flags.get(2);//[2];
    }

    public boolean getIsRelative() {
        return this._virtualPath != null && this._virtualPath.charAt(0) != '/';
    }
*/
  /*  public boolean getIsRoot() {
        return this._virtualPath.equals("/");
    }
*/
    /*public VirtualPath getParent() {
        this.FailIfRelativePath();
        if (this.getIsRoot()) {
            return null;
        }
        String text = this.getVirtualPathStringWhicheverAvailable();
        text = UrlPath.RemoveSlashFromPathIfNeeded(text);
        if (text.equals("~")) {
            text = this.getVirtualPathStringNoTrailingSlash();
        }
        int num = text.lastIndexOf('/');
        if (num == 0) {
            return VirtualPath.RootVirtualPath;
        }
        text = text.substring(0, num + 1);
        return new VirtualPath(text);
    }
*/
    private VirtualPath() {
    }
/*

    private VirtualPath(String virtualPath) {
        if (UrlPath.IsAppRelativePath(virtualPath)) {
            this._appRelativeVirtualPath = virtualPath;
            return;
        }
        this._virtualPath = virtualPath;
    }
*/

  /*  public int compareTo(Object obj) {
        VirtualPath virtualPath = (VirtualPath) ((obj instanceof VirtualPath) ? obj : null);
        if (virtualPath == null) {
            throw new IllegalArgumentException();
        }
        if (virtualPath == this) {
            return 0;
        }
        return StringUtils.equalsIgnoreCase(this.getVirtualPathString(), virtualPath.getVirtualPathString()) ? 0 : 1;
    }
*/
   /* public VirtualPath CombineWithAppRoot() {
        return HttpRuntime.getAppDomainAppVirtualPathObject().Combine(this);
    }*/

   /* public VirtualPath Combine(VirtualPath relativePath) {
        if (relativePath == null) {
            throw new IllegalArgumentException("relativePath");
        }
        if (!relativePath.getIsRelative()) {
            return relativePath;
        }
        this.FailIfRelativePath();
        return new VirtualPath(UrlPath.Combine(this.getVirtualPathStringWhicheverAvailable(), relativePath.getVirtualPathString()));
    }*/

   /* public VirtualPath SimpleCombine(String relativePath) {
        return this.SimpleCombine(relativePath, false);
    }

    public VirtualPath SimpleCombineWithDir(String directoryName) {
        return this.SimpleCombine(directoryName, true);
    }

    private VirtualPath SimpleCombine(String filename, boolean addTrailingSlash) {
        String text = this.getVirtualPathStringWhicheverAvailable() + filename;
        if (addTrailingSlash) {
            text += "/";
        }
        VirtualPath expr_22 = new VirtualPath(text);
        expr_22.CopyFlagsFrom(this, 7);
        return expr_22;
    }

    public VirtualPath MakeRelative(VirtualPath toVirtualPath) {
        VirtualPath arg_11_0 = new VirtualPath();
        this.FailIfRelativePath();
        toVirtualPath.FailIfRelativePath();
        arg_11_0._virtualPath = UrlPath.MakeRelative(this.getVirtualPathString(), toVirtualPath.getVirtualPathString());
        return arg_11_0;
    }

    public String MapPath() {
        return HostingEnvironment.MapPath(this);
    }

    public String MapPathInternal() {
        return HostingEnvironment.MapPathInternal(this);
    }

    public String MapPathInternal(boolean permitNull) {
        return HostingEnvironment.MapPathInternal(this, permitNull);
    }

    public String MapPathInternal(VirtualPath baseVirtualDir, boolean allowCrossAppMapping) {
        return HostingEnvironment.MapPathInternal(this, baseVirtualDir, allowCrossAppMapping);
    }
*/
    /*public String GetFileHash(Iterable virtualPathDependencies)
    {
        return HostingEnvironment.getVirtualPathProvider().GetFileHash(this, virtualPathDependencies);
    }*/
    /*public CacheDependency GetCacheDependency(Iterable virtualPathDependencies, java.time.LocalDateTime utcStart)
	{
		return HostingEnvironment.getVirtualPathProvider().GetCacheDependency(this, virtualPathDependencies, utcStart);
	}*/
	/*public boolean fileExists()
	{
		return HostingEnvironment.getVirtualPathProvider().fileExists(this);
	}
	public boolean DirectoryExists()
	{
		return HostingEnvironment.getVirtualPathProvider().DirectoryExists(this);
	}
	public VirtualFile GetFile()
	{
		return HostingEnvironment.getVirtualPathProvider().GetFile(this);
	}
	public VirtualDirectory GetDirectory()
	{
		return HostingEnvironment.getVirtualPathProvider().GetDirectory(this);
	}
	public String GetCacheKey()
	{
		return HostingEnvironment.getVirtualPathProvider().GetCacheKey(this);
	}
	public Stream OpenFile()
	{
		return VirtualPathProvider.OpenFile(this);
	}*/
    /*public void FailIfNotWithinAppRoot() {
        if (!this.getIsWithinAppRoot()) {
            throw new IllegalArgumentException(Resource.getString("Cross_app_not_allowed", new Object[]{this.getVirtualPathString()}));
        }
    }*/

    /*public void FailIfRelativePath() {
        if (this.getIsRelative()) {
            throw new IllegalArgumentException(Resource.getString("VirtualPath_AllowRelativePath", new Object[]{this._virtualPath}));
        }
    }*/

    /*public static VirtualPath Combine(VirtualPath v1, VirtualPath v2) {
        if (v1 == null) {
            v1 = HttpRuntime.getAppDomainAppVirtualPathObject();
        }
        if (v1 == null) {
            v2.FailIfRelativePath();
            return v2;
        }
        return v1.Combine(v2);
    }*/

    public static boolean equals(VirtualPath v1, VirtualPath v2) {
        return v1 == v2 || (v1 != null && v2 != null && VirtualPath.equalsHelper(v1, v2));
    }

    @Override
    public boolean equals(Object value) {
        if (value == null) {
            return false;
        }
        VirtualPath virtualPath = (VirtualPath) ((value instanceof VirtualPath) ? value : null);
        return virtualPath != null && VirtualPath.equalsHelper(virtualPath, this);
    }

    private static boolean equalsHelper(VirtualPath v1, VirtualPath v2) {
        return StringUtils.equalsIgnoreCase(v1.getVirtualPathString(), v2.getVirtualPathString());
    }

    @Override
    public int hashCode() {
        return this.getVirtualPathString().toLowerCase().hashCode();
        //return StringHelper.HashCode(this.getVirtualPathString(),StringComparison.OrdinalIgnoreCase);
    }

    @Override
    public String toString() {
        if (this._virtualPath == null && HttpRuntime.getAppDomainAppVirtualPathObject() == null) {
            return this._appRelativeVirtualPath;
        }
        return this.getVirtualPathString();
    }

    private void CopyFlagsFrom(VirtualPath virtualPath, int mask) {
        this.flags.setIntegerValue((this.flags.getIntegerValue() | (virtualPath.flags.getIntegerValue() & mask)));
    }

    public static String GetVirtualPathString(VirtualPath virtualPath) throws HttpException {
        if (!(virtualPath == null)) {
            return virtualPath.getVirtualPathString();
        }
        return null;
    }

    /*public static String GetVirtualPathStringNoTrailingSlash(VirtualPath virtualPath) {
        if (!(virtualPath == null)) {
            return virtualPath.getVirtualPathStringNoTrailingSlash();
        }
        return null;
    }*/

    /*public static String GetAppRelativeVirtualPathString(VirtualPath virtualPath) throws HttpException {
        if (!(virtualPath == null)) {
            return virtualPath.getAppRelativeVirtualPathString();
        }
        return null;
    }*/

   /* public static String GetAppRelativeVirtualPathStringOrEmpty(VirtualPath virtualPath) throws HttpException {
        if (!(virtualPath == null)) {
            return virtualPath.getAppRelativeVirtualPathString();
        }
        return "";
    }*/

    /*public static VirtualPath Create(String virtualPath) {
        return VirtualPath.Create(virtualPath, VirtualPathOptions.AllowAllPath);
    }*/

    /*public static VirtualPath CreateTrailingSlash(String virtualPath) {
        return VirtualPath.Create(virtualPath, VirtualPathOptions.EnsureTrailingSlash | VirtualPathOptions.AllowAbsolutePath | VirtualPathOptions.AllowAppRelativePath | VirtualPathOptions.AllowRelativePath);
    }

    public static VirtualPath CreateAllowNull(String virtualPath) {
        return VirtualPath.Create(virtualPath, VirtualPathOptions.AllowNull | VirtualPathOptions.AllowAbsolutePath | VirtualPathOptions.AllowAppRelativePath | VirtualPathOptions.AllowRelativePath);
    }

    public static VirtualPath CreateAbsolute(String virtualPath) {
        return VirtualPath.Create(virtualPath, VirtualPathOptions.AllowAbsolutePath);
    }*/

   /* public static VirtualPath CreateNonRelative(String virtualPath) {
        return VirtualPath.Create(virtualPath, VirtualPathOptions.AllowAbsolutePath | VirtualPathOptions.AllowAppRelativePath);
    }

    public static VirtualPath CreateAbsoluteTrailingSlash(String virtualPath) {
        return VirtualPath.Create(virtualPath, VirtualPathOptions.EnsureTrailingSlash | VirtualPathOptions.AllowAbsolutePath);
    }

    public static VirtualPath CreateNonRelativeTrailingSlash(String virtualPath) {
        return VirtualPath.Create(virtualPath, VirtualPathOptions.EnsureTrailingSlash | VirtualPathOptions.AllowAbsolutePath | VirtualPathOptions.AllowAppRelativePath);
    }
*/
    /*public static VirtualPath CreateAbsoluteAllowNull(String virtualPath) {
        return VirtualPath.Create(virtualPath, VirtualPathOptions.AllowNull | VirtualPathOptions.AllowAbsolutePath);
    }

    public static VirtualPath CreateNonRelativeAllowNull(String virtualPath) {
        return VirtualPath.Create(virtualPath, VirtualPathOptions.AllowNull | VirtualPathOptions.AllowAbsolutePath | VirtualPathOptions.AllowAppRelativePath);
    }

    public static VirtualPath CreateNonRelativeTrailingSlashAllowNull(String virtualPath) {
        return VirtualPath.Create(virtualPath, VirtualPathOptions.AllowNull | VirtualPathOptions.EnsureTrailingSlash | VirtualPathOptions.AllowAbsolutePath | VirtualPathOptions.AllowAppRelativePath);
    }
*/
   /* public static VirtualPath Create(String virtualPath, int options) {
        return new VirtualPath("F:\\Razor");//"F:\\Razor";
			*//*if (virtualPath != null)
			{
				virtualPath = virtualPath.trim();
			}
			if (!StringUtils.isBlank(virtualPath))
			{
				boolean flag = false;
				boolean flag2 = false;
				int length = virtualPath.length();
				final String text=virtualPath; 
				//fixed (String text = virtualPath)
				{
					//char* ptr = text;
					char[] ptr=text.toCharArray();
					if (ptr != null)
					{
						ptr += length / 2;
					}
					for (int i = 0; i < length; i++)
					{
						char c = ptr[(IntPtr)i];
						if (c <= '.')
						{
							if (c == '\0')
							{
								//throw new HttpException(Resource.getString("Invalid_vpath", new object[] { virtualPath }));
							}
							if (c == '.')
							{
								flag2 = true;
							}
						}
						else
						{
							if (c != '/')
							{
								if (c == '\\')
								{
									flag = true;
								}
							}
							else
							{
								if (i > 0 && *(ushort*)(ptr + (IntPtr)(i - 1)) == 47)
								{
									flag = true;
								}
							}
						}
					}
				}
				if (flag)
				{
					if ((options & VirtualPathOptions.FailIfMalformed) != (VirtualPathOptions)0)
					{
						throw new HttpException(Resource.getString("Invalid_vpath", new object[] { virtualPath }));
					}
					virtualPath = UrlPath.FixVirtualPathSlashes(virtualPath);
				}
				if ((options & VirtualPathOptions.EnsureTrailingSlash) != (VirtualPathOptions)0)
				{
					virtualPath = UrlPath.AppendSlashToPathIfNeeded(virtualPath);
				}
				VirtualPath virtualPath2 = new VirtualPath();
				if (UrlPath.IsAppRelativePath(virtualPath))
				{
					if (flag2)
					{
						virtualPath = UrlPath.ReduceVirtualPath(virtualPath);
					}
					if (virtualPath[0] == '~')
					{
						if ((options & VirtualPathOptions.AllowAppRelativePath) == (VirtualPathOptions)0)
						{
							throw new ArgumentException(Resource.getString("VirtualPath_AllowAppRelativePath", new object[] { virtualPath }));
						}
						virtualPath2._appRelativeVirtualPath = virtualPath;
					}
					else
					{
						if ((options & VirtualPathOptions.AllowAbsolutePath) == (VirtualPathOptions)0)
						{
							throw new ArgumentException(Resource.getString("VirtualPath_AllowAbsolutePath", new object[] { virtualPath }));
						}
						virtualPath2._virtualPath = virtualPath;
					}
				}
				else
				{
					if (virtualPath[0] != '/')
					{
						if ((options & VirtualPathOptions.AllowRelativePath) == (VirtualPathOptions)0)
						{
							throw new ArgumentException(Resource.getString("VirtualPath_AllowRelativePath", new object[] { virtualPath }));
						}
						virtualPath2._virtualPath = virtualPath;
					}
					else
					{
						if ((options & VirtualPathOptions.AllowAbsolutePath) == (VirtualPathOptions)0)
						{
							throw new ArgumentException(Resource.getString("VirtualPath_AllowAbsolutePath", new object[] { virtualPath }));
						}
						if (flag2)
						{
							virtualPath = UrlPath.ReduceVirtualPath(virtualPath);
						}
						virtualPath2._virtualPath = virtualPath;
					}
				}
				return virtualPath2;
			}
			if ((options & VirtualPathOptions.AllowNull) != (VirtualPathOptions)0)
			{
				return null;
			}
			throw new ArgumentNullException("virtualPath");*//*
    }
*/}