package com.bagongkia.telegram.bot.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.bagongkia.telegram.bot.enumeration.ChatStatusEnum;
import com.bagongkia.telegram.bot.enumeration.EventStatusEnum;
import com.bagongkia.telegram.bot.exception.EventException;
import com.bagongkia.telegram.bot.model.Event;
import com.bagongkia.telegram.bot.repository.EventRepository;
import com.bagongkia.telegram.bot.util.RandomString;

@Service
public class EventService {

	@Autowired
	private EventRepository eventRepository;
	
	public void save(Event event) {
		eventRepository.save(event);
	}
	
	public void createEvent(Long chatId) {
		List<Event> initEvents = eventRepository.findByChatIdAndStatus(chatId, EventStatusEnum.INIT.getCode());
		if (initEvents == null || initEvents.isEmpty()) {
			this.save(Event.builder().chatId(chatId).status(EventStatusEnum.INIT.getCode()).build());
		}
	}

	public void initEvent(Long chatId, String chatStatus, String message) {
		List<Event> initEvents = eventRepository.findByChatIdAndStatus(chatId, EventStatusEnum.INIT.getCode());
		if (initEvents != null && !initEvents.isEmpty()) {
			Event event = initEvents.get(0);
			if (ChatStatusEnum.INPUT_EVENT_NAME.getCode().equals(chatStatus)) {
				event.setEventName(message);
			} else if (ChatStatusEnum.INPUT_EVENT_LOCATION.getCode().equals(chatStatus)) {
				event.setEventLocation(message);
			}
			this.save(event);
		}
	}
	
	public String inputEventDate(Long chatId, String text) throws ParseException {
		List<Event> initEvents = eventRepository.findByChatIdAndStatus(chatId, EventStatusEnum.INIT.getCode());
		if (initEvents != null && !initEvents.isEmpty()) {
			Event event = initEvents.get(0);
			
			SimpleDateFormat dateFmt = new SimpleDateFormat("dd-MMM-yyyy");
			event.setEventDate(dateFmt.parse(text));
			event.setEventCode(getEventCode());
			event.setStatus(EventStatusEnum.READY.getCode());
			event.setCreatedTime(new Date());
			
			this.save(event);
			
			StringBuilder messageBuilder = new StringBuilder()
					.append("<strong>Event Code : </strong>").append(event.getEventCode()).append("\n")
					.append("<strong>Location   : </strong>").append(event.getEventLocation()).append("\n")
					.append("<strong>Event Date : </strong>").append(text).append("\n")
					.append("Your event is created. Start your event by send /start_event.");
			
			return messageBuilder.toString();
		}
		return null;
	}

	public SendMessage startEvent(Long chatId) throws EventException {
		List<Event> readyEvents = eventRepository.findByChatIdAndStatus(chatId, EventStatusEnum.READY.getCode());
		if (readyEvents != null && !readyEvents.isEmpty()) {
			List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
			for (Event event : readyEvents) {
				InlineKeyboardButton keyboardButton = new InlineKeyboardButton().setText(event.getEventCode()).setCallbackData(event.getEventCode());
				keyboardButtons.add(keyboardButton);
			}
			List<List<InlineKeyboardButton>> keyboardButtonsList = new ArrayList<>();
			keyboardButtonsList.add(keyboardButtons);
			
			return new SendMessage()
	                .setChatId(chatId)
	                .setText(ChatStatusEnum.START_EVENT_CODE.getMessage())
	                .setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(keyboardButtonsList))
	                .setParseMode("HTML");
		} else {
			return new SendMessage()
	                .setChatId(chatId)
	                .setText("No event. Create your event by send /create_event.")
	                .setParseMode("HTML");
		}
	}
	
	public SendMessage startEvent(Long chatId, String data) {
		Optional<Event> optEvent = eventRepository.findByEventCodeAndStatus(data, EventStatusEnum.READY.getCode());
		Optional<Event> optOnGoingEvent = eventRepository.findByEventCodeAndStatus(data, EventStatusEnum.ONGOING.getCode());
		if (optEvent.isPresent()) {
			Event event = optEvent.get();
			event.setStatus(EventStatusEnum.ONGOING.getCode());
			this.save(event);
			
			return new SendMessage()
	                .setChatId(chatId)
	                .setText("Event " + data + " has been started. Now participants can join.")
	                .setParseMode("HTML");
		} else if (optOnGoingEvent.isPresent()) {
			return new SendMessage()
	                .setChatId(chatId)
	                .setText("Event " + data + " already started.")
	                .setParseMode("HTML");
		} else {
			return new SendMessage()
	                .setChatId(chatId)
	                .setText("Event " + data + " is not found.")
	                .setParseMode("HTML");
		}
	}

	public void joinEvent(Long chatId, String text, String firstName) {
		// TODO Auto-generated method stub
		
	}
	
	private String getEventCode() {
		String generatedString = new RandomString(4).nextString();
		
		if (eventRepository.findByEventCode(generatedString).isPresent()) {
			return this.getEventCode();
		}
		return "#" + generatedString.toUpperCase();
	}
}
