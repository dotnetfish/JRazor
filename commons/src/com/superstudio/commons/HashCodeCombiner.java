package com.superstudio.commons;

public class HashCodeCombiner

{
	private long _combinedHash64 = 0x1505L;

	public int getCombinedHash() {
		return ((Object) _combinedHash64).hashCode();
		// return _combinedHash64;
	}

	public HashCodeCombiner Add(Iterable<Object> e) {
		if (e == null) {
			Add(0);
		} else {
			int count = 0;
			for (Object o : e) {
				Add(o);
				count++;
			}
			Add(count);
		}
		return this;
	}

	public HashCodeCombiner Add(int i) {
		_combinedHash64 = ((_combinedHash64 << 5) + _combinedHash64) ^ i;
		return this;
	}

	public HashCodeCombiner Add(Object o) {
		int hashCode = (o != null) ? o.hashCode() : 0;
		Add(hashCode);
		return this;
	}

	public static HashCodeCombiner Start() {
		return new HashCodeCombiner();
	}

	public void AddInt32(int hashCode) {
		// TODO Auto-generated method stub
		Add(hashCode);
	}

	public void AddObject(String _genericTypeFormat) {
		// TODO Auto-generated method stub
		Add(_genericTypeFormat);
	}

}
