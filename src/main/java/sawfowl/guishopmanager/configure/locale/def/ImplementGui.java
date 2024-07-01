package sawfowl.guishopmanager.configure.locale.def;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.configure.locale.abstractlocale.Gui;
import sawfowl.guishopmanager.configure.locale.def.gui.*;

@ConfigSerializable
public class ImplementGui implements Gui {

	@Setting("Auction")
	private ImplementAuction auction = new ImplementAuction();
	@Setting("Shop")
	private ImplementShop shop = new ImplementShop();
	@Setting("CommandShop")
	private ImplementCommandShop commandShop = new ImplementCommandShop();
	public ImplementGui() {}

	@Override
	public Auction auction() {
		return auction;
	}

	@Override
	public Shop shop() {
		return shop;
	}

	@Override
	public CommandShop commandShop() {
		return commandShop;
	}

}
