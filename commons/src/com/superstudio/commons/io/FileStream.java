package com.superstudio.commons.io;

import java.io.*;
import java.util.stream.Stream;

public class FileStream implements AutoCloseable {

	private FileOutputStream outPut;

	private FileInputStream inPut;
	private FileAccess access = FileAccess.ReadAndWrite;
	private String fileName;
	private FileMode mode = FileMode.CreateNew;

	public FileStream(String fileName) throws FileNotFoundException {
		super();
		outPut = new FileOutputStream(fileName);
		inPut = new FileInputStream(fileName);
	}

	public FileStream(String fileName, FileMode createnew, FileAccess access) throws FileNotFoundException {
		// TODO Auto-generated constructor stub
		open();
	}

	public FileStream(String s, FileMode create, FileAccess write, FileShare read) {

	}

	public void open() throws FileNotFoundException {
		if (access == FileAccess.Write) {
			outPut = new FileOutputStream(fileName);

		}

		if (access == FileAccess.Read) {
			inPut = new FileInputStream(fileName);
		}
	}

	/*
	 * public FileStream OpenRead(){ open(); }
	 */

	public void Write(String content) throws IOException {
		outPut.write(content.getBytes());
	}

	public void Write(byte[] bytes) throws IOException {
		outPut.write(bytes);
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		if (outPut != null) {
			outPut.close();
		}
		if (inPut != null) {
			inPut.close();
		}
	}

	public void Flush() throws IOException {
		// TODO Auto-generated method stub
		outPut.flush();
	}

	public long getLength() throws IOException {
		// TODO Auto-generated method stub
		return inPut.available();
	}

	public void Close() throws IOException {
		if (inPut != null)
			inPut.close();
		if (outPut != null)
			outPut.close();
	}

}
