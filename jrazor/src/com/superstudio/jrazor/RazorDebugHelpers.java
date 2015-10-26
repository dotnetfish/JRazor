package com.superstudio.jrazor;

import com.superstudio.codedom.CodeCompileUnit;
import com.superstudio.commons.Environment;
import com.superstudio.commons.Path;
import com.superstudio.commons.TaskFactory;
import com.superstudio.commons.csharpbridge.action.Action;
import com.superstudio.commons.io.File;
import com.superstudio.jrazor.parser.syntaxTree.Block;
import com.superstudio.jrazor.parser.syntaxTree.SyntaxTreeNode;
import com.superstudio.jrazor.text.TextChange;

import java.io.StringWriter;



 
//#if //Debug


public final class RazorDebugHelpers
{
	private static boolean _outputDebuggingEnabled = isDebuggingEnabled();

	private static final java.util.HashMap<Character, String> _printableEscapeChars = new java.util.HashMap<Character, String>();
	static
	{
		_printableEscapeChars.put('\0', "\\0");
		_printableEscapeChars.put('\\', "\\\\");
		_printableEscapeChars.put('\'', "'");
		_printableEscapeChars.put('\"', "\\\"");
		//_printableEscapeChars.put('\a', "\\a");
		_printableEscapeChars.put('\b', "\\b");
		_printableEscapeChars.put('\f', "\\f");
		_printableEscapeChars.put('\n', "\\n");
		_printableEscapeChars.put('\r', "\\r");
		_printableEscapeChars.put('\t', "\\t");
		//_printableEscapeChars.put('\v', "\\v");
	}

	public static boolean getOutputDebuggingEnabled()
	{
		return _outputDebuggingEnabled;
	}

 
	public static void writeGeneratedCode(String sourceFile, CodeCompileUnit codeCompileUnit)
	{
		if (!getOutputDebuggingEnabled())
		{
			return;
		}

				// Trim the html part of cshtml or vbhtml
				// REVIEW: Do these options need to be tweaked?
 
		runTask(() ->
		{
			String extension = Path.GetExtension(sourceFile);
			RazorCodeLanguage language = RazorCodeLanguage.getLanguageByExtension(extension);
			//CodeDomProvider provider = CodeDomProvider.CreateProvider(language.getLanguageName());
 
//			using (var writer = new StringWriter())
			StringWriter writer = new StringWriter();
			try
			{
				String outputExtension = extension.substring(0, 3);
				String outputFileName = normalize(sourceFile) + "_generated" + outputExtension;
				String outputPath = Path.Combine(Path.GetDirectoryName(sourceFile), outputFileName);
			//provider.GenerateCodeFromCompileUnit(codeCompileUnit, writer, new CodeGeneratorOptions());
			
				try {
					File.WriteAllText(outputPath, writer.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			finally
			{
				try {
					writer.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				writer=null;
			}
		}
	   );
	}

	public static void writeDebugTree(String sourceFile, Block document, PartialParseResult result, TextChange change, RazorEditorParser parser, boolean treeStructureChanged)
	{
		if (!getOutputDebuggingEnabled())
		{
			return;
		}

 
		runTask(() ->
		{
			String outputFileName = normalize(sourceFile) + "_tree";
			String outputPath = Path.Combine(Path.GetDirectoryName(sourceFile), outputFileName);
			StringBuilder treeBuilder = new StringBuilder();
			writeTree(document, treeBuilder);
			treeBuilder.append("\n");
			treeBuilder.append(String.format("Last Change: %s", change));
			treeBuilder.append("\n");
			treeBuilder.append(String.format("Normalized To: %s", change.normalize().clone()));
			
			treeBuilder.append("\n");
			treeBuilder.append(String.format("Partial Parse Result: %s", result));
			
			//treeBuilder.AppendFormat(, "Partial Parse Result: {0}", result);
			treeBuilder.append("\n");
			if (result.hasFlag(PartialParseResult.Rejected))
			{
				treeBuilder.append(String.format("Partial Parse Result: %s", result));
				//treeBuilder.AppendFormat(, "Tree Structure Changed: {0}", treeStructureChanged);
				treeBuilder.append("\n");
			}
			if (result.hasFlag(PartialParseResult.AutoCompleteBlock))
			{
				treeBuilder.append(String.format("Auto Complete Insert String: \"%s\"", parser.getAutoCompleteString()));
				//treeBuilder.AppendFormat(, "Auto Complete Insert String: \"{0}\"", parser.GetAutoCompleteString());
				treeBuilder.append("\n");
			}
			try {
				File.WriteAllText(outputPath, treeBuilder.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	   );
	}

 
	private static void writeTree(SyntaxTreeNode node, StringBuilder treeBuilder){
		writeTree(node,treeBuilder,0);
	}
	
	private static void writeTree(SyntaxTreeNode node, StringBuilder treeBuilder, int depth)
	{
		if (node == null)
		{
			return;
		}
		if (depth > 1)
		{
			writeIndent(treeBuilder, depth);
		}

		if (depth > 0)
		{
			treeBuilder.append("|-- ");
		}
		treeBuilder.append("\r\n");
		treeBuilder.append(convertEscapseSequences(node.toString()));
		treeBuilder.append("\r\n");
		if (node.getIsBlock())
		{
			for (SyntaxTreeNode child : ((Block)node).getChildren())
			{
				writeTree(child, treeBuilder, depth + 1);
			}
		}
	}

 
	//[SuppressMessage("Microsoft.Design", "CA1031:DoNotCatchGeneralExceptionTypes", Justification = "This is //Debug only"), SuppressMessage("Microsoft.Web.FxCop", "MW1202:DoNotUseProblematicTaskTypes", Justification = "This rule is not applicable to this assembly."), SuppressMessage("Microsoft.Web.FxCop", "MW1201:DoNotCallProblematicMethodsOnTask", Justification = "This rule is not applicable to this assembly.")]
	private static void runTask(Action action)
	{
				// Catch all errors since this is just a //Debug helper
 
		TaskFactory.getInstance().StartNew(new Runnable() {
            @Override
            public void run() {
                 //throw new RuntimeException();
                
          	  try
    			{
    				action.execute();
    			}
    			catch (java.lang.Exception e)
    			{
    			}
            }
        }
	   );
		
        
	}

	private static void writeIndent(StringBuilder sb, int depth)
	{
		for (int i = 0; i < (depth - 1) * 4; ++i)
		{
			if (i % 4 == 0)
			{
				sb.append("|");
			}
			else
			{
				sb.append(" ");
			}
		}
	}

	private static String normalize(String path)
	{
		return Path.GetFileName(path).replace('.', '_');
	}

	private static String convertEscapseSequences(String value)
	{
		StringBuilder sb = new StringBuilder();
 
		int vLen=value.length();
		for (int i=0;i<vLen;i++)
		{
			sb.append(getCharValue(value.charAt(i)));
		}
		return sb.toString();
	}

	private static String getCharValue(char ch)
	{
		String value = null;
		if ((value = _printableEscapeChars.get(ch)) != null)
		{
			return value;
		}
		return (new Character(ch)).toString();
	}

	private static boolean isDebuggingEnabled()
	{
		
		
		return Boolean.parseBoolean(Environment.GetEnvironmentVariable("RAZOR_Debug"));
	}
}
//#endif
