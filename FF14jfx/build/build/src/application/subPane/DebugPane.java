package application.subPane;

import java.util.ArrayList;

import application.ViewManagerPC;
import engine.CraftingStatus;
import engine.EngineStatus;
import engine.CraftingStatus.Node;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class DebugPane
{
	public static final double BOX_WIDTH = 200.0;
	public static final double BOX_HEIGHT = 200.0;
	public static final double BUTTON_HEIGHT = 24;
	public static final double SIDE_MARGIN = 20.0;
			
	private Stage stage;
	private Scene scene;
	private GridPane mainPane;
	
	private Button sturdyB;
	private Button centeredB;
	private Button pliantB;
	private Button HQB;
	private Button NormalB;
	
	ViewManagerPC vm;
	
	private ArrayList<Button> buttons;
	
	public DebugPane(ViewManagerPC vm) {
		this.vm = vm;
		
		buttons = new ArrayList<>();
		
		sturdyB = new Button("");
		centeredB = new Button("");
		pliantB = new Button("");
		HQB = new Button("");
		NormalB = new Button("");
		
		buttons.add(sturdyB);
		buttons.add(centeredB);
		buttons.add(pliantB);
		buttons.add(HQB);
		buttons.add(NormalB);
		
		stage = new Stage();
		mainPane = new GridPane();
		scene = new Scene(mainPane, BOX_WIDTH, BOX_HEIGHT);
		stage.setScene(scene);
		stage.setTitle("����ģʽ");
		init();
		
	}
	
	private void init() {
		stage.setX(vm.getStage().getX() + vm.getStage().getWidth() + 20.0);
		stage.setY(vm.getStage().getY());
		
		int i = 0;
		GridPane buttonContainer = new GridPane();
		
		buttonContainer.setVgap(10.0);
		
		for(Node n: CraftingStatus.getList()) {
			Button b = buttons.get(i);
			
			b.setPrefWidth(BOX_WIDTH - SIDE_MARGIN);
			b.setPrefHeight(BUTTON_HEIGHT);
			
			b.setText(n.getCs().getName());
			b.setTextFill(n.getCs().getFxColor());
			b.setOnMouseClicked(e -> {
				if(vm.getEngine().getEngineStatus() == EngineStatus.Crafting) {
					vm.setUsedDebug(true);
					vm.getEngine().setCraftingStatus(n.getCs());
					vm.updateStatus();
				}
			});
			
			buttonContainer.add(b, 0, i);
			i++;
		}
		
		mainPane.add(buttonContainer, 0, 0);
		GridPane.setMargin(buttonContainer, new Insets(SIDE_MARGIN));
	}
	
	public void display() {
		stage.show();
	}
	
	public void close() {
		stage.close();
	}
}
