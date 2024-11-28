package dao;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import dto.UsuarioGlobal;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InicioSesionControlador implements Initializable{

    @FXML
    private Button botonIniciarSesion;
    @FXML
    private Button botonRegistrate;
    @FXML
    private TextField txtContrasenia;
    @FXML
    private TextField txtDNI;
    
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
    	// Texto que aparecerá en los TextFields
        txtDNI.setPromptText("Ingrese su DNI");
        txtContrasenia.setPromptText("Ingrese su contraseña");

       
	}
    
    
    @FXML
    private void iniciarSesion (ActionEvent event) { //BOTON INICIAR SESION 
    	
	//Declarar objetos 
    	String dni = txtDNI.getText();;
    	String contrasenia = txtContrasenia.getText();
    	
    	
	//Comprobar si los campos están vacios
    	if (dni.isEmpty()&& contrasenia.isEmpty()) { //si faltan ambos 
    		new Alert (Alert.AlertType.WARNING, "Para iniciar sesion debes introducir el DNI y la contraseña").showAndWait();;
    		return;
    		
    	}else if(dni.isEmpty()) { //si falta el dni
    		new Alert (Alert.AlertType.WARNING, "Para iniciar sesion debes introducir el DNI").showAndWait();;
    		return;	
    		
    	}else if(contrasenia.isEmpty()){ //si falta la contraseña 
    		new Alert (Alert.AlertType.WARNING, "Para iniciar sesion debes la contraseña").showAndWait();;
    		return;
    	}
    	
    	
    	try(Connection conn = BaseDeDatos.Conexion.dameConexion("convive")) {
    		
    		//Enviar al método la tabla menor o adulto 
			boolean autentificar = verificarCredenciales (conn, dni, contrasenia, "adulto") || 
									verificarCredenciales (conn, dni, contrasenia, "menor");
			
			
			//Comprobar que tabla estamos usando para pasarla a MI PERFIL
			if( verificarCredenciales (conn, dni, contrasenia, "adulto")) {
				String tabla = "adulto";
				UsuarioGlobal.getInstacne().setTabla(tabla);
				System.out.println(UsuarioGlobal.getInstacne().getTabla());
			}else {
				String tabla = "menor";
				UsuarioGlobal.getInstacne().setTabla(tabla);
				System.out.println(UsuarioGlobal.getInstacne().getTabla());
			}
    		
			//Comprobar si los datos corresponenden con la BBDD
			if (autentificar) {
				
					new Alert(Alert.AlertType.INFORMATION, "Los datos introducidos son correctos").showAndWait();;
			    try {
			        // Cargar la vista principal desde el archivo FXML
			        Parent root = FXMLLoader.load(getClass().getResource("/vista/VistaPrincipal.fxml"));
			        
			        // Crear una nueva escena con la vista principal
			        Scene escena = new Scene(root);
			        Image cursorImage = new Image(getClass().getResourceAsStream("/imagenes/cursor.png"));
			        escena.setCursor(new ImageCursor(cursorImage));
			        
			        // Obtener el escenario actual y reemplazarlo con la nueva escena
			        Stage escenarioActual = (Stage) botonIniciarSesion.getScene().getWindow();
			        escenarioActual.setScene(escena);
			        escenarioActual.setTitle("Principal - Convive");
			        escenarioActual.centerOnScreen();
			        escenarioActual.show();
			        
			        
			        
			    } catch (Exception e) {
			        e.printStackTrace();
			        new Alert(Alert.AlertType.ERROR, "No se pudo cargar la ventana principal").showAndWait();
			    }
			}else {
				new Alert(Alert.AlertType.ERROR, "DNI o Contraseña incorrectos").showAndWait();;
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo conectar a la base de datos").showAndWait();
        }
    }
    
    
    
    
    
    // Método para verificar credenciales (si es un menor o un adulto)
    private boolean verificarCredenciales(Connection conn, String dni, String contrasenia, String tabla) {
        String sql = "SELECT * FROM " + tabla + " WHERE dni = ? AND contrasenia = ?";
        
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            
        	//guardar datos de la bbdd
        	pst.setString(1, dni);
        	pst.setString(2, contrasenia);
        	
            
            ResultSet rs = pst.executeQuery();
            
            //DNI GLOBAL 
            UsuarioGlobal.getInstacne().setDniGlobal(dni);
            
            return rs.next();
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    @FXML
    private void registrarse (ActionEvent event) { //BOTON REGISTRARSE
	//Este metodo te llevará a la ventana "Login" para registrar un nuevo usuario
    	try {
    		//Cargar la nueva ventana 
    		Parent root = FXMLLoader.load(getClass().getResource("/vista/VistaLogin.fxml"));
            
			//Crear una escena + escenario
			Scene escena = new Scene(root);
			Stage escenario = new Stage();
			escenario.initModality(Modality.APPLICATION_MODAL);
			escenario.setScene(escena);
			escenario.setTitle("Login - ConVive");
			
			escenario.showAndWait();
			
		} catch (Exception e) {
			Alert alert = new Alert (Alert.AlertType.ERROR);
			alert.setHeaderText(null);
			alert.setTitle("ERROR");
			alert.setContentText(e.getMessage());
			alert.showAndWait();		}
    }

	

}
