package sawfowl.guishopmanager.configure.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.storage.DBStorage;
import sawfowl.localeapi.api.ConfigTypes;

@ConfigSerializable
public class ConfigType {

	@Setting("Auction")
	private String auction = ConfigTypes.HOCON.toString();
	@Setting("Shops")
	private String shops = ConfigTypes.HOCON.toString();
	@Setting("CommandsShops")
	private String commandsShops = ConfigTypes.HOCON.toString();
	@Setting("SqlFormat")
	private String sqlFormat = DBStorage.Format.JSON.name();
	public ConfigType() {}

	public String getAuction() {
		return auction;
	}

	public String getShops() {
		return shops;
	}

	public String getCommandsShops() {
		return commandsShops;
	}

	public String getSqlFormat() {
		return sqlFormat;
	}

}
