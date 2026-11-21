/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controladores;

import daos.CursoDAO;
import daos.ProfesorDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.event.PopupMenuEvent;
import modelos.Asignatura;
import modelos.Curso;
import modelos.Persona;
import modelos.Profesor;
import utilidades.MiPopupMenuAdapter;
import utilidades.PersonaObtenible;
import utilidades.Utilidades;
import vistas.VentanaAddProfesor;
import vistas.VentanaConsultaProfesores;
import vistas.VentanaEditarProfesor;
import vistas.VentanaProfesor;

/**
 *Controlador con las ventanas para modificar, insertar y eliminar profesores. 
 * También cuenta con todos los eventos de estas ventanas
 * @author Juan Sánchez
 */
public class ControladorProfesores {
    /**
     * JFrame con la ventana de consulta de profesores
     */
    private VentanaConsultaProfesores consultaProfesor;
    /**
     * JFrame con la ventana de edición de profesores
     */
    private VentanaEditarProfesor editarProfesor;
    /**
     * JFrame con la ventana de inserción de Profesor
     */
    private VentanaAddProfesor addProfesor;
    /**
     * JFrame con la ventana de inicio de la parte de los profesores
     */
    private VentanaProfesor inicioProfesor;
    /**
     * Controlador inicial para voler a la pantalla de inicio
     */
    private ControladorInicial inicio;
    
