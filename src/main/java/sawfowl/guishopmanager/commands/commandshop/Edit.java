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
		CommandShopData shop = context.one(CommandParameters.COMMAND_SHOP).orElse(null);
		if(shop != null) {
			run(player, shop);
		} else {
			List<Component> messages = new ArrayList<Component>();
			for(CommandShopData shop1 : plugin.getAllCommandShops()) {
				String shopId = shop1.getID();
				final ServerPlayer fPlayer = player;
				Component hover = getCommands(locale).shop().openForEdit();
				Component message = shop1.getOrDefaultTitle(locale).clickEvent(SpongeComponents.executeCallback(cause -> {
					if(plugin.shopExists(shopId)) {
						run(fPlayer, shop1);
					} else cause.sendMessage(plugin.getLocales().getAsReferenced(getLocale(cause)).messages().shop().shopNotExists(shopId));
				})).hoverEvent(HoverEvent.showText(hover));
				messages.add(message);
			}
			PaginationList.builder()
			.title(getCommands(locale).commandShop().title())
			.padding(getCommands(locale).commandShop().padding())
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
		return Arrays.asList(ParameterSettings.of(CommandParameters.COMMAND_SHOP, false, locale -> getExceptions(locale).shopNotPresent()));
	}

	private void run(ServerPlayer player, CommandShopData shop) {
		plugin.getCommandShopMenu().createInventoryToEditor(shop.getCommandShopMenuData(1), (ServerPlayer) player, shop.getID(), 1);
	}

}
