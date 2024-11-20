package dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class Actividades {
	
	    private int id;
	    private String nombre;
	    private String descripcion;
	    private LocalDate fecha;
	    private LocalTime hora;
	    private String lugar;
	    private int edadMin;
	    private int edadMax;
	    private int creador;

	    public Actividades(int id, String nombre, String descripcion, LocalDate fecha, LocalTime hora, String lugar,
	                       int edadMin, int edadMax, int creador) {
	        this.id = id;
	        this.nombre = nombre;
	        this.descripcion = descripcion;
	        this.fecha = fecha;
	        this.hora = hora;
	        this.lugar = lugar;
	        this.edadMin = edadMin;
	        this.edadMax = edadMax;
	        this.creador = creador;
	    }

	    

	    // Getters y setters
	    public int getId() { return id; }
	    public void setId(int id) { this.id = id; }
	    public String getNombre() { return nombre; }
	    public void setNombre(String nombre) { this.nombre = nombre; }
	    public String getDescripcion() { return descripcion; }
	    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
	    public LocalDate getFecha() { return fecha; }
	    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
	    public LocalTime getHora() { return hora; }
	    public void setHora(LocalTime hora) { this.hora = hora; }
	    public String getLugar() { return lugar; }
	    public void setLugar(String lugar) { this.lugar = lugar; }
	    public int getEdadMin() { return edadMin; }
	    public void setEdadMin(int edadMin) { this.edadMin = edadMin; }
	    public int getEdadMax() { return edadMax; }
	    public void setEdadMax(int edadMax) { this.edadMax = edadMax; }
	    public int getCreador() { return creador; }
	    public void setCreador(int creador) { this.creador = creador; }

	    @Override
	    public String toString() {
	        return "Actividades [id=" + id + ", nombre=" + nombre + ", descripcion=" + descripcion +
	                ", fecha=" + fecha + ", hora=" + hora + ", lugar=" + lugar +
	                ", edadMin=" + edadMin + ", edadMax=" + edadMax + ", creador=" + creador + "]";
	    }
	}
