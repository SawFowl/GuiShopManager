package sawfowl.guishopmanager.configure.locale.def.messages;

import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;

import sawfowl.guishopmanager.configure.locale.PlaceholderKeys;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Messages.Auction;
import sawfowl.localeapi.api.Text;

@ConfigSerializable
public class ImplementAuction implements Auction {

	@Setting("MaxVolume")
	private Component maxVolume = deserialize("&cYou are already selling the maximum amount of items.");
	@Setting("Buy")
	@Comment("You can use the following placeholders to display the currency type:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Displays the currency symbol.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Displays the currency symbol using the design from its name.\n" + PlaceholderKeys.CURRENCY_NAME + " - Displays the name of the currency.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Displays the currency name in the plural.")
	private Component buy = deserialize("&aYou have successfully buy on auction &r%item%&ax%amount% for %currency-styled-symbol%%removed%. Your balance %currency-styled-symbol%%balance%. Seller &b%player%&a.");
	@Setting("Sell")
	@Comment("You can use the following placeholders to display the currency type:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Displays the currency symbol.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Displays the currency symbol using the design from its name.\n" + PlaceholderKeys.CURRENCY_NAME + " - Displays the name of the currency.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Displays the currency name in the plural.")
	private Component sell = deserialize("&aYou have successfully sold on auction &r%item%&ax%amount% for %currency-styled-symbol%%added%. Your balance %currency-styled-symbol%%balance%. Buyer &b%player%&a.");
	@Setting("Expired")
	private Component expired = deserialize("&aYour items have expired. Click on this message to get them back.");
	@Setting("BetExpired")
	private Component betExpired = deserialize("&aCompleted transaction for redemption of items at auction. Click on this message to get them.");
	@Setting("Tax")
	@Comment("You can use the following placeholders to display the currency type:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Displays the currency symbol.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Displays the currency symbol using the design from its name.\n" + PlaceholderKeys.CURRENCY_NAME + " - Displays the name of the currency.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Displays the currency name in the plural.")
	private Component tax = deserialize("&aWithholding tax: %currency-styled-symbol%%amount%.");
	@Setting("NoEmptySlots")
	private Component noEmptySlots = deserialize("&cThere are not enough empty slots in your inventory. Free at least %size%.");
	@Setting("ItemNotFound")
	private Component itemNotFound = deserialize("&cItem was purchased by someone else or removed from sale.");
	@Setting("CancelBuy")
	private Component cancelBuy = deserialize("&cYou can't buy or assign a bet on your item.");
	@Setting("ItemAdded")
	private Component itemAdded = deserialize("&aYou putted the item up for sale.");
	@Setting("BetIsNotSet")
	private Component betIsNotSet = deserialize("&eYou did not set your bet on the goods.");
	@Setting("ItemBlocked")
	private Component itemBlocked = deserialize("&cThis item cannot be put up for sale.");
	@Setting("LongComponents")
	private Component longComponents = deserialize("&cThe size of the components in the item is too large.");
	public ImplementAuction() {}

	@Override
	public Component maxVolume() {
		return maxVolume;
	}

	@Override
	public Component buy(ItemStack itemStack, Currency currency, double removed, double balance, Component seller) {
		return Text.of(this.buy).replace(PlaceholderKeys.ITEM, itemStack.asComponent().hoverEvent(HoverEvent.showItem(Key.key(ItemTypes.registry().valueKey(itemStack.type()).asString()), itemStack.quantity()))).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).replace(PlaceholderKeys.REMOVED, removed).replace(PlaceholderKeys.BALANCE, balance).replace(PlaceholderKeys.PLAYER, seller).get();
	}

	@Override
	public Component sell(ItemStack itemStack, Currency currency, double added, double balance, Component buyer) {
		return Text.of(this.sell).replace(PlaceholderKeys.ITEM, itemStack.asComponent().hoverEvent(HoverEvent.showItem(Key.key(ItemTypes.registry().valueKey(itemStack.type()).asString()), itemStack.quantity()))).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).replace(PlaceholderKeys.ADDED, added).replace(PlaceholderKeys.BALANCE, balance).replace(PlaceholderKeys.PLAYER, buyer).get();
	}

	@Override
	public Component expired() {
		return expired;
	}

	@Override
	public Component betExpired() {
		return betExpired;
	}

	@Override
	public Component tax(Currency currency, double amount) {
		return Text.of(tax).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).replace(PlaceholderKeys.AMOUNT, amount).get();
	}

	@Override
	public Component noEmptySlots(int size) {
		return replace(noEmptySlots, PlaceholderKeys.SIZE, size);
	}

	@Override
	public Component itemNotFound() {
		return itemNotFound;
	}

	@Override
	public Component cancelBuy() {
		return cancelBuy;
	}

	@Override
	public Component itemAdded() {
		return itemAdded;
	}

	@Override
	public Component betIsNotSet() {
		return betIsNotSet;
	}

	@Override
	public Component itemBlocked() {
		return itemBlocked;
	}

	@Override
	public Component longComponents() {
		return longComponents;
	}

}
