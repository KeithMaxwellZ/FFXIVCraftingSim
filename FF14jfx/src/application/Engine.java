package application;

import java.util.ArrayList;
import java.util.List;

import application.components.CraftingStatus;
import application.components.Timer;
import application.subPane.CraftingHistoryPane;
import exceptions.CraftingException;
import exceptions.ExceptionStatus;
import skills.ActiveBuff;
import skills.Buff;
import skills.BuffSkill;
import skills.PQSkill;
import skills.Skill;
import skills.SpecialSkills;

public class Engine
{		
	private double progressDifference = 0.8;
	private double qualityDifference = 0.6;
	
	private int craftsmanship;
	private int control;
	private int buffControl;
	private int recCraftsmanship;
	private int recControl;
	
	private int baseProgEff;
	private int baseQltyEff;
	private int round;
	
	private int csCount;
	
	private ArrayList<String> logs;
	
	private CraftingStatus cs;
	
	private boolean progIncreased;
	private boolean qltyIncreased;
	private boolean working;
	
	private Timer timer;
	private CraftingHistoryPane ch;
	
	protected ArrayList<ActiveBuff> activeBuffs;
	
	protected int totalDurability;
	protected int presentDurability;
	protected int totalProgress;
	protected int presentProgress;
	protected int totalQuality;
	protected int presentQuality;
	protected int totalCP;
	protected int presentCP;
	protected int innerQuietLvl;
	
	protected boolean observed;
	protected boolean success = true;
	
	
	public Engine(int craftsmanship, int control, int totalCP, int totalDurability, 
				int totalProgress, int totalQUality, int recCraftsmanship, int recControl,
				double porgressDifference, double qualityDifference, CraftingHistoryPane ch) {
		this.craftsmanship = craftsmanship; 
		this.control = control;
		this.totalCP = totalCP;
		this.totalDurability = totalDurability;
		this.totalProgress = totalProgress;
		this.totalQuality = totalQUality;
		this.recCraftsmanship = recCraftsmanship;
		this.recControl = recControl;
		this.progressDifference = porgressDifference;
		this.qualityDifference = qualityDifference;
		this.ch = ch;
		
		activeBuffs = new ArrayList<>();
		logs = new ArrayList<>();
		
		presentDurability = totalDurability;
		presentProgress = 0;
		presentQuality = 0;
		csCount = 0;
		presentCP = totalCP;
		observed = false;
		working = true;
		progIncreased = false;
		qltyIncreased = false;
		
		timer = new Timer();
		timer.startTimer();
		
		round = 0;
		
		cs = CraftingStatus.Normal;
		
		calcBaseProg();
		calcBaseQlty();
		
		setEnumEngine();
		
		addToLogs("Craftsmanship: " + craftsmanship);
		addToLogs("Control: " + control);
		addToLogs("CP: " + totalCP);
		addToLogs("Progress: " + totalProgress);
		addToLogs("Quality: " + totalQUality);
		addToLogs("=========================");
	}
	
	public void addActiveBuff(Buff b, int last) {
		for(ActiveBuff ab: activeBuffs) {
			if(ab.buff==b) {
				ab.setRemaining(last);
				return;
			}
		}
		
		activeBuffs.add(new ActiveBuff(b, last));
	}
	
	private void setEnumEngine()
	{
		for(Skill sk: PQSkill.values()) {
			sk.setEngine(this);
		}
		
		for(Skill sk: BuffSkill.values()) {
			sk.setEngine(this);
		}
		
		for(Skill sk: SpecialSkills.values()) {
			sk.setEngine(this);
		}
	}
	
	private void successfulUse(Skill sk) {
		int durDec = (int)Math.round((double)sk.getDurCost() / 
									  (buffExist(Buff.waste_not) ? 2 : 1) / 
									  (cs == CraftingStatus.Sturdy ? 2 : 1));
		int cpDec = (int)Math.round((double)sk.getCPCost() / (cs == CraftingStatus.Pliant ? 2 : 1));

		addToLogs("Duration Cost: " + durDec)
		;

		presentDurability -= durDec;
		presentCP -= cpDec; 
		round++;
	}
	
	public void beginning(Skill sk) {
		success = false;
		progIncreased = false;
		qltyIncreased = false;
		addToLogs(" ");
		addToLogs("===Round " + round + " ===");
		addToLogs("Crafting Status: " + cs.toString());
		addToLogs("Skill name: " + (sk).toString());
		addToLogs("Observed?: " + observed);
	}
	
	public void useSkill(Skill sk) throws CraftingException { 
		if(presentCP < Math.round((double)sk.getCPCost() / (cs == CraftingStatus.Pliant ? 2 : 1))) {
			throw new CraftingException(ExceptionStatus.No_Enough_CP);
		} 
		if(sk instanceof PQSkill) {
			usePQSkill((PQSkill)sk);
		} else if (sk instanceof BuffSkill) {
			useBuffSkill((BuffSkill)sk);
		} else if (sk instanceof SpecialSkills) {
			useSpecialSkills((SpecialSkills)sk);
		}
	}
	
