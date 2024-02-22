package sawfowl.guishopmanager.commands.commandshop;

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
import sawfowl.guishopmanager.data.commandshop.CommandShopData;
import sawfowl.guishopmanager.utils.CommandParameters;

public class Edit extends AbstractPlayerCommand {

	public Edit(GuiShopManager instance) {
		super(instance);
	}

	@Override
	public void execute(CommandContext context, ServerPlayer player, Locale locale) throws CommandException {
		if(plugin.commandShopsEmpty()) exception(locale, "Messages", "ShopListEmptyEditor");
		if(context.one(CommandParameters.SHOP_ID).isPresent()) {
			if(plugin.commandShopExists(context.one(CommandParameters.SHOP_ID).get())) {
				run(player, context.one(CommandParameters.SHOP_ID).get());
			} else exception(locale, "Messages", "ShopIDNotExists");
		} else {
			List<Component> messages = new ArrayList<Component>();
			for(CommandShopData shop : plugin.getAllCommandShops()) {
				final ServerPlayer fPlayer = player;
				Component hover = getComponent(locale, "Hover", "OpenShopEdit");
				Component message = shop.getOrDefaultTitle(locale).clickEvent(SpongeComponents.executeCallback(cause -> {
					run(fPlayer, shop.getID());
				})).hoverEvent(HoverEvent.showText(hover));
				messages.add(message);
			}
			PaginationList.builder()
			.title(getComponent(locale, "Messages", "ShopListTitle"))
			.padding(getComponent(locale, "Messages", "ShopListPadding"))
			.contents(messages)
			.linesPerPage(10)
			.sendTo(player);
		}
	
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
		return Permissions.COMMANDSSHOP_EDIT;
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return Arrays.asList(ParameterSettings.of(CommandParameters.SHOP_ID, false, "Messages", "ShopIDNotPresent"));
	}

	private void run(ServerPlayer player, String shopID) {
		plugin.getCommandShopMenu().createInventoryToEditor(plugin.getCommandShopData(shopID).getCommandShopMenuData(1), (ServerPlayer) player, shopID, 1);
	}

}
