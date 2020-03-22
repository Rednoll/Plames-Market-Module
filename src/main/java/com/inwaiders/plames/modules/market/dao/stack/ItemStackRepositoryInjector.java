package com.inwaiders.plames.modules.market.dao.stack;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.market.domain.stack.ItemStackImpl;

@Service
public class ItemStackRepositoryInjector {

	@Autowired
	private ItemStackRepository repository;
	
	@PostConstruct
	private void inject() {
		
		ItemStackImpl.setRepository(repository);
	}
}
