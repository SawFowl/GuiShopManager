package sawfowl.guishopmanager.commands.commandshop;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import net.kyori.adventure.text.Component;

import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.AbstractPlayerCommand;
import sawfowl.guishopmanager.data.commandshop.CommandShopData;
import sawfowl.guishopmanager.data.commandshop.CommandShopMenuData;
import sawfowl.guishopmanager.utils.CommandParameters;
import sawfowl.localeapi.api.TextUtils;

public class CommandShopCreate extends AbstractPlayerCommand {
	
	public CommandShopCreate(GuiShopManager instance) {
		super(instance);
	}

	@Override
	public void execute(CommandContext context, ServerPlayer player, Locale locale) throws CommandException {
		String shopID = TextUtils.clearDecorations(context.one(CommandParameters.SHOP_ID).get().toLowerCase());
		if(plugin.commandShopExists(shopID)) {
			player.sendMessage(getComponent(locale, "Messages", "ShopIDAlreadyExists"));
		} else {
			CommandShopData shop = new CommandShopData(Component.text(shopID));
			shop.addMenu(1, new CommandShopMenuData());
			plugin.addCommandShopData(shopID, shop);
			plugin.getCommandShopMenu().createInventoryToEditor(plugin.getCommandShopData(shopID).getCommandShopMenuData(1), player, shopID, 1);
		}
	}

	@Override
	public Parameterized build() {
		return fastBuild();
	}

	@Override
	public String command() {
		return "create";
	}

	@Override
	public Component getComponent(Object[] arg0) {
		return null;
	}

	@Override
	public String permission() {
		return Permissions.COMMANDSSHOP_CREATE;
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return Arrays.asList(ParameterSettings.of(CommandParameters.SHOP_ID, false, false, new Object[] {"Messages", "ShopIDNotPresent"}));
	}

}
