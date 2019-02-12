package com.bagongkia.telegram.bot.model.pk;

import java.io.Serializable;

import lombok.Data;

@Data
public class QuestionVotePK implements Serializable {
	
	private static final long serialVersionUID = -3011992729819803230L;
	
	private Long questionId;
	private Long chatId;
}
