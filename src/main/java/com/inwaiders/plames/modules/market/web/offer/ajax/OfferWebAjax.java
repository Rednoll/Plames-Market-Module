package com.inwaiders.plames.modules.market.web.offer.ajax;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.inwaiders.plames.api.locale.PlamesLocale;
import com.inwaiders.plames.modules.market.domain.item.ItemImpl;
import com.inwaiders.plames.modules.market.domain.offer.OfferImpl;

@RestController
@RequestMapping("web/controller/ajax/market/offer")
public class OfferWebAjax {

	@Autowired
	private ObjectMapper mapper = null;
	
	@GetMapping("")
	public ArrayNode mainPage(@RequestParam(required = false) String name, @RequestParam(required = false) Long id, @RequestParam(defaultValue = "0", required = false) int page, @RequestParam(defaultValue = "12", required = false) int pageSize) {
		
		if(page < 0) page = 0;
		if(pageSize < 1) pageSize = 12;
		
		List<OfferImpl> allOffers = null;
		
		if(id == null && (name == null || name.isEmpty())) {
			
			allOffers = OfferImpl.getOrderedByName();
		}
		else {
			
			allOffers = OfferImpl.search(name, id);
		}
		
		ArrayNode jsonOffers = mapper.createArrayNode();
		
			for(int i = page*pageSize; i < page*pageSize + pageSize; i++) {
				
				if(i >= allOffers.size()) break;
				
				jsonOffers.add(allOffers.get(i).toJson(mapper));
			}
			
		return jsonOffers;
	}
	
	@GetMapping(value = "/{id}/description", produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> description(@PathVariable(name="id") long offerId) {

		OfferImpl offer = OfferImpl.getById(offerId);
		
		if(offer != null) {
			
			try {
				
				return new ResponseEntity<String>(offer.getDescription(PlamesLocale.getSystemLocale()), HttpStatus.OK);
			}
			catch(Exception e) {
				
				return new ResponseEntity<String>(PlamesLocale.getSystemLocale().getMessage("data.loading_error"), HttpStatus.OK);
			}
		}
		
		return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping("/{id}/active")
	public ResponseEntity activeToggle(@RequestBody JsonNode json, @PathVariable(name="id") long offerId) {
		
		if(!json.has("active") || !json.get("active").isBoolean()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
	
		boolean active = json.get("active").asBoolean();
	
		OfferImpl offer = OfferImpl.getById(offerId);
		
		if(offer != null) {
			
			offer.setActive(active);
			offer.save();
		
			return new ResponseEntity(HttpStatus.OK);
		}
		
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
}