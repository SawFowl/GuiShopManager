package sawfowl.guishopmanager.configure.config;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class Aliases {

	@Setting("Enable")
	private boolean enable = true;
	@Setting("List")
	private List<String> list = Arrays.asList("shop");
	public Aliases() {}

	public boolean isEnable() {
		return enable;
	}

	public List<String> getList() {
		return list;
	}

	private Aliases setList(String... array) {
		list = Arrays.asList(array);
		return this;
	}

	public static Aliases createAuction() {
		return new Aliases().setList("auction", "market");
	}

}
