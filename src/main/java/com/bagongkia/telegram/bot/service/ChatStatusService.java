package com.bagongkia.telegram.bot.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bagongkia.telegram.bot.enumeration.ChatStatusEnum;
import com.bagongkia.telegram.bot.model.ChatStatus;
import com.bagongkia.telegram.bot.repository.ChatStatusRepository;

@Service
public class ChatStatusService {

	@Autowired
	private ChatStatusRepository chatStatusRepository;
	
	public void setChatStatus(Long chatId, ChatStatusEnum status) {
		chatStatusRepository.save(ChatStatus.builder().chatId(chatId).status(status.getCode()).build());
	}
	
	public Optional<ChatStatus> getStatus(Long chatId) {
		return chatStatusRepository.findById(chatId);
	}
}
