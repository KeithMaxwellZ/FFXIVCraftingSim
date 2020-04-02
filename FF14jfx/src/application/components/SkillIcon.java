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

	private static SkillIcon icon1;
	private static SkillIcon icon2;
	
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
		
		b = new Button();
		
		if(s == null) {
			icon = new Image("/icons/Blank.png");
			costText = new Text(" ");
			iv = new ImageView(icon);
			rec = new Rectangle(40.0, 40.0, Color.DARKGRAY);
			
			getIv().setOpacity(0.0);
			rec.setOpacity(0.0);
		} else {
			icon = new Image(s.getAddress(), true);
			costText = new Text(s.getCPCost() != 0 ? Integer.toString(s.getCPCost()) : "");
			iv = new ImageView(icon);
			rec = new Rectangle(40.0, 40.0, Color.DARKGRAY);
		}
		
		kv1 = new KeyValue(getIv().opacityProperty(), 1.0);
		kv2 = new KeyValue(getIv().opacityProperty(), 0.1);
		
		kf1 = new KeyFrame(Duration.millis(1900), kv1);
		kf2 = new KeyFrame(Duration.millis(1), kv2);
		
		getIv().setSmooth(true);
		rec.setSmooth(true);
		
		tml.getKeyFrames().addAll(kf1, kf2);
		
		init();
	}
	
	private void init() {
		costText.setFill(Color.WHITE);
		
		getChildren().add(rec);
		getChildren().add(getIv());
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
			if(engine.getEngineStatus() == EngineStatus.Editing) {
				if(getIcon1() == null) {
					setIcon1(this);
					if(s == null) {
						rec.setOpacity(0.5);
					} else {
						iv.setOpacity(0.3);
					}
				} else {
					getIcon1().getIv().setOpacity(1.0);
					icon2 = this;
					SkillIcon.switchPos(getIcon1(), icon2);
					setIcon1(null);
					icon2 = null;
				}
			} else if(engine.getEngineStatus() == EngineStatus.Crafting) {
				if(s != null && vm.getTimer().getTime() >= (vm.getHasGCD() ? 2.00 : 0)) {
					performSkill(getSkill(), getIv());
				}
			} else if(s == null) {
				// Do nothing
			} else {
				startWarning();
			}
		});
		
		this.setOnMouseEntered(e -> {
			if(s != null) {
				vm.getSkillDescription().setText("  " + s.getName() + " " +
							(!s.getBaseProgressRate().equals("0.0%") ? "进度效率： " + s.getBaseProgressRate() : "") + " " +
							(!s.getBaseQualityRate().equals("0.0%") ? "品质效率： " + s.getBaseQualityRate() : "") + " " + 
							(s.getDurCost() != 0 ? "耐久消耗: " + s.getDurCost() : ""));
			}
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
				vm.postFinishMessage(e.es);
			} else if (	e.es == ExceptionStatus.Not_HQ ||
						e.es == ExceptionStatus.No_Inner_Quiet ||
						e.es == ExceptionStatus.Inner_Quiet_Exists ||
						e.es == ExceptionStatus.Not_Turn_One ||
						e.es == ExceptionStatus.Waste_Not_Exist ||
						e.es == ExceptionStatus.No_Enough_CP ||
						e.es == ExceptionStatus.Maximun_Reached) {
				vm.postInvalidMessage(e.es);
			} else {
				vm.postUnexpectedMessage();
			}
		} finally {
			if(engine.getEngineStatus() == EngineStatus.Crafting) {
				vm.updateAll();
			}
		}
	}
	
	public void refreshDisplay(Skill sk) {
		this.s = sk;
		if(this.s == null) {
			getIv().setImage(new Image("/icons/Blank.png", true));
			rec.setOpacity(0.0);
			costText.setText("");
		} else {
			getIv().setImage(new Image(s.getAddress(), true));
			getIv().setOpacity(1.0);
			rec.setOpacity(1.0);
			costText.setText(Integer.toString(s.getCPCost()));
		}
		
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

	public static SkillIcon getIcon1()
	{
		return icon1;
	}

	public static void setIcon1(SkillIcon icon1)
	{
		SkillIcon.icon1 = icon1;
	}

	public ImageView getIv()
	{
		return iv;
	}

}
