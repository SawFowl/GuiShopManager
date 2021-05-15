package sawfowl.guishopmanager;

public class Permissions {

	public static final String reload = "guishopmanager.reload";
	public static final String create = "guishopmanager.create";
	public static final String delete = "guishopmanager.delete";
	public static final String edit = "guishopmanager.edit";
	public static final String openself = "guishopmanager.open.self";
	public static final String openother = "guishopmanager.open";
	public static final String auctionadditem = "guishopmanager.auction.add";
	public static final String auctionopenother = "guishopmanager.auction.open";
	public static final String auctionitemblocking = "guishopmanager.auction.itemblocking";
	public static final String auctionopenself = "guishopmanager.auction.open.self";
	public static final String auctioncurrencyes = "guishopmanager.auction.currency";

	public static String currencyPermission(String currency) {
		return auctioncurrencyes + "." + currency;
	}

}
