package com.inwaiders.plames.modules.market.domain.item;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.inwaiders.plames.modules.market.dao.item.ItemRepository;
import com.inwaiders.plames.modules.market.domain.price.Price;
import com.inwaiders.plames.modules.market.domain.price.PriceImpl;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;

import enterprises.inwaiders.plames.api.locale.PlamesLocale;
import enterprises.inwaiders.plames.dao.EntityLink;
import enterprises.inwaiders.plames.spring.SpringUtils;

@Entity(name = "Item")
@Table(name = "market_items")
public class ItemImpl implements Item {
	
	private static transient ItemRepository repository;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@Column(name = "name")
	private String name = null;
	
	@ElementCollection
	@Column(name = "aliases", table = "market_items_aliases")
	private Set<String> aliases = new HashSet<>();
	
	@OneToOne(targetEntity = PriceImpl.class, cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "price_id")
	private Price price = null;
	
	@Column(name = "metadata")
	private String metadata = null;
	
	@Column(name = "target_application_name")
	private String targetApplicationName = null;
	
	@Column(name = "deleted")
	private volatile boolean deleted = false;
	
	@Column(name = "active")
	private boolean active = false;
	
	@Override
	public int hashCode() {
		
		return Objects.hash(aliases, deleted, id, metadata, name, price, targetApplicationName);
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItemImpl other = (ItemImpl) obj;
		return Objects.equals(aliases, other.aliases) && deleted == other.deleted && Objects.equals(id, other.id)
				&& Objects.equals(metadata, other.metadata) && Objects.equals(name, other.name)
				&& Objects.equals(price, other.price)
				&& Objects.equals(targetApplicationName, other.targetApplicationName);
	}

	public String getDescription(PlamesLocale locale) {
		
		String result = "";
		
		result += locale.getMessage("name_word")+": "+getName()+" ("+String.join(", ", aliases)+")";
		result += "<br/>";
		result += "Metadata: "+getMetadata();
		result += "<br/>";
		result += locale.getMessage("tan_word")+": "+getTargetApplicationName();
		
		if(price != null) {
			
			result += "<br/>";
			result += locale.getMessage("price_word")+": ";
			
			for(Entry<Currency, Long> entry : price.getCurrencies().entrySet()) {
				
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
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	@Override
	public String getName() {
		
		return this.name;
	}

	@Override
	public Set<String> getAliases() {
		
		return this.aliases;
	}
	
	public void setPrice(Price price) {
		
		this.price = price;
	}

	@Override
	public Price getPrice() {
		
		return this.price;
	}

	public void setMetadata(String meta) {
		
		this.metadata = meta;
	}
	
	@Override
	public String getMetadata() {
		
		return this.metadata;
	}

	public void setTargetApplicationName(String name) {
		
		this.targetApplicationName = name;
	}
	
	@Override
	public String getTargetApplicationName() {
		
		return this.targetApplicationName;
	}

	@Override
	public Long getId() {
	
		return this.id;
	}
	
	public void save() {
		
		if(!deleted) {
			
			repository.save(this);
		}
	}
	
	public void delete() {
		
		deleted = true;
	}
	
	public ObjectNode toJson(ObjectMapper mapper) {
		
		ObjectNode node = mapper.createObjectNode();
			node.put("id", this.getId());
			node.put("name", this.getName());
			node.put("active", this.isActive());
			
			ArrayNode aliasesJson = node.putArray("aliases");
			
				for(String alias : this.getAliases()) {
					
					aliasesJson.add(alias);
				}
			
			node.put("metadata", this.getMetadata());
			node.put("target_application_name", this.getTargetApplicationName());
			
			if(price != null) {
				
				node.put("price", ((PriceImpl) this.getPrice()).toJson(mapper));
			}
			
		return node;
	}
	
	public void loadFromJson(ObjectNode node) {

		if(node.has("active") && node.get("active").isBoolean()) {
			
			this.setActive(node.get("active").asBoolean());
		}
		
		if(node.has("name") && node.get("name").isTextual()) {
		
			this.setName(node.get("name").asText());
		}
		
		if(node.has("metadata") && node.get("metadata").isTextual()) {
			
			this.setMetadata(node.get("metadata").asText());
		}
		
		if(node.has("target_application_name") && node.get("target_application_name").isTextual()) {
			
			this.setTargetApplicationName(node.get("target_application_name").asText());
		}
		
		if(node.has("price") && node.get("price").isObject()) {
			
			this.setPrice(PriceImpl.fromJson((ObjectNode) node.get("price")));
		}
	}
	
	public static ItemImpl create() {
		
		ItemImpl item = new ItemImpl();
		
			item = repository.save(item);
	
		return item;
	}
	
	public static ItemImpl getByMetadata(String meta) {
		
		return repository.getByMetadata(meta);
	}

	public static ItemImpl getByName(String name) {
		
		return repository.getByName(name);
	}
	
	public static ItemImpl getById(long id) {
		
		return repository.getOne(id);
	}
	
	public static List<ItemImpl> getOrderedByName() {
		
		return repository.getOrderedByName();
	}
	
	public static List<ItemImpl> search(String name, Long id, String tan) {
		
		if(name != null) {
			
			name = name.toLowerCase();
		}
		
		if(tan != null) {
			
			tan = tan.toLowerCase();
		}
		
		String finalName = name;
		String finalTan = tan;
		
		List<ItemImpl> result = getOrderedByName();
		
		result.removeIf((ItemImpl item)-> {
			
			if(finalName != null && !finalName.isEmpty()) {
				
				if(item.getName().toLowerCase().contains(finalName)) return false;
				
				Set<String> aliases = item.getAliases();
				
				for(String aliase : aliases) {
					
					if(aliase.toLowerCase().contains(finalName)) {
					
						return false;
					}
				}
			}
			
			if(id != null) {
				
				if(item.getId().longValue() == id.longValue()) return false;
				if(String.valueOf(item.getId()).contains(String.valueOf(id))) return false;
			}
			
			if(finalTan != null && !finalTan.isEmpty()) {
				
				if(item.getTargetApplicationName().toLowerCase().equals(finalTan)) return false;
			}
			
			return true;
		});
		
		return result;
	}
	
	public static List<ItemImpl> getAll() {
		
		return repository.findAll();
	}
	
	public static void setRepository(ItemRepository rep) {
		
		repository = rep;
	}
	
	public static class HighLevelRepository extends ItemHlRepository<ItemImpl> {

		public EntityLink getLink(ItemImpl item) {
			
			return new EntityLink(SpringUtils.getEntityName(ItemImpl.class), item.getId());
		}
		
		@Override
		public void save(ItemImpl item) {
			
			item.save();
		}
		
		@Override
		public ItemImpl create() {
			
			return ItemImpl.create();
		}

		public ItemImpl getById(Long id) {
			
			return ItemImpl.getById(id);
		}
		
		@Override
		public ItemImpl getByMetadata(String meta) {
			
			return ItemImpl.getByMetadata(meta);
		}
	}
}
