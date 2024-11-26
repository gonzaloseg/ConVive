package dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Period;
import java.util.ResourceBundle;

import BaseDeDatos.Conexion;
import dto.Actividades;
import dto.UsuarioGlobal;

public class ListaEventosControlador implements Initializable {
    @FXML private Button botonVolver;
    @FXML private VBox actividadVBox;
    
    private static final String SQL_OBTENER_ACTIVIDADES = "SELECT * FROM actividad ORDER BY fecha DESC";

    /*__________________________________________________________*/

    public void initialize(URL location, ResourceBundle resources) {
    	cargarDatos();
    }
    
    /* CARGA LOS DATOS DE CADA ACTIVIDAD */
    public void cargarDatos() {
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(SQL_OBTENER_ACTIVIDADES);
             ResultSet rs = stmt.executeQuery()) {
        	String dniGlobal = UsuarioGlobal.getInstacne().getDniGlobal();
            actividadVBox.getChildren().clear(); // Limpiar las actividades previas

            while (rs.next()) {
                Actividades actividad = new Actividades(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDate("fecha").toLocalDate(),
                    rs.getTime("hora").toLocalTime(),
                    rs.getString("lugar"),
                    rs.getInt("edad_min"),
                    rs.getInt("edad_max"),
                    rs.getInt("creador")
                );

                VBox actividadBox = crearContainerActividad(actividad, dniGlobal);
                actividadVBox.getChildren().add(actividadBox);
            }
        } catch (SQLException e) {
            System.err.println("Error al cargar datos de actividades: " + e.getMessage());
        }
    }

    /* CREAR LOS CONTENEDORES PARA LAS ACTIVIDADES */
    private VBox crearContainerActividad(Actividades actividad, String dniGlobal) {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: #FFFFFF;");

        // Etiquetas de la actividad
        Label nombreLabel = new Label(actividad.getNombre());
        Label descripcionLabel = new Label(actividad.getDescripcion());
        Label fechaLabel = new Label("Fecha: " + actividad.getFecha());
        Label horaLabel = new Label("Hora: " + actividad.getHora());
        Label edadesLabel = new Label("Edades: " + actividad.getEdadMin() + " - " + actividad.getEdadMax());
        //Label apuntadosLabel = new Label("Número de apuntados: " + getApuntados(actividad.getId()));

        // Aplicar estilos a los label de las actividades
        nombreLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        nombreLabel.setStyle("-fx-text-fill: #22504e;"); 
        descripcionLabel.setFont(Font.font(14));
        descripcionLabel.setStyle("-fx-text-fill: #006d77;");
        fechaLabel.setFont(Font.font(14));
        fechaLabel.setStyle("-fx-text-fill: #006d77;");
        horaLabel.setFont(Font.font(14));
        horaLabel.setStyle("-fx-text-fill: #006d77;");
        edadesLabel.setFont(Font.font(14));
        edadesLabel.setStyle("-fx-text-fill: #878787;");
        //apuntadosLabel.setFont(Font.font(14));

        // Obtener el ID del usuario autenticado
        int idUsuario = -1;
        String sqlObtenerId = "SELECT id FROM adulto WHERE dni = ? UNION SELECT id FROM menor WHERE dni = ?";
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement pst = conn.prepareStatement(sqlObtenerId)) {
            pst.setString(1, dniGlobal);
            pst.setString(2, dniGlobal);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                idUsuario = rs.getInt("id");
            }
            //System.out.println(idUsuario);
        } catch (SQLException e) {
            System.err.println("Error al obtener ID del usuario: " + e.getMessage());
        }

        // Verificar si el usuario está inscrito
        boolean estaApuntado = false;
        if (idUsuario != -1) {
            String sqlVerificarApuntado = "SELECT * FROM apuntados WHERE id_adulto = ? AND id_actividad = ?";
            try (Connection conn = Conexion.dameConexion("convive");
                 PreparedStatement pst = conn.prepareStatement(sqlVerificarApuntado)) {
                pst.setInt(1, idUsuario);
                pst.setInt(2, actividad.getId());

                ResultSet rs = pst.executeQuery();
                estaApuntado = rs.next();
            } catch (SQLException e) {
                System.err.println("Error al verificar inscripción: " + e.getMessage());
            }
        }

        // Crear botones
        Button apuntarButton = new Button();
        if (idUsuario == actividad.getCreador()) {
            // Si el usuario es el creador de la actividad, mostrar los botones "Eliminar" y "Editar"
            Button editarButton = new Button("Editar actividad");
            editarButton.setOnAction(event -> editarActividad(actividad.getId()));
            editarButton.setStyle(	// Aplicar estilo al botón editar
                "-fx-background-color: #006D77;" +
                "-fx-background-radius: 18px;" +
                "-fx-border-radius: 18px;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;"
                );
            
            Button eliminarButton = new Button("Eliminar actividad");
            eliminarButton.setOnAction(event -> eliminarActividad(actividad.getId()));
            eliminarButton.setStyle( // Aplicar estilo al botón eliminar
                "-fx-background-color: red;" +
                "-fx-background-radius: 18px;" +
                "-fx-border-radius: 18px;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;"
                );

            // Agregar los botones al contenedor
            container.getChildren().addAll(nombreLabel, descripcionLabel, fechaLabel, horaLabel, edadesLabel, editarButton, eliminarButton);
        } else {
            // Si no es el creador, mostrar el botón de "Apuntarse" o "Desapuntarse"
            int finalIdUsuario = idUsuario; // Declarar una nueva variable final
            int edadUsuario = obtenerEdadUsuario(idUsuario); // Asume que tienes un método para obtener la edad del usuario

            // Verificar si la edad está dentro del rango
            if (edadUsuario >= actividad.getEdadMin() && edadUsuario <= actividad.getEdadMax()) {
                // Si la edad está dentro del rango, mostrar el botón "Apuntarse" o "Desapuntarse"
                if (estaApuntado) {
                    apuntarButton.setText("Desapuntarse");
                    apuntarButton.setOnAction(event -> desapuntarse(finalIdUsuario, actividad.getId()));
                    apuntarButton.setStyle(	// Aplicar estilo al botón desapuntar
                            "-fx-background-color: #83C5BE;" +
                            "-fx-background-radius: 18px;" +
                            "-fx-border-radius: 18px;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;"
                            );
                } else {
                    apuntarButton.setText("Apuntarse");
                    apuntarButton.setOnAction(event -> apuntarse(finalIdUsuario, actividad.getId()));
                    apuntarButton.setStyle(	// Aplicar estilo al botón apuntar
                            "-fx-background-color: #006D77;" +
                            "-fx-background-radius: 18px;" +
                            "-fx-border-radius: 18px;" +
                            "-fx-text-fill: white;" +
                            "-fx-font-weight: bold;"
                            );
                }
            } else {
                // Si la edad no está dentro del rango, deshabilitar el botón
                apuntarButton.setText("No puedes apuntarte");
                apuntarButton.setStyle(	// Aplicar estilo al botón apuntar desabilitado
                        "-fx-background-color: #CDCDCD;" +
                        "-fx-background-radius: 18px;" +
                        "-fx-border-radius: 18px;" +
                        "-fx-text-fill: #878787;" +
                        "-fx-font-weight: bold;"
                        );
                apuntarButton.setDisable(true); // Deshabilitar el botón

                // Mensaje adicional + cambio estilo de los label a gris
                nombreLabel.setStyle("-fx-text-fill: #878787;");
                descripcionLabel.setStyle("-fx-text-fill: #878787;");
                fechaLabel.setStyle("-fx-text-fill: #878787;");
                horaLabel.setStyle("-fx-text-fill: #878787;");
                edadesLabel.setStyle("-fx-text-fill: #878787;");
                //apuntadosLabel.setStyle("-fx-text-fill: #878787;");
                Label mensajeRestriccionEdad = new Label("Tu edad no cumple con los requisitos para apuntarte a esta actividad.");
                container.getChildren().add(mensajeRestriccionEdad);
            }
            if (estaApuntado) {
                apuntarButton.setText("Desapuntarse");
                apuntarButton.setOnAction(event -> desapuntarse(finalIdUsuario, actividad.getId()));
                
            } else {
                apuntarButton.setText("Apuntarse");
                apuntarButton.setOnAction(event -> apuntarse(finalIdUsuario, actividad.getId()));
            }
            container.getChildren().addAll(nombreLabel, descripcionLabel, fechaLabel, horaLabel, edadesLabel, apuntarButton);
        }
        return container;
    }
    
    /* APUNTARSE A UNA ACTIVIDAD */
    private void apuntarse(int idUsuario, int idActividad) {
        String sqlApuntarse = "INSERT INTO apuntados (id_adulto, id_actividad) VALUES (?, ?)";
        
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement pst = conn.prepareStatement(sqlApuntarse)) {
            
            // Verificar si el usuario ya está inscrito
            String sqlVerificar = "SELECT * FROM apuntados WHERE id_adulto = ? AND id_actividad = ?";
            try (PreparedStatement pstVerificar = conn.prepareStatement(sqlVerificar)) {
                pstVerificar.setInt(1, idUsuario);
                pstVerificar.setInt(2, idActividad);
                ResultSet rs = pstVerificar.executeQuery();
                
                // Si no está inscrito, insertar el registro
                if (!rs.next()) {
                    pst.setInt(1, idUsuario);
                    pst.setInt(2, idActividad);
                    int rowsAffected = pst.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Usuario apuntado a la actividad.");
                        cargarDatos(); // Recargar las actividades para actualizar el estado de inscripción
                    } else {
                        System.err.println("Error al apuntarse a la actividad.");
                    }
                } else {
                    System.out.println("El usuario ya está inscrito en esta actividad.");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al apuntarse a la actividad: " + e.getMessage());
        }
    }

    /* DESAPUNTARSE DE UNA ACTIVIDAD*/
    private void desapuntarse(int idUsuario, int idActividad) {
        String sqlDesapuntarse = "DELETE FROM apuntados WHERE id_adulto = ? AND id_actividad = ?";
        
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement pst = conn.prepareStatement(sqlDesapuntarse)) {
            
            pst.setInt(1, idUsuario);
            pst.setInt(2, idActividad);
            int rowsAffected = pst.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Usuario desapuntado de la actividad.");
                cargarDatos(); // Recargar las actividades para actualizar el estado de inscripción
            } else {
                System.err.println("Error al desapuntarse de la actividad.");
            }

        } catch (SQLException e) {
            System.err.println("Error al desapuntarse de la actividad: " + e.getMessage());
        }
    }

	
    /* ELIMINAR ACTIVIDAD */
    private void eliminarActividad(int actividadId) {
        String sqlEliminar = "DELETE FROM actividad WHERE id = ?";
        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement pst = conn.prepareStatement(sqlEliminar)) {
            pst.setInt(1, actividadId);
            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Actividad eliminada con éxito.");
                cargarDatos();
            } else {
                System.err.println("No se pudo eliminar la actividad.");
            }
        } catch (SQLException e) {
            System.err.println("Error al eliminar actividad: " + e.getMessage());
        }
    }

    /* EDITAR ACTIVIDAD */    
    private void editarActividad(int actividadId) {
        System.out.println("Editar actividad con ID: " + actividadId);
        try {
            // Cargar la vista FXML para editar la actividad
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/VistaEditarActividad.fxml"));
            AnchorPane root = loader.load();

            // Obtener el controlador de la vista cargada
            EditarActividadControlador controlador = loader.getController();

            // Recuperar la actividad desde la base de datos
            Actividades actividad = obtenerActividad(actividadId);

            // Verifica si la actividad existe antes de pasarla al controlador
            if (actividad != null) {
                // Pasa la actividad al controlador
                controlador.setActividad(actividad);
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

            // Cerrar la ventana actual
            Stage currentStage = (Stage) botonVolver.getScene().getWindow();
            currentStage.close();

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



    
    /* DA LA EDAD DEL USUARIO ACTUAL (PARA COMPROBAR SI SE PUEDE APUNTAR A UNA ACTIVIDAD O NO) */
    private int obtenerEdadUsuario(int idUsuario) {
        int edad = -1;  // Valor por defecto si no se encuentra la edad

        // Consulta SQL para obtener la fecha de nacimiento del usuario
        String sql = "SELECT fecha_nacimiento FROM adulto WHERE id = ? UNION SELECT fecha_nacimiento FROM menor WHERE id = ?";

        try (Connection conn = Conexion.dameConexion("convive");
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, idUsuario);
            pst.setInt(2, idUsuario);

            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                // Obtener la fecha de nacimiento
                Date fechaNacimiento = rs.getDate("fecha_nacimiento");
                
                // Calcular la edad
                LocalDate hoy = LocalDate.now();
                LocalDate fechaNacimientoLocal = fechaNacimiento.toLocalDate();
                edad = Period.between(fechaNacimientoLocal, hoy).getYears();  // Calcula la edad en años
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener la edad del usuario: " + e.getMessage());
        }

        return edad;  // Retorna la edad calculada o -1 si hubo algún error
    }

    
	/* BOTÓN PARA VOLVER A LA PÁGINA PRINCIPAL*/
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

            Stage currentStage = (Stage) botonVolver.getScene().getWindow();
            currentStage.close(); //cerrar la ventana

        } catch (Exception e) {
            e.printStackTrace();
        }
    } 

}