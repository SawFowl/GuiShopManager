package sawfowl.guishopmanager.commands.shop;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.AbstractCommand;
import sawfowl.guishopmanager.data.shop.Shop;
import sawfowl.guishopmanager.utils.CommandParameters;

public class Translate extends AbstractCommand {

	public Translate(GuiShopManager instance) {
		super(instance);
	}

	@Override
	public Parameterized build() {
		return Command.builder()
				.addParameters(CommandParameters.SHOP, CommandParameters.LOCALE, CommandParameters.TRANSLATE)
				.executor(this)
				.permission(permission())
				.build();
	}

	@Override
	public void execute(CommandContext context, Audience audience, Locale localeSrc, boolean isPlayer) throws CommandException {
		if(context.one(CommandParameters.SHOP).isPresent()) {
			Shop shop = context.one(CommandParameters.SHOP).get();
			if(context.one(CommandParameters.LOCALE).isPresent()) {
				Locale locale = context.one(CommandParameters.LOCALE).orElse(null);
				if(context.one(CommandParameters.TRANSLATE).isPresent()) {
					shop.addTitle(locale.toLanguageTag(), LegacyComponentSerializer.legacyAmpersand().deserialize(context.one(CommandParameters.TRANSLATE).get()));
					plugin.getShopStorage().saveShop(shop.getID());
					audience.sendMessage(getCommands(localeSrc).shop().translateAdded());
				} else exception(getExceptions(localeSrc).translateNotPresent());
			} else exception(getExceptions(localeSrc).localeNotPresent());
		} else exception(localeSrc, getExceptions(localeSrc).shopNotPresent());
	}

	@Override
	public String command() {
		return "translate";
	}

	@Override
	public String permission() {
		return Permissions.SHOP_TRANSLATE;
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return Arrays.asList(
			ParameterSettings.of(CommandParameters.SHOP, false, locale -> getExceptions(locale).shopNotPresent()),
			ParameterSettings.of(CommandParameters.LOCALE, false, locale -> getExceptions(locale).localeNotPresent()),
			ParameterSettings.of(CommandParameters.TRANSLATE, false, locale -> getExceptions(locale).translateNotPresent())
		);
	}

}
