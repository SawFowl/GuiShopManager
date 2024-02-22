package sawfowl.guishopmanager.commands.shop;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.AbstractCommand;
import sawfowl.guishopmanager.utils.CommandParameters;

public class Translate extends AbstractCommand {

	public Translate(GuiShopManager instance) {
		super(instance);
	}

	@Override
	public Parameterized build() {
		return null;
	}

	@Override
	public void execute(CommandContext context, Audience audience, Locale localeSrc, boolean isPlayer) throws CommandException {
		if(context.one(CommandParameters.SHOP_ID).isPresent()) {
			String shopId = context.one(CommandParameters.SHOP_ID).get();
			if(plugin.shopExists(shopId)) {
				if(context.one(CommandParameters.LOCALE).isPresent()) {
					String localeId = context.one(CommandParameters.LOCALE).get();
					if(plugin.getLocaleAPI().getLocalesList().stream().filter(locale -> locale.toLanguageTag().equals(localeId)).findFirst().isPresent()) {
						if(context.one(CommandParameters.TRANSLATE).isPresent()) {
							plugin.getShop(shopId).addTitle(localeId, LegacyComponentSerializer.legacyAmpersand().deserialize(context.one(CommandParameters.TRANSLATE).get()));
							plugin.getShopStorage().saveShop(shopId);
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
			ParameterSettings.of(CommandParameters.SHOP_ID, false, "Messages", "ShopIDNotPresent"),
			ParameterSettings.of(CommandParameters.LOCALE, false, "Messages", "LocaleNotPresent"),
			ParameterSettings.of(CommandParameters.TRANSLATE, false, "Messages", "TranslateNotPresent")
		);
	}

}
