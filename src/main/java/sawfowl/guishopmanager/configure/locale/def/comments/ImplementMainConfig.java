package sawfowl.guishopmanager.configure.locale.def.comments;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.configure.locale.abstractlocale.comments.MainConfig;

@ConfigSerializable
public class ImplementMainConfig implements MainConfig {

	@Setting("Auction")
	private ImplementAuction auction = new ImplementAuction();
	@Setting("MySql")
	private ImplementMySql mySql = new ImplementMySql();
	@Setting("Aliases")
	private String aliases = "Aliases for commands \"/gsm shop open\" and \"/gsm auction\".";
	@Setting("ConfigType")
	private String configType = "Configuration type when saving plugin data in files.\nAvailable variants: `.conf`, `.json`, `.yml`.";
	@Setting("SplitStorage")
	private String splitStorage = "Data storage methods.\nAvailable variants: File, H2, MySql.";
	@Setting("StorageFolders")
	private String storageFolders = "Catalog names to store shop data if no database is used.";
	@Setting("DebugEconomy")
	private String debugEconomy = "If true, the console will display messages about success or failure of transaction execution by the plugin in the economy.";
	@Setting("PlayerTransactionMessage")
	private String playerTransactionMessage = "Message to players on successful purchase/sale.";
	public ImplementMainConfig() {}

	@Override
	public Auction auction() {
		return auction;
	}

	@Override
	public MySql mySql() {
		return mySql;
	}

	@Override
	public String aliases() {
		return null;
	}

	@Override
	public String configType() {
		return configType;
	}

	@Override
	public String splitStorage() {
		return splitStorage;
	}

	@Override
	public String storageFolders() {
		return storageFolders;
	}

	@Override
	public String debugEconomy() {
		return debugEconomy;
	}

	@Override
	public String playerTransactionMessage() {
		return playerTransactionMessage;
	}

}
