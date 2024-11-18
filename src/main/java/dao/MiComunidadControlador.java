package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MiComunidadControlador {
    
    @FXML
    private BarChart<String, Number> barChart;

    @FXML
    private ImageView img_volver; 
    @FXML
    private Button btn_1;
    @FXML
    private Button btn_2;
    @FXML
    private Button btn_3;
    @FXML
    private Button btn_4;
    @FXML
    private Button btn_5;
    @FXML
    private Button btn_6;
    @FXML
    private Button btn_7;
    @FXML
    private Button btn_8;
    @FXML
    private Button btn_9;
    @FXML
    private Button btn_10;
    @FXML
    private Button btn_11;
    @FXML
    private Button btn_12;
    @FXML
    private Button btn_13;
    @FXML
    private Button btn_14;
    
    public void initialize() {
    	cargarDatosEnGrafica();
        // Asigna un evento de clic a img_volver
        img_volver.setOnMouseClicked(event -> volver(new ActionEvent()));
        btn_1.setOnMouseEntered(event -> cambiarColorBotonAmarillo(btn_1));
        btn_1.setOnMouseExited(event -> restaurarColorBoton(btn_1));
        btn_2.setOnMouseEntered(event -> cambiarColorBotonAmarillo(btn_2));
        btn_2.setOnMouseExited(event -> restaurarColorBoton(btn_2));
        btn_3.setOnMouseEntered(event -> cambiarColorBotonAmarillo(btn_3));
        btn_3.setOnMouseExited(event -> restaurarColorBoton(btn_3));
        btn_4.setOnMouseEntered(event -> cambiarColorBotonAmarillo(btn_4));
        btn_4.setOnMouseExited(event -> restaurarColorBoton(btn_4));
        btn_5.setOnMouseEntered(event -> cambiarColorBotonAmarillo(btn_5));
        btn_5.setOnMouseExited(event -> restaurarColorBoton(btn_5));
        btn_6.setOnMouseEntered(event -> cambiarColorBotonAmarillo(btn_6));
        btn_6.setOnMouseExited(event -> restaurarColorBoton(btn_6));
        btn_7.setOnMouseEntered(event -> cambiarColorBotonAmarillo(btn_7));
        btn_7.setOnMouseExited(event -> restaurarColorBoton(btn_7));
        btn_8.setOnMouseEntered(event -> cambiarColorBotonAmarillo(btn_8));
        btn_8.setOnMouseExited(event -> restaurarColorBoton(btn_8));
        btn_9.setOnMouseEntered(event -> cambiarColorBotonAmarillo(btn_9));
        btn_9.setOnMouseExited(event -> restaurarColorBoton(btn_9));
        btn_10.setOnMouseEntered(event -> cambiarColorBotonAmarillo(btn_10));
        btn_10.setOnMouseExited(event -> restaurarColorBoton(btn_10));
        btn_11.setOnMouseEntered(event -> cambiarColorBotonAmarillo(btn_11));
        btn_11.setOnMouseExited(event -> restaurarColorBoton(btn_11));
        btn_12.setOnMouseEntered(event -> cambiarColorBotonAmarillo(btn_12));
        btn_12.setOnMouseExited(event -> restaurarColorBoton(btn_12));
        btn_13.setOnMouseEntered(event -> cambiarColorBotonAmarillo(btn_13));
        btn_13.setOnMouseExited(event -> restaurarColorBoton(btn_13));
        btn_14.setOnMouseEntered(event -> cambiarColorBotonAmarillo(btn_14));
        btn_14.setOnMouseExited(event -> restaurarColorBoton(btn_14));
       

    }

    private void cambiarColorBotonAmarillo(Button boton) {
        boton.setStyle("-fx-background-color: yellow;");
    }

    private void restaurarColorBoton(Button boton) {
        boton.setStyle(""); // Elimina el estilo en línea, volviendo al estilo original
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

    private void cargarDatosEnGrafica() {
        // Obtiene los datos de la base de datos
        int numeroDeAdultos = getNumeroDeAdultos();
        int numeroDeMenores = getNumeroDeMenores();

        // Crea una serie de datos para la gráfica
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Distribución por Edad");

        // Añade los datos a la serie
        series.getData().add(new XYChart.Data<>("Adultos", numeroDeAdultos));
        series.getData().add(new XYChart.Data<>("Menores", numeroDeMenores));

        // Añade la serie al gráfico de barras
        barChart.getData().add(series);
    }

    private int getNumeroDeAdultos() {
        String query = "SELECT COUNT(*) AS total FROM adulto";
        try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Retorna 0 si ocurre un error
    }

    // Consulta el número de menores en tiempo real desde la tabla 'menores'
    private int getNumeroDeMenores() {
        String query = "SELECT COUNT(*) AS total FROM menor";
        try (Connection conn = BaseDeDatos.Conexion.dameConexion("convive");
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Retorna 0 si ocurre un error
    }
    
    


}