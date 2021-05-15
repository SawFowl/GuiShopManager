package sawfowl.guishopmanager.utils.serialization.auction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class SerializedBetData implements Serializable {

	SerializedBetData(){}
	public SerializedBetData(String server, UUID buyerUUID, String buyerName, BigDecimal money, Currency currency){
		this.server = server;
		this.buyerUUID = buyerUUID;
		this.buyerName = buyerName;
		this.money = money.doubleValue();
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
	private Currency currency;
	@Setting("Tax")
	private double tax;

	public String getServer() {
		return server;
	}
	public UUID getBuyerUUID() {
		return buyerUUID;
	}
	public Text getBuyerName() {
		return Text.of(buyerName);
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
	public void setMoney(BigDecimal money) {
		this.money = money.doubleValue();
	}
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}
	public double getTax() {
		return tax;
	}
	public void setTax(double taxPercent, double stackSize) {
		tax = BigDecimal.valueOf(((money / 100) * taxPercent) * stackSize).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	@Override
	public String toString() {
		return  "Server: " + server +
				", BuyerUUID: " + buyerUUID +
				", BuyerName: " + buyerName +
				", Money: " + money +
				", Currency: " + currency.getDisplayName().toPlain() + 
				", Tax: " + tax;
	}

}