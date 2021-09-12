package sawfowl.guishopmanager.commands;

import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.util.locale.LocaleSource;

import net.kyori.adventure.audience.Audience;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.utils.CommandParameters;

public class AuctionOpen implements CommandExecutor {

	GuiShopManager plugin;
	public AuctionOpen(GuiShopManager instance) {
		plugin = instance;
	}

	@Override
	public CommandResult execute(CommandContext context) throws CommandException {
		Object src = context.cause().root();
		ServerPlayer player = null;
		if(!(src instanceof ServerPlayer)) {
			if(!context.one(CommandParameters.PLAYER).isPresent()) {
				((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "PlayerIsNotPresent"));
				return CommandResult.success();
			} else {
				player = context.one(CommandParameters.PLAYER).get();
			}
		} else {
			if(!context.one(CommandParameters.PLAYER).isPresent()) {
				player = (ServerPlayer) src;
			} else {
				if(!((ServerPlayer) src).hasPermission(Permissions.AUCTION_OPEN_OTHER) && !((ServerPlayer) src).uniqueId().equals(context.one(CommandParameters.PLAYER).get().uniqueId())) {
					((ServerPlayer) src).sendMessage(plugin.getLocales().getComponent(((ServerPlayer) src).locale(), "Messages", "DontOpenOther"));
					return CommandResult.success();
				} else {
					player = context.one(CommandParameters.PLAYER).get();
				}
			}
		}
		plugin.getAuctionMenus().createInventory(player, 1, plugin.getAuctionItems().values().stream().collect(Collectors.toList()));
		return CommandResult.success();
	}

}
