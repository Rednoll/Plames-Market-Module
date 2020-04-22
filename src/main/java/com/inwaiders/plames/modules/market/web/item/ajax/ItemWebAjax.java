package com.inwaiders.plames.modules.market.web.item.ajax;

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
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.inwaiders.plames.modules.market.domain.item.ItemImpl;

import enterprises.inwaiders.plames.api.locale.PlamesLocale;

@RestController
@RequestMapping("web/controller/ajax/market/item")
public class ItemWebAjax {

	@Autowired
	private ObjectMapper mapper = null;
	
	@GetMapping("")
	public ArrayNode mainPage(@RequestParam(required = false) String name, @RequestParam(required = false) Long id, @RequestParam(name="tan", required = false) String targetApplicationName, @RequestParam(defaultValue = "0", required = false) int page, @RequestParam(defaultValue = "12", required = false) int pageSize) {
		
		if(page < 0) page = 0;
		if(pageSize < 1) pageSize = 12;
		
		List<ItemImpl> allItems = null;
		
		if(id == null && (name == null || name.isEmpty()) && (targetApplicationName == null || targetApplicationName.isEmpty())) {
			
			allItems = ItemImpl.getOrderedByName();
		}
		else {
			
			allItems = ItemImpl.search(name, id, targetApplicationName);
		}
		
		ArrayNode jsonItems = mapper.createArrayNode();
		
			for(int i = page*pageSize; i < page*pageSize + pageSize; i++) {
				
				if(i >= allItems.size()) break;
				
				jsonItems.add(allItems.get(i).toJson(mapper));
			}
			
		return jsonItems;
	}
	
	@GetMapping("/aliases")
	public ArrayNode getAllAliases() {
		
		List<ItemImpl> items = ItemImpl.getOrderedByName();
		ArrayNode array = mapper.createArrayNode();
		
		for(ItemImpl item : items) {
			
			for(String alias : item.getAliases()) {
			
				ObjectNode node = mapper.createObjectNode();
					node.put("alias", alias);
					node.put("id", item.getId());
					node.put("tan", item.getTargetApplicationName());
					
				array.add(node);
			}
		}
		
		return array;
	}
	
	@GetMapping(value = "/{id}/description", produces = "text/plain;charset=UTF-8")
	public ResponseEntity<String> description(@PathVariable(name="id") long itemId) {

		ItemImpl item = ItemImpl.getById(itemId);
		
		if(item != null) {
			
			try {
				
				return new ResponseEntity<String>(item.getDescription(PlamesLocale.getSystemLocale()), HttpStatus.OK);
			}
			catch(Exception e) {
				
				return new ResponseEntity<String>(PlamesLocale.getSystemLocale().getMessage("data.loading_error"), HttpStatus.OK);
			}
		}
		
		return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
	}
	
	@PostMapping("/{id}/active")
	public ResponseEntity activeToggle(@RequestBody JsonNode json, @PathVariable(name="id") long itemId) {
		
		if(!json.has("active") || !json.get("active").isBoolean()) return new ResponseEntity(HttpStatus.BAD_REQUEST);
	
		boolean active = json.get("active").asBoolean();
	
		ItemImpl item = ItemImpl.getById(itemId);
		
		if(item != null) {
			
			item.setActive(active);
			item.save();
		
			return new ResponseEntity(HttpStatus.OK);
		}
		
		return new ResponseEntity(HttpStatus.NOT_FOUND);
	}
}
