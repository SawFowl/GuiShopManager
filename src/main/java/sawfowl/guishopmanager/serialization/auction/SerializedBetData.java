package sawfowl.guishopmanager.serialization.auction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.utils.Currencies;

@ConfigSerializable
public class SerializedBetData implements Serializable {

	SerializedBetData(){}
	public SerializedBetData(String server, UUID buyerUUID, String buyerName, BigDecimal money, Currency currency){
		this.server = server;
		this.buyerUUID = buyerUUID;
		this.buyerName = buyerName;
		this.money = money.doubleValue();
		currencyId = Currencies.getId(currency);
		this.currency = currency;
	}

	private static final long serialVersionUID = 01;


	@Setting("Server")
	private String server;
	@Setting("BuyerUUID")
	private UUID buyerUUID;
	@Setting("BuyerName")
	private String buyerName;
	@Setting("Money")
	private double money;
	@Setting("Currency")
	private String currencyId;
	@Setting("Tax")
	private double tax;

	private Currency currency;

	public String getCurrencyId() {
		return currencyId;
	}

	public String getServer() {
		return server;
	}

	public UUID getBuyerUUID() {
		return buyerUUID;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public BigDecimal getMoney() {
		return BigDecimal.valueOf(money);
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public void setBuyerUUID(UUID buyerUUID) {
		this.buyerUUID = buyerUUID;
	}

	public void setBuyerName(String name) {
		buyerName = name;
	}

	public void changeMoney(BigDecimal money, boolean increase) {
		this.money = increase ? BigDecimal.valueOf(this.money).add(money).doubleValue() : BigDecimal.valueOf(this.money).subtract(money).doubleValue();
	}

	public void setMoney(BigDecimal money) {
		this.money = money.doubleValue();
	}

	public void setCurrency(Currency currency) {
		currencyId = Currencies.getId(currency);
		this.currency = currency;
	}

	public double getTax() {
		return tax;
	}

	public void setTax(double taxPercent, double stackSize) {
		tax = BigDecimal.valueOf(((money / 100) * taxPercent) * stackSize).setScale(2, RoundingMode.HALF_UP).doubleValue();
	}

	@Override
	public String toString() {
		return  "Server: " + server +
				", BuyerUUID: " + buyerUUID +
				", BuyerName: " + buyerName +
				", Money: " + money +
				", Currency: " + currencyId + 
				", Tax: " + tax;
	}

}