package com.superstudio.codedom.compiler;



import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * @author cloudartisan
 */
public class CompilerErrorCollection extends ArrayList<CompilerError> implements Serializable {
	public final CompilerError getItem(int index) {
		return get(index);
	}

	public final void setItem(int index, CompilerError value) {
		add(index, value);
	}

	public final boolean hasErrors() throws IOException {
		if (super.size() > 0) {
			Iterator<CompilerError> enumerator = super.iterator();
			try {
				while (enumerator.hasNext()) {
					if (!( enumerator.next()).getIsWarning()) {
						return true;
					}
				}
			} finally {
				java.io.Closeable disposable = (java.io.Closeable) ((enumerator instanceof java.io.Closeable)
						? enumerator : null);
				if (disposable != null) {
					disposable.close();
				}
			}
			return false;
		}
		return false;
	}

	public final boolean getHasWarnings() throws IOException {
		if (super.size() > 0) {
			Iterator enumerator = super.iterator();
			try {
				while (enumerator.hasNext()) {
					if (((CompilerError) enumerator.next()).getIsWarning()) {
						return true;
					}
				}
			} finally {
				java.io.Closeable disposable = (java.io.Closeable) ((enumerator instanceof java.io.Closeable)
						? enumerator : null);
				if (disposable != null) {
					disposable.close();
				}
			}
			return false;
		}
		return false;
	}

	public CompilerErrorCollection() {
	}

	public CompilerErrorCollection(CompilerErrorCollection value) {
		this.addAll(value);
	}

	public CompilerErrorCollection(CompilerError[] value) {
		this.addAll(value);
	}

	@Override
	public final boolean add(CompilerError value) {
		return add(value);
		//return size();
	}

	public final void addAll(CompilerError[] value) {
		if (value == null) {
			throw new IllegalArgumentException("value");
		}
		for (int i = 0; i < value.length; i++) {
			this.add(value[i]);
		}
		this.addAll(value);
	}

	public final void addAll(CompilerErrorCollection value) {
		if (value == null) {
			throw new IllegalArgumentException("value");
		}
		int count = value.size();
		for (int i = 0; i < count; i++) {
			this.add(value.getItem(i));
		}
	}

	public final boolean contains(CompilerError value) {
		return contains(value);
	}

	public final void copyTo(CompilerError[] array, int index) {
		subList(index, size() - index - 1).toArray(array);
	}


	/*
	public final void insert(int index, CompilerError value) {
		add(index, value);
	}
*/
	/*public final void remove(CompilerError value) {

		remove(value);
	}*/
}