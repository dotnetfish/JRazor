package com.superstudio.jrazor.text;




public final class TextExtensions
{
	public static void seek(ITextBuffer self, int characters)
	{
		self.setPosition(self.getPosition() + characters);
	}

	public static ITextDocument toDocument(ITextBuffer self)
	{
		ITextDocument ret = (ITextDocument)((self instanceof ITextDocument) ? self : null);
		if (ret == null)
			
		{
			ret = new SeekableTextReader(self);
		}
		return ret;
	}

	public static LookaheadToken beginLookahead(ITextBuffer self)
	{
		int start = self.getPosition();
		return new LookaheadToken(() ->
		{
			self.setPosition(start);
		}
	   );
	}

	public static String readToEnd(ITextBuffer self)
	{
		StringBuilder builder = new StringBuilder();
		int read;
		while ((read = self.read()) != -1)
		{
			builder.append((char)read);
		}
		return builder.toString();
	}
}