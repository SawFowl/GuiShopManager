package sawfowl.guishopmanager.configure.locale.ru.items;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.configure.locale.PlaceholderKeys;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Items.Lore;
import sawfowl.localeapi.api.Text;

@ConfigSerializable
public class ImplementLore implements Lore {

	@Setting("ChangePrice")
	private List<Component> changePrice = Arrays.asList(deserialize("&aЛевый клик увеличит цену"), deserialize("&aПравый клик уменьшит цену"));
	@Setting("ChangeSize")
	private List<Component> changeSize = Arrays.asList(deserialize("&aЛевый клик увеличит размер"), deserialize("&aПравый клик уменьшит размер"));
	@Setting("AuctionSwitchMode")
	private List<Component> auctionSwitchMode = Arrays.asList(deserialize("&eЛевый клик переключит тип цены"), deserialize("&eПравый клик переключит время и комиссии"));
	@Setting("TransactionVariants")
	private Component transactionVariants = deserialize("&eВарианты транзакций: ▼");
	@Setting("Currency")
	@Comment("Вы можете использовать следующие плейсхолдеры для отображения валюты:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Отображение символа валюты.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Отображение символа валюты с применением стиля из ее имени.\n" + PlaceholderKeys.CURRENCY_NAME + " - Отображение имени валюты.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Отображение имени валюты в множественном числе.")
	private Component currency = deserialize("&eТекущая валюта: &a%currency-name%");
	@Setting("Size")
	private Component size = deserialize("&eВыбранный размер: &a%size%");
	@Setting("Sum")
	private Component sum = deserialize("&eВсего: %currency-styled-symbol%%size%");
	@Setting("Price")
	@Comment("Вы можете использовать следующие плейсхолдеры для отображения валюты:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Отображение символа валюты.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Отображение символа валюты с применением стиля из ее имени.\n" + PlaceholderKeys.CURRENCY_NAME + " - Отображение имени валюты.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Отображение имени валюты в множественном числе.")
	private Component price = deserialize("&eВалюта: %currency-name%&e. Покупка: &a%buy%&e. Продажа: &a%sell%");
	@Setting("CommandPrice")
	@Comment("Вы можете использовать следующие плейсхолдеры для отображения валюты:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Отображение символа валюты.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Отображение символа валюты с применением стиля из ее имени.\n" + PlaceholderKeys.CURRENCY_NAME + " - Отображение имени валюты.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Отображение имени валюты в множественном числе.")
	private Component commandPrice = deserialize("&eВалюта: %currency-name%&e. Покупка: &a%buyprice%&e.");
	@Setting("AuctionPrice")
	@Comment("Вы можете использовать следующие плейсхолдеры для отображения валюты:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Отображение символа валюты.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Отображение символа валюты с применением стиля из ее имени.\n" + PlaceholderKeys.CURRENCY_NAME + " - Отображение имени валюты.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Отображение имени валюты в множественном числе.")
	private Component auctionPrice = deserialize("&eВалюта: %currency-name%&e. Цена за штуку: &a%price%&e. Всего: &a%total%");
	@Setting("AuctionBet")
	@Comment("Вы можете использовать следующие плейсхолдеры для отображения валюты:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Отображение символа валюты.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Отображение символа валюты с применением стиля из ее имени.\n" + PlaceholderKeys.CURRENCY_NAME + " - Отображение имени валюты.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Отображение имени валюты в множественном числе.")
	private Component auctionBet = deserialize("&eВалюта: %currency-name%&e. Ставка за штуку: &a%price%&e. Всего: &a%total%");
	@Setting("YourBet")
	@Comment("Вы можете использовать следующие плейсхолдеры для отображения валюты:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Отображение символа валюты.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Отображение символа валюты с применением стиля из ее имени.\n" + PlaceholderKeys.CURRENCY_NAME + " - Отображение имени валюты.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Отображение имени валюты в множественном числе.")
	private Component yourBet = deserialize("&eВаша ставка: %currency-styled-symbol%%size%. Всего: %currency-styled-symbol%%total%");
	@Setting("Tax")
	@Comment("Вы можете использовать следующие плейсхолдеры для отображения валюты:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Отображение символа валюты.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Отображение символа валюты с применением стиля из ее имени.\n" + PlaceholderKeys.CURRENCY_NAME + " - Отображение имени валюты.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Отображение имени валюты в множественном числе.")
	private Component tax = deserialize("&eНалог: %currency-styled-symbol%%size%");
	@Setting("Fee")
	@Comment("Вы можете использовать следующие плейсхолдеры для отображения валюты:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Отображение символа валюты.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Отображение символа валюты с применением стиля из ее имени.\n" + PlaceholderKeys.CURRENCY_NAME + " - Отображение имени валюты.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Отображение имени валюты в множественном числе.")
	private Component fee = deserialize("&eПошлина: %currency-styled-symbol%%size%");
	@Setting("AllowFree")
	private Component allowFree = deserialize("&eДоступно бесплатно");
	@Setting("SwitchFree")
	private Component switchFree = deserialize("&eВключить/выключить бесплатное использование");
	@Setting("Seller")
	private Component seller = deserialize("&eПродавец: &b%player%&e.");
	@Setting("Expired")
	private Component expired = deserialize("&eСнимется с продажи через: &a%expired%&e.");
	@Setting("CurrentBuyer")
	private Component currentBuyer = deserialize("&eТекущий покупатель по ставке: &b%player%&e.");
	@Setting("CurrentBet")
	@Comment("Вы можете использовать следующие плейсхолдеры для отображения валюты:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Отображение символа валюты.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Отображение символа валюты с применением стиля из ее имени.\n" + PlaceholderKeys.CURRENCY_NAME + " - Отображение имени валюты.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Отображение имени валюты в множественном числе.")
	private Component currentBet = deserialize("&eТекущая ставка: %currency-styled-symbol%%bet%&e.");
	@Setting("BetClick")
	private Component betClick = deserialize("&dЛевый клик &f- &dуказать вашу ставку");
	@Setting("BuyClick")
	private Component buyClick = deserialize("&dПравый клик &f- &dвыкуп предмета");
	public ImplementLore() {}

