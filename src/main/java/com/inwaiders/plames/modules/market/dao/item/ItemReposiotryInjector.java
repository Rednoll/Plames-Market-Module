package com.inwaiders.plames.modules.market.dao.item;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.market.domain.item.ItemImpl;

@Service
public class ItemReposiotryInjector {

	@Autowired
	private ItemRepository repository;
	
	@PostConstruct
	private void inject() {
		
		ItemImpl.setRepository(repository);
	}
}
