package sawfowl.guishopmanager.utils.serialization.shop;

import java.io.Serializable;
import java.math.BigDecimal;

import org.spongepowered.api.service.economy.Currency;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class SerializedShopPrice implements Serializable {

	SerializedShopPrice(){}
	public SerializedShopPrice(Currency currency) {
		this.currency = currency;
	}

	private static final long serialVersionUID = 01;

	@Setting("Currency")
	private Currency currency;
	@Setting("BuyPrice")
	private double buyPrice = 0;
	@Setting("SellPrice")
	private double sellPrice = 0;

	public Currency getCurrency() {
		return currency;
	}

	public BigDecimal getBuyPrice() {
		return BigDecimal.valueOf(buyPrice);
	}

	public BigDecimal getSellPrice() {
		return BigDecimal.valueOf(sellPrice);
	}

	public BigDecimal getBuyOrSellPrice(boolean isBuy) {
		return isBuy ? BigDecimal.valueOf(buyPrice) : BigDecimal.valueOf(sellPrice);
	}

	public void setBuyOrSellPrice(BigDecimal value, boolean isBuy) {
		if(isBuy) {
			buyPrice = value.doubleValue();
		} else {
			sellPrice = value.doubleValue();
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
		return  "Currency: " + currency.getDisplayName().toPlain() +
				", BuyPrice: " + buyPrice +
				", SellPrice: " + sellPrice;
	}

}
