package sawfowl.guishopmanager.commands.shop;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

import net.kyori.adventure.audience.Audience;

import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.AbstractCommand;
import sawfowl.guishopmanager.data.shop.Shop;
import sawfowl.guishopmanager.utils.CommandParameters;

public class Delete extends AbstractCommand {

	public Delete(GuiShopManager instance) {
		super(instance);
	}

	@Override
	public Parameterized build() {
		return fastBuild();
	}

	@Override
	public void execute(CommandContext context, Audience audience, Locale locale, boolean isPlayer) throws CommandException {
		if(context.one(CommandParameters.SHOP).isPresent()) {
			Shop shop = context.one(CommandParameters.SHOP).orElse(null);
			if(shop != null) {
				plugin.removeShop(shop.getID());
				audience.sendMessage(getComponent(locale, "Messages", "SuccessDelete"));
			} else {
				audience.sendMessage(getComponent(locale, "Messages", "ShopIDNotExists"));
			}
		} else {
			audience.sendMessage(getComponent(locale, "Messages", "ShopIDNotPresent"));
		}
	}

	@Override
	public String command() {
		return "delete";
	}

	@Override
	public String permission() {
		return Permissions.SHOP_DELETE;
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return Arrays.asList(ParameterSettings.of(CommandParameters.SHOP, false, "Messages", "ShopIDNotPresent"));
	}

}
