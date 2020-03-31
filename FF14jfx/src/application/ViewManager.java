package application;

import java.util.ArrayList;
import java.util.List;

import exceptions.CraftingException;
import exceptions.ExceptionStatus;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import skills.ActiveBuff;
import skills.BuffSkill;
import skills.PQSkill;
import skills.Skill;
import skills.SpecialSkills;

public class ViewManager
{
	private static final double WIDTH = 650;
	private static final double REC_WIDTH = 580;
	private static final double HEIGHT = 660;
	private static final double INPUT_HEIGHT = 90;
	private static final double SKILL_HEIGHT = 270;
	private static final double BAR_EDGE = 5.0;
	private static final double BAR_WIDTH = 400.0;
	private static final double BAR_HEIGHT = 30.0;
	private static final double CP_EDGE = 5.0;
	private static final double CP_WIDTH = 150.0;
	private static final double CP_HEIGHT = 30.0;
	
	private Stage stage;
	private Scene mainScene;
	private AnchorPane mainPane;
	private VBox mainContainer;
	private Circle statusDisp;
	private Text efficiencyDisp;
	private HBox buffContainer;
	private Text durabilityVal;
	private Text skillDescription;
	
	ArrayList<Text> progText;	//0=>Progress 1=>Quality 2=>CP 3=>Status 4=>Success
	ArrayList<Rectangle> bars; 	//0=>Progress 1=>Quality 2=>CP
	
	private int craftsmanship = 2500;
	private int control = 2400;
	private int cp = 538;
	private int dura = 70;
	private int tProg = 9181;
	private int tQlty = 64862;
	private int rCraftsmanship = 2480;
	private int rControl = 2195;
	
	private ArrayList<Skill> progressSkills;
	private ArrayList<Skill> qualitySkills;
	private ArrayList<Skill> buffSkills;
	private ArrayList<Skill> recoverySkills;
	private ArrayList<Skill> otherSkills;
	
	private Engine engine;
	
	public ViewManager() {
		engine = new Engine(craftsmanship, control, cp, dura, tProg, tQlty, rCraftsmanship, rControl);
		progText = new ArrayList<>();
		bars = new ArrayList<>();
				
		initSkillsList();
		initStage();
		initDisplay();
	}
	
	private void initSkillsList() {
		progressSkills = new ArrayList<>();
		qualitySkills = new ArrayList<>();
		buffSkills = new ArrayList<>();
		recoverySkills = new ArrayList<>();
		otherSkills = new ArrayList<>();
		
		progressSkills.add(PQSkill.Basic_Synthesis);
		progressSkills.add(PQSkill.Careful_Synthesis);
		progressSkills.add(PQSkill.Rapid_Synthesis);
		progressSkills.add(PQSkill.Groundwork);
		progressSkills.add(PQSkill.Focused_Synthesis);
		progressSkills.add(PQSkill.Brand_of_the_Elements);
		progressSkills.add(PQSkill.Intensive_Synthesis);
		progressSkills.add(PQSkill.Delicate_Synthesis);
		
		qualitySkills.add(PQSkill.Basic_Touch);
		qualitySkills.add(PQSkill.Standard_Touch);
		qualitySkills.add(PQSkill.Hasty_Touch);
		qualitySkills.add(PQSkill.Precise_Touch);
		qualitySkills.add(PQSkill.Focused_Touch);
		qualitySkills.add(PQSkill.Patient_Touch);
		qualitySkills.add(PQSkill.Prudent_Touch);
		qualitySkills.add(PQSkill.Preparatory_Touch);
		qualitySkills.add(SpecialSkills.Byregots_Blessing);
		
		buffSkills.add(BuffSkill.Muscle_Memory);
		buffSkills.add(BuffSkill.Reflect);
		buffSkills.add(BuffSkill.Inner_Quiet);
		buffSkills.add(BuffSkill.Waste_Not);
		buffSkills.add(BuffSkill.Waste_Not_II);
		buffSkills.add(BuffSkill.Great_Strides);
		buffSkills.add(BuffSkill.Innovation);
		buffSkills.add(BuffSkill.Veneration);
		buffSkills.add(BuffSkill.Name_of_the_Elements);
		buffSkills.add(BuffSkill.Final_Appraisal);
		
		recoverySkills.add(SpecialSkills.Masters_Mend);
		recoverySkills.add(BuffSkill.Manipulation);
		
		otherSkills.add(SpecialSkills.Observe);
		otherSkills.add(SpecialSkills.Tricks_of_the_Trade);
	}
	
