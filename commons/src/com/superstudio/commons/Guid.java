package com.superstudio.commons;

import java.util.UUID;

public class Guid {
	private UUID uuID;
	
	public Guid(){
		setUuID(UUID.randomUUID());
	}

	public UUID getUuID() {
		return uuID;
	}

	public void setUuID(UUID uuID) {
		this.uuID = uuID;
	}
}
