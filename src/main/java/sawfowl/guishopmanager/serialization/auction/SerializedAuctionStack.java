package sawfowl.guishopmanager.serialization.auction;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStackJsonNbt;

@ConfigSerializable
public class SerializedAuctionStack implements Serializable {

	SerializedAuctionStack(){}

	public SerializedAuctionStack(ItemStack itemStack, List<SerializedAuctionPrice> prices, UUID ownerUUID, String ownerName, long timeExpires, String serverName) {
		this.itemStack = new SerializedItemStackJsonNbt(itemStack);
		this.prices = prices;
		this.ownerUUID = ownerUUID;
		this.ownerName = ownerName;
		this.timeExpires = timeExpires;
		this.serverName = serverName;
		this.stackUUID = UUID.randomUUID();
	}

	private static final long serialVersionUID = 01;

	@Setting("ItemStack")
	private SerializedItemStackJsonNbt itemStack;
	@Setting("Prices")
	private List<SerializedAuctionPrice> prices;
	@Setting("OwnerUUID")
	private UUID ownerUUID;
	@Setting("OwnerName")
	private String ownerName;
	@Setting("TimeExpires")
	private long timeExpires = 0;
	@Setting("Server")
	private String serverName;
	@Setting("StackUUID")
	private UUID stackUUID;
	@Setting("BetData")
	private SerializedBetData serializedBetData;

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = new SerializedItemStackJsonNbt(itemStack);
	}
	public SerializedItemStackJsonNbt getSerializedItemStack() {
		return itemStack;
	}
	public long getTimeExpires() {
		return timeExpires;
	}
	public void updateExpires(long time) {
		timeExpires = time + System.currentTimeMillis() + 100;
	}
	public List<SerializedAuctionPrice> getPrices() {
		return prices;
	}
	public UUID getOwnerUUID() {
		return ownerUUID;
	}
	public UUID getStackUUID() {
		return stackUUID;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public String getServerName() {
		return serverName;
	}
	public SerializedBetData getBetData() {
		return serializedBetData;
	}
	public boolean betIsNull() {
		return serializedBetData == null || serializedBetData.getBuyerUUID() == null;
	}
	public void setOwner(UUID owner, String name) {
		this.ownerUUID = owner;
		this.ownerName = name;
	}
	public void setStackUUID(UUID stackUUID) {
		this.stackUUID = stackUUID;
	}
	public void setBetData(SerializedBetData serializedBetData) {
		this.serializedBetData = serializedBetData;
	}
	public boolean isExpired() {
		return timeExpires < System.currentTimeMillis();
	}
	public SerializedAuctionPrice getOrDefaultPrice(int priceNumber) {
		return prices.size() - 1 >= priceNumber ? prices.get(priceNumber) : prices.get(0);
	}
	public boolean containsCurrency(Currency currency) {
		for(SerializedAuctionPrice serializedAuctionPrice : prices) {
			if(currency.displayName().equals(serializedAuctionPrice.getCurrency().displayName())) {
				return true;
			}
		}
		return false;
	}
	public Component getExpireTimeFromNow() {
		if (isExpired()) {
			return Component.text("Now");
		} else {
			long millis = timeExpires - System.currentTimeMillis();
			long minute = (millis / (1000 * 60)) % 60;
			long hour = millis / (1000 * 60 * 60);
			return Component.text(String.format("%02dh %02dm", hour, minute));
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(ownerUUID, stackUUID, timeExpires);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null || getClass() != obj.getClass()) return false;
		if(this == obj) return true;
		SerializedAuctionStack other = (SerializedAuctionStack) obj;
		return Objects.equals(ownerUUID, other.ownerUUID) && Objects.equals(stackUUID, other.stackUUID) && timeExpires == other.timeExpires;
	}

}
