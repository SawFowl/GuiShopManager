package sawfowl.guishopmanager.utils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.kyori.adventure.text.Component;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.localeapi.api.PluginLocale;

public class Locales {

	GuiShopManager plugin;
	public Locales(GuiShopManager instance) {
		plugin = instance;
	}

	public Map<Locale, PluginLocale> getLocales() {
		return plugin.getLocaleAPI().getPluginLocales("guishopmanager");
	}
	public PluginLocale getDefaultLocale() {
		return getLocales().get(org.spongepowered.api.util.locale.Locales.DEFAULT);
	}
	public PluginLocale getOrDefaultLocale(Locale locale) {
		return getLocales().getOrDefault(locale, getDefaultLocale());
	}
	public Component getComponent(Locale locale, Object... localeNode) {
		return getOrDefaultLocale(locale).getComponent(localeNode);
	}
	public List<Component> getComponents(Locale locale, Object... localeNode) {
		return getOrDefaultLocale(locale).getListComponents(localeNode);
	}

}
