package application;


import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import skills.Skill;

public class CraftingHistory
{
	public static final double BOX_WIDTH = 300.0;
	public static final double BOX_HEIGHT = 690.0;

	private Stage historyStage;
	private Scene scene;
	private ScrollPane mainHistoryPane;
	private GridPane queue;
	private int presentLoc;
	
	public CraftingHistory()
	{
		mainHistoryPane = new ScrollPane();
		historyStage = new Stage();
		scene = new Scene(mainHistoryPane, BOX_WIDTH, BOX_HEIGHT);
		
		historyStage.setScene(scene);
		historyStage.setTitle("ÀúÊ·¼ÇÂ¼");
		
		queue = new GridPane();
		mainHistoryPane.setPrefHeight(BOX_HEIGHT);
		mainHistoryPane.setContent(queue);;
		mainHistoryPane.setBackground(new Background(
				new BackgroundFill(Color.DARKGRAY, null, null)));
		
		historyStage.setX(100.0);
		historyStage.setY(100.0);
		
		queue.setPrefWidth(BOX_WIDTH);
		queue.setPrefHeight(BOX_HEIGHT);
		queue.setHgap(10);
		queue.setVgap(10);
	}
	
	public void addToQueue(Skill sk, CraftingStatus cs, boolean success) {
		System.out.println("asdasd");
		GridPane gp = new GridPane();
		ImageView iv = new ImageView(new Image(sk.getAddress(), true));
		Text csT = new Text(cs.name);
		Text successT = new Text(success ? "Success" : "Failed");
		Circle statusDisp = new Circle(10.0, cs.color);
		
		gp.add(iv, 0, 0);
		gp.add(csT, 1, 0);
		gp.add(statusDisp, 2, 0);
		gp.add(successT, 3, 0);
		
		gp.setHgap(10.0);
		
		csT.setFill(cs.color);
		successT.setFill(success ? Color.GREEN : Color.RED);
		
		queue.add(gp, 0, presentLoc);
		presentLoc++;
	}
	
	public void display() {
		historyStage.show();
	}
	
	public void destory() {
		historyStage.close();
	}
}



