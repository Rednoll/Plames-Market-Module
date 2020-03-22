package com.inwaiders.plames.modules.market.domain.stack;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.inwaiders.plames.modules.market.dao.stack.ItemStackRepository;
import com.inwaiders.plames.modules.market.domain.item.Item;
import com.inwaiders.plames.modules.market.domain.item.ItemImpl;
import com.inwaiders.plames.modules.market.domain.price.Price;
import com.inwaiders.plames.modules.market.domain.price.PriceImpl;

@Entity(name = "ItemStack")
@Table(name = "market_item_stacks")
public class ItemStackImpl implements ItemStack {

	private static transient ItemStackRepository repository;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id = null;
	
	@ManyToOne(fetch = FetchType.EAGER, targetEntity = ItemImpl.class, optional = false)
	@JoinColumn(name = "item_id")
	private Item item = null;
	
	@Column(name = "quantity")
	private int quantity = 1;
	
	@OneToOne(targetEntity = PriceImpl.class, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "custom_price_id")
	private Price customPrice = null;
	
	@Column(name = "deleted")
	private volatile boolean deleted = false;
	
	public ItemStackImpl() {
		
	}
	
	public ItemStackImpl(Item item) {
		
		this.item = item;
	}
	
	@Override
	public boolean businessEquals(ItemStack is) {

		if(this.getItem().getId() != is.getItem().getId()) return false;
		
		return true;
	}
	
	@Override
	public void setItem(Item item) {
		
		this.item = item;
	}

	@Override
	public Item getItem() {
		
		return this.item;
	}

	@Override
	public void setQuantity(int quantity) {
		
		this.quantity = quantity;
	}
	
	@Override
	public void decrQuantity(int quantity) {
		
		this.quantity -= quantity;
		
		if(this.quantity < 0) {
			
			this.quantity = 0;
		}
	}

	@Override
	public int getQuantity() {
		
		return this.quantity;
	}

	@Override
	public String getMetadata() {
		
		return null;
	}

	public void setCustomPrice(Price price) {
		
		this.customPrice = price;
	}
	
	public Price getCustomPrice() {
		
		return this.customPrice;
	}
	
	@Override
	public Price getPrice() {
		
		if(customPrice != null) {
			
			return customPrice;
		}
		else {

			Price itemPrice = item.getPrice();
			
			if(itemPrice == null) {
				
				return null;
			}
			
			return item.getPrice().multiply(quantity);
		}
	}
	
	public Long getId() {
		
		return this.id;
	}
	
	public void save() {
		
		if(!deleted) {
		
			repository.save(this);
		}
	}
	
	public void delete() {
		
		this.deleted = true;
	}
	
	public ObjectNode toJson(ObjectMapper mapper) {
		
		ObjectNode node = mapper.createObjectNode();
	
			node.put("id", id);
			node.put("quantity", quantity);
			
			if(customPrice != null) {
				
				node.put("custom_price", ((PriceImpl) customPrice).toJson(mapper));
			}
			
			if(item != null) {
				
				node.put("item", ((ItemImpl )item).toJson(mapper));
			}
			
		return node;
	}
	
	@Override
	public ItemStackImpl clone() {
		
		ItemStackImpl clone = new ItemStackImpl(item);
			clone.setQuantity(this.getQuantity());
			clone.setCustomPrice(this.getCustomPrice());
	
		return clone;
	}
	
	public static ItemStackImpl parseFromJson(ObjectNode node){
		
		ItemImpl item = null;
		
		if(node.has("item_id") && node.get("item_id").canConvertToLong()) {
			
			long itemId = node.get("item_id").asLong();
			
			item = ItemImpl.getById(itemId);
		}
		
		if(item == null && node.has("item_name") && node.get("item_name").isTextual()) {
			
			String itemName = node.get("item_name").asText();
			
			item = ItemImpl.getByName(itemName);
		}
		
		ItemStackImpl itemStack = null;
		
			if(node.has("id") && node.get("id").isLong()) {
		
				itemStack = ItemStackImpl.getById(node.get("id").asLong());
					itemStack.setItem(item);
			}
			else {
				
				itemStack = ItemStackImpl.create(item);
			}
			
			if(node.has("quantity") && node.get("quantity").canConvertToInt()) {
				
				itemStack.setQuantity(node.get("quantity").asInt());
			}
			
			if(node.has("custom_price") && node.get("custom_price").isObject()) {
				
				itemStack.setCustomPrice(PriceImpl.fromJson((ObjectNode) node.get("custom_price")));
			}
		
		return itemStack;
	}
	
	public static ItemStackImpl create(Item item) {
		
		ItemStackImpl stack = new ItemStackImpl(item);
		
			stack = repository.save(stack);
		
		return stack;
	}
	
	public static ItemStackImpl getById(long id) {
		
		return repository.getOne(id);
	}
	
	public static void setRepository(ItemStackRepository rep) {
		
		repository = rep;
	}
}