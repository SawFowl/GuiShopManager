package sawfowl.guishopmanager.configure.locale.def;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.configure.locale.abstractlocale.Messages;
import sawfowl.guishopmanager.configure.locale.def.messages.*;

@ConfigSerializable
public class ImplementMessages implements Messages {

	@Setting("Auction")
	private ImplementAuction auction = new ImplementAuction();
	@Setting("Exceptions")
	private ImplementExceptions exceptions = new ImplementExceptions();
	@Setting("ShopTransactions")
	private ImplementShopTransactions shopTransactions = new ImplementShopTransactions();
	public ImplementMessages() {}

	@Override
	public Auction auction() {
		return auction;
	}

	@Override
	public Exceptions exceptions() {
		return exceptions;
	}

	@Override
	public ShopTransactions shopTransactions() {
		return shopTransactions;
	}

}
