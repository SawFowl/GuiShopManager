package sawfowl.guishopmanager.utils.serialization.auction;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import sawfowl.guishopmanager.utils.serialization.SerializedItemStack;

@ConfigSerializable
public class SerializedAuctionStack implements Serializable {

	SerializedAuctionStack(){}

	public SerializedAuctionStack(ItemStack itemStack, List<SerializedAuctionPrice> prices, UUID ownerUUID, String ownerName, long timeExpires, String serverName, UUID stackUUID) {
		this.serializedItemStack = new SerializedItemStack(itemStack);
		this.prices = prices;
		this.ownerUUID = ownerUUID;
		this.ownerName = ownerName;
		this.timeExpires = timeExpires;
		this.serverName = serverName;
		this.stackUUID = stackUUID;
	}

	private static final long serialVersionUID = 01;

	@Setting("ItemStack")
	private SerializedItemStack serializedItemStack;
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
	//@Setting("StackUUID")
	private UUID stackUUID;
	@Setting("BetData")
	private SerializedBetData serializedBetData;

	public void setItemStack(ItemStack itemStack) {
		serializedItemStack = new SerializedItemStack(itemStack.copy());
	}
	public SerializedItemStack getSerializedItemStack() {
		return serializedItemStack;
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
	public Text getOwnerName() {
		return Text.of(ownerName);
	}
	public String getServerName() {
		return serverName;
	}
	public SerializedBetData getBetData() {
		return serializedBetData;
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
    		if(currency.getId().equals(serializedAuctionPrice.getCurrency().getId())) {
    	    	return true;
    		}
    	}
    	return false;
    }
    public Text getExpireTimeFromNow() {
        if (isExpired()) {
            return Text.of("Now");
        } else {
            long millis = timeExpires - System.currentTimeMillis();
            long minute = (millis / (1000 * 60)) % 60;
            long hour = millis / (1000 * 60 * 60);
            return Text.of(String.format("%02dh %02dm", hour, minute));
        }
    }

}
