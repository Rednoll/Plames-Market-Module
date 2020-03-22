package com.inwaiders.plames.modules.market.web.offer.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.inwaiders.plames.modules.market.domain.offer.Offer;
import com.inwaiders.plames.modules.market.domain.offer.OfferImpl;

@RestController
@RequestMapping("/api/market/rest")
public class OfferRestController {

	@Autowired
	private ObjectMapper mapper;
	
	@GetMapping(value = "/offers/{id}")
	public ObjectNode get(@PathVariable long id) {
		
		OfferImpl offer = OfferImpl.getById(id);
			
		return offer.toJson(mapper);
	}
	
	@PutMapping(value = "/offers/{id}")
	public ResponseEntity save(@PathVariable long id, @RequestBody ObjectNode node) {
		
		OfferImpl offer = OfferImpl.getById(id);
	
		if(offer == null) return new ResponseEntity(HttpStatus.NOT_FOUND);
		
			offer.loadFromJson(node);
		
		offer.save();
		
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@GetMapping(value = "/offers")
	public List<ObjectNode> getAll() {

		List<OfferImpl> offers = OfferImpl.getAll();
		List<ObjectNode> jsonOffers = new ArrayList<>();
	
		for(OfferImpl offer : offers) {
			
			jsonOffers.add(offer.toJson(mapper));
		}
		
		return jsonOffers;
	}
	
	@PostMapping(value = "/offers")
	public ObjectNode create(@RequestBody ObjectNode node) {

		OfferImpl offer = OfferImpl.create();

			offer.loadFromJson(node);
		
		offer.save();
		
		return offer.toJson(mapper);
	}
	
	@DeleteMapping(value = "/offers/{id}")
	public ResponseEntity delete(@PathVariable long id) {
	
		OfferImpl offer = OfferImpl.getById(id);
		
		if(offer != null) {		
			
			offer.delete();
					
			return new ResponseEntity<>(HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
}
