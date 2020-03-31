package skills;

public enum Buff
{
	final_appraisal("最终确认", true, 0, 0), 
	great_strides("阔步", true, 0, 1.0),
	inner_quiet("内静", false, 0, 0),
	innovation("改革", false, 0, 0.5),
	manipulation("掌握", false, 0, 0),
	name_of_the_elements("元素之美名", false, 0, 0),
	waste_not("俭约", false, 0, 0),
	veneration("崇敬（暂译）", false, 0.5, 0),
	muscle_memory("坚信", true, 1, 0);
	
	public String name;
	private String address;
	private boolean once;
	private double progressBuff;
	private double qualityBuff;

	private Buff(String name, boolean once,  double progressBuff, double qualityBuff) {
		this.name = name;
		this.once = once;
		this.progressBuff = progressBuff;
		this.qualityBuff = qualityBuff;
		this.address = "/icons/buff_" + this.toString() + ".png";
	}
	
	public String getAddress() {
		return address;
	}

	public String getName() {
		return name;
	}
	
	public boolean isOnce() {
		return once;
	}
	
	public double getProgressBuff() {
		return progressBuff;
	}
	
	public double getQualityBuff() {
		return qualityBuff;
	}
}
