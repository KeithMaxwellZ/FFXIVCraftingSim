package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import application.ViewManagerPC;


public class Main extends Application {
	
	@Override
	public void start(Stage stage) throws Exception {
		ViewManagerPC vm = new ViewManagerPC();
		  
		stage = vm.getStage();
		stage.show();
		vm.showAbout();
	}

	public static void main(String[] args) {
		launch(args);
	}
}

//BuildPath
//<file name="C:\Program Files\Java\jdk1.8.0_221\lib\ant-javafx.jar"/>
//<file name="C:\Program Files\Java\jdk1.8.0_221\jre\lib\ext\jfxrt.jar"/>