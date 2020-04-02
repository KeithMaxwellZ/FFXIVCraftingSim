package application;

import java.util.ArrayList;
import java.util.List;

import application.components.EngineStatus;
import application.components.SkillIcon;
import application.components.Timer;
import application.subPane.AdvancedSettingsPane;
import application.subPane.CraftingHistoryPane;
import application.subPane.EditModePane;
import exceptions.ExceptionStatus;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import skills.ActiveBuff;
import skills.Buff;
import skills.BuffSkill;
import skills.PQSkill;
import skills.Skill;
import skills.SpecialSkills;

public class ViewManager
{
	private static final double WIDTH = 650;
	private static final double REC_WIDTH = 580;
	private static final double HEIGHT = 690;
	private static final double EDGE_GENERAL = 4.0;
	private static final double SKILL_HEIGHT = 270;
	private static final double BAR_EDGE = 5.0;
	private static final double BAR_WIDTH = 400.0;
	private static final double BAR_HEIGHT = 30.0;
	private static final double CP_EDGE = 5.0;
	private static final double CP_WIDTH = 150.0;
	private static final double CP_HEIGHT = 15.0;
	
	private static final String VERSION = "V1.4.0";
	
	private static final Color TEXT_COLOR = Color.BLACK;
	
	private Stage stage;
	private Scene mainScene;
	private AnchorPane mainPane;
	private AnchorPane lastSkillAp;
	private GridPane iconContainer;
	private VBox mainContainer;
	private HBox buffContainer;
	private Circle statusDisp;
	private Text efficiencyDisp;
	private Text durabilityText;
	private Text round;
	private Text skillDescription;
	private Timeline tml = new Timeline();
	
	private ArrayList<Text> progText;	//0=>Progress 1=>Quality 2=>CP 3=>Status 4=>Success
	private ArrayList<Rectangle> bars; 	//0=>Progress 1=>Quality 2=>CP
	
	private int craftsmanship = 2563;
	private int control = 2620;
	private int cp = 635;
	private int dura = 60;
	private int tProg = 9181;
	private int tQlty = 64862;
	private int rCraftsmanship = 2484;
	private int rControl = 2206;
	private double progressDifference = 0.8;
	private double qualityDifference = 0.6;
	
	private boolean hasGCD = true;

	private Skill lastSkill = null;
	
	private ArrayList<Skill> progressSkills;
	private ArrayList<Skill> qualitySkills;
	private ArrayList<Skill> buffSkills;
	private ArrayList<Skill> recoverySkills;
	private ArrayList<Skill> otherSkills;
	
	private Engine engine;
	
	private CraftingHistoryPane ch;
	private AdvancedSettingsPane asp;
	private EditModePane emp;
	
	private Timer tm;
	
	public ViewManager() {
		engine = new Engine(craftsmanship, control, cp, dura, tProg, tQlty, 
				rCraftsmanship, rControl, progressDifference, qualityDifference, ch);
		progText = new ArrayList<>();
		bars = new ArrayList<>();
		tm = new Timer();
		
		tm.startTimer();
				
		initSkillsList();
		initStage();
		initMainDisplay();
		
		ch = new CraftingHistoryPane(this);
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
		otherSkills.add(SpecialSkills.Careful_Observation);
	}
	
