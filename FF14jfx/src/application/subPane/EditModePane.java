package application.subPane;

import java.util.Iterator;
import java.util.Optional;

import application.Engine;
import application.ViewManager;
import application.components.EngineStatus;
import application.components.SkillIcon;
import exceptions.CraftingException;
import exceptions.ExceptionStatus;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import skills.BuffSkill;
import skills.PQSkill;
import skills.Skill;
import skills.SpecialSkills;

public class EditModePane
{	
	public static final double BOX_WIDTH = 300.0;
	public static final double BOX_HEIGHT = 300.0;
	public static final double BUTTON_WIDTH = 260.0;
	public static final double BUTTON_HEIGHT = 80.0;

	private Stage emStage;
	private Scene emScene;
	private AnchorPane emPane;
	private GridPane gp;
	private Button iconRearr;
	private Button exportSettings;
	private Button importSettings;
	
	private ViewManager vm;
	private Engine engine;
	
	private boolean userGuide = true;
	
	public EditModePane(ViewManager vm, Engine engine) {
		this.vm = vm;
		this.engine = engine;
		
		emStage = new Stage();
		emPane = new AnchorPane();
		emScene = new Scene(emPane, BOX_WIDTH, BOX_HEIGHT);
		gp = new GridPane();
		iconRearr = new Button("编辑模式：关");
		exportSettings = new Button("导出键位");
		importSettings = new Button("导入键位");
		
		initDisplay();
	}
	
	private void initDisplay() {
		emStage.setTitle("编辑键位");
		emStage.setScene(emScene);
		
		iconRearr.setPrefWidth(BUTTON_WIDTH);
		iconRearr.setPrefHeight(BUTTON_HEIGHT);
		iconRearr.setOnMouseClicked(e -> {
			if(engine.getEngineStatus() == EngineStatus.Crafting) {
				vm.postInvalidMessage(ExceptionStatus.Now_Crafting);
			} else if(engine.getEngineStatus() == EngineStatus.Editing) {
				engine.setEngineStatus(EngineStatus.Pending);
				iconRearr.setText("编辑模式：关");
			} else if(engine.getEngineStatus() == EngineStatus.Pending) {
				engine.setEngineStatus(EngineStatus.Editing);
				iconRearr.setText("编辑模式：开");
			}
			if(userGuide) {
				displayGuide();
			}
		});
		
		exportSettings.setPrefWidth(BUTTON_WIDTH);
		exportSettings.setPrefHeight(BUTTON_HEIGHT);
		exportSettings.setOnMouseClicked(e -> {
			String s = exportCode();
			displayCode(s);
		});
		
		importSettings.setPrefWidth(BUTTON_WIDTH);
		importSettings.setPrefHeight(BUTTON_HEIGHT);
		importSettings.setOnMouseClicked(e -> {
			importCode("");
		});
		
		gp.add(iconRearr, 0, 0);
		gp.add(exportSettings, 0, 1);
		gp.add(importSettings, 0, 2);
		
		gp.setVgap(10.0);
		
		emPane.getChildren().add(gp);
		
		AnchorPane.setLeftAnchor(gp, 20.0);
		AnchorPane.setTopAnchor(gp, 20.0);
		
		emStage.setX(vm.getStage().getX() + vm.getStage().getWidth() + 10);
		emStage.setY(vm.getStage().getY()); 
		
		emStage.setOnCloseRequest(e -> {
			if(SkillIcon.getIcon1() != null) {
				SkillIcon.getIcon1().getIv().setOpacity(1.0);
			}
			engine.setEngineStatus(EngineStatus.Pending);
		});
	}
	
	private int findIndex(Skill[] sklist, Skill target) {
		for(int i = 0; i < sklist.length; i++) {
			if(sklist[i] == target) {
				return i;
			}
		}
		return -1;
	}
	
	private void displayGuide() {
		Alert al = new Alert(AlertType.CONFIRMATION);
		al.setTitle("提示");
		al.setHeaderText("关于编辑模式");
		TextArea  hintTf = new TextArea("点击选中一个空格/技能后，点击另一个即可交换他们的位置。" + '\n'
				+ "下次是否继续显示此提示？");
		hintTf.setEditable(false);
		hintTf.setWrapText(true);
		al.getDialogPane().setExpanded(true);
		al.getDialogPane().setExpandableContent(hintTf);
		Optional<ButtonType> result = al.showAndWait();
		if (result.get() == ButtonType.OK){
		    userGuide = true;
		} else {
		    userGuide = false;
		}
	}
	
