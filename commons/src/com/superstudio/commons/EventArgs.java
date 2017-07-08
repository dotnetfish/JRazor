package com.superstudio.commons;

public interface EventArgs {

	EmptyEventArgs Empty = new EmptyEventArgs();

	public static class EmptyEventArgs implements EventArgs {

	}
}
