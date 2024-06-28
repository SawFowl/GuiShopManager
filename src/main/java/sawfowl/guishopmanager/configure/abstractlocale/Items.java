package sawfowl.guishopmanager.configure.abstractlocale;

import java.util.List;

import net.kyori.adventure.text.Component;

import sawfowl.localeapi.api.PluginLocale;

public interface Items {

	interface Names extends PluginLocale {

		Component back();

		Component next();

		Component addPage();

		Component buy();

		Component sell();

		Component exit();

		Component buyAndBack();

		Component sellAndBack();

		Component size();

		Component price();

		Component clear();

		Component changeCurrency();

		Component switchMode();

		Component auctionAddItem();

		Component returnAuctionItem();

	}

	interface Lore extends PluginLocale {

		List<Component> changePrice();

		List<Component> changeSize();

		Component transactionVariants();

		Component currentCurrency();

		Component currentSize();

		Component currentSum();

		Component price();

		Component commandPrice();

		Component auctionPrice();

		Component auctionBet();

		Component yourBet();

		Component tax();

		Component fee();

		Component switchFree();

		Component auctionSwitchMode();

		Component seller();

		Component expired();

		Component currentBuyer();

		Component currentBet();

		Component betClick();

		Component buyClick();

	}

	Names names();

	Lore lore();

}
