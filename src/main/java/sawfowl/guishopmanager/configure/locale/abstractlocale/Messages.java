package sawfowl.guishopmanager.configure.locale.abstractlocale;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import net.kyori.adventure.text.Component;

import sawfowl.localeapi.api.LocaleReference;

public interface Messages {

	interface Auction extends LocaleReference {

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

		Component addedItemBlocking();

		Component addedMaskBlocking();

		Component longNBT();

	}

	interface Exceptions {

		Component noMoney();

		Component noMoneyForFee();

		Component noItems();

		String economyNotFound();

	}

	interface ShopTransactions extends LocaleReference {

		Component itemSell(ItemStack itemStack, Currency currency, double added, double balance);

		Component itemBuy(ItemStack itemStack, Currency currency, double removed, double balance);

		Component buyCommands(Currency currency, double removed, double balance);

	}

	Auction auction();

	Exceptions exceptions();

	ShopTransactions shopTransactions();

}
