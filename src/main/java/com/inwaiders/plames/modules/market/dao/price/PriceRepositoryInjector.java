package com.inwaiders.plames.modules.market.dao.price;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.market.domain.price.PriceImpl;

@Service
public class PriceRepositoryInjector {

	@Autowired
	private PriceRepository repository;
	
	@PostConstruct
	private void inject() {
		
		PriceImpl.setRepository(repository);
	}
}
