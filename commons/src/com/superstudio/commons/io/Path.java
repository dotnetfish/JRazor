package com.superstudio.commons.io;

import java.util.UUID;

import com.superstudio.commons.Environment;
import com.superstudio.commons.csharpbridge.StringHelper;


public final class Path
{
	public static final char DirectorySeparatorChar = '\\';
	public static final String DirectorySeparatorCharAsString = "\\";
	public static final char AltDirectorySeparatorChar = '/';
	public static final char VolumeSeparatorChar = ':';
	//@Deprecated
	//public static final char[] InvalidPathChars = new char[] {'"', '<','>', '|','\0','\u0001','\u0002','\u0003','\u0004','\u0005','\u0006','\b','\t','\n','\f','\r','\u000e','\u000f','\u0010','\u0011','\u0012','\u0013','\u0014','\u0015','\u0016','\u0017','\u0018','\u0019','\u001a','\u001b','\u001c','\u001d','\u001e','\u001f'};
	public static final char[] TrimEndChars = new char[] {'\t','\n','\f','\r',' ','\u0085','\u00a0'};
	private static final char[] RealInvalidPathChars = new char[] {'"','<','>','|','\0','\u0001','\u0002','\u0003','\u0004','\u0005','\u0006','\b','\t','\n','\f','\r','\u000e','\u000f','\u0010','\u0011','\u0012','\u0013','\u0014','\u0015','\u0016','\u0017','\u0018','\u0019','\u001a','\u001b','\u001c','\u001d','\u001e','\u001f'};
	private static final char[] InvalidPathCharsWithAdditionalChecks = new char[] {'"','<','>','|','\0','\u0001','\u0002','\u0003','\u0004','\u0005','\u0006','\b','\t','\n','\f','\r','\u000e','\u000f','\u0010','\u0011','\u0012','\u0013','\u0014','\u0015','\u0016','\u0017','\u0018','\u0019','\u001a','\u001b','\u001c','\u001d','\u001e','\u001f','*','?'};
	private static final char[] InvalidFileNameChars = new char[] {'"','<','>','|','\0','\u0001','\u0002','\u0003','\u0004','\u0005','\u0006','\b','\t','\n','\f','\r','\u000e','\u000f','\u0010','\u0011','\u0012','\u0013','\u0014','\u0015','\u0016','\u0017','\u0018','\u0019','\u001a','\u001b','\u001c','\u001d','\u001e','\u001f',':','*','?','\\','/'};
	public static final char PathSeparator = ';';
	public static final int MaxPath = 260;
	private static final int MaxDirectoryLength = 255;
	public static final int MAX_PATH = 260;
	public static final int MAX_DIRECTORY_PATH = 248;
	public static final int MaxLongPath = 32000;
	private static final String LongPathPrefix = "\\\\?\\";
	private static final String UNCPathPrefix = "\\\\";
	private static final String UNCLongPathPrefixToInsert = "?\\UNC\\";
	private static final String UNCLongPathPrefix = "\\\\?\\UNC\\";
	private static final char[] s_Base32Char = new char[] {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5'};
	public static String ChangeExtension(String path, String extension)
	{
		if (path != null)
		{
			Path.CheckInvalidPathChars(path, false);
			String text = path;
			int num = path.length();
			while (--num >= 0)
			{
				char c = path.charAt(num);
				if (c == '.')
				{
					text = path.substring(0, num);
					break;
				}
				if (c == java.io.File.separatorChar || c == Path.AltDirectorySeparatorChar || c == Path.VolumeSeparatorChar)
				{
					break;
				}
			}
			if (extension != null && path.length() != 0)
			{
				if (extension.length() == 0 || extension.charAt(0) != '.')
				{
					text += ".";
				}
				text += extension;
			}
			return text;
		}
		return null;
	}
	public static String GetDirectoryName(String path)
	{
		if (path != null)
		{
			Path.CheckInvalidPathChars(path, false);
			String text = Path.NormalizePath(path, false);
			if (path.length() > 0)
			{
				try
				{
					String text2 = Path.RemoveLongPathPrefix(path);
					int num = 0;
					while (num < text2.length() && text2.charAt(num) != '?' && text2.charAt(num) != '*')
					{
						num++;
					}
					if (num > 0)
					{
						(new java.io.File(text2.substring(0, num))).getAbsolutePath();
					}
				}
				catch (SecurityException e)
				{
					if (path.indexOf("~") != -1)
					{
						text = Path.NormalizePath(path, false, false);
					}
				/*}
				catch (PathTooLongException e2)
				{*/
				}
				catch (UnsupportedOperationException e3)
				{
				}
				/*catch (IOException e4)
				{
				}*/
				catch (IllegalArgumentException e5)
				{
				}
			}
			path = text;
			int rootLength = Path.GetRootLength(path);
			int num2 = path.length();
			if (num2 > rootLength)
			{
				num2 = path.length();
				if (num2 == rootLength)
				{
					return null;
				}
				while (num2 > rootLength && path.charAt(--num2) != java.io.File.separatorChar && path.charAt(num2) != Path.AltDirectorySeparatorChar)
				{
				}
				return path.substring(0, num2);
			}
		}
		return null;
	}
	public static int GetRootLength(String path)
	{
		Path.CheckInvalidPathChars(path, false);
		int i = 0;
		int length = path.length();
		if (length >= 1 && Path.IsDirectorySeparator(path.charAt(0)))
		{
			i = 1;
			if (length >= 2 && Path.IsDirectorySeparator(path.charAt(1)))
			{
				i = 2;
				int num = 2;
				while (i < length)
				{
					if ((path.charAt(i) == java.io.File.separatorChar || path.charAt(i) == Path.AltDirectorySeparatorChar) && --num <= 0)
					{
						break;
					}
					i++;
				}
			}
		}
		else
		{
			if (length >= 2 && path.charAt(1) == Path.VolumeSeparatorChar)
			{
				i = 2;
				if (length >= 3 && Path.IsDirectorySeparator(path.charAt(2)))
				{
					i++;
				}
			}
		}
		return i;
	}
	public static boolean IsDirectorySeparator(char c)
	{
		return c == java.io.File.separatorChar || c == Path.AltDirectorySeparatorChar;
	}
 
//ORIGINAL LINE: [__DynamicallyInvokable] public static char[] GetInvalidPathChars()
	public static char[] GetInvalidPathChars()
	{
		return (char[])Path.RealInvalidPathChars.clone();
	}
 
//ORIGINAL LINE: [__DynamicallyInvokable] public static char[] GetInvalidFileNameChars()
	public static char[] GetInvalidFileNameChars()
	{
		return (char[])Path.InvalidFileNameChars.clone();
	}
 
//ORIGINAL LINE: [__DynamicallyInvokable] public static string GetExtension(string path)
	public static String GetExtension(String path)
	{
		if (path == null)
		{
			return null;
		}
		Path.CheckInvalidPathChars(path, false);
		int length = path.length();
		int num = length;
		while (--num >= 0)
		{
			char c = path.charAt(num);
			if (c == '.')
			{
				if (num != length - 1)
				{
					return path.substring(num, length);
				}
				return "";
			}
			else
			{
				if (c == java.io.File.separatorChar || c == Path.AltDirectorySeparatorChar || c == Path.VolumeSeparatorChar)
				{
					break;
				}
			}
		}
		return "";
	}
 
//ORIGINAL LINE: [__DynamicallyInvokable, SecuritySafeCritical] public static string GetFullPath(string path)
	public static String GetFullPath(String path)
	{
		String fullPathInternal = Path.GetFullPathInternal(path);
	//	FileIOPermission.QuickDemand(FileIOPermissionAccess.PathDiscovery, fullPathInternal, false, false);
		return fullPathInternal;
	}
 
//ORIGINAL LINE: [SecurityCritical] internal static string UnsafeGetFullPath(string path)
	public static String UnsafeGetFullPath(String path)
	{
		String fullPathInternal = Path.GetFullPathInternal(path);
		//FileIOPermission.QuickDemand(FileIOPermissionAccess.PathDiscovery, fullPathInternal, false, false);
		return fullPathInternal;
	}
	public static String GetFullPathInternal(String path)
	{
		if (path == null)
		{
			throw new IllegalArgumentException("path");
		}
		return Path.NormalizePath(path, true);
	}
 
//ORIGINAL LINE: [SecuritySafeCritical] internal static string NormalizePath(string path, bool fullCheck)
	public static String NormalizePath(String path, boolean fullCheck)
	{
		return Path.NormalizePath(path, fullCheck, Path.MaxPath);
	}
 
//ORIGINAL LINE: [SecuritySafeCritical] internal static string NormalizePath(string path, bool fullCheck, bool expandShortPaths)
	public static String NormalizePath(String path, boolean fullCheck, boolean expandShortPaths)
	{
		return Path.NormalizePath(path, fullCheck, Path.MaxPath, expandShortPaths);
	}
	public static String NormalizePath(String path, boolean fullCheck, int maxPathLength)
	{
		return Path.NormalizePath(path, fullCheck, maxPathLength, true);
	}
	 
	static String NormalizePath(String path, boolean fullCheck, int maxPathLength, boolean expandShortPaths){
		return path;
	}
//C# TO JAVA CONVERTER TODO TASK: C# 'unsafe' code is not converted by C# to Java Converter:
 
//ORIGINAL LINE: [SecurityCritical] internal unsafe static string NormalizePath(string path, bool fullCheck, int maxPathLength, bool expandShortPaths)
//	  static string NormalizePath(string path, bool fullCheck, int maxPathLength, bool expandShortPaths)
//		{
//			if (fullCheck)
//			{
//				path = path.TrimEnd(Path.TrimEndChars);
//				Path.CheckInvalidPathChars(path, false);
//			}
//			int i = 0;
//			PathHelper pathHelper;
//			if (path.Length + 1 <= Path.MaxPath)
//			{
//				char* charArrayPtr = stackalloc char[(UIntPtr)Path.MaxPath];
//				pathHelper = new PathHelper(charArrayPtr, Path.MaxPath);
//			}
//			else
//			{
//				pathHelper = new PathHelper(path.Length + Path.MaxPath, maxPathLength);
//			}
//			uint num = 0u;
//			uint num2 = 0u;
//			bool flag = false;
//			uint num3 = 0u;
//			int num4 = -1;
//			bool flag2 = false;
//			bool flag3 = true;
//			int num5 = 0;
//			bool flag4 = false;
//			if (path.Length > 0 && (path[0] == Path.DirectorySeparatorChar || path[0] == Path.AltDirectorySeparatorChar))
//			{
//				pathHelper.Append('\\');
//				i++;
//				num4 = 0;
//			}
//			while (i < path.Length)
//			{
//				char c = path[i];
//				if (c == Path.DirectorySeparatorChar || c == Path.AltDirectorySeparatorChar)
//				{
//					if (num3 == 0u)
//					{
//						if (num2 > 0u)
//						{
//							int num6 = num4 + 1;
//							if (path[num6] != '.')
//							{
//								throw new ArgumentException(Environment.GetResourceString("Arg_PathIllegal"));
//							}
//							if (num2 >= 2u)
//							{
//								if (flag2 && num2 > 2u)
//								{
//									throw new ArgumentException(Environment.GetResourceString("Arg_PathIllegal"));
//								}
//								if (path[num6 + 1] == '.')
//								{
//									int num7 = num6 + 2;
//									while ((long)num7 < (long)num6 + (long)((ulong)num2))
//									{
//										if (path[num7] != '.')
//										{
//											throw new ArgumentException(Environment.GetResourceString("Arg_PathIllegal"));
//										}
//										num7++;
//									}
//									num2 = 2u;
//								}
//								else
//								{
//									if (num2 > 1u)
//									{
//										throw new ArgumentException(Environment.GetResourceString("Arg_PathIllegal"));
//									}
//									num2 = 1u;
//								}
//							}
//							if (num2 == 2u)
//							{
//								pathHelper.Append('.');
//							}
//							pathHelper.Append('.');
//							flag = false;
//						}
//						if ((num > 0u & flag3) && i + 1 < path.Length && (path[i + 1] == Path.DirectorySeparatorChar || path[i + 1] == Path.AltDirectorySeparatorChar))
//						{
//							pathHelper.Append(Path.DirectorySeparatorChar);
//						}
//					}
//					num2 = 0u;
//					num = 0u;
//					if (!flag)
//					{
//						flag = true;
//						pathHelper.Append(Path.DirectorySeparatorChar);
//					}
//					num3 = 0u;
//					num4 = i;
//					flag2 = false;
//					flag3 = false;
//					if (flag4)
//					{
//						pathHelper.TryExpandShortFileName();
//						flag4 = false;
//					}
//					int expr_20C = pathHelper.Length - 1;
//					if (expr_20C - num5 > Path.MaxDirectoryLength)
//					{
//						throw new PathTooLongException(Environment.GetResourceString("IO.PathTooLong"));
//					}
//					num5 = expr_20C;
//				}
//				else
//				{
//					if (c == '.')
//					{
//						num2 += 1u;
//					}
//					else
//					{
//						if (c == ' ')
//						{
//							num += 1u;
//						}
//						else
//						{
//							if (c == '~' & expandShortPaths)
//							{
//								flag4 = true;
//							}
//							flag = false;
//							if (flag3 && c == Path.VolumeSeparatorChar)
//							{
//								char c2 = (i > 0) ? path[i - 1] : ' ';
//								if (num2 != 0u || num3 < 1u || c2 == ' ')
//								{
//									throw new ArgumentException(Environment.GetResourceString("Arg_PathIllegal"));
//								}
//								flag2 = true;
//								if (num3 > 1u)
//								{
//									int num8 = 0;
//									while (num8 < pathHelper.Length && pathHelper[num8] == ' ')
//									{
//										num8++;
//									}
//									if ((ulong)num3 - (ulong)((long)num8) == 1uL)
//									{
//										pathHelper.Length = 0;
//										pathHelper.Append(c2);
//									}
//								}
//								num3 = 0u;
//							}
//							else
//							{
//								num3 += 1u + num2 + num;
//							}
//							if (num2 > 0u || num > 0u)
//							{
//								int num9 = (num4 >= 0) ? (i - num4 - 1) : i;
//								if (num9 > 0)
//								{
//									for (int j = 0; j < num9; j++)
//									{
//										pathHelper.Append(path[num4 + 1 + j]);
//									}
//								}
//								num2 = 0u;
//								num = 0u;
//							}
//							pathHelper.Append(c);
//							num4 = i;
//						}
//					}
//				}
//				i++;
//			}
//			if (pathHelper.Length - 1 - num5 > Path.MaxDirectoryLength)
//			{
//				throw new PathTooLongException(Environment.GetResourceString("IO.PathTooLong"));
//			}
//			if (num3 == 0u && num2 > 0u)
//			{
//				int num10 = num4 + 1;
//				if (path[num10] != '.')
//				{
//					throw new ArgumentException(Environment.GetResourceString("Arg_PathIllegal"));
//				}
//				if (num2 >= 2u)
//				{
//					if (flag2 && num2 > 2u)
//					{
//						throw new ArgumentException(Environment.GetResourceString("Arg_PathIllegal"));
//					}
//					if (path[num10 + 1] == '.')
//					{
//						int num11 = num10 + 2;
//						while ((long)num11 < (long)num10 + (long)((ulong)num2))
//						{
//							if (path[num11] != '.')
//							{
//								throw new ArgumentException(Environment.GetResourceString("Arg_PathIllegal"));
//							}
//							num11++;
//						}
//						num2 = 2u;
//					}
//					else
//					{
//						if (num2 > 1u)
//						{
//							throw new ArgumentException(Environment.GetResourceString("Arg_PathIllegal"));
//						}
//						num2 = 1u;
//					}
//				}
//				if (num2 == 2u)
//				{
//					pathHelper.Append('.');
//				}
//				pathHelper.Append('.');
//			}
//			if (pathHelper.Length == 0)
//			{
//				throw new ArgumentException(Environment.GetResourceString("Arg_PathIllegal"));
//			}
//			if (fullCheck && (pathHelper.OrdinalStartsWith("http:", false) || pathHelper.OrdinalStartsWith("file:", false)))
//			{
//				throw new ArgumentException(Environment.GetResourceString("Argument_PathUriFormatNotSupported"));
//			}
//			if (flag4)
//			{
//				pathHelper.TryExpandShortFileName();
//			}
//			int num12 = 1;
//			if (fullCheck)
//			{
//				num12 = pathHelper.GetFullPathName();
//				flag4 = false;
//				int num13 = 0;
//				while (num13 < pathHelper.Length && !flag4)
//				{
//					if (pathHelper[num13] == '~' & expandShortPaths)
//					{
//						flag4 = true;
//					}
//					num13++;
//				}
//				if (flag4 && !pathHelper.TryExpandShortFileName())
//				{
//					int num14 = -1;
//					for (int k = pathHelper.Length - 1; k >= 0; k--)
//					{
//						if (pathHelper[k] == Path.DirectorySeparatorChar)
//						{
//							num14 = k;
//							break;
//						}
//					}
//					if (num14 >= 0)
//					{
//						if (pathHelper.Length >= maxPathLength)
//						{
//							throw new PathTooLongException(Environment.GetResourceString("IO.PathTooLong"));
//						}
//						int lenSavedName = pathHelper.Length - num14 - 1;
//						pathHelper.Fixup(lenSavedName, num14);
//					}
//				}
//			}
//			if (num12 != 0 && pathHelper.Length > 1 && pathHelper[0] == '\\' && pathHelper[1] == '\\')
//			{
//				int l;
//				for (l = 2; l < num12; l++)
//				{
//					if (pathHelper[l] == '\\')
//					{
//						l++;
//						break;
//					}
//				}
//				if (l == num12)
//				{
//					throw new ArgumentException(Environment.GetResourceString("Arg_PathIllegalUNC"));
//				}
//				if (pathHelper.OrdinalStartsWith("\\\\?\\globalroot", true))
//				{
//					throw new ArgumentException(Environment.GetResourceString("Arg_PathGlobalRoot"));
//				}
//			}
//			if (pathHelper.Length >= maxPathLength)
//			{
//				throw new PathTooLongException(Environment.GetResourceString("IO.PathTooLong"));
//			}
//			if (num12 == 0)
//			{
//				int num15 = Marshal.GetLastWin32Error();
//				if (num15 == 0)
//				{
//					num15 = 161;
//				}
//				__Error.WinIOError(num15, path);
//				return null;
//			}
//			string text = pathHelper.ToString();
//			if (string.Equals(text, path, StringComparison.Ordinal))
//			{
//				text = path;
//			}
//			return text;
//		}
	public static boolean HasLongPathPrefix(String path)
	{
		return path.startsWith("\\\\?\\");
	}
	public static String AddLongPathPrefix(String path)
	{
		if (path.startsWith("\\\\?\\"))
		{
			return path;
		}
		if (path.startsWith("\\\\"))
		{
			return path.substring(0,2)+ "?\\UNC\\"+path.substring(2,path.length());
		}
		return "\\\\?\\" + path;
	}
	public static String RemoveLongPathPrefix(String path)
	{
		if (!path.startsWith("\\\\?\\"))
		{
			return path;
		}
		if (path.toUpperCase().startsWith("\\\\?\\UNC\\"))
		{
			return StringHelper.remove(path, 2, 6);
		}
		return path.substring(4);
	}
	public static StringBuilder RemoveLongPathPrefix(StringBuilder pathSB)
	{
		String text = pathSB.toString();
		if (!text.startsWith("\\\\?\\"))
		{
			return pathSB;
		}
		if (text.toUpperCase().startsWith("\\\\?\\UNC\\"))
		{
			return pathSB.delete(2, 8);
		}
		return pathSB.delete(0, 4);
	}
 
//ORIGINAL LINE: [__DynamicallyInvokable] public static string GetFileName(string path)
	public static String GetFileName(String path)
	{
		if (path != null)
		{
			Path.CheckInvalidPathChars(path, false);
			int length = path.length();
			int num = length;
			while (--num >= 0)
			{
				char c = path.charAt(num);
				if (c == java.io.File.separatorChar || c == Path.AltDirectorySeparatorChar || c == Path.VolumeSeparatorChar)
				{
					return path.substring(num + 1, num + 1 + length - num - 1);
				}
			}
		}
		return path;
	}
 
//ORIGINAL LINE: [__DynamicallyInvokable] public static string GetFileNameWithoutExtension(string path)
	public static String GetFileNameWithoutExtension(String path)
	{
		path = (new java.io.File(path)).getName();
		if (path == null)
		{
			return null;
		}
		int length;
		if ((length = path.lastIndexOf('.')) == -1)
		{
			return path;
		}
		return path.substring(0, length);
	}
 
//ORIGINAL LINE: [__DynamicallyInvokable] public static string GetPathRoot(string path)
	public static String GetPathRoot(String path)
	{
		if (path == null)
		{
			return null;
		}
		path = Path.NormalizePath(path, false);
		return path.substring(0, Path.GetRootLength(path));
	}
 
//ORIGINAL LINE: [__DynamicallyInvokable, SecuritySafeCritical] public static string GetTempPath()
	public static String GetTempPath()
	{
		//(new EnvironmentPermission(PermissionState.Unrestricted)).Demand();
		StringBuilder stringBuilder = new StringBuilder(260);
		//boolean arg_28_0 = Win32Native.GetTempPath(260, stringBuilder) != 0;
		String path = stringBuilder.toString();
		/*if (!arg_28_0)
		{
			__Error.WinIOError();
		}*/
		return Path.GetFullPathInternal(path);
	}
	public static boolean IsRelative(String path)
	{
		return (path.length() < 3 || path.charAt(1) != Path.VolumeSeparatorChar || path.charAt(2) != java.io.File.separatorChar || ((path.charAt(0) < 'a' || path.charAt(0) > 'z') && (path.charAt(0) < 'A' || path.charAt(0) > 'Z'))) && (path.length() < 2 || path.charAt(0) != '\\' || path.charAt(1) != '\\');
	}
 
//ORIGINAL LINE: [__DynamicallyInvokable] public static string GetRandomFileName()
	public static String GetRandomFileName()
	{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: byte[] array = new byte[10];
		/*byte[] array = new byte[10];
		RNGCryptoServiceProvider rNGCryptoServiceProvider = null;
		String result;
		try
		{
			rNGCryptoServiceProvider = new RNGCryptoServiceProvider();
			rNGCryptoServiceProvider.GetBytes(array);
			char[] expr_22 = Path.ToBase32StringSuitableForDirName(array).toCharArray();
			expr_22[8] = '.';
			result = new String(expr_22, 0, 12);
		}
		finally
		{
			if (rNGCryptoServiceProvider != null)
			{
				rNGCryptoServiceProvider.Dispose();
			}
		}
		return result;*/
		return new UUID(0, 0).randomUUID().toString();
	}
 
//ORIGINAL LINE: [__DynamicallyInvokable, SecuritySafeCritical] public static string GetTempFileName()
	public static String GetTempFileName()
	{
		return Path.InternalGetTempFileName(true);
	}
 
//ORIGINAL LINE: [SecurityCritical] internal static string UnsafeGetTempFileName()
	public static String UnsafeGetTempFileName()
	{
		return Path.InternalGetTempFileName(false);
	}
 
//ORIGINAL LINE: [SecurityCritical] private static string InternalGetTempFileName(bool checkHost)
	private static String InternalGetTempFileName(boolean checkHost)
	{
		String tempPath = Path.GetTempPath();
		StringBuilder stringBuilder = new StringBuilder(260);
		/*(new FileIOPermission(FileIOPermissionAccess.Write, tempPath)).Demand();
		
		if (Win32Native.GetTempFileName(tempPath, "tmp", 0, stringBuilder) == 0)
		{
			__Error.WinIOError();
		}*/
		return stringBuilder.toString();
	}
 
//ORIGINAL LINE: [__DynamicallyInvokable] public static bool HasExtension(string path)
	public static boolean HasExtension(String path)
	{
		if (path != null)
		{
			Path.CheckInvalidPathChars(path, false);
			int num = path.length();
			while (--num >= 0)
			{
				char c = path.charAt(num);
				if (c == '.')
				{
					return num != path.length() - 1;
				}
				if (c == java.io.File.separatorChar || c == Path.AltDirectorySeparatorChar || c == Path.VolumeSeparatorChar)
				{
					break;
				}
			}
		}
		return false;
	}
 
//ORIGINAL LINE: [__DynamicallyInvokable] public static bool IsPathRooted(string path)
	public static boolean IsPathRooted(String path)
	{
		if (path != null)
		{
			Path.CheckInvalidPathChars(path, false);
			int length = path.length();
			if ((length >= 1 && (path.charAt(0) == java.io.File.separatorChar || path.charAt(0) == Path.AltDirectorySeparatorChar)) || (length >= 2 && path.charAt(1) == Path.VolumeSeparatorChar))
			{
				return true;
			}
		}
		return false;
	}
 
//ORIGINAL LINE: [__DynamicallyInvokable] public static string Combine(string path1, string path2)
	public static String Combine(String path1, String path2)
	{
		if (path1 == null || path2 == null)
		{
			throw new IllegalArgumentException((path1 == null) ? "path1" : "path2");
		}
		Path.CheckInvalidPathChars(path1, false);
		Path.CheckInvalidPathChars(path2, false);
		return Path.CombineNoChecks(path1, path2);
	}
 
//ORIGINAL LINE: [__DynamicallyInvokable] public static string Combine(string path1, string path2, string path3)
	public static String Combine(String path1, String path2, String path3)
	{
		if (path1 == null || path2 == null || path3 == null)
		{
			throw new IllegalArgumentException((path1 == null) ? "path1" : ((path2 == null) ? "path2" : "path3"));
		}
		Path.CheckInvalidPathChars(path1, false);
		Path.CheckInvalidPathChars(path2, false);
		Path.CheckInvalidPathChars(path3, false);
		return Path.CombineNoChecks(Path.CombineNoChecks(path1, path2), path3);
	}
	public static String Combine(String path1, String path2, String path3, String path4)
	{
		if (path1 == null || path2 == null || path3 == null || path4 == null)
		{
			throw new IllegalArgumentException((path1 == null) ? "path1" : ((path2 == null) ? "path2" : ((path3 == null) ? "path3" : "path4")));
		}
		Path.CheckInvalidPathChars(path1, false);
		Path.CheckInvalidPathChars(path2, false);
		Path.CheckInvalidPathChars(path3, false);
		Path.CheckInvalidPathChars(path4, false);
		return Path.CombineNoChecks(Path.CombineNoChecks(Path.CombineNoChecks(path1, path2), path3), path4);
	}
 
//ORIGINAL LINE: [__DynamicallyInvokable] public static string Combine(params string[] paths)
	public static String Combine(String... paths)
	{
		if (paths == null)
		{
			throw new IllegalArgumentException("paths");
		}
		int num = 0;
		int num2 = 0;
		for (int i = 0; i < paths.length; i++)
		{
			if (paths[i] == null)
			{
				throw new IllegalArgumentException("paths");
			}
			if (paths[i].length() != 0)
			{
				Path.CheckInvalidPathChars(paths[i], false);
				if (Path.IsPathRooted(paths[i]))
				{
					num2 = i;
					num = paths[i].length();
				}
				else
				{
					num += paths[i].length();
				}
				char c = paths[i].charAt(paths[i].length() - 1);
				if (c != java.io.File.separatorChar && c != Path.AltDirectorySeparatorChar && c != Path.VolumeSeparatorChar)
				{
					num++;
				}
			}
		}
		StringBuilder stringBuilder = new StringBuilder(num);
		for (int j = num2; j < paths.length; j++)
		{
			if (paths[j].length() != 0)
			{
				if (stringBuilder.length() == 0)
				{
					stringBuilder.append(paths[j]);
				}
				else
				{
					StringBuilder expr_C8 = stringBuilder;
					char c2 = expr_C8.charAt(expr_C8.length() - 1);
					if (c2 != java.io.File.separatorChar && c2 != Path.AltDirectorySeparatorChar && c2 != Path.VolumeSeparatorChar)
					{
						stringBuilder.append(java.io.File.separatorChar);
					}
					stringBuilder.append(paths[j]);
				}
			}
		}
		return stringBuilder.toString(); //stringBuilder.toString();
	}
	private static String CombineNoChecks(String path1, String path2)
	{
		if (path2.length() == 0)
		{
			return path1;
		}
		if (path1.length() == 0)
		{
			return path2;
		}
		if (Path.IsPathRooted(path2))
		{
			return path2;
		}
		char c = path1.charAt(path1.length() - 1);
		if (c != java.io.File.separatorChar && c != Path.AltDirectorySeparatorChar && c != Path.VolumeSeparatorChar)
		{
			return path1 + "\\" + path2;
		}
		return path1 + path2;
	}
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: internal static string ToBase32StringSuitableForDirName(byte[] buff)
	public static String ToBase32StringSuitableForDirName(byte[] buff)
	{
		StringBuilder stringBuilder = new StringBuilder(16);
		int num = buff.length;
		int num2 = 0;
		do
		{
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: byte b = (num2 < num) ? buff[num2++] : 0;
			byte b = (num2 < num) ? buff[num2++] : 0;
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: byte b2 = (num2 < num) ? buff[num2++] : 0;
			byte b2 = (num2 < num) ? buff[num2++] : 0;
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: byte b3 = (num2 < num) ? buff[num2++] : 0;
			byte b3 = (num2 < num) ? buff[num2++] : 0;
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: byte b4 = (num2 < num) ? buff[num2++] : 0;
			byte b4 = (num2 < num) ? buff[num2++] : 0;
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: byte b5 = (num2 < num) ? buff[num2++] : 0;
			byte b5 = (num2 < num) ? buff[num2++] : 0;
			stringBuilder.append(Path.s_Base32Char[(int)(b & 31)]);
			stringBuilder.append(Path.s_Base32Char[(int)(b2 & 31)]);
			stringBuilder.append(Path.s_Base32Char[(int)(b3 & 31)]);
			stringBuilder.append(Path.s_Base32Char[(int)(b4 & 31)]);
			stringBuilder.append(Path.s_Base32Char[(int)(b5 & 31)]);
//C# TO JAVA CONVERTER WARNING: The right shift operator was not replaced by Java's logical right shift operator since the left operand was not confirmed to be of an unsigned type, but you should review whether the logical right shift operator (>>>) is more appropriate:
			stringBuilder.append(Path.s_Base32Char[(b & 224) >> 5 | (b4 & 96) >> 2]);
//C# TO JAVA CONVERTER WARNING: The right shift operator was not replaced by Java's logical right shift operator since the left operand was not confirmed to be of an unsigned type, but you should review whether the logical right shift operator (>>>) is more appropriate:
			stringBuilder.append(Path.s_Base32Char[(b2 & 224) >> 5 | (b5 & 96) >> 2]);
//C# TO JAVA CONVERTER WARNING: The right shift operator was replaced by Java's logical right shift operator since the left operand was originally of an unsigned type, but you should confirm this replacement:
//C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
//ORIGINAL LINE: b3 = (byte)(b3 >> 5);
			b3 = (byte)(b3 >>> 5);
			if ((b4 & 128) != 0)
			{
				b3 |= 8;
			}
			if ((b5 & 128) != 0)
			{
				b3 |= 16;
			}
			stringBuilder.append(Path.s_Base32Char[(int)b3]);
		} while (num2 < num);
		return stringBuilder.toString();
	}
	public static void CheckSearchPattern(String searchPattern)
	{
		int num;
		while ((num = searchPattern.indexOf("..")) != -1)
		{
			if (num + 2 == searchPattern.length())
			{
				throw new IllegalArgumentException(Environment.GetResourceString("Arg_InvalidSearchPattern"));
			}
			if (searchPattern.charAt(num + 2) == java.io.File.separatorChar || searchPattern.charAt(num + 2) == Path.AltDirectorySeparatorChar)
			{
				throw new IllegalArgumentException(Environment.GetResourceString("Arg_InvalidSearchPattern"));
			}
			searchPattern = searchPattern.substring(num + 2);
		}
	}
	public static boolean HasIllegalCharacters(String path, boolean checkAdditional)
	{
		if (checkAdditional)
		{
			return StringHelper.indexOfAny(path, Path.InvalidPathCharsWithAdditionalChecks) >= 0;
		}
		return StringHelper.indexOfAny(path, Path.RealInvalidPathChars) >= 0;
	}

	public static void CheckInvalidPathChars(String path)
	{
		CheckInvalidPathChars(path, false);
	}

//C# TO JAVA CONVERTER NOTE: Java does not support optional parameters. Overloaded method(s) are created above:
//ORIGINAL LINE: internal static void CheckInvalidPathChars(string path, bool checkAdditional = false)
	public static void CheckInvalidPathChars(String path, boolean checkAdditional)
	{
		if (path == null)
		{
			throw new IllegalArgumentException("path");
		}
		if (Path.HasIllegalCharacters(path, checkAdditional))
		{
			throw new IllegalArgumentException(Environment.GetResourceString("Argument_InvalidPathChars"));
		}
	}
	public static String InternalCombine(String path1, String path2)
	{
		if (path1 == null || path2 == null)
		{
			throw new IllegalArgumentException((path1 == null) ? "path1" : "path2");
		}
		Path.CheckInvalidPathChars(path1, false);
		Path.CheckInvalidPathChars(path2, false);
		if (path2.length() == 0)
		{
			throw new IllegalArgumentException(Environment.GetResourceString("Argument_PathEmpty")+ "path2");
		}
		if (Path.IsPathRooted(path2))
		{
			throw new IllegalArgumentException(Environment.GetResourceString("Arg_Path2IsRooted")+ "path2");
		}
		int length = path1.length();
		if (length == 0)
		{
			return path2;
		}
		char c = path1.charAt(length - 1);
		if (c != java.io.File.separatorChar && c != Path.AltDirectorySeparatorChar && c != Path.VolumeSeparatorChar)
		{
			return path1 + "\\" + path2;
		}
		return path1 + path2;
	}
	public static boolean IsWithinAppRoot(String appDomainAppVirtualPath, String pageDirectory) {
		// TODO Auto-generated method stub
		return false;
	}
}