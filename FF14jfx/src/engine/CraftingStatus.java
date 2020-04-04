package engine;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.paint.Color;

public enum CraftingStatus
{
	Sturdy(  "顽丈      ", Color.BLUE, java.awt.Color.BLUE),
	Centered("安定      ", Color.YELLOW, java.awt.Color.YELLOW),
	Pliant(  "高性能   ", Color.GREEN, java.awt.Color.GREEN),
	HQ(      "高品质   ", Color.RED, java.awt.Color.RED),
	Normal(  "通常      ", Color.WHITE, java.awt.Color.WHITE),
	
	MQ(		 "最高品质", Color.FUCHSIA, new java.awt.Color(Integer.parseInt("FF00FF", 16))),
	LQ(	 	 "低品质   ", Color.BLACK, java.awt.Color.BLACK),
	;
	
	
	
	public static class Node {
		private CraftingStatus cs;
		private double probability;
		
		private Node(CraftingStatus cs, double probability) {
			this.cs = cs;
			this.probability = probability;
		}
		
		public CraftingStatus getCs() {
			return cs;
		}
	}
	
	public enum Mode {
		Expert, Normal, Testing;
	}
	
	public static ArrayList<Node> expertCs;
	public static ArrayList<Node> normalCs;
	
	private static Random r;
	private static Mode mode;
	private static CraftingStatus lastStatus;
	
	private String name;
	private Color fxColor;
	private java.awt.Color awtColor;
	
	static {
		r = new Random();
		
		mode = Mode.Expert;
		lastStatus = Normal;
		
		expertCs = new ArrayList<>();
		normalCs = new ArrayList<>();
		
		expertCs.add(new Node(Sturdy, 0.18));
		expertCs.add(new Node(Centered, 0.15));
		expertCs.add(new Node(Pliant, 0.12));
		expertCs.add(new Node(HQ, 0.10));
		expertCs.add(new Node(Normal, 0.45));
		
		normalCs.add(new Node(Normal, 0.75));
		normalCs.add(new Node(HQ, 0.2));
		normalCs.add(new Node(MQ, 0.035));
		normalCs.add(new Node(LQ, 0.015));
	}
	
	private CraftingStatus(String name, Color fxColor, java.awt.Color awtColor) {
		this.name = name;
		this.fxColor = fxColor;
		this.awtColor = awtColor;
	}
	
	public static CraftingStatus getNextStatus() {
		if(mode == Mode.Expert) {
			return getNextExpertStatus();
		} else if(mode == Mode.Normal) {
			return getNextNormalStatus();
		} else {
			return Normal;
		}
	}
	
	private static CraftingStatus getNextExpertStatus() {
		double d = r.nextDouble();
		
		double temp = 0;
		for(Node n: expertCs) {
			temp += n.probability;
			if(temp >= d) {return n.cs;}
		}
		return expertCs.get(expertCs.size() - 1).cs;
	}
	
	private static CraftingStatus getNextNormalStatus() {
		if(lastStatus == HQ) {
			lastStatus = Normal;
		} else if(lastStatus == MQ) {
			lastStatus = LQ;
		} else if(lastStatus == LQ) {
			lastStatus = Normal;
		} else {
			double d = r.nextDouble();
			double temp = 0;
			for(Node n: normalCs) {
				temp += n.probability;
				if(temp >= d) {
					lastStatus = n.cs;
					return lastStatus;
				}
			}
			lastStatus = normalCs.get(normalCs.size() - 1).cs;
		}
		
		return lastStatus;
	}
	
	public static void setRandom(Random ra) {
		r = ra;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Color getFxColor()
	{
		return fxColor;
	}
	
	public java.awt.Color getAwtColor()
	{
		return awtColor;
	}
	
	public static void setMode(Mode m) {
		mode = m;
	}
	
	public static ArrayList<Node> getList() {
		if(mode == Mode.Expert) {
			return expertCs;
		} else if(mode == Mode.Normal) {
			return normalCs;
		} else {
			ArrayList<Node> al = new ArrayList<CraftingStatus.Node>();
			al.add(new Node(Normal, 1.0));
			return al;
		}
	}
}

