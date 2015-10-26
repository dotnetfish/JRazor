package com.superstudio.codedom.compiler;

import com.superstudio.commons.Environment;
import com.superstudio.commons.IntPtr;
import com.superstudio.commons.RuntimeEnvironment;
import com.superstudio.commons.SafeUserTokenHandle;
import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.commons.io.*;

public final class Executor
{
	private static final int ProcessTimeOut = 600000;
	private static final String fileAccess = null;

	public static String GetRuntimeInstallDirectory()
	{
		return RuntimeEnvironment.GetRuntimeDirectory();
	}

	private static FileStream CreateInheritedFile(String file)
	{
		return new FileStream(file, FileMode.CreateNew, FileAccess.Write, FileShare.forValue(FileShare.Read.getValue() | FileShare.Inheritable.getValue()));
	}

	public static void ExecWait(String cmd, TempFileCollection tempFiles)
	{
		String text = null;
		String text2 = null;
		RefObject<String> tempRef_text = new RefObject<String>(text);
		RefObject<String> tempRef_text2 = new RefObject<String>(text2);
		Executor.ExecWaitWithCapture(cmd, tempFiles, tempRef_text, tempRef_text2);
		text = tempRef_text.getRefObj();
		text2 = tempRef_text2.getRefObj();
	}

	public static int ExecWaitWithCapture(String cmd, TempFileCollection tempFiles, RefObject<String> outputName, RefObject<String> errorName)
	{
		return Executor.ExecWaitWithCapture(null, cmd, Environment.CurrentDirectory, tempFiles, outputName, errorName, null);
	}

	public static int ExecWaitWithCapture(String cmd, String currentDir, TempFileCollection tempFiles, RefObject<String> outputName, RefObject<String> errorName)
	{
		return Executor.ExecWaitWithCapture(null, cmd, currentDir, tempFiles, outputName, errorName, null);
	}

	public static int ExecWaitWithCapture(IntPtr userToken, String cmd, TempFileCollection tempFiles, RefObject<String> outputName, RefObject<String> errorName)
	{
		return Executor.ExecWaitWithCapture(new SafeUserTokenHandle(userToken, false), cmd, Environment.CurrentDirectory, tempFiles, outputName, errorName, null);
	}

	public static int ExecWaitWithCapture(IntPtr userToken, String cmd, String currentDir, TempFileCollection tempFiles, RefObject<String> outputName, RefObject<String> errorName)
	{
		return Executor.ExecWaitWithCapture(new SafeUserTokenHandle(userToken, false), cmd, Environment.CurrentDirectory, tempFiles, outputName, errorName, null);
	}

