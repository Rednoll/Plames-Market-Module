package com.inwaiders.plames.modules.market.dao.cart;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.market.domain.cart.CartImpl;

@Service
public class CartRepositoryInjector {

	@Autowired
	private CartRepository repository;

	@PostConstruct
	private void inject() {
		
		CartImpl.setRepository(repository);
	}
}
