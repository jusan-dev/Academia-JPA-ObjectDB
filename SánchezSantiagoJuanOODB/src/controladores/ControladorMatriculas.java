/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controladores;

import daos.AlumnoDAO;
import daos.CursoDAO;
import daos.MatriculaDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.PopupMenuEvent;
import modelos.Alumno;
import modelos.Asignatura;
import modelos.Curso;
import modelos.Matricula;
import modelos.Persona;
import utilidades.MiPopupMenuAdapter;
import utilidades.Utilidades;
import vistas.VentanaAddMatricula;
import vistas.VentanaConsultaMatriculas;
import vistas.VentanaMatriculas;

/**
 *Controlador con todos los eventos y ventanas relacionados con la creación y
 * consulta de matrículas
 * @author Juan Sánchez
 */
public class ControladorMatriculas {
    /**
     * JFrame con la ventana de inicio de este apartado
     */
    private VentanaMatriculas inicioMatricula;
    /**
     * JFrame con la ventana de inserción de Matrícula
     */
    private VentanaAddMatricula addMatricula;
    /**
     * JFrame con la ventana de consulta de Matriculas
     */
    private VentanaConsultaMatriculas consultaMatricula;
    /**
     * Controlador para volver a la ventana inicial de la aplicación
     */
    private ControladorInicial inicio;
    
