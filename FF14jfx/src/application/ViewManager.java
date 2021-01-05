package application;

import java.util.ArrayList;

import application.components.Timer;
import engine.Engine;
import javafx.stage.Stage;
import skills.Skill;

public abstract class ViewManager 
{
	protected int craftsmanship = 2758;
	protected int control = 2907;
	protected int cp = 657;
	protected int totalDurability = 50;						// Durability
	protected int totalProgress = 11126;					// Total progress
	protected int totalQuality = 82400;					// Total quality
	protected int rCraftsmanship = 2620;			// Recommended craftsmanship
	protected int rControl = 2540;				// Recommended control
	protected double progressDifference = 0.8;	// Level difference index of progress
	protected double qualityDifference = 0.6;		// Level difference index of quality
	protected long seed = 0;
	protected Timer tm;		
	
	protected boolean hasGCD = true;				// GCD mode
	protected boolean usedDebug = false;

	protected Skill lastSkill = null;				// Store the last skill used
	
	protected ArrayList<Skill> progressSkills;	// ArrayLists that stores different types of
	protected ArrayList<Skill> qualitySkills;		// skills, I defined the categories
	protected ArrayList<Skill> buffSkills;
	protected ArrayList<Skill> recoverySkills;
	protected ArrayList<Skill> otherSkills;
	
	protected Engine engine;						// The engine of the program

	
	// == getters and setters ==
	public Timer getTimer() {
		return tm;
	}
	
	public boolean getHasGCD() {
		return hasGCD;
	}
	
	public void setHasGCD(boolean b) {
		hasGCD = b;
	}

	public int getrCraftsmanship() {
		return rCraftsmanship;
	}

	public void setrCraftsmanship(int rCraftsmanship) {
		this.rCraftsmanship = rCraftsmanship;
	}

	public int getrControl() {
		return rControl;
	}

	public void setrControl(int rControl) {
		this.rControl = rControl;
	}
	
	public int getCraftsmanship() {
		return craftsmanship;
	}


	public int getControl() {
		return control;
	}

	public void setCraftsmanship(int craftsmanship) {
		this.craftsmanship = craftsmanship;
	}
	
	public void setControl(int control) {
		this.control = control;
	}
	
	public void setCP(int cp) {
		this.cp = cp;
	}
	
	public int getCP() {
		return cp;
	}

	public double getProgressDifference() {
		return progressDifference;
	}

	public void setProgressDifference(double progressDifference) {
		this.progressDifference = progressDifference;
	}

	public double getQualityDifference() {
		return qualityDifference;
	}

	public void setQualityDifference(double qualityDifference) {
		this.qualityDifference = qualityDifference;
	}

	public Skill getLastSkill() {
		return lastSkill;
	}

	public void setLastSkill(Skill lastSkill) {
		this.lastSkill = lastSkill;
	}
	
	public long getSeed() {
		return seed;
	}
	
	public void setSeed(long seed) {
		this.seed = seed;
	}

	public Engine getEngine()
	{
		return engine;
	}
	
	public void setUsedDebug(boolean b) {
		this.usedDebug = b;
	}
	
	// == abstract methods ==
	public abstract Stage getStage();
}
