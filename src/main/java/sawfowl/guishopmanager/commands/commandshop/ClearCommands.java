package sawfowl.guishopmanager.commands.commandshop;

import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.AbstractPlayerCommand;
import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStack;

public class ClearCommands extends AbstractPlayerCommand {

	public ClearCommands(GuiShopManager instance) {
		super(instance);
	}

	@Override
	public void execute(CommandContext context, ServerPlayer player, Locale locale) throws CommandException {
		ItemStack itemStack = null;
		boolean main = true;
		if(!player.itemInHand(HandTypes.MAIN_HAND).isEmpty()) {
			itemStack = player.itemInHand(HandTypes.MAIN_HAND);
		} else if(!player.itemInHand(HandTypes.OFF_HAND).isEmpty()) {
			itemStack = player.itemInHand(HandTypes.OFF_HAND);
			main = false;
		}
		if(itemStack != null && !itemStack.type().equals(ItemTypes.AIR.get())) {
			SerializedItemStack shopStack = new SerializedItemStack(itemStack);
			shopStack.getOrCreateComponent().removeComponent(getContainer(), "Commands");
			if(main) {
				player.setItemInHand(HandTypes.MAIN_HAND, shopStack.getItemStack());
			} else player.setItemInHand(HandTypes.OFF_HAND, shopStack.getItemStack());
			player.sendMessage(getCommands(locale).commandShop().commandsRemoved());
		} else exception(getExceptions(locale).itemNotPresent());
	}

	@Override
	public Parameterized build() {
		return fastBuild();
	}

	@Override
	public String command() {
		return "clearcommands";
	}

	@Override
	public String permission() {
		return Permissions.COMMANDSSHOP_EDIT;
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return null;
	}

}
