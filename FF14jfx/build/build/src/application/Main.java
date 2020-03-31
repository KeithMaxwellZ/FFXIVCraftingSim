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
