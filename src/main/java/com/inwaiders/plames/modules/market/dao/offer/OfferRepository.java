package com.inwaiders.plames.modules.market.dao.offer;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inwaiders.plames.modules.market.domain.offer.OfferImpl;

@Repository
public interface OfferRepository extends JpaRepository<OfferImpl, Long>{

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Override
	@Query("SELECT offer FROM Offer offer WHERE offer.id = :id AND offer.deleted != true")
	public OfferImpl getOne(@Param(value = "id") Long id);
	
	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Query("SELECT offer FROM Offer offer WHERE offer.name = :name AND offer.deleted != true")
	public OfferImpl getByName(@Param(value = "name") String name);
	
	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Query("SELECT offer FROM Offer offer WHERE offer.deleted != true ORDER BY offer.name")
	public List<OfferImpl> getOrderedByName();
	
	@Override
	@Query("SELECT offer FROM Offer offer WHERE offer.deleted != true")
	public List<OfferImpl> findAll();
	
	@Override
	@Query("SELECT COUNT(*) FROM Offer offer WHERE offer.deleted != true")
	public long count();
}
