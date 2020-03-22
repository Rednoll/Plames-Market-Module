package com.inwaiders.plames.modules.market.dao.item;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inwaiders.plames.modules.market.domain.item.ItemImpl;

@Repository
public interface ItemRepository extends JpaRepository<ItemImpl, Long>{

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Override
	@Query("SELECT item FROM Item item WHERE item.id = :id AND item.deleted != true")
	public ItemImpl getOne(@Param(value = "id") Long id);
	
	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Query("SELECT item FROM Item item WHERE item.name = :name AND item.deleted != true")
	public ItemImpl getByName(@Param(value = "name") String name);
	
	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Query("SELECT item FROM Item item WHERE item.metadata = :metadata AND item.deleted != true")
	public ItemImpl getByMetadata(@Param(value = "metadata") String metadata);

	@Query("SELECT item FROM Item item WHERE item.deleted != true ORDER BY item.name")
	public List<ItemImpl> getOrderedByName();
	
	@Override
	@Query("SELECT item FROM Item item WHERE item.deleted != true")
	public List<ItemImpl> findAll();
	
	@Override
	@Query("SELECT COUNT(*) FROM Item item WHERE item.deleted != true")
	public long count();
}