	public void importCode(String s) {
		String raw = s;
		if(s.equals("")) {
			TextInputDialog dialog = new TextInputDialog("");
			dialog.setTitle("导入键位");
			dialog.setHeaderText(null);
			dialog.setContentText("请填入键位代码");
		
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()){
			    raw += result.get();
			} 
		}
		try {
			decode(raw);
		} catch (CraftingException ce) {
			Alert al = new Alert(AlertType.WARNING);
			
			al.setTitle("错误");
			al.setContentText(ce.es.getMessage());
			
			al.showAndWait();
		}
	}
	
	private void decode(String raw) throws CraftingException {
		if(raw.length() != 180) {
			throw new CraftingException(ExceptionStatus.Code_Error);
		} else {
			Iterator<Node> irr = vm.getIconContainer().getChildren().iterator();
			int i = 0;
			while(irr.hasNext()) {
				SkillIcon si = (SkillIcon)irr.next();
				si.refreshDisplay(si.getSkill());
				try {
					String type = raw.substring(i, i+1);
					String skl = raw.substring(i+1, i+3);
					System.out.println(i + " " + type + " " + skl);
					int typeI = Integer.parseInt(type);
					int sklI = Integer.parseInt(skl);
					if(typeI == 0) {
						si.setSkill(null);
						si.refreshDisplay(null);
					} else if(0 < typeI && typeI < 4) {
						if(typeI == 1) {
							si.setSkill(PQSkill.values()[sklI]);
						} else if(typeI == 2) {
							si.setSkill(BuffSkill.values()[sklI]);
						} else if(typeI == 3) {
							si.setSkill(SpecialSkills.values()[sklI]);
						}
						si.refreshDisplay(si.getSkill());
					} else {
						System.out.println("Type");
						throw new CraftingException(ExceptionStatus.Code_Error);
					}
				} catch(NumberFormatException e) {
					System.out.println("Format");
					throw new CraftingException(ExceptionStatus.Code_Error);
				} catch(IndexOutOfBoundsException e) {
					System.out.println("Index");
					throw new CraftingException(ExceptionStatus.Code_Error);
				}
				i += 3;
			}
		}
	}
	
	public String exportCode() {
		String s = "";
		Iterator<Node> irr = vm.getIconContainer().getChildren().iterator();
		while(irr.hasNext()) {
			Skill sk = ((SkillIcon)irr.next()).getSkill();
			if(sk != null) {
				if(sk instanceof PQSkill) {
					int i = findIndex(PQSkill.values(), sk);
					String t = (i < 10 ? "0" : "") + Integer.toString(i);
					s += ("1" + t);
				} else if (sk instanceof BuffSkill) {
					int i = findIndex(BuffSkill.values(), sk);
					String t = (i < 10 ? "0" : "") + Integer.toString(i);
					s += ("2" + t);
				} else if (sk instanceof SpecialSkills) {
					int i = findIndex(SpecialSkills.values(), sk);
					String t = (i < 10 ? "0" : "") + Integer.toString(i);
					s += ("3" + t);
				}
			} else {
				s += "000";
			}
		}
		
		return s;
	}
	
	public void displayCode(String s) {
		
		
		Alert al = new Alert(AlertType.INFORMATION);
		GridPane container = new GridPane();
		TextArea logsOutput = new TextArea();
		
		logsOutput.setEditable(false);
		logsOutput.setWrapText(true);
		
		logsOutput.setText(s);
		
		GridPane.setVgrow(logsOutput, Priority.ALWAYS);
		GridPane.setHgrow(logsOutput, Priority.ALWAYS);
		
		al.setTitle("导出键位");
		al.setHeaderText("导出成功");
		
		
		container.setMaxWidth(Double.MAX_VALUE);
		container.add(new Text("以下为键位代码"), 0, 0);
		container.add(logsOutput, 0, 1);
		
		al.getDialogPane().setExpandableContent(container);
		al.getDialogPane().setExpanded(true);
		
		al.showAndWait();
	}
	
	public void display() {
		emStage.show();
	}
	
	public void close() {
		emStage.close();
	}
}
