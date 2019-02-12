package com.bagongkia.telegram.bot.model.pk;

import java.io.Serializable;

import lombok.Data;

@Data
public class EventMemberPK implements Serializable {

	private static final long serialVersionUID = 8835192844443398889L;
	
	private Long eventId;
	private Long chatId;
}
