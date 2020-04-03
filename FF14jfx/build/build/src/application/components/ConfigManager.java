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

import application.Engine;
import application.ViewManager;
import application.subPane.EditModePane;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextArea;

public class ConfigManager
{
	private static final String CONFIG_NAME = "Config"; // property file name
	
	ViewManager vm;
	Engine engine;
	public ConfigManager(ViewManager vm, Engine engine) {
		this.vm = vm;
		this.engine = engine;
	}
	
	public void exportConfig() {
		Properties pt = new Properties();
		
		pt.setProperty("craftsmanship", vm.getInputTf().get(0).getText());
		pt.setProperty("control", vm.getInputTf().get(1).getText());
		pt.setProperty("CP", vm.getInputTf().get(2).getText());
		pt.setProperty("iconMapping", new EditModePane(vm, engine).exportCode());
		
		
		OutputStream os = null;
		
		try {
			os = new FileOutputStream(CONFIG_NAME);
			pt.store(os, "");
			os.close();
		} catch (IOException e) {
			Alert al = new Alert(AlertType.ERROR);
			TextArea ta = new TextArea();
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			String exceptionText = null;
			
			e.printStackTrace(pw);
			exceptionText = sw.toString();
			ta.appendText(exceptionText);
			
			al.setTitle("¥¢¥Ê ß∞‹");
			al.setHeaderText(null);
			al.setContentText("¥¢¥Ê ß∞‹ «Î÷ÿ ‘");
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
			
			pt.load(in);
			
			vm.setCraftsmanship(Integer.parseInt(pt.getProperty("craftsmanship")));
			vm.setControl(Integer.parseInt(pt.getProperty("control")));
			vm.setCP(Integer.parseInt(pt.getProperty("CP")));
			
			emp.importCode(pt.getProperty("iconMapping"));
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
				
				al.setTitle("∂¡»° ß∞‹");
				al.setHeaderText(null);
				al.setContentText("≈‰÷√Œƒº˛≤ª¥Ê‘⁄ªÚ∏Ò Ω¥ÌŒÛ");
				al.getDialogPane().setExpandableContent(ta);
				al.getDialogPane().setExpanded(false);
				
				al.showAndWait();
			}
			return;
		} 
	}
}
