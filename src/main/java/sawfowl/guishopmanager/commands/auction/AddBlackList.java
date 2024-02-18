package sawfowl.guishopmanager.commands.auction;

import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.util.locale.LocaleSource;

import net.kyori.adventure.audience.Audience;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.CommandParameters;

public class AddBlackList implements CommandExecutor {

	GuiShopManager plugin;
	public AddBlackList(GuiShopManager instance) {
		plugin = instance;
	}

	@Override
	public CommandResult execute(CommandContext context) throws CommandException {
		Object src = context.cause().root();
		if(!(src instanceof ServerPlayer)) {
			((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "OnlyPlayer"));
		} else {
			ServerPlayer player = (ServerPlayer) src;
			ItemStack itemStack = null;
			if(!player.itemInHand(HandTypes.MAIN_HAND).isEmpty()) {
				itemStack = player.itemInHand(HandTypes.MAIN_HAND);
			} else if(!player.itemInHand(HandTypes.OFF_HAND).isEmpty()) {
				itemStack = player.itemInHand(HandTypes.OFF_HAND);
			}
			if(itemStack == null || itemStack.type() == ItemTypes.AIR) {
				player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ItemNotPresent"));
			} else {
				boolean save = context.hasFlag(CommandParameters.MASK) || context.hasFlag(CommandParameters.ITEM);
				ItemStack toAdd = itemStack.copy();
				toAdd.setQuantity(1);
				if(context.hasFlag(CommandParameters.MASK)) {
					if(!plugin.maskIsBlackList(RegistryTypes.ITEM_TYPE.get().valueKey(toAdd.type()).asString())) {
						plugin.addBlackListMask(toAdd);
						player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "AddedToBlackList"));
					} else {
						player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ItemIsAlreadyBlocked"));
					}
				} else if(context.hasFlag(CommandParameters.ITEM)) {
					if(!plugin.itemIsBlackList(toAdd)) {
						plugin.addBlackListStack(toAdd);
						player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "AddedToBlackList"));
					} else {
						player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ItemIsAlreadyBlocked"));
					}
				} else {
					player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ItemNotPresent"));
				}
				if(save) {
					plugin.updateConfigs();
				}
			}
		}
		return CommandResult.success();
	}

}
