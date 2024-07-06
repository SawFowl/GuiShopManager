package sawfowl.guishopmanager.configure.locale.ru.commands;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.guishopmanager.configure.locale.PlaceholderKeys;
import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.Auction;

@ConfigSerializable
public class ImplementAuction implements Auction {

	@Setting("AddedToBlackList")
	private Component addedToBlackList = deserialize("&cПредмет добавлен в черный список");
	@Setting("ItemIsAlreadyBlocked")
	private Component itemIsAlreadyBlocked = deserialize("&cПредмет уже заблокирован.");
	@Setting("PriceNotPresent")
	private Component priceNotPresent = deserialize("&eВы не указали цену за лот. Чтобы подтвердить размещение лота, нажмите на это сообщение.");
	@Setting("BetNotPresent")
	private Component betNotPresent = deserialize("&eВы не указали ставку для своего лота. Чтобы подтвердить размещение лота, нажмите на это сообщение.");
	@Setting("CurrencyNotPresent")
	private Component currencyNotPresent = deserialize("&eВы не указали валюту для вашего лота. Будет использована валюта по умолчанию. Чтобы подтвердить размещение лота, нажмите на это сообщение. \n&eДоступные валюты: &6%currencies%&e.");
	public ImplementAuction() {}

	@Override
	public Component addedToBlackList() {
		return addedToBlackList;
	}

	@Override
	public Component allreadyBlocked() {
		return itemIsAlreadyBlocked;
	}

	@Override
	public Component priceNotPresent() {
		return priceNotPresent;
	}

	@Override
	public Component betNotPresent() {
		return betNotPresent;
	}

	@Override
	public Component currencyNotPresent(Component currencies) {
		return replace(currencyNotPresent, PlaceholderKeys.CURRENCIES, currencies);
	}

}
