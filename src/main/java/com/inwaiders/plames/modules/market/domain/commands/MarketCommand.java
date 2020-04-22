package com.inwaiders.plames.modules.market.domain.commands;

import enterprises.inwaiders.plames.api.command.CommandException;
import enterprises.inwaiders.plames.api.messenger.profile.UserProfile;
import enterprises.inwaiders.plames.domain.messenger.command.MessengerCommand;

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