    /**
     * Constrcutor del controlador que inicializa todos sus atributos, cambia
     * sus propiedades y contiene todos los eventos de las matrículas
     * @param controladorInicial  Controlador con el que volver a la pantalla
     * principal
     */
    public ControladorMatriculas(ControladorInicial controladorInicial){
        this.inicioMatricula = new VentanaMatriculas();
        this.addMatricula = new VentanaAddMatricula();
        this.consultaMatricula = new VentanaConsultaMatriculas();
        this.inicio = controladorInicial;
        
        //Se establece que las ventanas liberen memoria cuando se cierren
        this.inicioMatricula.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.addMatricula.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.consultaMatricula.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        //La ventana de inicio de Matrícula se hace visible
        this.inicioMatricula.setVisible(true);
        
        
        //Eventos
        
        //Evento que cambia de la ventana de inicio de este controlador a la ventana
        //principal de la aplicación
        inicioMatricula.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                Utilidades.cambioLimpioVentana(inicioMatricula, inicio.getVentana());  
            }
        });
        //Evento que cambia de la ventana de inicio a la ventana de consulta de Matrícula
        inicioMatricula.jBotonMatricula.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se restablecen los cursos existentes en el comboBox
                //cada vez que se entra en la página
                consultaMatricula.jComboCursos.removeAllItems();
                for(Curso c : CursoDAO.consultaTotalCursos()){
                    consultaMatricula.jComboCursos.addItem(c);
                }
                //Se deselecciona todo Curso en el comboBox
                consultaMatricula.jComboCursos.setSelectedIndex(-1);
                Utilidades.cambioLimpioVentana(inicioMatricula, consultaMatricula);
            }
        });
        //Evento que cambia de la ventana de inicio a la de inserción de Matŕicula
        inicioMatricula.jBotonAddMatricula.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se preselecciona el check de nuevo alumno
                addMatricula.jCheckNuevoAlumno.setSelected(true);
                //Se cargan los comboBox de Asignatura y sexo
                Utilidades.cargarComboAsignatura(addMatricula.jComboAsignatura);
                Utilidades.cargarComboSexo(null, addMatricula.jComboSexo);
                Utilidades.cambioLimpioVentana(inicioMatricula, addMatricula);
            }
        });
        
        
        
        //Evento para volver a la ventana de inicio de Matrícula
        addMatricula.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                Utilidades.cambioLimpioVentana(addMatricula, inicioMatricula);
            }
        });
        /*
        Evento para cargar los cursos existentes en la ventana de inserción de 
        Matrícula en función de la asignatura escogida
        */
        addMatricula.jComboAsignatura.addPopupMenuListener(new MiPopupMenuAdapter(){
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                //Se asegura de que no se ha escogido la opción vacía
                if (addMatricula.jComboAsignatura.getSelectedIndex() != -1) {
                    //Se recoge la asignatura
                    Asignatura a = (Asignatura) addMatricula.jComboAsignatura.getSelectedItem();
                    //Se borran los elementos del comboBox de Curso
                    addMatricula.jComboCurso.removeAllItems();
                    //Se recogen los cursos en función de la asignatura
                    List<Curso> cursos = CursoDAO.consultaVagaCursosAsignatura(a);
                    //Si hay, se llena el comboBox, si no, se manda un aviso
                    if (!cursos.isEmpty()) {
                        for (Curso c : cursos) {
                            addMatricula.jComboCurso.addItem(c);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No hay cursos para esa asignatura", "Aviso", JOptionPane.WARNING_MESSAGE);
                    }
                //Si no se escoge una Asignatura, se manda un aviso
                }else{
                    JOptionPane.showMessageDialog(null, "Selecciona una asignatura", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //Evento que inserta una Matrícula
        addMatricula.jBotonEnviar.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se establece un Alumno null para validar los cambios que se realizarán
                Alumno a = null;
                //Si el check de nuevo Alumno está seleccionado, se recogen todos
                //los datos del Alumno
                if(addMatricula.jCheckNuevoAlumno.isSelected()){
                    Persona p = Utilidades.recogerPersona(addMatricula);
                    //Si son datos válidos, se convierte en un Alumno
                    if(p != null){
                        a = new Alumno(p);
                    }
                //Si el check de nuevo alumno no está seleccionado, se recoge solo
                //el DNI
                }else{
                    String dni = addMatricula.getDni();
                    //Se comprueba si es válido
                    if(Persona.validoDNI(dni)){
                        //Se busca en la base de datos
                        a = AlumnoDAO.consultaTotalAlumno(dni.toUpperCase());
                        //Si no existe tal Alumno, se lanza un mensaje
                        if(a == null){
                            JOptionPane.showMessageDialog(null, "Alumno no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    //Si el DNI es inválido, se informa al usuario
                    }else{
                        JOptionPane.showMessageDialog(null, "DNI inválido", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                /*
                Si, tras todo este proceso, se ha conseguido un Alumno que cuenta
                con todos su datos válidos, se recogen los datos académicos
                */
                if( a != null){
                    //Se recoge el curso seleccionado
                    Curso c = (Curso) addMatricula.jComboCurso.getSelectedItem();
                    //Si no se ha escogido la opción vacía, se crea una nueva Matrícula
                    if (c != null) {
                        //Esta se inserta y se avisa del valor de éxito al usuario
                        if (MatriculaDAO.insertarMatricula(new Matricula(a, c)) != 0) {
                            JOptionPane.showMessageDialog(null, "Alumno insertado correctamente", "Error", JOptionPane.INFORMATION_MESSAGE);
                        }else{
                            JOptionPane.showMessageDialog(null, "Error en la inserción", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    //Si no se elige un curso, se lanza un mensaje
                    } else {
                        JOptionPane.showMessageDialog(null, "Elige un curso", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        //Evento que controla la editabilidad de los campos del formulario
        //en función del check de nuevo Alumno
        addMatricula.jCheckNuevoAlumno.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se recogen los campos a cambiar para mayor legibilidad
                JTextField[] campos = {addMatricula.jTextoNombre, addMatricula.jTextoEmail,
                                       addMatricula.jTextoNacimiento};
                //Si el check está puesto, todos los campos son editables
                if(addMatricula.jCheckNuevoAlumno.isSelected()){
                    for(JTextField t : campos){
                        t.setEnabled(true);
                    }
                    addMatricula.jComboSexo.setEnabled(false);
                //Si no, ninguno lo es
                }else{
                    for(JTextField t : campos){
                        t.setEnabled(false);
                    }
                    addMatricula.jComboSexo.setEnabled(false);
                    //En este caso, se avisa al usuario de los cambios
                    JOptionPane.showMessageDialog(null, "Solo necesitamos el DNI para alumnos ya presentes", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            
        });
        
        
        
        //Evento que cambia de la ventana de consulta a la de inicio de Matrícula
        consultaMatricula.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                Utilidades.cambioLimpioVentana(consultaMatricula, inicioMatricula);
            }
        });
        //Evento que muestra las matrículas de un Alumno en función de su DNI
        consultaMatricula.jBuscarAlumno.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se recoge el DNI del campo de texto
                String dni  = consultaMatricula.jTextoDNI.getText().toUpperCase();
                //Se recogen los valores de los check para establecer qué matrículas
                //se buscan
                boolean pasado = consultaMatricula.jCheckPasado.isSelected();
                boolean presente = consultaMatricula.jCheckPresente.isSelected();
                //Si hay DNI se realiza la búsqueda
                if(!dni.isBlank()){
                    //Se establece la variable para la lista de matrículas
                    List<Matricula> matriculas = null;
                    //Se busca el Alumno
                    Alumno a = AlumnoDAO.consultaVagaAlumno(dni);
                    //Si existe, se realiza la búsqueda
                    if(a != null){
                        /*
                        Si se seleccionan ambos checks, se buscan todas las 
                        matrículas del Alumno
                        */
                        if (pasado && presente) {
                            matriculas = MatriculaDAO.consultaTotalMatriculasAlumno(dni);
                        /*
                        Si solo se selecciona el check de pasadas, se buscan las matrículas
                        que tienen una nota, es decir, que ya han sido calificadas
                        */
                        } else if (pasado) {
                            matriculas = MatriculaDAO.consultaTotalMatriculasActivasAlumno(a.getDni(), false);
                        /*
                        Si solo se selecciona el check de presentes, se buscan las
                        matrículas que no tienen nota, que están en curso
                        */
                        } else if (presente) {
                            matriculas = MatriculaDAO.consultaTotalMatriculasActivasAlumno(a.getDni(), true);
                        }
                        //Se borran los datos de la tabla
                        consultaMatricula.modelo.setRowCount(0);
                        //Si se ha seleccionado un check, se insertan las matrículas
                        //en la tabla con los datos especificados en recuperarDatos()
                        if (matriculas != null) {
                            if (Utilidades.cargarEnTabla(Utilidades.recuperarDatosMatricula(matriculas),
                            consultaMatricula.jTablaMatriculas) == 0) {
                                //Si no hay matrículas a insertar, se lanza un mensaje
                                JOptionPane.showMessageDialog(null, 
                                "No se han encontrado matrículas de ese alumno", "Aviso", JOptionPane.WARNING_MESSAGE);
                            }
                        //Si no se selecciona ningún check, se avisa al usuario
                        } else {
                            JOptionPane.showMessageDialog(null, "Selecciona qué tipo de matrículas ver", "Aviso", JOptionPane.ERROR_MESSAGE);
                        }
                    //Si no existe, se avisa al usuario    
                    }else {
                        JOptionPane.showMessageDialog(null, "Alumno no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                        //Y se vacía el campo de texto
                        consultaMatricula.jTextoDNI.setText("");
                    }
                //Si no, se lanza un mensaje
                } else {
                    JOptionPane.showMessageDialog(null, "Introduce un DNI", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        //Evento que muestra las matrículas en función del curso seleccionado
        consultaMatricula.jComboCursos.addPopupMenuListener(new MiPopupMenuAdapter(){
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                //Se comprueba que se selecciona un curso válido y se prepara
                //la lista de matrículas
                if (consultaMatricula.jComboCursos.getSelectedIndex() != -1) {
                    Curso c = (Curso) consultaMatricula.jComboCursos.getSelectedItem();
                    List<Matricula> matriculas = null;
                    //Se recogen los check seleccionados
                    boolean pasado = consultaMatricula.jCheckPasado.isSelected();
                    boolean presente = consultaMatricula.jCheckPresente.isSelected();
                    //Se muestran las matrículas en función de los check seleccionados
                    //de la misma forma que el método anterior, pero con otro DAO
                    /*
                    Ahí está la dificultad que encuentro para hacer un método que
                    lo englobe todo
                    */
                    if (pasado && presente) {
                        matriculas = MatriculaDAO.consultaTotalMatriculasCurso(c.getId());
                    } else if (pasado) {
                        matriculas = MatriculaDAO.consultaTotalMatriculasActivasCurso(c.getId(), false);
                    } else if (presente) {
                        matriculas = MatriculaDAO.consultaTotalMatriculasActivasCurso(c.getId(), true);
                    }
                    //Se vacía la tabla
                    consultaMatricula.modelo.setRowCount(0);
                    //Se introducen los datos si se ha seleccionado, como mínimo, un check
                    if (matriculas != null) {
                        if (Utilidades.cargarEnTabla(Utilidades.recuperarDatosMatricula(matriculas),
                                consultaMatricula.jTablaMatriculas) == 0) {
                            //Si no hay nada que insertar, se lanza un mensaje
                            JOptionPane.showMessageDialog(null,
                                    "No se han encontrado matrículas de ese curso", "Aviso", JOptionPane.WARNING_MESSAGE);
                            //Y se establece el comboBox en su posición por defecto
                            consultaMatricula.jComboCursos.setSelectedIndex(-1);
                        }
                    //Se avisa de la necesidad de seleccionar un check para mostrar las matrículas
                    } else {
                        JOptionPane.showMessageDialog(null, "Selecciona qué tipo de matrículas a ver", "Aviso", JOptionPane.WARNING_MESSAGE);
                        consultaMatricula.jComboCursos.setSelectedIndex(-1);
                    }
                } 
            }
        });
    }
    /**
     * Método que devuelve el JFrame incial de este controlador
     * @return JFrame inicial de ControladorMatrícula
     */
    public VentanaMatriculas getInicioMatricula() {
        return inicioMatricula;
    }
    
}
