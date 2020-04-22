package com.inwaiders.plames.modules.market;

import com.inwaiders.plames.modules.market.domain.commands.MarketCommand;
import com.inwaiders.plames.modules.market.domain.item.ItemHlRepository;
import com.inwaiders.plames.modules.market.domain.item.ItemImpl;
import com.inwaiders.plames.modules.market.domain.market.MarketHlRepository;
import com.inwaiders.plames.modules.market.domain.market.MarketImpl;

import enterprises.inwaiders.plames.api.command.CommandRegistry;
import enterprises.inwaiders.plames.api.locale.PlamesLocale;
import enterprises.inwaiders.plames.domain.messenger.profile.impl.SystemProfile;
import enterprises.inwaiders.plames.domain.module.impl.ModuleBase;
import enterprises.inwaiders.plames.modules.webcontroller.domain.module.BaseWebDescription;
import enterprises.inwaiders.plames.modules.webcontroller.domain.module.WebDescribedModule;
import enterprises.inwaiders.plames.modules.webcontroller.domain.module.WebDescription;
import enterprises.inwaiders.plames.modules.webcontroller.domain.module.button.Button;

public class MarketModule extends ModuleBase implements WebDescribedModule {

	private static MarketModule INSTANCE = new MarketModule();
	
	private BaseWebDescription webDescription = new BaseWebDescription();
	
	public MarketModule() {
	
		Button button = new Button();
			button.setName("Список предложений");
			button.setFontColor("#7892A3");
			button.setBackgroundColor("#BAE1FF");
			button.setBordersColor("#9EBFD8");
			button.setTargetPage("/market/offers");

		webDescription.addButton(button);
		
		button = new Button();
			button.setName("Список предметов");
			button.setFontColor("#7892A3");
			button.setBackgroundColor("#BAE1FF");
			button.setBordersColor("#9EBFD8");
			button.setTargetPage("/market/items");

			webDescription.addButton(button);
	}
	
	@Override
	public void preInit() {
		
		CommandRegistry registry = CommandRegistry.getDefaultRegistry();
			registry.registerCommand(new MarketCommand());
		
		MarketHlRepository.setRepository(new MarketImpl.HighLevelRepository());
		ItemHlRepository.setRepository(new ItemImpl.HighLevelRepository());
	}
	
	@Override
	public void init() {
		
		
	}

	@Override
	public WebDescription getWebDescription() {
		
		return this.webDescription;
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