	private void initStage() {
		mainPane = new AnchorPane();
		stage = new Stage();
		mainScene = new Scene(mainPane, WIDTH, HEIGHT);
		
		mainPane.setBackground(new Background(new BackgroundFill(Color.LIGHTGREY, null, null)));
		
		stage.setTitle("FFXIV Crafting Simulator");
		stage.setScene(mainScene);
		stage.setResizable(false);
	}
	
	private void initDisplay() {
		mainContainer = new VBox(20);
		mainContainer.setSpacing(10);
		mainContainer.setFillWidth(false);
		mainContainer.setAlignment(Pos.CENTER_LEFT);
		mainPane.getChildren().add(mainContainer);

		mainContainer.getChildren().add(initInput());
		mainContainer.getChildren().add(initProgressBar());
		mainContainer.getChildren().add(initCPDisplay());
		initEfficiencyDisp();
		mainContainer.getChildren().add(efficiencyDisp);
		mainContainer.getChildren().add(initBuffDisp());
		mainContainer.getChildren().add(initSkills());

		
		AnchorPane.setTopAnchor(mainContainer, 30.0);
		AnchorPane.setLeftAnchor(mainContainer, 30.0);
	}
	
	private Node initInput() {

		GridPane gp = new GridPane();
		gp.setHgap(5);
		gp.setVgap(3);
		gp.setPrefWidth(REC_WIDTH);
		gp.setPrefHeight(INPUT_HEIGHT);
		
		Text craftT = new Text("制作精度");
		Text controlT = new Text("加工精度");
		Text CPT = new Text("CP");
		Text totalProgT = new Text("总进度");
		Text totalQltyT = new Text("总品质");
		Text totalDuraT = new Text("总耐久");
		Text rCraftT = new Text("推荐制作精度");
		Text rControlT = new Text("推荐加工精度");
		
		TextField craftTf = new TextField(Integer.toString(craftsmanship));
		TextField controlTf = new TextField(Integer.toString(control));
		TextField CPTf = new TextField(Integer.toString(cp));
		TextField totalProgTf = new TextField(Integer.toString(tProg));
		TextField totalQltyTf = new TextField(Integer.toString(tQlty));
		TextField totalDuraTf = new TextField(Integer.toString(dura));
		TextField rCraftTf = new TextField(Integer.toString(rCraftsmanship));
		TextField rControlTf = new TextField(Integer.toString(rControl));
		
		double tfWidth = 60;
		craftTf.setPrefWidth(tfWidth);
		controlTf.setPrefWidth(tfWidth);
		CPTf.setPrefWidth(tfWidth);
		totalProgTf.setPrefWidth(tfWidth);
		totalQltyTf.setPrefWidth(tfWidth);
		totalDuraTf.setPrefWidth(tfWidth);
		rCraftTf.setPrefWidth(tfWidth);
		rControlTf.setPrefWidth(tfWidth);
		
		Button confirm = new Button("确认");
		confirm.setOnAction(e -> {
			engine = new Engine(Integer.parseInt(craftTf.getText()), 
								Integer.parseInt(controlTf.getText()), 
								Integer.parseInt(CPTf.getText()), 
								Integer.parseInt(totalDuraTf.getText()),
								Integer.parseInt(totalProgTf.getText()), 
								Integer.parseInt(totalQltyTf.getText()), 
								Integer.parseInt(rCraftTf.getText()), 
								Integer.parseInt(rControlTf.getText()));
			updateAll();
		});
		
		int i = 3;
		int j = 2;
		gp.add(craftT, i, j);
		gp.add(craftTf, i, j + 1);
		gp.add(confirm, i++, j + 2);
		
		gp.add(controlT, i, j);
		gp.add(controlTf, i++, j + 1);
		
		gp.add(CPT, i, j);
		gp.add(CPTf, i++, j + 1);
		
		gp.add(totalProgT, i, j);
		gp.add(totalProgTf, i++, j + 1);
		
		gp.add(totalQltyT, i, j);
		gp.add(totalQltyTf, i++, j + 1);
		
		gp.add(totalDuraT, i, j);
		gp.add(totalDuraTf, i++, j + 1);
		
		gp.add(rCraftT, i, j);
		gp.add(rCraftTf, i++, j + 1);
		
		gp.add(rControlT, i, j);
		gp.add(rControlTf, i++, j + 1);
		
		
		
		AnchorPane ap = new AnchorPane();
		double edge = 8.0;
		ap.setPrefWidth(REC_WIDTH + edge);
		ap.setPrefHeight(INPUT_HEIGHT + edge);
		ap.getChildren().add(gp);
		ap.setBackground(new Background(new BackgroundFill(Color.SILVER, null, null)));
		gp.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
		
		AnchorPane.setLeftAnchor(gp, edge / 2);
		AnchorPane.setTopAnchor(gp, edge / 2);
		
		return ap;
	}
	
