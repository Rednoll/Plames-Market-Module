package com.inwaiders.plames.modules.market.domain.price;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyClass;
import javax.persistence.MapKeyJoinColumn;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.inwaiders.plames.modules.market.dao.price.PriceRepository;
import com.inwaiders.plames.modules.wallet.domain.account.CurrencyAccount;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;
import com.inwaiders.plames.modules.wallet.domain.currency.impl.CurrencyImpl;

@Entity(name = "Price") //Not @Embeddable see bulletin "A1"
@Table(name = "market_prices")
public class PriceImpl implements Price {

	private static transient PriceRepository repository;
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id = null;
	
	@ElementCollection
	@CollectionTable(name = "market_prices_currencies_collection", joinColumns = @JoinColumn(name = "price_id"))
	@Column(name="amount")
	@MapKeyClass(CurrencyImpl.class)
	@MapKeyJoinColumn(name="currency_id", referencedColumnName="id")
    private Map<Currency, Long> currencies = new HashMap<>();
	
	@Column(name = "deleted")
	private volatile boolean deleted = false;
	
	public PriceImpl() {
		
	}
	
	public PriceImpl(Map<Currency, Long> currencyAmounts) {
	
		this.currencies = currencyAmounts;
	}
	
	public Price multiply(double multiplier) {
		
		Map<Currency, Long> newPriceCurAmounts = new HashMap<>();
	
		for(Entry<Currency, Long> entry : currencies.entrySet()) {
			
			newPriceCurAmounts.put(entry.getKey(), Math.round(((double) entry.getValue()*multiplier)));
		}
		
		return new PriceImpl(newPriceCurAmounts);
	}
	
	@Override
	public boolean checkPurchaseOpportunity(Collection<CurrencyAccount> accounts) {
		
		Map<Currency, Long> accountsBalances = new HashMap<>();
		
		for(CurrencyAccount account : accounts) {
		
			Currency currency = account.getCurrency();
			Long accBalance = account.getBalance();
			
			if(accountsBalances.containsKey(currency)) {
				
				accountsBalances.put(currency, accountsBalances.get(currency) + accBalance);
			}
			else {
				
				accountsBalances.put(currency, accBalance);
			}
		}
		
		for(Entry<Currency, Long> entry : currencies.entrySet()) {
			
			Currency currency = entry.getKey();
			Long necessaryAmount = entry.getValue();
			
			if(!accountsBalances.containsKey(currency)) return false;
			
			Long accBalance = accountsBalances.get(currency);
		
			if(accBalance < necessaryAmount) {
				
				return false;
			}
		}
		
		return true;
	}

	@Override
	public void addCurrencyAmount(Currency currency, Long amount) {

		if(!currencies.containsKey(currency)) {
			
			currencies.put(currency, amount);
		}
		else {
			
			Long value = currencies.get(currency);
		
			currencies.put(currency, value+amount);
		}
	}
	
	public Price add(Price price) {
		
		Map<Currency, Long> newCurrencyAmounts = new HashMap<>(this.currencies);
		
		for(Entry<Currency, Long> entry : price.getCurrencies().entrySet()) {
			
			Currency currency = entry.getKey();
			Long amount = entry.getValue();
			
			if(newCurrencyAmounts.containsKey(currency)) {
				
				newCurrencyAmounts.put(currency, newCurrencyAmounts.get(currency)+amount);
			}
			else {
				
				newCurrencyAmounts.put(currency, amount);
			}
		}
		
		return new PriceImpl(newCurrencyAmounts);
	}
	
	public Price add(Price... prices) {
		
		return add(Arrays.asList(prices));
	}
	
	public Price add(Collection<Price> prices) {
		
		Map<Currency, Long> newCurrencyAmounts = new HashMap<>(this.currencies);
		
		for(Price price : prices) {
			
			if(price == null) continue; 
			
			for(Entry<Currency, Long> entry : price.getCurrencies().entrySet()) {
				
				Currency currency = entry.getKey();
				Long amount = entry.getValue();
				
				if(newCurrencyAmounts.containsKey(currency)) {
					
					newCurrencyAmounts.put(currency, newCurrencyAmounts.get(currency)+amount);
				}
				else {
					
					newCurrencyAmounts.put(currency, amount);
				}
			}
		}
		
		return new PriceImpl(newCurrencyAmounts);
	}
	
	@Override
	public Map<Currency, Long> getCurrencies() {
		
		return currencies;
	}
	
	public static PriceImpl fromJson(ObjectNode data) {	
		
		PriceImpl price = null;
		
		if(data.has("id") && data.get("id").isNumber()) {
			
			long id = data.get("id").asLong();

			price = PriceImpl.getById(id);
			
			if(price == null) {
				
				price = PriceImpl.create();
			}
		}
		else {
			
			price = PriceImpl.create();
		}
		
		price.loadFromJson(data);
		price.save();
		
		return price;
	}

	public void loadFromJson(ObjectNode data) {
		
		currencies.clear();
		
		if(data.has("currencies") && data.get("currencies").isObject()) {
			
			JsonNode jsonCurrencies = data.get("currencies");
			
			Iterator<Entry<String, JsonNode>> entries = jsonCurrencies.fields();
			
			while(entries.hasNext()) {
				
				Entry<String, JsonNode> entry = entries.next();
			
				String currencyAliase = entry.getKey();
				JsonNode rawAmount = entry.getValue();
			
				Currency currency = Currency.parseBySign(currencyAliase);
			
				if(currency == null) continue;
				
				if(rawAmount.isNumber()) {
				
					long amount = currency.parseAmount(rawAmount.asText());
				
					this.currencies.put(currency, amount);
				}
			}
		}
	}
	
	public ObjectNode toJson(ObjectMapper mapper) {
		
		ObjectNode node = mapper.createObjectNode();
		
			node.put("id", this.id);
			
			ObjectNode jsonCurrencies = mapper.createObjectNode();
			
				for(Entry<Currency, Long> entry : currencies.entrySet()) {
					
					Currency currency = entry.getKey();
					Long amount = entry.getValue();
					
					jsonCurrencies.put(currency.getTag(), currency.getDisplayAmount(amount));
				}
				
			node.put("currencies", jsonCurrencies);
			
		return node;
	}
	
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
	}
	
	public static PriceImpl create() {
		
		PriceImpl price = new PriceImpl();
		
			price = repository.save(price);
	
		return price;
	}
	
	public static PriceImpl getById(long id) {
		
		return repository.getOne(id);
	}
	
	public static List<PriceImpl> getAll() {
		
		return repository.findAll();
	}
	
	public static void setRepository(PriceRepository rep) {
		
		repository = rep;
	}
}
