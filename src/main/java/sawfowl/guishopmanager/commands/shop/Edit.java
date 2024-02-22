package sawfowl.guishopmanager.commands.shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.pagination.PaginationList;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;

import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.AbstractPlayerCommand;
import sawfowl.guishopmanager.utils.CommandParameters;
import sawfowl.guishopmanager.data.shop.Shop;

public class Edit extends AbstractPlayerCommand {

	public Edit(GuiShopManager instance) {
		super(instance);
	}

	@Override
	public void execute(CommandContext context, ServerPlayer player, Locale locale) throws CommandException {
		if(!plugin.shopsEmpty()) {
			if(!context.one(CommandParameters.SHOP_ID).isPresent() || !plugin.shopExists(context.one(CommandParameters.SHOP_ID).get())) {
				List<Component> messages = new ArrayList<Component>();
				for(Shop shop : plugin.getAllShops()) {
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
			} else {
				run(player, context.one(CommandParameters.SHOP_ID).get());
			}
		} else exception(player, "Messages", "ShopListEmptyEditor");
	}

	@Override
	public Parameterized build() {
		return fastBuild();
	}

	@Override
	public String command() {
		return "edit";
	}

	@Override
	public String permission() {
		return Permissions.SHOP_EDIT;
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return Arrays.asList(ParameterSettings.of(CommandParameters.SHOP_ID, false, "Messages", "ShopIDNotPresent"));
	}

	private void run(ServerPlayer player, String shopID) {
		plugin.getShopMenu().createInventoryToEditor(plugin.getShop(shopID).getShopMenuData(1), (ServerPlayer) player, shopID, 1);
	}

}
