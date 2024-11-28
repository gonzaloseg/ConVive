package dao;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application {
	
	//Para cargar la primera pantalla (inicio sesion)
	@Override
	public void start(Stage primaryStage) throws Exception {
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaInicioSesion.fxml"));
	    AnchorPane root = loader.load();
	    Scene scene = new Scene(root);
	    
	    Image icon = new Image(getClass().getResourceAsStream("/imagenes/icono.png"));
	    primaryStage.getIcons().add(icon);

	    Image cursorImage = new Image(getClass().getResourceAsStream("/imagenes/cursor.png"));
	    scene.setCursor(new ImageCursor(cursorImage));

	    primaryStage.setTitle("Inicio de sesión - ConVive");
	    primaryStage.setScene(scene);
	    primaryStage.show();
	}


}

