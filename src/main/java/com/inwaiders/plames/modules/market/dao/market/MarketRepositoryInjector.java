package com.inwaiders.plames.modules.market.dao.market;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.market.domain.market.MarketImpl;

@Service
public class MarketRepositoryInjector {

	@Autowired
	private MarketRepository repository;
	
	@PostConstruct
	private void inject() {
		
		MarketImpl.setRepository(repository);
	}
}
