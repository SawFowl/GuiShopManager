package sawfowl.guishopmanager.storage;

import java.util.UUID;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.data.commandshop.CommandItemData;
import sawfowl.guishopmanager.data.commandshop.CommandShopData;
import sawfowl.guishopmanager.data.commandshop.CommandShopMenuData;
import sawfowl.guishopmanager.data.shop.Shop;
import sawfowl.guishopmanager.data.shop.ShopItem;
import sawfowl.guishopmanager.data.shop.ShopMenuData;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;
import sawfowl.guishopmanager.utils.Currencies;

public interface DataStorage {

	void saveShop(String shopId);

	void loadShops();

	void deleteShop(String shopId);

	void saveCommandsShop(String shopId);

	void loadCommandsShops();

	void deleteCommandsShop(String shopId);

	void loadAuction();

	void saveAuctionStack(SerializedAuctionStack serializedAuctionStack);

	void removeAuctionStack(UUID stackUUID);

	void saveExpireAuctionData(SerializedAuctionStack serializedAuctionStack);

	void removeExpireAuctionData(SerializedAuctionStack serializedAuctionStack);

	void saveExpireBetAuctionData(SerializedAuctionStack serializedAuctionStack);

	void removeExpireBetAuctionData(SerializedAuctionStack serializedAuctionStack);

	default Shop setShopCurrencies(GuiShopManager plugin, Shop shop) {
		for(ShopMenuData shopMenuData : shop.getMenus().values()) for(ShopItem shopItem : shopMenuData.getItems().values()) {
			shopItem.getPrices().forEach(price -> {
				if(!price.isDefault()) {
					Currencies.getCurrency(price.getCurrencyId()).ifPresent(currency -> {
						price.setCurrency(currency);
					});
				} else price.setCurrency(plugin.getEconomyService().defaultCurrency());
			});
			shopItem.getPrices().removeIf(p -> !p.isPresent());
		}
		return shop;
	}

	default CommandShopData setCommandShopCurrencies(GuiShopManager plugin, CommandShopData shop) {
		for(CommandShopMenuData shopMenuData : shop.getMenus().values()) for(CommandItemData shopItem : shopMenuData.getItems().values()) {
			shopItem.getPrices().forEach(price -> {
				if(!price.isDefault()) {
					Currencies.getCurrency(price.getCurrencyId()).ifPresent(currency -> {
						price.setCurrency(currency);
					});
				} else price.setCurrency(plugin.getEconomyService().defaultCurrency());
			});
			shopItem.getPrices().removeIf(p -> !p.isPresent());
		}
		return shop;
	}

	default SerializedAuctionStack setAuctionCurrencies(GuiShopManager plugin, SerializedAuctionStack auctionStack) {
		auctionStack.getPrices().forEach(price -> {
			if(!price.isDefault()) {
				Currencies.getCurrency(price.getCurrencyId()).ifPresent(currency -> {
					price.setCurrency(currency);
				});
			} else price.setCurrency(plugin.getEconomyService().defaultCurrency());
		});
		auctionStack.getPrices().removeIf(p -> !p.isPresent());
		if(auctionStack.getBetData() != null) auctionStack.getBetData().setCurrency(plugin.getEconomy().checkCurrency(auctionStack.getBetData().getCurrencyId()));
		return auctionStack;
	}

}