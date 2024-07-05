package sawfowl.guishopmanager.configure.config.auction;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class Fee {

	@Setting("Enable")
	private boolean enable = true;
	@Setting("Size")
	private double size;
	public Fee() {}
	public Fee(double size){
		this.size = size;
	}

	public boolean isEnable() {
		return enable;
	}

	public double getSize() {
		return size;
	}

}