    /**
     * Controlador que inicia todos los atributos, cambia sus propiedades
     * respecto a su comportamiento al cerrarse y los eventos de estos
     * @param controladorInicio 
     */
    public ControladorProfesores(ControladorInicial controladorInicio){
        this.consultaProfesor = new VentanaConsultaProfesores();
        this.editarProfesor = new VentanaEditarProfesor();
        this.addProfesor = new VentanaAddProfesor();
        this.inicioProfesor = new VentanaProfesor();
        this.inicio = controladorInicio;
        
        //La ventana de inicio se hace visible
        this.inicioProfesor.setVisible(true);
        
        //Todas las ventanas liberan memoria al cerrarse pero no cierran la app
        this.inicioProfesor.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.consultaProfesor.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.editarProfesor.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.addProfesor.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        //Eventos
        
        //Evento para pasar de la ventana de inicio a la ventana principal de la app
        inicioProfesor.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                Utilidades.cambioLimpioVentana(inicioProfesor, inicio.getVentana());  
            }
        });
        //Evento para pasar de la ventana de inicio a la de consulta
        inicioProfesor.jBotonProfesores.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarComboConsulta();
                Utilidades.cambioLimpioVentana(inicioProfesor, consultaProfesor);
            }
        });
        //Evento para pasar de la ventana de inicio a la de inserción
        inicioProfesor.jBotonAddProfesor.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                Utilidades.cambioLimpioVentana(inicioProfesor, addProfesor);
                Utilidades.cargarComboSexo(null, addProfesor.jComboSexo);
                addProfesor.jComboEspecialidad.addItem("Humanidades");
                addProfesor.jComboEspecialidad.addItem("Ciencias");
                addProfesor.jComboEspecialidad.setSelectedIndex(-1);
            }
        });
        
        
        
        //Evento para volver de la ventana de inserción a la de inicio
        addProfesor.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                Utilidades.cambioLimpioVentana(addProfesor, inicioProfesor);  
            }
        });
        //Evento que añade un Profesor
        addProfesor.jBotonEnviar.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se recogen los datos del profesor
                String experiencia = addProfesor.jTextoExperiencia.getText();
                Profesor p = recogerProfesor(experiencia, addProfesor);
                //Si son válidos, se establece su especialidad
                if (p != null) {
                    p.setEspecialidad((String) addProfesor.jComboEspecialidad.getSelectedItem());
                    if (p.getEspecialidad() == null) {
                        //Si no se selecciona una especialidad, se manda un aviso
                        JOptionPane.showMessageDialog(null, "Selecciona una especialidad", "Error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        //Si se escoge, se inserta el Profesor y se manda un 
                        //mensaje en función del valor de éxito
                        if (ProfesorDAO.insertarProfesor(p) != 0){
                            JOptionPane.showMessageDialog(null, "Docente introducido correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            //También se limpia la ventana
                            Utilidades.limpiarVentana(addProfesor);
                        }else{
                           JOptionPane.showMessageDialog(null, "Erro en la inserción", "Error", JOptionPane.ERROR_MESSAGE); 
                        }
                    }
                }
            }
        });
        
        
        
        //Evento para pasar de la ventana de consulta a la de inicio
        consultaProfesor.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                Utilidades.cambioLimpioVentana(consultaProfesor, inicioProfesor);
                
            }
        });
        //Evento que busca un profesor en función de su nombre
        consultaProfesor.jBotonBuscarProfesor.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se recoge el nombre
                String nombre = consultaProfesor.jTextoNombre.getText();
                List<Object[]> filas;
                //Si se ha escrito un nombre, se busca en la base de datos y 
                //se prepara para insertar los profesores coincidentes en la tabla
                if(!nombre.isBlank()){
                    filas = recuperarDatos(ProfesorDAO.consultaVagaProfesorNombre(nombre));
                //Si no se ha escrito un nombre se buscan todos los profesores
                }else{
                    filas = recuperarDatos(ProfesorDAO.consultaVagaProfesores());
                }
                //Finalmente, se insertan los datos obtenidos en la búsqueda
                if(Utilidades.cargarEnTabla(filas, consultaProfesor.jTablaProfesores) == 0){
                    //Si no hay datos, se manda un mensaje
                    JOptionPane.showMessageDialog(null, "No se han encontrado profesores", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        //Evento que permite el borrado o edición de profesores
        consultaProfesor.jTablaProfesores.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                //Si se hacen dos click, se recoge el id y salta el popup
                if(e.getClickCount() == 2){
                    String dni = (String)Utilidades.getIdFilaSeleccionada(consultaProfesor.jTablaProfesores);
                    int eleccion = Utilidades.editarOBorrar("profesor/a");
                    //Si decide editarlo, se recogen todos sus datos, se carga
                    //la ventana de edición y se pasa a esta
                    if(eleccion == 0){
                        Profesor p = ProfesorDAO.consultaTotalProfesor(dni);
                        Utilidades.cambiarVentana(consultaProfesor, editarProfesor, consultaProfesor.modelo);
                        cargarEdicion(p);
                    //Si se decide borrar, se borra y se manda un mensaje en
                    //función del valor de éxito
                    }else if(eleccion == 1){
                        if(ProfesorDAO.eliminarProfesor(dni) == 1){
                            JOptionPane.showMessageDialog(null, "Profesor borrado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            Utilidades.limpiarVentana(consultaProfesor);
                        }else{
                            JOptionPane.showMessageDialog(null, "Ha ocurrido un error en el borrado", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }   
                }
            }  
        });
        //Evento que busca los profesores en función de la asignatura
        consultaProfesor.jComboAsignaturas.addPopupMenuListener(new MiPopupMenuAdapter(){
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                //Se comprueba que se ha escogido una asignatura válida
                if(consultaProfesor.jComboAsignaturas.getSelectedIndex() != -1){
                    Asignatura a = (Asignatura) consultaProfesor.jComboAsignaturas.getSelectedItem();
                    List<Profesor> profesores = new ArrayList();
                    //Se buscan los cursos junto con su profesor en función de la
                    //asignatura. Se hace así porque no hay una relación entre 
                    //Asignatura y Profesor porque sería redundante
                        for (Curso c : CursoDAO.consultaCursosProfesorPorAsignatura(a.getId())) {
                        //Si el curso tiene un profesor no presente en la lista de profesores
                        //y ese profesor es realmente uno, se inserta en la lista de profesores
                        if (!profesores.contains(c.getProfesor()) && c.getProfesor() != null) {
                            profesores.add(c.getProfesor());
                        }
                    }
                    //Se insertan los datos en la tabla. Si no hay filas de las
                    //que recuperar datos, se manda un mensaje
                    if (Utilidades.cargarEnTabla(recuperarDatos(profesores), consultaProfesor.jTablaProfesores) == 0) {
                        JOptionPane.showMessageDialog(null, "No se han encontrado profesores", "Error", JOptionPane.WARNING_MESSAGE);
                    }
                //Si no se selecciona una asignatura, también se manda un mensaje
                }else{
                    JOptionPane.showMessageDialog(null, "Selecciona una asignatura", "Error", JOptionPane.WARNING_MESSAGE);
                } 
            }
        });
        
        
        
        //Evento que pasa de la ventana de edición a la de consulta
        editarProfesor.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                Utilidades.cambioLimpioVentana(editarProfesor, consultaProfesor);  
            }
        });
        //Evento que guarda los cambios hechos en los datos del Profesor
        editarProfesor.jBotonGuardar.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se recoge el Profesor
                String experiencia = editarProfesor.jTextoExperiencia.getText();
                Profesor p = recogerProfesor(experiencia, editarProfesor);
                //Si los datos son válidos, se recoge la especialidad y se actualiza
                if (p != null) {
                    p.setEspecialidad(editarProfesor.jTextoEspecialidad.getText());
                    if (!p.getEspecialidad().isBlank()) {
                        //Se manda un mensaje en función del éxito de la actualización
                        if(ProfesorDAO.actualizarProfesor(p) != 0){
                            JOptionPane.showMessageDialog(null, "Datos modificados", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            
                        }else{
                            JOptionPane.showMessageDialog(null, "No se han realizado cambios", "Error", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            }
        });
        //Evento que disocia a un Profesor de un Curso
        editarProfesor.jBotonCursos.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se instancia una variable que recoge el éxito en los procesos
                //de separación
                int exito = 0;
                //Se recogen los Cursos seleccionados
                List<Curso> cursos = editarProfesor.jListaCursos.getSelectedValuesList();
                for(Curso c: cursos){
                    //Se disocian los cursos y se quitan de la lista si se 
                    //tiene éxito
                    if(ProfesorDAO.quitarCurso(c.getId()) == 1){
                        exito ++;
                        editarProfesor.modelo.removeElement(c);
                    }
                }
                //Si todo va bien se lanza un mensaje
                if(exito == cursos.size()){
                    JOptionPane.showMessageDialog(null, "Cursos disociados correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                //Si alguno falla, se avisa y se observará porque seguirá en la lista
                }else{
                    JOptionPane.showMessageDialog(null, "Se produjo un error en la disociación", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
    }
    /**
     * Recupera los datos a mostrar en una tabla de una lista de profesores
     * @param profs Lista con los profesores de los que extraer datos
     * @return      Lista de arrays de Objects que se insertan en la tabla
     */
    private List<Object[]> recuperarDatos(List<Profesor> profs){
        //Se instancia la lista, se recogen los datos requeridos de los profesores
        //y se insertan en la lista
        List<Object[]> filas = new ArrayList();
        for(Profesor p : profs){
            Object[] objeto = {p.getDni(), p.getNombre(), p.getEmail(), 
            p.getFecha_nacimiento(),p.getEspecialidad()};
            filas.add(objeto);
        }
        return filas;
    }
    /**
     * Procedimiento que carga la ventana de edición
     * @param p Profesor con el que cargar los datos de la ventana
     */
    private void cargarEdicion(Profesor p){     
        //Se introducen todos los datos
        editarProfesor.jTextoDNI.setText(p.getDni());
        editarProfesor.jTextoNombre.setText(p.getNombre());
        editarProfesor.jTextoEmail.setText(p.getEmail());
        editarProfesor.jTextoExperiencia.setText(Integer.toString(p.getExperiencia()));
        Utilidades.cargarComboSexo(p.getSexo(), editarProfesor.jComboSexo);
        editarProfesor.jTextoNacimiento.setText(p.getFecha_nacimiento().toString());
        editarProfesor.jTextoEspecialidad.setText(p.getEspecialidad());
        //Se quitan los cursos que puedan haber en la lista
        editarProfesor.modelo.removeAllElements();
        //Se insertan los cursos del profesor en esta
        editarProfesor.modelo.addAll(CursoDAO.consultaTotalCursosProfesor(p.getDni()));
    }
    /**
     * Procedimiento que carga el comboBox de la ventana de consulta
     */
    private void cargarComboConsulta(){
        Asignatura a = new Asignatura();
        a.setId(-1);
        a.setNombre("-Todas las asignaturas-");
        //Se recogen todas las asignaturas en el comboBox
        Utilidades.cargarComboAsignatura(consultaProfesor.jComboAsignaturas);
        //Se inserta una asignatura general en el primer puesto
        consultaProfesor.jComboAsignaturas.insertItemAt(a, 0);
    }
    /**
     * Función que crea un Profesor a partir de los datos de una ventana y 
     * de una cadena con su experiencia
     * @param experienciaCadena Cadena con la experiencia del profesor
     * @param v                 Ventana que implementa la interfaz PersonaObtenible
     * @return                  Profesor si todos los datos son válidos. Si no,
     *                          devuelve null
     */
    private Profesor recogerProfesor(String experienciaCadena, PersonaObtenible v){
        //Se recogen los datos generales de la persona
        Persona persona = Utilidades.recogerPersona(v);
        Profesor p = null;
        //Si los datos de la persona son válidos, se parsea laexperiencia
        //Y se crea el profesor
        if(persona != null){
            try{
                int experiencia = Integer.parseInt(experienciaCadena);
                p = new Profesor(persona);
                p.setExperiencia(experiencia);
            //Si hay un error en el parseo se lanza un mensaje y se devuelve null
            }catch(NumberFormatException ex){
                JOptionPane.showMessageDialog(null, "Años de experiencia inválidos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        return p;
    }
    /**
     * Método que devuelve el JFrame incial de este controlador
     * @return JFrame inicial de ControladorProfesor
     */
    public VentanaProfesor getInicioProfesor() {
        return inicioProfesor;
    }
    
}
