package br.com.guisi.simulador.trafego;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import br.com.guisi.simulador.trafego.view.MainViewController;

public class SimuladorApp extends Application {

	public static void main(String args[]) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		
		FXMLLoader loader = new FXMLLoader();
        try {
        	Pane node = loader.load(getClass().getResourceAsStream("/fxml/TelaPrincipal.fxml"));
			Scene scene = new Scene(node);
			stage.setScene(scene);
			stage.show();
			
			MainViewController controller = loader.getController();
			stage.setOnCloseRequest((event) -> controller.cancelTasks());
        } catch (IOException e) {
        	throw new RuntimeException("Unable to load FXML file", e);
        }
	}
	
}