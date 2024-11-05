package dto;

public class Actividades {
int id;
String nombre;
String descripcion;
String hora;
int edad_min;
int edad_max;
int creador;

public Actividades(int id, String nombre, String descripcion, String hora, int edad_min, int edad_max, int creador) {
	super();
	this.id = id;
	this.nombre = nombre;
	this.descripcion = descripcion;
	this.hora = hora;
	this.edad_min = edad_min;
	this.edad_max = edad_max;
	this.creador = creador;
}
public int getId() {
	return id;
}

/**
 * @param id the id to set
 */
public void setId(int id) {
	this.id = id;
}
/**
 * @return the nombre
 */
public String getNombre() {
	return nombre;
}
/**
 * @param nombre the nombre to set
 */
public void setNombre(String nombre) {
	this.nombre = nombre;
}
/**
 * @return the descripcion
 */
public String getDescripcion() {
	return descripcion;
}
/**
 * @param descripcion the descripcion to set
 */
public void setDescripcion(String descripcion) {
	this.descripcion = descripcion;
}
/**
 * @return the hora
 */
public String getHora() {
	return hora;
}
/**
 * @param hora the hora to set
 */
public void setHora(String hora) {
	this.hora = hora;
}
/**
 * @return the edad_min
 */
public int getEdad_min() {
	return edad_min;
}
/**
 * @param edad_min the edad_min to set
 */
public void setEdad_min(int edad_min) {
	this.edad_min = edad_min;
}
/**
 * @return the edad_max
 */
public int getEdad_max() {
	return edad_max;
}
/**
 * @param edad_max the edad_max to set
 */
public void setEdad_max(int edad_max) {
	this.edad_max = edad_max;
}
/**
 * @return the creador
 */
public int getCreador() {
	return creador;
}
/**
 * @param creador the creador to set
 */
public void setCreador(int creador) {
	this.creador = creador;
}
@Override
public String toString() {
	return "Actividades [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion + ", hora=" + hora
			+ ", edad_min=" + edad_min + ", edad_max=" + edad_max + ", creador=" + creador + "]";
}

}
