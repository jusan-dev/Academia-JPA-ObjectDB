
package modelos;

import javax.persistence.*;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;


/**
 * Clase que cuenta con los atributos del Objeto Alumno en la base de datos
 * @author Juan Sánchez
 */
@Entity
public class Alumno extends Persona implements Serializable{
    /**
     * Double que representa la nota media total del Alumno
     */
    private double mediaAcademica;
    
    /**
     * Conjunto con las matrículas del Alumno. 
     * Se escoge un conjunto porque así se evitan duplicados cuando se extraen los 
     * datos completos del Alumno mediante FETCH en JPQL.
     * Está mapeado por el atributo alumno en Matrícula. Cuando se elimina el
     * Alumno se eliminan sus matrículas.
     * En caso de que las matrículas se separen del alumno, también se eliminarían
     * con orphan remove
     */
    @OneToMany(mappedBy = "alumno", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<Matricula> matriculas;
    
    /**
     * Constructor por defecto. Incializa el conjunto de matrículas y establece
     * la nota media en un -1 para poder tratarla en la visualización del Alumno.
     * Se elige un LinkedHashSet porque así no hay necesidad de ordenar las matrículas
     */
    public Alumno(){
        this.matriculas = new LinkedHashSet();
        this.mediaAcademica = -1;}

    /**
     * Constructor a partir de una Pèrsona. Como Alumno implementa Persona, 
     * es necesario incializar el resto de sus atributos si se parte de una 
     * Persona. Es útil cuando se recogen dados de PersonaObtenible
     * @param p 
     */
    public Alumno(Persona p) {
        super(p.getDni(), p.getNombre(), p.getSexo(), p.getEmail(), p.getFecha_nacimiento());
        this.matriculas = new LinkedHashSet();
        this.mediaAcademica = -1;
    }
    
    
    /**
     * Método que devuelve la nota media del Alumno
     * @return  Double con la nota media del Alumno
     */
    public double getMediaAcademica() {
        return mediaAcademica;
    }

    public void setMediaAcademica(double mediaAcademica) {
        this.mediaAcademica = mediaAcademica;
    }
    /**
     * Método que devuelve las matrículas del Alumno
     * @return  Conjunto con las matrículas del Alumno
     */
    /*
    No se trata el antialiasing porque en la aplicación se va a tratar con las
    matrículas cuando se recojan. Además, como JPA identifica los objetos con
    su id, tampoco tendría mucho sentido
    */
    public Set<Matricula> getMatriculas() {
        return matriculas; 
    }
    
}
