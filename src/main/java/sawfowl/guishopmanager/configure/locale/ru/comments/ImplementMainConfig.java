package sawfowl.guishopmanager.configure.locale.ru.comments;

import java.util.stream.Stream;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.configure.locale.abstractlocale.comments.MainConfig;
import sawfowl.localeapi.api.ConfigTypes;

@ConfigSerializable
public class ImplementMainConfig implements MainConfig {

	@Setting("Auction")
	private ImplementAuction auction = new ImplementAuction();
	@Setting("MySql")
	private ImplementMySql mySql = new ImplementMySql();
	@Setting("Aliases")
	private String aliases = "Алиасы для команд \"/gsm shop open\" и \"/gsm auction\".";
	@Setting("SqlFormat")
	private String sqlFormat = "Тип конфигурации при сохранении данных плагина в БД.\nДоступные варианты: `" + String.join("`, `", Stream.of(ConfigTypes.values()).filter(t -> t != ConfigTypes.UNKNOWN).map(t -> t.getTypeName()).toArray(String[]::new)) + "'.";
	@Setting("SplitStorage")
	private String splitStorage = "Разделение хранилищ данных.\nДоступные варианты: File, H2, MySql.";
	@Setting("StorageFolders")
	private String storageFolders = "Имена каталогов для хранения данных магазина, если не используется база данных.";
	@Setting("DebugEconomy")
	private String debugEconomy = "Если значение равно true, то в консоли будут отображаться сообщения об успехе или неудаче выполнения транзакции плагином в экономике.";
	@Setting("PlayerTransactionMessage")
	private String playerTransactionMessage = "Сообщение игрокам об успешной покупке/продаже.";
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
	public String sqlFormat() {
		return sqlFormat;
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
