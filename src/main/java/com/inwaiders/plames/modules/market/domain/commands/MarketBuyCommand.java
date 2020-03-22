package com.inwaiders.plames.modules.market.domain.commands;

import com.inwaiders.plames.api.command.CommandException;
import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.api.utils.DescribedFunctionResult;
import com.inwaiders.plames.domain.messenger.command.MessengerCommand;
import com.inwaiders.plames.modules.market.MarketModule;
import com.inwaiders.plames.modules.market.domain.offer.OfferImpl;
import com.inwaiders.plames.system.utils.MessageUtils;

public class MarketBuyCommand extends MessengerCommand {

	public MarketBuyCommand() {
	
		this.addAliases("buy");
	}
	
	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
		
		User user = profile.getUser();
		
		String offerName = args[0];
	
		OfferImpl offer = OfferImpl.getByName(offerName);

		if(offer == null) {
			
			throw new CommandException("$offer.not_found", offerName);
		}
		
		int quantity = 1;
		
		if(args.length == 2) {
		
			try {
			
				quantity = Integer.valueOf(args[1]);
			}
			catch(NumberFormatException e) {
				
				throw new CommandException("$command.buy.quantity_format_er");
			}
		}
		
		DescribedFunctionResult result = offer.buy(user, quantity);
		
		MessageUtils.send(MarketModule.getSystemProfile(), profile, result.getDescription());
	}
}
