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
    	
    	String sql = "SELECT * FROM "+tabla+" WHERE dni = ?";
    	try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive")){
    		PreparedStatement pst = conn.prepareStatement(sql);
    		
            pst.setString(1, dniGlobal); // Establecer el valor del parámetro ?
            
    		ResultSet rs = pst.executeQuery();
    		
    		while (rs.next()) {
    			//Guardar los datos del usuario desde la bbdd
    			int id = rs.getInt("id");
    			String dni = rs.getString("dni");
    			String nombre = rs.getString("nombre");
    			String apellidos = rs.getString("apellidos");
    			String fechaN = rs.getString("fecha_nacimiento");
    			String piso = rs.getString("piso");
    			
    			//Cuando el usuario es menor de edad aparecera su tutor a cargo 
    			if (tabla.equals("menor")) {
    				String tutor = rs.getString("tutor"); //id de la tabla adultos 
    				
    				sql = "SELECT * FROM adulto WHERE id = ?";
    				try (Connection conne = BaseDeDatos.Conexion.dameConexion("convive")){
    		    		pst = conne.prepareStatement(sql);
    		            pst.setString(1, tutor); // Establecer el valor del parámetro ?
    		            
    		    		ResultSet r = pst.executeQuery();
    		    		
    		    		while (r.next()) {
    		    			String nombreTutor = r.getString("nombre"); //Nombre del tutor (tabla adultos)
    		    			labelComodin.setText(nombreTutor);
    		    		}
    		    		
    				} catch (Exception e) {
						e.printStackTrace();
						e.getMessage();
					}
    				
				//Cuando el usuario es mayor de edad apareceran los menores que tiene a cargo
    			}else {
    				
    				//Seleciona el menor cuyo num de tutor sea igual al id del adulto
    				sql = "SELECT * FROM menor m JOIN adulto a ON a.id = m.tutor WHERE m.tutor = ?";
    				
    				try (Connection c = BaseDeDatos.Conexion.dameConexion("convive")){
    					pst = c.prepareStatement(sql);
    					pst.setInt(1, id); //Establece el valor del parametro ?
    					
    					ResultSet result = pst.executeQuery();
    					
    					StringBuilder nombresMenores = new StringBuilder();
    					while (result.next()) {
    					    
    						// Obtener el nombre del menor completo
    					    String nombreMenor = result.getString("nombre");
    					    String apellidoMenor = result.getString("apellidos");
    					    String nombreCompletoMenor = nombreMenor + " " + apellidoMenor;
    					     
    					    if (nombresMenores.length() > 0) { //si no es el primer nombre
    					    	nombresMenores.append(", ");  // Agrega una coma si no es el primer nombre
    					    }
    					    nombresMenores.append(nombreCompletoMenor);
    					}

    					// Establecer el texto concatenado en el Label
    					labelComodin.setText("Menores a cargo -> "+nombresMenores.toString());
    					
					} catch (Exception e) {
						e.printStackTrace();
						e.getMessage();
					}
    			}

    		
    			//Imprimirlos en la ventana 
    			labelNombre.setText(nombre);
    			labelApellidos.setText(apellidos);
    			labelPiso.setText(piso);
    			labelFechaN.setText(fechaN);
    			
    		}
    		
		}  catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
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
