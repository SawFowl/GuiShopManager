package sawfowl.guishopmanager.configure.locale.def.comments;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.configure.locale.abstractlocale.comments.MainConfig.Auction;

@ConfigSerializable
public class ImplementAuction implements Auction {

	@Setting("ComponentLimit")
	private String componentLimit = "Character limit in the components of an item.\nFor validation, all components are converted to a single `json` string.";
	@Setting("Server")
	private String server = "The name of the server on which the return of the item to the player will be available. Use different names on different servers.";
	@Setting("ExpireTime")
	private String expireTime = "Time until the item is removed from sale in minutes.";
	@Setting("Tax")
	private String tax = "Income tax.";
	@Setting("Fee")
	private String fee = "A fee charged to the player when putting an item up for auction.";
	public ImplementAuction() {}

	@Override
	public String componentLimit() {
		return componentLimit;
	}

	@Override
	public String server() {
		return server;
	}

	@Override
	public String expireTime() {
		return expireTime;
	}

	@Override
	public String tax() {
		return tax;
	}

	@Override
	public String fee() {
		return fee;
	}

}
