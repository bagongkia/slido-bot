package com.bagongkia.telegram.bot.enumeration;

import lombok.Getter;

@Getter
public enum EventStatusEnum {
	
	INIT("INIT"),
	READY("READY"),
	ONGOING("ONGOING"),
	DONE("DONE"),
	FULL("FULL"),
	CLOSED("CLOSED");
	
	private String code;
	
	private EventStatusEnum(String code) {
		this.code = code;
	}
}
