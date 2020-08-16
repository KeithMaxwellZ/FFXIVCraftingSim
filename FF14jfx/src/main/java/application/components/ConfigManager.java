package application.components;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Properties;

import application.ViewManagerPC;
import application.subPane.EditModePane;
import engine.Engine;
import engine.EngineStatus;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

public class ConfigManager
{
	private static final String CONFIG_NAME = "Config"; // property file name
	
	ViewManagerPC vm;
	Engine engine;
	public ConfigManager(ViewManagerPC vm, Engine engine) {
		this.vm = vm;
		this.engine = engine;
	}
	
	public void exportConfig() {
		Properties pt = new Properties();
		
		pt.setProperty("craftsmanship", vm.getInputTf().get(0).getText());
		pt.setProperty("control", vm.getInputTf().get(1).getText());
		pt.setProperty("CP", vm.getInputTf().get(2).getText());
		pt.setProperty("iconMapping", new EditModePane(vm, engine).exportCode());
		pt.setProperty("HotkeyBinding", vm.exportHotkeyBinding());
		
		OutputStream os = null;
		
		try {
			Alert al = new Alert(AlertType.INFORMATION);

			os = new FileOutputStream(CONFIG_NAME);
			pt.store(os, "");
			os.close();
			
			al.setTitle("完成");
			al.setHeaderText(null);
			al.setContentText("成功导出配置文件");
		} catch (IOException e) {
			Alert al = new Alert(AlertType.ERROR);
			TextArea ta = new TextArea();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			String exceptionText = null;
			
			e.printStackTrace(pw);
			exceptionText = sw.toString();
			ta.appendText(exceptionText);
			
			al.setTitle("储存失败");
			al.setHeaderText(null);
			al.setContentText("储存失败 请重试");
			al.getDialogPane().setExpandableContent(ta);
			al.getDialogPane().setExpanded(false);
			
			al.showAndWait();
		}
	}
	
	public void importConfig(boolean showDialog) {
		Properties pt = new Properties();
		
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(CONFIG_NAME));  
			EditModePane emp = new EditModePane(vm, engine);
			Alert al = new Alert(AlertType.INFORMATION);

			pt.load(in);
			
			vm.setCraftsmanship(Integer.parseInt(pt.getProperty("craftsmanship")));
			vm.setControl(Integer.parseInt(pt.getProperty("control")));
			vm.setCP(Integer.parseInt(pt.getProperty("CP")));
			vm.importHotkeyBinding(pt.getProperty("HotkeyBinding"));
			
			vm.importPlayerData(pt.getProperty("craftsmanship"), pt.getProperty("control"), pt.getProperty("CP"));
			
			if(vm.getEngine().getEngineStatus() == EngineStatus.Crafting) {
				
			} else {
				emp.importCode(pt.getProperty("iconMapping"));
			}
			
			
			al.setTitle("完成");
			al.setHeaderText(null);
			al.setContentText("成功导入配置文件");
		} catch (IOException e) {
			if(showDialog) {
				Alert al = new Alert(AlertType.ERROR);
				TextArea ta = new TextArea();
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				String exceptionText = null;
				
				e.printStackTrace(pw);
				exceptionText = sw.toString();
				ta.appendText(exceptionText);
				
				al.setTitle("读取失败");
				al.setHeaderText(null);
				al.setContentText("配置文件不存在或格式错误");
				al.getDialogPane().setExpandableContent(ta);
				al.getDialogPane().setExpanded(false);
				
				al.showAndWait();
			}
			return;
		} 
	}
}
