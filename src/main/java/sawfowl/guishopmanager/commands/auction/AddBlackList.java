package sawfowl.guishopmanager.commands.auction;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.registry.RegistryTypes;

import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.AbstractPlayerCommand;
import sawfowl.guishopmanager.utils.CommandParameters;

public class AddBlackList extends AbstractPlayerCommand {

	public AddBlackList(GuiShopManager instance) {
		super(instance);
	}

	@Override
	public void execute(CommandContext context, ServerPlayer player, Locale locale) throws CommandException {
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
			if(save) plugin.updateConfigs();
		}
	}

	@Override
	public Parameterized build() {
		return builder()
				.addFlags(CommandParameters.MASK, CommandParameters.ITEM)
				.executor(this)
				.build();
	}

	@Override
	public String command() {
		return "blacklist";
	}

	@Override
	public String permission() {
		return Permissions.AUCTION_BLOCK_ITEM;
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return new ArrayList<ParameterSettings>();
	}

}
