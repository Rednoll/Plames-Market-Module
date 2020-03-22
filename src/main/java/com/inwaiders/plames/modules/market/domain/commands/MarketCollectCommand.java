package com.inwaiders.plames.modules.market.domain.commands;

import com.inwaiders.plames.api.command.CommandException;
import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.api.utils.DescribedFunctionResult;
import com.inwaiders.plames.domain.messenger.command.MessengerCommand;
import com.inwaiders.plames.modules.market.MarketModule;
import com.inwaiders.plames.modules.market.domain.cart.Cart;
import com.inwaiders.plames.modules.market.domain.profile.SupportMarket;
import com.inwaiders.plames.system.utils.MessageUtils;

public class MarketCollectCommand extends MessengerCommand {

	public MarketCollectCommand() {
		
		this.addAliases("collect");
	}
	
	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
		
		if(profile instanceof SupportMarket) {
			
			SupportMarket target = (SupportMarket) profile;

			String targetApplicationName = profile.getMessengerType();
			
			Cart cart = Cart.getCart(profile.getUser(), targetApplicationName);
			
			if(cart == null) {
					
				MessageUtils.send(MarketModule.getSystemProfile(), profile, "$cart.not_found", targetApplicationName);
			}
			
			DescribedFunctionResult result = target.collectMarketCart(cart);
		
			if(result != null && result.getDescription() != null && !result.getDescription().isEmpty()) {
				
				MessageUtils.send(MarketModule.getSystemProfile(), profile, result.getDescription());
			}
		}
		else {
			
			MessageUtils.send(MarketModule.getSystemProfile(), profile, "$cart.not_supported");
		}
	}
}
