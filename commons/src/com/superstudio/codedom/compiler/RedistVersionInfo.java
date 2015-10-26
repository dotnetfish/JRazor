package com.superstudio.codedom.compiler;


import java.util.*;

import com.superstudio.commons.Environment;
import com.superstudio.commons.Registry;
import com.superstudio.commons.SR;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.commons.io.Path;

public final class RedistVersionInfo
{
	public static final String DirectoryPath = "CompilerDirectoryPath";

	public static final String NameTag = "CompilerVersion";

	public static final String DefaultVersion = "v4.0";

	public static final String InPlaceVersion = "v4.0";

	public static final String RedistVersion = "v3.5";

	public static final String RedistVersion20 = "v2.0";

	private static final String MSBuildToolsPath = "MSBuildToolsPath";

	private static final String dotNetFrameworkRegistryPath = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\MSBuild\\ToolsVersions\\";

	public static String GetCompilerPath(Map<String, String> provOptions, String compilerExecutable)
	{
		String text = Executor.GetRuntimeInstallDirectory();
		if (provOptions != null)
		{
			String result = null;
			boolean flag = provOptions.containsKey("CompilerDirectoryPath") ? (result = provOptions.get("CompilerDirectoryPath")).equals(result) : false;
			String text2 = null;
			boolean flag2 = provOptions.containsKey("CompilerVersion") ? (text2 = provOptions.get("CompilerVersion")).equals(text2) : false;
			if (flag & flag2)
			{
				throw new IllegalStateException(SR.GetString("Cannot_Specify_Both_Compiler_Path_And_Version", new Object[] {"CompilerDirectoryPath", "CompilerVersion"}));
			}
			if (flag)
			{
				return result;
			}
			if (flag2 && !(text2.equals("v4.0")))
			{
				if (!(text2.equals("v3.5")))
				{
					if (!(text2.equals("v2.0")))
					{
						text = null;
					}
					else
					{
						text = RedistVersionInfo.GetCompilerPathFromRegistry(text2);
					}
				}
				else
				{
					text = RedistVersionInfo.GetCompilerPathFromRegistry(text2);
				}
			}
		}
		if (text == null)
		{
			throw new IllegalStateException(SR.GetString("CompilerNotFound", new Object[] {compilerExecutable}));
		}
		return text;
	}

	private static String GetCompilerPathFromRegistry(String versionVal)
	{
		String environmentVariable = Environment.GetEnvironmentVariable("COMPLUS_InstallRoot");
		String environmentVariable2 = Environment.GetEnvironmentVariable("COMPLUS_Version");
		String text;
		if (!StringHelper.isNullOrEmpty(environmentVariable) && !StringHelper.isNullOrEmpty(environmentVariable2))
		{
			text = Path.Combine(environmentVariable, environmentVariable2);
			if ((new java.io.File(text)).isDirectory())
			{
				return text;
			}
		}
		String str = versionVal.substring(1);
		Object tempVar = Registry.GetValue("HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\MSBuild\\ToolsVersions\\" + str, "MSBuildToolsPath", null);
		text = ((String)((tempVar instanceof String) ? tempVar : null));
		if (text != null && (new java.io.File(text)).isDirectory())
		{
			return text;
		}
		return null;
	}
}