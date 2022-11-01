package sawfowl.guishopmanager.commands;

import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.locale.LocaleSource;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.data.commandshop.CommandShopData;
import sawfowl.guishopmanager.data.commandshop.CommandShopMenuData;
import sawfowl.guishopmanager.utils.CommandParameters;

public class CommandsShopCreate implements CommandExecutor {

	private final GuiShopManager plugin;
	public CommandsShopCreate(GuiShopManager plugin) {
		this.plugin = plugin;
	}
	@Override
	public CommandResult execute(CommandContext context) throws CommandException {
		Object src = context.cause().root();
		if(!(src instanceof ServerPlayer)) {
			((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "OnlyPlayer"));
		} else {
			ServerPlayer player = (ServerPlayer) src;
			if(context.one(CommandParameters.SHOP_ID).isPresent()) {
				String shopID = removeCodes(context.one(CommandParameters.SHOP_ID).get().toLowerCase());
				if(plugin.commandShopExists(shopID)) {
					player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ShopIDAlreadyExists"));
				} else {
					CommandShopData shop = new CommandShopData(Component.text(shopID));
					shop.addMenu(1, new CommandShopMenuData());
					plugin.addCommandShopData(shopID, shop);
					plugin.getCommandShopMenu().createInventoryToEditor(plugin.getCommandShopData(shopID).getCommandShopMenuData(1), player, shopID, 1);
				}
			} else {
				player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ShopIDNotPresent"));
			}
		}
		return CommandResult.success();
	}

	private String removeCodes(String string) {
		while(string.indexOf('&') != -1 && !string.endsWith("&") && isStyleChar(string.charAt(string.indexOf("&") + 1))) string = string.replaceAll("&" + string.charAt(string.indexOf("&") + 1), "");
		return string;
	}

	private boolean isStyleChar(char ch) {
		return "0123456789abcdefklmnor".indexOf(ch) != -1;
	}

}
