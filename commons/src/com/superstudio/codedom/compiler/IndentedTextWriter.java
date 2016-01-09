package com.superstudio.codedom.compiler;

import com.superstudio.commons.CultureInfo;
import com.superstudio.commons.io.TextWriter;

import java.io.FileNotFoundException;
import java.io.IOException;

public class IndentedTextWriter extends TextWriter {
	private TextWriter writer;

	private int indentLevel;

	private boolean tabsPending;

	private String tabString;

	public static final String DefaultTabString = "    ";

	@Override
	public String getEncoding() {
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
	public void close() throws IOException {
		this.writer.close();
	}

	@Override
	public void flush() throws IOException {
		this.writer.flush();
	}

	protected void outputTabs() {
		if (this.tabsPending) {
			for (int i = 0; i < this.indentLevel; i++) {
				try {
					this.writer.write(this.tabString);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			this.tabsPending = false;
		}
	}

	@Override
	public void write(String s) throws IOException {
		this.outputTabs();
		if (s != null)
			this.writer.write(s);
	}

	@Override
	public void write(boolean value) throws IOException {
		this.outputTabs();
		this.writer.write(value);
	}

	@Override
	public void write(char value) throws IOException {
		this.outputTabs();
		this.writer.write(value);
	}

	@Override
	public void write(char[] buffer) throws IOException {
		this.outputTabs();
		this.writer.write(buffer);
	}

	@Override
	public void write(char[] buffer, int index, int count) throws IOException {
		this.outputTabs();
		this.writer.write(buffer, index, count);
	}

	@Override
	public void write(double value) throws IOException {
		this.outputTabs();
		this.writer.write(value);
	}

	@Override
	public void write(float value) throws IOException {
		this.outputTabs();
		this.writer.write(value);
	}

	@Override
	public void write(int value) throws IOException {
		this.outputTabs();
		this.writer.write(value);
	}

	@Override
	public void write(long value) throws IOException {
		this.outputTabs();
		this.writer.write(value);
	}

	@Override
	public void write(Object value) throws IOException {
		this.outputTabs();
		this.writer.write(value);
	}

	/*
	 * @Override public void write(String format, Object arg0) {
	 * this.outputTabs(); this.writer.write(format, arg0); }
	 * 
	 * @Override public void write(String format, Object arg0, Object arg1) {
	 * this.outputTabs(); this.writer.write(format, arg0, arg1); }
	 */

	@Override
	public void write(String format, Object... arg) throws IOException {
		this.outputTabs();
		this.writer.write(format, arg);
	}

	public final void writeLineNoTabs(String s) throws IOException {
		this.writer.writeLine(s);
	}

	@Override
	public void writeLine(String s) throws IOException {
		this.outputTabs();
		this.writer.writeLine(s);
		this.tabsPending = true;
	}

	@Override
	public void writeLine() throws IOException {
		this.outputTabs();
		this.writer.writeLine();
		this.tabsPending = true;
	}

	@Override
	public void writeLine(boolean value) throws IOException {
		this.outputTabs();
		this.writer.writeLine(value);
		this.tabsPending = true;
	}

	@Override
	public void writeLine(char value) throws IOException {
		this.outputTabs();
		this.writer.writeLine(value);
		this.tabsPending = true;
	}

	@Override
	public void writeLine(char[] buffer) throws IOException {
		this.outputTabs();
		this.writer.writeLine(buffer);
		this.tabsPending = true;
	}

	@Override
	public void writeLine(char[] buffer, int index, int count) throws IOException {
		this.outputTabs();
		this.writer.writeLine(buffer, index, count);
		this.tabsPending = true;
	}

	@Override
	public void writeLine(double value) throws IOException {
		this.outputTabs();
		this.writer.writeLine(value);
		this.tabsPending = true;
	}

	@Override
	public void writeLine(float value) throws IOException {
		this.outputTabs();
		this.writer.writeLine(value);
		this.tabsPending = true;
	}

	/*
	 * @Override public void writeLine(int value) { this.outputTabs();
	 * this.writer.writeLine(value); this.tabsPending = true; }
	 * 
	 * @Override public void writeLine(long value) { this.outputTabs();
	 * this.writer.writeLine(value); this.tabsPending = true; }
	 */

	@Override
	public void writeLine(Object value) throws IOException {
		this.outputTabs();
		this.writer.writeLine(value);
		this.tabsPending = true;
	}

	@Override
	public void writeLine(String format, Object arg0) throws IOException {
		this.outputTabs();
		this.writer.writeLine(format, arg0);
		this.tabsPending = true;
	}

	/*
	 * @Override public void writeLine(String format, Object arg0, Object arg1)
	 * { this.outputTabs(); this.writer.writeLine(format, arg0, arg1);
	 * this.tabsPending = true; }
	 */

	@Override
	public void writeLine(String format, Object... arg) throws IOException {
		this.outputTabs();
		this.writer.writeLine(format, arg);
		this.tabsPending = true;
	}


	@Override
	public void writeLine(int value) throws IOException {
		this.outputTabs();
		this.writer.writeLine(value);
		this.tabsPending = true;
	}

	public final void internalOutputTabs() throws IOException {
		for (int i = 0; i < this.indentLevel; i++) {
			this.writer.write(this.tabString);
		}
	}
}