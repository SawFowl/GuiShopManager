package sawfowl.guishopmanager.configure.abstractlocale.commands;

import net.kyori.adventure.text.Component;

import sawfowl.localeapi.api.PluginLocale;

public interface Auction extends PluginLocale {

	Component addedToBlackList();

	Component itemIsAlreadyBlocked();

	Component betNotPresent();

	Component priceNotPresent();

	Component currencyNotPresent();

}
