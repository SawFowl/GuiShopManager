package sawfowl.guishopmanager.serialization.auction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

@ConfigSerializable
public class SerializedAuctionPrice implements Serializable {

	SerializedAuctionPrice(){}
	public SerializedAuctionPrice(Currency currency) {
		currencyName = LegacyComponentSerializer.legacyAmpersand().serialize(currency.displayName());
		this.currency = currency;
	}

	private static final long serialVersionUID = 01;

	@Setting("Currency")
	private String currencyName;
	@Setting("Bet")
	private double bet;
	@Setting("Price")
	private double price;
	@Setting("Tax")
	private double tax;
	private double betTax;

	private Currency currency;

	public String getCurrencyName() {
		return currencyName;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public BigDecimal getBet() {
		return BigDecimal.valueOf(bet);
	}

	public void setBet(BigDecimal value) {
		bet = value.doubleValue();
	}

	public void setPrice(BigDecimal value) {
		price = value.doubleValue();
	}

	public void updateBet(BigDecimal value, boolean increase) {
		bet = increase ? BigDecimal.valueOf(bet).add(value).doubleValue() : BigDecimal.valueOf(bet).subtract(value).doubleValue();
		if(bet < 0) bet = 0;
	}

	public BigDecimal getPrice() {
		return BigDecimal.valueOf(price);
	}

	public void updatePrice(BigDecimal value, boolean increase) {
		price = increase ? BigDecimal.valueOf(price).add(value).doubleValue() : BigDecimal.valueOf(price).subtract(value).doubleValue();
		if(price < 0) price = 0;
	}

	public BigDecimal getBetOrPrice(boolean isBet, int priceNumber) {
		return isBet && priceNumber == 0 ? BigDecimal.valueOf(bet) : BigDecimal.valueOf(price);
	}

	public void updateBetOrPrice(BigDecimal value, boolean isBet, int priceNumber, boolean increase) {
		if(isBet && priceNumber == 0) {
			updateBet(value, increase);
		} else {
			updatePrice(value, increase);
		}
	}

	public void setZero() {
		price = 0;
		bet = 0;
	}

	public double getTax() {
		return tax;
	}

	public double getBetTax() {
		return betTax;
	}

	public void setTax(double taxPercent, double stackSize) {
		tax = BigDecimal.valueOf(((price * stackSize) / 100) * taxPercent).setScale(2, RoundingMode.HALF_UP).doubleValue();
		betTax = BigDecimal.valueOf(((bet * stackSize) / 100) * taxPercent).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	public boolean isZero() {
		return price <= 0 && bet <= 0;
	}

	@Override
	public String toString() {
		return  "Currency: " + currencyName +
				", Price: " + price+
				", Bet: " + bet + 
				", Tax: " + tax;
	}

}
