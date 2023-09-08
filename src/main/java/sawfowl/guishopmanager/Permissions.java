package sawfowl.guishopmanager;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.economy.Currency;

import net.kyori.adventure.text.Component;
import sawfowl.localeapi.api.TextUtils;

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

	public static boolean auctionCurrencyPermission(ServerPlayer player, Currency currency, boolean buy) {
		return player.hasPermission("guishopmanager.currency." + TextUtils.clearDecorations(currency.displayName())) || player.hasPermission(auctionCurrencyPermission(currency.symbol(), buy)) || player.hasPermission(auctionCurrencyPermission(currency.displayName(), buy)) || player.hasPermission(auctionCurrencyPermission(currency.pluralDisplayName(), buy));
	}

	public static String auctionCurrencyPermission(Component currency, boolean buy) {
		return "guishopmanager.currency." + TextUtils.clearDecorations(currency) + (buy ? ".auction.buy" : ".auction.sell");
	}

	public static boolean commandShopCurrencyPermission(ServerPlayer player, String shopId, Currency currency) {
		return player.hasPermission("guishopmanager.currency." + TextUtils.clearDecorations(currency.displayName())) || player.hasPermission(commandShopCurrencyPermission(shopId, currency.symbol())) || player.hasPermission(commandShopCurrencyPermission(shopId, currency.displayName())) || player.hasPermission(commandShopCurrencyPermission(shopId, currency.pluralDisplayName()));
	}

	public static String commandShopCurrencyPermission(String shopId, Component currency) {
		return "guishopmanager.currency."  + TextUtils.clearDecorations(currency) + ".commandshop." + shopId;
	}

	public static boolean shopCurrencyPermission(ServerPlayer player, String shopId, Currency currency, boolean buy) {
		return player.hasPermission("guishopmanager.currency." + TextUtils.clearDecorations(currency.displayName())) || player.hasPermission(shopCurrencyPermission(shopId, currency.symbol(), buy)) || player.hasPermission(shopCurrencyPermission(shopId, currency.displayName(), buy)) || player.hasPermission(shopCurrencyPermission(shopId, currency.pluralDisplayName(), buy));
	}

	public static String shopCurrencyPermission(String shopId, Component currency, boolean buy) {
		return "guishopmanager.currency." + TextUtils.clearDecorations(currency) + (buy ? ".shop.buy." : ".shop.sell.") + shopId;
	}

}
