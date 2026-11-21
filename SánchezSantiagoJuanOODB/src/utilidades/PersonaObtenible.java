
package utilidades;
/**
 * Interfaz que pretende ser implementada en los JFrames en los que se cuente
 * con datos pertienentes a la clase Persona para agilizar y automatizar 
 * la recogida de sus datos
 * @author Juan Sánchez
 */
public interface PersonaObtenible {
    /**
     * Recoge el DNI de la Persona
     * @return Cadena con el DNI de la Persona
     */
    String getDni();
    /**
     * Recoge el nombre de la Persona
     * @return Cadena con el nombre de la Persona
     */
    String getNombre();
    /**
     * Recoge el DNI de la Persona
     * @return Cadena con el DNI de la Persona
     */
    String getSexo();
    /**
     * Recoge el sexo de la Persona
     * @return Cadena con el sexo de la Persona
     */
    String getEmail();
    /**
     * Recoge la fecha de nacimiento de la Persona
     * @return Cadena con la fecha de nacimiento de la Persona
     */
    String getFecha_nacimiento();
    
    /*
    Todos los métodos devuelven una cadena porque como se van a recoger en su
    mayoría de un  JTextField, considero que es preferible tratarlos en el método
    que recoge estos datos
    */
}
