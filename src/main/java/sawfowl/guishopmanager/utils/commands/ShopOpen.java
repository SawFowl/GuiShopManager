package sawfowl.guishopmanager.utils.commands;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.utils.data.shop.Shop;

public class ShopOpen implements CommandExecutor {

	GuiShopManager plugin;
	public ShopOpen(GuiShopManager instance) {
		plugin = instance;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(plugin.getAllShops().isEmpty()) {
			src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "ShopListEmpty"));
		} else {
			Player player = null;
			if(!(src instanceof Player)) {
				if(!args.<Player>getOne(Text.of("Player")).isPresent()) {
					src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "PlayerIsNotPresent"));
				} else {
					player = args.<Player>getOne(Text.of("Player")).get();
				}
			} else {
				player = (Player) src;
			}
			if(args.<String>getOne(Text.of("Shop")).isPresent()) {
				if(!plugin.shopExists(args.<String>getOne(Text.of("Shop")).get())) {
					src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "ShopIDNotExists"));
				} else {
					if(args.<Player>getOne(Text.of("Player")).isPresent()) {
						if(src.hasPermission(Permissions.openother) || ((Player) src).getUniqueId().equals(args.<Player>getOne(Text.of("Player")).get().getUniqueId())) {
							player = args.<Player>getOne(Text.of("Player")).get();
							run(player, plugin.getShop(args.<String>getOne(Text.of("Shop")).get()));
						} else {
							src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "DontOpenOther"));
						}
					} else {
						run(player, plugin.getShop(args.<String>getOne(Text.of("Shop")).get()));
					}
				}
			} else {
				List<Text> messages = new ArrayList<Text>();
				for(Shop shop : plugin.getAllShops()) {
					Text message = Text.builder().append(shop.getOrDefaultTitle(player.getLocale()))
						.onHover(TextActions.showText(plugin.getLocales().getLocalizedText(player.getLocale(), "Hover", "OpenShop")))	
						.onClick(TextActions.executeCallback(callback -> {
							run((Player) src, shop);
						}))
						.build();
					messages.add(message);
				}
		        PaginationList.builder()
				.title(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ShopListTitle"))
				.padding(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ShopListPadding"))
				.contents(messages)
				.linesPerPage(10)
				.sendTo(player);
			}
		}
		return CommandResult.success();
	}

	private void run(Player player, Shop shop) {
		plugin.getShopMenu().createInventoryToPlayer(shop.getShopMenuData(1), player, shop.getDefaultName().toPlain(), 1);
	}

}
