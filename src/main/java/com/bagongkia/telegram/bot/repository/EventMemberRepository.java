package com.bagongkia.telegram.bot.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.bagongkia.telegram.bot.model.EventMember;

public interface EventMemberRepository extends CrudRepository<EventMember, Long> {

	Optional<EventMember> findByEventIdAndChatId(Long eventId, Long chatId);
	
	List<EventMember> findByChatId(Long chatId);
}
