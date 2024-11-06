package dao;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
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
    private ComboBox<String> comboboxVivienda;
    @FXML
    private VBox vboxPisos;
    @FXML
    private DatePicker datepickerFechaN;
    @FXML
	private Button botonVolver;
    @FXML
    private Button botonCrearUsuario;
	  
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		//Texto que aparecerá en los TextFields
        txtDNI.setPromptText("Ingrese su DNI");
        txtcontrasenia.setPromptText("Ecriba una  contraseña");
        txtcontrasenia2.setPromptText("Repita su contraseña");
        txtnombre.setPromptText("Ecriba su nombre");
        txtapellidos.setPromptText("Ecriba su nombre");
		
        //Para rellenar el combobox con cada piso
        ObservableList<String> pisos = FXCollections.observableArrayList();
        for (int i = 1; i <= 7; i++) {
            pisos.add(i + "ºA"); //añade los pisos A
            pisos.add(i + "ºB"); //añade los pisos B
        }
        comboboxVivienda.setItems(pisos); //carga los pisos al combobox
        
	}
	
	
	
	@FXML
    void crearUsuario(ActionEvent event) { //BOTON AGREGAR USUARIO
		String nombre = txtnombre.getText();
		String apellidos = txtapellidos.getText();
		String dni = txtDNI.getText();
		String contrasenia = txtcontrasenia.getText();
		String contrasenia2 = txtcontrasenia.getText();
		String piso = comboboxVivienda.getAccessibleText();
		String fechaN = datepickerFechaN.getAccessibleText();
		
		

	}
	
	 
	
	@FXML
    void volver(ActionEvent event) { //BOTON VOLVER 
		//Carga la ventana anterior (inicio sesion)
		Stage escenario = (Stage) this.botonVolver.getScene().getWindow();
		escenario.close();
    }
	

}
