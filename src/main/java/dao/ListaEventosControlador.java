package dao;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ListaEventosControlador {
	
	@FXML
    private Button botonVolver;
	
    @FXML
    void volver(ActionEvent event) { //BOTON VOLVER AL HOME
    	
    	try {
        
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaPrincipal.fxml"));
            AnchorPane root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Principal - ConVive");
            stage.show();

            Stage currentStage = (Stage) botonVolver.getScene().getWindow();
            currentStage.close(); //cerrar la ventana mi perfil

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
