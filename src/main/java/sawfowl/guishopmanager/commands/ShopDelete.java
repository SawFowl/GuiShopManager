package sawfowl.guishopmanager.commands;

import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.util.locale.LocaleSource;

import net.kyori.adventure.audience.Audience;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.CommandParameters;

public class ShopDelete implements CommandExecutor {

	GuiShopManager plugin;
	public ShopDelete(GuiShopManager instance) {
		plugin = instance;
	}

	@Override
	public CommandResult execute(CommandContext context) throws CommandException {
		Object src = context.cause().root();
		if(context.one(CommandParameters.SHOP_ID).isPresent()) {
			String shopId = context.one(CommandParameters.SHOP_ID).get();
			if(plugin.shopExists(shopId)) {
				plugin.removeShop(shopId);
				((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "SuccessDelete"));
			} else {
				((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "ShopIDNotExists"));
			}
		} else {
			((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "ShopIDNotPresent"));
		}
		return CommandResult.success();
	}

}
