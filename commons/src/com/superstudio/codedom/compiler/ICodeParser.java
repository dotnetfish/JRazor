package com.superstudio.codedom.compiler;

import com.superstudio.codedom.*;
import com.superstudio.commons.TextReader;

public interface ICodeParser
{
	CodeCompileUnit parse(TextReader codeStream);
}