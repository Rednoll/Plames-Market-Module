package com.inwaiders.plames.modules.market.dao.cart;

import java.util.List;

import javax.persistence.QueryHint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inwaiders.plames.modules.market.domain.cart.CartImpl;

import enterprises.inwaiders.plames.api.user.User;

@Repository
public interface CartRepository extends JpaRepository<CartImpl, Long>{

	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Override
	@Query("SELECT cart FROM Cart cart WHERE cart.id = :id AND cart.deleted != true")
	public CartImpl getOne(@Param(value = "id") Long id);
	
	@QueryHints({
		@QueryHint(name = "org.hibernate.cacheable", value = "true")
	})
	@Query("SELECT cart FROM Cart cart WHERE cart.targetApplicationName = :tan AND cart.owner = :owner AND cart.deleted != true")
	public CartImpl getByOwnerAndTan(@Param(value = "owner") User owner, @Param(value = "tan") String targetApplicationName);
	
	@Override
	@Query("SELECT cart FROM Cart cart WHERE cart.deleted != true")
	public List<CartImpl> findAll();
	
	@Override
	@Query("SELECT COUNT(*) FROM Cart cart WHERE cart.deleted != true")
	public long count();
}
