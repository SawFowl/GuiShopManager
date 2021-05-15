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
import sawfowl.guishopmanager.utils.data.shop.Shop;

public class ShopEdit implements CommandExecutor {

	GuiShopManager plugin;
	public ShopEdit(GuiShopManager instance) {
		plugin = instance;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(plugin.getAllShops().isEmpty()) {
			src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "ShopListEmptyEditor"));
		} else {
			if(!(src instanceof Player)) {
				src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "OnlyPlayer"));
			} else {
				Player player = (Player) src;
				List<Text> messages = new ArrayList<Text>();
				if(args.<String>getOne(Text.of("Shop")).isPresent()) {
					if(!plugin.shopExists(args.<String>getOne(Text.of("Shop")).get())) {
						src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "ShopIDNotExists"));
					} else {
						run(player, plugin.getShop(args.<String>getOne(Text.of("Shop")).get()));
					}
				} else {
					for(Shop shop : plugin.getAllShops()) {
						Text message = Text.builder().append(shop.getOrDefaultTitle(player.getLocale()))
							.onHover(TextActions.showText(plugin.getLocales().getLocalizedText(player.getLocale(), "Hover", "OpenShopEdit")))	
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
		}
		return CommandResult.success();
	}

	private void run(Player player, Shop shop) {
		plugin.getShopMenu().createInventoryToEditor(shop.getShopMenuData(1), (Player) player, shop.getDefaultName().toPlain(), 1);
	}

}
