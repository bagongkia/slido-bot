package com.bagongkia.telegram.bot.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequestMapping("/slidobot")
@RestController
public class SlidoController {
	
	@Value("${com.bagongkia.telegram.bot.token}")
	private String botToken;
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public void getUpdates(@RequestBody Update update) {
		System.out.println(update);
		SendMessage message = new SendMessage()
				.setChatId(update.getMessage().getChatId())
				.setText(update.getMessage().getText() + " too... (via webhook)");
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForEntity("https://api.telegram.org/bot" + botToken + "/sendMessage", message, Message.class);
	}
}