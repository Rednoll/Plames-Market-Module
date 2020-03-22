package com.inwaiders.plames.modules.market.dao.offer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inwaiders.plames.modules.market.domain.offer.OfferImpl;

@Service
public class OfferRepositoryInjector {

	@Autowired
	private OfferRepository repository;
	
	@PostConstruct
	private void inject() {
		
		OfferImpl.setRepository(repository);
	}
}
