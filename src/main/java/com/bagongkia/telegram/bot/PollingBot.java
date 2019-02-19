package com.bagongkia.telegram.bot;

import java.text.ParseException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.bagongkia.telegram.bot.enumeration.ChatStatusEnum;
import com.bagongkia.telegram.bot.exception.EventException;
import com.bagongkia.telegram.bot.model.ChatStatus;
import com.bagongkia.telegram.bot.service.ChatStatusService;
import com.bagongkia.telegram.bot.service.EventService;

@Component
public class PollingBot extends TelegramLongPollingBot {
	
	@Value("${com.bagongkia.telegram.bot.username}")
	private String botUsername;
	
	@Value("${com.bagongkia.telegram.bot.token}")
	private String botToken;
	
	@Autowired
	private ChatStatusService chatStatusService;
	
	@Autowired
	private EventService eventService;

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasText()) {
			
			Object message = null;
			ChatStatusEnum nextChatStatus = ChatStatusEnum.WELCOME;
			String text = null;
			String parseMode = "Markdown";
			
			try {
				if ("/create_event".equals(update.getMessage().getText())) {
					eventService.createEvent(update.getMessage().getChatId());
					nextChatStatus = ChatStatusEnum.INPUT_EVENT_NAME;
				} else if ("/start_event".equals(update.getMessage().getText())) {
					message = eventService.startEvent(update.getMessage().getChatId());
					nextChatStatus = ChatStatusEnum.START_EVENT_CODE;
				} else if ("/join_event".equals(update.getMessage().getText())) {
					nextChatStatus = ChatStatusEnum.JOIN_EVENT_CODE;
				} else if ("/close_event".equals(update.getMessage().getText())) {
					nextChatStatus = ChatStatusEnum.CLOSE_EVENT_CODE;
				} else if ("/ask_question".equals(update.getMessage().getText())) {
					nextChatStatus = ChatStatusEnum.ASK_QUESTION;
				} else if ("/list_question".equals(update.getMessage().getText())) {
					message = eventService.listMessage(update.getMessage().getChatId());
					nextChatStatus = ChatStatusEnum.LIST_QUESTION;
				} else if ("/answer_question".equals(update.getMessage().getText())) {
					message = eventService.answerQuestion(update.getMessage().getChatId());
					nextChatStatus = ChatStatusEnum.ANSWER_QUESTION;
				} else {
					Optional<ChatStatus> optChatStatus = chatStatusService.getStatus(update.getMessage().getChatId());  
					if (optChatStatus.isPresent()) {
						if (ChatStatusEnum.INPUT_EVENT_NAME.getCode().equals(optChatStatus.get().getStatus())) {
							eventService.initEvent(update.getMessage().getChatId(), optChatStatus.get().getStatus(), update.getMessage().getText());
							nextChatStatus = ChatStatusEnum.INPUT_EVENT_LOCATION;
						} else if (ChatStatusEnum.INPUT_EVENT_LOCATION.getCode().equals(optChatStatus.get().getStatus())) {
							eventService.initEvent(update.getMessage().getChatId(), optChatStatus.get().getStatus(), update.getMessage().getText());
							nextChatStatus = ChatStatusEnum.INPUT_EVENT_DATE;
						} else if (ChatStatusEnum.INPUT_EVENT_DATE.getCode().equals(optChatStatus.get().getStatus())) {
							text = eventService.inputEventDate(update.getMessage().getChatId(), update.getMessage().getText());
							parseMode = "HTML";
							nextChatStatus = ChatStatusEnum.EVENT_CREATED;
						} else if (ChatStatusEnum.START_EVENT_CODE.getCode().equals(optChatStatus.get().getStatus())) {
							nextChatStatus = ChatStatusEnum.EVENT_STARTED;
						} else if (ChatStatusEnum.JOIN_EVENT_CODE.getCode().equals(optChatStatus.get().getStatus())) {
							eventService.joinEvent(update.getMessage().getChatId(), update.getMessage().getText(), update.getMessage().getChat().getFirstName());
							nextChatStatus = ChatStatusEnum.EVENT_JOINED;
						} else if (ChatStatusEnum.CLOSE_EVENT_CODE.getCode().equals(optChatStatus.get().getStatus())) {
							message = eventService.closeEvent(update.getMessage().getChatId(), update.getMessage().getText());
							nextChatStatus = ChatStatusEnum.EVENT_CLOSED;
						} else if (ChatStatusEnum.ASK_QUESTION.getCode().equals(optChatStatus.get().getStatus())) {
							message = eventService.askQuestion(update.getMessage().getChatId(), update.getMessage().getMessageId(), update.getMessage().getText());
							nextChatStatus = ChatStatusEnum.EVENT_CLOSED;
						}
					}
				}
			} catch(ParseException e) {
				text = "Invalid date format. Retype the event date (format: dd-MMM-yyyy, ex: 11-APR-2019).";
			} catch(EventException e) {
				text = e.getMessage();
			}
			
			chatStatusService.setChatStatus(update.getMessage().getChatId(), nextChatStatus);
			if (message == null) {
				message = new SendMessage()
		                .setChatId(update.getMessage().getChatId())
		                .setText(text == null ? nextChatStatus.getMessage() : text)
		                .setParseMode(parseMode);
			}
			
			
	        executeMessage(message);
	    } else if (update.hasCallbackQuery() && update.getCallbackQuery().getData() != null && !update.getCallbackQuery().getData().isEmpty()) {
	    	Object message = null;
	    	String text = null;
	    	String parseMode = "Markdown";
	    	String data = update.getCallbackQuery().getData();
	    	Integer fromMessageId = update.getCallbackQuery().getMessage().getMessageId();
	    	ChatStatusEnum nextChatStatus = ChatStatusEnum.WELCOME;
	    	
	    	Long chatId = update.getCallbackQuery().getMessage().getChat().getId();
	    	Optional<ChatStatus> optChatStatus = chatStatusService.getStatus(chatId);
	    	
			if (optChatStatus.isPresent()) {
				if (ChatStatusEnum.START_EVENT_CODE.getCode().equals(optChatStatus.get().getStatus())) {
					message = eventService.startEvent(chatId, data);
				}
			}
			
			if (data.substring(0, 4).equals("MSG-")) {
				eventService.voteMessage(chatId, Long.parseLong(data.substring(4)));
				SendMessage sendMessage = eventService.listMessage(chatId);
				message = new EditMessageText()
						.setChatId(chatId)
						.setMessageId(fromMessageId)
						.setText(sendMessage.getText())
						.setReplyMarkup((InlineKeyboardMarkup) sendMessage.getReplyMarkup())
						.setParseMode("HTML");
			}
			
			chatStatusService.setChatStatus(chatId, nextChatStatus);
			if (message == null) {
				message = new SendMessage()
		                .setChatId(chatId)
		                .setText(text == null ? nextChatStatus.getMessage() : text)
		                .setParseMode(parseMode);
			}
	    	
	        executeMessage(message);
	    }
	}

	@Override
	public String getBotUsername() {
		return botUsername;
	}

	@Override
	public String getBotToken() {
		return botToken;
	}
	
	private void executeMessage(Object message) {
		try {
			if (message instanceof SendMessage) {
				execute((SendMessage) message);
			} else if (message instanceof ForwardMessage) {
				execute((ForwardMessage) message);
			} else if (message instanceof EditMessageText) {
				execute((EditMessageText) message);
			}
		} catch(TelegramApiException e) {
			e.printStackTrace();
		}
	}
}
