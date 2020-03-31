package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import application.ViewManager;


public class Main extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		ViewManager vm = new ViewManager();
		  
		stage = vm.getStage();
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}

//BuildPath
//<file name="C:\Program Files\Java\jdk1.8.0_221\lib\ant-javafx.jar"/>
//<file name="C:\Program Files\Java\jdk1.8.0_221\jre\lib\ext\jfxrt.jar"/>