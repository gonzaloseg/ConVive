package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MiPerfilControlador {

	 @FXML private ImageView img_volver;
	 @FXML private Label labelNombre;
	 @FXML private Label labelApellidos;
	 @FXML private Label labelFechaN;
	 @FXML private Label labelPiso;
	 @FXML private Label labelComodin;

	 
	    public void initialize() {
	        // Asigna un evento de clic a img_volver
	        img_volver.setOnMouseClicked(event -> volver(new ActionEvent()));
	    }

	    public void rellenarPerfil(String dniGlobal, String tabla) {
	    	//labelNombre.setText(tabla);
	    	String sql = "SELECT * FROM "+tabla+" WHERE dni = ?";
	    	try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive")){
	    		PreparedStatement pst = conn.prepareStatement(sql);
	    		
	            pst.setString(1, dniGlobal); // Establecer el valor del parámetro ?
	            
	    		ResultSet rs = pst.executeQuery();
	    		
	    		while (rs.next()) {
	    			//Guardar los datos del usuario desde la bbdd
	    			String dni = rs.getString("dni");
	    			String nombre = rs.getString("nombre");
	    			String apellidos = rs.getString("apellidos");
	    			String fechaN = rs.getString("fecha_nacimiento");
	    			String piso = rs.getString("piso");
	    			
	    			/*ESTO AUN NO FUNCIONA
	    			 * if (tabla == "menor") {
	    				String tutor = "tutor";
	    				
	    				sql = "SELECT * FROM adulto WHERE id = ?";
	    				try (Connection conne = BaseDeDatos.Conexion.dameConexion("convive")){
	    		    		pst = conne.prepareStatement(sql);
	    		    		
	    		            pst.setString(1, tutor); // Establecer el valor del parámetro ?
	    		            
	    		    		ResultSet r = pst.executeQuery();
	    		    		
	    		    		while (r.next()) {
	    		    			String nombreTutor = rs.getString("nombre");
	    		    			labelComodin.setText(nombreTutor);
	    		    		}	
	    				}
	    			}*/
	    			//Imprimirlos en la ventana 
	    			labelNombre.setText(nombre);
	    			labelApellidos.setText(apellidos);
	    			labelPiso.setText(piso);
	    			labelFechaN.setText(fechaN);
	    			
	    		}
	    		
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	    
	    
	    
	    
	    
	    
	    @FXML
	    void volver(ActionEvent event) {
	        try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaPrincipal.fxml"));
	            AnchorPane root = loader.load();

	            Scene scene = new Scene(root);
	            Stage stage = new Stage();
	            stage.setScene(scene);
	            stage.setTitle("Principal - ConVive");
	            stage.show();

	            // Cerrar la ventana actual
	            Stage currentStage = (Stage) img_volver.getScene().getWindow();
	            currentStage.close();

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
}
