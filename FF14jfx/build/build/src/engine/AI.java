package engine;

import java.util.ArrayList;
import java.util.Iterator;

import skills.ActiveBuff;
import skills.Buff;
import skills.BuffSkill;
import skills.PQSkill;
import skills.Skill;
import skills.SpecialSkills;

public class AI
{
	private static ArrayList<ArrayList<Skill>> FS;
	
	Engine e;
	
	int finalizeSequence;
	private Iterator<Skill> FSIterator;
	
	static {
		FS = new ArrayList<ArrayList<Skill>>();
		
		ArrayList<Skill> temp = new ArrayList<Skill>();
		temp.add(BuffSkill.Great_Strides);
		temp.add(BuffSkill.Innovation);
		temp.add(SpecialSkills.Observe);
		temp.add(PQSkill.Focused_Touch);
		temp.add(BuffSkill.Great_Strides);
		temp.add(SpecialSkills.Byregots_Blessing);
		temp.add(PQSkill.Basic_Synthesis);
		
		FS.add(new ArrayList<Skill>(temp));
		
		temp = new ArrayList<Skill>();
		temp.add(BuffSkill.Great_Strides);
		temp.add(BuffSkill.Innovation);
		temp.add(PQSkill.Preparatory_Touch);
		temp.add(PQSkill.Prudent_Touch);
		temp.add(BuffSkill.Great_Strides);
		temp.add(SpecialSkills.Byregots_Blessing);
		temp.add(PQSkill.Basic_Synthesis);
		
		FS.add(new ArrayList<Skill>(temp));
		
		temp = new ArrayList<Skill>();
		temp.add(BuffSkill.Great_Strides);
		temp.add(BuffSkill.Innovation);
		temp.add(PQSkill.Preparatory_Touch);
		temp.add(PQSkill.Prudent_Touch);
		temp.add(BuffSkill.Great_Strides);
		temp.add(SpecialSkills.Byregots_Blessing);
		temp.add(PQSkill.Basic_Synthesis);
		
		FS.add(new ArrayList<Skill>(temp));
	}
	
	public AI(Engine e) {
		this.e = e;
		finalizeSequence = 0;
		FSIterator = null;
	}
	
