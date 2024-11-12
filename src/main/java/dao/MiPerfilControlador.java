package dao;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MiPerfilControlador {
	@FXML
    private Button botonVolver;
	 @FXML
	    private ImageView img_volver;

	    public void initialize() {
	        // Asigna un evento de clic a img_volver
	        img_volver.setOnMouseClicked(event -> volver(new ActionEvent()));
	    }

	    @FXML
	    void volver(ActionEvent event) {
	        try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaPrincipal.fxml"));
	            AnchorPane root = loader.load();

	            Scene scene = new Scene(root);
	            Stage stage = new Stage();
	            stage.setScene(scene);
	            stage.setTitle("Principal - ConVive");
	            stage.show();

	            // Cerrar la ventana actual
	            Stage currentStage = (Stage) botonVolver.getScene().getWindow();
	            currentStage.close();

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
}
