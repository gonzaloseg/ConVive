package dao;

import java.sql.Connection; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MiComunidadControlador {
    
    //Información de los residentes del bloque
    @FXML private ImageView img_volver; 
    @FXML private Button btn_1;
    @FXML private Button btn_2;
    @FXML private Button btn_3;
    @FXML private Button btn_4;
    @FXML private Button btn_5;
    @FXML private Button btn_6;
    @FXML private Button btn_7;
    @FXML private Button btn_8;
    @FXML private Button btn_9;
    @FXML private Button btn_10;
    @FXML private Button btn_11;
    @FXML private Button btn_12;
    @FXML private Button btn_13;
    @FXML private Button btn_14;
    
    //Información de los residentes del piso
    @FXML private Label lbl_mipiso;
    @FXML private Label labelPiso;
    
    
    @FXML private Label lbl_informacion; // Para mostrar información en pantalla.
    @FXML
    private BarChart<String, Number> barChart;
    private String userId; // Variable para almacenar el ID del usuario
    
    @FXML
    public void initialize() {
        // Asignar eventos a los botones
        configurarEventosBoton(btn_1, "1ºA");
        configurarEventosBoton(btn_2, "1ºB");
        configurarEventosBoton(btn_3, "2ºA");
        configurarEventosBoton(btn_4, "2ºB");
        configurarEventosBoton(btn_5, "3ºA");
        configurarEventosBoton(btn_6, "3ºB");
        configurarEventosBoton(btn_7, "4ºA");
        configurarEventosBoton(btn_8, "4ºB");
        configurarEventosBoton(btn_9, "5ºA");
        configurarEventosBoton(btn_10, "5ºB");
        configurarEventosBoton(btn_11, "6ºA");
        configurarEventosBoton(btn_12, "6ºB");
        configurarEventosBoton(btn_13, "7ºA");
        configurarEventosBoton(btn_14, "7ºB");
        cargarDatosEnGrafica();
        
        //lbl_informacion.setVisible(false);
    }
    
    
    
    
    
/* RELLENAR LA INFORMACIÓN DEL PISO DEL USUARIO LOGADO */ 
    
    public void rellenarMiVivienda (String dniGlobal, String tabla) {
    	String sql = "SELECT piso FROM " +tabla+ " WHERE dni = ?";
    	String piso= "";
    	
    	//Guarda el piso del usuario logado y lo imprime en el cuadro de información
    	try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive")){
    		PreparedStatement pst= conn.prepareStatement(sql);
    		pst.setString(1, dniGlobal);
    		ResultSet rs = pst.executeQuery();
    		
    		//Guarda el piso del usuario logado 
    		while (rs.next()) {
    			piso = rs.getString("piso");
    			labelPiso.setText("Bloque 1, piso "+piso);
    		}
		} catch (Exception e) {
			e.printStackTrace(); e.getMessage();
		}
    	
    	StringBuilder residentes = new StringBuilder(); //lista para almacenar los residentes del piso
    	residentes.append("Residentes: ").append("\n");
    	
    	sql= "SELECT dni, nombre, apellidos FROM adulto WHERE piso = ?";
    	
    	//Guarda en la lista los adultos que habitan en el piso
    	try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive")){
    		PreparedStatement pst= conn.prepareStatement(sql);
    		pst.setString(1, piso);
    		ResultSet rs = pst.executeQuery();
    		
    		while (rs.next()) {
    			String dni = rs.getString("dni");
    			String nombre = rs.getString("nombre");
    			String apellidos = rs.getString("apellidos");
    			
    			if (dni.equals(dniGlobal)) { //Si es el usuario logado, lo indica en el cuadro de información
    				residentes.append("  - ").append(nombre+" "+apellidos+"  (yo)").append("\n");
    			}else {
    				residentes.append("  - ").append(nombre+" "+apellidos).append("\n");
    			}	
    		}
		} catch (Exception e) {
			e.printStackTrace(); e.getMessage();
		}
    	
    	sql= "SELECT dni, nombre, apellidos FROM menor WHERE piso = ?";
    	
    	//Guarda en la lista los menores que habitan en el piso
    	try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive")){
    		PreparedStatement pst= conn.prepareStatement(sql);
    		pst.setString(1, piso);
    		ResultSet rs = pst.executeQuery();
    		
    		while (rs.next()) {
    			String dni = rs.getString("dni");
    			String nombre = rs.getString("nombre");
    			String apellidos = rs.getString("apellidos");
    			
    			if (dni.equals(dniGlobal)) { //Si es el usuario logado, lo indica en el cuadro de información
    				residentes.append("  - ").append(nombre+" "+apellidos+"  (yo)").append("\n");
    			}else {
    				residentes.append("  - ").append(nombre+" "+apellidos).append("\n");
    			}	
    		}	
		} catch (Exception e) {
			e.printStackTrace(); e.getMessage();
		}
    	
    	//rellena los residentes en el cuadro de información
    	lbl_mipiso.setText(residentes.toString());
    }
    
    
    
    
    
