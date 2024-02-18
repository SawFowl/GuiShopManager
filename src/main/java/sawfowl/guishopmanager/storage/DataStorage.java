package sawfowl.guishopmanager.storage;

import java.util.UUID;

import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;

public interface DataStorage {

	void saveShop(String shopId);

	void loadShops();

	void deleteShop(String shopId);

	void saveCommandsShop(String shopId);

	void loadCommandsShops();

	void deleteCommandsShop(String shopId);

	void loadAuction();

	void saveAuctionStack(SerializedAuctionStack serializedAuctionStack);

	void removeAuctionStack(UUID stackUUID);

	void saveExpireAuctionData(SerializedAuctionStack serializedAuctionStack);

	void removeExpireAuctionData(SerializedAuctionStack serializedAuctionStack);

	void saveExpireBetAuctionData(SerializedAuctionStack serializedAuctionStack);

	void removeExpireBetAuctionData(SerializedAuctionStack serializedAuctionStack);

}