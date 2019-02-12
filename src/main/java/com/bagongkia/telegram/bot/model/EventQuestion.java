package com.bagongkia.telegram.bot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "event_question")
public class EventQuestion extends BaseModel {
	
	@Id
	@Column(name = "id")
	private Long id;
	
	@Column(name = "chat_id")
	private Long chatId;
	
	@Column(name = "event_id")
	private Long eventId;
	
	@Column(name = "question")
	private String question;
}
