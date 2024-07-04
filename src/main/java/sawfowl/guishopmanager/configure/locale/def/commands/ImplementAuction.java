package sawfowl.guishopmanager.configure.locale.def.commands;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.guishopmanager.configure.locale.PlaceholderKeys;
import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.Auction;

@ConfigSerializable
public class ImplementAuction implements Auction {

	@Setting("AddedToBlackList")
	private Component addedToBlackList = deserialize("&cItem added to the blacklist.");
	@Setting("ItemIsAlreadyBlocked")
	private Component itemIsAlreadyBlocked = deserialize("&cThe item is already blocked.");
	@Setting("PriceNotPresent")
	private Component priceNotPresent = deserialize("&eYou didn't specify a price for your lot. To confirm the lot placement, click on this message.");
	@Setting("BetNotPresent")
	private Component betNotPresent = deserialize("&eYou didn't specify a bet for your lot. To confirm the lot placement, click on this message.");
	@Setting("CurrencyNotPresent")
	private Component currencyNotPresent = deserialize("&eYou did not specify the currency for your lot. The default currency will be used. To confirm the lot placement, click on this message. \n&eAvailable currencies: &6%currencies%&e.");
	public ImplementAuction() {}

	@Override
	public Component addedToBlackList() {
		return addedToBlackList;
	}

	@Override
	public Component allreadyBlocked() {
		return itemIsAlreadyBlocked;
	}

	@Override
	public Component priceNotPresent() {
		return priceNotPresent;
	}

	@Override
	public Component betNotPresent() {
		return betNotPresent;
	}

	@Override
	public Component currencyNotPresent(Component currencies) {
		return replace(currencyNotPresent, PlaceholderKeys.CURRENCIES, currencies);
	}

}