	private Node initProgressBar() {
		GridPane container = new GridPane();
		container.setAlignment(Pos.CENTER);
		
		Text durabilityText = new Text("  耐久          ");
		durabilityVal = new Text("  " + engine.presentDurability + "/" + engine.totalDurability);
		AnchorPane progressBar = createBar(Color.DARKGREEN, BAR_WIDTH, BAR_HEIGHT, BAR_EDGE);
		AnchorPane qualityBar = createBar(Color.DARKBLUE, BAR_WIDTH, BAR_HEIGHT, BAR_EDGE);
		Text progressText = new Text(engine.presentProgress + "/" + engine.totalProgress);
		Text qualityText = new Text(engine.presentQuality + "/" + engine.totalQuality);
		
		progText.add(progressText);
		progText.add(qualityText);
		
		container.add(durabilityText, 0, 0);
		container.add(durabilityVal, 0, 1);
		
		container.add(progressBar, 1, 0);
		container.add(progressText, 2, 0);
		
		container.add(qualityBar, 1, 1);
		container.add(qualityText, 2, 1);
		
		container.setVgap(20);
		container.setHgap(20);
		
		return container;
	}
	
	private Node initCPDisplay() {
		HBox container = new HBox();
		container.setAlignment(Pos.CENTER);
		
		Text status = new Text("  通常   ");
		statusDisp = new Circle(10, Color.WHITE);
		Text cp = new Text("CP");
		AnchorPane cpBar = createBar(Color.PURPLE, CP_WIDTH, CP_HEIGHT, CP_EDGE);
		Text cpVal = new Text(engine.presentCP + "/" + engine.totalCP);
		Text success = new Text("");
		
		progText.add(cpVal);
		progText.add(status);
		progText.add(success);
		bars.get(2).setWidth(CP_WIDTH);
		
		container.getChildren().addAll(status, statusDisp, cp, cpBar, cpVal, success);
		
		container.setSpacing(30);
		container.setLayoutX(10);
		
		return container;
	} 
	
	private void initEfficiencyDisp() {
		efficiencyDisp = new Text(); 
		efficiencyDisp.setText("  100%效率下的进展: " + engine.getBaseProgEff() + 
							" | 100%效率下的品质: " + engine.getBaseQltyEff());
	}
	
	private Node initBuffDisp() {
		buffContainer = new HBox(10);
		buffContainer.setPrefHeight(32.0);
		Text buffText = new Text("  Buff:");
		
		buffContainer.getChildren().add(buffText);
		
		return buffContainer;
	}
	
