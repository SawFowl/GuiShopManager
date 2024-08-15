package sawfowl.guishopmanager.configure.locale.abstractlocale.comments;

public interface MainConfig {

	interface Auction {

		String componentLimit();

		String server();

		String expireTime();

		String tax();

		String fee();

	}

	interface MySql {

		String title();

		String syncInterval();

	}

	Auction auction();

	MySql mySql();

	String aliases();

	String configType();

	String splitStorage();

	String storageFolders();

	String debugEconomy();

	String playerTransactionMessage();

}
