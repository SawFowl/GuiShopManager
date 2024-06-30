package sawfowl.guishopmanager.commands.commandshop;

import java.util.ArrayList;
import java.util.Arrays;
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
import sawfowl.guishopmanager.commands.AbstractPlayerCommand;
import sawfowl.guishopmanager.serialization.commandsshop.CommandsList;
import sawfowl.guishopmanager.utils.CommandParameters;
import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStackJsonNbt;

public class AddCommand extends AbstractPlayerCommand {

	public AddCommand(GuiShopManager instance) {
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
			if(context.one(CommandParameters.COMMAND).isPresent()) {
				String command = context.one(CommandParameters.COMMAND).get();
				SerializedItemStackJsonNbt shopStack = new SerializedItemStackJsonNbt(itemStack);
				List<String> serializedCommandsList = shopStack.getOrCreateComponent().containsComponent(GuiShopManager.getInstance().getPluginContainer(), "Commands") ? shopStack.getOrCreateComponent().getObjectsList(String.class, GuiShopManager.getInstance().getPluginContainer(), "Commands", new ArrayList<>()) : new ArrayList<>();
				serializedCommandsList.add(command);
				shopStack.getOrCreateComponent().putObject(getContainer(), "Commands", serializedCommandsList);
				itemStack.copyFrom(shopStack.getItemStack());
				if(main) {
					player.setItemInHand(HandTypes.MAIN_HAND, shopStack.getItemStack());
				} else player.setItemInHand(HandTypes.OFF_HAND, shopStack.getItemStack());
			}
			player.sendMessage(getComponent(locale, "Messages", "CommandAdded"));
		} else exception(locale, "Messages", "ItemNotPresent");
	}

	@Override
	public Parameterized build() {
		return fastBuild();
	}

	@Override
	public String command() {
		return "addcommand";
	}

	@Override
	public String permission() {
		return null;
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return Arrays.asList(ParameterSettings.of(CommandParameters.COMMAND, false, new Object[] {}));
	}

}
