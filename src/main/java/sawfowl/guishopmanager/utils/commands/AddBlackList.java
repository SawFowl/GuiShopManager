package sawfowl.guishopmanager.utils.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import sawfowl.guishopmanager.GuiShopManager;

public class AddBlackList implements CommandExecutor {

	GuiShopManager plugin;
	public AddBlackList(GuiShopManager instance) {
		plugin = instance;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)) {
			src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "OnlyPlayer"));
		} else {
			Player player = (Player) src;
			ItemStack itemStack = null;
			if(player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
				itemStack = player.getItemInHand(HandTypes.MAIN_HAND).get();
			} else if(player.getItemInHand(HandTypes.OFF_HAND).isPresent()) {
				itemStack = player.getItemInHand(HandTypes.OFF_HAND).get();
			}
			if(itemStack == null || itemStack.getType() == ItemTypes.AIR) {
				player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ItemNotPresent"));
			} else {
				boolean save = args.hasFlag("mask") || args.hasFlag("m") || args.hasFlag("item") || args.hasFlag("i");
				ItemStack toAdd = itemStack.copy();
				toAdd.setQuantity(1);
				if(args.hasFlag("mask") || args.hasFlag("m")) {
					if(!plugin.maskIsBlackList(toAdd.getType().getId())) {
						plugin.addBlackListMask(toAdd);
						player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "AddedToBlackList"));
					} else {
						player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ItemIsAlreadyBlocked"));
					}
				} else if(args.hasFlag("item") || args.hasFlag("i")) {
					if(!plugin.itemIsBlackList(toAdd)) {
						plugin.addBlackListStack(toAdd);
						player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "AddedToBlackList"));
					} else {
						player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ItemIsAlreadyBlocked"));
					}
				} else {
					player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ItemNotPresent"));
				}
				if(save) {
					plugin.updateConfigs();
				}
			}
		}
		return CommandResult.success();
	}

}
