package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

import BaseDeDatos.Conexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import dto.Actividades;
import dto.UsuarioGlobal;

public class MiPerfilControlador {

 @FXML private ImageView img_volver;
 @FXML private Label labelNombre;
 @FXML private Label labelApellidos;
 @FXML private Label labelFechaN;
 @FXML private Label labelPiso;
 @FXML private Label labelDni;
 @FXML private Label labelComodin;
 
 @FXML private Button actividadesApuntadas;
 @FXML private Button actividadesPropuestas;
 @FXML private Button botonClose;
 @FXML private Button botonClose1;
 @FXML private Button borrarActividad;
 @FXML private Button borrarActividad1;
 @FXML private Button editarActividad;
 @FXML private VBox contenedorTarjeta;
 @FXML private VBox contenedorTarjeta1;
 
 @FXML private TableView<Actividades> tablaActividadesApuntadas;
 @FXML private TableColumn<Actividades, String> columnaActividades;
 @FXML private TableColumn<Actividades, LocalDate> columnaFecha;
 @FXML private TableColumn<Actividades, LocalTime> columnaHora;
 
 @FXML private TableView<Actividades> tablaActividadesPropuestas;
 @FXML private TableColumn<Actividades, String> columnaActividades1;
 @FXML private TableColumn<Actividades, LocalDate> columnaFecha1;
 @FXML private TableColumn<Actividades, LocalTime> columnaHora1;
 
 private ObservableList<Actividades>listaActividades;
 private ObservableList<Actividades>listaActividades1;

	 
	public void initialize() {
	    // Asigna un evento de clic a img_volver
	    img_volver.setOnMouseClicked(event -> volver(new ActionEvent()));
	    
        contenedorTarjeta.setVisible(false); // Ocultar el contenedor de la tarjeta inicialmente
        contenedorTarjeta1.setVisible(false); // Ocultar el contenedor de la tarjeta inicialmente
        
        // Inicializar la lista
        listaActividades = FXCollections.observableArrayList();
        
        // Configurar las columnas para que usen los nombres de las propiedades del modelo
        columnaActividades.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        columnaHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        
        // Inicializar la lista1
        listaActividades1 = FXCollections.observableArrayList();
        
        // Configurar las columnas para que usen los nombres de las propiedades del modelo
        columnaActividades1.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        columnaFecha1.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        columnaHora1.setCellValueFactory(new PropertyValueFactory<>("hora"));
        
        /* Los menores de edad no tendrán posibilidda de crear nuevas actividades
           por eso, no le aparecera el boton "Actividades propuestas" con la lista de actividades */
        if (UsuarioGlobal.getInstacne().getTabla().equals("menor")) {
        	actividadesPropuestas.setVisible(false);
        }
        
	}

	 int idAuxiliar = 0;

	
//RELLENAR LOS DATOS DEL USUARIO 
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
    			
    			idAuxiliar= id;
    			
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
    					if (nombresMenores.isEmpty()) {
    						labelComodin.setText("Menores a cargo -> No tiene menores a cargo");
    					}else {
        					labelComodin.setText("Menores a cargo -> "+nombresMenores.toString());
    					}
    					
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
    			labelDni.setText(dni);			
    		}
    		
		}  catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
		}
    }
    
    

