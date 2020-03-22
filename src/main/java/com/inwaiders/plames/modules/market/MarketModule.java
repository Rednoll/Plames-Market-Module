package com.inwaiders.plames.modules.market;

import com.inwaiders.plames.api.command.CommandRegistry;
import com.inwaiders.plames.api.locale.PlamesLocale;
import com.inwaiders.plames.domain.messenger.profile.impl.SystemProfile;
import com.inwaiders.plames.modules.market.domain.commands.MarketCommand;
import com.inwaiders.plames.modules.market.domain.item.ItemHlRepository;
import com.inwaiders.plames.modules.market.domain.item.ItemImpl;
import com.inwaiders.plames.modules.market.domain.market.MarketHlRepository;
import com.inwaiders.plames.modules.market.domain.market.MarketImpl;
import com.inwaiders.plames.modules.webcontroller.domain.module.WebDescribedModuleBase;
import com.inwaiders.plames.modules.webcontroller.domain.module.button.Button;

public class MarketModule extends WebDescribedModuleBase {

	private static MarketModule INSTANCE = new MarketModule();
	
	public MarketModule() {
	
		Button button = new Button();
			button.setName("Список предложений");
			button.setFontColor("#7892A3");
			button.setBackgroundColor("#BAE1FF");
			button.setBordersColor("#9EBFD8");
			button.setTargetPage("/market/offers");

		this.buttons.add(button);
		
		button = new Button();
			button.setName("Список предметов");
			button.setFontColor("#7892A3");
			button.setBackgroundColor("#BAE1FF");
			button.setBordersColor("#9EBFD8");
			button.setTargetPage("/market/items");

		this.buttons.add(button);
	}
	
	@Override
	public void preInit() {
		
		CommandRegistry.registerCommand(new MarketCommand());
		
		MarketHlRepository.setRepository(new MarketImpl.HighLevelRepository());
		ItemHlRepository.setRepository(new ItemImpl.HighLevelRepository());
	}
	
	@Override
	public void init() {
		
		
	}

	@Override
	public String getName() {
		
		return "Market Module";
	}

	@Override
	public String getVersion() {
		
		return "1V";
	}

	@Override
	public String getDescription() {
		
		return PlamesLocale.getSystemMessage("module.market.description");
	}

	@Override
	public String getType() {
		
		return "functional";
	}

	@Override
	public String getLicenseKey() {
		
		return null;
	}

	@Override
	public long getSystemVersion() {
		
		return 0;
	}

	@Override
	public long getId() {
		
		return 857;
	}
	
	public static SystemProfile getSystemProfile() {
		
		return INSTANCE.getProfile();
	}
	
	public static MarketModule getInstance() {
		
		return INSTANCE;
	}
}