	private void initStage() {
		mainPane = new AnchorPane();
		stage = new Stage();
		mainScene = new Scene(mainPane, WIDTH, HEIGHT);
		
		mainPane.setBackground(new Background(
				new BackgroundFill(Color.LIGHTGRAY, null, null)));

		stage.setTitle("FFXIV Crafting Simulator " + VERSION);
		stage.setScene(mainScene);
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> {
			if(asp != null) {
				asp.close();
			}
			ch.close();
		});
	}
	
	private void initMainDisplay() {
		mainContainer = new VBox(20);
		
		mainContainer.setSpacing(10);
		mainContainer.setFillWidth(false);
		mainContainer.setAlignment(Pos.CENTER_LEFT);
		
		mainPane.getChildren().add(mainContainer);

		mainContainer.getChildren().add(initInput());
		mainContainer.getChildren().add(initProgressBar());
		mainContainer.getChildren().add(initCPDisplay());
		mainContainer.getChildren().add(initEfficiencyDisp());
		mainContainer.getChildren().add(initBuffDisp());
		mainContainer.getChildren().add(initSkills());

		VBox.setMargin(efficiencyDisp, new Insets(0, 0, 0 , 140.0));
		
		AnchorPane.setTopAnchor(mainContainer, 30.0);
		AnchorPane.setLeftAnchor(mainContainer, 30.0);
		
		engine.setEngineStatus(EngineStatus.Pending);;
		
		
	}
	
	private Node initInput() {		
		double tfWidth = 70.0;
		GridPane gp = new GridPane();
		GridPane border = new GridPane();
		GridPane back = new GridPane();
		Button confirm = new Button("确认");
		Button logs = new Button("日志"); 
		Button advanced = new Button("高级");
		Button finish = new Button("结束制作");
		Button iconRearr = new Button("编辑图标");
		
		ArrayList<Text> t = new ArrayList<Text>();
		
		Text craftT = new Text("制作精度");
		Text controlT = new Text("加工精度");
		Text CPT = new Text("CP");
		Text totalProgT = new Text("总进度");
		Text totalQltyT = new Text("总品质");
		Text totalDuraT = new Text("总耐久");

		
		TextField craftTf = new TextField(Integer.toString(craftsmanship));
		TextField controlTf = new TextField(Integer.toString(control));
		TextField CPTf = new TextField(Integer.toString(cp));
		TextField totalProgTf = new TextField(Integer.toString(tProg));
		TextField totalQltyTf = new TextField(Integer.toString(tQlty));
		TextField totalDuraTf = new TextField(Integer.toString(dura));

		CheckBox GCDCb = new CheckBox("GCD");
		
		gp.setHgap(5);
		gp.setVgap(3);
		gp.setPrefWidth(REC_WIDTH);
		
		GCDCb.setIndeterminate(false);
		GCDCb.setSelected(true);
		GCDCb.setTextFill(Color.WHITE);
		
		craftTf.setPrefWidth(tfWidth);
		controlTf.setPrefWidth(tfWidth);
		CPTf.setPrefWidth(tfWidth);
		totalProgTf.setPrefWidth(tfWidth);
		totalQltyTf.setPrefWidth(tfWidth);
		totalDuraTf.setPrefWidth(tfWidth);

		finish.setOnMouseClicked(e -> {
			if(engine.getEngineStatus() == EngineStatus.Crafting) {
				postFinishMessage(ExceptionStatus.Craft_Failed);
			}
		});
		
		iconRearr.setOnMouseClicked(e -> {
			emp = new EditModePane(this, engine);
			emp.display();
		});
		
		confirm.setOnMouseClicked(e -> {
			ch.destory();
			setLastSkill(null);
			craftsmanship = Integer.parseInt(craftTf.getText()); 
			control = Integer.parseInt(controlTf.getText()); 
			cp = Integer.parseInt(CPTf.getText());
			dura = Integer.parseInt(totalDuraTf.getText());
			tProg = Integer.parseInt(totalProgTf.getText()); 
			tQlty = Integer.parseInt(totalQltyTf.getText());
			ch = new CraftingHistoryPane(this);
			engine = new Engine(craftsmanship, control, cp, dura, tProg, tQlty, 
					rCraftsmanship, rControl, progressDifference,qualityDifference, ch);
			hasGCD = GCDCb.isSelected();
			SkillIcon.setVm(engine, tml, this);
			updateAll();
			ch.display();
		});
		
		logs.setOnMouseClicked(e -> {
			exportLogs();
		});
		
		advanced.setOnMouseClicked(e -> {
			asp = new AdvancedSettingsPane(t, this);
			asp.display();
		});
		
		int i = 0;
		int j = 0;
		gp.add(craftT, i, j);
		gp.add(controlT, i, j + 1);
		gp.add(CPT, i, j + 2);
		i++;
		
		gp.add(craftTf, i, j);
		gp.add(controlTf, i, j + 1);
		gp.add(CPTf, i, j + 2);
		i++;

		gp.add(totalProgT, i, j);
		gp.add(totalQltyT, i, j + 1);
		gp.add(totalDuraT, i, j + 2);
		i++;

		gp.add(totalProgTf, i, j);
		gp.add(totalQltyTf, i, j + 1);
		gp.add(totalDuraTf, i, j + 2);
		i++;

		gp.add(GCDCb, i, j);
		i++;
		
		gp.add(confirm, i, j);
		gp.add(logs, i, j + 1);
		gp.add(advanced, i, j + 2);
		i++;
		
		i++;
		i++;
		gp.add(finish, i, j);
		gp.add(iconRearr, i, j + 1);
		i++;

		border.setPrefWidth(REC_WIDTH + EDGE_GENERAL);
		border.add(back, 0, 0);
		border.setBackground(new Background(new BackgroundFill(Color.SILVER, new CornerRadii(10.0), null)));
		
		back.add(gp, 0, 0);
		back.setBackground(new Background(new BackgroundFill(Color.rgb(25,30,37,1.0), new CornerRadii(10.0), null)));
		
		GridPane.setMargin(back, new Insets(EDGE_GENERAL / 2));
		GridPane.setMargin(gp, new Insets(10));
		
		t.add(craftT);
		t.add(controlT);
		t.add(CPT);
		t.add(totalProgT);
		t.add(totalQltyT);
		t.add(totalDuraT);
		
		for(Text tx: t) {
			tx.setFill(Color.WHITE);
		}
		
		
		return border;
	}
	
	private Node initProgressBar() {
		GridPane container = new GridPane();
		AnchorPane progressBar = createBar(Color.DARKGREEN, BAR_WIDTH, BAR_HEIGHT, BAR_EDGE);
		AnchorPane qualityBar = createBar(Color.DARKBLUE, BAR_WIDTH, BAR_HEIGHT, BAR_EDGE);
		Text progressText = new Text(engine.presentProgress + "/" + engine.totalProgress);
		Text qualityText = new Text(engine.presentQuality + "/" + engine.totalQuality);
		ArrayList<Text> t = new ArrayList<Text>();
		
		durabilityText = new Text("耐久:  " + engine.presentDurability + "/" + engine.totalDurability);
		round = new Text("工次:  " + engine.getRound());
		
		progText.add(progressText);
		progText.add(qualityText);
		
		container.setAlignment(Pos.CENTER);
		
		container.add(durabilityText, 0, 0);
		container.add(round, 0, 1);
		
		container.add(progressBar, 1, 0);
		container.add(progressText, 2, 0);
		
		container.add(qualityBar, 1, 1);
		container.add(qualityText, 2, 1);
		
		container.setVgap(20);
		container.setHgap(20);
		
		t.add(durabilityText);
		t.add(round);
		t.add(progressText);
		t.add(qualityText);
		
		for(Text tx: t) {
			tx.setFill(TEXT_COLOR);
		}
		
		return container;
	}
	
	private Node initCPDisplay() {
		HBox container = new HBox();
		AnchorPane cpBar = createBar(Color.PURPLE, CP_WIDTH, CP_HEIGHT, CP_EDGE);
		Text status = new Text("  通常     ");
		Text cpVal = new Text(engine.presentCP + "/" + engine.totalCP);
		Text success = new Text("");
		Text cp = new Text("CP");
		ArrayList<Text> t = new ArrayList<Text>();

		statusDisp = new Circle(10, Color.WHITE);
		
		container.setAlignment(Pos.CENTER);
		
		status.setFill(Color.WHITE);
		
		progText.add(cpVal);
		progText.add(status);
		progText.add(success);
		bars.get(2).setWidth(CP_WIDTH);
		
		container.getChildren().addAll(status, statusDisp, cp, cpBar, cpVal, success);
		
		container.setSpacing(30);
		container.setLayoutX(10);
		
		t.add(cpVal);
		t.add(success);
		t.add(cp);
		
		for(Text tx: t) {
			tx.setFill(TEXT_COLOR);
		}
		
		return container;
	} 
	
	private Node initEfficiencyDisp() {
		HBox container = new HBox();
		Text lastSkillT = new Text("上一个技能:  ");
		lastSkillAp = new AnchorPane();
		efficiencyDisp = new Text(); 
		ArrayList<Text> t = new ArrayList<Text>();
		
		lastSkillAp.setPrefSize(40.0, 40.0);
		HBox.setMargin(lastSkillAp, new Insets(0, 30.0, 0, 0));

		efficiencyDisp.setText("  100%效率下的进展: " + engine.getBaseProgEff() + 
							" | 100%效率下的品质: " + engine.getBaseQltyEff());
		
		container.setAlignment(Pos.CENTER);
		container.getChildren().addAll(lastSkillT, lastSkillAp, efficiencyDisp);
		
		t.add(lastSkillT);
		t.add(efficiencyDisp);
		
		for(Text tx: t) {
			tx.setFill(TEXT_COLOR);
		}
		
		return container;
	}
	
	private Node initBuffDisp() {
		Text buffText = new Text("  Buff:");
		buffContainer = new HBox(10);
		buffContainer.setPrefHeight(32.0);
		
		buffContainer.getChildren().add(buffText);
		
		buffText.setFill(TEXT_COLOR);
		
		return buffContainer;
	}
	
	private Node initSkills() {
		GridPane skillContainer = new GridPane();
		AnchorPane border = new AnchorPane();	
		
		setIconContainer(new GridPane());
		
		setSkillDescription(new Text("  "));
		
		
		skillContainer.setVgap(5);
		getIconContainer().setHgap(5);
		
		int i = 2;
		
		skillContainer.add(getSkillDescription(), 0, 1);
		skillContainer.add(getIconContainer(), 0, 2);
		
		GridPane.setMargin(getSkillDescription(), new Insets(10.0));
		
		createSkillList(progressSkills, getIconContainer(), i++);
		createSkillList(qualitySkills, getIconContainer(), i++);
		createSkillList(buffSkills, getIconContainer(), i++);
		createSkillList(recoverySkills, getIconContainer(), i++);
		createSkillList(otherSkills, getIconContainer(), i++);
		
		border.getChildren().add(skillContainer);
		border.setPrefSize(REC_WIDTH + EDGE_GENERAL, SKILL_HEIGHT + EDGE_GENERAL);
		skillContainer.setPrefSize(REC_WIDTH, SKILL_HEIGHT);
		
		border.setBackground(new Background(
				new BackgroundFill(Color.SILVER, new CornerRadii(5.0), null)));
		skillContainer.setBackground(new Background(
				new BackgroundFill(Color.AZURE, new CornerRadii(5.0), null)));
		
		AnchorPane.setLeftAnchor(skillContainer, EDGE_GENERAL / 2);
		AnchorPane.setTopAnchor(skillContainer, EDGE_GENERAL / 2);
		
		for(Node n: skillContainer.getChildren()) {
			GridPane.setMargin(n, new Insets(0, 0, 0, 10));
		}
		
		return border;
	}
	
	private void createSkillList(List<Skill> skl, GridPane gp, int i) {
		
		//pqbro
		
		int j = 1;
		for(Skill s: skl) {
			engine.addToLogs(s.toString() + ": " + s.getAddress());;
			
			SkillIcon si = new SkillIcon(s, tml, this);
			
			gp.add(si, j, i);
			j++;
		}
		for(; j <= 12; j++) {
			SkillIcon si = new SkillIcon(null, tml, this);
			gp.add(si, j, i);
		}
		
		SkillIcon.setVm(engine, tml, this);

		return;
	}
	
	private AnchorPane createBar(Color c, double width, double height, double paneEdge) {
		AnchorPane bar = new AnchorPane();
		Rectangle edgeR = new Rectangle(width + 2 * paneEdge, height + 2 * paneEdge, Color.BLACK);
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
		updateLastSkill();
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
		engine.addToLogs("Present dur: " + engine.presentDurability);
		durabilityText.setText("耐久:  " + engine.presentDurability+ "/" + engine.totalDurability);
		round.setText("工次:  " + engine.getRound());;
		
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
		progText.get(3).setText("  " + engine.getCraftingStatus().getName());
		progText.get(3).setFill(engine.getCraftingStatus().getColor());
		statusDisp.setFill(engine.getCraftingStatus().getColor());
	}
	
	public void updateBuffDIsp() {
		Text buffText = new Text("  Buff:");
		
		buffContainer.getChildren().clear();
		buffText.setFill(TEXT_COLOR);
		buffContainer.getChildren().add(buffText);
		
		for(ActiveBuff ab: engine.activeBuffs) {
			engine.addToLogs("refreshing buff display... " + ab.buff.toString() + " " + ab.getRemaining());
			
			AnchorPane ap = new AnchorPane();
			ImageView iv = null;
			if(ab.buff == Buff.inner_quiet) {
				String add = "/icons/Inner_Quiet_Icon/Inner_Quiet_" + ab.getRemaining() + ".png";
				iv = new ImageView(new Image(add, true));
			} else {
				iv = new ImageView(new Image(ab.buff.getAddress(), true));
			}
			Text remaining = new Text(Integer.toString(ab.getRemaining()));
			
			ap.getChildren().add(iv);
			ap.getChildren().add(remaining);

			buffContainer.getChildren().add(ap);
			
		}
	}
	
	public void updateLastSkill() {
		if(getLastSkill() == null) {
			lastSkillAp.setBackground(Background.EMPTY);
		} else {
			lastSkillAp.setBackground(new Background(new BackgroundImage(
					new Image(getLastSkill().getAddress(), true), null, null, 
					BackgroundPosition.CENTER, null)));
		}
		
	}
	
	public void postFinishMessage(ExceptionStatus es) {
		Alert al = new Alert(AlertType.INFORMATION);
		GridPane gp = new GridPane();
		Text GCDMode = new Text("GCD: " + (getHasGCD() ? "开启" : "关闭"));
		Text runTime = new Text("总用时:  " + Double.toString(engine.getRuntime()) + "秒");
		Text val = new Text("收藏价值:  " + engine.getPresentQuality() / 10);
		
		updateAll();
		
		engine.setEngineStatus(EngineStatus.Pending);;
		engine.addToLogs("========= Summary =========");
		engine.addToLogs("Status: " + es.toString());
		engine.addToLogs("Total time: " + engine.getRuntime());
		engine.addToLogs("Value: " + (engine.getPresentQuality() / 10));
		engine.addToLogs("Skill Points: " + engine.SPCalc());
		engine.addToLogs("===========================");
		
		al.setTitle(es == ExceptionStatus.Craft_Failed ? "制作失败...." : "制作成功！");
		al.setHeaderText(es == ExceptionStatus.Craft_Failed ? "啊呀，制作失败了...." : "恭喜，制作成功！");
		
		int i = 0;
		gp.add(GCDMode, 0, i++);
		gp.add(runTime, 0, i++);
		gp.add(val, 0, i++);
		
		if(es == ExceptionStatus.Craft_Success) {		
			Text SP = new Text("技巧点数(暂译):  " + engine.SPCalc());
			gp.add(SP, 0, i++);	
		}
		
		al.getDialogPane().setExpandableContent(gp);
		al.getDialogPane().setExpanded(true);
		
		al.showAndWait();
	}
	
	public void postInvalidMessage(ExceptionStatus es) {
		Alert al = new Alert(AlertType.WARNING);
		
		al.setTitle("无法使用");
		al.setContentText(es.getMessage());
		
		al.showAndWait();
	}
	
	public void postUnexpectedMessage() {
		Alert al = new Alert(AlertType.WARNING);
		
		al.setTitle("未知错误");
		al.setContentText("你是怎么触发的...");
		
		al.showAndWait();
	}
	
	public void exportLogs() {
		Alert al = new Alert(AlertType.INFORMATION);
		GridPane container = new GridPane();
		Text title = new Text("日志");
		TextArea logsOutput = new TextArea();
		
		logsOutput.setEditable(false);
		logsOutput.setWrapText(false);
		
		for(String s: engine.getLogs()) {
			System.out.println(s);
			logsOutput.setText(logsOutput.getText() + "\n" + s);
		}
		
		GridPane.setVgrow(logsOutput, Priority.ALWAYS);
		GridPane.setHgrow(logsOutput, Priority.ALWAYS);
		
		al.setTitle("以下为日志输出");
		al.setHeaderText(null);
		
		container.setMaxWidth(Double.MAX_VALUE);
		container.add(title, 0, 0);
		container.add(logsOutput, 0, 1);
		
		al.getDialogPane().setExpandableContent(container);
		al.getDialogPane().setExpanded(true);
		
		al.showAndWait();
	}
	
	public Stage getStage() {
		return stage;
	}
	
	public Timer getTimer() {
		return tm;
	}
	
	public boolean getHasGCD() {
		return hasGCD;
	}

	public int getrCraftsmanship()
	{
		return rCraftsmanship;
	}

	public void setrCraftsmanship(int rCraftsmanship)
	{
		this.rCraftsmanship = rCraftsmanship;
	}

	public int getrControl()
	{
		return rControl;
	}

	public void setrControl(int rControl)
	{
		this.rControl = rControl;
	}

	public double getProgressDifference()
	{
		return progressDifference;
	}

	public void setProgressDifference(double progressDifference)
	{
		this.progressDifference = progressDifference;
	}

	public double getQualityDifference()
	{
		return qualityDifference;
	}

	public void setQualityDifference(double qualityDifference)
	{
		this.qualityDifference = qualityDifference;
	}

	public Text getSkillDescription()
	{
		return skillDescription;
	}

	public void setSkillDescription(Text skillDescription)
	{
		this.skillDescription = skillDescription;
	}

	public Skill getLastSkill()
	{
		return lastSkill;
	}

	public void setLastSkill(Skill lastSkill)
	{
		this.lastSkill = lastSkill;
	}

	public GridPane getIconContainer()
	{
		return iconContainer;
	}

	public void setIconContainer(GridPane iconContainer)
	{
		this.iconContainer = iconContainer;
	}
}



