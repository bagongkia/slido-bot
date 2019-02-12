package com.bagongkia.telegram.bot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.bagongkia.telegram.bot.model.Event;

public interface EventRepository extends CrudRepository<Event, Long> {

	List<Event> findAll();
	Optional<Event> findByEventCode(String eventCode);
	Optional<Event> findByChatIdAndStatus(Long chatId, String status);
}