	private void usePQSkill(PQSkill sk) throws CraftingException {
		if(sk == PQSkill.Intensive_Synthesis || sk == PQSkill.Precise_Touch) {
			if(cs != CraftingStatus.HQ) {
				throw new CraftingException(ExceptionStatus.Not_HQ);
			}
		}
		if(sk == PQSkill.Prudent_Touch && buffExist(Buff.waste_not)) {
			throw new CraftingException(ExceptionStatus.Waste_Not_Exist);
		}
		
		beginning(sk);

		if(sk.isSuccess()) {
			success = true;
			forwardProgress(sk);
		} else {
			if(sk == PQSkill.Patient_Touch) {
				innerQuietLvl =  (innerQuietLvl + 1) / 2;
				innerQuietLvl = (innerQuietLvl == 0 ? 1 : innerQuietLvl);
				setBuffInnerQuiet(innerQuietLvl);
			}
		}
		
		addToLogs("Success? : " + success);
		
		observed = false;
		finalizeRound(sk);
	}
	
	private void useBuffSkill(BuffSkill sk) throws CraftingException {
		if(sk == BuffSkill.Final_Appraisal) {
			beginning(sk);
			success = true;
			presentCP--;
			sk.createBuff();
			ch.addToQueue(sk, cs, success);
			return;
		}
		
		if(sk == BuffSkill.Inner_Quiet) {
			if(innerQuietLvl > 0) {
				throw new CraftingException(ExceptionStatus.Inner_Quiet_Exists);
			}
		}
		if(sk == BuffSkill.Muscle_Memory || sk == BuffSkill.Reflect) {
			if(round != 0) {
				throw new CraftingException(ExceptionStatus.Not_Turn_One);
			}
		}
		
		beginning(sk);

		if(sk.isSuccess()) {
			success = true;
			forwardProgress(sk);
		}
		
		observed = false;
		
		for(ActiveBuff ab: activeBuffs) {
			if(ab.buff == sk.getBuff()) {
				activeBuffs.remove(ab);
				break;
			}
		}

		finalizeRound(sk);
		sk.createBuff();
	}
	
	public void useSpecialSkills(SpecialSkills sk) throws CraftingException {
		if(innerQuietLvl <= 1 && sk == SpecialSkills.Byregots_Blessing) {
			throw new CraftingException(ExceptionStatus.No_Inner_Quiet); 
		}
		if(sk == SpecialSkills.Careful_Observation) {
			if(csCount >= 3) {
				throw new CraftingException(ExceptionStatus.Maximun_Reached);
			}
			csCount++;
			beginning(sk);
			success = true;
			cs = CraftingStatus.getNextStatus();
			return;
		}
		beginning(sk);

		observed = false;

		if(sk.isSuccess()) {
			success = true;
			forwardProgress(sk);
		}
		
		sk.execute();
		finalizeRound(sk);
	}
	
	private void forwardProgress(Skill sk) {
		double tempProgressRate = sk.getActualProgressRate();
		double tempQualityRate = sk.getActualQualityRate();
		
		int tempProgressIncrease = (int)Math.floor(baseProgEff * tempProgressRate);
		int tempQualityIncrease  = (int)Math.floor(Math.floor(baseQltyEff * 
								   (cs == CraftingStatus.HQ ? 1.5 : 1)) * tempQualityRate);
		addToLogs("Progress Increase: " + tempProgressIncrease + " rate: " + tempProgressRate);
		addToLogs("Quality Increase: " + tempQualityIncrease + " rate: " + tempQualityRate);
		
		progIncreased = tempProgressIncrease > 0;
		qltyIncreased = tempQualityIncrease > 0;
		
		presentProgress += tempProgressIncrease;
		presentQuality += tempQualityIncrease;
		
		if(presentProgress > totalProgress) {
			presentProgress = totalProgress;
		}
		if(presentQuality > totalQuality) {
			presentQuality = totalQuality;
		}
		
		if(presentProgress >= totalProgress && buffExist(Buff.final_appraisal)) {
			presentProgress = totalProgress - 1;
			for(ActiveBuff ab: activeBuffs) {
				if(ab.buff == Buff.final_appraisal) {
					activeBuffs.remove(ab);
					break;
				}
			}
		}
		
		if(sk == PQSkill.Patient_Touch) {
			innerQuietLvl *= 2;
			setBuffInnerQuiet(innerQuietLvl);
		} else if(tempQualityIncrease > 0 && buffExist(Buff.inner_quiet)) {
			innerQuietLvl += 1;
			setBuffInnerQuiet(innerQuietLvl);
			if(sk == PQSkill.Preparatory_Touch || sk == PQSkill.Precise_Touch) {
				innerQuietLvl += 1;
				setBuffInnerQuiet(innerQuietLvl);
			}
		} 
		
	}
	
