package com.inwaiders.plames.modules.market.domain.commands;

import java.util.Map.Entry;

import com.inwaiders.plames.modules.market.MarketModule;
import com.inwaiders.plames.modules.market.domain.offer.OfferImpl;
import com.inwaiders.plames.modules.market.domain.price.Price;
import com.inwaiders.plames.modules.market.domain.stack.ItemStack;
import com.inwaiders.plames.modules.wallet.domain.currency.Currency;

import enterprises.inwaiders.plames.api.command.CommandException;
import enterprises.inwaiders.plames.api.messenger.profile.UserProfile;
import enterprises.inwaiders.plames.domain.messenger.command.MessengerCommand;
import enterprises.inwaiders.plames.system.utils.MessageUtils;

public class MarketOfferDescribeCommand extends MessengerCommand {

	public MarketOfferDescribeCommand() {
	
		this.addAliases("offer");
	}
	
	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
		
		if(args.length < 1) {
			
			throw new CommandException("$command.reqeust_offer_name");
		}
	
		String offerName = args[0];
		
		OfferImpl offer = OfferImpl.getByName(offerName);
	
		if(offer == null) {
			
			throw new CommandException("$offer.not_found", offerName);
		}
		
		StringBuilder builder = new StringBuilder();
			builder.append(offerName+":");
		
			for(ItemStack is : offer.getItemStacks()) {
				
				builder.append("\n - "+is.getItem().getName()+" x"+is.getQuantity());
			}
			
			builder.append("\n"+profile.getUser().getLocale().getMessage("price_word")+":");
			
			Price price = offer.calcTotalPrice();
			
			for(Entry<Currency, Long> entry : price.getCurrencies().entrySet()) {
				
				Currency cur = entry.getKey();
				
				builder.append("\n - "+cur.getName()+": "+cur.getDisplayAmount(entry.getValue())+" "+cur.getTag());
			}
		
		MessageUtils.send(MarketModule.getSystemProfile(), profile, builder.toString());
	}
}
