package sawfowl.guishopmanager.configure.locale.ru.messages;

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
	@Comment("Вы можете использовать следующие плейсхолдеры для отображения валюты:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Отображение символа валюты.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Отображение символа валюты с применением стиля из ее имени.\n" + PlaceholderKeys.CURRENCY_NAME + " - Отображение имени валюты.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Отображение имени валюты в множественном числе.")
	private Component itemSell = deserialize("&aВы продали &r%item%&ax%size% за %currency-styled-symbol%%added%. Ваш баланс %currency-styled-symbol%%balance%.");
	@Setting("ItemBuy")
	@Comment("Вы можете использовать следующие плейсхолдеры для отображения валюты:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Отображение символа валюты.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Отображение символа валюты с применением стиля из ее имени.\n" + PlaceholderKeys.CURRENCY_NAME + " - Отображение имени валюты.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Отображение имени валюты в множественном числе.")
	private Component itemBuy = deserialize("&aВы купили &r%item%&ax%size% за %currency-styled-symbol%%removed%. Ваш баланс %currency-styled-symbol%%balance%.");
	@Setting("BuyCommands")
	@Comment("Вы можете использовать следующие плейсхолдеры для отображения валюты:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Отображение символа валюты.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Отображение символа валюты с применением стиля из ее имени.\n" + PlaceholderKeys.CURRENCY_NAME + " - Отображение имени валюты.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Отображение имени валюты в множественном числе.")
	private Component buyCommands = deserialize("&aВы заплатили %currency-styled-symbol%%removed% за выполнение команд от имени консоли. Ваш баланс %currency-styled-symbol%%balance%.");
	@Setting("ShopNotExists")
	private Component shopNotExists = deserialize("&cНет магазина с идентификатором = " + PlaceholderKeys.SHOP);
	public ImplementShop() {}

	@Override
	public Component itemSell(ItemStack itemStack, Currency currency, double added, double balance) {
		return Text.of(itemSell).replace(PlaceholderKeys.ITEM, itemStack.asComponent().hoverEvent(HoverEvent.showItem(Key.key(ItemTypes.registry().valueKey(itemStack.type()).asString()), 1))).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.SIZE, itemStack.quantity()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).replace(PlaceholderKeys.ADDED, added).replace(PlaceholderKeys.BALANCE, balance).get();
	}

	@Override
	public Component itemBuy(ItemStack itemStack, Currency currency, double removed, double balance) {
		return Text.of(itemBuy).replace(PlaceholderKeys.ITEM, itemStack.asComponent().hoverEvent(HoverEvent.showItem(Key.key(ItemTypes.registry().valueKey(itemStack.type()).asString()), 1))).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.SIZE, itemStack.quantity()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).replace(PlaceholderKeys.REMOVED, removed).replace(PlaceholderKeys.BALANCE, balance).get();
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
