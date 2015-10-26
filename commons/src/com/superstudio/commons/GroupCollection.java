package com.superstudio.commons;

import java.util.List;

public class GroupCollection<TKey,TItem> {
	private TKey key;

	public TKey getKey() {
		return key;
	}

	public void setKey(TKey key) {
		this.key = key;
	}
	
	public List<TItem> getItems() {
		return items;
	}

	public void setItems(List<TItem> items) {
		this.items = items;
	}
	
	

	private List<TItem> items;
}
