package com.superstudio.commons;

import java.util.UUID;

public class KeyGenerator {
	private UUID uuID;
	
	public KeyGenerator(){

		setUuID(UUID.randomUUID());
	}

	public UUID getUuID() {
		return uuID;
	}

	public void setUuID(UUID uuID) {
		this.uuID = uuID;
	}
}
