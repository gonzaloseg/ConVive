package dao;

import dto.Actividades;
import dto.UsuarioGlobal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ResourceBundle;

import BaseDeDatos.Conexion;

public class NuevaActividadControlador implements Initializable{

    @FXML private Button botonAgregarActividad;
    @FXML private Button botonVolver;
    
    @FXML private DatePicker dateFechaActividad;
    @FXML private TextArea txtDescripcionActividad;
    @FXML private Spinner<Integer> spinnerEdadMin;  // Cambiado a Spinner
    @FXML private Spinner<Integer> spinnerEdadMax;  // Cambiado a Spinner
    @FXML private TextField txtHoraActividad;
    @FXML private TextField txtLugarActividad;
    @FXML private TextField txtNombreActividad;
    
    @FXML private Label labelErrorNombre;
    @FXML private Label labelErrorHora;
    @FXML private Label labelErrorEdad;

/*INITIALIZE*/
    @Override
	public void initialize(URL location, ResourceBundle resources) {
    	
    	// Inicializa textos de sugerencia en los campos
    	txtDescripcionActividad.setPromptText("Describe en que va a consistir tu actividad");
    	txtLugarActividad.setPromptText("Indica donde se realizara la actividad");
    	txtHoraActividad.setPromptText("Indica a que hora se realizará la actividad");
    	txtNombreActividad.setPromptText("Ponle un nombre a tu actividad");
    	
    	//El valor minimi del spinner sera 0 y el maximo 99
    	spinnerEdadMin.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, 0));
        spinnerEdadMax.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, 0));
    	
        // Agrega el listener para verificar el DNI en tiempo real
        txtNombreActividad.setOnKeyReleased(event -> validarNombre());
        txtHoraActividad.setOnKeyReleased(event -> validarHora());
        spinnerEdadMax.setOnKeyReleased(event -> validarEdad());
        spinnerEdadMin.setOnKeyReleased(event -> validarEdad());
	}
    
    
    