	private Node initSkills() {
		
		GridPane skillContainer = new GridPane();
		skillDescription = new Text("");
		
		skillContainer.setVgap(5);
		
		int i = 0;
		int j = 2;
		skillContainer.add(skillDescription, 0, 1);
		skillContainer.add(createSkillList(progressSkills), i, j);
		skillContainer.add(createSkillList(qualitySkills), i, j + 1);
		skillContainer.add(createSkillList(buffSkills), i, j + 2);
		
		skillContainer.add(createSkillList(recoverySkills), i, j + 3);
		skillContainer.add(createSkillList(otherSkills), i, j + 4);
		
		AnchorPane border = new AnchorPane();
		double edge = 8.0;
		
		border.getChildren().add(skillContainer);
		border.setPrefSize(REC_WIDTH + edge, SKILL_HEIGHT + edge);
		skillContainer.setPrefSize(REC_WIDTH, SKILL_HEIGHT);
		
		border.setBackground(new Background(
				new BackgroundFill(Color.SILVER, new CornerRadii(5.0), null)));
		skillContainer.setBackground(new Background(
				new BackgroundFill(Color.AZURE, new CornerRadii(5.0), null)));
		
		AnchorPane.setLeftAnchor(skillContainer, edge / 2);
		AnchorPane.setTopAnchor(skillContainer, edge / 2);
		
		for(Node n: skillContainer.getChildren()) {
			GridPane.setMargin(n, new Insets(0, 0, 0, 10));
		}
		
		return border;
	}
	
	private Node createSkillList(List<Skill> skl) {
		HBox skillLine = new HBox();
		
		skillLine.setSpacing(5);
		
		for(Skill s: skl) {
			System.out.println(s.getAddress());
			
			AnchorPane skillIcon = new AnchorPane();
			Image icon = new Image(s.getAddress(), true);
			Button b = new Button();
			Text costText = new Text(s.getCPCost() != 0 ? Integer.toString(s.getCPCost()) : "");
			costText.setFill(Color.WHITE);
			skillIcon.getChildren().add(b);
			skillIcon.getChildren().add(costText);
			
			costText.setLayoutX(0);
			costText.setLayoutY(40.0);
			costText.setFont(Font.font(15));
			
			b.setLayoutX(0);
			b.setLayoutY(0);
			
			b.setPrefHeight(40);
			b.setPrefWidth(40);
			
			b.setBackground(new Background(new BackgroundImage(
					icon, null, null, BackgroundPosition.CENTER, null)));
			
			b.setOnMouseClicked(e -> {
				if(engine.isWorking()) {
					performSkill(s);
				}
			});
			b.setOnMouseEntered(e -> {
				skillDescription.setText(s.getName());
			});
			b.setOnMouseExited(e -> {
				skillDescription.setText("");
			});
			skillLine.getChildren().add(skillIcon);
		}

		return skillLine;
	}
	
	
	private void performSkill(Skill sk) {
		try {
			System.out.println("tst");
			engine.useSkill(sk); 
		} catch (CraftingException e) {
			if(e.es == ExceptionStatus.Craft_Failed || e.es == ExceptionStatus.Craft_Success) {
				postFinishMessage(e.es);
			} else if ( e.es == ExceptionStatus.Not_HQ ||
						e.es == ExceptionStatus.No_Inner_Quiet ||
						e.es == ExceptionStatus.Inner_Quiet_Exists ||
						e.es == ExceptionStatus.Not_Turn_One ||
						e.es == ExceptionStatus.Waste_Not_Exist ||
						e.es == ExceptionStatus.No_Enough_CP) {
				postInvalidMessage(e.es);
			} else {
				postUnexpectedMessage();
			}
		} finally {
			updateAll();
		}
	}
	
	private void postFinishMessage(ExceptionStatus es) {
		updateAll();
		engine.setWorking(false);
		Alert al = new Alert(AlertType.INFORMATION);
		System.out.println(es.toString());
		al.setTitle(es == ExceptionStatus.Craft_Failed ? "制作失败...." : "制作成功！");
		al.setHeaderText(es == ExceptionStatus.Craft_Failed ? "啊呀，制作失败了...." : "恭喜，制作成功！");
		al.setContentText("收藏价值:  " + engine.getPresentQuality() / 10);
		
		al.showAndWait();
	}
	
	private void postInvalidMessage(ExceptionStatus es) {
		Alert al = new Alert(AlertType.WARNING);
		al.setTitle("无法使用");
		al.setContentText(es.getMessage());
		
		al.showAndWait();
	}
	
	private void postUnexpectedMessage() {
		Alert al = new Alert(AlertType.WARNING);
		al.setTitle("未知错误");
		al.setContentText("你是怎么触发的...");
		
		al.showAndWait();
	}
	
