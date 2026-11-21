package utilidades;

import daos.AsignaturaDAO;
import java.awt.Component;
import java.awt.Container;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import modelos.Asignatura;
import modelos.Matricula;
import modelos.Persona;

/**
 *Clase con métodos comunes a diferentes controladores
 * @author Juan Sánchez
 */
public class Utilidades {

    //Reúno todos estos métodos en esta clase porque facilita el trabajo entre
    //las diferentes ventanas.
    //Habría quedado más limpio si fueran diferentes clases, pero no me ha dado tiempo.
    
    /**
     * Método que lanza un popup personalizado para preguntar por un borrado o
     * una actualización
     * @param objeto Cadena con el nombre del objeto sobre el que preguntar
     * @return       0 si se desea actualizar el objeto, 1 si se desea borrar
     */
    public static int editarOBorrar(String objeto) {
        //Solo cojo el método del JOptionPane y le establezco las opciones a elegir
        String[] opciones = {"Editar", "Borrar"};
        return JOptionPane.showOptionDialog(null, "Selecciona una acción:",
                "Editar o borrar " + objeto, 0, JOptionPane.QUESTION_MESSAGE, null, opciones, 0);
    }
    /**
     * Método que limpia los campos de la ventana introducida
     * @param ventana JFrame a limpiar
     */
    public static void limpiarVentana(JFrame ventana) {
        //Se comprueban todos los componentes de la ventana y se vacían,
        //se establece su opción predeterminada, se deseleccionan o se limpian
        for (Component c : ventana.getContentPane().getComponents()) {
            if (c instanceof JPanel) {
                //Si es un panel se lanza el método de abajo
                JPanel panel = (JPanel) c;
                Utilidades.limpiarContenedor(panel);
            } else if (c instanceof JTextField) {
                JTextField texto = (JTextField) c;
                if (texto.isEditable()) {
                    texto.setText("");
                }
            } else if (c instanceof JComboBox) {
                JComboBox combo = (JComboBox) c;
                combo.setSelectedIndex(-1);
                combo.removeAllItems();
            } else if (c instanceof JCheckBox) {
                ((JCheckBox) c).setSelected(false);
            } else if (c instanceof JScrollPane) {
                //Me costó bastante ver que una tabja está dentro de un scroll
                //y por eso primero tengo que buscar el scroll. Además, tengo que
                //coger el viewpoort del scroll. Tuve que buscarlo para poder hacerlo
                JScrollPane scroll = (JScrollPane) c;
                Component componente = scroll.getViewport().getView();
                if(componente instanceof JTable){
                    JTable tabla = (JTable) componente;
                    MiDefaultTableModel modelo = (MiDefaultTableModel)tabla.getModel();
                    modelo.setRowCount(0);
                }
            }
        }
    }           
    /**
     * Método que limpia un contenedor
     * @param contenedor Container a limpiar
     */
    //El mismo método anterior pero para contenedores
    public static void limpiarContenedor(Container contenedor) {
        for (Component c : contenedor.getComponents()) {
            if (c instanceof JTextField) {
                JTextField texto = (JTextField) c;
                if (texto.isEditable()) {
                    texto.setText("");
                }
            } else if (c instanceof JComboBox) {
                JComboBox combo = (JComboBox) c;
                combo.setSelectedIndex(-1);
                combo.removeAllItems();
            } else if (c instanceof JCheckBox) {
                ((JCheckBox) c).setSelected(false);
            }
        }
    }
    /**
     * Método que devuelve el objeto de la primera columna de la fila seleccionada
     * @param tabla JTable sobre la que recoger el objeto y mirar la fila seleccionada
     * @return      Objeto con el valor de la primera columna de la fila
     */
    public static Object getIdFilaSeleccionada(JTable tabla) {
        int fila = tabla.getSelectedRow();
        return tabla.getValueAt(fila, 0);
    }
    /**
     * Procedimiento que cambia de un JFrame a otro y limpia el primero
     * @param ventanaCerrando JFrame a cerrar
     * @param ventanaAbriendo JFrame a abrir
     */
    public static void cambioLimpioVentana(JFrame ventanaCerrando, JFrame ventanaAbriendo) {
        ventanaCerrando.setVisible(false);
        //la ventana se limpia tras hacerse invisible
        Utilidades.limpiarVentana(ventanaCerrando);
        ventanaAbriendo.setVisible(true);
    }
    /**
     * Procedimiento que hace visible una ventana, invisible otra y vacía la 
     * tabla de esta
     * @param ventanaCerrando  JFrame a cerrar y limpiar tabla
     * @param ventanaAbriendo  JFrame a abrir
     * @param modelo 
     */
    public static void cambiarVentana(JFrame ventanaCerrando, JFrame ventanaAbriendo, DefaultTableModel modelo){
        ventanaCerrando.setVisible(false);
        ventanaAbriendo.setVisible(true);
        modelo.setRowCount(0);
    }
    /**
     * Procedimiento que vacía la memoria utilizada por un JFrame y pasa a otro JFrame
     * @param ventanaCerrando JFrame a cerrar y limpiar de memoria
     * @param ventanaAbriendo JFrame a abrir
     */
    //La uso principalmente para cuando salgo de ventanas sin pulsar la X
    public static void cerrarVentana(JFrame ventanaCerrando, JFrame ventanaAbriendo) {
        ventanaCerrando.setVisible(false);
        //Como arriba, se limpia la ventana a hacer invisible
        Utilidades.limpiarVentana(ventanaCerrando);
        //Se libera la memoria de esa ventana
        ventanaCerrando.dispose();
        ventanaAbriendo.setVisible(true);
    }
    /**
     * Procedimiento que carga un JComboBox con los géneros establecidos
     * @param sexo  Cadena con el género de la persona
     * @param combo JComboBox a cargar
     */
    public static void cargarComboSexo(String sexo, JComboBox combo) {
        //Se borran los elementos, por si acaso
        combo.removeAllItems();
        //Se añaden los géneros
        combo.addItem("Masculino");
        combo.addItem("Femenino");
        combo.addItem("Otro");
        //Si sexo está null, se selecciona la opción vacía. De lo contrario,
        //se selecciona la opción de la persona
        if (sexo != null) {
            combo.setSelectedItem(sexo);
        } else {
            combo.setSelectedIndex(-1);
        }
    }
    /**
     * Procedimiento que carga las asignaturas en un JComboBox
     * @param combo JComboBox en el que cargar las asignaturas
     */
    public static void cargarComboAsignatura(JComboBox combo){
        combo.removeAllItems();
        //Se recogen todas las asignaturas, se cargan en el comboBox y se
        //selecciona la opción vacía
        for(Asignatura a : AsignaturaDAO.consultaVagaAsignaturas()){
            combo.addItem(a);
        }
        combo.setSelectedIndex(-1);
    }
    /**
     * Método que recoge los datos de una Persona
     * @param v JFrame que implementa la interfaz PersonaObtenible
     * @return  Persona con datos si todos estos son válidos. Null si alguno
     *          no lo es
     */
    public static Persona recogerPersona(PersonaObtenible v) {
        Persona p = null;
        //Se sirve de la interfaz para recoger todos los datos que estarán
        //presentes en el JFrame que la implemente
        String dni = v.getDni().toUpperCase();
        String nombre = v.getNombre();
        String email = v.getEmail();
        String sexo = v.getSexo();
        LocalDate nacimiento;
        
        //Si alguno de estos es inválido se lanza un mensaje personalizado
        if (dni.isBlank()) {
            JOptionPane.showMessageDialog(null, "Introduce un DNI", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (!Persona.validoDNI(dni)) {
            JOptionPane.showMessageDialog(null, "Introduce un DNI válido", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (nombre.isBlank()) {
            JOptionPane.showMessageDialog(null, "Introduce un nombre", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (email.isBlank()) {
            JOptionPane.showMessageDialog(null, "Introduce un email", "Error", JOptionPane.ERROR_MESSAGE);
        } else if (sexo.isBlank()) {
            JOptionPane.showMessageDialog(null, "Selecciona un sexo", "Error", JOptionPane.ERROR_MESSAGE);
        }else{
            try {
                //Por último, si no hay errores anteriores y la fecha se recibe
                //correctamente, se crea la Persona
                nacimiento = LocalDate.parse(v.getFecha_nacimiento());
                p = new Persona(dni, nombre, sexo, email, nacimiento);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(null, "Fecha inválida (yyyy-mm-dd)", "Error", JOptionPane.ERROR_MESSAGE);
            }  
        }
        return p;
    }
    /**
     * Método encargado de cargar listas de arrays de Object en una tabla. Es decir
     * carga las diferentes filas de una tabla que se construye con un modelo
     * @param filas Lista de arrays Object que se van a insertar
     * @param tabla JTable sobre la que insertar los datos
     * @return      1 si se inserta algo, 0 si no se inserta nada
     */
    public static int cargarEnTabla(List<Object[]> filas, JTable tabla){
        MiDefaultTableModel modelo = (MiDefaultTableModel) tabla.getModel();
        modelo.setRowCount(0);
        //El entero resultado permite mandar mensajes sobre el éxito o fracaso
        //al introducir objetos en la tabla
        int resultado = 1;
        switch (filas.size()) {
            case 0:
                resultado = 0;
                break;
            case 1:
                modelo.addRow(filas.getFirst());
                break;
            default:
                for (Object[] fila : filas) {
                    modelo.addRow(fila);
                }
                break;
        }
        return resultado;
    }
    /**
     * Método que recoge los datos que se pretenden presentar en una tabla. Para eso,
     * los introduce en diferentes arrays de Object
     * @param matriculas Lista con las matriculas de las que recoger los datos
     * @return 
     */
    //Como recojo matrículas en diferentes ventanas, este método consigue agilizar
    //ese proceso
    public static List<Object[]> recuperarDatosMatricula(List<Matricula> matriculas){
        List<Object[]> filas = new ArrayList();
        for(Matricula m : matriculas){
            //Si la matrícula no tiene nota, se le pone un mensaje especial. Se recogen
            //todos los datos en un array que se pueda insertar en la tabla
            String nota = m.getNota() == -1? "En curso" : Double.toString(m.getNota());
            Object[] objeto = {m.getId(), m.getAlumno().getDni(), m.getCurso().getNombre(), 
                m.getCurso().getAsignatura(), m.getFecha_matriculacion(), nota};
            filas.add(objeto);
        }
        return filas;
    }
}
