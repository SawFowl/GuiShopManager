package sawfowl.guishopmanager.serialization.commandsshop;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import sawfowl.guishopmanager.utils.Currencies;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.io.Serializable;
import java.math.BigDecimal;

@ConfigSerializable
public class SerializedCommandShopPrice implements Serializable {

	SerializedCommandShopPrice(){}
	public SerializedCommandShopPrice(Currency currency) {
		currencyId = Currencies.getId(currency);
		this.currency = currency;
	}

	private static final long serialVersionUID = 01;

	@Setting("Currency")
	private String currencyId;
	@Setting("Price")
	private double buyPrice = 0;
	@Setting("AllowFree")
	private boolean allowFree = false;

	private Currency currency;

	public String getCurrencyId() {
		return currencyId;
	}

	public Currency getCurrency() {
		return currency;
	}

	public boolean isPresent() {
		return currency != null;
	}

	public boolean isDefault() {
		return currencyId.equals(Currencies.getDefaultid());
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public BigDecimal getBuyPrice() {
		return BigDecimal.valueOf(buyPrice);
	}

	public void setPrice(BigDecimal value, boolean increase) {
		buyPrice = increase ? BigDecimal.valueOf(buyPrice).add(value).doubleValue() : BigDecimal.valueOf(buyPrice).subtract(value).doubleValue();
		if(buyPrice <= 0) buyPrice = 0;
	}

	public void setZero() {
		buyPrice = 0;
	}

	public boolean isAllowFree() {
		return allowFree;
	}

	public void switchFree() {
		setAllowFree(!allowFree);
	}

	public void setAllowFree(boolean allowFree) {
		this.allowFree = allowFree;
	}

	public boolean isZero() {
		return buyPrice <= 0;
	}

	@Override
	public String toString() {
		return  "Currency: " + LegacyComponentSerializer.legacyAmpersand().serialize(currency.displayName()) +
				", Price: " + buyPrice;
	}

}
