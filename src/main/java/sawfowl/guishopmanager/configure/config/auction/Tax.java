package sawfowl.guishopmanager.configure.config.auction;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class Tax {

	@Setting("Enable")
	private boolean enable = true;
	@Setting("Size")
	private double size;
	public Tax(){}
	public Tax(double size){
		this.size = size;
	}

	public boolean isEnable() {
		return enable;
	}

	public double getSize() {
		return size;
	}

}
