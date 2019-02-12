package com.bagongkia.telegram.bot.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RequestMapping("/slidobot")
@RestController
public class SlidoController {
	
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public void getUpdates(@RequestBody Update update) {

	}
}