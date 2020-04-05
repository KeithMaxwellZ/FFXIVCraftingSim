package application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import application.components.ConfigManager;
import application.components.SkillIcon;
import application.components.Timer;
import application.subPane.AdvancedSettingsPane;
import application.subPane.CraftingHistoryPane;
import application.subPane.EditModePane;
import engine.CraftingStatus;
import engine.Engine;
import engine.EngineStatus;
import exceptions.ExceptionStatus;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
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

/**
 * The main scene of the program
 * @author keithMaxwell 延夏 埃尔德里基
 *
 */
public class ViewManagerPC extends ViewManager
{
	private static final double WIDTH = 750; 			// The width of the scene 
	private static final double HEIGHT = 700;			// The height of the scene 
	private static final double REC_WIDTH = 680;		// Width of the panes
	private static final double EDGE_GENERAL = 4.0;		// The general edge width of panes
	private static final double SKILL_HEIGHT = 270;		// The height of skill pane
	private static final double BAR_EDGE = 1.5;			// The width of progress and quality bars
	private static final double BAR_WIDTH = 400.0;		// The width of the bars
	private static final double BAR_HEIGHT = 30.0;		// The height of the bars
	private static final double CP_EDGE = 1.5;			// CP bar edge width
	private static final double CP_WIDTH = 150.0;		// CP bar width
	private static final double CP_HEIGHT = 15.0;		// CP bar height
	
	private static final String VERSION = "V1.6.1-S";	// The version of the program
	
	private static final Color TEXT_COLOR = Color.BLACK; // The general color of the text
	
	private Stage stage;						// Main stage
	private Scene mainScene;					// Main scene of the stage
	private AnchorPane mainPane;				// Main pane of the scene, covers everything
	private AnchorPane lastSkillAp;				// The anchor pane that displays the last skill used
	private GridPane iconContainer;				// The pane that stores all the skill icons/buttons
	private VBox mainContainer;					// The container that stores other panes
	private HBox buffContainer;					// The container that display buffs
	private Circle statusDisp;					// The circle that displays the crafting status
	private GridPane efficiencyDisp;				// The text that displays current efficiency
	private Text durabilityText;				// The text that displays current durability
	private Text round;							// The text that displays current round
	private Text skillDescription;				// The text that displays the skill(where the cursor points) description
	private Timeline tml = new Timeline();		// The timeline that stores GCD animation
	
	private ArrayList<Text> progText;		// 0=>Progress 1=>Quality 2=>CP 3=>Status 4=>Success
	private ArrayList<Rectangle> bars; 		// 0=>Progress 1=>Quality 2=>CP
	private ArrayList<TextField> inputTf; 	// The ArrayList that stores the TextFields for input
	private ArrayList<SkillIcon> skillIcons;	// ArrayList that stores all skillIcon objects
												// Makes it easier to operate
	private ConfigManager cm; 					// The config manager that loads/saves the config
	
	private CraftingHistoryPane ch;				
	private AdvancedSettingsPane asp;			
	private EditModePane emp;	
	
	public ViewManagerPC() {
		engine = new Engine(craftsmanship, control, cp, dura, tProg, tQlty, 
				rCraftsmanship, rControl, progressDifference, 
				qualityDifference, ch, seed, CraftingStatus.Mode.Expert);
		progText = new ArrayList<>();
		bars = new ArrayList<>();
		tm = new Timer();
		skillIcons = new ArrayList<>();
		inputTf = new ArrayList<>();
		
		cm = new ConfigManager(this, getEngine());
		mainPane = new AnchorPane();

		
		tm.startTimer();
				
		
		initSkillsList();
		initMainDisplay();
		initStage();
		
//		cm.importConfig(false);
		
		ch = new CraftingHistoryPane(this);  // the CraftingHistoryPane need the size of the 
											 // main stage so it's initialized at last
		tml.setOnFinished(e -> {
			updateAll();
		});
		
	}
	
	/**
	 * manually define the category of each skill......
	 */
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
	
