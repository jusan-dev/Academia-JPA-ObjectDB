
package modelos;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import javax.persistence.*;
/**
 * Clase de la que heredan Alumno y Profesor y que cuenta con los atributos
 * básicos de una Persona en esta aplicación
 * @author Alumno
 */
//MappedSuperclass siver para que JPA reconozca que las otras clases heredan de esta
//y, por tanto, hereden sus atributos
@MappedSuperclass
public class Persona implements Serializable{
    /**
     * Cadena con el DNI del Persona, que funciona como id
     */
    //Un Alumno no utilizara su NRE como id porque entiendo que una Academia
    //no trabaja con esos datos
    @Id
    private String dni;
    /**
     * Cadena con el nombre del Persona
     */
    private String nombre;
    /**
     * Cadena con el sexo del Persona
     */
    private String sexo;
    /**
     * Cadena con el email del Persona
     */
    private String email;
    /**
     * LocalDate con la fecha de nacimiento de la Persona
     */
    private LocalDate fecha_nacimiento;

    /**
     * Constructor por defecto de la Persona
     */
    public Persona(){}
    
    /**
     * Constructor completo de la Persona
     * @param dni               Cadena con el DNI de la Persona
     * @param nombre            Cadena con el nombre de la Persona
     * @param sexo              Cadena con el sexo de la Persona
     * @param email             Cadena con el email de la Persona
     * @param fecha_nacimiento  LocalDate con la fecha de nacimiento de la Persona
     */
    public Persona(String dni, String nombre, String sexo, String email, LocalDate fecha_nacimiento) {
        this.dni = dni;
        this.nombre = nombre;
        this.sexo = sexo;
        this.email = email;
        this.fecha_nacimiento = fecha_nacimiento;
    }
    /**
     * Método que devuelve el DNI de la Persona
     * @return Cadena con el DNI de la Persona
     */
    public String getDni() {
        return dni;
    }
    /**
     * Procedimiento para establecer el DNI de la Persona
     * @param dni Cadena con el alumno de la Persona
     */
    public void setDni(String dni) {
        this.dni = dni;
    }
    /**
     * Método que devuelve el nombre de la Persona
     * @return Cadena con el nombre de la Persona
     */
    public String getNombre() {
        return nombre;
    }
    /**
     * Procedimiento para establecer el nombre de la Persona
     * @param nombre Cadeba con el nombre de la Persona
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    /**
     * Método que devuelve el sexo de la Persona
     * @return Cadena con el sexo de la Persona
     */
    public String getSexo() {
        return sexo;
    }
    /**
     * Procedimiento para establecer el sexo de la Persona
     * @param sexo Cadena con el sexo de la Persona
     */
    public void setSexo(String sexo) {
        this.sexo = sexo;
    }
    /**
     * Método que devuelve el email de la Persona
     * @return Cadena con el email de la Persona
     */
    public String getEmail() {
        return email;
    }
    /**
     * Procedimiento para establecer el email de la Persona
     * @param email Cadena con el email de la Persona
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Método que devuelve la fecha de nacimiento de la Persona
     * @return Cadena con la fecha de nacimiento de la Persona
     */
    public LocalDate getFecha_nacimiento() {
        return fecha_nacimiento;
    }
    /**
     * Procedimiento para establecer la fecha de nacimiento de la Persona
     * @param fecha_nacimiento LocalDate con la fecha de nacimiento de la Persona
     */
    public void setFecha_nacimiento(LocalDate fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }
    /**
     * Método que comprueba si el DNI es uno válido
     * @param dni Cadena con el DNI a validar
     * @return    True si es válido, false si es inválido
     */
    public static boolean validoDNI(String dni){
        //Se establecen las letras utilizads en un DNI
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        //Se comprueba que la cadena pasada tiene un formato correcto
        if (dni.matches("^\\d{8}[A-Z]$")){
            //Si es así, se obtiene el resto del número para conseguir la
            //posición de la letra en el array anterior
            int posicionLetra = Integer.parseInt(dni.substring(0, 8)) % 23;
            //Si la letra es correcta se devuelve true
            String letra = Character.toString(letras.charAt(posicionLetra));
            if(dni.substring(8).compareToIgnoreCase(letra) == 0){
                return true;
            }
        }
        return false;
    }
    //Equals para que se pueda poner el Profesor en el comboBox de la edición de Curso
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.dni);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Persona other = (Persona) obj;
        return Objects.equals(this.dni, other.dni);
    }
    
    
}