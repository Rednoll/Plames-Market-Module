package com.inwaiders.plames.modules.market.web.offer;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.inwaiders.plames.modules.market.domain.item.ItemImpl;
import com.inwaiders.plames.modules.market.domain.offer.OfferImpl;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;

@Controller
@RequestMapping("/market/offers")
public class OfferListPage {

	@GetMapping("")
	public String mainPage(Model model) {
		
		List<OfferImpl> offers = OfferImpl.getAll();
		
		model.addAttribute("offers", offers);
		
		List<ItemImpl> items = ItemImpl.getAll();
		
		model.addAttribute("items", items);

		List<Currency> currencies = Currency.getAll();
		
		model.addAttribute("currencies", currencies);
		
		return "market_offers";
	}
}