package sawfowl.guishopmanager.utils;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.registry.Registry;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.economy.Currency;

public interface Currencies extends Registry<Currency> {

	static Currencies get() {
		return (Currencies) Sponge.game().registry(RegistryTypes.CURRENCY);
	}

}
