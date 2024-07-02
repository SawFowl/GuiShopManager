package sawfowl.guishopmanager.configure.locale.abstractlocale;

import org.spongepowered.api.item.inventory.ItemStack;

import net.kyori.adventure.text.Component;

import sawfowl.localeapi.api.LocaleReference;

public interface Messages {

	interface Auction extends LocaleReference {

		Component maxVolume();

		Component buy(ItemStack itemStack, double removed, double balance, Component seller);

		Component sell(ItemStack itemStack, double added, double balance, Component buyer);

		Component expired();

		Component betExpired();

		Component tax();

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

	interface Transactions extends LocaleReference {

		Component itemSell(ItemStack itemStack, double balance);

		Component itemBuy(ItemStack itemStack, double balance);

		Component buyCommands(double removed, double balance);

	}

	Auction auction();

	Exceptions exceptions();

	Transactions transactions();

}
