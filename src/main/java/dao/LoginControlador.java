package dao;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
		String contrasenia2 = txtcontrasenia2.getText();
		String piso = comboboxVivienda.getAccessibleText(); //si getAccesibleText no funciona, usar getValue()
		String fechaN = datepickerFechaN.getAccessibleText();
		
		// comprueba si hay algun campo vacío
	    if (nombre.isEmpty() || apellidos.isEmpty() || dni.isEmpty() || contrasenia.isEmpty() || contrasenia2.isEmpty() || piso == null || fechaN.isEmpty()) {
    		new Alert (Alert.AlertType.WARNING, "Completa todos los campos").showAndWait();;
	        return;
	    }
	    
	    // comprueba si las contraseñas coinciden
	    if (!contrasenia.equals(contrasenia2)) {
    		new Alert (Alert.AlertType.WARNING, "Las contraseñas deben coincidir").showAndWait();;
	    	return;
	    }
	}
	/* Ainhoa -- tengo que mirarlo bien
	private boolean crearUsuarioEnBD(String nombre, String apellidos, String dni, String contrasenia, String piso, String fechaN) {
        // Cadena de conexión a la base de datos
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // SQL para insertar el nuevo usuario
            String sql = "INSERT INTO usuarios (nombre, apellidos, dni, contrasenia, piso, fecha_nacimiento) VALUES (?, ?, ?, ?, ?, ?)";
            
            // Preparar la sentencia SQL
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, nombre);
                stmt.setString(2, apellidos);
                stmt.setString(3, dni);
                stmt.setString(4, contrasenia);  // Aquí deberías cifrar la contraseña antes de almacenarla
                stmt.setString(5, piso);
                stmt.setString(6, fechaN);
                
                // Ejecutar la consulta
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0; // Si se insertó al menos un registro, retorna true
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Si ocurre un error, retornamos false
        }
    }
    */

	
	 
	
	@FXML
    void volver(ActionEvent event) { //BOTON VOLVER 
		//Carga la ventana anterior (inicio sesion)
		Stage escenario = (Stage) this.botonVolver.getScene().getWindow();
		escenario.close();
    }
}
