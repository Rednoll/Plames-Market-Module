package com.inwaiders.plames.modules.market.domain.offer;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.inwaiders.plames.modules.market.dao.offer.OfferRepository;
import com.inwaiders.plames.modules.market.domain.cart.Cart;
import com.inwaiders.plames.modules.market.domain.item.Item;
import com.inwaiders.plames.modules.market.domain.price.Price;
import com.inwaiders.plames.modules.market.domain.price.PriceImpl;
import com.inwaiders.plames.modules.market.domain.stack.ItemStack;
import com.inwaiders.plames.modules.market.domain.stack.ItemStackImpl;
import com.inwaiders.plames.modules.wallet.domain.account.CurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;
import com.inwaiders.plames.modules.wallet.domain.wallet.Wallet;

import enterprises.inwaiders.plames.api.locale.PlamesLocale;
import enterprises.inwaiders.plames.api.user.User;
import enterprises.inwaiders.plames.api.utils.DescribedFunctionResult;
import enterprises.inwaiders.plames.api.utils.DescribedFunctionResult.Status;

@Entity(name = "Offer")
@Table(name = "market_offers")
public class OfferImpl implements Offer {
	
	private static transient OfferRepository repository;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id = null;
	
	@OneToMany(targetEntity = ItemStackImpl.class, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "offer_id")
	private Set<ItemStack> itemStacks = new HashSet<>();
	
	@OneToOne(targetEntity = PriceImpl.class, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "custom_price_id")
	private Price customPrice = null;
	
	@Column(name = "discount")
	private double discount = 0;
	
	@Column(name = "name")
	private String name = null;
	
	@Column(name = "active")
	private boolean active = true;
	
	@Column(name = "deleted")
	private volatile boolean deleted = false;
	
