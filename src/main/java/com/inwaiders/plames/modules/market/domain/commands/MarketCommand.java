package com.inwaiders.plames.modules.market.domain.commands;

import com.inwaiders.plames.api.command.CommandException;
import com.inwaiders.plames.api.messenger.profile.UserProfile;
import com.inwaiders.plames.domain.messenger.command.MessengerCommand;

public class MarketCommand extends MessengerCommand{

	public MarketCommand() {
		
		this.addChildCommand(new MarketBuyCommand());
		this.addChildCommand(new MarketCartCommand());
		this.addChildCommand(new MarketCollectCommand());
		this.addChildCommand(new MarketOffersCommand());
		this.addChildCommand(new MarketOfferDescribeCommand());
		
		this.addAliases("market");
	}

	@Override
	public void run(UserProfile profile, String... args) throws CommandException {
		
		
	}
}
