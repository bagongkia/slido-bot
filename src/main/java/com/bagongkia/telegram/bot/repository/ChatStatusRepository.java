package com.bagongkia.telegram.bot.repository;

import org.springframework.data.repository.CrudRepository;

import com.bagongkia.telegram.bot.model.ChatStatus;

public interface ChatStatusRepository extends CrudRepository<ChatStatus, Long> {

	ChatStatus findByChatId(Long chatId);
}
