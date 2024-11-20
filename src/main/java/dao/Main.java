package dao;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	//Para cargar la primera pantalla (inicio sesion)
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/vista/VistaInicioSesion.fxml"));
        primaryStage.setTitle("INICIO SESION - CONVIVE"); //nombre de la ventana 
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
	
	public static void main(String[] args) {
		launch(args);
	}

}