	@Override
	public List<Component> changePrice() {
		return changePrice;
	}

	@Override
	public List<Component> changeSize() {
		return changeSize;
	}

	@Override
	public List<Component> auctionSwitchMode() {
		return auctionSwitchMode;
	}

	@Override
	public Component transactionVariants() {
		return transactionVariants;
	}

	@Override
	public Component currency(Currency currency) {
		return Text.of(this.currency).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).get();
	}

	@Override
	public Component size(int size) {
		return replace(this.size, PlaceholderKeys.SIZE, size);
	}

	@Override
	public Component sum(Currency currency, double size) {
		return Text.of(this.sum).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).replace(PlaceholderKeys.SIZE, size).get();
	}

	@Override
	public Component price(Currency currency, double buy, double sell) {
		return Text.of(this.price).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).replace(PlaceholderKeys.BUY, buy).replace(PlaceholderKeys.SELL, sell).get();
	}

	@Override
	public Component commandPrice(Currency currency, double price) {
		return Text.of(this.commandPrice).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).replace(PlaceholderKeys.PRICE, price).get();
	}

	@Override
	public Component auctionPrice(Currency currency, double price, double total) {
		return Text.of(this.auctionPrice).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).replace(PlaceholderKeys.PRICE, price).replace(PlaceholderKeys.TOTAL, total).get();
	}

	@Override
	public Component auctionBet(Currency currency, double price, double total) {
		return Text.of(this.auctionBet).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).replace(PlaceholderKeys.PRICE, price).replace(PlaceholderKeys.TOTAL, total).get();
	}

	@Override
	public Component yourBet(Currency currency, double size, double total) {
		return Text.of(yourBet).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).replace(PlaceholderKeys.SIZE, size).replace(PlaceholderKeys.TOTAL, total).get();
	}

	@Override
	public Component tax(Currency currency, double size) {
		return Text.of(tax).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).replace(PlaceholderKeys.SIZE, size).get();
	}

	@Override
	public Component fee(Currency currency, double size) {
		return Text.of(fee).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).replace(PlaceholderKeys.SIZE, size).get();
	}

	@Override
	public Component allowFree() {
		return allowFree;
	}

	@Override
	public Component switchFree() {
		return switchFree;
	}

	@Override
	public Component seller(String player) {
		return replace(seller, PlaceholderKeys.PLAYER, player);
	}

	@Override
	public Component expired(Component expired) {
		return replace(this.expired, PlaceholderKeys.EXPIRED, expired);
	}

	@Override
	public Component currentBuyer(String player) {
		return replace(currentBuyer, PlaceholderKeys.PLAYER, player);
	}

	@Override
	public Component currentBet(Currency currency, double bet) {
		return Text.of(currentBet).replace(PlaceholderKeys.CURRENCY_SYMBOL, currency.symbol()).replace(PlaceholderKeys.CURRENCY_STYLED_SYMBOL, currency.symbol().color(currency.displayName().color()).style(currency.displayName().style())).replace(PlaceholderKeys.CURRENCY_NAME, currency.displayName()).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, currency.pluralDisplayName()).replace(PlaceholderKeys.BET, bet).get();
	}

	@Override
	public Component betClick() {
		return betClick;
	}

	@Override
	public Component buyClick() {
		return buyClick;
	}

}
