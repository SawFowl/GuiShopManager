package sawfowl.guishopmanager.configure.locale.def.items;

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
	private List<Component> changePrice = Arrays.asList(deserialize("&aLeft click will increase the price "), deserialize("&aRight click will decrease the price "));
	@Setting("ChangeSize")
	private List<Component> changeSize = Arrays.asList(deserialize("&aLeft click will increase the size"), deserialize("&aRight click will decrease the size"));
	@Setting("AuctionSwitchMode")
	private List<Component> auctionSwitchMode = Arrays.asList(deserialize("&eLeft click to switch the price type"), deserialize("&eRight click to switch time and commissions"));
	@Setting("TransactionVariants")
	private Component transactionVariants = deserialize("&eTransaction variants: ▼");
	@Setting("Currency")
	@Comment("You can use the following placeholders to display the currency type:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Displays the currency symbol.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Displays the currency symbol using the design from its name.\n" + PlaceholderKeys.CURRENCY_NAME + " - Displays the name of the currency.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Displays the currency name in the plural.")
	private Component currency = deserialize("&eCurrent currency: &a%currency-styled-symbol%");
	@Setting("Size")
	private Component size = deserialize("&eSelected size: &a%size%");
	@Setting("Sum")
	private Component sum = deserialize("&eTotal: &a%size%");
	@Setting("Price")
	@Comment("You can use the following placeholders to display the currency type:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Displays the currency symbol.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Displays the currency symbol using the design from its name.\n" + PlaceholderKeys.CURRENCY_NAME + " - Displays the name of the currency.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Displays the currency name in the plural.")
	private Component price = deserialize("&eCurrency: &a%currency-styled-symbol%&e. Buy: &a%buy%&e. Sell: &a%sell%");
	@Setting("CommandPrice")
	@Comment("You can use the following placeholders to display the currency type:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Displays the currency symbol.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Displays the currency symbol using the design from its name.\n" + PlaceholderKeys.CURRENCY_NAME + " - Displays the name of the currency.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Displays the currency name in the plural.")
	private Component commandPrice = deserialize("&eCurrency: &a%currency-styled-symbol%&e. Buy: &a%buyprice%&e.");
	@Setting("AuctionPrice")
	@Comment("You can use the following placeholders to display the currency type:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Displays the currency symbol.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Displays the currency symbol using the design from its name.\n" + PlaceholderKeys.CURRENCY_NAME + " - Displays the name of the currency.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Displays the currency name in the plural.")
	private Component auctionPrice = deserialize("&eCurrency: &a%currency-styled-symbol%&e. Price for one: &a%price%&e. Total: &a%total%");
	@Setting("AuctionBet")
	@Comment("You can use the following placeholders to display the currency type:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Displays the currency symbol.\n" + PlaceholderKeys.CURRENCY_STYLED_SYMBOL + " - Displays the currency symbol using the design from its name.\n" + PlaceholderKeys.CURRENCY_NAME + " - Displays the name of the currency.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Displays the currency name in the plural.")
	private Component auctionBet = deserialize("&eCurrency: &a%currency-styled-symbol%&e. Bet for one: &a%price%&e. Total: &a%total%");
	@Setting("YourBet")
	private Component yourBet = deserialize("&eYour bet: &a%size%. Total: &a%total%");
	@Setting("Tax")
	private Component tax = deserialize("&eTax: &a%size%");
	@Setting("Fee")
	private Component fee = deserialize("&eFee: &a%size%");
	@Setting("AllowFree")
	private Component allowFree = deserialize("&eAvailable for free");
	@Setting("SwitchFree")
	private Component switchFree = deserialize("&eEnable/disable free use");
	@Setting("Seller")
	private Component seller = deserialize("&eSeller: &b%player%&e.");
	@Setting("Expired")
	private Component expired = deserialize("&eExpired: &a%expired%&e.");
	@Setting("CurrentBuyer")
	private Component currentBuyer = deserialize("&eCurrent buyer at the bet: &b%player%&e.");
	@Setting("CurrentBet")
	private Component currentBet = deserialize("&eCurrent bet: &a%bet%&e.");
	@Setting("BetClick")
	private Component betClick = deserialize("&dLeft click &f- &dset your bet");
	@Setting("BuyClick")
	private Component buyClick = deserialize("&dRight click &f- &dbuy an item");
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
	public Component sum(int size) {
		return replace(this.sum, PlaceholderKeys.SIZE, size);
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
	public Component yourBet(double size, double total) {
		return Text.of(yourBet).replace(PlaceholderKeys.SIZE, size).replace(PlaceholderKeys.TOTAL, total).get();
	}

	@Override
	public Component tax(double size) {
		return replace(tax, PlaceholderKeys.SIZE, size);
	}

	@Override
	public Component fee(double size) {
		return replace(fee, PlaceholderKeys.SIZE, size);
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
	public Component currentBet(double bet) {
		return replace(currentBet, PlaceholderKeys.BET, bet);
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
