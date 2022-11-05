package sawfowl.guishopmanager;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Permissions {

	public static final String HELP = "guishopmanager.user.help";
	public static final String RELOAD = "guishopmanager.staff.reload";
	public static final String COMMANDSSHOP_CREATE = "guishopmanager.staff.commandshop.create";
	public static final String COMMANDSSHOP_ADD_COMMAND = "guishopmanager.staff.commandshop.addcommand";
	public static final String COMMANDSSHOP_DELETE = "guishopmanager.staff.commandshop.delete";
	public static final String COMMANDSSHOP_EDIT = "guishopmanager.staff.commandshop.edit";
	public static final String COMMANDSSHOP_TRANSLATE = "guishopmanager.staff.commandshop.translate";
	public static final String COMMANDSSHOP_OPEN_OTHER = "guishopmanager.staff.commandshop.open";
	public static final String COMMANDSSHOP_OPEN_SELF = "guishopmanager.user.commandshop.open";
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

	public static String currencyPermission(Component currency) {
		return currencyPermission(removeComponents(currency));
	}

	public static String currencyPermission(String string) {
		return "guishopmanager.currency." + string;
	}

	private static String removeComponents(Component component) {
		String toReturn = LegacyComponentSerializer.legacyAmpersand().serialize(component);
		while(toReturn.indexOf('&') != -1 && !toReturn.endsWith("&") && isStyleChar(toReturn.charAt(toReturn.indexOf("&") + 1))) toReturn = toReturn.replaceAll("&" + toReturn.charAt(toReturn.indexOf("&") + 1), "");
		return toReturn;
	}

	private static boolean isStyleChar(char ch) {
		return "0123456789abcdefklmnor".indexOf(ch) != -1;
	}

}
