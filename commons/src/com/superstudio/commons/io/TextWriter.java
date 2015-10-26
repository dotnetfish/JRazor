package com.superstudio.commons.io;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;

import com.superstudio.commons.Encoding;
import com.superstudio.commons.csharpbridge.StringHelper;

public class TextWriter {

	private OutputStream writer;
	
	private Encoding encoding;
	
	private String newLine="\r\n";
	
	
	
	public TextWriter(String fileName,String info) throws FileNotFoundException{
		if(!File.Exists(fileName)){
			try {
				java.nio.file.Files.createFile(Paths.get(fileName), new FileAttribute[]{});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		writer=new FileOutputStream(fileName);
	}
	
	public TextWriter(String info ) throws FileNotFoundException{
		//writer=new FileOutputStream(fileName);
	}
	/*public String getNewLine() {
		// TODO Auto-generated method stub
		
		return null;
	}*/
	
	public Encoding getEncoding()
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
	

	

	
	public void Close() throws IOException
	{
		writer.close();
	}

	
	public void Flush() throws IOException
	{
		this.writer.flush();
	}

	

	
	public void Write(String s) throws IOException
	{
		
		this.writer.write(s.getBytes());
	}

	
	public void Write(boolean value) throws IOException
	{
		
		this.writer.write(String.valueOf(value).getBytes());
	}

	
	public void Write(char value) throws IOException
	{
		
		this.writer.write(value);
	}

	
	public void Write(char[] buffer) throws IOException
	{
		for(char ch:buffer){
			this.writer.write(ch);
			//this.writer.
		}
		
		
	}

	
	public void Write(char[] buffer, int index, int count) throws IOException
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

	
	public void Write(double value)throws IOException
	{
		
		this.writer.write(String.valueOf(value).getBytes());
	}

	
	public void Write(float value) throws IOException
	{
		
		this.writer.write(String.valueOf(value).getBytes());
	}

	
	public void Write(int value) throws IOException
	{
		
		this.writer.write(value);
	}

	
	public void Write(long value) throws IOException
	{
		
		this.writer.write(String.valueOf(value).getBytes());
	}

	
	public void Write(Object value) throws IOException
	{
		
		this.writer.write(value.toString().getBytes());
	}

	
	/*public void Write(String format, Object arg0)
	{
		
		this.writer.write(format, arg0);
	}

	
	public void Write(String format, Object arg0, Object arg1)
	{
		this.writer.write(format, arg0, arg1);
	}
*/
	
	public void Write(String format, Object... arg) throws IOException
	{
		
		this.writer.write(StringHelper.format(format, arg).getBytes());
	}

	

	
	public void WriteLine(String s) throws IOException
	{
		
		//this.writer.write("\r\n".getBytes());
		if(s!=null){
			this.writer.write(s.getBytes());
		}
		
		this.writer.write("\r\n".getBytes());
		
	}

	
	public void WriteLine() throws IOException
	{
		
		//this.writer.write("\r\n".getBytes());
	
		this.writer.write("\r\n".getBytes());
		
	}

	
	public void WriteLine(boolean value) throws IOException
	{
		
		//this.writer.write("\r\n".getBytes());
		this.writer.write(String.valueOf(value).getBytes());
		this.writer.write("\r\n".getBytes());
		
	}

	
	public void WriteLine(char value) throws IOException
	{
		
		//this.writer.write("\r\n".getBytes());
		this.writer.write(value);
		this.writer.write("\r\n".getBytes());
	
	}

	
	public void WriteLine(char[] buffer) throws IOException
	{
		
		//this.writer.write("\r\n".getBytes());
		Write(buffer);
		this.writer.write("\r\n".getBytes());
		
	}

	
	public void WriteLine(char[] buffer, int index, int count) throws IOException
	{
		
		//this.writer.WriteLine(buffer, index, count);
		//this.writer.write("\r\n".getBytes());
		Write(buffer);
		this.writer.write("\r\n".getBytes());
		
	}

	
	public void WriteLine(double value) throws IOException
	{
		
		//this.writer.WriteLine(value);
		//this.writer.write("\r\n".getBytes());
		this.writer.write(String.valueOf(value).getBytes());
		this.writer.write("\r\n".getBytes());
		
	}

	
	public void WriteLine(float value) throws IOException
	{
		//this.writer.write("\r\n".getBytes());
		this.writer.write(String.valueOf(value).getBytes());
		this.writer.write("\r\n".getBytes());
		//this.writer.WriteLine(value);
		
	}

	/*
	public void WriteLine(int value)
	{
		this.OutputTabs();
		this.writer.WriteLine(value);
		this.tabsPending = true;
	}

	
	public void WriteLine(long value)
	{
		this.OutputTabs();
		this.writer.WriteLine(value);
		this.tabsPending = true;
	}*/

	
	public void WriteLine(Object value) throws IOException
	{
		//this.writer.write("\r\n".getBytes());
		this.writer.write(value.toString().getBytes());
		this.writer.write("\r\n".getBytes());
		//this.writer.WriteLine(value);
		
	}

	
	public void WriteLine(String format, Object arg0) throws IOException
	{
		//this.writer.write("\r\n".getBytes());
		this.writer.write(StringHelper.format(format,arg0).getBytes());
		this.writer.write("\r\n".getBytes());
		//this.writer.WriteLine(format, arg0);
		
	}

	
	

	
	public void writeLine(String format, Object... arg) throws IOException
	{
		//this.writer.write("\r\n".getBytes());
		this.writer.write(StringHelper.format(format,arg).getBytes());
		this.writer.write("\r\n".getBytes());
	}

	
	public void WriteLine(int value) throws IOException
	{
		
		//this.writer.write("\r\n".getBytes());
		this.writer.write(value);
		this.writer.write("\r\n".getBytes());
		
	}

	public OutputStream asOutpuStream() {
		// TODO Auto-generated method stub
		return this.writer;
	}

}
