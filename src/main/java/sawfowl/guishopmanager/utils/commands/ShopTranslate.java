package sawfowl.guishopmanager.utils.commands;

import java.util.Locale;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import sawfowl.guishopmanager.GuiShopManager;

public class ShopTranslate implements CommandExecutor {

	GuiShopManager plugin;
	public ShopTranslate(GuiShopManager instance) {
		plugin = instance;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(args.<String>getOne(Text.of("Shop")).isPresent()) {
			String shopId = args.<String>getOne(Text.of("Shop")).get();
			if(plugin.shopExists(shopId)) {
				if(args.<String>getOne(Text.of("Locale")).isPresent()) {
					String localeId = args.<String>getOne(Text.of("Locale")).get();
					boolean exist = false;
					for(Locale locale : plugin.getLocaleAPI().getLocalesList()) {
						if(locale.toLanguageTag().equals(localeId)) {
							exist = true;
							break;
						}
					}
					if(exist) {
						if(args.<String>getOne(Text.of("Translate")).isPresent()) {
							plugin.getShop(shopId).addTitle(localeId, TextSerializers.FORMATTING_CODE.deserialize(args.<String>getOne(Text.of("Translate")).get()));
							plugin.getWorkShopData().saveShop(shopId);
							src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "TranslateAdded"));
						} else {
							src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "TranslateNotPresent"));
						}
					} else {
						src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "LocaleNotExist"));
					}
				} else {
					src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "LocaleNotPresent"));
				}
			} else {
				src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "ShopIDNotExists"));
			}
		} else {
			src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "ShopIDNotPresent"));
		}
		return CommandResult.success();
	}

}
