package sawfowl.guishopmanager.configure.config.auction;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.localeapi.api.LocalisedComment;

@ConfigSerializable
public class Expire {

	@Setting("ExpireTime")
	@LocalisedComment(path = { "Comments", "MainConfig", "Auction", "ExpireTime" }, plugin = "guishopmanager")
	private int time;
	@Setting("Tax")
	@LocalisedComment(path = { "Comments", "MainConfig", "Auction", "Tax" }, plugin = "guishopmanager")
	private Tax tax;
	@Setting("Fee")
	@LocalisedComment(path = { "Comments", "MainConfig", "Auction", "Fee" }, plugin = "guishopmanager")
	private Fee fee;
	public Expire(){}
	public Expire(int time, double tax, double fee){
		this.time = time;
		this.tax = new Tax(tax);
		this.fee = new Fee(fee);
	}

	public int getTime() {
		return time;
	}

	public Tax getTax() {
		return tax;
	}

	public Fee getFee() {
		return fee;
	}

	private static Expire create(int time, double tax, double fee) {
		return new Expire(time, tax, fee);
	}

	public static List<Expire> createDefault() {
		return Arrays.asList(create(720, 5d, 0.05d), create(1440, 10d, 0.15d), create(2160, 15d, 0.25d));
	}

}
