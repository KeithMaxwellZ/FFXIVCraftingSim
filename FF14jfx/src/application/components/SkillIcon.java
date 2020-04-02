package application.components;

import application.Engine;
import application.ViewManager;
import exceptions.CraftingException;
import exceptions.ExceptionStatus;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import skills.Skill;

public class SkillIcon extends AnchorPane
{
	private static Engine engine;
	private static ViewManager vm;
	private static Timeline tml;

	private Image icon;
	private Button b = new Button();
	private Text costText;
	private ImageView iv = new ImageView(icon);
	private Rectangle rec;
	private KeyValue kv1;
	private KeyValue kv2;
	private KeyFrame kf1;
	private KeyFrame kf2;
	
	private Skill s;
	
	private int[] pos;
	
	
	public SkillIcon(Skill s, Timeline tml, ViewManager vm) {
		this.s = s;
		
		icon = new Image(s.getAddress(), true);
		b = new Button();
		costText = new Text(s.getCPCost() != 0 ? Integer.toString(s.getCPCost()) : "");
		iv = new ImageView(icon);
		rec = new Rectangle(40.0, 40.0, Color.DARKGRAY);
		
		kv1 = new KeyValue(iv.opacityProperty(), 1.0);
		kv2 = new KeyValue(iv.opacityProperty(), 0.1);
		
		kf1 = new KeyFrame(Duration.millis(1900), kv1);
		kf2 = new KeyFrame(Duration.millis(1), kv2);
		
		iv.setSmooth(true);
		rec.setSmooth(true);
		
		tml.getKeyFrames().addAll(kf1, kf2);
		
		init();
	}
	
	private void init() {
		costText.setFill(Color.WHITE);
		
		getChildren().add(rec);
		getChildren().add(iv);
		getChildren().add(costText);
		getChildren().add(b);
		
		costText.setLayoutX(0);
		costText.setLayoutY(40.0);
		costText.setFont(Font.font(15));
		
		b.setLayoutX(0);
		b.setLayoutY(0);
		
		b.setPrefHeight(40);
		b.setPrefWidth(40);
		
		b.setOpacity(0.0);
		
		b.setOnMouseClicked(e -> {
			if(engine.isWorking()) {
				if(vm.getTimer().getTime() >= (vm.getHasGCD() ? 2.00 : 0)) {
					performSkill(getSkill(), iv);
				}
			} else {
				startWarning();
			}
		});
		
		this.setOnMouseEntered(e -> {
			vm.getSkillDescription().setText("  " + s.getName() + " " +
						(!s.getBaseProgressRate().equals("0.0%") ? "进度效率： " + s.getBaseProgressRate() : "") + " " +
						(!s.getBaseQualityRate().equals("0.0%") ? "品质效率： " + s.getBaseQualityRate() : "") + " " + 
						(s.getDurCost() != 0 ? "耐久消耗: " + s.getDurCost() : ""));
		});
		
		this.setOnMouseExited(e -> {
			vm.getSkillDescription().setText("");
		});
	}
	
	private void startWarning() {
		Alert al = new Alert(AlertType.WARNING);
		
		al.setTitle("未开始作业");
		al.setHeaderText(null);
		al.setContentText("请先按‘确认’键以开始作业");
		
		al.showAndWait();
	}
	
	private void performSkill(Skill sk, ImageView iv) {
		try {
			engine.useSkill(sk); 
			
			vm.getTimer().startTimer();
			vm.setLastSkill(sk);
			if(vm.getHasGCD()) {
				tml.play();
			}
		} catch (CraftingException e) {
			if(e.es == ExceptionStatus.Craft_Failed || e.es == ExceptionStatus.Craft_Success) {
				postFinishMessage(e.es);
			} else if (	e.es == ExceptionStatus.Not_HQ ||
						e.es == ExceptionStatus.No_Inner_Quiet ||
						e.es == ExceptionStatus.Inner_Quiet_Exists ||
						e.es == ExceptionStatus.Not_Turn_One ||
						e.es == ExceptionStatus.Waste_Not_Exist ||
						e.es == ExceptionStatus.No_Enough_CP ||
						e.es == ExceptionStatus.Maximun_Reached) {
				postInvalidMessage(e.es);
			} else {
				postUnexpectedMessage();
			}
		} finally {
			if(engine.isWorking() == true) {
				vm.updateAll();
			}
		}
	}
	
	private void postFinishMessage(ExceptionStatus es) {
		Alert al = new Alert(AlertType.INFORMATION);
		GridPane gp = new GridPane();
		Text GCDMode = new Text("GCD: " + (vm.getHasGCD() ? "开启" : "关闭"));
		Text runTime = new Text("总用时:  " + Double.toString(engine.getRuntime()) + "秒");
		Text val = new Text("收藏价值:  " + engine.getPresentQuality() / 10);
		
		vm.updateAll();
		
		engine.setWorking(false);
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
	
	public void refreshDisplay(Skill s) {
		this.s = s;
		iv.setImage(new Image(s.getAddress(), true));
	}
	
	public static void setVm(Engine e, Timeline timeLine, ViewManager viewManager) {
		engine = e;		
		tml = timeLine;
		vm = viewManager;
	}
	
	public Skill getSkill() {
		return s;
	}
	
	public void setSkill(Skill s) {
		this.s = s;
	}
	
	public int[] getPos() {
		return pos;
	}
	
	public void setPos(int[] pos) {
		this.pos = pos;
	}
	
	public static void switchPos(SkillIcon si1, SkillIcon si2) {
		Skill st = si1.getSkill();
		si1.setSkill(si2.getSkill());
		si2.setSkill(st);
		
		int[] pt = si1.getPos();
		si1.setPos(si2.getPos());
		si2.setPos(pt);
		
		si1.refreshDisplay(si1.getSkill());
		si2.refreshDisplay(si2.getSkill());
	}
}
