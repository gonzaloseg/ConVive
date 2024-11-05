package dao;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.Initializable;

public class LoginControlador implements Initializable{

	  @FXML
	  private Button botonVolver;
	  
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}
	
	@FXML
    void volver(ActionEvent event) { //BOTON VOLVER 
		//Carga la ventana anterior (inicio sesion)
		Stage escenario = (Stage) this.botonVolver.getScene().getWindow();
		escenario.close();
    }

}
