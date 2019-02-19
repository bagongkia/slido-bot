package com.bagongkia.telegram.bot.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import com.bagongkia.telegram.bot.enumeration.ChatStatusEnum;
import com.bagongkia.telegram.bot.enumeration.EventStatusEnum;
import com.bagongkia.telegram.bot.exception.EventException;
import com.bagongkia.telegram.bot.model.Event;
import com.bagongkia.telegram.bot.model.EventMember;
import com.bagongkia.telegram.bot.model.EventQuestion;
import com.bagongkia.telegram.bot.model.QuestionVote;
import com.bagongkia.telegram.bot.repository.EventMemberRepository;
import com.bagongkia.telegram.bot.repository.EventQuestionRepository;
import com.bagongkia.telegram.bot.repository.EventRepository;
import com.bagongkia.telegram.bot.repository.QuestionVoteRepository;
import com.bagongkia.telegram.bot.util.RandomString;

@Service
public class EventService {

	@Autowired
	private EventRepository eventRepository;
	
	@Autowired
	private EventMemberRepository eventMemberRepository;
	
	@Autowired
	private EventQuestionRepository eventQuestionRepository;
	
	@Autowired
	private QuestionVoteRepository questionVoteRepository;
	
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
		List<Event> onGoingEvents = eventRepository.findByChatIdAndStatus(chatId, EventStatusEnum.ONGOING.getCode());
		
