package sawfowl.guishopmanager.configure.locale.abstractlocale.commands;

import net.kyori.adventure.text.Component;

public interface Exceptions {

	Component playerIsNotPresent();

	Component itemNotPresent();

	Component zeroOrNullPrices();

	Component currencyNotPresent();

	Component dontOpenOther();

	Component shopNotPresent();

	Component shopAlreadyExists();

	Component menuNotPresent();

	Component invalidMenuId();

	Component slotNotPresent();

	Component invalidSlot();

	Component buyPriceNotPresent();

	Component sellPriceNotPresent();

	Component emptyTranslateName();

	Component localeNotExist();

	Component localeNotPresent();

	Component translateNotPresent();

	Component h2NotPresent();

}
