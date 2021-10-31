package sawfowl.guishopmanager.utils.commands;

import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.objectmapping.ObjectMappingException;
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
					String defaultName = TextSerializers.FORMATTING_CODE.stripCodes(args.<String>getOne(Text.of("Shop")).get()).toLowerCase();
					try {
						if(plugin.getRootNode().getNode("Aliases", "ShopOpen", "List").getValue(new TypeToken<List<String>>() {
							private static final long serialVersionUID = 01;}).stream().map(String::toLowerCase).collect(Collectors.toList()).contains(defaultName) ||
								plugin.getRootNode().getNode("Aliases", "Auction", "List").getValue(new TypeToken<List<String>>() {
									private static final long serialVersionUID = 01;}).stream().map(String::toLowerCase).collect(Collectors.toList()).contains(defaultName)) {
							src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "InvalidShopID"));
							return CommandResult.success();
						}
					} catch (ObjectMappingException e) {
						plugin.getLogger().error(e.getLocalizedMessage());
					}
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
