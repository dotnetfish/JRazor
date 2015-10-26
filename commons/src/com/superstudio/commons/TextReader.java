package com.superstudio.commons;


import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.function.Predicate;

import com.superstudio.commons.exception.ArgumentNullException;



public class TextReader extends StringReader {

	public TextReader(){
		this("");
	}
	public TextReader(String in) {
		super(in);
		// TODO Auto-generated constructor stub
	}
	
	

	public String ReadToEnd()  {
		
		StringBuilder builder = new StringBuilder();
		int ch = 0;
		
		//super.read
		try {
			while (ch != -1 )
			{
				ch = read();
				//read(); // Advance the reader
				builder.append((char)ch);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return builder.toString();
	}
	public  String readUntil( char terminator) 
	{
		return readUntil(terminator, false);
	}

	public  String readUntil( char terminator, boolean inclusive) 
	{
		
		// Rather not allocate an array to use ReadUntil(TextReader, params char[]) so we'll just call the predicate version directly
		return readUntil(c -> c == terminator, inclusive);
	}

	public  String readUntil( Character... terminators) throws ArgumentNullException 
	{
		// NOTE: Using named parameters would be difficult here, hence the inline comment
		return readUntil( false,  terminators);
	}

	public  String readUntil( boolean inclusive, Character... terminators) throws ArgumentNullException 
	{
		
		if (terminators == null)
		{
			throw new ArgumentNullException("terminators");
		}

		return readUntil(c -> 
		Arrays.asList(terminators).stream().anyMatch(p->p==c)
		//CollectionHelper.any(Arrays.asList(terminators), tc -> tc == c)
		,  inclusive);
	}

	public  String readUntil(Predicate<Character> condition) 
	{
		return readUntil(condition, false);
	}

	public  String readUntil( Predicate<Character> condition, boolean inclusive) 
	{
		if (condition == null)
		{
			//throw new ArgumentNullException("condition");
		}

		StringBuilder builder = new StringBuilder();
		int ch = -1;
		//super.read
		try {
			while ((ch = read()) != -1 && !condition.test((char)ch))
			{
				//read(); // Advance the reader
				builder.append((char)ch);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (inclusive)
		{
			  int l;
			try {
				l = read();
				if(l!=-1){
					  builder.append((char)l);
				  }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
			
		}

		return builder.toString();
	}

	public  String readWhile(Predicate<Character> condition) 
	{
		return readWhile(condition,  false);
	}

	public  String readWhile(Predicate<Character> condition, boolean inclusive) 
	{
		
		if (condition == null)
		{
			//throw new ArgumentNullException("condition");
		}

		return readUntil(ch -> !condition.test(ch), inclusive);
	}

	public  String readWhiteSpace(TextReader reader) 
	{
		return readWhile(c -> Character.isWhitespace(c));
	}

	public  String readUntilWhiteSpace()
	{
		return readUntil(c -> Character.isWhitespace(c));
	}
	
	public int peek() {
		// TODO Auto-generated method stub
		return 0;
	}

}
