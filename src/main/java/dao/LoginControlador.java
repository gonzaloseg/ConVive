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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
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

import dto.UsuarioGlobal;
import javafx.fxml.Initializable; //lola

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
    
    @FXML private Label labelErrorDni;
    @FXML private Label labelErrorContrasenia;
    @FXML private Label labelErrorNombre;
    @FXML private Label labelErrorApellidos;
    @FXML private Label labelErrorTutor;
    
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializa textos de sugerencia en los campos
        txtDNI.setPromptText("Ingrese su DNI");
        txtcontrasenia.setPromptText("Escriba una contraseña");
        txtcontrasenia2.setPromptText("Repita su contraseña");
        txtnombre.setPromptText("Escriba su nombre");
        txtapellidos.setPromptText("Escriba su apellido");
        txtTutor.setPromptText("Introduzca el dni de un adulto");

        // Agrega el listener para verificar el DNI en tiempo real
        txtDNI.setOnKeyReleased(event -> validarDNIEnTiempoReal());
        txtnombre.setOnKeyReleased(event -> validarNombre());        
        txtapellidos.setOnKeyReleased(event -> validarApellidos());
        txtcontrasenia.textProperty().addListener((observable, oldValue, newValue) -> validarContrasenias());
        txtcontrasenia2.textProperty().addListener((observable, oldValue, newValue) -> validarContrasenias());
        txtTutor.setOnKeyReleased(event -> validarTutor());

        // Rellena el ComboBox con opciones de pisos
        ObservableList<String> pisos = FXCollections.observableArrayList();
        for (int i = 1; i <= 7; i++) {
            pisos.add(i + "ºA");
            pisos.add(i + "ºB");
        }
        comboboxVivienda.setItems(pisos);
    }
    
    boolean esMayorDeEdad ;

    
    //Método para ver que el nombre este escrito solo con letras y espacios
    private void validarNombre () {
    	String nombre = txtnombre.getText();
    	
    	if (nombre != null && nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
    		txtnombre.setStyle("-fx-border-color: green;"); //Bordes verdes si esta escrito bien
    		labelErrorNombre.setText("");
    	}else {
    		txtnombre.setStyle("-fx-border-color: red;"); //Bordes rojos si no esta escrito bien
    		labelErrorNombre.setText("Solo puede contener letras");
    	}
    }
    
    
    
    //Método para ver que los apellidos este escrito solo con letras y espacios
    private void validarApellidos () {
    	String apellidos = txtapellidos.getText();
    	
    	if (apellidos != null && apellidos.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
    		txtapellidos.setStyle("-fx-border-color: green;"); //Bordes verdes si esta escrito bien
    		labelErrorApellidos.setText("");
    	}else {
    		txtapellidos.setStyle("-fx-border-color: red;"); //Bordes rojos si no esta escrito bien
    		labelErrorApellidos.setText("Solo puede contener letras");
    	}
    }
    
    
    
    // Método para validar el DNI en tiempo real
    private void validarDNIEnTiempoReal() {
        String dni = txtDNI.getText().trim();

        // Validación: debe tener 9 caracteres en total, con 8 números y 1 letra al final
        if (dni.length() == 9) {
            boolean numerosValidos = dni.substring(0, 8).chars().allMatch(Character::isDigit); // Verifica los primeros 8 caracteres
            boolean letraValida = Character.isLetter(dni.charAt(8)); // Verifica el último carácter como letra

            // Validación de la letra del DNI según el algoritmo
            if (numerosValidos && letraValida) {
                int numDni = Integer.parseInt(dni.substring(0, 8));
                char[] letrasValidas = {'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X',
                        'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'};
                char letraCorrecta = letrasValidas[numDni % 23];

             // Verifica si la letra es correcta
                if (dni.charAt(8) == letraCorrecta) {
                    txtDNI.setStyle("-fx-border-color: green;"); // Bordes verdes si el DNI es válido
                    labelErrorDni.setText("");
                } else {
                    txtDNI.setStyle("-fx-border-color: red;"); // Bordes rojos si la letra es incorrecta
                    labelErrorDni.setText("Letra del dni incorrecta");
                }
            } else {
                txtDNI.setStyle("-fx-border-color: red;"); // Bordes rojos si el formato es incorrecto
                labelErrorDni.setText("Formato de dni incorrecto");
            }
        } else {
            txtDNI.setStyle("-fx-border-color: red"); // Bordes rojos si no tiene la longitud correcta
            labelErrorDni.setText("Longitud del dni incorrecto");
        }
    }
    
    
    
    //Método para validar que las contraseñas son iguales 
    private void validarContrasenias() {
    	String contrasenia = txtcontrasenia.getText();
    	String contrasenia2 = txtcontrasenia2.getText();

    	if (contrasenia.equals(contrasenia2)) {
    		txtcontrasenia.setStyle("-fx-border-color: green;"); //Bordes verdes si las contraseñas coinciden
            txtcontrasenia2.setStyle("-fx-border-color: green;"); 
            labelErrorContrasenia.setText("");
        }else {
        	txtcontrasenia.setStyle("-fx-border-color: red;");//Bordes rojos si las contraseñas no coinciden
            txtcontrasenia2.setStyle("-fx-border-color: red;"); 
            labelErrorContrasenia.setText("Las contraseñas no coinciden");
        }
    }
    
    
    
    //Método para validar que si es menor tenga un tutor 
    private void validarTutor() {
    	String tutor = txtTutor.getText();
        LocalDate fechaNacimiento = datepickerFechaN.getValue();
        esMayorDeEdad = calcularMayorDeEdad(fechaNacimiento);
		if (esMayorDeEdad) {
			
		}else{
	    	if (tutor.isEmpty()) {
	    		txtTutor.setStyle("-fx-border-color: red;"); //Bordes rojos si no hay tutor 
	    		labelErrorTutor.setText("Los menores deben tener un tutor");
	    	}else {
	    		if (tutor.length() == 9) {
	                boolean numerosValidos = tutor.substring(0, 8).chars().allMatch(Character::isDigit); // Verifica los primeros 8 caracteres
	                boolean letraValida = Character.isLetter(tutor.charAt(8)); // Verifica el último carácter como letra

	                // Validación de la letra del DNI según el algoritmo
	                if (numerosValidos && letraValida) {
	                    int numDni = Integer.parseInt(tutor.substring(0, 8));
	                    char[] letrasValidas = {'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X',
	                    					'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'};
	                    char letraCorrecta = letrasValidas[numDni % 23];

	                 // Verifica si la letra es correcta
	                    if (tutor.charAt(8) == letraCorrecta) {
	                        txtTutor.setStyle("-fx-border-color: green;"); // Bordes verdes si el DNI es válido
	                        labelErrorTutor.setText("");
	                    } else {
	                    	txtTutor.setStyle("-fx-border-color: red;"); // Bordes rojos si la letra es incorrecta
	                        labelErrorTutor.setText("Letra del dni incorrecta");
	                    }
	                } else {
	                	txtTutor.setStyle("-fx-border-color: red;"); // Bordes rojos si el formato es incorrecto
	                    labelErrorTutor.setText("Formato de dni incorrecto");
	                }
	            } else {
	            	txtTutor.setStyle("-fx-border-color: red"); // Bordes rojos si no tiene la longitud correcta
	                labelErrorTutor.setText("Longitud del dni incorrecto");
	            }
	    		
	    	
	    	}
		}
    }

    @FXML
    void crearNuevoVecino(ActionEvent event) {
    	
        String dni = txtDNI.getText().trim();
        String nombre = txtnombre.getText().trim();
        String apellidos = txtapellidos.getText().trim();
        String contrasenia = txtcontrasenia.getText();
        String contrasenia2 = txtcontrasenia2.getText();
        String vivienda = comboboxVivienda.getValue();
        LocalDate fechaNacimiento = datepickerFechaN.getValue();
        String tutor = txtTutor.getText();

        if (dni.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || contrasenia.isEmpty() ||
                vivienda == null || fechaNacimiento == null) {
            mostrarAlerta("Todos los campos deben estar completos.");
            return;
        }
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
        	
        } else {
            new Alert(Alert.AlertType.ERROR, "El dni se debe componer de 8 numeros y una letra mayuscula").showAndWait();
            return;
        }

        int numDni = Integer.parseInt(dni.substring(0, 8));
        char[] letrasValidas = {'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 
                'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E'};
        char letracorrecta = letrasValidas[numDni % 23];
        
        if (letracorrecta != dni.charAt(8)) {
            new Alert(Alert.AlertType.ERROR, "Formato de dni incorrecto").showAndWait();
            return;
        }

        if (!contrasenia.equals(contrasenia2)) {
            mostrarAlerta("Las contraseñas no coinciden.");
            return;
        }
        
        
        

        esMayorDeEdad = calcularMayorDeEdad(fechaNacimiento);

        


        try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive")) {
            int filasAfectadas;

            /*if (dniExiste(conn, dni)) {
                mostrarAlerta("Ya existe un usuario registrado con este DNI en la comunidad.");
                return;
            }*/
            
            if (esMayorDeEdad) {
                String sql = "INSERT INTO adulto (dni, nombre, apellidos, contrasenia, piso , fecha_nacimiento) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);

                pst.setString(1, dni);
                pst.setString(2, nombre);
                pst.setString(3, apellidos);
                pst.setString(4, contrasenia);
                pst.setString(5, vivienda);
                pst.setDate(6, java.sql.Date.valueOf(fechaNacimiento));

                filasAfectadas = pst.executeUpdate();
                
            } else {
                if (tutor.isEmpty()) {
                    mostrarAlerta("Añade el dni de un tutor");
                    return;
                }
        		
                String sql = "INSERT INTO menor (dni, nombre, apellidos, contrasenia, piso , fecha_nacimiento, tutor) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement pst = conn.prepareStatement(sql);
                
                pst.setString(1, dni);
                pst.setString(2, nombre);
                pst.setString(3, apellidos);
                pst.setString(4, contrasenia);
                pst.setString(5, vivienda);
                pst.setDate(6, java.sql.Date.valueOf(fechaNacimiento));
                pst.setString(7, tutorMenor(conn, tutor));

                filasAfectadas = pst.executeUpdate();
            }
            
            if (filasAfectadas > 0) {
            	
            	UsuarioGlobal.getInstacne().setDniGlobal(dni);
                new Alert (Alert.AlertType.INFORMATION, "Usuario creado correctamente.").showAndWait();
                
                try {
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
                new Alert (Alert.AlertType.ERROR, "No se pudo crear el usuario").showAndWait();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private boolean calcularMayorDeEdad(LocalDate fechaNacimiento) {
        LocalDate fechaActual = LocalDate.now();
        Period periodo = Period.between(fechaNacimiento, fechaActual);
        return periodo.getYears() >= 18;
    }

    private boolean dniExiste(Connection conn, String dni) throws SQLException {
        String query = "SELECT * FROM adulto WHERE dni = ? UNION ALL SELECT * FROM menor WHERE dni = ?";
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setString(1, dni);
        pst.setString(2, dni);
        ResultSet rs = pst.executeQuery();
        
        int count = 0;
        while (rs.next()) {
            count += rs.getInt(1);
        }
        return count > 0;
    }
    
    private String tutorMenor(Connection conn, String tutor) throws SQLException {
        String query = "SELECT dni FROM adulto WHERE dni = ?";
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setString(1, tutor);
        ResultSet rs = pst.executeQuery();
        
        if (rs.next()) {
            return tutor;
        } else {
            mostrarAlerta("No existe un tutor con este dni registrado en el sistema.");
            throw new SQLException("Tutor no registrado");
        }
    }

    
    // Acción del botón "volver" para cerrar la ventana actual
    @FXML
    void volver(ActionEvent event) {
        Stage escenario = (Stage) this.botonVolver.getScene().getWindow();
        escenario.close();
    }
}