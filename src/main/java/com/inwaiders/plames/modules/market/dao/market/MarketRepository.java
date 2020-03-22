package com.inwaiders.plames.modules.market.dao.market;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inwaiders.plames.modules.market.domain.market.MarketImpl;

@Repository
public interface MarketRepository extends JpaRepository<MarketImpl, Long>{

}
