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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import javax.swing.event.PopupMenuEvent;
import modelos.Alumno;
import modelos.Curso;
import modelos.Matricula;
import modelos.Persona;
import utilidades.MiPopupMenuAdapter;
import utilidades.Utilidades;
import vistas.VentanaConsultaAlumnos;
import vistas.VentanaEditarAlumno;

/**
 *Clase encargada del control de la lógica relacionada con los alumnos
 * @author Juan Sánchez Santiago
 */
public class ControladorAlumnos {
    /**
     * JFrame para buscar alumnos
     */
    private VentanaConsultaAlumnos consultaAlumnos;
    
    /**
     * JFrame para editar Alumno
     */
    private VentanaEditarAlumno edicionAlumno;
    /**
     * Controlador para volver a la pantalla de inicio
     */
    private ControladorInicial inicio;
    
    /**
     * Constructor del controlador que cuenta con todos los eventos de estas
     * ventanas
     * @param controladorInicio Controlador encargador de cerrar la apliación
     * y pasar a otros controladores
     */
    public ControladorAlumnos(ControladorInicial controladorInicio){
        this.consultaAlumnos = new VentanaConsultaAlumnos();
        this.edicionAlumno = new VentanaEditarAlumno();
        /*
        Entiendo que no es lo mejor que haya tanta comunicación entre los 
        controladores, pero esta me parećia la manera más limpia y menos instrusiva
        de volver al punto inicia de la aplicación.
        */
        this.inicio = controladorInicio;
        
        
        //La ventana de consulta se hace visible
        this.consultaAlumnos.setVisible(true);
        
        //Se establece que todas las ventanas al cerrarse liberar memoria y no
        //cierran la aplicación
        this.consultaAlumnos.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.edicionAlumno.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        //Eventos
        
        
        consultaAlumnos.addWindowListener(new WindowAdapter(){
            /*
            Se carga el comboBox de la ventana de consulta aquí porque en mi portátil
            suele cargarse mal cuando lo cargo en un windowOpening
            */
            @Override
            public void windowOpened(WindowEvent e) {
                cargarComboBoxConsulta();
            }
            //Evento que abre la ventana de inicio cuando se cierra la de consulta
            @Override
            public void windowClosing(WindowEvent e) {
                Utilidades.cambioLimpioVentana(consultaAlumnos, inicio.getVentana());
                
            }
        });
        //Evento que busca el alumno o alumnos resultantes de escribir su(s) nombre(s)
        consultaAlumnos.jBotonBuscarAlumno.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se recoge el texto del JTextField
                String nombre = consultaAlumnos.jTextoNombre.getText();
                //Se prepara el array de Object que se va a usar para llenar la tabla
                List<Object[]> filas;
                //Si hay un nombre en el JTextField se busca entre los alumnos
                if(!nombre.isBlank()){
                    filas = recuperarDatos(AlumnoDAO.consultaVagaAlumnoPorNombre(nombre));
                    
                //Si no, se recogen todos los alumnos
                }else{
                    filas = recuperarDatos(AlumnoDAO.consultaVagaAlumnos());
                }
                /*
                Si en alguno de estos casos no se consigue ningún alumno, se informa
                al usuario
                */
                if(Utilidades.cargarEnTabla(filas, consultaAlumnos.jTablaAlumnos) == 0){
                    JOptionPane.showMessageDialog(null, "No se han encontrado alumnos", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        //Evento para editar o borrar un Alumno con un doble click
        consultaAlumnos.jTablaAlumnos.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {
                //Si se ha hecho dos veces click se dispara el evento
                if(e.getClickCount() == 2){
                    //Se recoge el dni
                    String dni = (String)Utilidades.getIdFilaSeleccionada(consultaAlumnos.jTablaAlumnos);
                    //Se llama al método de ayuda para que el usuario decida que hacer en un popup
                    int eleccion = Utilidades.editarOBorrar("alumno/a");
                    //Si decide editar al Alumno, se carga la ventana de edición
                    if(eleccion == 0){
                        //En estos casos no utilizo cambioLimpioVentana 
                        // porque no me interesa limpiar la ventana de consulta
                        Utilidades.cambiarVentana(consultaAlumnos, edicionAlumno, consultaAlumnos.modelo);
                        //Para cargarla, se recuperan todos los datos del Alumno
                        Alumno a = AlumnoDAO.consultaTotalAlumno(dni);
                        cargarEdicion(a);
                    //Si decide borrar el Alumno, se manda un mensaje según el éxito de la función
                    }else if(eleccion == 1){
                        if(AlumnoDAO.eliminarAlumno(dni) == 1){
                            JOptionPane.showMessageDialog(null, "Alumno borrado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                            /*
                            Cuando se borra un alumno, se limpia la ventana para no mostrar 
                            datos inexistentes
                            */
                            Utilidades.limpiarVentana(consultaAlumnos);
                        }else {
                            JOptionPane.showMessageDialog(null, "Ha ocurrido un error en el borrado", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    
                }
            }  
        });
        //Evento que busca alumnos por curso dependiendo de cuál se seleccione en el comboBox
        consultaAlumnos.jComboCursos.addPopupMenuListener(new MiPopupMenuAdapter(){
            @Override
            //El evento solo se dispara cuando el menú del comboBox aparece y desaparece
            //Así, evitaba errores durante la carga de los comboBox
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                //Si el usuario ha elegido realmente un curso se realiza el evento
                if(consultaAlumnos.jComboCursos.getSelectedIndex() != -1){
                    //Se recoge el Curso del comboBox
                    Curso c = (Curso) consultaAlumnos.jComboCursos.getSelectedItem();
                    //Se prepara la lista con las matriculas de ese curso
                    List<Matricula> matriculas;
                    //Si es el curso general se recogen todas las matrículas
                    if(c.getId() == -1){
                        matriculas = MatriculaDAO.consultaTotalMatriculas();
                    //Si no, se recogen todas las matrículas de un curso
                    }else{
                        matriculas = MatriculaDAO.consultaTotalMatriculasCurso(c.getId());
                    }
                    //Se prepara la lista de alumnos con esas matrículas
                    List<Alumno> alumnos = new ArrayList();
                    for (Matricula m : matriculas) {
                        /*
                        Si en la lista de matrículas hay alumnos que todavía no
                        se han insertado en la lista de alumnos, se insertan en esta.
                        Así se evitan alumnos duplicados.
                        */
                        if (!alumnos.contains(m.getAlumno())) {
                            alumnos.add(m.getAlumno());
                        }
                    }
                    /*
                    Una vez que se tienen los alumnos, se preparan sus datos y 
                    se insertan en la tabla
                    */
                    Utilidades.cargarEnTabla(recuperarDatos(alumnos),
                            consultaAlumnos.jTablaAlumnos);
                }
                    
            }
            
        });
        
        
        
        //Evento para volver a la ventana de consulta cuando se cierre la de edición
        edicionAlumno.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {
                Utilidades.cambioLimpioVentana(edicionAlumno, consultaAlumnos);
                
            }
        });
        //Evento para guardar los cambios en los datos de un Alumno
        edicionAlumno.jBotonGuardar.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Se recogen los datos genéricos del alumnos del formulario en una Persona
                Persona p = Utilidades.recogerPersona(edicionAlumno);
                //Si los datos son válidos, la Persona se convierte en un Alumno
                if(p != null){
                    Alumno a = new Alumno(p);
                    //Si la actualización se realiza, se manda un mensaje de éxito
                    if (AlumnoDAO.actualizarAlumno(a) != 0) {
                        JOptionPane.showMessageDialog(null, "Alumno editado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    //Si no, se manda uno de error
                    } else {
                        JOptionPane.showMessageDialog(null, "No se han realizado cambios", "Error", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        });
        //Evento para eliminar matrículas del Alumno
        edicionAlumno.jBotonMatriculas.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Entero para seguir las eliminaciones exitosas
                int aciertos = 0;
                //Se recogen las matrículas de la lista de la ventana
                List<Matricula> matriculas = edicionAlumno.jListaMatriculas.getSelectedValuesList();
                //Se eliminan todas las matrículas
                for(Matricula m : matriculas){
                    if(MatriculaDAO.eliminarMatricula(m.getId(), edicionAlumno.getDni()) == 1){
                        //Si la Matricula se elimina correctamente, se quita de la lista
                        //y se suma un acierto
                        edicionAlumno.modelo.removeElement(m);
                        aciertos++;
                    }
                }
                /*
                Una vez eliminadas todas las matrículas, se recalcula la media del
                Alumno
                */
                AlumnoDAO.establecerMediaAlumno(edicionAlumno.getDni());
                /*
                Si todas las matrículas seleccionadas no se han borrado, se 
                manda un error. No se señala cuales, puesto que aquellas que se
                hayan borrado realmente no aparecerán en la lista
                */
                if(aciertos != matriculas.size()){
                    JOptionPane.showMessageDialog(null, "Error en alguno de los borrados", "Error", JOptionPane.ERROR_MESSAGE);
                //Si no hay error se avisa del éxito
                }else if(aciertos > 0){
                    JOptionPane.showMessageDialog(null, "Matrículas borradas correctamente", "Éxtio", JOptionPane.INFORMATION_MESSAGE);
                }  
                    
            }
            
        });
    }
    /**
     * Función que recibe una lista de alumnos y establece los datos
     * a presentar en la tabla
     * @param almns List con los alumnos a mostrar en la tabla
     * @return      Array con los objetos que tienen los datos insertar en la tabla
     */
    private List<Object[]> recuperarDatos(List<Alumno> almns){
        //Se prepara la lista de filas
        List<Object[]> filas = new ArrayList();
        /*
        Para cada alumno en la lista pasada por parámetro se extraen los datos
        pertinentes para mostrarlos en la tabla. Si no tiene una nota media, se 
        establece que no hay datos académicos.
        */
        for(Alumno a : almns){
            double notaDouble = a.getMediaAcademica();
            String nota = notaDouble == -1
                    ? "Sin datos" : String.format("%.2f", notaDouble);
            Object[] objeto = {a.getDni(), a.getNombre(), a.getEmail(), nota,
                a.getFecha_nacimiento()};
            filas.add(objeto);
        }
        return filas;
    }
    /**
     * Procedimiento que carga los elementos de la ventana de edición de Alumno
     * @param a Alumno con los datos a cargar en la ventana
     */
    private void cargarEdicion(Alumno a){
        //De nuevo, si no hay nota se establece que no hay datos académicos
        double notaDouble = a.getMediaAcademica();
        String nota = a.getMediaAcademica() == -1? "Sin datos" : String.format("%.2f", notaDouble);
        
        //Se establece en DNI, nombre, email, sexo, nacimiennto y notas del Alumno
        edicionAlumno.jTextoDNI.setText(a.getDni());
        edicionAlumno.jTextoNombre.setText(a.getNombre());
        edicionAlumno.jTextoEmail.setText(a.getEmail());
        Utilidades.cargarComboSexo(a.getSexo(), edicionAlumno.jComboSexo);
        edicionAlumno.jTextoNacimiento.setText(a.getFecha_nacimiento().toString());
        edicionAlumno.jTextoNotaMedia.setText(nota);
        //En la lista de edición se insertan todas las matrículas del Alumno
        edicionAlumno.modelo.removeAllElements();;
        edicionAlumno.modelo.addAll(a.getMatriculas());
    }
    /**
     * Procedimiento que carga el comboBox de la consulta de Alumnos con los
     * cursos presentes en la base de datos. Además, el comboBox no señala ningún 
     * curso específico
     */
    private void cargarComboBoxConsulta(){
        Curso c = new Curso();
        c.setId(-1);
        c.setNombre("-Todos los cursos-");
        consultaAlumnos.jComboCursos.addItem(c);
        for (Curso curso : CursoDAO.consultaVagaCursos()) {
            consultaAlumnos.jComboCursos.addItem(curso);
        }
        consultaAlumnos.jComboCursos.setSelectedIndex(-1);
    }
    /*
    Algunos de estos métodos no se usan más de una vez pero creo que de esta 
    manera todo queda claro. Otros métodos como recogerDatos(), son comunes
    a varias de las ventanas del proyecto, pero no se unifican en un método porque
    cada una extrae unos datos diferentes de sus respectivos objetos.
    */
    /*
    En todos los controladores hay bastantes condicionales. Pero creo que es
    necesario para validar los datos que se introducen y mandar feedback específico
    al usuario
    */

    /**
     * Método que devuelve el JFrame incial de este controlador
     * @return JFrame inicial de ControladorAlumnos
     */
    public VentanaConsultaAlumnos getConsultaAlumnos() {
        return consultaAlumnos;
    }
    
    
}
