package sawfowl.guishopmanager.configure.locale.abstractlocale;

import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.*;

public interface Commands {

	Backup backup();

	Auction auction();

	CommandShop commandShop();

	Exceptions exceptions();

	Shop shop();

	Component run();

	Component title();

	Component padding();

	Component reload();

}
