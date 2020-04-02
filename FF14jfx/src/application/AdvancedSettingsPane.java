package application;

import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class AdvancedSettingsPane
{
	public static final double BOX_WIDTH = 300.0;
	public static final double BOX_HEIGHT = 250.0;
	public static final double TEXEFIELD_WIDTH = 70.0;
	
	private Stage boxStage;
	private Scene scene;
	private GridPane mainBoxPane;

	private ViewManager vm;
	
	Text rCraftT;
	Text rControlT;
	Text progDiffT;
	Text qltyDiffT;

	TextField rCraftTf;
	TextField rControlTf;
	TextField progDiffTf;
	TextField qltyDiffTf;
	
	ArrayList<Text> t;
	
	public AdvancedSettingsPane(ArrayList<Text> t, ViewManager vm)
	{
		this.t = t;
		this.vm = vm;
		
		mainBoxPane = new GridPane();
		boxStage = new Stage();
		scene = new Scene(mainBoxPane, BOX_WIDTH, BOX_HEIGHT);
		
		
		
		boxStage.setTitle("高级设置");
		boxStage.setScene(scene);
		
		rCraftT = new Text("推荐制作");
		rControlT = new Text("推荐加工");
		progDiffT = new Text("制作等级差");
		qltyDiffT = new Text("加工等级差");

		rCraftTf = new TextField(Integer.toString(vm.getrCraftsmanship()));
		rControlTf = new TextField(Integer.toString(vm.getrControl()));
		progDiffTf = new TextField(Double.toString(vm.getProgressDifference()));
		qltyDiffTf = new TextField(Double.toString(vm.getQualityDifference()));
		
		initDisplay();
	}
	
	private void initDisplay() {
		GridPane gp = new GridPane();
		Button b = new Button("确认");
		
		rCraftTf.setPrefWidth(TEXEFIELD_WIDTH);
		rControlTf.setPrefWidth(TEXEFIELD_WIDTH);
		progDiffTf.setPrefWidth(TEXEFIELD_WIDTH);
		qltyDiffTf.setPrefWidth(TEXEFIELD_WIDTH);

		vm.setrCraftsmanship(Integer.parseInt(rCraftTf.getText()));
		vm.setrControl(Integer.parseInt(rControlTf.getText()));
		vm.setProgressDifference(Double.parseDouble(progDiffTf.getText()));
		vm.setQualityDifference(Double.parseDouble(qltyDiffTf.getText()));

		gp.setVgap(5.0);
		gp.setHgap(5.0);
		
		int i = 0;
		int j = 0;
		gp.add(rCraftT, i, j);
		gp.add(rCraftTf, i + 1, j);
		j++;
		
		gp.add(rControlT, i, j);
		gp.add(rControlTf, i + 1, j);
		j++;
		
		gp.add(progDiffT, i, j);
		gp.add(progDiffTf, i + 1, j);
		j++;	
		
		gp.add(qltyDiffT, i, j);
		gp.add(qltyDiffTf, i + 1, j);
		j++;
		
		gp.add(b, i, j);
		
		t.add(rCraftT);
		t.add(rControlT);
		t.add(progDiffT);
		t.add(qltyDiffT);
		
		b.setOnMouseClicked(e -> {
			vm.setrCraftsmanship(Integer.parseInt(rCraftTf.getText()));
			vm.setrControl(Integer.parseInt(rControlTf.getText()));
			vm.setProgressDifference(Double.parseDouble(progDiffTf.getText()));
			vm.setQualityDifference(Double.parseDouble(qltyDiffTf.getText()));
			boxStage.close();
		});
		
		mainBoxPane.add(gp, 0, 0);
		
		GridPane.setMargin(gp, new Insets(20.0));
	}
	
	public void display() {
		boxStage.showAndWait();
	}
	

}
