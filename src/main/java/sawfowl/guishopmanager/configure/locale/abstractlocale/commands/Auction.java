package sawfowl.guishopmanager.configure.locale.abstractlocale.commands;

import net.kyori.adventure.text.Component;

import sawfowl.localeapi.api.Translation;

public interface Auction extends Translation {

	Component addedToBlackList();

	Component allreadyBlocked();

	Component betNotPresent();

	Component priceNotPresent();

	Component currencyNotPresent(Component currencies);

}
