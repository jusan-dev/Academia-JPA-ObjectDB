/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controladores;

import daos.CursoDAO;
import daos.MatriculaDAO;
import daos.ProfesorDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.PopupMenuEvent;
import modelos.Asignatura;
import modelos.Curso;
import modelos.Profesor;
import utilidades.MiPopupMenuAdapter;
import utilidades.Utilidades;
import vistas.VentanaAddCurso;
import vistas.VentanaConsultaCursos;
import vistas.VentanaCursos;
import vistas.VentanaEditarCurso;

/**
 *Clase encargada de todos los eventos relacionados con la modificación, inserción
 * y borrado de cursos
 * @author Juan Sánchez
 */
public class ControladorCursos {
    /**
     * JFrame para navegar entre las funciones del controlador
     */
    private VentanaCursos inicioCurso;
    /**
     * JFrame usado para añadir un Curso
     */
    private VentanaAddCurso addCurso;
    /**
     * JFrame utilizado para buscar cursos según diferentes parámetros
     */
    private VentanaConsultaCursos consultaCurso;
    /**
     * JFrame para editar algunos elementos de un Curso
     */
    private VentanaEditarCurso editarCurso;
    /**
     * Controlador que permite volver al inicio de la aplicación
     */
    private ControladorInicial inicio;
    
