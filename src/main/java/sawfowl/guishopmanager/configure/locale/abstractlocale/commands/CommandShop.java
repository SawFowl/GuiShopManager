package sawfowl.guishopmanager.configure.locale.abstractlocale.commands;

import net.kyori.adventure.text.Component;

public interface CommandShop {

	Component title();

	Component padding();

	Component commandAdded();

	Component commandsRemoved();

	Component empty();

	Component emptyEditor();

	Component delete();

}
