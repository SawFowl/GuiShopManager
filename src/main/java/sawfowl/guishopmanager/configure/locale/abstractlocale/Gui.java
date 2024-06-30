package sawfowl.guishopmanager.configure.locale.abstractlocale;

import net.kyori.adventure.text.Component;

public interface Gui {

	interface Auction {

		Component bet();

		Component returnItems();

		Component edit();

	}

	interface Shop {

		Component editBuy();

		Component editSell();

		Component buyTransaction();

		Component sellTransaction();

	}

	interface CommandShop {

		Component edit();

	}

	Auction auction();

	Shop shop();

	CommandShop commandShop();

}
