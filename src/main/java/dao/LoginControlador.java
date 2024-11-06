package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class LoginControlador implements Initializable {
    
    @FXML private TextField txtDNI;
    @FXML private TextField txtapellidos;
    @FXML private  TextField txtcontrasenia;
    @FXML private TextField txtcontrasenia2;
    @FXML private TextField txtnombre;
    @FXML private ComboBox<String> comboboxVivienda;
    @FXML private VBox vboxPisos;
    @FXML private DatePicker datepickerFechaN;
    @FXML private Button botonVolver;
    @FXML private Button botonCrearUsuario;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicialización de campos y ComboBox
        txtDNI.setPromptText("Ingrese su DNI");
        txtcontrasenia.setPromptText("Escriba una contraseña");
        txtcontrasenia2.setPromptText("Repita su contraseña");
        txtnombre.setPromptText("Escriba su nombre");
        txtapellidos.setPromptText("Escriba su apellido");
        
        ObservableList<String> pisos = FXCollections.observableArrayList();
        for (int i = 1; i <= 7; i++) {
            pisos.add(i + "ºA");
            pisos.add(i + "ºB");
        }
        comboboxVivienda.setItems(pisos);
    }

    public void crearNuevoVecino() {
        String dni = txtDNI.getText().trim();
        String nombre = txtnombre.getText().trim();
        String apellidos = txtapellidos.getText().trim();
        String contrasenia = txtcontrasenia.getText();
        String contrasenia2 = txtcontrasenia2.getText();
        String vivienda = comboboxVivienda.getValue();
        LocalDate fechaNacimiento = datepickerFechaN.getValue();

        // Validaciones iniciales
        if (dni.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || contrasenia.isEmpty() ||
                vivienda == null || fechaNacimiento == null) {
            mostrarAlerta("Todos los campos deben estar completos.");
            return;
        }

        if (!contrasenia.equals(contrasenia2)) {
            mostrarAlerta("Las contraseñas no coinciden.");
            return;
        }

        // Verifica si es mayor de edad
        boolean esMayorDeEdad = calcularMayorDeEdad(fechaNacimiento);

        // Selección de tabla en función de la edad
        String tabla = esMayorDeEdad ? "adulto" : "menor";

        // Inserción en la base de datos
        try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive")) {
        	if (dniExiste(conn, dni)) {
                mostrarAlerta("Ya existe un usuario registrado con este DNI en la comunidad.");
                return;
            }
            String sql = "INSERT INTO " + tabla + " (dni, nombre, apellidos, contrasenia, piso , fecha_nacimiento) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            pst.setString(1, dni);
            pst.setString(2, nombre);
            pst.setString(3, apellidos);
            pst.setString(4, contrasenia);
            pst.setString(5, vivienda);
            pst.setDate(6, java.sql.Date.valueOf(fechaNacimiento));

            int filasAfectadas = pst.executeUpdate();

            if (filasAfectadas > 0) {
                mostrarAlerta("Usuario creado correctamente en la tabla " + tabla + ".");
            } else {
                mostrarAlerta("No se pudo crear el usuario.");
            }
            

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al conectarse con la base de datos: " + e.getMessage());
        }
    }
    private boolean dniExiste(Connection conn, String dni) throws SQLException {
        String query = "SELECT COUNT(*) FROM adulto WHERE dni = ? UNION ALL SELECT COUNT(*) FROM menor WHERE dni = ?";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setString(1, dni);
            pst.setString(2, dni);
            
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) > 0) { // Si el conteo es mayor a 0, el DNI ya existe
                    return true;
                }
            }
        }
        return false; // Si no se encontró el DNI en ninguna tabla, retorna false
    }

    private boolean calcularMayorDeEdad(LocalDate fechaNacimiento) {
        Period edad = Period.between(fechaNacimiento, LocalDate.now());
        return edad.getYears() >= 18;
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    void volver(ActionEvent event) {
        // Regresar a la ventana anterior
        Stage escenario = (Stage) this.botonVolver.getScene().getWindow();
        escenario.close();
    }
}

	
   

	
	 
	
	
