package sawfowl.guishopmanager.utils.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import sawfowl.guishopmanager.GuiShopManager;

public class AuctionOpen implements CommandExecutor {

	GuiShopManager plugin;
	public AuctionOpen(GuiShopManager instance) {
		plugin = instance;
	}
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
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
		plugin.getAuctionMenus().createInventory(player, 1);
		return CommandResult.success();
	}

}
