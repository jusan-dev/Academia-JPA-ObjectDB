/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controladores;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import vistas.VentanaInicial;

/**
 *Controlador que lleva al resto de controladores
 * @author Juan Sánchez
 */
public class ControladorInicial {
    private VentanaInicial ventana;
    
    public ControladorInicial(){
        this.ventana = new VentanaInicial();
        this.ventana.setVisible(true);
        //Eventos
        
        //Evento para pasar al controlador de Alumnos
        ventana.jBotonAlumnos.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                //Lo hago de esta manera para no crear muchos objetos
                //cada vez que se vuelve al controlador inicial
                ControladorAlumnos c = new ControladorAlumnos(ControladorInicial.this);
                c.getConsultaAlumnos().setVisible(true);
                ventana.setVisible(false);
            }
        });
        //Evento para pasar al controlador de Matrículas
        ventana.jBotonMatriculas.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                ControladorMatriculas c = new ControladorMatriculas(ControladorInicial.this);
                c.getInicioMatricula().setVisible(true);
                ventana.setVisible(false);                
            }
        });
        //Evento para pasar al controlador de Profesores
        ventana.jBotonProfesores.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                ControladorProfesores c = new ControladorProfesores(ControladorInicial.this);
                c.getInicioProfesor().setVisible(true);
                ventana.setVisible(false);
            }
        });
        //Evento para pasar al controlador de Cursos
        ventana.jBotonCursos.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                ControladorCursos c = new ControladorCursos(ControladorInicial.this);
                c.getInicioCurso().setVisible(true);
                ventana.setVisible(false);
            }
        });
    }
    /**
     * Método que devuelve el JFrame de este controlador
     * @return JFrame inicial de la aplicación
     */
    public VentanaInicial getVentana() {
        return ventana;
    }
    
}
