package sawfowl.guishopmanager.commands;

import java.util.stream.Collectors;

import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.locale.LocaleSource;

import net.kyori.adventure.audience.Audience;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.serialization.commandsshop.SerializedCommandsList;
import sawfowl.guishopmanager.utils.CommandParameters;
import sawfowl.localeapi.serializetools.CompoundTag;
import sawfowl.localeapi.serializetools.SerializedItemStack;

public class AddCommand implements CommandExecutor {

	GuiShopManager plugin;
	public AddCommand(GuiShopManager instance) {
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
			boolean main = true;
			if(!player.itemInHand(HandTypes.MAIN_HAND).isEmpty()) {
				itemStack = player.itemInHand(HandTypes.MAIN_HAND);
			} else if(!player.itemInHand(HandTypes.OFF_HAND).isEmpty()) {
				itemStack = player.itemInHand(HandTypes.OFF_HAND);
				main = false;
			}
			if(itemStack == null || itemStack.type() == ItemTypes.AIR) {
				player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ItemNotPresent"));
			} else {
				if(context.one(CommandParameters.COMMAND).isPresent()) {
					String command = context.all(CommandParameters.COMMAND).stream().map(Object::toString).collect(Collectors.joining(", "));
					SerializedItemStack serializedItemStack = new SerializedItemStack(itemStack);
					SerializedCommandsList serializedCommandsList = serializedItemStack.getOrCreateTag().containsTag("guishopmanager") ? (SerializedCommandsList) serializedItemStack.getOrCreateTag().getTag("guishopmanager", CompoundTag.getClass(SerializedCommandsList.class)).get() : new SerializedCommandsList();
					serializedCommandsList.addCommand(command);
					serializedItemStack.getOrCreateTag().putTag("guishopmanager", serializedCommandsList);
					itemStack.copyFrom(serializedItemStack.getItemStack());
					if(main) {
						player.setItemInHand(HandTypes.MAIN_HAND, serializedItemStack.getItemStack());
					} else player.setItemInHand(HandTypes.OFF_HAND, serializedItemStack.getItemStack());
				}
			}
		}
		return CommandResult.success();
	}

}
