package application.components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;

import application.ViewManagerPC;
import application.components.LogManager.Node;
import exceptions.CraftingException;
import exceptions.ExceptionStatus;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import skills.BuffSkill;
import skills.PQSkill;
import skills.Skill;
import skills.SpecialSkills;

public class ReplayManager
{
	private Timeline tml;
	private Skill nextSkill;
	
	private ViewManagerPC vm;
	private LinkedList<Skill> skillQueue;
	
	private boolean autoPlay;
	
	private String replayPath;
	
	
	public ReplayManager(ViewManagerPC vm) {
		this.vm = vm;
		autoPlay = false;
	}
	
	public void saveReplay(int craftsmanship, int control, int cp, long seed,
			int value, int sp, int totalRound, ArrayList<Node> nodes) {
		String fileName = "V" + Integer.toString(value) + "_SP" + Integer.toString(sp) + "_T" + totalRound; 
		File f = new File("./replay/" + fileName + ".rep");
		System.out.println(f.getPath());
		System.out.println(f.getParentFile().exists());
		if(!f.getParentFile().exists()) {
			f.getParentFile().mkdir();
		}
		
		int i = 1;
		while(f.exists()) {
			f = new File("./replay/" + fileName + "_" + i + ".rep" );
			i++;
		}
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try
		{		
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			bw.write("REP\n");
			bw.write(Integer.toString(craftsmanship) + "\n");
			bw.write(Integer.toString(control) + "\n");
			bw.write(Integer.toString(cp) + "\n");
			bw.write(Long.toString(seed) + "\n");
			for(Node n: nodes) {
				Skill s = n.getSkill();
				String category = "";
				int index = s.getSkillIndex();
				if(s instanceof PQSkill) {
					category = "p";
				} else if(s instanceof BuffSkill) {
					category = "b";
				} else if(s instanceof SpecialSkills) {
					category = "s";
				} else {
					// Exception, maybe later update
				}				
				bw.write(category + Integer.toString(index) + "\n");
				bw.flush();
			}
			bw.write("f");
			bw.flush();
			bw.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		Alert al = new Alert(AlertType.INFORMATION);
		
		al.setTitle("保存成功");
		al.setTitle(null);
		al.setContentText("录像 " + f.getName() + " 已保存在/replay文件夹中");
		
		al.showAndWait();
	}
	
	public void load() throws CraftingException {
		if(chooseReplay()) {
			loadReplay();
			initDisplay();
		}
	}
	
	private boolean chooseReplay() {
		Alert al = new Alert(AlertType.INFORMATION);
		al.setTitle("选择录像");
		al.setHeaderText("请选择要读取的录像");
		
		File file = new File("./replay");   
	    File[] array = file.listFiles();
	    
	    ObservableList<String> fileNames = FXCollections.observableArrayList();
	    for(File f: array) {
	    	fileNames.add(f.getName());
	    }
	    
		ListView<String> lv = new ListView<String>(fileNames);
		lv.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		al.getDialogPane().setExpandableContent(lv);
		al.getDialogPane().setExpanded(true);
		
		Optional<ButtonType> result = al.showAndWait();
		if (result.get() == ButtonType.OK){
			int index = lv.getSelectionModel().getSelectedIndex();
			
			if(index == -1) {
				return false;
			}
			
		    replayPath = array[index].getPath();
			return true;
		} else {
		    return false;
		}
	}
	
	private void loadReplay() throws CraftingException {
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(replayPath)));
			
			if(!br.readLine().equals("REP")) {
				br.close();
				throw new CraftingException(ExceptionStatus.Replay_loading_Error);
			}
			
			int craftsmanship = Integer.parseInt(br.readLine());
			int control = Integer.parseInt(br.readLine());
			int cp = Integer.parseInt(br.readLine());
			long seed = Long.parseLong(br.readLine());
			skillQueue = new LinkedList<>();
			String ts = br.readLine();
			
			vm.setCraftsmanship(craftsmanship);
			vm.setControl(control);
			vm.setCP(cp);
			vm.setSeed(seed);
			vm.setHasGCD(false);
			
			while(!ts.equals("f")) {
				System.out.println(ts);
				char tc = ts.charAt(0);
				if(tc == 'p') {
					skillQueue.add(PQSkill.values()[Integer.parseInt(ts.substring(1, ts.length()))]);
				} else if(tc == 'b') {
					skillQueue.add(BuffSkill.values()[Integer.parseInt(ts.substring(1, ts.length()))]);
				} else if(tc == 's') {
					skillQueue.add(SpecialSkills.values()[Integer.parseInt(ts.substring(1, ts.length()))]);
				} else {
					br.close();
					throw new CraftingException(ExceptionStatus.Replay_loading_Error);
				}
				ts = br.readLine();
			}
			
			br.close();
			
			
		} catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initDisplay() {
		Stage stage = new Stage();
		GridPane gp = new GridPane();
		Scene scene = new Scene(gp, 200.0, 60.0);
		
		Button start = new Button("play");
		Button stop = new Button("stop");
		Button next = new Button("next");
		
		stage.setScene(scene);
		
		gp.add(start, 0, 0);
		gp.add(stop, 1, 0);
		gp.add(next, 2, 0);
		
		GridPane.setMargin(start, new Insets(10.0));
		GridPane.setMargin(stop, new Insets(10.0));
		GridPane.setMargin(next, new Insets(10.0));
		
		nextSkill = skillQueue.poll();
		start.setOnMouseClicked(e -> {
//			play(nextSkill);
			tml.play();
			autoPlay = true;
		});
		
		stop.setOnMouseClicked(e -> {
			autoPlay = false;
			tml.stop();
		});
		
		next.setOnMouseClicked(e -> {
			play(nextSkill);
		});
		
		KeyValue kv1 = new KeyValue(gp.opacityProperty(), 1.0);

		
		KeyFrame kf1 = new KeyFrame(Duration.seconds(2.0), kv1);
		KeyFrame kf2 = new KeyFrame(Duration.seconds(2.0), kv1);
		
		tml = new Timeline();
		tml.getKeyFrames().addAll(kf1, kf2);
				
		tml.setOnFinished(e -> {
			System.out.println(nextSkill.toString());
			if(nextSkill != null) {
				if(autoPlay) {
					play(nextSkill);
					if(nextSkill != null) {
						tml.play();
					}
				}
			}
		});
		
		stage.setAlwaysOnTop(true);
		stage.show();
	}
	
	public void play(Skill sk) {
		System.out.println("calledplay");
		for(SkillIcon si: vm.getSkillIcons()) {
			if(si.getSkill() == sk) {
					si.clicked();
					nextSkill = skillQueue.poll();
					return;
			}
		}
	}
}
