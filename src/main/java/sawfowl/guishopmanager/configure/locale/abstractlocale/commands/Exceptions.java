package sawfowl.guishopmanager.configure.locale.abstractlocale.commands;

import net.kyori.adventure.text.Component;

public interface Exceptions {

	Component onlyPlayer();

	Component playerIsNotPresent();

	Component itemNotPresent();

	Component zeroOrNullPrices();

	Component dontOpenOther();

	Component shopIDNotPresent();

	Component shopIDAlreadyExists();

	Component shopIDNotExists();

	Component invalidShopID();

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

}
