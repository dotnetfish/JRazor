package com.superstudio.jrazor.text;



public interface ITextBuffer
{
	int getLength();
	int getPosition();
	void setPosition(int value);
	int read();
	int peek();
	default void Seek(int characters) {
		setPosition(getPosition() + characters);
	 };
	 default ITextDocument toDocument() 
		{
			ITextDocument ret = (ITextDocument)this;
			if (ret == null){
				ret = new SeekableTextReader(this);
			}
			return ret;
		};
		default LookaheadToken BeginLookahead()
		{
			int start = getPosition();
	 
			return new LookaheadToken(() ->
			{
				setPosition(start);
			}
		   );
		}

	//C# TO JAVA CONVERTER TODO TASK: Extension methods are not available in Java:
	//ORIGINAL LINE: public static string ReadToEnd(this ITextBuffer self)
		default String ReadToEnd()
		{
			StringBuilder builder = new StringBuilder();
			int read;
			while ((read = this.read()) != -1)
			{
				builder.append((char)read);
			}
			return builder.toString();
		}	
		
}