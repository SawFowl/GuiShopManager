package sawfowl.guishopmanager.configure.locale.abstractlocale.commands;

import net.kyori.adventure.text.Component;

public interface CommandShop {

	Component listTitle();

	Component listPadding();

	Component commandAdded();

	Component listEmpty();

	Component listEmptyEditor();

	Component successDelete();

}