/*ALERTAS*/
    private void alertaError(String mensaje) {
    	Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    private void alertaInformacion(String mensaje) {
    	Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Información");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    
    
/*VALIDACIONES A TIEMPO REAL*/
    private void validarNombre() {
    	String nombre = txtNombreActividad.getText();
    	
    	if (nombre != null && nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
    		txtNombreActividad.setStyle("-fx-border-color: green;"); //Bordes verdes si esta escrito bien
    		labelErrorNombre.setText("");
    	}else {
    		txtNombreActividad.setStyle("-fx-border-color: red;"); //Bordes rojos si no esta escrito bien
    		labelErrorNombre.setText("Solo puede contener letras");
    	}
    }
    
    private void validarHora() {
        String hora = txtHoraActividad.getText();
        LocalTime horaActividad = null;
        
        try {
            horaActividad = LocalTime.parse(hora);
            txtHoraActividad.setStyle("-fx-border-color: green;");
        	labelErrorHora.setText("");
        } catch (Exception e) {
        	txtHoraActividad.setStyle("-fx-border-color: red;");
        	labelErrorHora.setText("Formato de hora incorrecto");
        }
    }
    
    private void validarEdad() {
    	int edadMin = spinnerEdadMin.getValue();  
        int edadMax = spinnerEdadMax.getValue(); 
        
    	if (edadMin > edadMax) {
        	spinnerEdadMax.setStyle("-fx-border-color: red;");
        	spinnerEdadMin.setStyle("-fx-border-color: red;");
        	labelErrorEdad.setText("La edad minima no puede ser mayor que la maxima");
        }else {
        	spinnerEdadMax.setStyle("-fx-border-color: green;");
        	spinnerEdadMin.setStyle("-fx-border-color: green;");
        	labelErrorEdad.setText("");
        }
    }
    
    
/*BOTON AGREGAR NUEVA ACTIVIDAD*/    
    @FXML
    void agregarActividad(ActionEvent event) { 
    	String nombre = txtNombreActividad.getText();
        LocalDate fecha = dateFechaActividad.getValue();
        String hora = txtHoraActividad.getText();
        String descripcion = txtDescripcionActividad.getText();
        int edadMin = spinnerEdadMin.getValue();  // Obtener valor de Spinner
        int edadMax = spinnerEdadMax.getValue();  // Obtener valor de Spinner
        String lugar = txtLugarActividad.getText();
        
        
        // Validar campos vacios 
        if (nombre.isEmpty() || fecha == null || hora.isEmpty() || descripcion.isEmpty() || lugar.isEmpty()) {            
        	alertaError("Se deben rellenar todos los campos");
            return;
        }
        
        //Validar el nombre bien escrito
        if (nombre == null && nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
        	alertaError("Solo pueden aparecer letras en el nombre");
    	}

        // Validar el formato de la hora
        LocalTime horaActividad = null;
        try {
            horaActividad = LocalTime.parse(hora);
        } catch (Exception e) {
        	alertaError("La hora no tiene un formato válido (hh:mm)");
            return;
        }

        // Comprobar que la edad mínima no sea mayor que la edad máxima
        if (edadMin > edadMax) {
        	alertaError("La edad mínima no puede ser mayor que la edad máxima.");
            return;
        }
        
        
        //AÑADIR NUEVA ACTIVIDAD A LA BBDD
        try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive")){
        	int filasAfectadas;
        	
        	String sql = "INSERT INTO actividad (nombre, descripcion, fecha, hora, lugar, edad_min, edad_max, creador) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";
            PreparedStatement pst = conn.prepareStatement(sql);
            
            pst.setString(1, nombre);
            pst.setString(2, descripcion);
            pst.setDate(3, java.sql.Date.valueOf(fecha));
            pst.setTime(4, java.sql.Time.valueOf(horaActividad));
            pst.setString(5, lugar);
            pst.setInt(6, edadMin);
            pst.setInt(7, edadMax);
            
            String x = UsuarioGlobal.getInstacne().getDniGlobal(); 
            creadorActividad(x);      
            
            pst.setInt(8, creadorActividad(x));
            
            filasAfectadas = pst.executeUpdate();
            
            if (filasAfectadas > 0) {
            	alertaInformacion("Nueva actividad añadida correctamente, gracias por tu colaboración ;)");
            }else {
            	alertaError("Ocurrio un error al añadir la actividad, intentelo de nuevo mas tarde");
            }
            
            // Limpiar todos los campos después de añadir la actividad
            txtNombreActividad.clear();
            dateFechaActividad.setValue(null);
            txtHoraActividad.clear();
            txtDescripcionActividad.clear();
            spinnerEdadMin.getValueFactory().setValue(0); 
            spinnerEdadMax.getValueFactory().setValue(0);
            txtLugarActividad.clear();
        	
        }catch (SQLException e) {
            e.printStackTrace();
        }   
    }


    private int creadorActividad(String x) {
    	int y= 0;
    	
    	try(Connection conn = BaseDeDatos.Conexion.dameConexion("convive")) {
    		String sql= "SELECT id FROM adulto WHERE dni= ?";
    		PreparedStatement pst= conn.prepareStatement(sql);
    		
    		pst.setString(1, x);
    		ResultSet rs = pst.executeQuery();
    	
    		while (rs.next()) {
                y = rs.getInt("id");
            }
    	
		} catch (Exception e) {
			 e.printStackTrace();
	    }
    	return y; 	
    }
    
    
   
/*BOTON VOLVER AL HOME*/
    @FXML
    void volver(ActionEvent event) { 
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaPrincipal.fxml"));
            AnchorPane root = loader.load();

            Scene scene = new Scene(root);
            Image cursorImage = new Image(getClass().getResourceAsStream("/imagenes/cursor.png"));
    	    scene.setCursor(new ImageCursor(cursorImage));

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Principal - ConVive");
            stage.show();

            Stage currentStage = (Stage) botonVolver.getScene().getWindow();
            currentStage.close(); //cerrar la ventana mi perfil

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
