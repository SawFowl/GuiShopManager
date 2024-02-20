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

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;

import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.AbstractCommand;
import sawfowl.guishopmanager.data.commandshop.CommandShopData;
import sawfowl.guishopmanager.utils.CommandParameters;

public class CommandShop extends AbstractCommand {

	public CommandShop(GuiShopManager instance) {
		super(instance);
	}

	@Override
	public Parameterized build() {
		return builder()
				.addChild(new AddCommand(plugin).build(), "addcommand")
				.addChild(new CommandShopCreate(plugin).build(), "create")
				.addChild(new CommandShopDelete(plugin).build(), "delete")
				.addChild(new CommandShopEdit(plugin).build(), "edit")
				.addChild(new CommandShopTranslate(plugin).build(), "translate")
				.executor(this)
				.build();
	}

	@Override
	public void execute(CommandContext context, Audience src, Locale locale, boolean isPlayer) throws CommandException {
		if(plugin.commandShopsEmpty()) {
			src.sendMessage(getComponent(locale, "Messages", "ShopListEmpty"));
		} else {
			if(context.one(CommandParameters.SHOP_ID).isPresent()) {
				String shopID = context.one(CommandParameters.SHOP_ID).get();
				ServerPlayer srcPlayer = (src instanceof ServerPlayer) ? (ServerPlayer) src : null;
				if(!plugin.commandShopExists(shopID)) {
					src.sendMessage(getComponent(locale, "Messages", "ShopIDNotExists"));
				} else {
					if(context.one(CommandParameters.PLAYER).isPresent()) {
						if(context.cause().hasPermission(Permissions.COMMANDSSHOP_OPEN_OTHER) || context.one(CommandParameters.PLAYER).get().uniqueId().equals((src instanceof ServerPlayer) ? ((ServerPlayer) src).uniqueId() : null)) {
							run(context.one(CommandParameters.PLAYER).get(), shopID);
						} else exception(locale, "Messages", "DontOpenOther");
					} else {
						if(srcPlayer != null) {
							run(srcPlayer, shopID);
						} else exception(locale, "Messages", "PlayerIsNotPresent");
					}
				}
			} else {
				List<Component> messages = new ArrayList<Component>();
				ServerPlayer srcPlayer = (src instanceof ServerPlayer) ? (ServerPlayer) src : null;
				ServerPlayer player = null;
				if(srcPlayer == null && !context.one(CommandParameters.PLAYER).isPresent()) {
					exception(locale, "Messages", "PlayerIsNotPresent");
				} else if(srcPlayer != null) {
					if(!context.one(CommandParameters.PLAYER).isPresent()) {
						player = srcPlayer;
					} else {
						if(srcPlayer.uniqueId().equals(context.one(CommandParameters.PLAYER).get().uniqueId())) {
							player = srcPlayer;
						} else {
							if(!srcPlayer.hasPermission(Permissions.COMMANDSSHOP_OPEN_OTHER)) {
								srcPlayer.sendMessage(getComponent(srcPlayer.locale(), "Messages", "DontOpenOther"));
							}
						}
					}
				} else if(srcPlayer == null && context.one(CommandParameters.PLAYER).isPresent()) {
					player = context.one(CommandParameters.PLAYER).get();
				}
				Component hover = getComponent(locale, "Hover", "OpenShop");
				for(CommandShopData shop : plugin.getAllCommandShops()) {
					final ServerPlayer fPlayer = player;
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
	}

	@Override
	public String command() {
		return "commandshop";
	}

	@Override
	public Component getComponent(Object[] arg0) {
		return null;
	}

	@Override
	public String permission() {
		return Permissions.COMMANDSSHOP_OPEN_SELF;
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return Arrays.asList(
			ParameterSettings.of(CommandParameters.SHOP_ID, false, "Messages", "ShopIDNotExists"),
			ParameterSettings.of(CommandParameters.PLAYER, false, "Messages", "PlayerIsNotPresent")
		);
	}

	private void run(ServerPlayer player, String shopID) {
		if(player != null) plugin.getCommandShopMenu().createInventoryToPlayer(plugin.getCommandShopData(shopID).getCommandShopMenuData(1), player, shopID, 1);
	}

}