	private void finalizeRound(Skill sk) throws CraftingException {
		ch.addToQueue(sk, cs, success);
		
		successfulUse(sk);
				
		finishCheck();
		updateBuff();
		updateStatus();
		calcBaseQlty();
	}
	
	private void updateBuff() {
		for(int i = 0; i < activeBuffs.size(); i++)
		{
			if(activeBuffs.get(i).buff == Buff.manipulation) {
				presentDurability += 5;
				if(presentDurability > totalDurability) {
					presentDurability = totalDurability;
				}
			}
			activeBuffs.get(i).decrease();
			if(activeBuffs.get(i).getRemaining() == 0) {
				activeBuffs.remove(i);
				i--;
			} else if(activeBuffs.get(i).buff.isOnce() && (activeBuffs.get(i).buff.getProgressBuff() > 0) && progIncreased)
			{
				activeBuffs.remove(i);
				i--;
			} else if(activeBuffs.get(i).buff.isOnce() && activeBuffs.get(i).buff.getQualityBuff() > 0 && qltyIncreased)
			{
				activeBuffs.remove(i);
				i--;
			}
		}
	}
	
	private void finishCheck() throws CraftingException {
		if(presentProgress >= totalProgress) {
			timer.stopTimer();
			throw new CraftingException(ExceptionStatus.Craft_Success);
		} else if(presentDurability <= 0) {
			timer.stopTimer();
			throw new CraftingException(ExceptionStatus.Craft_Failed);
		} else {
			return; 
		}
	}
	
	private void updateStatus() {
		cs = CraftingStatus.getNextStatus();
	}
	
	private boolean buffExist(Buff b) {
		for(ActiveBuff ab: activeBuffs) {
			if(ab.buff == b) return true;
		}
		return false;
	}
	
	private void calcBaseProg() {
		baseProgEff = (int)Math.floor(progressDifference * (0.21 * (double)craftsmanship + 2) * 
				(10000.0 + (double)craftsmanship)/(10000.0 + (double)recCraftsmanship));
	}
	
	private void calcBaseQlty() {
		buffControl = control;
		if(innerQuietLvl > 1) {
			buffControl = (int)Math.floor((double)control * (1.0 + 0.2 * (double)(innerQuietLvl - 1)));
		}
		baseQltyEff = (int)Math.floor(qualityDifference * (0.35 * (double)buffControl + 35) * 
				(10000.0 + (double)buffControl)/(10000.0 + (double)recControl));
	}
	
	public int SPCalc() {
		final int lv1 = 4500;
		final int lv2 = 5000;
		final int lv3 = 6000;
		
		if(presentQuality/10 < lv1) {
			return 0;
		} else if (presentQuality/10 < lv2) {
			return (int)Math.floor(175 + ((double)presentQuality/10 - lv1) * 0.1);
		} else if (presentQuality/10 < lv3) {
			return (int)Math.floor(370 + ((double)presentQuality/10 - lv2) * 0.25);
		} else {
			return (int)Math.floor(800 + ((double)presentQuality/10 - lv3) * 0.9);
		}
	}
	
	public void addToLogs(String s) {
		logs.add(s);
	}
	
	public void setBuffInnerQuiet(int val) {
		if(innerQuietLvl >= 11) {
			innerQuietLvl = 11;
		}
		for(ActiveBuff ab: activeBuffs) {
			if(ab.buff == Buff.inner_quiet) {
				ab.setRemaining(innerQuietLvl);
				return;
			}
		}
	}
	
	public List<ActiveBuff> getActiveBuffs() {
		return activeBuffs;
	}

	public int getTotalDurability()
	{
		return totalDurability;
	}
	
	public int getPresentDurability()
	{
		return presentDurability;
	}
	
	public void setPresentDurability(int i) {
		presentDurability = i;
	}

	public int getTotalProgress()
	{
		return totalProgress;
	}

	public int getPresentProgress()
	{
		return presentProgress;
	}

	public int getTotalQuality()
	{
		return totalQuality;
	}

	public int getPresentQuality()
	{
		return presentQuality;
	}

	public int getTotalCP()
	{
		return totalCP;
	}

	public int getPresentCP()
	{
		return presentCP;
	}
	
	public void setPresentCP(int i) {
		presentCP = i;
	}
	
	public boolean isObserved() {
		return observed;
	}
	
	public void setObserved(boolean b) {
		observed = b;
	}
	
	public int getBaseProgEff() {
		return baseProgEff;
	}
	
	public int getBaseQltyEff() {
		return baseQltyEff;
	}
	
	public int getInnerQuiet() {
		return innerQuietLvl;
	}
	
	public void setInnerQuiet(int i) {
		innerQuietLvl = i;
	}
	
	public CraftingStatus getCraftingStatus() {
		return cs;
	}
	
	public boolean isWorking() {
		return working;
	}
	
	public void setWorking(boolean b) {
		working = b;
	}
	
	public int getRound() {
		return round + 1;
	}
	
	public ArrayList<String> getLogs() {
		return new ArrayList<String>(logs);
	}
	
	public double getRuntime() {
		return timer.getTime();
	}
}
