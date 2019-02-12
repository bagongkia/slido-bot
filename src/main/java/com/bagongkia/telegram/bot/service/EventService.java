package com.bagongkia.telegram.bot.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bagongkia.telegram.bot.enumeration.ChatStatusEnum;
import com.bagongkia.telegram.bot.enumeration.EventStatusEnum;
import com.bagongkia.telegram.bot.exception.EventException;
import com.bagongkia.telegram.bot.model.Event;
import com.bagongkia.telegram.bot.repository.EventRepository;

@Service
public class EventService {

	@Autowired
	private EventRepository eventRepository;
	
	public void save(Event event) {
		eventRepository.save(event);
	}
	
	public void createEvent(Long chatId) {
		Optional<Event> optEvent = eventRepository.findByChatIdAndStatus(chatId, EventStatusEnum.INIT.getCode());
		if (!optEvent.isPresent()) {
			this.save(Event.builder().chatId(chatId).status(EventStatusEnum.INIT.getCode()).build());
		}
	}

	public void initEvent(Long chatId, String chatStatus, String message) throws ParseException {
		Optional<Event> optEvent = eventRepository.findByChatIdAndStatus(chatId, EventStatusEnum.INIT.getCode());
		if (optEvent.isPresent()) {
			Event event = optEvent.get();
			if (ChatStatusEnum.INPUT_EVENT_NAME.getCode().equals(chatStatus)) {
				event.setEventName(message);
			} else if (ChatStatusEnum.INPUT_EVENT_LOCATION.getCode().equals(chatStatus)) {
				event.setEventLocation(message);
			} else if (ChatStatusEnum.INPUT_EVENT_DATE.getCode().equals(chatStatus)) {
				SimpleDateFormat dateFmt = new SimpleDateFormat("dd-MMM-yyyy");
				event.setEventDate(dateFmt.parse(message));
				event.setStatus(EventStatusEnum.READY.getCode());
			}
			this.save(event);
		}
	}

	public void startEvent(Long chatId, String text) throws EventException {
		// TODO Auto-generated method stub
		
	}

	public void joinEvent(Long chatId, String text, String firstName) {
		// TODO Auto-generated method stub
		
	}
}
