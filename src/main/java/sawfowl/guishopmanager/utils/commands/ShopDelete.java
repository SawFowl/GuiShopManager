package sawfowl.guishopmanager.utils.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import sawfowl.guishopmanager.GuiShopManager;

public class ShopDelete implements CommandExecutor {

	GuiShopManager plugin;
	public ShopDelete(GuiShopManager instance) {
		plugin = instance;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(args.<String>getOne(Text.of("Shop")).isPresent()) {
			String shopId = args.<String>getOne(Text.of("Shop")).get();
			if(plugin.shopExists(shopId)) {
				plugin.removeShop(shopId);
				src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "SuccessDelete"));
			} else {
				src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "ShopIDNotExists"));
			}
		} else {
			src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "ShopIDNotPresent"));
		}
		return CommandResult.success();
	}

}
