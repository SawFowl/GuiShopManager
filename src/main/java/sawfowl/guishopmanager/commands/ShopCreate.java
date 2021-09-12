package sawfowl.guishopmanager.commands;

import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.configurate.serialize.SerializationException;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.CommandParameters;
import sawfowl.guishopmanager.data.shop.Shop;
import sawfowl.guishopmanager.data.shop.ShopMenuData;

public class ShopCreate implements CommandExecutor  {

	GuiShopManager plugin;
	public ShopCreate(GuiShopManager instance) {
		plugin = instance;
	}

	@Override
	public CommandResult execute(CommandContext context) throws CommandException {
		Object src = context.cause().root();
		if(!(src instanceof ServerPlayer)) {
			((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "OnlyPlayer"));
		} else {
			ServerPlayer player = (ServerPlayer) src;
			if(context.one(CommandParameters.SHOP_ID).isPresent()) {
				String shopID = context.one(CommandParameters.SHOP_ID).get().toLowerCase();
				try {
					if(!plugin.getRootNode().node("Aliases", "ShopOpen", "List").empty() && plugin.getRootNode().node("Aliases", "ShopOpen", "List").getList(String.class).stream().map(String::toLowerCase).collect(Collectors.toList()).contains(shopID)) {
						player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "InvalidShopID"));
						return CommandResult.success();
					}
				} catch (SerializationException e) {
					plugin.getLogger().error(e.getLocalizedMessage());
				}
				if(plugin.shopExists(shopID)) {
					player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ShopIDAlreadyExists"));
				} else {
					Component defaultName = LegacyComponentSerializer.legacyAmpersand().deserialize(shopID);
					defaultName.decorations().clear();
					shopID = LegacyComponentSerializer.legacyAmpersand().serialize(defaultName);
					Shop shop = new Shop(defaultName);
					shop.addMenu(1, new ShopMenuData());
					plugin.addShop(shopID, shop);
					plugin.getShopMenu().createInventoryToEditor(plugin.getShop(shopID).getShopMenuData(1), player, shopID, 1);
				}
			} else {
				player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ShopIDNotPresent"));
			}
		}
		return CommandResult.success();
	}

}
