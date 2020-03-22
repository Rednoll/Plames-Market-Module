package com.inwaiders.plames.modules.market.domain.commands;

import java.util.Collection;

import com.inwaiders.plames.api.command.CommandException;
import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.api.user.User;
import com.inwaiders.plames.domain.messenger.command.MessengerCommand;
import com.inwaiders.plames.modules.market.MarketModule;
import com.inwaiders.plames.modules.market.domain.cart.Cart;
import com.inwaiders.plames.modules.market.domain.stack.ItemStack;
import com.inwaiders.plames.system.utils.MessageUtils;

public class MarketCartCommand extends MessengerCommand {

	public MarketCartCommand() {
	
		this.addAliases("cart");
	}
	
	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
	
		User user = profile.getUser();
		
		String targetApplicationName = null;
		
		if(args.length == 0 || args[0] == null || args[0].isEmpty()) {
			
			targetApplicationName = profile.getMessengerType();
		}
		else {
			
			targetApplicationName = args[0];
		}
		
		Cart cart = Cart.getCart(user, targetApplicationName);
	
		if(cart == null) {
			
			throw new CommandException("$cart.not_found", targetApplicationName);
		}
		
		StringBuilder builder = new StringBuilder();
			builder.append(user.getLocale().getMessage("command.cart_view.cart_label", targetApplicationName)+":\n");
		
			Collection<ItemStack> itemStacks = cart.getItemStacks();
	
			for(ItemStack is : itemStacks) {
				
				builder.append(is.getItem().getName()+" x"+is.getQuantity()+"\n");
			}
			
		MessageUtils.send(MarketModule.getSystemProfile(), profile, builder.toString());
	}
}
