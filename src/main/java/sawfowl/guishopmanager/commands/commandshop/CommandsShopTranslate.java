package sawfowl.guishopmanager.commands.commandshop;

import java.util.Locale;

import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.util.locale.LocaleSource;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.CommandParameters;

public class CommandsShopTranslate implements CommandExecutor {

	GuiShopManager plugin;
	public CommandsShopTranslate(GuiShopManager instance) {
		plugin = instance;
	}

	@Override
	public CommandResult execute(CommandContext context) throws CommandException {
		Object src = context.cause().root();
		if(context.one(CommandParameters.SHOP_ID).isPresent()) {
			String shopId = context.one(CommandParameters.SHOP_ID).get();
			if(plugin.commandShopExists(shopId)) {
				if(context.one(CommandParameters.LOCALE).isPresent()) {
					String localeId = context.one(CommandParameters.LOCALE).get();
					boolean exist = false;
					for(Locale locale : plugin.getLocaleAPI().getLocalesList()) {
						if(locale.toLanguageTag().equals(localeId)) {
							exist = true;
							break;
						}
					}
					if(exist) {
						if(context.one(CommandParameters.TRANSLATE).isPresent()) {
							plugin.getCommandShopData(shopId).addTitle(localeId, LegacyComponentSerializer.legacyAmpersand().deserialize(context.one(CommandParameters.TRANSLATE).get()));
							plugin.getCommandsShopStorage().saveCommandsShop(shopId);
							((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "TranslateAdded"));
						} else {
							((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "TranslateNotPresent"));
						}
					} else {
						((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "LocaleNotExist"));
					}
				} else {
					((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "LocaleNotPresent"));
				}
			} else {
				((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "ShopIDNotExists"));
			}
		} else {
			((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "ShopIDNotPresent"));
		}
		return CommandResult.success();
	}

}
