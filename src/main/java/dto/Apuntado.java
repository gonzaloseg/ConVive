package dto;

public class Apuntado {
    private int id;             // id de la inscripción
    private Integer idAdulto;   // id del adulto, puede ser nulo
    private Integer idMenor;    // id del menor, puede ser nulo
    private int idActividad;    // id de la actividad a la que está apuntado

    // Constructor sin parámetros
    public Apuntado() {}

    // Constructor con parámetros
    public Apuntado(int id, Integer idAdulto, Integer idMenor, int idActividad) {
        this.id = id;
        this.idAdulto = idAdulto;
        this.idMenor = idMenor;
        this.idActividad = idActividad;
    }

    // Getter y Setter para el campo id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    // Getter y Setter para el campo idAdulto
    public Integer getIdAdulto() {
        return idAdulto;
    }

    public void setIdAdulto(Integer idAdulto) {
        this.idAdulto = idAdulto;
    }

    // Getter y Setter para el campo idMenor
    public Integer getIdMenor() {
        return idMenor;
    }

    public void setIdMenor(Integer idMenor) {
        this.idMenor = idMenor;
    }

    // Getter y Setter para el campo idActividad
    public int getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    @Override
    public String toString() {
        return "Apuntado{" +
                "id=" + id +
                ", idAdulto=" + idAdulto +
                ", idMenor=" + idMenor +
                ", idActividad=" + idActividad +
                '}';
    }
}
