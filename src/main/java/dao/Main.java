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
/*
¿Por qué así no funciona?
 * 
@Override
public void start(Stage primaryStage) {
	try {
		
		//crear objeto cargar de la clase fxml
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaInicioSesion.fxml"));
		//cargar ventana Inicio sesion
		AnchorPane root = loader.load();					
				
		//BorderPane root = new BorderPane(); //viene por defecto 
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		primaryStage.setTitle("INICIO SESION"); //Título
		primaryStage.setScene(scene);
		primaryStage.show();
		
	} catch(Exception e) {
		e.printStackTrace();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
}*/
