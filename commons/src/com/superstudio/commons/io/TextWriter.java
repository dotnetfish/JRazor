package com.superstudio.commons.io;

import com.superstudio.commons.Encoding;
import com.superstudio.commons.csharpbridge.StringHelper;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Paths;

public class TextWriter {

	private OutputStreamWriter writer;
	
	private String encoding;
	
	private String newLine="\r\n";
	
	
	
	public TextWriter(String fileName,String encoding) throws Exception{
		java.io.File file=new java.io.File(fileName);
		if(! file.exists()){
			try {
				java.nio.file.Files.createFile(Paths.get(fileName));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		writer=new OutputStreamWriter(new FileOutputStream(fileName),encoding);
		this.encoding= Encoding.UTF8;
	}
	
	public TextWriter(String info ) throws FileNotFoundException{
		//writer=new FileOutputStream(fileName);
	}
	/*public String getNewLine() {
		// TODO Auto-generated method stub
		
		return null;
	}*/
	
	public String getEncoding()
	{
		return encoding;
	}

	
	public String getNewLine()
	{
		return newLine;
	}
	
	public void setNewLine(String value)
	{
		 newLine = value;
	}

	/*public final int getIndent()
	{
		return this.indentLevel;
	}
	public final void setIndent(int value)
	{
		if (value < 0)
		{
			value = 0;
		}
		this.indentLevel = value;
	}*/

	/*public final TextWriter getInnerWriter()
	{
		return this.writer;
	}

	public final String getTabString()
	{
		return this.tabString;
	}
*/
	

	

	
	public void close() throws IOException
	{
		writer.close();
	}

	
	public void flush() throws IOException
	{
		this.writer.flush();
	}

	

	
	public void write(String s) throws IOException
	{
		
		this.writer.write(s);
	}

	
	public void write(boolean value) throws IOException
	{
		
		this.writer.write(String.valueOf(value));
	}

	
	public void write(char value) throws IOException
	{
		
		this.writer.write(value);
	}

	
	public void write(char[] buffer) throws IOException
	{
		for(char ch:buffer){
			this.writer.write(ch);
			//this.writer.
		}
		
		
	}

	
	public void write(char[] buffer, int index, int count) throws IOException
	{
		int start=0;
		for(char ch:buffer){
			if(start>=index && start<index+count){
				this.writer.write(ch);
			}
			start++;
			//this.writer.
		}
		//this.writer.write(buffer, index, count);
	}

	
	public void write(double value)throws IOException
	{
		
		this.writer.write(String.valueOf(value));
	}

	
	public void write(float value) throws IOException
	{
		
		this.writer.write(String.valueOf(value));
	}

	
	public void write(int value) throws IOException
	{
		
		this.writer.write(value);
	}

	
	public void write(long value) throws IOException
	{
		
		this.writer.write(String.valueOf(value));
	}

	
	public void write(Object value) throws IOException
	{
		
		this.writer.write(value.toString());
	}

	
	/*public void write(String format, Object arg0)
	{
		
		this.writer.write(format, arg0);
	}

	
	public void write(String format, Object arg0, Object arg1)
	{
		this.writer.write(format, arg0, arg1);
	}
*/
	
	public void write(String format, Object... arg) throws IOException
	{
		this.writer.write(String.format(format,arg));
		//this.writer.write(StringHelper.format(format, arg));
	}

	

	
	public void writeLine(String s) throws IOException
	{
		
		//this.writer.write("\r\n".getBytes());
		if(s!=null){
			this.writer.write(s);
		}
		
		this.writer.write("\r\n");
		
	}

	
	public void writeLine() throws IOException
	{
		
		//this.writer.write("\r\n".getBytes());
	
		this.writer.write("\r\n");
		
	}

	
	public void writeLine(boolean value) throws IOException
	{
		
		//this.writer.write("\r\n".getBytes());
		this.writer.write(String.valueOf(value));
		this.writer.write("\r\n");
		
	}

	
	public void writeLine(char value) throws IOException
	{
		
		//this.writer.write("\r\n".getBytes());
		this.writer.write(value);
		this.writer.write("\r\n");
	
	}

	
	public void writeLine(char[] buffer) throws IOException
	{
		
		//this.writer.write("\r\n".getBytes());
		write(buffer);
		this.writer.write("\r\n");
		
	}

	
	public void writeLine(char[] buffer, int index, int count) throws IOException
	{
		
		//this.writer.writeLine(buffer, index, count);
		//this.writer.write("\r\n".getBytes());
		write(buffer);
		this.writer.write("\r\n");
		
	}

	
	public void writeLine(double value) throws IOException
	{
		
		//this.writer.writeLine(value);
		//this.writer.write("\r\n".getBytes());
		this.writer.write(String.valueOf(value));
		this.writer.write("\r\n");
		
	}

	
	public void writeLine(float value) throws IOException
	{
		//this.writer.write("\r\n".getBytes());
		this.writer.write(String.valueOf(value));
		this.writer.write("\r\n");
		//this.writer.writeLine(value);
		
	}

	/*
	public void writeLine(int value)
	{
		this.outputTabs();
		this.writer.writeLine(value);
		this.tabsPending = true;
	}

	
	public void writeLine(long value)
	{
		this.outputTabs();
		this.writer.writeLine(value);
		this.tabsPending = true;
	}*/

	
	public void writeLine(Object value) throws IOException
	{
		//this.writer.write("\r\n");
		this.writer.write(value.toString());
		this.writer.write("\r\n");
		//this.writer.writeLine(value);
		
	}

	
	public void writeLine(String format, Object arg0) throws IOException
	{
		//this.writer.write("\r\n");
		this.writer.write(String.format(format,arg0));
		this.writer.write("\r\n");
		//this.writer.writeLine(format, arg0);
		
	}

	
	

	
	public void writeLine(String format, Object... arg) throws IOException
	{
		//this.writer.write("\r\n");
		this.writer.write(String.format(format,arg));
		this.writer.write("\r\n");
	}

	
	public void writeLine(int value) throws IOException
	{
		
		//this.writer.write("\r\n");
		this.writer.write(value);
		this.writer.write("\r\n");
		
	}

	/*public OutputStream asOutpuStream() {
		// TODO Auto-generated method stub
		return null;
	}*/

}
