package sawfowl.guishopmanager.configure.locale.ru.gui;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.configure.locale.abstractlocale.Gui.Auction;
import sawfowl.localeapi.api.TextUtils;

@ConfigSerializable
public class ImplementAuction implements Auction {

	@Setting("Auction")
	private Component auction = TextUtils.deserializeLegacy("&2Аукцион");
	@Setting("Bet")
	private Component bet = TextUtils.deserializeLegacy("&2Ставка");
	@Setting("ReturnItems")
	private Component returnItems = TextUtils.deserializeLegacy("&2Возврат предметов");
	@Setting("Edit")
	private Component edit = TextUtils.deserializeLegacy("&2Настройка предмета");
	public ImplementAuction() {}

	@Override
	public Component auction() {
		return auction;
	}

	@Override
	public Component bet() {
		return bet;
	}

	@Override
	public Component returnItems() {
		return returnItems;
	}

	@Override
	public Component edit() {
		return edit;
	}

}
