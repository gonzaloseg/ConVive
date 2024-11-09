package dto;

import java.time.ZonedDateTime;

public class Actividades {
    private int id;
    private String nombre;
    private String descripcion;
    private ZonedDateTime fechaHora;
    private String lugar;
    private int edadMin;
    private int edadMax;
    private int creador;

    // Constructor
    public Actividades(String nombre, String descripcion, ZonedDateTime fechaHora, String lugar, int edadMin, int edadMax, int creador) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaHora = fechaHora;
        this.lugar = lugar;
        this.edadMin = edadMin;
        this.edadMax = edadMax;
        this.creador = creador;
    }

    /**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @param nombre the nombre to set
	 */
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	/**
	 * @param descripcion the descripcion to set
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * @param fechaHora the fechaHora to set
	 */
	public void setFechaHora(ZonedDateTime fechaHora) {
		this.fechaHora = fechaHora;
	}

	/**
	 * @param lugar the lugar to set
	 */
	public void setLugar(String lugar) {
		this.lugar = lugar;
	}

	/**
	 * @param edadMin the edadMin to set
	 */
	public void setEdadMin(int edadMin) {
		this.edadMin = edadMin;
	}

	/**
	 * @param edadMax the edadMax to set
	 */
	public void setEdadMax(int edadMax) {
		this.edadMax = edadMax;
	}

	/**
	 * @param creador the creador to set
	 */
	public void setCreador(int creador) {
		this.creador = creador;
	}

	// Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public ZonedDateTime getFechaHora() {
        return fechaHora;
    }

    public String getLugar() {
        return lugar;
    }

    public int getEdadMin() {
        return edadMin;
    }

    public int getEdadMax() {
        return edadMax;
    }

    public int getCreador() {
        return creador;
    }

    @Override
    public String toString() {
        return "Actividad: " + nombre + ", ID: " + id + ", Fecha: " + fechaHora;
    }
}
