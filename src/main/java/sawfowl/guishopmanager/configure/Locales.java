package sawfowl.guishopmanager.configure;

import java.util.Locale;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.configurate.ConfigurateException;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.configure.locale.abstractlocale.AbstractLocale;
import sawfowl.guishopmanager.configure.locale.def.ImplementPluginLocale;
import sawfowl.localeapi.api.ConfigTypes;
import sawfowl.localeapi.api.LocaleService;
import sawfowl.localeapi.api.PluginLocale;

public class Locales {

	private final LocaleService localeService;
	private String[] localesTags;
	private String pluginid = "guishopmanager";
	public Locales(LocaleService localeService) {
		this.localeService = localeService;
		localeService.localesExist(pluginid);
		localeService.createPluginLocale(pluginid, ConfigTypes.HOCON, org.spongepowered.api.util.locale.Locales.DEFAULT);
		localeService.createPluginLocale(pluginid, ConfigTypes.YAML, org.spongepowered.api.util.locale.Locales.RU_RU);
		localeService.setDefaultReference(GuiShopManager.getInstance().getPluginContainer(), ImplementPluginLocale.class);
		generateDefault();
		generateRu();
	}

	public LocaleService getLocaleService() {
		return localeService;
	}

	public String[] getLocalesTags() {
		if(localesTags != null) return localesTags;
		return localesTags = localeService.getLocalesList().stream().map(Locale::toLanguageTag).toList().stream().toArray(String[]::new);
	}

	public AbstractLocale getLocale(ServerPlayer player) {
		return getLocale(player.locale());
	}

	public AbstractLocale getLocale(Locale locale) {
		return getPluginLocale(locale).asReference(AbstractLocale.class);
	}

	public AbstractLocale getSystemLocale() {
		return getPluginLocale(localeService.getSystemOrDefaultLocale()).asReference(AbstractLocale.class);
	}

	private void generateDefault() {
		if(getPluginLocale(org.spongepowered.api.util.locale.Locales.DEFAULT).getLocaleRootNode().empty()) try {
			getPluginLocale(org.spongepowered.api.util.locale.Locales.DEFAULT).setLocaleReference(ImplementPluginLocale.class);
			getPluginLocale(org.spongepowered.api.util.locale.Locales.DEFAULT).saveLocaleNode();
		} catch (ConfigurateException e) {
			e.printStackTrace();
		}
	}

	private void generateRu() {
		/*if(getPluginLocale(org.spongepowered.api.util.locale.Locales.RU_RU).getLocaleRootNode().empty()) try {
			getPluginLocale(org.spongepowered.api.util.locale.Locales.RU_RU).setLocaleReference(ImplementRuPluginLocale.class);
			getPluginLocale(org.spongepowered.api.util.locale.Locales.RU_RU).saveLocaleNode();
		} catch (ConfigurateException e) {
			e.printStackTrace();
		}*/
	}

	private PluginLocale getPluginLocale(Locale locale) {
		return localeService.getPluginLocales(pluginid).getOrDefault(locale, localeService.getPluginLocales(pluginid).get(org.spongepowered.api.util.locale.Locales.DEFAULT));
	}

}
