package com.bagongkia.telegram.bot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.bagongkia.telegram.bot.model.QuestionVote;

public interface QuestionVoteRepository extends CrudRepository<QuestionVote, Long> {

	List<QuestionVote> findAll();
	List<QuestionVote> findByQuestionId(Long questionId);
	Optional<QuestionVote> findByQuestionIdAndChatId(Long questionId, Long chatId);
}
