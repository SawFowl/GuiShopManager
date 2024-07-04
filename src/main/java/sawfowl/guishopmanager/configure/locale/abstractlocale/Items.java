package sawfowl.guishopmanager.configure.locale.abstractlocale;

import java.util.List;

import org.spongepowered.api.service.economy.Currency;

import net.kyori.adventure.text.Component;

import sawfowl.localeapi.api.LocaleReference;

public interface Items {

	interface Name extends LocaleReference {

		Component back();

		Component next();

		Component addPage();

		Component buy();

		Component sell();

		Component exit();

		Component buyAndBack();

		Component sellAndBack();

		Component size(Component size);

		Component price(Component price);

		Component clear();

		Component changeCurrency();

		Component switchMode();

		Component auctionAddItem();

		Component returnAuctionItem();

	}

	interface Lore extends LocaleReference {

		List<Component> changePrice();

		List<Component> changeSize();

		List<Component> auctionSwitchMode();

		Component transactionVariants();

		Component currency(Currency currency);

		Component size(int size);

		Component sum(Currency currency, double size);

		Component price(Currency currency, double buy, double sell);

		Component commandPrice(Currency currency, double price);

		Component auctionPrice(Currency currency, double price, double total);

		Component auctionBet(Currency currency, double price, double total);

		Component yourBet(Currency currency, double size, double total);

		Component tax(Currency currency, double size);

		Component fee(Currency currency, double size);

		Component allowFree();

		Component switchFree();

		Component seller(String player);

		Component expired(Component expired);

		Component currentBuyer(String player);

		Component currentBet(Currency currency, double value);

		Component betClick();

		Component buyClick();

	}

	Name name();

	Lore lore();

}
