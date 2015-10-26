package com.superstudio.codedom.compiler;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import com.superstudio.codedom.*;

 
public class CompilerErrorCollection extends CollectionBase<CompilerError> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2406246001640652572L;

	public final CompilerError getItem(int index) {
		return (CompilerError) get(index);
	}

	public final void setItem(int index, CompilerError value) {
		add(index, value);
	}

	public final boolean getHasErrors() throws IOException {
		if (super.size() > 0) {
			Iterator<CompilerError> enumerator = super.iterator();
			try {
				while (enumerator.hasNext()) {
					if (!(enumerator.next()).getIsWarning()) {
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
			Iterator<CompilerError> enumerator = super.iterator();
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

	public final int Add(CompilerError value) {
		add(value);
		return size();
	}

	public final void addAll(CompilerError[] value) {
		if (value == null) {
			throw new IllegalArgumentException("value");
		}
		for (int i = 0; i < value.length; i++) {
			this.Add(value[i]);
		}
	}

	public final void addAll(CompilerErrorCollection value) {
		if (value == null) {
			throw new IllegalArgumentException("value");
		}
		int count = value.size();
		for (int i = 0; i < count; i++) {
			this.Add(value.getItem(i));
		}
	}

	public final boolean Contains(CompilerError value) {
		return contains(value);
	}

	public final void CopyTo(CompilerError[] array, int index) {
		subList(index, size() - index - 1).toArray(array);
	}

	/*
	 * public final int indexOf(CompilerError value) { return
	 * super.List.indexOf(value); }
	 */
	public final void Insert(int index, CompilerError value) {
		add(index, value);
	}

	public final void Remove(CompilerError value) {
		remove(value);
	}
}