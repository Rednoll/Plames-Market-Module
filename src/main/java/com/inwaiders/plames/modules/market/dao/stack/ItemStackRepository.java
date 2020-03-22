package com.inwaiders.plames.modules.market.dao.stack;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inwaiders.plames.modules.market.domain.stack.ItemStackImpl;

@Repository
public interface ItemStackRepository extends JpaRepository<ItemStackImpl, Long>{

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Override
	@Query("SELECT stack FROM ItemStack stack WHERE stack.id = :id AND stack.deleted != true")
	public ItemStackImpl getOne(@Param(value = "id") Long id);
	
	@Override
	@Query("SELECT stack FROM ItemStack stack WHERE stack.deleted != true")
	public List<ItemStackImpl> findAll();
	
	@Override
	@Query("SELECT COUNT(*) FROM ItemStack stack WHERE stack.deleted != true")
	public long count();
}
