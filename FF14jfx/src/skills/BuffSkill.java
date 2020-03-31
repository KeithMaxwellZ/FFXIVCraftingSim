package skills;

import java.util.Random;
import skills.Buff;
import application.CraftingStatus;
import application.Engine;

public enum BuffSkill implements Skill
{
	Muscle_Memory("坚信", 					 
													6, 10, 3.0, 0.0, 1.0, 5, true, true, Buff.muscle_memory),
	Reflect("闲静",								
													24, 10, 0.0, 1.0, 1.0, 3, true, true, Buff.inner_quiet),
	Inner_Quiet("内静",						
													18, 0 , 0.0, 0.0, 1.0, 1, false, false, Buff.inner_quiet),
	Waste_Not("俭约",							
													56, 0 , 0.0, 0.0, 1.0, 4, false, false, Buff.waste_not),
	Waste_Not_II("俭约II",					
													98, 0 , 0.0, 0.0, 1.0, 8, false, false, Buff.waste_not),
	Great_Strides("阔步",					
													32, 0 , 0.0, 0.0, 1.0, 3, false, false, Buff.great_strides),
	Innovation("改革",
													18, 0 , 0.0, 0.0, 1.0, 4, false, false, Buff.innovation),
	Veneration("崇敬（暂译）",						
													18, 0 , 0.0, 0.0, 1.0, 4, false, false, Buff.veneration),
	Name_of_the_Elements("元素之美名", 	
													30, 0 , 0.0, 0.0, 1.0, 3, true, false, Buff.name_of_the_elements),
	Final_Appraisal("最终确认",				 
													1, 0 , 0.0, 0.0, 1.0, 5, false, false, Buff.final_appraisal),
	Manipulation("掌握",					
													96, 0 , 0.0, 0.0, 1.0, 8, false, false, Buff.manipulation),
	;
	
	String name;
	String imgAddress;
	int cpCost;	
	int durabilityCost; 
	double progressRate;
	double qualityRate;
	double successRate;
	int last;
	boolean once;
	boolean firstTurn;

	Random r;
	Buff buff;
	Engine engine;
	
	private BuffSkill(String name,  int cpCost, int durabilityCost,
					   double progressRate, double qualityRate, double successRate,
					   int last, boolean once, boolean firstTurn, Buff buff) {
		this.name = name;
		this.imgAddress = "/icons/" + this.toString() + ".png";
		this.cpCost = cpCost;
		this.durabilityCost = durabilityCost;
		this.progressRate = progressRate;
		this.qualityRate = qualityRate;
		this.successRate = successRate;
		this.last = last;
		this.once = once;
		this.firstTurn = firstTurn;
		this.buff = buff;
		r = new Random();
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getAddress() {
		return imgAddress;
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
	public double getActualProgressRate()
	{
		if(progressRate == 0) return 0;
		double temp = 1.0;
		for(ActiveBuff ab: engine.getActiveBuffs()) {
			temp += ab.buff.getProgressBuff();
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

	public void createBuff() {
		engine.addToLogs("Buff Created: " + buff.toString() + " " + last);
		engine.addActiveBuff(buff, last);
		if(this == Inner_Quiet || this == Reflect) {
			engine.setInnerQuiet(last);
		}
	}
}
