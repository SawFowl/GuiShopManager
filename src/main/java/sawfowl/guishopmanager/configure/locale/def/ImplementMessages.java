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
	@Setting("Shop")
	private ImplementShop shopTransactions = new ImplementShop();
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
	public Shop shop() {
		return shopTransactions;
	}

}
