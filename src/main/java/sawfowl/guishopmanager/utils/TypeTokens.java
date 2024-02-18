package sawfowl.guishopmanager.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import io.leangen.geantyref.TypeToken;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;
import sawfowl.guishopmanager.serialization.commandsshop.SerializedCommandShop;
import sawfowl.guishopmanager.serialization.shop.SerializedShop;
import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStackJsonNbt;

public class TypeTokens extends sawfowl.localeapi.api.serializetools.itemstack.TypeTokens {

	public static final TypeToken<Map<UUID, Set<SerializedAuctionStack>>> MAP_EXPIRED_AUCTIONSTACKS_TOKEN = new TypeToken<Map<UUID, Set<SerializedAuctionStack>>>(){};

	public static final TypeToken<Map<String, SerializedItemStackJsonNbt>> MAP_JSON_ITEMSTACK_TOKEN = new TypeToken<Map<String, SerializedItemStackJsonNbt>>(){};

	public static final TypeToken<List<SerializedItemStackJsonNbt>> LIST_SERIALIZED_JSON_STACKS_TOKEN = new TypeToken<List<SerializedItemStackJsonNbt>>(){};

	public static final TypeToken<Set<SerializedAuctionStack>> LIST_AUCTIONSTACK_TOKEN = new TypeToken<Set<SerializedAuctionStack>>(){};

	public static final TypeToken<SerializedAuctionStack> AUCTIONSTACK_TOKEN = new TypeToken<SerializedAuctionStack>(){};

	public static final TypeToken<SerializedShop> SHOP_TOKEN = new TypeToken<SerializedShop>(){};

	public static final TypeToken<SerializedCommandShop> COMMANDS_SHOP_TOKEN = new TypeToken<SerializedCommandShop>(){};

}
