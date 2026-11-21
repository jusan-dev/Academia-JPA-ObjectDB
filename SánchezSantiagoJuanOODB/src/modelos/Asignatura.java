
package modelos;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *Clase que implementa los atributos de una Asignatura
 * @author Juan Sánchez
 */
@Entity
public class Asignatura implements Serializable {
    /*
    Id de la Asignatura, que se autoincrementa mediante la base de datos
    */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    /**
     * Cadena que representa el nombre de la Asignatura
     */
    private String nombre;
    /**
     * Cadena que representa el ámbito de la Asignatura (Humanidades o Ciencias)
     */
    private String ambito;

    /**
     * Constructor por defecto de la asignatura
     */
    public Asignatura() {
    }
    /**
     * Constructor copia de la Asignatura. Útil cuando se recupera una 
     * Asignatura con EntityManger y hay que pasarla a otro flujo. De lo 
     * contrario se produciría un error por mezclar objetos de diferentes flujos
     * @param a 
     */
    public Asignatura(Asignatura a){
        this.id = a.id;
        this.nombre = a.nombre;
        this.ambito = a.ambito;
    }
    /**
     * Constructor con el nombre y el ámbito de la Asignatura. Usado cuando
     * se inserta en la base de datos
     * @param nombre Cadena con el nombre de la Asignatura
     * @param ambito Cadena con el ámbito de la Asignatura
     */
    public Asignatura(String nombre, String ambito) {
        this.nombre = nombre;
        this.ambito = ambito;
    }
    /**
     * Método que devuelve el id de la Asignatura
     * @return Entero con el id de la Asignatura
     */
    public int getId() {
        return id;
    }
    /**
     * Procedimiento que establece el id de la Asignatura
     * @param id Entero que representa el id de la Asignatura
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * Método que devuelve el nombre de de la Asignatura
     * @return Cadena con el nombre de la Asignatura
     */
    public String getNombre() {
        return nombre;
    }
    /**
     * Procedimiento que establece el nombre de la Asignatura
     * @param nombre Cadena que representa el nombre de la Asignatura
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    /**
     * Método que devuelve el ámbito de la Asignatura
     * @return Cadena con el ámbito de la asignatura
     */
    public String getAmbito() {
        return ambito;
    }
    /**
     * Método que devuelve el nombre de la asignatura como una cadena
     * @return  Cadena con el nombre de la Asignatura
     */
    @Override
    public String toString() {
        return nombre;
    }
    
    
}