//VER LAS ACTIVIDADES DONDE TE HAS APUNTADO 
    @FXML
    void actividadesApuntadas(ActionEvent event) {
    	ObservableList<Actividades> listaActividades = FXCollections.observableArrayList();

        String sql = "SELECT a.id, a.nombre, a.fecha, a.hora FROM actividad a JOIN apuntados ap ON a.id = ap.id_actividad WHERE ap.id_adulto OR ap.id_menor = ?";

        try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive")) {
        	PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, idAuxiliar);
            
            try (ResultSet rs = pst.executeQuery()) {
            	
                while (rs.next()) { //recorre la bbdd
                	
                	int idActividad = rs.getInt("id"); // Recupera el ID de la actividad
                    String nombreAct = rs.getString("nombre");
                    LocalDate fechaAct = rs.getDate("fecha").toLocalDate();
                    LocalTime horaAct = rs.getTime("hora").toLocalTime();
                    

                    //hay que rellenar los campos que se van a usar y dejar vacios los campos que no se estan usando
                    Actividades actividad = new Actividades(idActividad, nombreAct, "", fechaAct, horaAct, "", 0, 0, 0);  
                    listaActividades.add(actividad);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }

        tablaActividadesApuntadas.setItems(listaActividades); //añade todas las act a la tabla 
        contenedorTarjeta.setVisible(true);
    	
    }
    
    @FXML
    void borrarActividad(ActionEvent event) {
    	Actividades actividadSeleccionada = tablaActividadesApuntadas.getSelectionModel().getSelectedItem();
		
		if (actividadSeleccionada != null) {
			
	        int actividadId = actividadSeleccionada.getId(); // Recupera el ID de la actividad

	        if (actividadId > 0) {
	            // Borrar de la tabla en pantalla
	            listaActividades.remove(actividadSeleccionada);

	            // Borrar de la base de datos
	            String sql = "DELETE FROM apuntados WHERE id_adulto OR id_menor = ? AND id_actividad = ?";
	            try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive")) {
	                PreparedStatement pst = conn.prepareStatement(sql);
	                pst.setInt(1, idAuxiliar);  // ID del adulto
	                pst.setInt(2, actividadId);  // ID de la actividad

	                // Ejecutar la actualización
	                int rowsAffected = pst.executeUpdate();

	                if (rowsAffected > 0) {
	                    new Alert(Alert.AlertType.INFORMATION, "Ya no estás apuntado/a a la actividad").show();
	                } else {
	                    new Alert(Alert.AlertType.ERROR, "No hemos podido borrarte de la actividad, por favor, inténtelo de nuevo").show();
	                }
	            } catch (SQLException e) {
	                e.printStackTrace();
	                new Alert(Alert.AlertType.ERROR, "Ocurrió un error. Intente nuevamente").show();
	            }
	        } else {
	            new Alert(Alert.AlertType.WARNING, "ID de actividad no válido").show();
	        }
	    } else {
	        new Alert(Alert.AlertType.WARNING, "Debes seleccionar una actividad para eliminar").show();
	    }
	
    }
    
    @FXML
    void close (ActionEvent event ) {
    	contenedorTarjeta.setVisible(false);
    }
    
    
    
    
    
    
  //VER LAS ACTIVIDADES QUE HAS PROPUESTO
    @FXML
    void actividadesPropuestas(ActionEvent event) {
    	ObservableList<Actividades> listaActividades1 = FXCollections.observableArrayList();

        String sql = "SELECT id, nombre, fecha, hora FROM actividad WHERE creador = ?";

        
        try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive")) {
        	PreparedStatement pst = conn.prepareStatement(sql);
            pst.setInt(1, idAuxiliar);
            
            try (ResultSet rs = pst.executeQuery()) {
            	
                while (rs.next()) { //recorre la bbdd
                	/*
                	 Con la línea 189 comentada, carga la tabla pero da error al borrar una actividad
                	 Si pruebas a "descomentarla" veras como la tabla aparece vacia 
                	 El metodo para borrar los elementos está más abajo, el problema es que no coge el id de la actividad seleccionada
                	 Mete en tu tabla apuntados mas datos para que un usuario tenga al menos 3 actividades y poder comprobar bien todo
                	 */
                	int idActividad = rs.getInt("id"); // Recupera el ID de la actividad
                    String nombreAct = rs.getString("nombre");
                    LocalDate fechaAct = rs.getDate("fecha").toLocalDate();
                    LocalTime horaAct = rs.getTime("hora").toLocalTime();
                    

                    //hay que rellenar los campos que se van a usar y dejar vacios los campos que no se estan usando
                    Actividades actividad = new Actividades(idActividad, nombreAct, "", fechaAct, horaAct, "", 0, 0, 0);  
                    listaActividades1.add(actividad);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }

        tablaActividadesPropuestas.setItems(listaActividades1); //añade todas las act a la tabla 
        contenedorTarjeta1.setVisible(true);
    	
    }
    
    @FXML //Borrar una actividad creada por el usuario logado
    void borrarActividad1(ActionEvent event) {
    	Actividades actividadSeleccionada = tablaActividadesPropuestas.getSelectionModel().getSelectedItem();
		
		if (actividadSeleccionada != null) {
			
	        int actividadId = actividadSeleccionada.getId(); // Recupera el ID de la actividad

	        if (actividadId > 0) {
	            // Borrar de la tabla en pantalla
	            listaActividades1.remove(actividadSeleccionada);

	            // Borrar de la base de datos
	            String sql = "DELETE FROM actividad WHERE id = ?";
	            try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive")) {
	                PreparedStatement pst = conn.prepareStatement(sql);
	                pst.setInt(1, actividadId);  // ID de la actividad

	                // Ejecutar la actualización
	                 pst.executeUpdate();

	                
                    new Alert(Alert.AlertType.INFORMATION, "Ya no estás apuntado/a a la actividad").show();
	                
	            } catch (SQLException e) {
	                e.printStackTrace();
	                new Alert(Alert.AlertType.ERROR, "Ocurrió un error. Intente nuevamente").show();
	            }
	        } else {
	            new Alert(Alert.AlertType.WARNING, "ID de actividad no válido").show();
	        }
	    } else {
	        new Alert(Alert.AlertType.WARNING, "Debes seleccionar una actividad para eliminar").show();
	    }
    }
    
    @FXML //Editar las actividades que has propuesto 
    private void editarActividad (ActionEvent event) {
    	Actividades actividadSeleccionada = tablaActividadesPropuestas.getSelectionModel().getSelectedItem();
        int actividadId = actividadSeleccionada.getId(); // Recupera el ID de la actividad

        
    	try {
            // Cargar la vista FXML para editar la actividad
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaEditarActividad.fxml"));
            AnchorPane root = loader.load();
            
            // Obtener la instancia del controlador y pasar los datos (MI PERFIL)
            EditarActividadControlador controller = loader.getController();
           
            // Recuperar la actividad desde la base de datos
            Actividades actividad = obtenerActividad(actividadId);

            // Verifica si la actividad existe antes de pasarla al controlador
            if (actividad != null) {
                // Pasa la actividad al controlador
            	controller.setActividad(actividad);
            } else {
                // Si no se encuentra la actividad, mostrar un mensaje o manejar el error
                System.out.println("No se pudo cargar la actividad.");
                return;
            }

            // Mostrar la nueva ventana
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Editar Actividad");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /* DEVUELVE LA ACTIVIDAD PARA EDITARLA */
    private Actividades obtenerActividad(int actividadId) {
        Actividades actividad = null;

        String sql = "SELECT * FROM actividad WHERE id = ?";

        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement pst = conn.prepareStatement(sql)) {
            
            pst.setInt(1, actividadId);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                LocalDate fecha = rs.getDate("fecha").toLocalDate();
                String horaString = rs.getString("hora");
                LocalTime hora = null;

                // Parse the String into LocalTime
                if (horaString != null && !horaString.isEmpty()) {
                    hora = LocalTime.parse(horaString);
                }

                String lugar = rs.getString("lugar");
                int edadMin = rs.getInt("edad_min");
                int edadMax = rs.getInt("edad_max");
                int creador = rs.getInt("creador");

                actividad = new Actividades(actividadId, nombre, descripcion, fecha, hora, lugar, edadMin, edadMax, creador);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return actividad;
    }
 
    @FXML //Cerrar tarjeta 
    void close1 (ActionEvent event ) {
    	contenedorTarjeta1.setVisible(false);
    }
    
    
    
    
    
    
    
	
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
