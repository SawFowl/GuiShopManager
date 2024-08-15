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
import sawfowl.guishopmanager.configure.locale.abstractlocale.Messages.Shop;
import sawfowl.localeapi.api.Text;

@ConfigSerializable
public class ImplementShop implements Shop {

	@Setting("ItemSell")
	@Comment("You can use the following placeholders to display the currency type:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Displays the currency symbol.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Displays the currency symbol using the design from its name.\n" + PlaceholderKeys.CURRENCY_NAME + " - Displays the name of the currency.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Displays the currency name in the plural.")
	private Component itemSell = deserialize("&aYou have successfully sold &r%item%&ax%size% for %currency-styled-symbol%%added%. Your balance %currency-styled-symbol%%balance%.");
	@Setting("ItemBuy")
	@Comment("You can use the following placeholders to display the currency type:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Displays the currency symbol.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Displays the currency symbol using the design from its name.\n" + PlaceholderKeys.CURRENCY_NAME + " - Displays the name of the currency.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Displays the currency name in the plural.")
	private Component itemBuy = deserialize("&aYou have successfully purchased &r%item%&ax%size% for %currency-styled-symbol%%removed%. Your balance %currency-styled-symbol%%balance%.");
	@Setting("BuyCommands")
	@Comment("You can use the following placeholders to display the currency type:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Displays the currency symbol.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Displays the currency symbol using the design from its name.\n" + PlaceholderKeys.CURRENCY_NAME + " - Displays the name of the currency.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Displays the currency name in the plural.")
	private Component buyCommands = deserialize("&aYou paid %currency-styled-symbol%%removed% to execute console commands. Your balance %currency-styled-symbol%%balance%.");
	private Component shopNotExists = deserialize("&cThere is no shop with id = " + PlaceholderKeys.SHOP);
	public ImplementShop() {}

	@Override
	public Component itemSell(ItemStack itemStack, Currency currency, double added, double balance) {
		return Text.of(itemSell).replace(PlaceholderKeys.ITEM, itemStack.asComponent().hoverEvent(HoverEvent.showItem(Key.key(ItemTypes.registry().valueKey(itemStack.type()).asString()), itemStack.quantity()))).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.SIZE, itemStack.quantity()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).replace(PlaceholderKeys.ADDED, added).replace(PlaceholderKeys.BALANCE, balance).get();
	}

	@Override
	public Component itemBuy(ItemStack itemStack, Currency currency, double removed, double balance) {
		return Text.of(itemBuy).replace(PlaceholderKeys.ITEM, itemStack.asComponent().hoverEvent(HoverEvent.showItem(Key.key(ItemTypes.registry().valueKey(itemStack.type()).asString()), itemStack.quantity()))).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.SIZE, itemStack.quantity()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).replace(PlaceholderKeys.REMOVED, removed).replace(PlaceholderKeys.BALANCE, balance).get();
	}

	@Override
	public Component buyCommands(Currency currency, double removed, double balance) {
		return Text.of(buyCommands).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).replace(PlaceholderKeys.REMOVED, removed).replace(PlaceholderKeys.BALANCE, balance).get();
	}

	@Override
	public Component shopNotExists(String shop) {
		return replace(shopNotExists, PlaceholderKeys.SHOP, shop);
	}

}
