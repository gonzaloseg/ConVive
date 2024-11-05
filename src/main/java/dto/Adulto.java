package dto;

public class Adulto {
	
	private int id;
	private String dni, nombre, apellidos;
	private int fechaN;
	private String contrasenia, piso;
	
	
	public Adulto(int id, String dni, String nombre, String apellidos, int fechaN, String contrasenia, String piso) {
		super();
		this.id = id;
		this.dni = dni;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.fechaN = fechaN;
		this.contrasenia = contrasenia;
		this.piso = piso;
	}
	public Adulto () {
		super ();
	}
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getDni() {
		return dni;
	}
	public void setDni(String dni) {
		this.dni = dni;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	public int getFechaN() {
		return fechaN;
	}
	public void setFechaN(int fechaN) {
		this.fechaN = fechaN;
	}
	public String getContrasenia() {
		return contrasenia;
	}
	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
	}
	public String getPiso() {
		return piso;
	}
	public void setPiso(String piso) {
		this.piso = piso;
	}
	
	

}
