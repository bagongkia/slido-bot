package com.bagongkia.telegram.bot.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Event extends BaseModel {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "chat_id")
	private Long chatId;
	
	@Column(name = "event_code")
	private String eventCode;
	
	@Column(name = "event_name")
	private String eventName;
	
	@Column(name = "event_location")
	private String eventLocation;
	
	@Column(name = "event_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date eventDate;
	
	@Column(name = "status")
	private String status;
	
}
