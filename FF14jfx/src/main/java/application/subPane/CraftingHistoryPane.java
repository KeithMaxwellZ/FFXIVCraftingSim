package application.subPane;


import java.util.ArrayList;

import application.ViewManagerPC;
import engine.CraftingStatus;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import skills.Skill;

public class CraftingHistoryPane
{
	public static final double BOX_WIDTH = 300.0;
	public static final double BOX_HEIGHT = 690.0;

	private Stage historyStage;
	private Scene scene;
	private ScrollPane mainHistoryPane;
	private GridPane queue;
	private int presentLoc;
	
	private ArrayList<Skill> skillQueue;
	
	private ViewManagerPC vm;
	
	public CraftingHistoryPane(ViewManagerPC vm)
	{
		this.vm = vm;
		
		skillQueue = new ArrayList<>();
		mainHistoryPane = new ScrollPane();
		historyStage = new Stage();
		scene = new Scene(mainHistoryPane, BOX_WIDTH, BOX_HEIGHT);
		queue = new GridPane();
		
		historyStage.setScene(scene);
		historyStage.setTitle("ÀúÊ·¼ÇÂ¼");
		
		mainHistoryPane.setPrefHeight(BOX_HEIGHT);
		mainHistoryPane.setContent(queue);
		
		queue.setBackground(new Background(
				new BackgroundFill(Color.DARKGRAY, null, null)));
		
		historyStage.setX(vm.getStage().getX() - BOX_WIDTH - 10.0);
		historyStage.setY(vm.getStage().getY());
		
		queue.setPrefWidth(BOX_WIDTH);
		queue.setPrefHeight(BOX_HEIGHT);
		queue.setHgap(10);
		queue.setVgap(3);
	}
	
	public void addToQueue(Skill sk, CraftingStatus cs, boolean success) {
		GridPane gp = new GridPane();
		ImageView iv = new ImageView(new Image(sk.getAddress(), true));
		Text csT = new Text(cs.getName());
		Text successT = new Text(success ? "Success" : "Failed");
		Text srT = new Text(Double.toString(sk.getSuccessRate(cs)));
		Circle statusDisp = new Circle(10.0, cs.getFxColor());
		
		skillQueue.add(sk);
		
		gp.add(iv, 0, 0);
		gp.add(csT, 1, 0);
		gp.add(statusDisp, 2, 0);
		gp.add(successT, 3, 0);
		gp.add(srT, 4, 0);
		
		gp.setHgap(10.0);
		
		csT.setFill(cs.getFxColor());
		successT.setFill(success ? Color.GREEN : Color.RED);
		
		queue.add(gp, 0, presentLoc);
		GridPane.setMargin(gp, new Insets(15.0, 0, 0, 15.0));
		presentLoc++;
	}
	
	public void display() {
		historyStage.show();
		vm.getStage().requestFocus();
	}
	
	public void destory() {
		historyStage.close();
	}
	
	public void close() {
		historyStage.close();
	}
}



