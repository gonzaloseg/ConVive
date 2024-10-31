package BaseDeDatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    public static Connection dameConexion(String bbdd) {
        Connection conn = null;
        try {
            // Cargar el driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establecer conexión
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + bbdd, "root", "");
        } catch (ClassNotFoundException e) {
            System.out.println("Error: Driver MySQL no encontrado.");
            e.printStackTrace();
        } catch (SQLException ex) {
            System.out.println("Error: No se pudo establecer conexión con la base de datos.");
            ex.printStackTrace();
        }
        return conn;
    }
}

