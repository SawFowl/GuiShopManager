package sawfowl.guishopmanager.utils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.leangen.geantyref.TypeToken;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;
import sawfowl.guishopmanager.serialization.commandsshop.SerializedCommandShop;
import sawfowl.guishopmanager.serialization.shop.SerializedShop;

public class TypeTokens extends sawfowl.localeapi.serializetools.TypeTokens {

	public static final TypeToken<Map<UUID, List<SerializedAuctionStack>>> MAP_EXPIRED_AUCTIONSTACKS_TOKEN = new TypeToken<Map<UUID, List<SerializedAuctionStack>>>(){};

	public static final TypeToken<List<SerializedAuctionStack>> LIST_AUCTIONSTACK_TOKEN = new TypeToken<List<SerializedAuctionStack>>(){};

	public static final TypeToken<SerializedAuctionStack> AUCTIONSTACK_TOKEN = new TypeToken<SerializedAuctionStack>(){};

	public static final TypeToken<SerializedShop> SHOP_TOKEN = new TypeToken<SerializedShop>(){};

	public static final TypeToken<SerializedCommandShop> COMMANDS_SHOP_TOKEN = new TypeToken<SerializedCommandShop>(){};

}
