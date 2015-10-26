package com.superstudio.commons;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.superstudio.commons.io.FileStream;
import com.superstudio.commons.io.TextWriter;

public class StreamWriter extends TextWriter implements AutoCloseable{

	private FileStream fileStream;
	public StreamWriter(FileStream fileStream, String encoding) throws FileNotFoundException {
		// TODO Auto-generated constructor stub
		super("");
		this.fileStream=fileStream;
	}

	public void Write(String content) throws IOException {
		// TODO Auto-generated method stub
		fileStream.Write(content);
		
	}

	/*public void WriteLine(String object) {
		// TODO Auto-generated method stub
		
	}*/

	public void WriteLine() throws IOException {
		// TODO Auto-generated method stub
		fileStream.Write("\r\n");
		
	}

	public void Flush() throws IOException {
		// TODO Auto-generated method stub
		fileStream.Flush();
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		fileStream.close();
	}

	
}