	/**
	 * initiate the main stage
	 */
	private void initStage() {
		stage = new Stage();

		mainScene = new Scene(mainPane, WIDTH, HEIGHT);
		
		mainPane.setBackground(new Background(
				new BackgroundFill(Color.LIGHTGRAY, null, null)));

		stage.setTitle("FFXIV Crafting Simulator " + VERSION);
		stage.setScene(mainScene);
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> {			// Close other related windows
			closeSubPanes(true);
		});
		
	}
	
	/**
	 * initiate the main display contents
	 */
	private void initMainDisplay() {
		mainContainer = new VBox(20);
		
		mainContainer.setSpacing(10);
		mainContainer.setFillWidth(false);
		mainContainer.setAlignment(Pos.CENTER_LEFT);
		
		mainPane.getChildren().add(mainContainer);

		mainContainer.getChildren().add(initInput());
		mainContainer.getChildren().add(initInfoDisplay());
//		mainContainer.getChildren().add(initCPDisplay());
//		mainContainer.getChildren().add(initEffAndBuffDisplay());
//		mainContainer.getChildren().add(initBuffDisp());
		mainContainer.getChildren().add(initSkills());

		VBox.setMargin(efficiencyDisp, new Insets(0, 0, 0 , 140.0));
		
		AnchorPane.setTopAnchor(mainContainer, 10.0);
		AnchorPane.setLeftAnchor(mainContainer, 30.0);
		
		getEngine().setEngineStatus(EngineStatus.Pending);
	}
	
	/**
	 * initiate the input section
	 * @return the pane of input section
	 */
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
		Button loadConfig = new Button("加载配置");
		Button saveConfig = new Button("保存配置");
		
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
		
		ChoiceBox<String> cb = new ChoiceBox<String>();
		
		cb.getItems().add("高难");
		cb.getItems().add("普通");
		cb.getItems().add("无");
		
		inputTf.add(craftTf);
		inputTf.add(controlTf);
		inputTf.add(CPTf);
		inputTf.add(totalProgTf);
		inputTf.add(totalQltyTf);
		inputTf.add(totalDuraTf);
		
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

		cb.setValue("高难");
		
		// Define the action when confirm button is clicked
		confirm.setOnMouseClicked(e -> {
			CraftingStatus.Mode m = null;
			
			usedDebug = false;
			
			ch.destory();
			setLastSkill(null);
			
			craftsmanship = Integer.parseInt(craftTf.getText()); 
			control = Integer.parseInt(controlTf.getText()); 
			cp = Integer.parseInt(CPTf.getText());
			dura = Integer.parseInt(totalDuraTf.getText());
			tProg = Integer.parseInt(totalProgTf.getText()); 
			tQlty = Integer.parseInt(totalQltyTf.getText());
			ch = new CraftingHistoryPane(this);
			hasGCD = GCDCb.isSelected();
			
			if(cb.getValue().equals(cb.getItems().get(0))) {
				m = CraftingStatus.Mode.Expert;
			} else if(cb.getValue().equals(cb.getItems().get(1))) {
				m = CraftingStatus.Mode.Normal;
			} else {
				m = CraftingStatus.Mode.Testing;
			}
			
			engine = new Engine(craftsmanship, control, cp, dura, tProg, tQlty, 
					rCraftsmanship, rControl, progressDifference, 
					qualityDifference, ch, seed, m);
			// Creates a new engine to restart everything
			
			SkillIcon.setVm(getEngine(), tml, this);
			updateAll();
			ch.display();
			
			closeSubPanes(false);
		});
		
		// Define the action when advanced settings button is clicked
		advanced.setOnMouseClicked(e -> {
			if(asp != null) {
				asp.close();
			}
			asp = new AdvancedSettingsPane(t, this);
			asp.display();
		});
		// Define the action when export logs button is clicked
		logs.setOnMouseClicked(e -> {
			exportLogs();
		});
		

		// Define the action when finish button is clicked
		finish.setOnMouseClicked(e -> {		
			if(getEngine().getEngineStatus() == EngineStatus.Crafting) {
				postFinishMessage(ExceptionStatus.Craft_Failed);
			}
		});
		
		// Define the action when rearrange icon mapping button is clicked
		iconRearr.setOnMouseClicked(e -> { 
			if(emp == null) {
				emp = new EditModePane(this, getEngine());
			} 
			emp.setEngine(getEngine());
			emp.display();
		});
		
		// Define the action when save / load config button is clicked
		saveConfig.setOnMouseClicked(e -> {
			cm.exportConfig();
		});
		
		loadConfig.setOnMouseClicked(e -> {
			cm.importConfig(true);
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
		gp.add(cb, i, j + 2);
		i++;
		
		gp.add(confirm, i, j);
		gp.add(advanced, i, j + 1);
		gp.add(logs, i, j + 2);
		i++;
		
		i++;
		i++;
		gp.add(finish, i, j);
		gp.add(iconRearr, i, j + 1);
		i++;
		gp.add(loadConfig, i, j);
		gp.add(saveConfig, i, j + 1);
		i++;

		// Draw the edge of the pane
		border.setPrefWidth(REC_WIDTH + EDGE_GENERAL);
		border.add(back, 0, 0);
		border.setBackground(new Background(new BackgroundFill(Color.SILVER, new CornerRadii(10.0), null)));
		
		back.add(gp, 0, 0);
		back.setBackground(new Background(new BackgroundFill(Color.rgb(25,30,37,1.0), new CornerRadii(10.0), null)));
		
		GridPane.setMargin(back, new Insets(EDGE_GENERAL / 2));
		GridPane.setMargin(gp, new Insets(10));
		
		gp.autosize();		
		
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
	
	private Node initInfoDisplay() {
		GridPane gp = new GridPane();
		Rectangle rec1 = new Rectangle(REC_WIDTH, 2, Color.rgb(64, 64, 64));
		Rectangle rec2 = new Rectangle(REC_WIDTH, 2, Color.rgb(38, 38, 38));
		
		int i = 0;
		gp.add(initProgressBar(), 0, i++);
		gp.add(rec2, 0, i++);
		gp.add(rec1, 0, i++);
		gp.add(initEffAndBuffDisplay(), 0, i++);
		
		gp.setVgap(0);
		
		return gp;
	}
	
	/**
	 * Initiate the two progress bars and other related information display
	 * @return
	 */
	private Node initProgressBar() {
		GridPane container = new GridPane();
		GridPane left = new GridPane();
		GridPane right = new GridPane();
		AnchorPane progressBar = createBar(Color.DARKGREEN, BAR_WIDTH, BAR_HEIGHT, BAR_EDGE);
		AnchorPane qualityBar = createBar(Color.DARKBLUE, BAR_WIDTH, BAR_HEIGHT, BAR_EDGE);
		Text progressText = new Text(getEngine().getPresentProgress() + "/" + getEngine().getTotalProgress());
		Text qualityText = new Text(getEngine().getPresentQuality() + "/" + getEngine().getTotalQuality());
		ArrayList<Text> t = new ArrayList<Text>();
		
		GridPane lb = new GridPane();
		GridPane rt = new GridPane();
		Text status = new Text("通常　　");
		status.setFill(Color.WHITE);
		statusDisp = new Circle(10, Color.WHITE);
		
		durabilityText = new Text("耐久:  " + getEngine().getPresentDurability() + "/" + getEngine().getTotalDurability());
		round = new Text("工次:  " + getEngine().getRound());
		
		progText.add(progressText);
		progText.add(qualityText);
		progText.add(status);
		
		container.setAlignment(Pos.CENTER);
		left.setAlignment(Pos.CENTER);
		right.setAlignment(Pos.CENTER);
		
		lb.add(statusDisp, 0, 0);
		lb.add(status, 1, 0);
		
		left.add(durabilityText, 0, 0);
		left.add(round, 0, 1);
		left.add(lb, 0, 2);
		
		rt.add(progressBar, 1, 0);
		rt.add(progressText, 2, 0);
		rt.add(qualityBar, 1, 1);
		rt.add(qualityText, 2, 1);
		
		right.add(rt, 0, 0);
		right.add(initCPDisplay(), 0, 1);
		
		GridPane leftBack = new GridPane();
		leftBack.add(left, 0, 0);
		GridPane.setMargin(left, new Insets(10.0));
		
		leftBack.setBackground(new Background(
				new BackgroundFill(Color.rgb(34, 34, 34), new CornerRadii(10.0, 0, 0, 0, false), null)));
		
		GridPane rightBack = new GridPane();
		rightBack.add(right, 0, 0);
		GridPane.setMargin(right, new Insets(10.0));
		
		rightBack.setBackground(new Background(
				new BackgroundFill(Color.rgb(48, 48, 48), new CornerRadii(0, 10.0, 0, 0, false), null)));
		
		container.add(leftBack, 0, 0);
		container.add(rightBack, 1, 0);
		
		rt.setVgap(10.0);
		rt.setHgap(10.0);
		
		left.setVgap(20.0);
		right.setVgap(10.0);
		
		container.setHgap(20);
				
		t.add(durabilityText);
		t.add(round);
		t.add(progressText);
		t.add(qualityText);
		
		for(Text tx: t) {
			tx.setFill(Color.WHITE);
		}
		
		
		
		GridPane ap = new GridPane();
		ap.add(container, 0, 0);
		GridPane.setMargin(container, new Insets(0, 10.0, 0, 0));
		
		ap.setBackground(new Background(new BackgroundFill(Color.rgb(48, 48, 48), new CornerRadii(10.0, 10.0, 0, 0, false), null)));
//		container.setBackground(new Background(new BackgroundFill(Color.rgb(48, 48, 48), null, null)));
		
		return ap;
	}
	
	/**
	 * Initiate the CP display bar
	 * @return
	 */
	private Node initCPDisplay() {
		HBox container = new HBox();
		AnchorPane cpBar = createBar(Color.PURPLE, CP_WIDTH, CP_HEIGHT, CP_EDGE);
		Text cpVal = new Text(getEngine().getPresentCP() + "/" + getEngine().getTotalCP());
		Text success = new Text("Success!");
		Text cp = new Text("CP");
		ArrayList<Text> t = new ArrayList<Text>();

		
//		success.setFill(Color.rgb(48, 48, 48));
		success.setFont(Font.font(15));
		
		container.setAlignment(Pos.CENTER);
		
		
		progText.add(cpVal);
		progText.add(success);
		bars.get(2).setWidth(CP_WIDTH);
		
		container.getChildren().addAll(cp, cpBar, cpVal, success); //status, statusDisp,
		
		container.setSpacing(30);
		container.setLayoutX(10);
		
		t.add(cpVal);
		t.add(cp);
		
		for(Text tx: t) {
			tx.setFill(Color.WHITE);
		}
		
		GridPane ap = new GridPane();
		ap.add(container, 0, 0);
		GridPane.setMargin(container, new Insets(10.0));
		
//		ap.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
//		container.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
		
		return ap;
	} 
	
	private Node initEffAndBuffDisplay() {
		GridPane container = new GridPane();
		
		container.add(initBuffDisp(), 0, 0);
		container.add(initefficiencyDisp(), 0, 1);
		container.setVgap(10.0);
		
		GridPane ap = new GridPane();
		ap.setMinWidth(REC_WIDTH);
		ap.add(container, 0, 0);
		GridPane.setMargin(container, new Insets(10.0));

		ap.setBackground(new Background(new BackgroundFill(Color.rgb(32, 28, 32), new CornerRadii(0, 0, 10.0, 10.0, false), null)));
//		container.setBackground(new Background(new BackgroundFill(Color.YELLOW, null, null)));
		
		return ap;
	}
	
	/**
	 * Initiate the efficiency display text and previous skill display
	 * @return
	 */
	private Node initefficiencyDisp() {
		HBox container = new HBox();
		Text lastSkillT = new Text("上一个技能:  ");
		ArrayList<Text> t = new ArrayList<Text>();
		lastSkillAp = new AnchorPane();
		efficiencyDisp = new GridPane(); 
		
		Text line1 = new Text("   100%效率下的进展: " + getEngine().getBaseProgEff());
		Text line2 = new Text("   100%效率下的品质: " + getEngine().getBaseQltyEff());
		
		lastSkillAp.setPrefSize(39.0, 39.0);
		lastSkillAp.setMaxSize(39.0, 39.0);
		HBox.setMargin(lastSkillT, new Insets(0, 10.0, 0, 10.0));
		HBox.setMargin(lastSkillAp, new Insets(5.0, 30.0, 5.0, 0));
		HBox.setMargin(efficiencyDisp, new Insets(5.0, 30.0, 5.0, 0));
		
		efficiencyDisp.add(line1, 0, 0);
		efficiencyDisp.add(line2, 0, 1);
		
		efficiencyDisp.setVgap(8.0);
		
		container.setMinWidth(REC_WIDTH - 32.0);
		container.setAlignment(Pos.CENTER_LEFT);
		container.getChildren().addAll(lastSkillT, lastSkillAp, efficiencyDisp);
		
		t.add(lastSkillT);
		t.add(line1);
		t.add(line2);
		
		for(Text tx: t) {
			tx.setFill(Color.WHITE);
		}
		
//		container.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
		GridPane ap = new GridPane();
		ap.add(container, 0, 0);
		GridPane.setMargin(container, new Insets(2.0));
		
		ap.setBackground(new Background(
				new BackgroundFill(Color.rgb(0, 195, 249), new CornerRadii(5.0), null)));
		container.setBackground(new Background(
				new BackgroundFill(Color.rgb(30, 24, 30), new CornerRadii(5.0), null)));
		
		return ap;
	}
	
	/**
	 * Initiate the buffs display bar
	 * @return
	 */
	private Node initBuffDisp() {
		Text buffText = new Text("  Buff:");
		buffContainer = new HBox(10);
		buffContainer.setPrefHeight(32.0);
		
		buffContainer.getChildren().add(buffText);
		
		buffContainer.setMinHeight(40.0);
		
		buffText.setFont(Font.font(15.0));
		buffText.setFill(Color.WHITE);
		
		return buffContainer;
	}
	
	/**
	 * Initiate the skill icons display
	 * @return
	 */
	private Node initSkills() {
		GridPane skillContainer = new GridPane();
		AnchorPane border = new AnchorPane();	
		
		skillDescription = new Text(" ");
		iconContainer = new GridPane();
		
		skillContainer.setVgap(5);
		skillContainer.add(skillDescription, 0, 1);
//		GridPane.setHgrow(skillDescription, Priority.ALWAYS);
//		GridPane.setVgrow(skillDescription, Priority.ALWAYS);
		
		
		skillContainer.add(iconContainer, 0, 2);
				
		skillDescription.setFill(Color.WHITE);
		iconContainer.setHgap(5);
		
		int i = 2; // Makes it easier to code (easier to copy and paste)
		createSkillList(progressSkills, getIconContainer(), i++);
		createSkillList(qualitySkills, getIconContainer(), i++);
		createSkillList(buffSkills, getIconContainer(), i++);
		createSkillList(recoverySkills, getIconContainer(), i++);
		createSkillList(otherSkills, getIconContainer(), i++);
		
		border.getChildren().add(skillContainer);
		border.setPrefSize(REC_WIDTH + EDGE_GENERAL, SKILL_HEIGHT + EDGE_GENERAL);
		skillContainer.setPrefSize(REC_WIDTH, SKILL_HEIGHT);
		
		border.setBackground(new Background(
				new BackgroundFill(Color.WHITE, new CornerRadii(5.0), null)));
		skillContainer.setBackground(new Background(
				new BackgroundFill(Color.rgb(35, 35, 35), new CornerRadii(5.0), null)));
		
		
		
		AnchorPane.setLeftAnchor(skillContainer, EDGE_GENERAL / 2);
		AnchorPane.setTopAnchor(skillContainer, EDGE_GENERAL / 2);
		
		for(Node n: skillContainer.getChildren()) {
			GridPane.setMargin(n, new Insets(0, 0, 0, 10));
		}
		
		return border;
	}
	
	/**
	 * Initiate the skill icons display
	 * @param skl the list that store skills that needed to be displayer
	 * @param gp  the GridPane that stores all these icons
	 * @param i   the line which these skills are located
	 */
	private void createSkillList(List<Skill> skl, GridPane gp, int i) {
		int j = 1;
		for(Skill s: skl) {
			getEngine().addToLogs(s.toString() + ": " + s.getAddress());;
			
			SkillIcon si = new SkillIcon(s, tml, this);
			skillIcons.add(si);
			
			gp.add(si, j, i);
			j++;
		}
		
		// Fill the rest with empty ones
		for(; j <= 12; j++) { 
			SkillIcon si = new SkillIcon(null, tml, this);
			gp.add(si, j, i);
		}
		
		SkillIcon.setVm(getEngine(), tml, this);

		return;
	}
	
	/**
	 * The main method that draws the bar
	 * @param c the color of the bar (Color class is from javafx)
	 * @param width the width of the bar
	 * @param height the height of the bar
	 * @param paneEdge the edge thickness of the bar
	 * @return
	 */
	private AnchorPane createBar(Color c, double width, double height, double paneEdge) {
		AnchorPane bar = new AnchorPane();
		Rectangle edgeR = new Rectangle(width + 2 * paneEdge, height + 2 * paneEdge, Color.SILVER);
		Rectangle fill = new Rectangle(width, height, Color.rgb(32, 32, 32));
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
	
	private void closeSubPanes(boolean closeDisplayPane) {
		if(asp != null) {
			asp.close();
			if(asp.getDp() != null) {
				asp.getDp().close();
			}
		}
		if(emp != null) {
			emp.close();
		}
		if(closeDisplayPane) {
			if(ch != null) {
				ch.close();
			}
		}
	}
	
	/**
	 * Updates all the displays
	 */
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
		updateSkillCP();
	}
	
	public void updateProgress() {
		progText.get(0).setText(getEngine().getPresentProgress() + "/" + getEngine().getTotalProgress());
		if(getEngine().getPresentProgress()>=getEngine().getTotalProgress()) {
			bars.get(0).setWidth(BAR_WIDTH);
		} else {
			bars.get(0).setWidth((double)getEngine().getPresentProgress()/getEngine().getTotalProgress()*BAR_WIDTH);
		}	}
	
	public void updateQuality() {
		progText.get(1).setText(getEngine().getPresentQuality() + "/" + getEngine().getTotalQuality());
		if(getEngine().getPresentQuality()>=getEngine().getTotalQuality()) {
			bars.get(1).setWidth(BAR_WIDTH);
		} else {
			bars.get(1).setWidth((double)getEngine().getPresentQuality()/getEngine().getTotalQuality()*BAR_WIDTH);
		}
	}
	
	public void updateCP() {
		progText.get(3).setText(getEngine().getPresentCP() + "/" + getEngine().getTotalCP());
		if(getEngine().getPresentCP()>=getEngine().getTotalCP()) {
			bars.get(2).setWidth(CP_WIDTH);
		} else {
			bars.get(2).setWidth((double)getEngine().getPresentCP()/getEngine().getTotalCP()*CP_WIDTH);
		}
	}
	
	public void updateDur() {
		getEngine().addToLogs("Present dur: " + getEngine().getPresentDurability());
		durabilityText.setText("耐久:  " + getEngine().getPresentDurability()+ "/" + getEngine().getTotalDurability());
		round.setText("工次:  " + getEngine().getRound());;
		
	}
	
	public void updateEffDisp() {
		((Text)efficiencyDisp.getChildren().get(0)).setText("  100%效率下的进展: " + getEngine().getBaseProgEff());
		((Text)efficiencyDisp.getChildren().get(1)).setText("  100%效率下的品质: " + getEngine().getBaseQltyEff());

	}
	
	public void updateSuccess() {
		Text t = progText.get(4);
		System.out.println(t.getText());
		
		if(getEngine().isSkillSuccess()) {
			t.setText("Success!");
			t.setFill(Color.GREEN);
		} else {
			t.setText("Fail...");
			t.setFill(Color.RED);
		}
		
	}
	
	public void updateStatus() {
		progText.get(2).setText(engine.getCraftingStatus().getName());
		progText.get(2).setFill(engine.getCraftingStatus().getFxColor());
		statusDisp.setFill(engine.getCraftingStatus().getFxColor());
	}
	
	public void updateBuffDIsp() {
		Text buffText = new Text("  Buff:");
		
		buffText.setFill(Color.WHITE);
		
		buffContainer.getChildren().clear();
		buffContainer.getChildren().add(buffText);
		
		for(ActiveBuff ab: getEngine().getActiveBuffs()) {
			getEngine().addToLogs("refreshing buff display... " + ab.buff.toString() + " " + ab.getRemaining());
			
			AnchorPane ap = new AnchorPane();
			ImageView iv = null;
			if(ab.buff == Buff.inner_quiet) {
				String add = "/icons/Inner_Quiet_Icon/Inner_Quiet_" + ab.getRemaining() + ".png";
				iv = new ImageView(new Image(add, true));
			} else {
				iv = new ImageView(new Image(ab.buff.getAddress(), true));
			}
			Text remaining = new Text(Integer.toString(ab.getRemaining()));
			remaining.setFill(Color.WHITE);
			
			ap.getChildren().add(iv);
			ap.getChildren().add(remaining);

			buffContainer.getChildren().add(ap);
			HBox.setMargin(ap, new Insets(5.0, 0.0, 5.0, 0.0));
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
	
	private void updateSkillCP() {
		Iterator<Node> iter = iconContainer.getChildren().iterator();
		while(iter.hasNext()) {
			SkillIcon si = (SkillIcon)iter.next();
			if(si.getSkill()!=null) {
				int i = si.getSkill().getCPCost();
				i = (getEngine().getCraftingStatus() == CraftingStatus.Pliant ? (i+1)/2 : i);
				if(i!=0) {
					si.setCostText(Integer.toString(i));
				} else {
					si.setCostText(" ");
				}
			}
		}
	}
	
	/**
	 * pop up the crafting finished message box
	 * @param es
	 */
	public void postFinishMessage(ExceptionStatus es) {
		
		// Update the CP cost (otherwise it might be halved)
		Iterator<Node> iter = iconContainer.getChildren().iterator();
		while(iter.hasNext()) {
			SkillIcon si = (SkillIcon)iter.next();
			if(si.getSkill()!=null) {
				int i = si.getSkill().getCPCost();
				if(i!=0) {
					si.setCostText(Integer.toString(i));
				} else {
					si.setCostText(" ");
				}
			}
		}
		
		Alert al = new Alert(AlertType.INFORMATION);
		GridPane gp = new GridPane();
		Text DebugMode = new Text(usedDebug ? "使用过Debug" : "");
		Text GCDMode = new Text("GCD: " + (getHasGCD() ? "开启" : "关闭"));
		Text runTime = new Text("总用时:  " + Double.toString(getEngine().getRuntime()) + "秒");
		Text val = new Text("收藏价值:  " + getEngine().getPresentQuality() / 10);
		
		updateAll(); // update before taking summary
		
		getEngine().setEngineStatus(EngineStatus.Pending);;
		getEngine().addToLogs("========= Summary =========");
		getEngine().addToLogs("Used Debug: " + usedDebug);
		getEngine().addToLogs("Has GCD: " + hasGCD);
		getEngine().addToLogs("Status: " + es.toString());
		getEngine().addToLogs("Total time: " + getEngine().getRuntime());
		getEngine().addToLogs("Value: " + (getEngine().getPresentQuality() / 10));
		getEngine().addToLogs("Skill Points: " + getEngine().SPCalc());
		getEngine().addToLogs("===========================");
		
		al.setTitle(es == ExceptionStatus.Craft_Failed ? "制作失败...." : "制作成功！");
		al.setHeaderText(es == ExceptionStatus.Craft_Failed ? "啊呀，制作失败了...." : "恭喜，制作成功！");
		
		int i = 0;
		if(usedDebug) {
			gp.add(DebugMode, 0, i++);
		}
		gp.add(GCDMode, 0, i++);
		gp.add(runTime, 0, i++);
		gp.add(val, 0, i++);
		
		if(es == ExceptionStatus.Craft_Success) {		
			Text SP = new Text("技巧点数(暂译):  " + getEngine().SPCalc());
			gp.add(SP, 0, i++);	
		}
		
		al.getDialogPane().setExpandableContent(gp);
		al.getDialogPane().setExpanded(true);
		
		al.showAndWait();
	}
	
	/**
	 *  Pop up the invalid action message box
	 * @param es stores the error message
	 */
	public void postInvalidMessage(ExceptionStatus es) {
		Alert al = new Alert(AlertType.WARNING);
		
		al.setTitle("无法使用");
		al.setContentText(es.getMessage());
		
		al.showAndWait();
	}
	
	/**
	 *  Pop up unexpected error message (hasn't triggered yet
	 */
	public void postUnexpectedMessage() {
		Alert al = new Alert(AlertType.WARNING);
		
		al.setTitle("未知错误");
		al.setContentText("你是怎么触发的...");
		
		al.showAndWait();
	}
	
	/**
	 * Main function to export the logs, which is stored in engine
	 */
	public void exportLogs() {
		Alert al = new Alert(AlertType.INFORMATION);
		GridPane container = new GridPane();
		Text title = new Text("日志");
		TextArea logsOutput = new TextArea();
		
		logsOutput.setEditable(false);
		logsOutput.setWrapText(false);
		
		for(String s: getEngine().getLogs()) {
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
	
	public void updateProperties() {
		inputTf.get(0).setText(Integer.toString(craftsmanship));
		inputTf.get(1).setText(Integer.toString(control));
		inputTf.get(2).setText(Integer.toString(cp));
	}
	
	
	// == getters and setters ==
	public Stage getStage() {
		return stage;
	}
	
	public Text getSkillDescription() {
		return skillDescription;
	}

	public void setSkillDescription(Text skillDescription) {
		this.skillDescription = skillDescription;
	}
	
	public GridPane getIconContainer() {
		return iconContainer;
	}

	public void setIconContainer(GridPane iconContainer) {
		this.iconContainer = iconContainer;
	}
	
	public ArrayList<TextField> getInputTf()
	{
		return inputTf;
	}
}
