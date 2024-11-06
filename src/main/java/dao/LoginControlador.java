package dao;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.fxml.Initializable;

public class LoginControlador implements Initializable{
	@FXML
    private TextField txtDNI;
    @FXML
    private TextField txtapellidos;
    @FXML
    private PasswordField txtcontrasenia;
    @FXML
    private PasswordField txtcontrasenia2;
    @FXML
    private TextField txtnombre;
    @FXML
    private ComboBox<?> comboboxVivienda;
    @FXML
    private DatePicker datepickerFechaN;
    @FXML
	private Button botonVolver;
	  
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// Texto que aparecerá en los TextFields
        txtDNI.setPromptText("Ingrese su DNI");
        txtcontrasenia.setPromptText("Ecriba una  contraseña");
        txtcontrasenia2.setPromptText("Repita su contraseña");
        txtnombre.setPromptText("Ecriba su nombre");
        txtapellidos.setPromptText("Ecriba su nombre");
		
	}
	
	 @FXML
    void seleccionarVivienda(ActionEvent event) {
/*
 		Producto productoSeleccionado = sbComboBox.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            labelResultado.setText("Producto seleccionado: " + productoSeleccionado.getNombre());
        } else {
            labelResultado.setText("No ha seleccionado ningún producto.");
        }
 */
    }
	
	@FXML
    void volver(ActionEvent event) { //BOTON VOLVER 
		//Carga la ventana anterior (inicio sesion)
		Stage escenario = (Stage) this.botonVolver.getScene().getWindow();
		escenario.close();
    }

}
