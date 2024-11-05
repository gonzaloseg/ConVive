package dto;

import java.time.ZonedDateTime;

public class Actividades {
    private int id;
    private String nombre;
    private ZonedDateTime date;

    public Actividades(ZonedDateTime date, String nombre, int id) {
        this.date = date;
        this.nombre = nombre;
        this.id = id;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public String getNombre() {
        return nombre;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Actividad: " + nombre + ", ID: " + id;
    }
}