/* COMENTA LO QUE VIENE AHORA -> (INFORMACIÓN DEL BLOQUE / GRÁFICA)*/    
    
    private void configurarEventosBoton(Button boton, String piso) {
        img_volver.setOnMouseClicked(event -> volver(new ActionEvent()));

        // Cambiar color al pasar el ratón
        boton.setOnMouseEntered(event -> cambiarColorBotonAmarillo(boton));
        boton.setOnMouseExited(event -> restaurarColorBoton(boton));

        // Mostrar habitantes al hacer clic
        boton.setOnAction(event -> mostrarHabitantes(piso));
    }

    private void cambiarColorBotonAmarillo(Button boton) {
        boton.setStyle("-fx-background-color: yellow;");
    }

    private void restaurarColorBoton(Button boton) {
        boton.setStyle(""); // Elimina el estilo en línea, volviendo al estilo original
    }

    private void mostrarHabitantes(String piso) {
        // Consulta a la base de datos para obtener habitantes
        String habitantes = getHabitantesPorPiso(piso);

        // Muestra la información en la etiqueta o ventana emergente
        if (!habitantes.isEmpty()) {
        	
            lbl_informacion.setText("Habitantes del piso " + piso + ":\n" + habitantes);
        } else {
            lbl_informacion.setText("No hay información disponible para el piso " + piso);
        }
    }
    
    
    
    
    

    private void cargarDatosEnGrafica() {
        // Datos ficticios para graficar (debes usar datos reales)
        int numeroDeAdultos = obtenerNumeroHabitantes("adulto");
        int numeroDeMenores = obtenerNumeroHabitantes("menor");

        // Crea una serie de datos para la gráfica
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Distribución por Edad");

        // Añade los datos a la serie
        series.getData().add(new XYChart.Data<>("Adultos", numeroDeAdultos));
        series.getData().add(new XYChart.Data<>("Menores", numeroDeMenores));

        // Añade la serie al gráfico de barras
        barChart.getData().clear(); // Limpiar datos anteriores
        barChart.getData().add(series);
    }
    
    
    
    
    private int obtenerNumeroHabitantes(String tipo) {
        String query = tipo.equals("adulto") 
            ? "SELECT COUNT(*) AS total FROM adulto" 
            : "SELECT COUNT(*) AS total FROM menor";

        try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0; // Por defecto, 0 si hay error
    }
    
    
    

    private String getHabitantesPorPiso(String piso) {
        StringBuilder habitantes = new StringBuilder();
        String queryAdultos = "SELECT nombre FROM adulto WHERE piso = ?";
        String queryMenores = "SELECT nombre FROM menor WHERE piso = ?";

        try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive");
             PreparedStatement stmtAdultos = conn.prepareStatement(queryAdultos);
             PreparedStatement stmtMenores = conn.prepareStatement(queryMenores)) {

            // Consultar adultos
            stmtAdultos.setString(1, piso);
            try (ResultSet rsAdultos = stmtAdultos.executeQuery()) {
                habitantes.append("Mayores:\n");
                if (!rsAdultos.isBeforeFirst()) {
                    habitantes.append("  No hay adultos registrados en este piso.\n");
                } else {
                    while (rsAdultos.next()) {
                        habitantes.append("  - ").append(rsAdultos.getString("nombre")).append("\n");
                    }
                }
            }

            // Consultar menores
            stmtMenores.setString(1, piso);
            try (ResultSet rsMenores = stmtMenores.executeQuery()) {
                habitantes.append("Menores:\n");
                if (!rsMenores.isBeforeFirst()) {
                    habitantes.append("  No hay menores registrados en este piso.\n");
                } else {
                    while (rsMenores.next()) {
                        habitantes.append("  - ").append(rsMenores.getString("nombre")).append("\n");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al obtener los habitantes del piso " + piso;
        }

        return habitantes.toString();
    }
    public void setUserId(String id) {
        this.userId = id;

        // Obtén la información del piso y actualiza el Label
        String infoPiso = getMiInfoPiso(id);
        lbl_mipiso.setText(infoPiso); // Actualiza el Label con la información del piso
    }
    
    
    
    
    
    

    private String getMiInfoPiso(String id) {
        StringBuilder habitantes = new StringBuilder();
        String queryPisoPorIdAdulto = "SELECT piso FROM adulto WHERE id = ?";
        String queryPisoPorIdMenor = "SELECT piso FROM menor WHERE id = ?";
        String queryAdultos = "SELECT nombre FROM adulto WHERE piso = ?";
        String queryMenores = "SELECT nombre FROM menor WHERE piso = ?";

        String piso = null;

        try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive")) {

            // Determinar el piso del usuario en la tabla adulto
            try (PreparedStatement stmtPisoAdulto = conn.prepareStatement(queryPisoPorIdAdulto)) {
                stmtPisoAdulto.setString(1, id);
                try (ResultSet rsAdulto = stmtPisoAdulto.executeQuery()) {
                    if (rsAdulto.next()) {
                        piso = rsAdulto.getString("piso");
                    }
                }
            }

            // Si no se encontró en adultos, buscar en la tabla menor
            if (piso == null) {
                try (PreparedStatement stmtPisoMenor = conn.prepareStatement(queryPisoPorIdMenor)) {
                    stmtPisoMenor.setString(1, id);
                    try (ResultSet rsMenor = stmtPisoMenor.executeQuery()) {
                        if (rsMenor.next()) {
                            piso = rsMenor.getString("piso");
                        }
                    }
                }
            }

            // Si no se encuentra el piso, retornar un mensaje de error
            if (piso == null) {
                return "No se pudo determinar el piso del usuario con ID: " + id;
            }

            // Consultar adultos y menores que viven en el piso
            try (PreparedStatement stmtAdultos = conn.prepareStatement(queryAdultos);
                 PreparedStatement stmtMenores = conn.prepareStatement(queryMenores)) {

                // Consultar adultos
                stmtAdultos.setString(1, piso);
                try (ResultSet rsAdultos = stmtAdultos.executeQuery()) {
                    habitantes.append("Mayores:\n");
                    if (!rsAdultos.isBeforeFirst()) {
                        habitantes.append("  No hay adultos registrados en este piso.\n");
                    } else {
                        while (rsAdultos.next()) {
                            habitantes.append("  - ").append(rsAdultos.getString("nombre")).append("\n");
                        }
                    }
                }

                // Consultar menores
                stmtMenores.setString(1, piso);
                try (ResultSet rsMenores = stmtMenores.executeQuery()) {
                    habitantes.append("Menores:\n");
                    if (!rsMenores.isBeforeFirst()) {
                        habitantes.append("  No hay menores registrados en este piso.\n");
                    } else {
                        while (rsMenores.next()) {
                            habitantes.append("  - ").append(rsMenores.getString("nombre")).append("\n");
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error al obtener los habitantes del piso del usuario con ID: " + id;
        }

        return habitantes.toString();
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
