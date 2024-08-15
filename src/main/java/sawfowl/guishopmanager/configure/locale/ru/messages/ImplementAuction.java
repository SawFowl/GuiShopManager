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
import sawfowl.guishopmanager.configure.locale.abstractlocale.Messages.Auction;
import sawfowl.localeapi.api.Text;

@ConfigSerializable
public class ImplementAuction implements Auction {

	@Setting("MaxVolume")
	private Component maxVolume = deserialize("&cВы уже продаете максимальное количество товаров.");
	@Setting("Buy")
	@Comment("Вы можете использовать следующие плейсхолдеры для отображения валюты:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Отображение символа валюты.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Отображение символа валюты с применением стиля из ее имени.\n" + PlaceholderKeys.CURRENCY_NAME + " - Отображение имени валюты.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Отображение имени валюты в множественном числе.")
	private Component buy = deserialize("&aВы купили на аукционе &r%item%&ax%amount% за %currency-styled-symbol%%removed%. Ваш баланс %currency-styled-symbol%%balance%. Продавец &b%player%&a.");
	@Setting("Sell")
	@Comment("Вы можете использовать следующие плейсхолдеры для отображения валюты:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Отображение символа валюты.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Отображение символа валюты с применением стиля из ее имени.\n" + PlaceholderKeys.CURRENCY_NAME + " - Отображение имени валюты.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Отображение имени валюты в множественном числе.")
	private Component sell = deserialize("&aВы продали на аукционе &r%item%&ax%amount% за %currency-styled-symbol%%added%. Ваш баланс %currency-styled-symbol%%balance%. Покупатель &b%player%&a.");
	@Setting("Expired")
	private Component expired = deserialize("&aИстек срок выставления на продажу ваших предметов. Кликните на это сообщение, чтобы получить их обратно.");
	@Setting("BetExpired")
	private Component betExpired = deserialize("&aЗавершена сделка по выкупу предметов по ставке на аукционе. Нажмите на это сообщение, чтобы получить их.");
	@Setting("Tax")
	@Comment("Вы можете использовать следующие плейсхолдеры для отображения валюты:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Отображение символа валюты.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Отображение символа валюты с применением стиля из ее имени.\n" + PlaceholderKeys.CURRENCY_NAME + " - Отображение имени валюты.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Отображение имени валюты в множественном числе.")
	private Component tax = deserialize("&aУдержанный налог: %currency-styled-symbol%%amount%.");
	@Setting("NoEmptySlots")
	private Component noEmptySlots = deserialize("&cВ вашем инвентаре недостаточно свободных слотов. Освободите хотя бы %size%.");
	@Setting("ItemNotFound")
	private Component itemNotFound = deserialize("&cПредмет был куплен другим игроком или снят с продажи.");
	@Setting("CancelBuy")
	private Component cancelBuy = deserialize("&cВы не можете купить или назначить ставку на свой предмет.");
	@Setting("ItemAdded")
	private Component itemAdded = deserialize("&aВы выставили товар на продажу.");
	@Setting("BetIsNotSet")
	private Component betIsNotSet = deserialize("&eВы не назначили ставку за предмет.");
	@Setting("ItemBlocked")
	private Component itemBlocked = deserialize("&cЭтот предмет не может быть выставлен на продажу.");
	@Setting("LongComponents")
	private Component longComponents = deserialize("&cРазмер компонентов у предмета слишком велик.");
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
