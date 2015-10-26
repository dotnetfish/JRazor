package com.superstudio.commons;

public class SimpleBitVector32 {

	private int data;

	SimpleBitVector32(int data) {
		this.data = data;
	}

	// private int data;

	public boolean get(int bit) {

		return (data & bit) == bit;
	}

	public void set(int bit, boolean value) {
		int _data = data;
		if (value) {
			data = _data | bit;
		} else {
			data = _data & ~bit;
		}
	}

	void Set(int bit) {
		data |= bit;
	}

	void Clear(int bit) {
		data &= ~bit;
	}

	void Toggle(int bit) {
		data ^= bit;
	}

	
	/*
	 * COPY_FLAG copies the value of flags from a source field into a
	 * destination field.
	 * 
	 * In the macro: + "&flag" limits the outer xor operation to just the flag
	 * we're interested in. + These are the results of the two xor operations:
	 *
	 * fieldDst fieldSrc inner xor outer xor 0 0 0 0 0 1 1 1 1 0 1 0 1 1 0 1
	 */

	void Copy(SimpleBitVector32 src, int bit) {
		data ^= (data ^ src.data) & bit;
	}

	public int getIntegerValue() {
		return data;
	}

	public void setIntegerValue(int interValue) {
		this.data = interValue;
	}

}
