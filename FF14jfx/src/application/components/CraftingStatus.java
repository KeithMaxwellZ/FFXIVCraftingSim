package application.components;

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.paint.Color;

public enum CraftingStatus
{
	Sturdy(  "顽丈   ", Color.BLUE),
	Centered("安定   ", Color.YELLOW),
	Pliant(  "高性能", Color.GREEN),
	HQ(      "高品质", Color.RED),
	Normal(  "通常   ", Color.WHITE);
	
	static class Node {
		CraftingStatus cs;
		double probability;
		
		private Node(CraftingStatus cs, double probability) {
			this.cs = cs;
			this.probability = probability;

		}
	}
	
	
	static ArrayList<Node> probList;
	private String name;
	private Color color;
	static Random r;
	
	static {
		r = new Random();
		probList = new ArrayList<CraftingStatus.Node>();
		initProb();
	}
	
	private CraftingStatus(String name, Color color) {
		this.setName(name);
		this.setColor(color);
	}
	
	private static void initProb() {
		probList.add(new Node(Sturdy, 0.18));
		probList.add(new Node(Centered, 0.15));
		probList.add(new Node(Pliant, 0.12));
		probList.add(new Node(HQ, 0.10));
		probList.add(new Node(Normal, 0.45));
	}
	
	public static CraftingStatus getNextStatus() {
		double d = r.nextDouble();
		
		double temp = 0;
		for(Node n: probList) {
			temp += n.probability;
			if(temp >= d) {return n.cs;}
		}
		return probList.get(probList.size() - 1).cs;
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

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}
}

