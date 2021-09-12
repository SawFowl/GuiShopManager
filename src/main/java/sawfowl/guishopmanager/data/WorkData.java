package sawfowl.guishopmanager.data;

import java.util.UUID;

import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;

public abstract class WorkData {

	public abstract void saveShop(String shopId);

	public abstract void loadShops();

	public abstract void deleteShop(String shopId);

	public abstract void loadAuction();
	
	public abstract void saveAuctionStack(SerializedAuctionStack serializedAuctionStack);
	
	public abstract void removeAuctionStack(UUID stackUUID);

	public abstract void saveExpireAuctionData(SerializedAuctionStack serializedAuctionStack);

	public abstract void removeExpireAuctionData(SerializedAuctionStack serializedAuctionStack);

	public abstract void saveExpireBetAuctionData(SerializedAuctionStack serializedAuctionStack);

	public abstract void removeExpireBetAuctionData(SerializedAuctionStack serializedAuctionStack);

}