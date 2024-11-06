package dao;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MiComunidadControlador {
	@FXML
    private Button btnVolver;
	
	// Acción del botón "volver" para cerrar la ventana actual
    @FXML
    void volver(ActionEvent event) {
        Stage escenario = (Stage) this.btnVolver.getScene().getWindow();
        escenario.close();
    }
}
