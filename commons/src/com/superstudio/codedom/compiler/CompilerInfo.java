package com.superstudio.codedom.compiler;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import com.superstudio.commons.Resource;
import com.superstudio.commons.exception.ConfigurationErrorsException;

public final class CompilerInfo
{
	public String _codeDomProviderTypeName;

	public CompilerParameters _compilerParams;

	public String[] _compilerLanguages;

	public String[] _compilerExtensions;

	public String configFileName;

	public Map<String, String> _providerOptions;

	public int configFileLineNumber;

	public boolean _mapped;

	private java.lang.Class type;

	public java.lang.Class getCodeDomProviderType() throws ClassNotFoundException, ConfigurationErrorsException
	{
		if (this.type == null)
		{
			synchronized (this)
			{
				if (this.type == null)
				{
					this.type = java.lang.Class.forName(this._codeDomProviderTypeName);
					if (this.type == null)
					{
						if (this.configFileName == null)
						{
							throw new ConfigurationErrorsException(Resource.getString("Unable_To_Locate_Type", new Object[] {this._codeDomProviderTypeName, "", 0}));
						}
						//throw new ConfigurationErrorsException(Resource.getString("Unable_To_Locate_Type", new Object[] {this._codeDomProviderTypeName}), this.configFileName, this.configFileLineNumber);
					}
				}
			}
		}
		return this.type;
	}

	public boolean getIsCodeDomProviderTypeValid() throws ClassNotFoundException
	{
		return java.lang.Class.forName(this._codeDomProviderTypeName) != null;
	}

	public CompilerParameters getCompilerParams()
	{
		return this._compilerParams;
	}

	public Map<String, String> getProviderOptions()
	{
		return this._providerOptions;
	}

	/*private CompilerInfo()
	{
	}*/

	public String[] GetLanguages()
	{
		return this.CloneCompilerLanguages();
	}

	public String[] GetExtensions()
	{
		return this.CloneCompilerExtensions();
	}

	public CodeDomProvider CreateProvider() throws ClassNotFoundException, ConfigurationErrorsException, InstantiationException, IllegalAccessException
	{
		if (this._providerOptions.size() > 0)
		{
			java.lang.reflect.Constructor constructor=null;
			try {
				constructor = this.getCodeDomProviderType().getConstructor(Map.class);
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (constructor != null)
			{
				try {
					return (CodeDomProvider)constructor.newInstance(this._providerOptions);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			return (CodeDomProvider)this.getCodeDomProviderType().newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			return (CodeDomProvider)this.getCodeDomProviderType().newInstance();
	}

	public CodeDomProvider CreateProvider(Map<String, String> providerOptions) throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, ConfigurationErrorsException
	{
		if (providerOptions == null)
		{
			throw new IllegalArgumentException("providerOptions");
		}
		java.lang.reflect.Constructor constructor = 
				this.getCodeDomProviderType().getConstructor(Map.class);
		if (constructor != null)
		{
			return (CodeDomProvider)constructor.newInstance(providerOptions);
		}
		throw new IllegalStateException(Resource.getString("Provider_does_not_support_options", new Object[] {this.getCodeDomProviderType().toString()}));
	}

	public CompilerParameters CreateDefaultCompilerParameters()
	{
		return this.CloneCompilerParameters();
	}

	public CompilerInfo(CompilerParameters compilerParams, String codeDomProviderTypeName, String[] compilerLanguages, String[] compilerExtensions)
	{
		this._compilerLanguages = compilerLanguages;
		this._compilerExtensions = compilerExtensions;
		this._codeDomProviderTypeName = codeDomProviderTypeName;
		if (compilerParams == null)
		{
			compilerParams = new CompilerParameters();
		}
		this._compilerParams = compilerParams;
	}

	public CompilerInfo(CompilerParameters compilerParams, String codeDomProviderTypeName)
	{
		this._codeDomProviderTypeName = codeDomProviderTypeName;
		if (compilerParams == null)
		{
			compilerParams = new CompilerParameters();
		}
		this._compilerParams = compilerParams;
	}

	@Override
	public int hashCode()
	{
		return this._codeDomProviderTypeName.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		CompilerInfo compilerInfo = (CompilerInfo)((o instanceof CompilerInfo) ? o : null);
		try {
			return o != null && (this.getCodeDomProviderType() == compilerInfo.getCodeDomProviderType() &&
					this.getCompilerParams().getWarningLevel() == compilerInfo.getCompilerParams().getWarningLevel() && this.getCompilerParams().getIncludeDebugInformation() == compilerInfo.getCompilerParams().getIncludeDebugInformation()) && this.getCompilerParams().getCompilerOptions() == compilerInfo.getCompilerParams().getCompilerOptions();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConfigurationErrorsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private CompilerParameters CloneCompilerParameters()
	{
		CompilerParameters tempVar = new CompilerParameters();
		tempVar.setIncludeDebugInformation(this._compilerParams.getIncludeDebugInformation());
		tempVar.setTreatWarningsAsErrors(this._compilerParams.getTreatWarningsAsErrors());
		tempVar.setWarningLevel(this._compilerParams.getWarningLevel());
		tempVar.setCompilerOptions(this._compilerParams.getCompilerOptions());
		return tempVar;
	}

	private String[] CloneCompilerLanguages()
	{
		String[] array = new String[this._compilerLanguages.length];
		System.arraycopy(this._compilerLanguages, 0, array, 0, this._compilerLanguages.length);
		return array;
	}

	private String[] CloneCompilerExtensions()
	{
		String[] array = new String[this._compilerExtensions.length];
		System.arraycopy(this._compilerExtensions, 0, array, 0, this._compilerExtensions.length);
		return array;
	}
}