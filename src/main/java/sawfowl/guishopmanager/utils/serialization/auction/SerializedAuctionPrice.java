package sawfowl.guishopmanager.utils.serialization.auction;

import java.io.Serializable;
import java.math.BigDecimal;

import org.spongepowered.api.service.economy.Currency;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class SerializedAuctionPrice implements Serializable {

	SerializedAuctionPrice(){}
	public SerializedAuctionPrice(Currency currency) {
		this.currency = currency;
	}

	private static final long serialVersionUID = 01;

	@Setting("Currency")
	private Currency currency;
	@Setting("Bet")
	private double bet;
	@Setting("Price")
	private double price;
	@Setting("Tax")
	private double tax;

	public Currency getCurrency() {
		return currency;
	}
	public BigDecimal getBet() {
		return BigDecimal.valueOf(bet);
	}
	public void setBet(BigDecimal value) {
		bet = value.doubleValue();
		if(bet < 0) {
			bet = 0;
		}
	}
	public BigDecimal getPrice() {
		return BigDecimal.valueOf(price);
	}
	public void setPrice(BigDecimal value) {
		price = value.doubleValue();
		if(price < 0) {
			price = 0;
		}
	}
	public BigDecimal getBetOrPrice(boolean isBet, int priceNumber) {
		return isBet && priceNumber == 0 ? BigDecimal.valueOf(bet) : BigDecimal.valueOf(price);
	}
	public void setBetOrPrice(BigDecimal value, boolean isBet, int priceNumber) {
		if(isBet && priceNumber == 0) {
			setBet(value);
		} else {
			setPrice(value);
		}
	}
	public void setZero() {
		price = 0;
		bet = 0;
	}
	public double getTax() {
		return tax;
	}
	public void setTax(double taxPercent, double stackSize) {
		tax = BigDecimal.valueOf(((price * stackSize) / 100) * taxPercent).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	@Override
	public String toString() {
		return  "Currency: " + currency.getDisplayName().toPlain() +
				", Price: " + price+
				", Bet: " + bet + 
				", Tax: " + tax;
	}

}
