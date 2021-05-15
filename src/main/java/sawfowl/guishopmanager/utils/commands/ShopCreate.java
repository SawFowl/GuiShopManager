package sawfowl.guishopmanager.utils.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.data.shop.Shop;
import sawfowl.guishopmanager.utils.data.shop.ShopMenuData;

public class ShopCreate implements CommandExecutor {

	GuiShopManager plugin;
	public ShopCreate(GuiShopManager instance) {
		plugin = instance;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)) {
			src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "OnlyPlayer"));
		} else {
			if(args.<String>getOne(Text.of("Shop")).isPresent()) {
				if(plugin.shopExists(args.<String>getOne(Text.of("Shop")).get())) {
					src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "ShopIDAlreadyExists"));
				} else {
					String defaultName = TextSerializers.FORMATTING_CODE.stripCodes(args.<String>getOne(Text.of("Shop")).get());
					Shop shop = new Shop(Text.of(defaultName));
					shop.addMenu(1, new ShopMenuData());
					plugin.addShop(defaultName, shop);
					plugin.getShopMenu().createInventoryToEditor(plugin.getShop(defaultName).getShopMenuData(1), (Player) src, defaultName, 1);
				}
			} else {
				src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "ShopIDNotPresent"));
			}
		}
		return CommandResult.success();
	}

}
