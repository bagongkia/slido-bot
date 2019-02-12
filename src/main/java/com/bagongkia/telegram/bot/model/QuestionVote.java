package com.bagongkia.telegram.bot.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.bagongkia.telegram.bot.model.pk.QuestionVotePK;

import lombok.Getter;
import lombok.Setter;

@IdClass(QuestionVotePK.class)
@Getter
@Setter
@Entity
@Table(name = "question_vote")
public class QuestionVote extends BaseModel {
	
	@Id
	@Column(name = "question_id")
	private Long questionId;
	
	@Id
	@Column(name = "chat_id")
	private Long chatId;
}
