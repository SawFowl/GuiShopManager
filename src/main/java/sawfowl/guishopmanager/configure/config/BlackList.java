package sawfowl.guishopmanager.configure.config;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.localeapi.api.LocalisedComment;
import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStack;

@ConfigSerializable
public class BlackList {

	@Setting("Masks")
	@LocalisedComment(path = { "Comments", "BlackList", "Masks" }, plugin = "guishopmanager")
	private List<String> masks = Arrays.asList("dirt", "minecraft:barrier");
	@Setting("Items")
	@LocalisedComment(path = { "Comments", "BlackList", "Items" }, plugin = "guishopmanager")
	private List<SerializedItemStack> items = Arrays.asList(new SerializedItemStack("minecraft:bedrock", 1, null), new SerializedItemStack("minecraft:cobblestone", 1, null));
	public BlackList() {}

	public List<String> getMasks() {
		return masks;
	}

	public void setMasks(List<String> masks) {
		this.masks = masks;
	}

	public List<SerializedItemStack> getItems() {
		return items;
	}

	public void setItems(List<SerializedItemStack> items) {
		this.items = items;
	}

}
