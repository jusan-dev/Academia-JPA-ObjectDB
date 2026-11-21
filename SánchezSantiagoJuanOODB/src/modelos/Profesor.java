package modelos;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 *Clase con los atributos de Profeosr y los métodos para trabajar con estos
 * @author Juan Sánchez
 */
@Entity
public class Profesor extends Persona implements Serializable{
    /**
     * Cadena con la especialidad del Profesor (Humanidades o ciencias)
     */
    private String especialidad;
    /**
     * Entero con los años de experiencia del Profesor
     */
    private int experiencia;
    /**
     * Conjunto con los cursos del Profesor. El dueño de la relación es Curso
     * y no hay cascade porque no existe ninguna relación entre ambos que vaya
     * a afectar a sus componentes o a su existencia
     */
    @OneToMany(mappedBy = "profesor")
    private Set<Curso> cursos;

    /**
     * Constructor por defecto de Profesor que inicializa su conjunto de cursos
     */
    public Profesor() {
        this.cursos = new LinkedHashSet();
    }
    /**
     * Constructor completo de Profesor //Creo que no se usa
     * @param dni
     * @param nombre
     * @param sexo
     * @param email
     * @param fecha_nacimiento
     * @param especialidad
     * @param experiencia 
     */
    public Profesor(String dni, String nombre, String sexo, String email, LocalDate fecha_nacimiento, String especialidad, int experiencia) {
        super(dni, nombre, sexo, email, fecha_nacimiento);
        this.especialidad = especialidad;
        this.experiencia = experiencia;
        this.cursos = new LinkedHashSet();
    }
    /**
     * Constructor de Profesor a partir de Persona. También se inicializan sus cursos
     * @param p Persona con los datos básicos de Profesor
     */
    public Profesor(Persona p) {
        super(p.getDni(), p.getNombre(), p.getSexo(), p.getEmail(), p.getFecha_nacimiento());
        this.cursos = new LinkedHashSet();
    }
    /**
     * Método que devuelve la especialidad del Profesor
     * @return Cadena con la especialidad del Profesor
     */
    public String getEspecialidad() {
        return especialidad;
    }
    /**
     * Procedimiento para establecer la especialidad del Profesor
     * @param especialidad Cadena con la especialidad del Profesor
     */
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
    /**
     * Método que devuelve la experiencia del Profesor
     * @return Entero con la experiencia del Profesor
     */
    public int getExperiencia() {
        return experiencia;
    }
    /**
     * Procedimiento para establecer la experiencia del Profesor
     * @param experiencia Entero con la experiencia del Profesor
     */
    public void setExperiencia(int experiencia) {
        this.experiencia = experiencia;
    }
    /**
     * Método que devuelve los cursos del Profesor
     * @return Conjunto con los cursos del Profesor
     */
    public Set<Curso> getCursos() {
        return cursos;
    }
    /**
     * Método que devuelve una cade con algunos de los datos del Profesor
     * @return Cadena con los datos del profesor
     */
    @Override
    public String toString() {
        String profesor = super.getDni().equals("-1")? super.getNombre() : super.getNombre() + ": " + especialidad;
        return profesor;
    }
    
}
