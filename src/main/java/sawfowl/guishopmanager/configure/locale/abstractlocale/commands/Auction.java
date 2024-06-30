package sawfowl.guishopmanager.configure.locale.abstractlocale.commands;

import net.kyori.adventure.text.Component;

import sawfowl.localeapi.api.LocaleReference;

public interface Auction extends LocaleReference {

	Component addedToBlackList();

	Component itemIsAlreadyBlocked();

	Component betNotPresent();

	Component priceNotPresent();

	Component currencyNotPresent(Component currencies);

}
