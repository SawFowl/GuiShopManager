package sawfowl.guishopmanager.commands.shop;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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
			Optional<Shop> shop = context.one(CommandParameters.SHOP);
			if(shop.isPresent()) {
				if(context.one(CommandParameters.LOCALE).isPresent()) {
					Locale locale = context.one(CommandParameters.LOCALE).orElse(null);
					if(locale != null) {
						if(context.one(CommandParameters.TRANSLATE).isPresent()) {
							shop.get().addTitle(locale.toLanguageTag(), LegacyComponentSerializer.legacyAmpersand().deserialize(context.one(CommandParameters.TRANSLATE).get()));
							plugin.getShopStorage().saveShop(shop.get().getID());
							audience.sendMessage(getComponent(localeSrc, "Messages", "TranslateAdded"));
						} else exception(localeSrc, "Messages", "TranslateNotPresent");
					} else exception(localeSrc, "Messages", "LocaleNotExist");
				} else exception(localeSrc, "Messages", "LocaleNotPresent");
			} else exception(localeSrc, "Messages", "ShopIDNotExists");
		} else exception(localeSrc, "Messages", "ShopIDNotPresent");
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
			ParameterSettings.of(CommandParameters.SHOP, false, "Messages", "ShopIDNotPresent"),
			ParameterSettings.of(CommandParameters.LOCALE, false, "Messages", "LocaleNotPresent"),
			ParameterSettings.of(CommandParameters.TRANSLATE, false, "Messages", "TranslateNotPresent")
		);
	}

}
