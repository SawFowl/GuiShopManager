package sawfowl.guishopmanager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Permissions {

	public static final String HELP = "guishopmanager.user.help";
	public static final String RELOAD = "guishopmanager.staff.reload";
	public static final String SHOP_CREATE = "guishopmanager.staff.shop.create";
	public static final String SHOP_DELETE = "guishopmanager.staff.shop.delete";
	public static final String SHOP_EDIT = "guishopmanager.staff.shop.edit";
	public static final String SHOP_TRANSLATE = "guishopmanager.staff.shop.translate";
	public static final String SHOP_OPEN_OTHER = "guishopmanager.staff.shop.open";
	public static final String SHOP_OPEN_SELF = "guishopmanager.user.shop.open";
	public static final String AUCTION_BLOCK_ITEM = "guishopmanager.staff.auction.blockitem";
	public static final String AUCTION_OPEN_OTHER = "guishopmanager.staff.auction.open";
	public static final String AUCTION_OPEN_SELF = "guishopmanager.user.auction.open";
	public static final String AUCTION_ADD_ITEM = "guishopmanager.user.auction.add";
	public static final String AUCTION_CURRENCIES = "guishopmanager.currency";

	public static String currencyPermission(Component currency) {
		currency.decorations().clear();
		return AUCTION_CURRENCIES + "." + LegacyComponentSerializer.legacyAmpersand().serialize(currency);
	}

	public static String currencyPermission(String string) {
		return AUCTION_CURRENCIES + "." + string;
	}

}
