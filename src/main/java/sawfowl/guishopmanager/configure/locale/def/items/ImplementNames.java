package sawfowl.guishopmanager.configure.locale.def.items;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.guishopmanager.configure.locale.PlaceholderKeys;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Items.Names;

@ConfigSerializable
public class ImplementNames implements Names {

	@Setting("Back")
	private Component back = deserialize("&eBack");
	@Setting("Next")
	private Component next = deserialize("&eNext");
	@Setting("AddPage")
	private Component addPage = deserialize("&eAdd page");
	@Setting("Buy")
	private Component buy = deserialize("&eBuy and exit");
	@Setting("Sell")
	private Component sell = deserialize("&eSell and exit");
	@Setting("Exit")
	private Component exit = deserialize("&eSave and exit");
	@Setting("BuyAndBack")
	private Component buyAndBack = deserialize("&eBuy and go back");
	@Setting("SellAndBack")
	private Component sellAndBack = deserialize("&eSell and go back");
	@Setting("Size")
	private Component size = deserialize("&eSize: &a%size%");
	@Setting("Price")
	private Component price = deserialize("&ePrice: &a%price%");
	@Setting("Clear")
	private Component clear = deserialize("&eClear");
	@Setting("ChangeCurrency")
	private Component changeCurrency = deserialize("&eChange currency");
	@Setting("SwitchMode")
	private Component switchMode = deserialize("&eSwitch mode");
	@Setting("AuctionAddItem")
	private Component auctionAddItem = deserialize("&eSale your item");
	@Setting("ReturnAuctionItem")
	private Component returnAuctionItem = deserialize("&eReturn your items");
	public ImplementNames() {}

	@Override
	public Component back() {
		return back;
	}

	@Override
	public Component next() {
		return next;
	}

	@Override
	public Component addPage() {
		return addPage;
	}

	@Override
	public Component buy() {
		return buy;
	}

	@Override
	public Component sell() {
		return sell;
	}

	@Override
	public Component exit() {
		return exit;
	}

	@Override
	public Component buyAndBack() {
		return buyAndBack;
	}

	@Override
	public Component sellAndBack() {
		return sellAndBack;
	}

	@Override
	public Component size(Component size) {
		return replace(this.size, PlaceholderKeys.SIZE, size);
	}

	@Override
	public Component price(Component price) {
		return replace(this.price, PlaceholderKeys.PRICE, price);
	}

	@Override
	public Component clear() {
		return clear;
	}

	@Override
	public Component changeCurrency() {
		return changeCurrency;
	}

	@Override
	public Component switchMode() {
		return switchMode;
	}

	@Override
	public Component auctionAddItem() {
		return auctionAddItem;
	}

	@Override
	public Component returnAuctionItem() {
		return returnAuctionItem;
	}

}
