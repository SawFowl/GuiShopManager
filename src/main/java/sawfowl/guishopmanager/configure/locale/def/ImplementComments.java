package sawfowl.guishopmanager.configure.locale.def;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.configure.locale.abstractlocale.Comments;
import sawfowl.guishopmanager.configure.locale.abstractlocale.comments.BlackList;
import sawfowl.guishopmanager.configure.locale.abstractlocale.comments.MainConfig;
import sawfowl.guishopmanager.configure.locale.def.comments.ImplementBlackList;
import sawfowl.guishopmanager.configure.locale.def.comments.ImplementMainConfig;

@ConfigSerializable
public class ImplementComments implements Comments {

	@Setting("BlackList")
	private ImplementBlackList blackList = new ImplementBlackList();
	@Setting("MainConfig")
	private ImplementMainConfig mainConfig = new ImplementMainConfig();
	public ImplementComments() {}

	@Override
	public BlackList blackList() {
		return blackList;
	}

	@Override
	public MainConfig mainConfig() {
		return mainConfig;
	}

}
