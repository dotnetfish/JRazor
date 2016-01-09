package com.superstudio.jrazor.generator;

import java.util.Collections;

import com.superstudio.commons.csharpbridge.RefObject;
import com.superstudio.commons.csharpbridge.StringHelper;
import com.superstudio.jrazor.RazorEngineHost;
import com.superstudio.jrazor.parser.SyntaxConstants;
import com.superstudio.jrazor.parser.syntaxTree.Span;
import com.superstudio.jrazor.parser.syntaxTree.SpanKind;
public final class CodeGeneratorPaddingHelper
{
	private static final char[] _newLineChars = { '\r', '\n' };

	// there is some duplicity of code here, but its very simple and since this is a host path, I'd rather not create another class to encapsulate the data.
	public static int paddingCharCount(RazorEngineHost host, Span target, int generatedStart)
	{
		int padding = calculatePadding(host, target, generatedStart);

		if (host.getDesignTimeMode() && host.getIsIndentingWithTabs())
		{
			int spaces = 0;
			
			int tabs = Math.floorDiv(padding, host.getTabSize());
			spaces = padding % host.getTabSize();

			return tabs + spaces;
		}
		else
		{
			return padding;
		}
	}

	// Special case for statement padding to account for brace positioning in the editor.
	public static String padStatement(RazorEngineHost host, 
			String code, Span target, RefObject<Integer> startGeneratedCode,
			RefObject<Integer> paddingCharCount)
	{
		//Integer temp_paddingCharCount=paddingCharCount.getRefObj();
		if (host == null)
		{
			//throw new ArgumentNullException("host");
		}

		if (target == null)
		{
			//throw new ArgumentNullException("target");
		}

		// We are passing 0 rather than startgeneratedcode intentionally (keeping v2 behavior).
		int padding = calculatePadding(host, target, 0);

		// We treat statement padding specially so for brace positioning, so that in the following example:
		//   @if (foo > 0)
		//   {
		//   }
		//
		// the braces shows up under the @ rather than under the if.
		Integer tempSgc=startGeneratedCode.getRefObj();
		if (host.getDesignTimeMode() && padding > 0 && target.getPrevious().getKind() == SpanKind.Transition
				&& target.getPrevious().getContent().equals(SyntaxConstants.TransitionString)) // target.Previous is guaranteed to be none null if you got any padding.
		{
			padding--;
			tempSgc--;
			
		}

		String generatedCode = padInternal(host, code, padding, paddingCharCount);
		startGeneratedCode.setRefObj(tempSgc);
		return generatedCode;
	}

	public static String pad(RazorEngineHost host, String code, Span target, RefObject<Integer> paddingCharCount)
	{
		int padding = calculatePadding(host, target, 0);

		return padInternal(host, code, padding, paddingCharCount);
	}

	public static String pad(RazorEngineHost host, String code, Span target, int generatedStart, RefObject<Integer> paddingCharCount)
	{
		int padding = calculatePadding(host, target, generatedStart);

		return padInternal(host, code, padding, paddingCharCount);
	}

	// internal for unit testing only, not intended to be used directly in code
	public static int calculatePadding(RazorEngineHost host, Span target, int generatedStart)
	{
		if (host == null)
		{
			//throw new ArgumentNullException("host");
		}

		if (target == null)
		{
			//throw new ArgumentNullException("target");
		}

		int padding;

		padding = collectSpacesAndTabs(target, host.getTabSize()) - generatedStart;

		// if we add generated text that is longer than the padding we wanted to insert we have no recourse and we have to skip padding
		// example:
		// Razor code at column zero: @somecode()
		// Generated code will be:
		// In design time: __o = somecode();
		// In Run time: Write(somecode());
		//
		// In both cases the padding would have been 1 space to remote the space the @ symbol takes, which will be smaller than the 6 chars the hidden generated code takes.
		if (padding < 0)
		{
			padding = 0;
		}

		return padding;
	}

	private static String padInternal(RazorEngineHost host, String code, int padding, RefObject<Integer> paddingCharCount)
	{
		if (host.getDesignTimeMode() && host.getIsIndentingWithTabs())
		{
			int spaces = 0;
			//RefObject<Integer> tempRef_spaces = new RefObject<Integer>(spaces);
			int tabs = Math.floorDiv(padding, host.getTabSize());
			spaces = padding % host.getTabSize();

			paddingCharCount.setRefObj(tabs + spaces);
			Character[] tabsChar=new Character[tabs];
			
			Collections.nCopies(tabs, '\t').toArray(tabsChar);
			
			Character[] spaceChar=new Character[spaces];
			
			Collections.nCopies(spaces, ' ').toArray(spaceChar);
			
			return String.valueOf(tabsChar) + String.valueOf(spaceChar) + code;
		}
		else
		{
			
			paddingCharCount.setRefObj(padding);
			//return code.padLeft(padding + code.length(), ' ');
			return StringHelper.padLeft(code, padding+code.length(), " ");
		}
	}

	private static int collectSpacesAndTabs(Span target, int tabSize)
	{
		Span firstSpanInLine = target;

		String currentContent = null;

		while (firstSpanInLine.getPrevious() != null)
		{
			// When scanning previous spans we need to be break down the spans with spaces.
			// Because the parser doesn't so for example a span looking like \n\n\t needs to be broken down, and we should just grab the \t.
			String tempVar = firstSpanInLine.getPrevious().getContent();
			String previousContent = (tempVar != null) ? tempVar : "";

			int lastNewLineIndex = StringHelper.lastIndexOfAny(previousContent,_newLineChars);

			if (lastNewLineIndex < 0)
			{
				firstSpanInLine = firstSpanInLine.getPrevious();
			}
			else
			{
				if (lastNewLineIndex != previousContent.length() - 1)
				{
					firstSpanInLine = firstSpanInLine.getPrevious();
					currentContent = previousContent.substring(lastNewLineIndex + 1);
				}

				break;
			}
		}

		// We need to walk from the beginning of the line, because space + tab(tabSize) = tabSize columns, but tab(tabSize) + space = tabSize+1 columns.
		Span currentSpanInLine = firstSpanInLine;

		if (currentContent == null)
		{
			currentContent = currentSpanInLine.getContent();
		}

		int padding = 0;
		while (currentSpanInLine != target)
		{
			if (currentContent != null)
			{
				for (int i = 0; i < currentContent.length(); i++)
				{
					if (currentContent.charAt(i) == '\t')
					{
						// Example:
						// <space><space><tab><tab>:
						// iter 1) 1
						// iter 2) 2
						// iter 3) 4 = 2 + (4 - 2)
						// iter 4) 8 = 4 + (4 - 0)
						padding = padding + (tabSize - (padding % tabSize));
					}
					else
					{
						padding++;
					}
				}
			}

			currentSpanInLine = currentSpanInLine.getNext();
			currentContent = currentSpanInLine.getContent();
		}

		return padding;
	}
}