package sawfowl.guishopmanager.configure.locale.abstractlocale;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import net.kyori.adventure.text.Component;

import sawfowl.localeapi.api.Translation;

public interface Messages {

	interface Auction extends Translation {

		Component maxVolume();

		Component buy(ItemStack itemStack, Currency currency, double removed, double balance, Component seller);

		Component sell(ItemStack itemStack, Currency currency, double added, double balance, Component buyer);

		Component expired();

		Component betExpired();

		Component tax(Currency currency, double amount);

		Component noEmptySlots(int size);

		Component itemNotFound();

		Component cancelBuy();

		Component itemAdded();

		Component betIsNotSet();

		Component itemBlocked();

		Component longComponents();

	}

	interface Exceptions {

		Component noMoney();

		Component noMoneyForFee();

		Component noItems();

		String economyNotFound();

	}

	interface Shop extends Translation {

		Component itemSell(ItemStack itemStack, Currency currency, double added, double balance);

		Component itemBuy(ItemStack itemStack, Currency currency, double removed, double balance);

		Component buyCommands(Currency currency, double removed, double balance);

		Component shopNotExists(String shop);

	}

	Auction auction();

	Exceptions exceptions();

	Shop shop();

}
