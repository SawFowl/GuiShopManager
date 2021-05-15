package sawfowl.guishopmanager.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.TypeTokens;

import com.google.common.reflect.TypeToken;

import mr_krab.localeapi.utils.YamlLocaleUtil;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import sawfowl.guishopmanager.GuiShopManager;

public class Locales {

	GuiShopManager plugin;
	public Locales(GuiShopManager instance) {
		plugin = instance;
	}

	// ЛОКАЛИЗАЦИЯ
	public Map<Locale, YamlLocaleUtil> getLocales() {
		return plugin.getLocaleAPI().getYamlLocalesMap("guishopmanager");
	}
	public YamlLocaleUtil getDefaultLocale() {
		return getLocales().get(plugin.getLocaleAPI().getDefaultLocale());
	}
	public YamlLocaleUtil getOrDefaultLocale(Locale locale) {
		return getLocales().getOrDefault(locale, getDefaultLocale());
	}
	public YamlLocaleUtil getLocale(Locale locale) {
		return getLocales().get(locale);
	}
	public Text getLocalizedText(Locale locale, Object... localeNode) {
		try {
			return getOrDefaultLocale(locale).getLocaleNode().getNode(localeNode).getValue(TypeTokens.TEXT_TOKEN);
		} catch (ObjectMappingException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
			return Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&cAn error occurred while trying to get text for a localized message.")).onHover(TextActions.showText(Text.of(e.getLocalizedMessage()))).build();
		}
	}
	public Text getDefaultLocalizedText(Object... localeNode) {
		try {
			return getDefaultLocale().getLocaleNode().getNode(localeNode).getValue(TypeTokens.TEXT_TOKEN);
		} catch (ObjectMappingException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
			return Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&cAn error occurred while trying to get text for a localized message.")).onHover(TextActions.showText(Text.of(e.getLocalizedMessage()))).build();
		}
	}
	public List<Text> getLocalizedListText(Locale locale, Object... localeNode) {
		try {
			return getOrDefaultLocale(locale).getLocaleNode().getNode(localeNode).getValue(new TypeToken<List<Text>>() {
				private static final long serialVersionUID = 01;});
		} catch (ObjectMappingException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
			return Arrays.asList(Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&cAn error occurred while trying to get text for a localized message.")).onHover(TextActions.showText(Text.of(e.getLocalizedMessage()))).build());
		}
	}

}