		if (onGoingEvents != null && !onGoingEvents.isEmpty()) {
			return new SendMessage()
	                .setChatId(chatId)
	                .setText("You can't start multiple events in a while.")
	                .setParseMode("HTML");
		} else if (optEvent.isPresent()) {
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

	public SendMessage joinEvent(Long chatId, String text, String firstName) {
		Optional<Event> optEvent = eventRepository.findByEventCodeAndStatus(text, EventStatusEnum.ONGOING.getCode());
		
		if (optEvent.isPresent()) {
			Optional<EventMember> optEventMember = eventMemberRepository.findByEventIdAndChatId(optEvent.get().getId(), chatId);
			if (!optEventMember.isPresent()) {
				EventMember eventMember = new EventMember();
				eventMember.setChatId(chatId);
				eventMember.setCreatedTime(new Date());
				eventMember.setEventId(optEvent.get().getId());
				
				eventMemberRepository.save(eventMember);
			}
			return new SendMessage()
	                .setChatId(chatId)
	                .setText("You've joinned the event.\n<b>Start asking question</b> by /ask_question.")
	                .setParseMode("HTML");
		} else {
			return new SendMessage()
	                .setChatId(chatId)
	                .setText("Event is not found")
	                .setParseMode("HTML");
		}
	}
	
	public SendMessage askQuestion(Long chatId, Integer messageId, String message) {
		List<EventMember> eventMembers = eventMemberRepository.findByChatId(chatId);
		Event event = null;
		if (eventMembers != null && !eventMembers.isEmpty()) {
			for (EventMember member : eventMembers) {
				Optional<Event> optEvent = eventRepository.findById(member.getEventId());
				if (optEvent.isPresent() && optEvent.get().getStatus().equals(EventStatusEnum.ONGOING.getCode())) {
					event = optEvent.get();
					break;
				}
			}
		}
		
		if (event != null) {
			EventQuestion eventQuestion = new EventQuestion();
			eventQuestion.setChatId(chatId);
			eventQuestion.setMessageId(messageId);
			eventQuestion.setMessage(message);
			eventQuestion.setCreatedTime(new Date());
			eventQuestion.setEventId(event.getId());
			eventQuestion.setAnswered(false);
			
			eventQuestionRepository.save(eventQuestion);
			return new SendMessage()
	                .setChatId(chatId)
	                .setText("Your question is successfully posted.")
	                .setParseMode("HTML");
			
		} else {
			return new SendMessage()
	                .setChatId(chatId)
	                .setText("Join event first for asking question. Type /join_event.")
	                .setParseMode("HTML");
		}
	}
	
	public SendMessage listMessage(Long chatId) {
		List<EventMember> eventMembers = eventMemberRepository.findByChatId(chatId);
		Event event = null;
		if (eventMembers != null && !eventMembers.isEmpty()) {
			for (EventMember member : eventMembers) {
				Optional<Event> optEvent = eventRepository.findById(member.getEventId());
				if (optEvent.isPresent() && optEvent.get().getStatus().equals(EventStatusEnum.ONGOING.getCode())) {
					event = optEvent.get();
					break;
				}
			}
		}
		
		List<InlineKeyboardButton> keyboardButtons = new ArrayList<>();
		if (event != null) {
			List<EventQuestion> eventQuestions = eventQuestionRepository.findByEventId(event.getId());
			for (EventQuestion question : eventQuestions) {
				List<QuestionVote> votes= questionVoteRepository.findByQuestionId(question.getId());
				Integer vote = 0;
				if (votes != null && !votes.isEmpty()) {
					vote = votes.size();
				}
				InlineKeyboardButton keyboardButton = new InlineKeyboardButton().setText(question.getMessage() + " - (" + vote + (vote > 1 ? " votes)" : " vote)")).setCallbackData("MSG-" + question.getId().toString());
				keyboardButtons.add(keyboardButton);
			}
		}
		List<List<InlineKeyboardButton>> keyboardButtonsList = new ArrayList<>();
		if (!keyboardButtons.isEmpty()) {
			keyboardButtonsList.add(keyboardButtons);
		}
		
		if (!keyboardButtonsList.isEmpty()) {
			return new SendMessage()
	                .setChatId(chatId)
	                .setText("You can vote the questions. List of questions:")
	                .setReplyMarkup(new InlineKeyboardMarkup().setKeyboard(keyboardButtonsList))
	                .setParseMode("HTML");
		} else {
			return new SendMessage()
	                .setChatId(chatId)
	                .setText("No question so far. Start asking questions /ask_question.")
	                .setParseMode("HTML");
		}
	}
	
	public Object answerQuestion(Long chatId) {
		List<Event> events = eventRepository.findByChatIdAndStatus(chatId, EventStatusEnum.ONGOING.getCode());
		if (events != null && !events.isEmpty()) {
			Event event = events.get(0);
			List<EventQuestion> eventQuestions = eventQuestionRepository.findByEventId(event.getId());
			EventQuestion nextQuestion = null;
			EventQuestion firstQuestion = null;
			Integer vote = 0;
			if(eventQuestions != null && !eventQuestions.isEmpty()) {
				for (EventQuestion question : eventQuestions) {
					if (!question.getAnswered()) {
						if (firstQuestion == null) {
							firstQuestion = question;
						}
						List<QuestionVote> votes= questionVoteRepository.findByQuestionId(question.getId());
						if (votes != null && !votes.isEmpty() && votes.size() >= vote) {
							nextQuestion = question;
							vote = votes.size();
						}
					}
				}
				
				if (nextQuestion == null) {
					nextQuestion = firstQuestion;
				}
			}
			
			if (nextQuestion != null) {
				nextQuestion.setAnswered(true);
				eventQuestionRepository.save(nextQuestion);
				
				return new ForwardMessage()
		                .setChatId(chatId)
		                .setFromChatId(nextQuestion.getChatId())
		                .setMessageId(nextQuestion.getMessageId());
			} else {
				return new SendMessage()
		                .setChatId(chatId)
		                .setText("No question is available.")
		                .setParseMode("HTML");
			}
			
		} else {
			return new SendMessage()
	                .setChatId(chatId)
	                .setText("Command is not available.")
	                .setParseMode("HTML");
		}
	}
	
	public SendMessage closeEvent(Long chatId, String text) {
		Optional<Event> optEvent = eventRepository.findByEventCodeAndStatus(text, EventStatusEnum.ONGOING.getCode());
		if (optEvent.isPresent()) {
			Event event = optEvent.get();
			event.setStatus(EventStatusEnum.CLOSED.getCode());
			this.save(event);
			
			return new SendMessage()
	                .setChatId(chatId)
	                .setText("Event " + text + " has been closed.")
	                .setParseMode("HTML");
		} else {
			return new SendMessage()
	                .setChatId(chatId)
	                .setText("Event " + text + " is not found.")
	                .setParseMode("HTML");
		}
	}
	
	private String getEventCode() {
		String generatedString = new RandomString(4).nextString();
		
		if (eventRepository.findByEventCode(generatedString).isPresent()) {
			return this.getEventCode();
		}
		return "#" + generatedString.toUpperCase();
	}

	public void voteMessage(Long chatId, Long questionId) {
		Optional<QuestionVote> questionVote = questionVoteRepository.findByQuestionIdAndChatId(questionId, chatId);
		if (questionVote.isPresent()) {
			questionVoteRepository.delete(questionVote.get());
		} else {
			QuestionVote newVote = new QuestionVote();
			newVote.setChatId(chatId);
			newVote.setQuestionId(questionId);
			questionVoteRepository.save(newVote);
		}
	}
}
