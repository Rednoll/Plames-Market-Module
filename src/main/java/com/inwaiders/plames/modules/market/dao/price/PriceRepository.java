package com.inwaiders.plames.modules.market.dao.price;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inwaiders.plames.modules.market.domain.price.PriceImpl;

@Repository
public interface PriceRepository extends JpaRepository<PriceImpl, Long>{

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Override
	@Query("SELECT price FROM Price price WHERE price.id = :id AND price.deleted != true")
	public PriceImpl getOne(@Param(value = "id") Long id);
	
	@Override
	@Query("SELECT price FROM Price price WHERE price.deleted != true")
	public List<PriceImpl> findAll();
	
	@Override
	@Query("SELECT COUNT(*) FROM Price price WHERE price.deleted != true")
	public long count();
}
