package skills;

import java.util.Random;

import application.Engine;
import application.components.CraftingStatus;

public enum PQSkill implements Skill
{
	Basic_Synthesis("制作", 				 
														 0, 10, 1.2, 0, 1),
	Careful_Synthesis("模范制作", 			 
														 7, 10, 1.5, 0, 1),
	Rapid_Synthesis("高速制作", 				 
														 0, 10, 5.0, 0, 0.5),
	Groundwork("坯料制作", 						
														18, 20, 3.0, 0, 1.0),
	Focused_Synthesis("注视制作", 			 
														 5, 10, 2.0, 0, 0.5),
	Brand_of_the_Elements("元素之印记", 	 
														 6, 10, 1.0, 0, 1),
	Intensive_Synthesis("集中制作",		 
														 6, 10, 3.0, 0, 1),
	
	Basic_Touch("加工",						 
														18, 10, 0, 1.00, 1.0),
	Standard_Touch("中级加工",				 
														32, 10, 0, 1.25, 1.0),
	Hasty_Touch("仓促",						  
														 0, 10, 0, 1.00, 0.6),
	Precise_Touch("集中加工",					 
														18, 10, 0, 1.50, 1.0),
	Focused_Touch("注视加工",					 
														18, 10, 0, 1.50, 0.5),
	Patient_Touch("专心加工",					 
														 6, 10, 0, 1.00, 0.5),
	Prudent_Touch("俭约加工",					 
														25,  5, 0, 1.00, 1.0),
	Preparatory_Touch("坯料加工",			 
														40, 20, 0, 2.00, 1.0),
	
	Delicate_Synthesis("精密制作",		 
														32, 10, 1.0, 1.0, 1.0);
	
	
	String name;
	String imgAddress;
	int cpCost;	
	int durabilityCost; 
	double progressRate;
	double qualityRate;
	double successRate;

	static Random r;
	
	Engine engine;
	
	static {
		r = new Random();
	}
	
	private PQSkill(String name,  int cpCost, int durabilityCost,
			double progressRate, double qualityRate, double successRate) {
		this.name = name;
		this.imgAddress = "/icons/" + this.toString() + ".png";
		this.cpCost = cpCost;
		this.durabilityCost = durabilityCost;
		this.progressRate = progressRate;
		this.qualityRate = qualityRate;
		this.successRate = successRate;		
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override 
	public String getBaseProgressRate() {
		return Double.toString(progressRate * 100) + "%";
	}
	
	@Override 
	public String getBaseQualityRate() {
		return Double.toString(qualityRate * 100) + "%";
	}
	
	@Override
	public String getAddress() {
		return imgAddress;
	}
	
	@Override
	public double getActualProgressRate()
	{
		if(progressRate == 0) return 0;
		double temp = 1.0;
		boolean hasNOTE = false;
		for(ActiveBuff ab: engine.getActiveBuffs()) {
			if(ab.buff==Buff.name_of_the_elements) {hasNOTE = true;}
			temp += ab.buff.getProgressBuff();
		}
		
		if(this==Brand_of_the_Elements && hasNOTE) {
			temp += (double)engine.getPresentProgress()/engine.getTotalProgress()*2;
		}
		
		return temp * progressRate;
	}

	@Override
	public double getActualQualityRate()
	{
		if(qualityRate == 0) return 0;
		double temp = 1.0;
		for(ActiveBuff ab: engine.getActiveBuffs()) {
			temp += ab.buff.getQualityBuff();
		}
		
		return temp * qualityRate;
	}

	@Override
	public boolean isSuccess()
	{
		if(engine.isObserved() && (this == Focused_Synthesis || this == Focused_Touch)) {return true;}
		return r.nextDouble() <= (successRate + (engine.getCraftingStatus() == CraftingStatus.Centered ? 0.25 : 0));
	}

	@Override
	public int getCPCost()
	{
		return cpCost;
	}

	@Override
	public int getDurCost()
	{
		return durabilityCost;
	}
	
	@Override
	public void setEngine(Engine e)
	{
		engine = e;
	}
	
	@Override
	public double getSuccessRate() {
		if(successRate == 1.0) {
			return 1.0;
		}
		double d = successRate + (engine.getCraftingStatus() == CraftingStatus.Centered ? 0.25 : 0);
		d = (double)Math.round(d * 10)/10;
		return d;
	}
 
	public static void setRandom(Random ra) {
		r = ra;
	}
}
