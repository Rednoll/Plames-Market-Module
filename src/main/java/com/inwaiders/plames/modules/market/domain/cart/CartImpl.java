package com.inwaiders.plames.modules.market.domain.cart;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.domain.user.impl.UserImpl;
import com.inwaiders.plames.modules.market.dao.cart.CartRepository;
import com.inwaiders.plames.modules.market.domain.stack.ItemStack;
import com.inwaiders.plames.modules.market.domain.stack.ItemStackImpl;

@Entity(name = "Cart")
@Table(name = "market_carts")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class CartImpl implements Cart {

	protected static transient CartRepository repository;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@OneToOne(targetEntity = UserImpl.class, fetch = FetchType.EAGER)
	@JoinColumn(name = "owner_id")
	protected User owner;
	
	@OneToMany(targetEntity = ItemStackImpl.class, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "cart_id")
	private Set<ItemStack> itemStacks = new HashSet<>();
	
	@Column(name = "target_application_name")
	private String targetApplicationName = null;
	
	@Column(name = "deleted")
	private volatile boolean deleted = false;
	
	public CartImpl() {
		
	}
	
	public CartImpl(User owner) {
		
		this.owner = owner;
	}
	
	@Override
	public void addItemStack(ItemStack is) {
	
		for(ItemStack suspect : itemStacks) {
			
			if(suspect.businessEquals(is)) {
				
				suspect.setQuantity(suspect.getQuantity()+is.getQuantity());
				save();
				return;
			}
		}		
		
		itemStacks.add(is);
		is.save();
		save();
	}

	public boolean isEmpty() {
		
		for(ItemStack is : itemStacks) {
			
			if(is.getQuantity() != 0) {
				
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public User getOwner() {
		
		return this.owner;
	}
	
	public void setTargetApplicationName(String tan) {
	
		this.targetApplicationName = tan;
	}
	
	public String getTargetApplicationName() {
		
		return this.targetApplicationName;
	}
	
	public Long getId() {
		
		return this.id;
	}
	
	@Override
	public void loadFromJson(String json) {
		
		ObjectMapper mapper = new ObjectMapper();
			
		try {
			
			ObjectNode jsonCart = (ObjectNode) mapper.readTree(json);
			
				if(jsonCart.has("owner_id") && jsonCart.get("owner_id").isLong()) {
					
					this.owner = User.getById(jsonCart.get("owner_id").asLong());
				}
				
				if(jsonCart.has("item_stacks") && jsonCart.get("item_stacks").isArray()) {
					
					ArrayNode jsonItemStacks = (ArrayNode) jsonCart.get("item_stacks");
					
					for(JsonNode jsonItemStack : jsonItemStacks) {
						
						this.itemStacks.add(ItemStackImpl.parseFromJson((ObjectNode) jsonItemStack));
					}
				}
		}
		catch(JsonMappingException e) {
			
			e.printStackTrace();
		}
		catch(JsonProcessingException e) {
			
			e.printStackTrace();
		}
	}
	
	public String toJson() {
		
		return toJson(new ObjectMapper()).toString();
	}
	
	public ObjectNode toJson(ObjectMapper mapper) {
		
		ObjectNode node = mapper.createObjectNode();
			node.put("id", id);
			node.put("owner_id", owner.getId());
			
			ArrayNode jsonItemStacks = node.putArray("item_stacks");
			
				for(ItemStack is : itemStacks) {
					
					ObjectNode jsonItemStack = mapper.createObjectNode();
						jsonItemStack.put("id", is.getId());
						jsonItemStack.put("quantity", is.getQuantity());
						jsonItemStack.put("metadata", is.getMetadata());
						
						try {
							
							jsonItemStack.put("item", mapper.readTree(is.getItem().getMetadata()));
						}
						catch(JsonMappingException e) {
							
							e.printStackTrace();
						}
						catch(JsonProcessingException e) {
							
							e.printStackTrace();
						}
				
					jsonItemStacks.add(jsonItemStack);
				}
				
		return node;
	}
	
	@Override
	public ItemStack getItemStackById(long id) {
		
		for(ItemStack is : itemStacks) {
			
			if(is.getId() == id) {
				
				return is;
			}
		}
		
		return null;
	}
	
	@Override
	public Collection<ItemStack> getItemStacks() {
	
		return this.itemStacks;
	}
	
	public void save() {
		
		if(!deleted) {
			
			repository.save(this);
		}
	}
	
	public void delete() {
		
		deleted = true;
	}
	
	public static CartImpl create(User owner, String tan) {
		
		CartImpl cart = new CartImpl(owner);
			cart.setTargetApplicationName(tan);
		
			cart = repository.save(cart);
			
		return cart;
	}
	
	public static CartImpl getById(Long id) {
		
		return repository.getOne(id);
	}
	
	public static CartImpl getByOwnerAndTan(User owner, String tan) {
		
		return repository.getByOwnerAndTan(owner, tan);
	}
	
	public static void setRepository(CartRepository rep) {
		
		repository = rep;
	}
	
	public static class HighLevelRepository extends CartHlRepository {

		private String targetApplicationName = null;
		
		public HighLevelRepository(String name) {
			
			this.targetApplicationName = name;
		}
		
		public CartImpl create(User user) {
			
			return CartImpl.create(user, targetApplicationName);
		}
		
		@Override
		public CartImpl getCart(User user) {
			
			CartImpl cart = CartImpl.getByOwnerAndTan(user, targetApplicationName);
		
			if(cart == null) {
				
				cart = create(user);
			}
			
			return cart;
		}

		@Override
		public String getTargetApplicationName() {
			
			return this.targetApplicationName;
		}
	}
}