package sawfowl.guishopmanager.configure.abstractlocale.commands;

import net.kyori.adventure.text.Component;

public interface Exceptions {

	Component onlyPlayer();

	Component playerIsNotPresent();

	Component itemNotPresent();

	Component zeroOrNullPrices();

	Component dontOpenOther();

}
