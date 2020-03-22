package com.inwaiders.plames.modules.market.web.item.rest;

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
import com.inwaiders.plames.modules.market.domain.item.ItemImpl;

@RestController
@RequestMapping("/api/market/rest")
public class ItemRestController {

	@Autowired
	private ObjectMapper mapper;
	
	@GetMapping(value = "/items/{id}")
	public ObjectNode get(@PathVariable long id) {
		
		ItemImpl item = ItemImpl.getById(id);

		if(item != null) {
			
			return item.toJson(mapper);
		}
		
		return null;
	}
	
	@PutMapping(value = "/items/{id}")
	public ResponseEntity save(@PathVariable long id, @RequestBody ObjectNode node) {
		
		ItemImpl item = ItemImpl.getById(id);
	
			item.loadFromJson(node);
		
		item.save();
		
		return new ResponseEntity(HttpStatus.OK);
	}
	
	@PostMapping(value = "/items")
	public ObjectNode create(@RequestBody ObjectNode node) {

		ItemImpl item = ItemImpl.create();

			item.loadFromJson(node);
		
		item.save();
			
		return item.toJson(mapper);
	}
	
	@DeleteMapping(value = "/items/{id}")
	public ResponseEntity delete(@PathVariable long id) {
	
		ItemImpl item = ItemImpl.getById(id);
		
		if(item != null) {		
			
			item.delete();
					
			return new ResponseEntity<>(HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
}
