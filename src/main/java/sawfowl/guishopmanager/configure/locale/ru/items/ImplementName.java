package sawfowl.guishopmanager.configure.locale.ru.items;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.guishopmanager.configure.locale.PlaceholderKeys;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Items.Name;

@ConfigSerializable
public class ImplementName implements Name {

	@Setting("Back")
	private Component back = deserialize("&eНазад");
	@Setting("Next")
	private Component next = deserialize("&eДалее");
	@Setting("AddPage")
	private Component addPage = deserialize("&eДобавить страницу");
	@Setting("Buy")
	private Component buy = deserialize("&eКупить и выйти");
	@Setting("Sell")
	private Component sell = deserialize("&eПродать и выйти");
	@Setting("Exit")
	private Component exit = deserialize("&eСохранить и выйти");
	@Setting("BuyAndBack")
	private Component buyAndBack = deserialize("&eКупить и вернуться назад");
	@Setting("SellAndBack")
	private Component sellAndBack = deserialize("&eПродать и вернуться назад");
	@Setting("Size")
	private Component size = deserialize("&eРазмер: &a%size%");
	@Setting("Price")
	private Component price = deserialize("&eЦена: &a%price%");
	@Setting("Clear")
	private Component clear = deserialize("&eСброс настроек");
	@Setting("ChangeCurrency")
	private Component changeCurrency = deserialize("&eСменить валюту");
	@Setting("SwitchMode")
	private Component switchMode = deserialize("&eПереключить режим");
	@Setting("AuctionAddItem")
	private Component auctionAddItem = deserialize("&eВыставить предмет на продажу");
	@Setting("ReturnAuctionItem")
	private Component returnAuctionItem = deserialize("&eВернуть ваши предметы");
	public ImplementName() {}

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
