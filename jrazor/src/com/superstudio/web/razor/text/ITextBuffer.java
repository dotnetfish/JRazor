package com.superstudio.web.razor.text;


public interface ITextBuffer
{
	int getLength();
	int getPosition();
	void setPosition(int value);
	int read();
	int peek();
	default void seek(int characters) {
		setPosition(getPosition() + characters);
	 }

	default ITextDocument toDocument()
		{
			ITextDocument ret = (ITextDocument)this;
			if (ret == null){
				ret = new SeekableTextReader(this);
			}
			return ret;
		}

	default LookaheadToken BeginLookahead()
		{
			int start = getPosition();

			return new LookaheadToken(() ->
			{
				setPosition(start);
			}
		   );
		}


		default String readToEnd()
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