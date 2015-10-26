package com.superstudio.codedom.compiler;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.Encoding;
import com.superstudio.commons.io.TextWriter;

public class IndentedTextWriter extends TextWriter {
	private TextWriter writer;

	private int indentLevel;

	private boolean tabsPending;

	private String tabString;

	public static final String DefaultTabString = "    ";

	@Override
	public Encoding getEncoding() {
		return this.writer.getEncoding();
	}

	@Override
	public String getNewLine() {
		return this.writer.getNewLine();
	}

	@Override
	public void setNewLine(String value) {
		this.writer.setNewLine(value);
	}

	public final int getIndent() {
		return this.indentLevel;
	}

	public final void setIndent(int value) {
		if (value < 0) {
			value = 0;
		}
	
		this.indentLevel = value;
	}

	public final TextWriter getInnerWriter() {
		return this.writer;
	}

	public final String getTabString() {
		return this.tabString;
	}

	public IndentedTextWriter(TextWriter writer) throws FileNotFoundException {
		this(writer, "    ");
	}

	public IndentedTextWriter(TextWriter writer, String tabString) throws FileNotFoundException {
		super(CultureInfo.InvariantCulture);
		this.writer = writer;
		this.tabString = tabString;
		this.indentLevel = 0;
		this.tabsPending = false;
	}

	@Override
	public void Close() throws IOException {
		this.writer.Close();
	}

	@Override
	public void Flush() throws IOException {
		this.writer.Flush();
	}

	protected void OutputTabs() {
		if (this.tabsPending) {
			for (int i = 0; i < this.indentLevel; i++) {
				try {
					this.writer.Write(this.tabString);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.tabsPending = false;
		}
	}

	@Override
	public void Write(String s) throws IOException {
		this.OutputTabs();
		if (s != null)
			this.writer.Write(s);
	}

	@Override
	public void Write(boolean value) throws IOException {
		this.OutputTabs();
		this.writer.Write(value);
	}

	@Override
	public void Write(char value) throws IOException {
		this.OutputTabs();
		this.writer.Write(value);
	}

	@Override
	public void Write(char[] buffer) throws IOException {
		this.OutputTabs();
		this.writer.Write(buffer);
	}

	@Override
	public void Write(char[] buffer, int index, int count) throws IOException {
		this.OutputTabs();
		this.writer.Write(buffer, index, count);
	}

	@Override
	public void Write(double value) throws IOException {
		this.OutputTabs();
		this.writer.Write(value);
	}

	@Override
	public void Write(float value) throws IOException {
		this.OutputTabs();
		this.writer.Write(value);
	}

	@Override
	public void Write(int value) throws IOException {
		this.OutputTabs();
		this.writer.Write(value);
	}

	@Override
	public void Write(long value) throws IOException {
		this.OutputTabs();
		this.writer.Write(value);
	}

	@Override
	public void Write(Object value) throws IOException {
		this.OutputTabs();
		this.writer.Write(value);
	}

	/*
	 * @Override public void Write(String format, Object arg0) {
	 * this.OutputTabs(); this.writer.Write(format, arg0); }
	 * 
	 * @Override public void Write(String format, Object arg0, Object arg1) {
	 * this.OutputTabs(); this.writer.Write(format, arg0, arg1); }
	 */

	@Override
	public void Write(String format, Object... arg) throws IOException {
		this.OutputTabs();
		this.writer.Write(format, arg);
	}

	public final void WriteLineNoTabs(String s) throws IOException {
		this.writer.WriteLine(s);
	}

	@Override
	public void WriteLine(String s) throws IOException {
		this.OutputTabs();
		this.writer.WriteLine(s);
		this.tabsPending = true;
	}

	@Override
	public void WriteLine() throws IOException {
		this.OutputTabs();
		this.writer.WriteLine();
		this.tabsPending = true;
	}

	@Override
	public void WriteLine(boolean value) throws IOException {
		this.OutputTabs();
		this.writer.WriteLine(value);
		this.tabsPending = true;
	}

	@Override
	public void WriteLine(char value) throws IOException {
		this.OutputTabs();
		this.writer.WriteLine(value);
		this.tabsPending = true;
	}

	@Override
	public void WriteLine(char[] buffer) throws IOException {
		this.OutputTabs();
		this.writer.WriteLine(buffer);
		this.tabsPending = true;
	}

	@Override
	public void WriteLine(char[] buffer, int index, int count) throws IOException {
		this.OutputTabs();
		this.writer.WriteLine(buffer, index, count);
		this.tabsPending = true;
	}

	@Override
	public void WriteLine(double value) throws IOException {
		this.OutputTabs();
		this.writer.WriteLine(value);
		this.tabsPending = true;
	}

	@Override
	public void WriteLine(float value) throws IOException {
		this.OutputTabs();
		this.writer.WriteLine(value);
		this.tabsPending = true;
	}

	/*
	 * @Override public void WriteLine(int value) { this.OutputTabs();
	 * this.writer.WriteLine(value); this.tabsPending = true; }
	 * 
	 * @Override public void WriteLine(long value) { this.OutputTabs();
	 * this.writer.WriteLine(value); this.tabsPending = true; }
	 */

	@Override
	public void WriteLine(Object value) throws IOException {
		this.OutputTabs();
		this.writer.WriteLine(value);
		this.tabsPending = true;
	}

	@Override
	public void WriteLine(String format, Object arg0) throws IOException {
		this.OutputTabs();
		this.writer.WriteLine(format, arg0);
		this.tabsPending = true;
	}

	/*
	 * @Override public void WriteLine(String format, Object arg0, Object arg1)
	 * { this.OutputTabs(); this.writer.WriteLine(format, arg0, arg1);
	 * this.tabsPending = true; }
	 */

	@Override
	public void writeLine(String format, Object... arg) throws IOException {
		this.OutputTabs();
		this.writer.writeLine(format, arg);
		this.tabsPending = true;
	}

	
	
	 
	@Override
	public void WriteLine(int value) throws IOException {
		this.OutputTabs();
		this.writer.WriteLine(value);
		this.tabsPending = true;
	}

	public final void InternalOutputTabs() throws IOException {
		for (int i = 0; i < this.indentLevel; i++) {
			this.writer.Write(this.tabString);
		}
	}
}