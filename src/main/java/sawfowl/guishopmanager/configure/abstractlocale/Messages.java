package sawfowl.guishopmanager.configure.abstractlocale;

import net.kyori.adventure.text.Component;

import sawfowl.localeapi.api.PluginLocale;

public interface Messages extends PluginLocale {

	interface Auction {

		Component maxVolume();

		Component buy();

		Component sell();

		Component expired();

		Component betExpired();

		Component tax();

		Component noEmptySlots();

		Component itemNotFound();

		Component cancelBuy();

		Component itemAdded();

		Component betIsNotSet();

		Component itemBlocked();

		Component addedItemBlocking();

		Component addedMaskBlocking();

		Component longNBT();

	}

	Auction auction();

	Component noMoney();

	Component noMoneyForFee();

	Component noItems();

	Component itemSell();

	Component itemBuy();

	Component buyCommands();

	Component shopIDNotPresent();

	Component shopIDAlreadyExists();

	Component shopIDNotExists();

	Component invalidShopID();

	Component menuNotPresent();

	Component invalidMenuId();

	Component slotNotPresent();

	Component invalidSlot();

	Component buyPriceNotPresent();

	Component sellPriceNotPresent();

	Component commandAdded();

	Component shopItemAdded();

	Component successDelete();

	Component shopListEmpty();

	Component shopListEmptyEditor();

	Component debugOn();

	Component debugOff();

	Component deload();

	Component emptyTranslateName();

	Component localeNotExist();

	Component localeNotPresent();

	Component translateNotPresent();

	Component translateAdded();

	Component shopListTitle();

	Component shopListPadding();

	Component commandsTitle();

	Component commandsPadding();

	String economyNotFound();

}
