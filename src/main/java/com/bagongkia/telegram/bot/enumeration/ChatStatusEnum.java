package com.bagongkia.telegram.bot.enumeration;

import lombok.Getter;

@Getter
public enum ChatStatusEnum {
	WELCOME("000", "*Hi, this is Slido Bot*\nThis bot will help you create the questions platform for your event."),
	INPUT_EVENT_NAME("001", "Let's create an event. First, type the event name."),
	INPUT_EVENT_LOCATION("002", "Type the event location."),
	INPUT_EVENT_DATE("003", "Type the event date (format: dd-MMM-yyyy, ex: 11-Apr-2019)."),
	EVENT_CREATED("004", "Your event is created successfully."),
	START_EVENT_CODE("005", "You'll start your event. Type the event code."),
	EVENT_STARTED("006", "Event has started."),
	JOIN_EVENT_CODE("007", "Which event do you want to join? Type the event code."),
	EVENT_JOINED("008", "You've joined. Start asking questions by send command /ask_question.\n\nYou can sending other commands:\n/list_question - get a list of existing questions"),
	ASK_QUESTION("009", "Type your question."),
	QUESTION_ASKED("010", ""),
	LIST_QUESTION("011", ""),
	ANSWER_QUESTION("012", "");
	
	private final String code;
	private final String message;
	
	private ChatStatusEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}
}