	private AnchorPane createBar(Color c, double width, double height, double edge) {

		AnchorPane bar = new AnchorPane();
		Rectangle edgeR = new Rectangle(width + 2 * edge, height + 2 * edge, Color.BLACK);
		Rectangle fill = new Rectangle(width, height, Color.WHITE);
		Rectangle progress = new Rectangle(0, height, c);
		
		edgeR.setArcWidth(15.0);
		edgeR.setArcHeight(15.0);
		fill.setArcWidth(15.0);
		fill.setArcHeight(15.0);
		progress.setArcWidth(15.0);
		progress.setArcHeight(15.0);
		
		bars.add(progress);
		
		bar.getChildren().add(edgeR);
		bar.getChildren().add(fill);
		bar.getChildren().add(progress);
		
		AnchorPane.setTopAnchor(fill, (double)BAR_EDGE);
		AnchorPane.setTopAnchor(progress, (double)BAR_EDGE);
		AnchorPane.setLeftAnchor(fill, (double)BAR_EDGE);
		AnchorPane.setLeftAnchor(progress, (double)BAR_EDGE);
			
		return bar;
	}
	
	public void updateAll() {
		updateProgress();
		updateQuality();
		updateCP();
		updateDur();
		updateEffDisp();
		updateSuccess();
		updateBuffDIsp();
		updateStatus();
	}
	
	public void updateProgress() {
		progText.get(0).setText(engine.presentProgress + "/" + engine.totalProgress);
		if(engine.presentProgress>=engine.totalProgress) {
			bars.get(0).setWidth(BAR_WIDTH);
		} else {
			bars.get(0).setWidth((double)engine.presentProgress/engine.totalProgress*BAR_WIDTH);
		}	}
	
	public void updateQuality() {
		progText.get(1).setText(engine.presentQuality + "/" + engine.totalQuality);
		if(engine.presentQuality>=engine.totalQuality) {
			bars.get(1).setWidth(BAR_WIDTH);
		} else {
			bars.get(1).setWidth((double)engine.presentQuality/engine.totalQuality*BAR_WIDTH);
		}
	}
	
	public void updateCP() {
		progText.get(2).setText(engine.presentCP + "/" + engine.totalCP);
		if(engine.presentCP>=engine.totalCP) {
			bars.get(2).setWidth(CP_WIDTH);
		} else {
			bars.get(2).setWidth((double)engine.presentCP/engine.totalCP*CP_WIDTH);
		}
	}
	
	public void updateDur() {
		System.out.println("Present dur: " + engine.presentDurability);
		durabilityVal.setText("  " + engine.presentDurability+ "/" + engine.totalDurability);;
		
	}
	
	public void updateEffDisp() {
		efficiencyDisp.setText("  100%效率下的进展: " + engine.getBaseProgEff() + 
							" | 100%效率下的品质: " + engine.getBaseQltyEff());
	}
	
	public void updateSuccess() {
		Text t = progText.get(4);
		if(engine.success) {
			t.setText("Success!");
			t.setFill(Color.GREEN);
		} else {
			t.setText("Fail...");
			t.setFill(Color.RED);
		}
		t.setFont(Font.font(20));
	}
	
	public void updateStatus() {
		progText.get(3).setText("  " + engine.getCraftingStatus().name);
		progText.get(3).setFill(engine.getCraftingStatus().color);
		statusDisp.setFill(engine.getCraftingStatus().color);
	}
	
	public void updateBuffDIsp() {
		buffContainer.getChildren().clear();
		Text buffText = new Text("  Buff:");
		buffContainer.getChildren().add(buffText);
		for(ActiveBuff ab: engine.activeBuffs) {
			System.out.println("refreshing buffs... " + ab.buff.toString() + " " + ab.getRemaining());
			
			AnchorPane ap = new AnchorPane();
			ImageView iv = new ImageView(new Image(ab.buff.getAddress(), true));
			Text remaining = new Text(Integer.toString(ab.getRemaining()));
			ap.getChildren().add(iv);
			ap.getChildren().add(remaining);

			buffContainer.getChildren().add(ap);
		}
	}
	
	public Stage getStage() {
		return stage;
	}
}
