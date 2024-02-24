package sawfowl.guishopmanager.serialization.shop;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import sawfowl.guishopmanager.utils.Currencies;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import java.io.Serializable;
import java.math.BigDecimal;

@ConfigSerializable
public class SerializedShopPrice implements Serializable {

	SerializedShopPrice(){}
	public SerializedShopPrice(Currency currency) {
		currencyId = Currencies.getId(currency);
		this.currency = currency;
	}

	private static final long serialVersionUID = 01;

	@Setting("Currency")
	private String currencyId;
	@Setting("BuyPrice")
	private double buyPrice = 0;
	@Setting("SellPrice")
	private double sellPrice = 0;

	private Currency currency;

	public String getCurrencyId() {
		return currencyId;
	}

	public Currency getCurrency() {
		return currency;
	}

	public boolean isDefault() {
		return currencyId.equals(Currencies.getDefaultid());
	}

	public boolean isPresent() {
		return currency != null;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public BigDecimal getBuyPrice() {
		return BigDecimal.valueOf(buyPrice);
	}

	public BigDecimal getSellPrice() {
		return BigDecimal.valueOf(sellPrice);
	}

	public void setBuyOrSellPrice(BigDecimal value, boolean isBuy, boolean increase) {
		if(isBuy) {
			buyPrice = increase ? BigDecimal.valueOf(buyPrice).add(value).doubleValue() : BigDecimal.valueOf(buyPrice).subtract(value).doubleValue();
			if(buyPrice <= 0) buyPrice = 0;
		} else {
			sellPrice = increase ? BigDecimal.valueOf(sellPrice).add(value).doubleValue() : BigDecimal.valueOf(sellPrice).subtract(value).doubleValue();
			if(sellPrice <= 0) sellPrice = 0;
		}
	}

	public void setZero() {
		buyPrice = 0;
		sellPrice = 0;
	}

	public boolean isZero() {
		return buyPrice <= 0 && sellPrice <= 0;
	}

	@Override
	public String toString() {
		return  "Currency: " + LegacyComponentSerializer.legacyAmpersand().serialize(currency.displayName()) +
				", BuyPrice: " + buyPrice +
				", SellPrice: " + sellPrice;
	}

}
