package BaseDeDatos;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
	

	
		public static  Connection dameConexion(String bbdd) {
			Connection conn = null;

			try { // registro el driver de connection

				Class.forName("com.mysql.cj.jdbc.Driver");

			} catch (ClassNotFoundException e) {

				e.printStackTrace();

			}

			try { // Establezco la conexion don la BBDD

				conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+bbdd, "root", "");
				
			} catch (SQLException ex) {

				ex.printStackTrace();
				System.out.println("SQLException : " + ex.getMessage());

			}
			return conn;

		}
		}

	
	

	
