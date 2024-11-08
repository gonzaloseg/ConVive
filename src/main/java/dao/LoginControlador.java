package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
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
    @FXML private TextField txtTutor;
    @FXML private ComboBox<String> comboboxVivienda;
    @FXML private VBox vboxPisos;
    @FXML private DatePicker datepickerFechaN;
    @FXML private Button botonVolver;
    @FXML private Button botonNuevoVecino;
    

    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializa textos de sugerencia en los campos
        txtDNI.setPromptText("Ingrese su DNI");
        txtcontrasenia.setPromptText("Escriba una contraseña");
        txtcontrasenia2.setPromptText("Repita su contraseña");
        txtnombre.setPromptText("Escriba su nombre");
        txtapellidos.setPromptText("Escriba su apellido");
        txtTutor.setPromptText("Introduzca el dni de un adulto");
        
        
        // Rellena el ComboBox con opciones de pisos
        ObservableList<String> pisos = FXCollections.observableArrayList();
        for (int i = 1; i <= 7; i++) {
            pisos.add(i + "ºA");
            pisos.add(i + "ºB");
        }
        comboboxVivienda.setItems(pisos);
    }

    
    
    @FXML
    void crearNuevoVecino(ActionEvent event) {
    	
    	//Lee los datos introducidos
        String dni = txtDNI.getText().trim();
        String nombre = txtnombre.getText().trim();
        String apellidos = txtapellidos.getText().trim();
        String contrasenia = txtcontrasenia.getText();
        String contrasenia2 = txtcontrasenia2.getText();
        String vivienda = comboboxVivienda.getValue();
        LocalDate fechaNacimiento = datepickerFechaN.getValue();
        String tutor = txtTutor.getText();

/*VALIDACIONES*/
        
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
                if (!Character.isLetter(dni.charAt(i))) { //compueba si el caracter es distento a una letra
                    numerosValidos = false;
                    break;
                }
            }
            boolean letraValida = Character.isLetter(dni.charAt(8)); //El boleano es true cuando cumple la condición
        	
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
        
        char [] letrasValidas = {'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 
        					'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'};
        char letracorrecta = letrasValidas[numDni%23];
        
        if (letracorrecta != dni.charAt(8)) {
        	new Alert(Alert.AlertType.ERROR, "Formato de dni incorrecto").showAndWait();
			return;
        }
        
        
        // Valida que ambas contraseñas coincidan
        if (!contrasenia.equals(contrasenia2)) {
            mostrarAlerta("Las contraseñas no coinciden.");
            return;
        }
/**/
        
        // Determina si el vecino es mayor de edad
        boolean esMayorDeEdad = calcularMayorDeEdad(fechaNacimiento); //true si es mayor

        
        // Inserta los datos en la base de datos
        try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive")) {
        	int filasAfectadas;
        	
        	// Verifica si ya existe un vecino con el mismo DNI
        	if (dniExiste(conn, dni)) {
                mostrarAlerta("Ya existe un usuario registrado con este DNI en la comunidad.");
                return;
            }
            
        	if (esMayorDeEdad == true ) {
        		String sql = "INSERT INTO adulto (dni, nombre, apellidos, contrasenia, piso , fecha_nacimiento) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);

                // Rellena los parámetros de la consulta SQL
                pst.setString(1, dni);
                pst.setString(2, nombre);
                pst.setString(3, apellidos);
                pst.setString(4, contrasenia);
                pst.setString(5, vivienda);
                pst.setDate(6, java.sql.Date.valueOf(fechaNacimiento));

                filasAfectadas = pst.executeUpdate();
                
        	}else { //necesita un tutor 
        		
        		if (tutor.isEmpty()) {
                    mostrarAlerta("Añade el dni de un tutor");
                    return;
                }
        		
	            String sql = "INSERT INTO menor (dni, nombre, apellidos, contrasenia, piso , fecha_nacimiento, tutor) VALUES (?, ?, ?, ?, ?, ?, ?)";
	            PreparedStatement pst = conn.prepareStatement(sql);
	
	            // Rellena los parámetros de la consulta SQL
	            pst.setString(1, dni);
	            pst.setString(2, nombre);
	            pst.setString(3, apellidos);
	            pst.setString(4, contrasenia);
	            pst.setString(5, vivienda);
	            pst.setDate(6, java.sql.Date.valueOf(fechaNacimiento));
	            pst.setString(7, tutorMenor(conn, tutor)); //metodo abajo
	            
	            filasAfectadas = pst.executeUpdate();
        	}
        	
            // Confirma si la inserción fue exitosa
            if (filasAfectadas > 0) {
            	new Alert (Alert.AlertType.INFORMATION, "Usuario creado correctamente.").showAndWait();
            	
            	//Cuando el usuario se crea te lleva al home (ventana principal)
                try {
                    // Cargar la ventana del perfil
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaPrincipal.fxml"));
                    AnchorPane root = loader.load();

                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.setTitle("Mi Perfil - ConVive");
                    stage.centerOnScreen();
                    stage.show();

                    Stage currentStage = (Stage) botonNuevoVecino.getScene().getWindow();
                    currentStage.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            	
       
            } else {
            	new Alert (Alert.AlertType.ERROR, "No se puedo crear el usuario").showAndWait();
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
    

    
//Añade el id del tutor segun su dni
    private String tutorMenor (Connection conn, String dni) throws SQLException {
    	String sql = "SELECT id FROM adulto WHERE dni = ?";
    	String idTutor = "";
    	
    	try (PreparedStatement pst = conn.prepareStatement(sql)) {
    		pst.setString(1, dni);
			try (ResultSet rs= pst.executeQuery()){
				if (rs.next()) {
					 idTutor = rs.getString("id");
				}
			}	
		}
		return idTutor;		
    }

    
    
    // Muestra una alerta con el mensaje especificado cuando es un error
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
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


	
	 
	
	
