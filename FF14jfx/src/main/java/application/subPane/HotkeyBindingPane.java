package application.subPane;

import application.ViewManager;
import application.components.SkillIcon;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HotkeyBindingPane
{
	public static final double BOX_WIDTH = 200.0;
	public static final double BOX_HEIGHT = 100.0;
	
	private Stage mainStage;
	private Scene mainScene;
	private AnchorPane mainPane;
	
	private Text t;
	
	private String key;
	private String mod;
	
	private ViewManager vm;
	
	public HotkeyBindingPane(ViewManager vm, EditModePane emp) {
		this.vm = vm;
		
		mainStage = new Stage();
		mainPane = new AnchorPane();
		mainScene = new Scene(mainPane, BOX_WIDTH, BOX_HEIGHT);
		
		mainStage.setScene(mainScene);
		
		mainStage.setX(emp.getStage().getX());
		mainStage.setY(emp.getStage().getY() + emp.getStage().getHeight() + 30);
		
		key = null;
		mod = null;
		
		init();
	}
	
	private void init() {
		GridPane gp = new GridPane();
		HBox hb = new HBox();
		t = new Text("");
		Button confirm = new Button("确定");
		Button cancel = new Button("清空");
		
		hb.getChildren().add(confirm);
		hb.getChildren().add(cancel);
		
		gp.add(t, 0, 0);
		gp.add(hb, 0, 1);
		GridPane.setMargin(t, new Insets(10.0));
		
		mainPane.getChildren().add(gp);
		
		mainScene.setOnKeyPressed(e -> {
			String s = e.getCode().toString();
			if(s.length() == 1) {
				key = s;
			} else if(s.length() == 6 && s.subSequence(0, 5).equals("DIGIT")) {
				key = s.substring(5);
			} else if(s.equals("CONTROL") || s.equals("ALT") || s.equals("SHIFT")) {
				mod = s;
			}
			
			updateDisplay(t);
		});
		
		confirm.setOnMouseClicked(e -> {
			if(key == null && mod != null) {
				Alert al = new Alert(AlertType.WARNING);
				
				al.setTitle("非法输入");
				al.setHeaderText(null);
				al.setContentText("请确保输入了合法的按键/组合键");
				
				al.showAndWait();
			} else if(SkillIcon.getIcon1() != null) {
				SkillIcon.getIcon1().setKeyCombination(key, mod);
				key = null;
				mod = null;
				updateDisplay(t);
			}
		});
		
		cancel.setOnMouseClicked(e -> {
			key = null;
			mod = null;
			SkillIcon.removeChoice();
			updateDisplay(t);
		});
	}
	
	private void updateDisplay(Text t) {
		t.setText("");
		if(mod != null) {
			t.setText(mod + "+");
		} 
		if(key != null) {
			t.setText(t.getText() + key);
		}
	}
	
	public Stage getStage() {
		return mainStage;
	}
	
	public void display() {
		mainStage.show();
	}
	
	public void close() {
		mainStage.close();
	}
}
