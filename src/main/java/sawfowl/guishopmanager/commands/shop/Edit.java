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
			Shop shop = context.one(CommandParameters.SHOP).orElse(null);
			if(shop == null) {
				List<Component> messages = new ArrayList<Component>();
				for(Shop shop1 : plugin.getAllShops()) {
					final ServerPlayer fPlayer = player;
					Component hover = plugin.getLocales().getComponent(player.locale(), "Hover", "OpenShopEdit");
					Component message = shop1.getOrDefaultTitle(player.locale()).clickEvent(SpongeComponents.executeCallback(cause -> {
						run(fPlayer, shop);
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
				run(player, shop);
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
		return Arrays.asList(ParameterSettings.of(CommandParameters.SHOP, false, "Messages", "ShopIDNotPresent"));
	}

	private void run(ServerPlayer player, Shop shop) {
		plugin.getShopMenu().createInventoryToEditor(shop.getShopMenuData(1), (ServerPlayer) player, shop.getID(), 1);
	}

}
