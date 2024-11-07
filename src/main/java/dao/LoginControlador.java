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
    
    // Campos de entrada para los datos del vecino
    @FXML private TextField txtDNI;
    @FXML private TextField txtapellidos;
    @FXML private TextField txtcontrasenia;
    @FXML private TextField txtcontrasenia2;
    @FXML private TextField txtnombre;
    @FXML private ComboBox<String> comboboxVivienda;
    @FXML private VBox vboxPisos;
    @FXML private DatePicker datepickerFechaN;
    @FXML private Button botonVolver;
    @FXML private Button botonCrearUsuario;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializa textos de sugerencia en los campos
        txtDNI.setPromptText("Ingrese su DNI");
        txtcontrasenia.setPromptText("Escriba una contraseña");
        txtcontrasenia2.setPromptText("Repita su contraseña");
        txtnombre.setPromptText("Escriba su nombre");
        txtapellidos.setPromptText("Escriba su apellido");
        
        // Rellena el ComboBox con opciones de pisos
        ObservableList<String> pisos = FXCollections.observableArrayList();
        for (int i = 1; i <= 7; i++) {
            pisos.add(i + "ºA");
            pisos.add(i + "ºB");
        }
        comboboxVivienda.setItems(pisos);
    }

    // Método para crear un nuevo vecino en la base de datos
    public void crearNuevoVecino() {
        String dni = txtDNI.getText().trim();
        String nombre = txtnombre.getText().trim();
        String apellidos = txtapellidos.getText().trim();
        String contrasenia = txtcontrasenia.getText();
        String contrasenia2 = txtcontrasenia2.getText();
        String vivienda = comboboxVivienda.getValue();
        LocalDate fechaNacimiento = datepickerFechaN.getValue();

        // Validaciones iniciales para campos vacíos
        if (dni.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || contrasenia.isEmpty() ||
                vivienda == null || fechaNacimiento == null) {
            mostrarAlerta("Todos los campos deben estar completos.");
            return;
        }
        
        
        //Validar que el dni tenga 8 num y una letra
        if (dni.length() == 9) {
            boolean numerosValidos = true;
            for (int i = 0; i < 8; i++) {
                if (!Character.isDigit(dni.charAt(i))) {
                    numerosValidos = false;
                    break;
                }
            }
            boolean letraValida = Character.isLetter(dni.charAt(8));
        	
            if (!numerosValidos && !letraValida) {
            	new Alert(Alert.AlertType.ERROR, "El dni se debe componer de 8 numeros y una letra mayuscula").showAndWait();
    			return;
            }
        	
        }else {
        	new Alert(Alert.AlertType.ERROR, "El dni se debe componer de 8 numeros y una letra mayuscula").showAndWait();
			return;
        }

        
        //Validar dni en formato correcto
        int numDni = Integer.parseInt(dni.substring(0, 8)); // coge los num del dni
        char letraDni;
        boolean valido;
        
        switch (numDni) {
	        case 0: letraDni = 'T'; valido = true; break;
	        case 1: letraDni = 'R'; valido = true; break;
	        case 2: letraDni = 'W'; valido = true; break;
	        case 3: letraDni = 'A'; valido = true; break;
	        case 4: letraDni = 'G'; valido = true; break;
	        case 5: letraDni = 'M'; valido = true; break;
	        case 6: letraDni = 'Y'; valido = true; break;
	        case 7: letraDni = 'F'; valido = true; break;
	        case 8: letraDni = 'P'; valido = true; break;
	        case 9: letraDni = 'D'; valido = true; break;
	        case 10: letraDni = 'X'; valido = true; break;
	        case 11: letraDni = 'B'; valido = true; break;
	        case 12: letraDni = 'N'; valido = true; break;
	        case 13: letraDni = 'J'; valido = true; break;
	        case 14: letraDni = 'Z'; valido = true; break;
	        case 15: letraDni = 'S'; valido = true; break;
	        case 16: letraDni = 'Q'; valido = true; break;
	        case 17: letraDni = 'V'; valido = true; break;
	        case 18: letraDni = 'H'; valido = true; break;
	        case 19: letraDni = 'L'; valido = true; break;
	        case 20: letraDni = 'C'; valido = true; break;
	        case 21: letraDni = 'K'; valido = true; break;
	        case 22: letraDni = 'E'; valido = true; break;
	        default: valido = false; 
		}
        
        if (valido == false) {
			new Alert(Alert.AlertType.ERROR, "Formato de dni incorrecto").showAndWait();
			return;
        }
        
        
        // Valida que ambas contraseñas coincidan
        if (!contrasenia.equals(contrasenia2)) {
            mostrarAlerta("Las contraseñas no coinciden.");
            return;
        }

        // Determina si el vecino es mayor de edad
        boolean esMayorDeEdad = calcularMayorDeEdad(fechaNacimiento);

        // Selecciona la tabla en función de la edad
        String tabla = esMayorDeEdad ? "adulto" : "menor";

        // Inserta los datos en la base de datos
        try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive")) {
        	// Verifica si ya existe un vecino con el mismo DNI
        	if (dniExiste(conn, dni)) {
                mostrarAlerta("Ya existe un usuario registrado con este DNI en la comunidad.");
                return;
            }
            
            // Consulta para insertar en la tabla correspondiente
            String sql = "INSERT INTO " + tabla + " (dni, nombre, apellidos, contrasenia, piso , fecha_nacimiento) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(sql);

            // Rellena los parámetros de la consulta SQL
            pst.setString(1, dni);
            pst.setString(2, nombre);
            pst.setString(3, apellidos);
            pst.setString(4, contrasenia);
            pst.setString(5, vivienda);
            pst.setDate(6, java.sql.Date.valueOf(fechaNacimiento));

            int filasAfectadas = pst.executeUpdate();

            // Confirma si la inserción fue exitosa
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

    // Método para verificar si el DNI ya existe en ambas tablas (adulto y menor)
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
        return false; // Retorna false si el DNI no está en ninguna tabla
    }

    // Calcula si una persona es mayor de edad (18 años o más)
    private boolean calcularMayorDeEdad(LocalDate fechaNacimiento) {
        Period edad = Period.between(fechaNacimiento, LocalDate.now());
        return edad.getYears() >= 18;
    }

    // Muestra una alerta con el mensaje especificado
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Acción del botón "volver" para cerrar la ventana actual
    @FXML
    void volver(ActionEvent event) {
        Stage escenario = (Stage) this.botonVolver.getScene().getWindow();
        escenario.close();
    }
}


	
	 
	
	
