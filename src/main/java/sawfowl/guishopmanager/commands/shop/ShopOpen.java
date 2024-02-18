package sawfowl.guishopmanager.commands.shop;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.util.locale.LocaleSource;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.utils.CommandParameters;
import sawfowl.guishopmanager.data.shop.Shop;

public class ShopOpen implements CommandExecutor {

	GuiShopManager plugin;
	public ShopOpen(GuiShopManager instance) {
		plugin = instance;
	}

	@Override
	public CommandResult execute(CommandContext context) throws CommandException {
		Audience src = context.cause().audience();
		if(plugin.shopsEmpty()) {
			src.sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "ShopListEmpty"));
		} else {
			if(context.one(CommandParameters.SHOP_ID).isPresent()) {
				String shopID = context.one(CommandParameters.SHOP_ID).get();
				ServerPlayer srcPlayer = (src instanceof ServerPlayer) ? (ServerPlayer) src : null;
				if(!plugin.shopExists(shopID)) {
					src.sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "ShopIDNotExists"));
				} else {
					if(context.one(CommandParameters.PLAYER).isPresent()) {
						if(context.cause().hasPermission(Permissions.SHOP_OPEN_OTHER) || context.one(CommandParameters.PLAYER).get().uniqueId().equals((src instanceof ServerPlayer) ? ((ServerPlayer) src).uniqueId() : null)) {
							run(context.one(CommandParameters.PLAYER).get(), shopID);
						} else {
							src.sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "DontOpenOther"));
						}
					} else {
						if(srcPlayer != null) {
							run(srcPlayer, shopID);
						} else {
							src.sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "PlayerIsNotPresent"));
						}
					}
				}
			} else {
				List<Component> messages = new ArrayList<Component>();
				ServerPlayer srcPlayer = (src instanceof ServerPlayer) ? (ServerPlayer) src : null;
				ServerPlayer player = null;
				if(srcPlayer == null && !context.one(CommandParameters.PLAYER).isPresent()) {
					src.sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "PlayerIsNotPresent"));
					return CommandResult.success();
				} else if(srcPlayer != null) {
					if(!context.one(CommandParameters.PLAYER).isPresent()) {
						player = srcPlayer;
					} else {
						if(srcPlayer.uniqueId().equals(context.one(CommandParameters.PLAYER).get().uniqueId())) {
							player = srcPlayer;
						} else {
							if(!srcPlayer.hasPermission(Permissions.SHOP_OPEN_OTHER)) {
								srcPlayer.sendMessage(plugin.getLocales().getComponent(srcPlayer.locale(), "Messages", "DontOpenOther"));
							}
						}
					}
				} else if(srcPlayer == null && context.one(CommandParameters.PLAYER).isPresent()) {
					player = context.one(CommandParameters.PLAYER).get();
				}
				Component hover = plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Hover", "OpenShop");
				for(Shop shop : plugin.getAllShops()) {
					final ServerPlayer fPlayer = player;
					Component message = shop.getOrDefaultTitle(((LocaleSource) src).locale()).clickEvent(SpongeComponents.executeCallback(cause -> {
						run(fPlayer, shop.getID());
					})).hoverEvent(HoverEvent.showText(hover));
					messages.add(message);
				}
				PaginationList.builder()
				.title(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "ShopListTitle"))
				.padding(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "ShopListPadding"))
				.contents(messages)
				.linesPerPage(10)
				.sendTo(src);
			}
		}
		return CommandResult.success();
	}

	private void run(ServerPlayer player, String shopID) {
		if(player != null) plugin.getShopMenu().createInventoryToPlayer(plugin.getShop(shopID).getShopMenuData(1), player, shopID, 1);
	}

}