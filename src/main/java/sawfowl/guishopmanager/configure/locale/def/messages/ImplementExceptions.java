package sawfowl.guishopmanager.configure.locale.def.messages;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.configure.locale.abstractlocale.Messages.Exceptions;
import sawfowl.localeapi.api.TextUtils;

@ConfigSerializable
public class ImplementExceptions implements Exceptions {

	@Setting("NoMoney")
	private Component noMoney = TextUtils.deserializeLegacy("&cYou don't have enough money.");
	@Setting("NoMoneyForFee")
	private Component noMoneyForFee = TextUtils.deserializeLegacy("&cYou do not have enough money to list the item for sale at the auction.");
	@Setting("NoItems")
	private Component noItems = TextUtils.deserializeLegacy("&cYou don't have enough items.");
	@Setting("EconomyNotFound")
	private String economyNotFound = "There is no economy plugin on the server. The functions of the plugin will not be available.";
	public ImplementExceptions() {}

	@Override
	public Component noMoney() {
		return noMoney;
	}

	@Override
	public Component noMoneyForFee() {
		return noMoneyForFee;
	}

	@Override
	public Component noItems() {
		return noItems;
	}

	@Override
	public String economyNotFound() {
		return economyNotFound;
	}

}
