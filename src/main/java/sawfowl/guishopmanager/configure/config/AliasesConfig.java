package sawfowl.guishopmanager.configure.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class AliasesConfig {

	@Setting("Shop")
	private Aliases shop = new Aliases();
	@Setting("Auction")
	private Aliases auction = Aliases.createAuction();
	public AliasesConfig() {}

	public Aliases getShop() {
		return shop;
	}

	public Aliases getAuction() {
		return auction;
	}

}
