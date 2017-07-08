package com.superstudio.commons;


import com.superstudio.commons.exception.ArgumentNullException;

import java.io.*;
import java.util.Arrays;
import java.util.function.Predicate;



public class TextReader extends StringReader {

	public TextReader(){
		this("");
	}
	private  String source="";
	public TextReader(String in) {
		super(in);
		source=in;
		// TODO Auto-generated constructor stub
	}
	
	

	public String readToEnd()  {
		return  source;

		/*StringBuffer sb = new StringBuffer();

		try {

			FileInputStream fis = new FileInputStream(fileName);

			InputStreamReader isr = new InputStreamReader(fis, encoding);

			BufferedReader br = new BufferedReader(isr);

			String line = null;

			while ((line = br.readLine()) != null) {

				sb.append(line);

				sb.append(SEP);

			}

			br.close();

			isr.close();

			fis.close();

		} catch (Exception e) {

			e.printStackTrace();

		}

		return sb.toString();*/


	/*	StringBuilder builder = new StringBuilder();
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
		return new String(builder.toString(),"UTF-8");*/
	}
	public  String readUntil( char terminator) throws IOException,ArgumentNullException
	{
		return readUntil(terminator, false);
	}

	public  String readUntil( char terminator, boolean inclusive) throws IOException,ArgumentNullException
	{
		
		// Rather not allocate an array to use ReadUntil(TextReader, params char[]) so we'll just call the predicate version directly
		return readUntil(c -> c == terminator, inclusive);
	}

	public  String readUntil( Character... terminators) throws ArgumentNullException ,IOException
	{
		// NOTE: Using named parameters would be difficult here, hence the inline comment
		return readUntil( false,  terminators);
	}

	public  String readUntil( boolean inclusive, Character... terminators) throws ArgumentNullException ,IOException
	{
		
		if (terminators == null)
		{
			throw new ArgumentNullException("terminators");
		}

		return readUntil(c -> 
		Arrays.asList(terminators).stream().anyMatch(p->p==c)
		,  inclusive);
	}

	public  String readUntil(Predicate<Character> condition) throws ArgumentNullException,IOException
	{
		return readUntil(condition, false);
	}

	public  String readUntil( Predicate<Character> condition, boolean inclusive) throws ArgumentNullException,IOException
	{
		if (condition == null)
		{
			throw new ArgumentNullException("condition");
		}

		StringBuilder builder = new StringBuilder();
		int ch = -1;
			while ((ch = read()) != -1 && !condition.test((char)ch))
			{
				builder.append((char)ch);
			}

		if (inclusive)
		{
			  int l;
				l = read();
				if(l!=-1){
					  builder.append((char)l);
				  }
		}

		return builder.toString();
	}

	public  String readWhile(Predicate<Character> condition) throws IOException,ArgumentNullException
	{
		return readWhile(condition,  false);
	}

	public  String readWhile(Predicate<Character> condition, boolean inclusive) throws IOException,ArgumentNullException
	{
		
		if (condition == null)
		{
			throw new ArgumentNullException("condition");
		}

		return readUntil(ch -> !condition.test(ch), inclusive);
	}

	public  String readWhiteSpace(TextReader reader) throws IOException,ArgumentNullException
	{
		return readWhile(c -> Character.isWhitespace(c));
	}

	public  String readUntilWhiteSpace() throws IOException,ArgumentNullException
	{
		return readUntil(c -> Character.isWhitespace(c));
	}
	
	public int peek() {

		try {
			return super.read();
		} catch (IOException e) {
			return  0;
		}
	}

}
