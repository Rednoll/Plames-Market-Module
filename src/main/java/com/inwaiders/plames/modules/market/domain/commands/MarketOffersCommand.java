package com.inwaiders.plames.modules.market.domain.commands;

import java.util.List;

import com.inwaiders.plames.modules.market.MarketModule;
import com.inwaiders.plames.modules.market.domain.offer.OfferImpl;

import enterprises.inwaiders.plames.api.command.CommandException;
import enterprises.inwaiders.plames.api.messenger.profile.UserProfile;
import enterprises.inwaiders.plames.domain.messenger.command.MessengerCommand;
import enterprises.inwaiders.plames.system.utils.MessageUtils;

public class MarketOffersCommand extends MessengerCommand {

	public MarketOffersCommand() {
	
		this.addAliases("offers");
	}
	
	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
		
		int pageSize = 5;
		int pageIndex = 1;
		
		if(args.length > 0) {
			
			try {
				
				pageIndex = Integer.valueOf(args[0]);
			}
			catch(NumberFormatException e) {
				
				throw new CommandException("$command.offers_list.index_er");
			}
		}
		
		if(pageIndex < 1) {
			
			throw new CommandException("$command.offers_list.index_er");
		}
		
		pageIndex -= 1;
		
		List<OfferImpl> offers = OfferImpl.getOrderedByName();
		
		int pagesCount = (int) Math.ceil((double)offers.size() / (double)pageSize);
		
		if(pagesCount < pageIndex) {
			
			throw new CommandException("$command.offers_list.index_er");
		}
		
		StringBuilder builder = new StringBuilder();
			builder.append("======== "+(pageIndex+1)+" / "+pagesCount+" ========");
		
			for(int i = 0; i < pageSize; i++) {
				
				int pI = pageIndex*pageSize + i;
				
				if(pI >= offers.size()) break;
				
				OfferImpl offer = offers.get(pI);
				
				builder.append("\n"+(pI+1)+". "+offer.getName());
			}
	
			builder.append("\n"+"======== "+(pageIndex+1)+" / "+pagesCount+" ========");
			
		MessageUtils.send(MarketModule.getSystemProfile(), profile, builder.toString());
	}
}