	public Skill RecSkill() {
		if(finalizeSequence == 0) {
			finalizeCheck();
		}
		
		if(finalizeSequence != 0) {
			if(FSIterator == null) {
				FSIterator = FS.get(finalizeSequence).iterator();
			}
			
			if(FSIterator.hasNext()) {
				return FSIterator.next();
			}
			else {
				return PQSkill.Basic_Synthesis;
			}
		}
		
		if(e.getRound() == 0) {
			return BuffSkill.Muscle_Memory;
		}
		else if(e.getRound() == 1) {
			return BuffSkill.Veneration;
		}
		
		if(e.innerQuietLvl == 0 && (e.cs == CraftingStatus.Normal || 
								  e.cs == CraftingStatus.Sturdy ||
								  e.cs == CraftingStatus.Centered)) {
			return BuffSkill.Inner_Quiet;
		}
		
		if(e.cs == CraftingStatus.Normal) {
			boolean hasManipulation = e.buffExist(Buff.manipulation);
			boolean hasWasteNot = e.buffExist(Buff.waste_not);
			
			if(e.presentDurability >= 41 && e.presentDurability <= 60) {
				return PQSkill.Hasty_Touch;
			}
			else if(e.presentDurability >= 21 && e.presentDurability <= 40) {
				if(hasManipulation || hasWasteNot) {
					return PQSkill.Hasty_Touch;
				}
				else {
					return BuffSkill.Manipulation;
				}
			}
			else if(e.presentDurability >= 11 && e.presentDurability <= 20) {
				if(hasManipulation) {
					return SpecialSkills.Masters_Mend;
				}
				else if(hasWasteNot) {
					return PQSkill.Hasty_Touch;
				}
				else {
					return BuffSkill.Manipulation;
				}
			}
			else if(e.presentDurability >= 6 && e.presentDurability <= 10) {
				if(hasWasteNot) {
					return PQSkill.Hasty_Touch;
				}
				else {
					return SpecialSkills.Masters_Mend;
				}
			}
			else {
				return SpecialSkills.Masters_Mend;
			}
		}
		else if(e.cs == CraftingStatus.Pliant) {
			boolean hasManipulation = e.buffExist(Buff.manipulation);
			boolean hasWasteNot = e.buffExist(Buff.waste_not);
			
			int manipulationRemaining = 0;
			if(hasManipulation) {
				for(ActiveBuff ab: e.activeBuffs) {
					if(ab.buff == Buff.manipulation) {
						manipulationRemaining = ab.getRemaining();
					}
				}
			}
			
			if(e.presentDurability >= 56 && e.presentDurability <= 60) {
				if(hasManipulation || hasWasteNot) {
					return PQSkill.Preparatory_Touch;
				}
				else {
					return BuffSkill.Manipulation;
				}
			}
			else if(e.presentDurability >= 26 && e.presentDurability <= 55) {
				if(hasWasteNot) {
					if(manipulationRemaining > 1) {
						return PQSkill.Preparatory_Touch;
					}
					else{
						return BuffSkill.Manipulation;
					}
				}
				else if(hasManipulation) {
					return BuffSkill.Waste_Not_II;
				}
				else {
					return BuffSkill.Manipulation;
				}
			}
			else if(e.presentDurability >= 11 && e.presentDurability <= 25) {
				if(hasWasteNot) {
					if(manipulationRemaining > 1) {
						return PQSkill.Preparatory_Touch;
					}
					else{
						return BuffSkill.Manipulation;
					}
				}
				else if(hasManipulation) {
					return SpecialSkills.Masters_Mend;
				}
				else {
					return BuffSkill.Manipulation;
				}
			}
			else{
				return SpecialSkills.Masters_Mend;
			}
		}
		else if(e.cs == CraftingStatus.Centered) {
//			boolean hasManipulation = e.buffExist(Buff.manipulation);
			boolean hasWasteNot = e.buffExist(Buff.waste_not);
						
			if(e.presentDurability >= 11 && e.presentDurability <= 60) {
				if(e.innerQuietLvl <= 5) {
					return PQSkill.Patient_Touch;
				} else {
					return PQSkill.Hasty_Touch;
				}
			}
			else if(e.presentDurability >= 6 && e.presentDurability <= 10) {
				if(hasWasteNot) {
					if(e.innerQuietLvl <= 5) {
						return PQSkill.Patient_Touch;
					} else {
						return PQSkill.Hasty_Touch;
					}
				}
				else {
					return SpecialSkills.Masters_Mend;
				}
			}
			else{
				return SpecialSkills.Masters_Mend;
			}
		}
		else if(e.cs == CraftingStatus.Sturdy) {
//			boolean hasManipulation = e.buffExist(Buff.manipulation);
			boolean hasWasteNot = e.buffExist(Buff.waste_not);
						
			if(e.presentDurability >= 56 && e.presentDurability <= 60) {
				if(hasWasteNot) {
					return PQSkill.Preparatory_Touch;
				} else {
					return PQSkill.Hasty_Touch;
				}
			}
			else if(e.presentDurability >= 6 && e.presentDurability <= 55) {
				if(hasWasteNot) {
					if(e.presentDurability % 5 == 0) {
						return PQSkill.Preparatory_Touch;
					} else {
						return PQSkill.Hasty_Touch;
					}
				}
				else {
					return PQSkill.Hasty_Touch;
				}
			}
			else if(e.presentDurability >= 4 && e.presentDurability <= 5) {
				if(hasWasteNot) {
					return PQSkill.Hasty_Touch;
				}
				else {
					return PQSkill.Prudent_Touch;
				}
			}
			else{
				return SpecialSkills.Masters_Mend;
			}
		}
		else if(e.cs == CraftingStatus.HQ) {
			boolean hasManipulation = e.buffExist(Buff.manipulation);
			boolean hasWasteNot = e.buffExist(Buff.waste_not);
						
			if(e.presentDurability >= 56 && e.presentDurability <= 60) {
				if(hasManipulation || hasWasteNot) {
					return PQSkill.Precise_Touch;
				}
				else {
					return SpecialSkills.Tricks_of_the_Trade;
				}
			}
			else if(e.presentDurability >= 6 && e.presentDurability <= 55) {
				if(hasWasteNot) {
					return PQSkill.Precise_Touch;
				}
				else {
					return SpecialSkills.Tricks_of_the_Trade;
				}
			}

			else{
				return SpecialSkills.Tricks_of_the_Trade;
			}
		}
		return null;
	}
	
	private void finalizeCheck()
	{
		int qualityRemaining = 60000 - e.presentProgress;
		int progressRemaining = e.totalProgress - e.presentProgress;
		if(qualityRemaining <= (double)e.getBaseQltyEff() * 15.5 && e.presentCP >= 186 && e.presentDurability >= 51) {
			if(progressRemaining > (double)e.getBaseProgEff() * 1.2 && progressRemaining <= (double)e.getBaseProgEff() * 1.5 && e.presentCP >= 193) {
				finalizeSequence = 3;
			}
			else if(progressRemaining <= e.getBaseProgEff()) {
				finalizeSequence = 3;
			}
		}
		else if(qualityRemaining <= (double)e.getBaseQltyEff() * 14.0 && e.presentCP >= 171 && e.presentDurability >= 36) {
			if(progressRemaining > (double)e.getBaseProgEff() * 1.2 && progressRemaining <= (double)e.getBaseProgEff() * 1.5 && e.presentCP >= 178) {
				finalizeSequence = 2;
			}
			else if(progressRemaining <= e.getBaseProgEff()) {
				finalizeSequence = 2;
			}
		}
		else if(qualityRemaining <= (double)e.getBaseQltyEff() * 11.25 && e.presentCP >= 131 && e.presentDurability >= 21) {
			if(progressRemaining > (double)e.getBaseProgEff() * 1.2 && progressRemaining <= (double)e.getBaseProgEff() * 1.5 && e.presentCP >= 138) {
				finalizeSequence = 1;
			}
			else if(progressRemaining <= e.getBaseProgEff()) {
				finalizeSequence = 1;
			}
		}
	}
}
