package com.bagongkia.telegram.bot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.bagongkia.telegram.bot.model.pk.EventMemberPK;

import lombok.Getter;
import lombok.Setter;

@IdClass(EventMemberPK.class)
@Getter
@Setter
@Entity
@Table(name = "event_member")
public class EventMember extends BaseModel {

	@Id
	@Column(name = "event_id")
	private Long eventId;
	
	@Id
	@Column(name = "chat_id")
	private Long chatId;
	
}