	public static int ExecWaitWithCapture(SafeUserTokenHandle userToken,
			String cmd,
			String currentDir, 
			TempFileCollection tempFiles, 
			RefObject<String> outputName, 
			RefObject<String> errorName, String trueCmdLine)
	{
		int result = 0;
		try
		{
			//WindowsImpersonationContext impersonation = Executor.RevertImpersonation();
			try
			{
				//result = Executor.ExecWaitWithCaptureUnimpersonated(userToken, cmd, currentDir, tempFiles, outputName, errorName, trueCmdLine);
			}
			finally
			{
				//Executor.ReImpersonate(impersonation);
			}
		}
		catch (java.lang.Exception e)
		{
			throw e;
		}
		return result;
	}

//C# TO JAVA CONVERTER TODO TASK: C# 'unsafe' code is not converted by C# to Java Converter:
	/*private  static int ExecWaitWithCaptureUnimpersonated(SafeUserTokenHandle userToken, 
			String cmd, String currentDir, 
			TempFileCollection tempFiles, 
			//ref String outputName, 
			RefObject<String> outputName,
			RefObject<String> errorName, 
			String trueCmdLine)
		{
			//IntSecurity.UnmanagedCode.Demand();
			int result = 0;
			if (outputName.getRefObj() == null || outputName.getRefObj().length() == 0)
			{
				outputName.setRefObj(tempFiles.AddExtension("out"));
			}
			if (errorName.getRefObj() == null || errorName.getRefObj().length() == 0)
			{
				errorName.setRefObj(tempFiles.AddExtension("err")) ;
			}
			FileStream fileStream = Executor.CreateInheritedFile(outputName.getRefObj());
			FileStream fileStream2 = Executor.CreateInheritedFile(errorName.getRefObj());
			boolean flag = false;
			//SafeNativeMethods.PROCESS_INFORMATION pROCESS_INFORMATION = new SafeNativeMethods.PROCESS_INFORMATION();
			//SafeProcessHandle safeProcessHandle = new SafeProcessHandle();
			//SafeThreadHandle safeThreadHandle = new SafeThreadHandle();
			//SafeUserTokenHandle safeUserTokenHandle = null;
			try
			{
				StreamWriter expr_7D = new StreamWriter(fileStream, Encoding.UTF8);
				expr_7D.Write(currentDir);
				expr_7D.Write("> ");
				//expr_7D.WriteLine(((trueCmdLine != null) ? trueCmdLine : cmd));
				expr_7D.WriteLine();
				expr_7D.WriteLine();
				expr_7D.Flush();
				//NativeMethods.STARTUPINFO sTARTUPINFO = new NativeMethods.STARTUPINFO();
				sTARTUPINFO.cb = Marshal.SizeOf(sTARTUPINFO);
				sTARTUPINFO.dwFlags = 257;
				sTARTUPINFO.wShowWindow = 0;
				sTARTUPINFO.hStdOutput = fileStream.SafeFileHandle;
				sTARTUPINFO.hStdError = fileStream2.SafeFileHandle;
				sTARTUPINFO.hStdInput = new SafeFileHandle(UnsafeNativeMethods.GetStdHandle(-10), false);
				Map<String,String> stringMap = new HashMap<String,String>();
				for(Map.Entry<String, String> dictionaryEntry : Environment.GetEnvironmentVariables().entrySet())
				{
					stringMap.put(dictionaryEntry.getKey(),dictionaryEntry.getValue());
				}
				stringMap.put("_ClrRestrictSecAttributes", "1");
				byte[] array = EnvironmentBlock.ToByteArray(StringDictionary, false);
				try
				{
					fixed (byte* ptr = array)
					{
						IntPtr intPtr = new IntPtr((void*)ptr);
						if (userToken == null || userToken.IsInvalid)
						{
							RuntimeHelpers.PrepareConstrainedRegions();
							try
							{
								goto IL_31A;
							}
							finally
							{
								flag = NativeMethods.CreateProcess(null, new StringBuilder(cmd), null, null, true, 0, intPtr, currentDir, sTARTUPINFO, pROCESS_INFORMATION);
								if (pROCESS_INFORMATION.hProcess != (IntPtr)0 && pROCESS_INFORMATION.hProcess != NativeMethods.INVALID_HANDLE_VALUE)
								{
									safeProcessHandle.InitialSetHandle(pROCESS_INFORMATION.hProcess);
								}
								if (pROCESS_INFORMATION.hThread != (IntPtr)0 && pROCESS_INFORMATION.hThread != NativeMethods.INVALID_HANDLE_VALUE)
								{
									safeThreadHandle.InitialSetHandle(pROCESS_INFORMATION.hThread);
								}
							}
						}
						flag = SafeUserTokenHandle.DuplicateTokenEx(userToken, 983551, null, 2, 1, out safeUserTokenHandle);
						if (flag)
						{
							RuntimeHelpers.PrepareConstrainedRegions();
							try
							{
							}
							finally
							{
								flag = NativeMethods.CreateProcessAsUser(safeUserTokenHandle, null, cmd, null, null, true, 0, new HandleRef(null, intPtr), currentDir, sTARTUPINFO, pROCESS_INFORMATION);
								if (pROCESS_INFORMATION.hProcess != (IntPtr)0 && pROCESS_INFORMATION.hProcess != NativeMethods.INVALID_HANDLE_VALUE)
								{
									safeProcessHandle.InitialSetHandle(pROCESS_INFORMATION.hProcess);
								}
								if (pROCESS_INFORMATION.hThread != (IntPtr)0 && pROCESS_INFORMATION.hThread != NativeMethods.INVALID_HANDLE_VALUE)
								{
									safeThreadHandle.InitialSetHandle(pROCESS_INFORMATION.hThread);
								}
							}
						}
					}
				}
				finally
				{
					byte* ptr = null;
				}
			}
			finally
			{
				if (!flag && safeUserTokenHandle != null && !safeUserTokenHandle.IsInvalid)
				{
					safeUserTokenHandle.Close();
					safeUserTokenHandle = null;
				}
				fileStream.Close();
				fileStream2.Close();
			}
			IL_31A:
			if (flag)
			{
				try
				{
					ProcessWaitHandle processWaitHandle = null;
					boolean flag2;
					try
					{
						processWaitHandle = new ProcessWaitHandle(safeProcessHandle);
						flag2 = processWaitHandle.WaitOne(600000, false);
					}
					finally
					{
						if (processWaitHandle != null)
						{
							processWaitHandle.Close();
						}
					}
					if (!flag2)
					{
						throw new ExternalException(SR.GetString("ExecTimeout", new Object[] { cmd }), 258);
					}
					int num = 259;
					if (!NativeMethods.GetExitCodeProcess(safeProcessHandle, out num))
					{
						throw new ExternalException(SR.GetString("ExecCantGetRetCode", new Object[] { cmd }), Marshal.GetLastWin32Error());
					}
					result = num;
					return result;
				}
				finally
				{
					safeProcessHandle.Close();
					safeThreadHandle.Close();
					if (safeUserTokenHandle != null && !safeUserTokenHandle.IsInvalid)
					{
						safeUserTokenHandle.Close();
					}
				}
			}
			int expr_3CA = Marshal.GetLastWin32Error();
			if (expr_3CA == 8)
			{
				throw new OutOfMemoryException();
			}
			Win32Exception inner = new Win32Exception(expr_3CA);
			throw new ExternalException(SR.GetString("ExecCantExec", new object[] { cmd }), inner);
		}
*/
 
//ORIGINAL LINE: [SecurityPermission(SecurityAction.Assert, ControlPrincipal = true, UnmanagedCode = true), PermissionSet(SecurityAction.LinkDemand, Unrestricted = true)] internal static WindowsImpersonationContext RevertImpersonation()
	/*public static WindowsImpersonationContext RevertImpersonation()
	{
		//return WindowsIdentity.Impersonate(new IntPtr(0));
	}*/

	/*public static void ReImpersonate(WindowsImpersonationContext impersonation)
	{
		impersonation.Undo();
	}*/
}