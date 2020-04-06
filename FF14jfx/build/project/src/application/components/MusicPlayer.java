package application.components;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javazoom.jl.player.Player;


public class MusicPlayer extends Thread
{
	private MediaPlayer player;
	public MusicPlayer() {
		try {
//			String source = new File("src\\application\\components\\Music.mp3").toURI().toString();
//			String source = "https://cdns-preview-4.dzcdn.net/stream/c-45e58da428510c6a90c423fef058ffd6-2.mp3";
//			player = new Player(getClass().getResourceAsStream("Music.mp3"));
//			player = new Player("https://cdns-preview-4.dzcdn.net/stream/c-45e58da428510c6a90c423fef058ffd6-2.mp3");
			Media m = new Media("https://cdns-preview-4.dzcdn.net/stream/c-45e58da428510c6a90c423fef058ffd6-2.mp3");
			player = new MediaPlayer(m);
		} catch(Exception ex) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Exception Dialog");
			alert.setHeaderText("Look, an Exception Dialog");
			alert.setContentText("Could not find file blabla.txt!");


			// Create expandable Exception.
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			String exceptionText = sw.toString();

			Label label = new Label("The exception stacktrace was:");

			TextArea textArea = new TextArea(exceptionText);
			textArea.setEditable(false);
			textArea.setWrapText(true);

			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);

			GridPane expContent = new GridPane();
			expContent.setMaxWidth(Double.MAX_VALUE);
			expContent.add(label, 0, 0);
			expContent.add(textArea, 0, 1);

			// Set expandable Exception into the dialog pane.
			alert.getDialogPane().setExpandableContent(expContent);

			alert.showAndWait();
		}
	}
	
	public void run()
	{
		play();
	}
	
	
	public void play() {
		System.out.println("played");
		try{
			player.play();
		} catch(Exception ex) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Exception Dialog");
			alert.setHeaderText("Look, an Exception Dialog");
			alert.setContentText("Could not find file blabla.txt!");


			// Create expandable Exception.
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			ex.printStackTrace(pw);
			String exceptionText = sw.toString();

			Label label = new Label("The exception stacktrace was:");

			TextArea textArea = new TextArea(exceptionText);
			textArea.setEditable(false);
			textArea.setWrapText(true);

			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);

			GridPane expContent = new GridPane();
			expContent.setMaxWidth(Double.MAX_VALUE);
			expContent.add(label, 0, 0);
			expContent.add(textArea, 0, 1);

			// Set expandable Exception into the dialog pane.
			alert.getDialogPane().setExpandableContent(expContent);

			alert.showAndWait();
		}
	}
}
