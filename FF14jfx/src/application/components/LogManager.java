package application.components;

import java.util.ArrayList;

import application.ViewManagerPC;
import engine.CraftingStatus;
import skills.ActiveBuff;
import skills.Skill;

public class LogManager
{
	// Turn info
	public class Node {
		Skill s;
		boolean skillSuccess = true;
		boolean observed = false;
		CraftingStatus cs;
		int presentProgress = 0;
		int presentQuality = 0;
		int progressIncrease = 0;
		int qualityIncrease = 0;
		double progressRate = 0;
		double qualityRate = 0;
		int presentDurability = 0;
		int durabilityDecrease = 0;
		int presentCP = 0;
		int CPDecrease = 0;
		ArrayList<ActiveBuff> buffList;
		int round;
		
		public Skill getSkill() {
			return s;
		}
		public boolean isSkillSuccess() {
			return skillSuccess;
		}
		public boolean isObserved() {
			return observed;
		}
		public CraftingStatus getCraftingStatus() {
			return cs;
		}
		public int getPresentProgress() {
			return presentProgress;
		}
		public int getPresentQuality() {
			return presentQuality;
		}
		public int getProgressIncrease() {
			return progressIncrease;
		}
		public int getQualityIncrease() {
			return qualityIncrease;
		}
		public double getProgressRate() {
			return progressRate;
		}
		public double getQualityRate() {
			return qualityRate;
		}
		public int getPresentDurability() {
			return presentDurability;
		}
		public int getDurabilityDecrease() {
			return durabilityDecrease;
		}
		public int getPresentCP() {
			return presentCP;
		}
		public int getCPDecrease() {
			return CPDecrease;
		}
		public ArrayList<ActiveBuff> getBuffList() {
			return buffList;
		}
		public int getRound() {
			return round;
		}
	}
	
	// Base info
	private int craftsmanship;
	private int control;
	private int cp;
	private int totalProgress;
	private int totalQuality;
	private int totalDurability;
	private CraftingStatus.Mode m;
	
	// Finish info
	private boolean finished;
	private boolean usedDebug;
	private boolean hasGCD;
	private double runtime;
	private int value;
	private int sp;
	private int totalRound;
	private long seed;
	
	private ArrayList<String> logs;
	private ArrayList<Node> craftingHistory; 
	private Node presentNode;
	
	private ViewManagerPC vm;
	
	public LogManager() {
		craftingHistory = new ArrayList<>();
		finished = false;
	}
	
	public void init() {
		presentNode = new Node();
	}
	
	public Node getPresentNode() {
		return presentNode;
	}
	
	public void setSkill(Skill s) {
		presentNode.s = s;
	}
	
	public void setSkillSuccess(boolean skillSuccess) {
		presentNode.skillSuccess = skillSuccess;
	}
	
	public void setOberved(boolean observed) {
		presentNode.observed = observed;
	}
	
	public void setCraftingStatus(CraftingStatus cs) {
		presentNode.cs = cs;
	}
	
	public void setPresentProgress(int presentProgress) {
		presentNode.presentProgress = presentProgress;
	}
	
	public void setPresentQuality(int presentQuality) {
		presentNode.presentQuality = presentQuality;
	}
	
	public void setPresentProgressIncrease(int presentProgressIncrease) {
		presentNode.progressIncrease = presentProgressIncrease;
	}
	
	public void setPresentProgressRate(double presentProgressRate) {
		presentNode.progressRate = presentProgressRate;
	}
	
	public void setPresentQualityIncrease(int presentQualityIncrease) {
		presentNode.qualityIncrease = presentQualityIncrease;
	}
	
	public void setPresentQualityRate(double presentQualityRate) {
		presentNode.qualityRate = presentQualityRate;
	}
	
	public void setPresentCP(int presentCP) {
		presentNode.presentCP = presentCP;
	}
	
	public void setCPDecrease(int CPDecrease) {
		presentNode.CPDecrease = CPDecrease;
	}
	
	public void setBuffList(ArrayList<ActiveBuff> buffList) {
		presentNode.buffList = new ArrayList<ActiveBuff>(buffList);
	}
	
	public void setPresentDurability(int presentDurability) {
		presentNode.presentDurability = presentDurability;
	}
	
	public void setDurabilityDecrease(int durabilityDecrease) {
		presentNode.durabilityDecrease = durabilityDecrease;
	}
	
	public void setRound(int round) {
		presentNode.round = round;
	}
	
	public void nodeFinish() {
		craftingHistory.add(presentNode);
	}
	
	public void setBaseInfo( int craftsmanship, int control, int cp, int totalProgress, 
							int totalQuality, int totalDurability, long seed,
							CraftingStatus.Mode m) {
		this.craftsmanship = craftsmanship;
		this.control = control;
		this.cp = cp;
		this.totalProgress = totalProgress;
		this.totalQuality = totalQuality;
		this.totalDurability = totalDurability;
		this.seed = seed;
		this.m = m;
		
		
	}
	
	public void setFinishInfo(boolean usedDebug, boolean hasGCD, 
			double runtime, int value, int sp, int totalRound) {
		this.usedDebug = usedDebug;
		this.hasGCD = hasGCD;
		this.runtime = runtime;
		this.value = value;
		this.sp = sp;
		this.totalRound = totalRound;
		
		finished = true;
		
	}
	
	public void createLogs() {
		logs = new ArrayList<>();
		logs.add("Craftsmanship: " + craftsmanship);
		logs.add("Control: " + control);
		logs.add("CP: " + cp);
		logs.add("Progress: " + totalProgress);
		logs.add("Quality: " + totalQuality);
		logs.add("Durability: " + totalDurability);
		logs.add("Seed: " + seed);
		logs.add("Crafting Status Mode: " + m.toString());
		logs.add("============Base Information=============");
		
		for(Node n: craftingHistory) {
			logs.add("========= Round "+n.round+"==========");
			logs.add("Skill: " + n.s);
			logs.add("Skill Success: " + n.skillSuccess);
			logs.add("Observed: " + n.observed);
			logs.add("Crafting Status: " + n.cs);
			logs.add("Present Progress: " + n.presentProgress);
			logs.add("Progress Increase: " + n.progressIncrease);
			logs.add("Progress Rate: " + n.qualityIncrease);
			logs.add("Present Quality: " + n.presentQuality);
			logs.add("Quality Increase: " + n.qualityIncrease);
			logs.add("Quality Rate: " + n.qualityRate);
			logs.add("Present Durability: " + n.presentDurability);
			logs.add("Durability Decrease: " + n.durabilityDecrease);
			logs.add("Present CP: " + n.presentCP);
			logs.add("CP Decrease: " + n.CPDecrease);
			logs.add("Active buffs:");
			for(ActiveBuff ab: n.buffList) {
				logs.add("    " + ab.getRemaining() + " " + ab.buff.toString());
			}
			logs.add("    ");
		}
		
		if(finished) {
			logs.add("========= Summary =========");
			logs.add("Used Debug: " + usedDebug);
			logs.add("Has GCD: " + hasGCD);
			logs.add("Total time: " + runtime);
			logs.add("Total Rounds: " + totalRound);
			logs.add("Value: " + value);
			logs.add("Skill Points: " + sp);
			logs.add("===========================");
		}
	}
	
	public void saveReplay() {
		ReplayManager rm = new ReplayManager(vm); 
		rm.saveReplay(craftsmanship, control, cp, seed, value, sp, totalRound, craftingHistory);
	}
	
	public ArrayList<String> exportLogs() {
		return logs;
	}
	
	public void setViewManager(ViewManagerPC vm) {
		this.vm = vm;
	}
}
