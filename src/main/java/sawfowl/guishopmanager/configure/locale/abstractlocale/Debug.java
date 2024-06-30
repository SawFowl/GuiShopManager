package sawfowl.guishopmanager.configure.locale.abstractlocale;

public interface Debug {

	String errorTakeMoney(String player);

	String errorGiveMoney(String player);

	String infoTakeMoney(String player, double removed, double balance);

	String infoGiveMoney(String player, double added, double balance);

}