    /**
     * Constructor completo del controlador que cuenta con todos sus eventos
     * @param controladorInicio Controlador usado para volver a la ventana inicial
     */
    public ControladorCursos(ControladorInicial controladorInicio){
        //Se inicializan todos los elementos de la clase
        this.inicioCurso = new VentanaCursos();
        this.addCurso = new VentanaAddCurso();
        this.consultaCurso = new VentanaConsultaCursos();
        this.editarCurso = new VentanaEditarCurso();
        this.inicio = controladorInicio;
        
        //La ventana del inicio del controlador se hace visible
        this.inicioCurso.setVisible(true);
        
        //S establece que todas las ventanas liberen memoria al cerrarse pero no cierren
        //la aplicación
        this.inicioCurso.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.addCurso.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.consultaCurso.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.editarCurso.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //Eventos
        
        //Evento para cambiar a la ventana de inicio de la aplicación
        inicioCurso.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                Utilidades.cambioLimpioVentana(inicioCurso, inicio.getVentana());
            }
        });
        //Evento para cambiar a la ventana para añadir un Curso
        inicioCurso.jBotonAddCurso.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                /*
                Como dije en ControladorALumno, cargo los comboBox en el cambio
                de ventana porque es como mejor funciona en mi portátil
                */
                Utilidades.cargarComboAsignatura(addCurso.jComboAsignatura);
                Utilidades.cambioLimpioVentana(inicioCurso, addCurso);
            }
        });
        //Evento para cambiar a la ventana de consulta de cursos
        inicioCurso.jBotonCurso.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se carga en comboBox de Asignatura y Profesor con todos los profesores
                Utilidades.cargarComboAsignatura(consultaCurso.jComboAsignatura);
                cargarComboProfesor(consultaCurso.jComboProfesores, null);
                Utilidades.cambioLimpioVentana(inicioCurso, consultaCurso);
            }
        });
        
        
        
        //Evento que cambia a la ventana de inicio de la parte de cursos
        addCurso.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                Utilidades.cambioLimpioVentana(addCurso, inicioCurso);
            }
        });
        /*
        Evento que carga los profesores del comboBox de profesores en la ventana
        de adición de un curso de forma dinámica según la asignatura escogida, siendo 
        esta de Humanidades o de Ciencias
        */
        addCurso.jComboAsignatura.addPopupMenuListener(new MiPopupMenuAdapter(){
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                int indice = addCurso.jComboAsignatura.getSelectedIndex();
                //El proceso se hace siempre que no se escoja la opción vacía del comboBox
                if(indice != -1){
                    Asignatura a = (Asignatura) addCurso.jComboAsignatura.getSelectedItem();
                    cargarComboProfesor(addCurso.jComboProfesor, a.getAmbito());
                }
            } 
        });
        //Evento que añade el curso creado a la base de datos
        addCurso.jBotonAddCurso.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se recoge el nombre del curso
                String nombre = addCurso.jTextoNombre.getText();
                //Se recoge la asignatura y la posición del comboBox de Profesor
                Asignatura a = (Asignatura) addCurso.jComboAsignatura.getSelectedItem();
                //Así se comprueba que el Profesor seleccionado no es el objeto vacío
                int indice = addCurso.jComboProfesor.getSelectedIndex();
                if (!nombre.isBlank() && a != null && indice != -1) {
                    //Si los elementos son válidos se inserta el curso
                    Curso curso = recogerCurso(nombre, a,
                            addCurso.jComboProfesor);
                    //Se manda un mensaje al usuario en función del valor de éxtio de la inserción
                    if (CursoDAO.insertarCurso(curso) != 0) {
                        JOptionPane.showMessageDialog(null, "Curso añadido", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al añadir el curso", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                //Si alguno de los elementos es inválido, se avisa al usuario
                }else{
                    JOptionPane.showMessageDialog(null, "Introduce un nombre, asignatura y profesor", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            
        });
        
        
        
        //Evento que cambia de la ventana de consulta a la de inicio de cursos
        consultaCurso.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                Utilidades.cambioLimpioVentana(consultaCurso, inicioCurso);
            }
        });
        //Evento que añade los cursos correspondientes a la lista de la consulta
        //en función de su Asignatura
        consultaCurso.jComboAsignatura.addPopupMenuListener(new MiPopupMenuAdapter(){
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                //Se borran todos los elementos anteriores de la lista
                consultaCurso.modelo.removeAllElements();
                //Se realiza el disparador si la Asignatura escogida es válida
                if ((consultaCurso.jComboAsignatura.getSelectedIndex() != -1)) {
                    //Se recoge la Asignatura
                    Asignatura a = (Asignatura) consultaCurso.jComboAsignatura.getSelectedItem();
                    //Si no es la Asignatura general, se recogen sus cursos
                    if (a.getId() != -1) {
                        consultaCurso.modelo.addAll(CursoDAO.consultaTotalCursosAsignatura(a.getId()));
                        //Si la consulta está vacía, se avisa al usuario
                        if (consultaCurso.modelo.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "No hay cursos con esa asignatura", "Error", JOptionPane.WARNING_MESSAGE);
                        }
                    //Si se escoge la Asignatura general, se muestra un aviso específico  
                    }else {
                        /*
                        Este condicional y esa asignatura genérica están puestos
                        para mantener la simetría con el otro comboBox. No es 
                        necesario pero creía que quedaba bien
                        */
                        JOptionPane.showMessageDialog(null, "Para buscar por asignatura hay que escoger una asignatura", "Error", JOptionPane.WARNING_MESSAGE);
                    }
                //Si no se ha escogido una asignatura, se avisa al usuario
                }
                /*
                Como la consulta de Curso se hace por Asignatura y Profesor, cuando
                uno de los dos comboBox se activa, el otro pasa a la selección vacía
                para dejar claro cual es el comboBox activo
                */
                consultaCurso.jComboProfesores.setSelectedIndex(-1);
            }
            
        });
        //Evento que muestra cursos en la lista de consulta en función de su Profesor
        consultaCurso.jComboProfesores.addPopupMenuListener(new MiPopupMenuAdapter(){
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                //Se borran todos los elementos
                consultaCurso.modelo.removeAllElements();
                //Se asegura de que es una elección válida
                if (consultaCurso.jComboProfesores.getSelectedIndex() != -1) {
                    //Se recoge el Profeosr
                    Profesor p = (Profesor) consultaCurso.jComboProfesores.getSelectedItem();
                    //Si no es el genérico, se muestran los cursos por Profesor
                    if (!p.getDni().equals("-1")) {
                        consultaCurso.modelo.addAll(CursoDAO.consultaTotalCursosProfesor(p.getDni()));
                        //Si no hay cursos con ese profesor, se manda un aviso
                        if (consultaCurso.modelo.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "No hay cursos con ese profesor", "Error", JOptionPane.WARNING_MESSAGE);
                        }
                    //Si es el Profesr genérico, se muestran los cursos sin Profesor  
                    }else {
                        consultaCurso.modelo.addAll(CursoDAO.consultaTotalCursosProfesor(null));
                        //Si no hay cursos sin Profesor, se manda un aviso
                        if (consultaCurso.modelo.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "No hay cursos sin profesor", "Error", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
                //La selección de Asignaturas se reinicia
                consultaCurso.jComboAsignatura.setSelectedIndex(-1);
            }
            /*
            Estos dos eventos anteriores tienen muchos elementos en común. Por eso
            pense en abstraerlos en un método, pero las opciones en las que pensaba
            suponían hacer que los DAOs implementaran una interfaz. Creo que eso
            reduciría la comprensión del código, así que me decanté por hacer estos 
            dos enventos, que tienen elementos diferentes, aunque la logica subyacente
            sea la misma.
            */
        });
        //Evento que muestra todos los cursos en la base de datos
        consultaCurso.jBotonTodosCursos.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se limpia la tabla
                consultaCurso.modelo.removeAllElements();
                //Se añaden todos los cursos recogidos
                consultaCurso.modelo.addAll(CursoDAO.consultaTotalCursos());
                //Si no hay cursos, se manda un aviso
                if(consultaCurso.modelo.isEmpty()){
                    JOptionPane.showMessageDialog(null, "No hay cursos", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
            
        });
        //Evento que borra los cursos seleccionados
        consultaCurso.jBotonBorrarCursos.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Entero para controlar las eliminaciones exitosas
                int exito = 0;
                //Lista para recuperar los cursos seleccionados
                List<Curso> cursos = consultaCurso.jListaCursos.getSelectedValuesList();
                //Si se han escogido cursos, se borran
                if (!cursos.isEmpty()) {
                    //Cada vez que se borre un Curso exitósamente se quita de la lista
                    //y se suma un éxito
                    for (Curso c : cursos) {
                        if(CursoDAO.eliminarCurso(c.getId()) == 1){
                            exito++; 
                            consultaCurso.modelo.removeElement(c);
                        }
                    }
                    //Si se borran todos los cursos, se manda un mensaje
                    if (exito == cursos.size()) {
                        JOptionPane.showMessageDialog(null, "Cursos borrados correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    //De lo contrario, también se avisa. Se verá aquellos que no se han borrado
                    }else {
                        JOptionPane.showMessageDialog(null, "Error en el borrado", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                //Si no se han seleccionado cursos, se manda un aviso
                }else{
                    JOptionPane.showMessageDialog(null, "No hay cursos seleccionados", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
            
        });
        //Evento para lanzar el poppup de borrado o edición de un curso
        consultaCurso.jListaCursos.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                //Si se hace doble clikc, se lanza el evento
                if(e.getClickCount() == 2){
                    //Se recogen los cursos seleccionados
                    List <Curso> cursos = consultaCurso.jListaCursos.getSelectedValuesList();
                    /*
                    Si es más de un Curso se manda un error. Creo que es físicamente imposible
                    hacerlo, pero lo puse por si se diera el caso
                    */
                    if(cursos.size() > 1){
                        JOptionPane.showMessageDialog(null, "Solo se puede editar un curso a la vez", "Error", JOptionPane.ERROR_MESSAGE);
                    }else{
                        //Si solo es un Curso, se lanza el popup
                        int eleccion = Utilidades.editarOBorrar("curso");
                        //Si se decide editarlo, se recoge el Curso completo
                        if (eleccion == 0){
                            Curso c = CursoDAO.consultaTotalCurso(cursos.getFirst().getId());
                            //Y se carga la ventana de edición, pasando a esta
                            consultaCurso.setVisible(false);
                            editarCurso.setVisible(true);
                            consultaCurso.modelo.removeAllElements();
                            cargarEdicion(c);
                        //Si se decide borrarlo, se borra
                        }else if(eleccion == 1){
                            //Si se tiene éxito, se avisa, así como si se produce un error
                            if(CursoDAO.eliminarCurso(cursos.getFirst().getId()) != 0){
                                JOptionPane.showMessageDialog(null, "Curso borrado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                                //Si se borra correctamente, también se elimina de la lista
                                consultaCurso.modelo.removeElement(cursos.getFirst());
                            }else{
                                JOptionPane.showMessageDialog(null, "Error en el borrado", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }  
        });
        
        
        
        //Evento que abre la ventana de consulta cuando se cierra la de edición
        editarCurso.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                Utilidades.cambioLimpioVentana(editarCurso, consultaCurso);
            }
        });
        //Evento que guarda los cambios hechos a los datos del Curso
        editarCurso.jBotonGuardar.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se recoge el nombre del curso
                String nombre = editarCurso.jTextoNombre.getText();
                //Se recoge la asignatura de la lista
                /*
                Hice una lista porque así recuperaba el objeto entero y no tenía
                que buscar la asignatura más tarde a partir de su nombre, que 
                podría darme problemas.
                */
                Asignatura a = editarCurso.modeloLista.getElementAt(0);
                //Si no se deja el nombre del Curso en blanco, se crea este
                if(!nombre.isBlank()){
                    Curso curso = recogerCurso(nombre, a,
                            editarCurso.jComboProfesores);
                    //Se le establece su id con la etiqueta oculta
                    curso.setId(Integer.parseInt(editarCurso.jEtiquetaId.getText()));
                    //Si se actualiza correctamente, se avisa y se cierra la ventana
                    if(CursoDAO.actualizarCurso(new Curso(curso)) != 0){
                        JOptionPane.showMessageDialog(null, "Curso editado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        Utilidades.cerrarVentana(editarCurso, consultaCurso);
                    //Si hay un error, también se avisa
                    }else{
                        JOptionPane.showMessageDialog(null, "Error en la edición", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                //Si no se escribe un nombre, se lanza un mensaje al usuario
                }else{
                    JOptionPane.showMessageDialog(null, "Introduce un nombre", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //Evento para establecer las notas de un curso
        editarCurso.jBotonNotas.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                /*
                Se establece que cuando se pulsa el botón, se deja de editar la tabla
                así, se evitan errores por los que la nota, al seguir en modo de
                edición, no puede convertirse a Double de forma correcta
                */
                JTable tabla = editarCurso.jTablaNotas;
                if(tabla.isEditing()){
                    tabla.getCellEditor().stopCellEditing();
                }
                //Se establece un entero para preparar el mensaje de éxito
                List<Integer> ids = new ArrayList();
                //Se comprueban todas las filas
                for (int i = 0; i < editarCurso.modeloNotas.getRowCount(); i++) {
                    //Se recoge el id de cada una
                    int id  = (Integer) tabla.getValueAt(i, 0);
                    /*
                    Si se puede convertir la nota escrita a un double entre 1 y 10
                    se cambia la nota de la matrícula
                    */
                    try{
                        double nota = Double.parseDouble(tabla.getValueAt(i, 5).toString());
                        if(nota < 0 || nota > 10) throw new NumberFormatException();
                        //Se lanza un mensaje en función del valor de éxito de la actualización
                        //Es un mensaje p
                        if(MatriculaDAO.establecerNota(id, nota) == 0){
                            //Si se produce algún error en la inserción, se recoge el id en la lista
                            ids.add(id);
                        }
                    //Si se introduce una nota inválida, se lanza un aviso y 
                    //se restablece el valor anterior
                    }catch(NumberFormatException ex){
                        ids.add(id);
                        editarCurso.modeloNotas.setValueAt("En curso", i, 5);
                    }
                }
                //Si no hay errores, se lanza un mensaje de éxito
                if(ids.isEmpty()){
                    JOptionPane.showMessageDialog(null, "Notas establecidas", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                //Si los hay, se crea una cadena con los ids con error y se lanza un aviso
                }else{
                    StringBuilder constructor = new StringBuilder();
                    for(Integer i : ids){
                        constructor.append(i);
                        constructor.append(" | ");
                    }
                    JOptionPane.showMessageDialog(null, "Error en la inserción de las matrículas " + constructor.toString(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    
    
    /**
     * Procedimiento para cargar el comboBox mandado con los profesores de
     * cierta especialidad o de todos los profesores
     * @param comboProf     JcomboBox a cargar
     * @param especialidad  Cadena con la especialidad a buscar. Si es null se mandan
     * todos los profesores
     */
    private void cargarComboProfesor(JComboBox comboProf, String especialidad){
        //Se borran los elementos del comboBox;
        comboProf.removeAllItems();
        //Se crea un Profesor genérico
        Profesor p = new Profesor();
        p.setNombre("-Sin profesor-");
        p.setDni("-1");
        //Se inserta ese profesor
        comboProf.addItem(p);
        //Se crea una lista para llenar el comboBox
        List<Profesor> profesores;
        //Si el String mandado es null, se recogen todos los profesores
        if(especialidad == null){
            profesores = ProfesorDAO.consultaVagaProfesores();
        //Si no, se recogen los de la especialidad señalada
        }else{
            profesores = ProfesorDAO.consultaVagaProfesorEspecialidad(especialidad);
        }
        //Se añaden los profesores al comboBox
        for (Profesor prof : profesores) {
            comboProf.addItem(prof);
        }
        //Se establece el comboBox en la posición sin Profesor
        comboProf.setSelectedIndex(-1);
    }
    /**
     * Procedimiento que carga la ventana de edición de cursos
     * @param c Curso con el que cargar la página
     */
    private void cargarEdicion(Curso c){
        //Se recogen los elementos de la ventana para que sea más legible
        JLabel id = editarCurso.jEtiquetaId;
        JTextField especialidad = editarCurso.jTextoEspecialidad;
        DefaultListModel asignatura = (DefaultListModel) editarCurso.jListaAsignatura.getModel();
        //La etiqueta del id se hace invisible y se le pone el id del Curso
        id.setVisible(false);
        id.setText(Integer.toString(c.getId()));
        //Se pone el nombre del curso en su lugar
        editarCurso.jTextoNombre.setText(c.getNombre());
        
        //Se introduce la Asignatura en la lista como único elemento
        asignatura.addElement(c.getAsignatura());
        //Se intoduce la especialidad
        especialidad.setText(c.getAsignatura().getAmbito());
        //Se recogen los datos a mostrar de las matrículas activas del curso
        List<Object[]> filas = Utilidades.recuperarDatosMatricula(MatriculaDAO.consultaTotalMatriculasActivasCurso(c.getId(), true));
        //Si no se carga ninguna matrícula, se lanza un aviso
        if(Utilidades.cargarEnTabla(filas, editarCurso.jTablaNotas) == 0){
            JOptionPane.showMessageDialog(null, "No hay matrículas pendientes para este curso", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
        //Se introduce en el comboBox de Profesor los profesores del ámbito del curso
        edicionProfesoresAmbito(editarCurso.jComboProfesores, especialidad.getText());
        //Si el curso cuenta con un profesor, se establece como el seleccionado
        if (c.getProfesor() != null) {
            editarCurso.jComboProfesores.setSelectedItem(c.getProfesor());
        }
    }
    /**
     * Función que crea un Curso a partir de los datos obtenidos
     * @param nombre        Cadena con el nombre del curso
     * @param asignatura    Asignatura del curso
     * @param profesor      JComboBox con los posibles profesores
     * @return              Curso completo a falta de su id
     */
    private Curso recogerCurso(String nombre, Asignatura asignatura, JComboBox profesor){
        Profesor p = (Profesor) profesor.getSelectedItem();
        Profesor prof;
        //Si el profesor recogido del comboBox no es genérico, se crea su copia
        if (!p.getDni().equals("-1")) {
            prof = new Profesor(p);
        }else{
            prof = null;
        }
        //Finalmente, se manda un nuevo Curso
        return new Curso(nombre, asignatura, prof);
    }
    /**
     * Procedimiento que carga un JComboBox con profesores de una especialidad
     * @param profesores    JComboBox a rellenar
     * @param especialidad  Cadena con la especialidad a tener en cuenta para
     * llenar el comboBox
     */
    private void edicionProfesoresAmbito(JComboBox profesores, String especialidad){
        //Se borran los elementos del comboBox
        profesores.removeAllItems();
        //Se crea el Profesor genérico y se introduce
        Profesor sinProf = new Profesor();
        sinProf.setDni("-1");
        sinProf.setNombre("-Sin profesor-");
        profesores.addItem(sinProf);
        //Se introducen los profesores de la especialidad establecida
        for(Profesor p :ProfesorDAO.consultaVagaProfesorEspecialidad(especialidad)){
            profesores.addItem(p);
        }
    }  
    /**
     * Método que devuelve el JFrame incial de este controlador
     * @return JFrame inicial de ControladorCursos
     */
    public VentanaCursos getInicioCurso() {
        return inicioCurso;
    }
    
}
