package com.inwaiders.plames.modules.market.domain.market;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.inwaiders.plames.dao.EntityLink;
import com.inwaiders.plames.modules.market.dao.market.MarketRepository;
import com.inwaiders.plames.modules.market.domain.item.Item;
import com.inwaiders.plames.modules.market.domain.item.ItemImpl;
import com.inwaiders.plames.spring.SpringUtils;

@Entity
@Table(name = "market_markets")
public class MarketImpl implements Market {

	private static transient MarketRepository repository;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name = "target_application_name")
	private String targetApplicationName = null;
	
	@ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, targetEntity = ItemImpl.class)
	@JoinTable(name = "market_items_markets_mtm", joinColumns = @JoinColumn(name = "market_id"), inverseJoinColumns = @JoinColumn(name = "item_id"))
	@OrderBy
	private List<Item> items = new ArrayList<>();
	
	@Column(name = "deleted")
	private volatile boolean deleted = false;
	
	public MarketImpl() {
		
	}
	
	public void setTargetApplicationName(String appName) {
		
		this.targetApplicationName = appName;
	}
	
	@Override
	public String getTargetApplicationName() {
		
		return this.targetApplicationName;
	}

	@Override
	public List<Item> getItems() {

		return this.items;
	}
	
	@Override
	public Long getId() {
		
		return this.id;
	}

	@Override
	public void save() {
		
		if(!deleted) {
			
			repository.save(this);
		}
	}

	@Override
	public void delete() {
		
		this.deleted = true;
		repository.save(this);
	}
	
	public static MarketImpl create(String tan) {
		
		MarketImpl market = new MarketImpl();
			market.setTargetApplicationName(tan);;
		
			market = repository.save(market);
		
		return market;
	}
	
	public static MarketImpl getById(long id) {
		
		return repository.getOne(id);
	}
	
	public static void setRepository(MarketRepository rep) {
		
		repository = rep;
	}
	
	public static class HighLevelRepository extends MarketHlRepository<MarketImpl> {

		public EntityLink getLink(MarketImpl market) {
			
			return new EntityLink(SpringUtils.getEntityName(MarketImpl.class), market.getId());
		}

		@Override
		public void save(MarketImpl market) {
			
			market.save();
		}
		
		@Override
		public MarketImpl create(String tan) {
			
			return MarketImpl.create(tan);
		}

		@Override
		public MarketImpl getById(Long id) {
			
			return MarketImpl.getById(id);
		}
	}
}
