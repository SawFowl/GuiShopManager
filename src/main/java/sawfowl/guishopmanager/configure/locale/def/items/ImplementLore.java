package sawfowl.guishopmanager.configure.locale.def.items;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.configure.locale.abstractlocale.Items.Lore;

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
	private Component currency = deserialize("&eCurrent currency: &a%currency%");
	@Setting("Size")
	private Component size = deserialize("&eSelected size: &a%size%");
	@Setting("Sum")
	private Component sum = deserialize("&eTotal: &a%size%");
	@Setting("Price")
	private Component price = deserialize("&eCurrency: &a%currency%&e. Buy: &a%buyprice%&e. Sell: &a%sellprice%");
	@Setting("CommandPrice")
	private Component commandPrice = deserialize("&eCurrency: &a%currency%&e. Buy: &a%buyprice%&e.");
	@Setting("AuctionPrice")
	private Component auctionPrice = deserialize("&eCurrency: &a%currency%&e. Price for one: &a%price%&e. Total: &a%total%");
	@Setting("AuctionBet")
	private Component auctionBet = deserialize("&eCurrency: &a%currency%&e. Bet for one: &a%price%&e. Total: &a%total%");
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
		return null;
	}

	@Override
	public Component size(int size) {
		return null;
	}

	@Override
	public Component sum(int size) {
		return null;
	}

	@Override
	public Component price(Currency currency, double buy, double sell) {
		return null;
	}

	@Override
	public Component commandPrice(Currency currency, double price) {
		return null;
	}

	@Override
	public Component auctionPrice(Currency currency, double price, double total) {
		return null;
	}

	@Override
	public Component auctionBet(Currency currency, double price, double total) {
		return null;
	}

	@Override
	public Component yourBet(double size, double total) {
		return null;
	}

	@Override
	public Component tax(double size) {
		return null;
	}

	@Override
	public Component fee(double size) {
		return null;
	}

	@Override
	public Component allowFree() {
		return null;
	}

	@Override
	public Component switchFree() {
		return null;
	}

	@Override
	public Component seller(String player) {
		return null;
	}

	@Override
	public Component expired(Component expired) {
		return null;
	}

	@Override
	public Component currentBuyer(String player) {
		return null;
	}

	@Override
	public Component currentBet(double value) {
		return null;
	}

	@Override
	public Component betClick() {
		return null;
	}

	@Override
	public Component buyClick() {
		return null;
	}

}
