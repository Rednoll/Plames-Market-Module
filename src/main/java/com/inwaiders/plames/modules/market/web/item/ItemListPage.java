package com.inwaiders.plames.modules.market.web.item;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/market/items")
public class ItemListPage {

	@GetMapping("")
	public String mainPage(Model model) {

		return "market_items";
	}
}