package com.inwaiders.plames.modules.market.web.cart.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.inwaiders.plames.modules.market.domain.cart.CartImpl;

@RestController
@RequestMapping("/api/market/rest")
public class CartRestController {

	@Autowired
	private ObjectMapper mapper;
	
	@GetMapping(value = "/carts/{id}")
	public ObjectNode get(@PathVariable long id) {
		
		CartImpl cart = CartImpl.getById(id);
		
		return cart.toJson(mapper);
	}
}