	@Override
	public int hashCode() {
		return Objects.hash(active, customPrice, deleted, discount, itemStacks, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OfferImpl other = (OfferImpl) obj;
		return active == other.active && Objects.equals(customPrice, other.customPrice) && deleted == other.deleted
				&& Double.doubleToLongBits(discount) == Double.doubleToLongBits(other.discount)
				&& Objects.equals(itemStacks, other.itemStacks) && Objects.equals(name, other.name);
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public DescribedFunctionResult buy(User user, int quantity) {
	
		Price price = calcTotalPrice();
		
			price = price.multiply(quantity);
		
		Wallet wallet = Wallet.getByOwner(user);
		
		Set<CurrencyAccount> accounts = new HashSet<>();
		
		wallet.getPrivateAccounts().stream().map(account -> accounts.add(account));
		
		if(!price.checkPurchaseOpportunity(accounts)) return new DescribedFunctionResult(Status.ERROR, "Недостаточно средств!");
		
		for(ItemStack is : itemStacks) {
			
			Cart cart = Cart.getCart(user, is.getItem().getTargetApplicationName());
		
			ItemStack cartStack = is.clone();
				cartStack.setQuantity(is.getQuantity()*quantity);
			
			cart.addItemStack(cartStack);
			cart.save();
		}
		
		for(Entry<Currency, Long> entry : price.getCurrencies().entrySet()) {
			
			Currency currency = entry.getKey();
			Long amount = entry.getValue();
			
			CurrencyAccount account = wallet.getPrivateAccount(currency);	
				account.add(-amount);
		
			account.save();
		}
		
		return new DescribedFunctionResult(Status.OK, "Успешная покупка "+getName()+"!");
	}
	
	public String getDescription(PlamesLocale locale) {
		
		String result = "";
		
		result += locale.getMessage("name_word")+": "+getName();
		result += "<br/>";
		result += locale.getMessage("sale_word")+": "+String.format("%.2f", getDiscount()*100D)+"%";
		
		if(itemStacks.size() > 0) {
			
			result += "<br/>";
			result += locale.getMessage("items_word")+": ";
			
			for(ItemStack is : itemStacks) {
				
				Item item = is.getItem();
				
				result += "<br/>";
				result +="    - ["+item.getTargetApplicationName()+"] "+item.getName()+" X "+is.getQuantity();
			}
		}
		
		if(customPrice != null) {
			
			result += "<br/>";
			result += locale.getMessage("price_word")+": ";
			
			for(Entry<Currency, Long> entry : customPrice.getCurrencies().entrySet()) {
				
				Currency currency = entry.getKey();
				String amount = currency.getDisplayAmount(entry.getValue());
				
				result += "<br/>";
				result +="    - "+currency.getName()+": "+amount+" "+currency.getTag();
			}
		}
		
		return result;
	}
	
	public void setActive(boolean active) {
		
		this.active = active;
	}
	
	public boolean isActive() {
		
		return this.active;
	}
	
	public void setItemStacks(Set<ItemStack> stacks) {
		
		this.itemStacks = stacks;
	}
	
	@Override
	public Set<ItemStack> getItemStacks() {
		
		return this.itemStacks;
	}

	@Override
	public Price calcTotalPrice() {
		
		if(customPrice != null) {
			
			return customPrice;
		}
		
		Price price = new PriceImpl();
		
			Set<Price> itemsStacksPrices = getItemsStacksPrices();
		
				price = price.add(itemsStacksPrices);
		
			price = price.multiply(1D-discount);
		
		return price;
	}
	
	public Set<Price> getItemsStacksPrices() {
		
		Set<Price> result = new HashSet<>();
		
		for(ItemStack stack : itemStacks){
			
			result.add(stack.getPrice());
		}
		
		return result;
	}
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	public String getName() {
		
		return this.name;
	}
	
	public void setCustomPrice(Price price) {
		
		this.customPrice = price;
	}
	
	public Price getCustomPrice() {
		
		return this.customPrice;
	}
	
	public void setDiscount(double discount) {

		this.discount = discount;
	}
	
	public double getDiscount() {
		
		return this.discount;
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
		
		repository.save(this);
	}
	
	public ObjectNode toJson(ObjectMapper mapper) {
		
		ObjectNode node = mapper.createObjectNode();
		
			node.put("id", this.getId());
			node.put("name", this.getName());
			node.put("active", this.isActive());
			
			if(customPrice != null) {
				
				node.put("custom_price", ((PriceImpl) this.getCustomPrice()).toJson(mapper));
			}
			
			node.put("discount", this.getDiscount());
			
			ArrayNode jsonItemStacks = node.putArray("item_stacks");
			
				for(ItemStack is : itemStacks) {
					
					jsonItemStacks.add(((ItemStackImpl) is).toJson(mapper));
				}
			
		return node;
	}
	
	public void loadFromJson(ObjectNode node) {
		
		if(node.has("name") && node.get("name").isTextual()) {
			
			this.setName(node.get("name").asText());
		}

		if(node.has("active") && node.get("active").isBoolean()) {
			
			this.setActive(node.get("active").asBoolean());
		}
		
		if(node.has("custom_price")) {
			
			JsonNode jsonCustomPrice = node.get("custom_price");
		
			if((jsonCustomPrice == null || jsonCustomPrice.isNull())) {
				
				if(this.customPrice != null) {
					
					this.customPrice.delete();
					this.setCustomPrice(null);
				}
			}
			else if(jsonCustomPrice.isObject()){
				
				this.setCustomPrice(PriceImpl.fromJson((ObjectNode) node.get("custom_price")));
			}
		}
		
		if(node.has("discount") && node.get("discount").isNumber()) {
			
			this.setDiscount(node.get("discount").asDouble());
		}
		
		if(node.has("item_stacks") && node.get("item_stacks").isArray()) {
			
			Set<ItemStack> itemStacks = this.getItemStacks();
			
				itemStacks.clear();
				
			ArrayNode jsonItemStacks = (ArrayNode) node.get("item_stacks");
		
			for(JsonNode jsonItemStack : jsonItemStacks) {
				
				ItemStackImpl itemStack = ItemStackImpl.parseFromJson((ObjectNode) jsonItemStack);
				
				itemStacks.add(itemStack);
			}
		}
	}
	
	public static OfferImpl create() {
		
		OfferImpl offer = new OfferImpl();
		
			offer = repository.save(offer);
			
		return offer;
	}
	
	public static List<OfferImpl> search(String name, Long id) {

		if(name != null) {
			
			name = name.toLowerCase();
		}
		
		String finalName = name;
		
		List<OfferImpl> result = getOrderedByName();
		
		result.removeIf((OfferImpl offer)-> {
			
			if(finalName != null && !finalName.isEmpty()) {
				
				if(offer.getName().toLowerCase().contains(finalName)) return false;
			}
			
			if(id != null) {
				
				if(offer.getId().longValue() == id.longValue()) return false;
			
				if(String.valueOf(offer.getId()).contains(String.valueOf(id))) return false;
			}
			
			return true;
		});
		
		return result;
	}
	
	public static List<OfferImpl> getOrderedByName(){
		
		return repository.getOrderedByName();
	}
	
	public static OfferImpl getByName(String name) {
		
		return repository.getByName(name);
	}
	
	public static OfferImpl getById(Long id) {
		
		return repository.getOne(id);
	}
	
	public static List<OfferImpl> getAll() {
		
		return repository.findAll();
	}
	
	public static void setRepository(OfferRepository rep) {
		
		repository = rep;
	}
}