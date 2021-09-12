package sawfowl.guishopmanager.configure;

public class Expire {

	public Expire(int time, double tax, double fee, boolean isTax, boolean isFee) {
		this.time = time * 60000;
		this.tax = tax;
		this.fee = fee;
		this.isTax = isTax;
		this.isFee = isFee;
	}

	private long time;
	private double tax;
	private double fee;
	private boolean isTax;
	private boolean isFee;

	public long getTime() {
		return time;
	}
	public double getTax() {
		return tax;
	}
	public double getFee() {
		return fee;
	}
	public boolean isTax() {
		return isTax;
	}
	public boolean isFee() {
		return isFee;
	}

}
