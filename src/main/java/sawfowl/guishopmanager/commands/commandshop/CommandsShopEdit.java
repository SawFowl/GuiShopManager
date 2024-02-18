package sawfowl.guishopmanager.commands.commandshop;

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
import sawfowl.guishopmanager.data.commandshop.CommandShopData;
import sawfowl.guishopmanager.utils.CommandParameters;

public class CommandsShopEdit implements CommandExecutor {

	private final GuiShopManager plugin;
	public CommandsShopEdit(GuiShopManager plugin) {
		this.plugin = plugin;
	}

	@Override
	public CommandResult execute(CommandContext context) throws CommandException {
		Object src = context.cause().root();
		if(plugin.commandShopsEmpty()) {
			((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "ShopListEmptyEditor"));
		} else {
			if(!(src instanceof ServerPlayer)) {
				((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "OnlyPlayer"));
			} else {
				ServerPlayer player = (ServerPlayer) src;
				if(context.one(CommandParameters.SHOP_ID).isPresent()) {
					if(!plugin.commandShopExists(context.one(CommandParameters.SHOP_ID).get())) {
						((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "ShopIDNotExists"));
					} else {
						run(player, context.one(CommandParameters.SHOP_ID).get());
					}
				} else {
					List<Component> messages = new ArrayList<Component>();
					for(CommandShopData shop : plugin.getAllCommandShops()) {
						final ServerPlayer fPlayer = player;
						Component hover = plugin.getLocales().getComponent(player.locale(), "Hover", "OpenShopEdit");
						Component message = shop.getOrDefaultTitle(player.locale()).clickEvent(SpongeComponents.executeCallback(cause -> {
							run(fPlayer, shop.getID());
						})).hoverEvent(HoverEvent.showText(hover));
						messages.add(message);
					}
					PaginationList.builder()
					.title(plugin.getLocales().getComponent(player.locale(), "Messages", "ShopListTitle"))
					.padding(plugin.getLocales().getComponent(player.locale(), "Messages", "ShopListPadding"))
					.contents(messages)
					.linesPerPage(10)
					.sendTo(player);
				}
			}
		}
		return CommandResult.success();
	}

	private void run(ServerPlayer player, String shopID) {
		plugin.getCommandShopMenu().createInventoryToEditor(plugin.getCommandShopData(shopID).getCommandShopMenuData(1), (ServerPlayer) player, shopID, 1);
	}

}
