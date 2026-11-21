package modelos;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Clase con los atributos ligados a una Matrícula así como con métodos para extraer
 * y establecer sus datos
 * @author Juan Sánchez
 */
@Entity
public class Matricula implements Serializable {
    /**
     * Entero con el id autogenerado y autoincrementado
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    /**
     * Double con la nota ligada a la matrícula
     */
    private double nota;
    /**
     * LocalDate con la fecha de matriculación
     */
    private LocalDate fecha_matriculacion;
    /**
     * Alumno con el alumno de la Matrícula. Está puesto cascade Merge porque
     * se supone que así JPA, al introducir una Matrícula, comprueba si 
     * existe el Alumno y, si se da, no hay probabilidad de que aparezca un error.
     * Por lo que estuve buscando de esta manera se asegura de que no haya errores
     * cuando se va insertar una Matrícula con un Alumno ya existente. Aunque con 
     * Persist tampoco debiera de haber problemas, pero de esta forma se garantiza
     * que no pase nada no pretendido
     */
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "id_alumno")
    private Alumno alumno;
    /**
     * Curso con el curso de la Matrícula. Matrícula es la dueña de la relación
     * con Curso y se liga mediante id_curso. En este caso no hay cascade porque
     * realmente no existen cambios en la Matrícula que deban afectar al Curso
     */
    @ManyToOne
    @JoinColumn(name = "id_curso")
    private Curso curso;
    
    /**
     * Constructor por defecto de Matrícula. Solo se establece la fecha de 
     * matriculación y una nota de -1 para poder tratarla en la presentación
     * en tablas
     */
    public Matricula() {
        this.fecha_matriculacion = LocalDate.now();
        this.nota = -1;
    }
    /**
     * Constructor con el Alumno y el Curso de la Matrícula. Fecha y nota se 
     * establecen de igual manera que en el constructor anterior
     * @param alumno Alumno con el alumno del Curso
     * @param curso  Curso con el curso del Alumno
     */
    public Matricula(Alumno alumno, Curso curso) {
        this.nota = -1;
        this.fecha_matriculacion = LocalDate.now();
        this.alumno = alumno;
        this.curso = curso;
    }
    /**
     * Método que devuelve el id de la Matrícula
     * @return Entero con el id de la Matrícula
     */
    public int getId() {
        return id;
    }
    /**
     * Procedimiento para establecer el id de la Matrícula
     * @param id Entero con el profesor de la Matrícula
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * Método que devuelve la nota de la Matrícula
     * @return Double con la nota de la Matrícula
     */
    public double getNota() {
        return nota;
    }
    /**
     * Procedimiento para establecer la nota de la Matrícula
     * @param nota Double con la nota de la Matrícula
     */
    public void setNota(double nota) {
        this.nota = nota;
    }
    /**
     * Método que devuelve la fecha de matriculción de la Matrícula
     * @return LocalDate con la fecha de matriculación de la Matrícula
     */
    public LocalDate getFecha_matriculacion() {
        return fecha_matriculacion;
    }
    /**
     * Método que devuelve el Alumno de la Matrícula
     * @return Alumno con el alumno de la Matrícula
     */
    public Alumno getAlumno() {
        return alumno;
    }
    /**
     * Método que devuelve el Curso de la Matrícula
     * @return Curso con el curso de la Matrícula
     */
    public Curso getCurso() {
        return curso;
    }
    /**
     * Método para devolver una cadena con el curso, la fecha de matriculación
     * y la nota de la Matrícula. Si no hay nota se establece un valor por
     * defecto
     * @return 
     */
    @Override
    public String toString() {
        String cadenaNota = this.nota == -1 ? "en curso": Double.toString(this.nota);
        return "Curso: " + this.curso.getNombre() + "| Fecha de matriculación: " + this.fecha_matriculacion + 
               "| Nota del curso: " + cadenaNota;
    }
    
    
    
    
}
