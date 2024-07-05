package sawfowl.guishopmanager.configure.config;

import java.util.List;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.configure.config.auction.Expire;
import sawfowl.localeapi.api.LocalisedComment;

@ConfigSerializable
public class Auction {

	@Setting("Enable")
	private boolean enable = true;
	@Setting("ComponentLimit")
	@LocalisedComment(path = { "Comments", "MainConfig", "Auction", "ComponentLimit" }, plugin = "guishopmanager")
	private int componentLimit = 2000;
	@Setting("Server")
	@LocalisedComment(path = { "Comments", "MainConfig", "Auction", "Server" }, plugin = "guishopmanager")
	private String server = "ServerNumberOne";
	@Setting("Expire")
	private List<Expire> expire = Expire.createDefault();
	public Auction(){}

	public boolean isEnable() {
		return enable;
	}
	public int getComponentLimit() {
		return componentLimit;
	}
	public String getServer() {
		return server;
	}

	public List<Expire> getExpire() {
		if(expire.isEmpty()) expire = Expire.createDefault();
		return expire;
	}

}
