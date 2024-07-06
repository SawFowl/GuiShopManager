package sawfowl.guishopmanager.configure.locale.ru.comments;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.configure.locale.abstractlocale.comments.MainConfig.Auction;

@ConfigSerializable
public class ImplementAuction implements Auction {

	@Setting("ComponentLimit")
	private String componentLimit = "Ограничение на количество символов в компонентах элемента.\nДля проверки все компоненты преобразуются в одну `json` строку.";
	@Setting("Server")
	private String server = "Название сервера, на котором будет доступен возврат предмета игроку. Используйте разные имена на разных серверах.";
	@Setting("ExpireTime")
	private String expireTime = "Время до снятия товара с продажи в минутах.";
	@Setting("Tax")
	private String tax = "Налог на прибыль.";
	@Setting("Fee")
	private String fee = "Плата, взимаемая с игрока при выставлении предмета на аукцион.";
	public ImplementAuction() {}

	@Override
	public String componentLimit() {
		return componentLimit;
	}

	@Override
	public String server() {
		return server;
	}

	@Override
	public String expireTime() {
		return expireTime;
	}

	@Override
	public String tax() {
		return tax;
	}

	@Override
	public String fee() {
		return fee;
	}

}
