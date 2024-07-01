package sawfowl.guishopmanager.configure.locale.def.gui;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.configure.locale.abstractlocale.Gui.Shop;
import sawfowl.localeapi.api.TextUtils;

@ConfigSerializable
public class ImplementShop implements Shop {

	@Setting("EditBuy")
	private Component editBuy = TextUtils.deserialize("&2Setting of purchase item");
	@Setting("EditSell")
	private Component editSell = TextUtils.deserialize("&2Setting of sell item");
	@Setting("BuyTransaction")
	private Component buyTransaction = TextUtils.deserialize("&2Buy");
	@Setting("SellTransaction")
	private Component sellTransaction = TextUtils.deserialize("&2Sell");
	public ImplementShop() {}

	@Override
	public Component editBuy() {
		return editBuy;
	}

	@Override
	public Component editSell() {
		return editSell;
	}

	@Override
	public Component buyTransaction() {
		return buyTransaction;
	}

	@Override
	public Component sellTransaction() {
		return sellTransaction;
	}

}
