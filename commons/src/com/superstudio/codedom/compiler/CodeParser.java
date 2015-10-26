package com.superstudio.codedom.compiler;

import com.superstudio.codedom.*;
import com.superstudio.commons.TextReader;

public abstract class CodeParser implements ICodeParser
{
	public abstract CodeCompileUnit Parse(TextReader codeStream);
}