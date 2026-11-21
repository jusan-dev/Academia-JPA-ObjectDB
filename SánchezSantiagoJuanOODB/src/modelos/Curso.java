
package modelos;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * Clase con los atributos de un Curso, así como con métodos para tratar con
 * sus elementos
 * @author Juan Sánchez
 */
@Entity
public class Curso implements Serializable{
    /**
     * Entero con el identificador del Curso, que se autoincrementa
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    /**
     * Cadena con el nombre de la asignatura
     */
    private String nombre;
    /**
     * Conjunto de matrículas en el Curso. El dueño de la relación es Matrícula.
     * Pero si se elimina un Curso se eliminan sus Matrículas. Además, si se
     * disocia un Curso de sus matrículas también se eliminan.
     */
    @OneToMany(mappedBy = "curso", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Matricula> matriculas;
    /**
     * Asignatura con la asignatura del Curso. Se marca que la clave ajena que 
     * liga a Curso con Asignatura es id_asignatura
     */
    @ManyToOne
    @JoinColumn(name = "id_asignatura")
    private Asignatura asignatura;
    /**
     * Profesor con el profesor del Curso. Se marca que la clave ajena que 
     * liga a Curso con Profesor es id_profesor
     */
    @ManyToOne
    @JoinColumn(name = "id_profesor")
    private Profesor profesor;
    
    /**
     * Constructor vacío de Curso. Se inicializan sus matrículas para evitar
     * errores.
     */
    public Curso() {
        this.matriculas = new LinkedHashSet();
    }
    /**
     * Constructor de copia de Curso. Igual que en Asignatura, para evitar
     * que se mezclen objetos de diferentes flujos. Copia todos sus datos
     * @param c Curso del que hacer el constructor de copia
     */
    public Curso(Curso c) {
        this.id = c.id;
        this.nombre = c.nombre;
        this.matriculas = c.getCopiaMatriculas();
        this.asignatura = new Asignatura(c.asignatura);
        this.profesor = c.getProfesor();
    }

    /**
     * Constructor completo de Curso, se inicializan todos sus elementos
     * @param nombre     Cadena con el nombre del Curso
     * @param asignatura Asignatura con la asignatura del Curso
     * @param profesor   Profesor con el profesor del Curso
     */
    public Curso(String nombre, Asignatura asignatura, Profesor profesor) {
        this.nombre = nombre;
        this.matriculas = new LinkedHashSet();
        this.asignatura = asignatura;
        this.profesor = profesor;
    }
    /**
     * Constructor con el nombre del Curso
     * @param nombre Cadena con el nombre del curso
     */
    public Curso(String nombre) {
        this.nombre = nombre;
        this.matriculas = new LinkedHashSet();
    }
    
    /**
     * Método que devuelve el id del Curso
     * @return Entero con el id del Curso
     */
    public int getId() {
        return id;
    }
    /**
     * Procedimiento para establecer el id del Curso
     * @param id Entero con el id del Curso
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * Método que devuelve el nombre del Curso
     * @return Cadena con el nombre del Curso
     */
    public String getNombre() {
        return nombre;
    }
    /**
     * Procedimiento para establecer el nombre del Curso
     * @param nombre Cadena con el nombre del Curso
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    /**
     * Método que devuelve las matrículas del Curso
     * @return Conjunto con las matrículas del Curso
     */
    public Set<Matricula> getMatriculas() {
        return matriculas;
    }
    /**
     * Procedimiento para establecer las matrículas del Curso
     * @param matriculas Conjunto con las matrículas del Curso
     */
    public void setMatriculas(Set<Matricula> matriculas) {
        this.matriculas = matriculas;
    }
    /**
     * Método que devuelve una copia de las matrículas del Curso
     * @return Conjunto con la copia de las matrículas del Curso
     */
    /*
    Se utiliza para evitar la mezcla de flujos cuando se trabaje con las 
    matrículas de un Curso
    */
    public Set<Matricula> getCopiaMatriculas(){
        Set<Matricula> copia = new LinkedHashSet();
        for(Matricula m : this.matriculas){
            Matricula mat = new Matricula();
            mat.setId(m.getId());
            copia.add(mat);
        }
        return copia;
    }
    /**
     * Método que devuelve la Asignatura del Curso
     * @return Asignatura con la asignatura del Curso
     */
    public Asignatura getAsignatura() {
        return asignatura;
    }
    /**
     * Método que devuelve el Profesor del Curso
     * @return Profesor con el profesor del Curso
     */
    public Profesor getProfesor() {
        return profesor;
    }
    /**
     * Procedimiento para establecer el Profesor del Curso
     * @param profesor Profesor con el profesor del Curso
     */
    public void setProfesor(Profesor profesor) {
        this.profesor = profesor;
    } 
    /**
     * Método que devuelve los datos del Curso, con su nombre, asignatura, alumnos
     * y profesor
     * @return Cadena con los datos del Curso
     */
    @Override
    public String toString() {
        StringBuilder curso = new StringBuilder();
        if(this.asignatura == null){
            curso.append(this.nombre);
        }else{
            curso.append(this.nombre).append(": ").append(this.asignatura);
            curso.append(this.profesor == null? " | Sin profesor": " | " + this.profesor.getNombre());
        }
        return curso.toString();
    }
    
    
}

