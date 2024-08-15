package sawfowl.guishopmanager.utils;

import java.util.Optional;

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.economy.Currency;

import sawfowl.localeapi.api.TextUtils;

public class Currencies {

	public static String getId(Currency currency) {
		return Sponge.game().registry(RegistryTypes.CURRENCY).findValueKey(currency).map(ResourceKey::asString).orElse(getDefaultid());
	}

	public static String getDefaultid() {
		return Sponge.server().serviceProvider().economyService().isPresent() ?
				"currency:" + TextUtils.clearDecorations(Sponge.server().serviceProvider().economyService().get().defaultCurrency().displayName()).toLowerCase() :
					"n/a";
	}

	public static Optional<Currency> getCurrency(String id) {
		return Sponge.game().registry(RegistryTypes.CURRENCY).findValue(ResourceKey.resolve(id)).or(() -> id.equals("currency:" + TextUtils.clearDecorations(Sponge.server().serviceProvider().economyService().get().defaultCurrency().displayName()).toLowerCase()) ? Optional.ofNullable(Sponge.server().serviceProvider().economyService().get().defaultCurrency()) : Optional.empty());
	}

}
