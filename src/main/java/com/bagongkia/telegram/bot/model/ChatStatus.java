package com.bagongkia.telegram.bot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ChatStatus {

	@Id
	@Column(name = "chat_id")
	private Long chatId;

	@Column(name = "status")	
	private String status;
}
