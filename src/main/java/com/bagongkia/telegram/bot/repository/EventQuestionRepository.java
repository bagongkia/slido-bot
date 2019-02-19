package com.bagongkia.telegram.bot.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bagongkia.telegram.bot.model.EventQuestion;

public interface EventQuestionRepository extends CrudRepository<EventQuestion, Long> {

	List<EventQuestion> findByEventId(Long eventId);
}
